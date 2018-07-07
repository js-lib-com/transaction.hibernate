package js.transaction.hibernate;

import java.util.Properties;

import js.lang.BugError;
import js.lang.Config;
import js.lang.ConfigException;
import js.log.Log;
import js.log.LogFactory;
import js.transaction.TransactionException;
import js.util.Classes;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * Hibernate database adapter creates session factory and use it to create transactions. Keeps a transactions cache
 * stored on thread local variables and use it to retrieve currently executed Hibernate session, see
 * {@link #getSession()}.
 * <p>
 * Session factory is configured from provided configuration object, see {@link Config} class for format description.
 * 
 * @author Iulian Rotaru
 * @version draft
 */
final class HibernateAdapter
{
  /** Class logger. */
  private static final Log log = LogFactory.getLog(HibernateAdapter.class);

  // it seems MySQL - that happens to be use into production, has wait timeout 8 hours
  // is reasonable to believe any database may have similar values
  // but to be on safe side I choose test period to be 30 minutes

  /** Database connection keep alive period, set to half hour. */
  private static final String TEST_PERIOD = "1800";
  /** Query for database connection keep alive transaction. */
  private static final String TEST_QUERY = "SELECT 1;";

  /** Keep current on working transaction on thread local variable. */
  private final ThreadLocal<TransactionImpl> transactionsCache = new ThreadLocal<>();

  /**
   * Hibernate session factory. This factory creates Hibernate session which is the native interface of
   * {@link Transaction} implementation.
   */
  private SessionFactory sessionFactory;

  /**
   * Hibernate transaction timeout. This value is global per database adapter, i.e. on application. Value expressed in
   * seconds. Default value is zero which means blocking connection. It is loaded from
   * <code>hibernate.transaction.timeout</code> property, if exists.
   */
  private int transactionTimeout;

  /**
   * Hibernate adapter initialization. Read Hibernate properties from configuration object and create the session
   * factory; also configure connections pool. All properties from configuration object are passed as they are to
   * Hibernate session factory builder.
   * <p>
   * Scan and register mappings files in the configured package(s). For Hibernate mappings there are
   * <code>mappings</code> child elements into configuration object. There can be a not limited number of
   * <code>mappings</code> elements but usually it is a single one. A mapping element has <code>package</code> and
   * <code>files-pattern</code> attributes. Files pattern is optional with default <code>*.hbm.xml</code>.
   * 
   * @param config configuration object.
   * @throws ConfigException if configuration object is not well formed.
   * @throws HibernateException if a property is not recognized by Hibernate session factory builder.
   */
  public void config(Config config) throws ConfigException, HibernateException
  {
    log.trace("config(Config)");

    boolean testSession = config.getAttribute("test-session", boolean.class, false);

    transactionTimeout = config.getProperty("hibernate.transaction.timeout", int.class, 0);
    int min_size = config.getProperty("hibernate.c3p0.min_size", int.class, 0);
    int max_size = config.getProperty("hibernate.c3p0.max_size", int.class, 0);
    int max_statements = config.getProperty("hibernate.c3p0.max_statements", int.class, 0);

    // takes care max_size greater than min_size and max_statements greater than max_size
    // otherwise strange behavior occurs on multi-threading, see DatabaseTest#executeConcurentNativeHibernate()

    if(max_size > 0 && max_size < 4) {
      max_size = 4;
    }
    if(min_size > 0 && min_size > max_size) {
      min_size = max_size;
    }
    if(max_statements > 0 && max_statements < max_size) {
      max_statements = max_size;
    }

    Properties properties = config.getUnusedProperties();
    if(!testSession) {
      // do not use C3P0 database connections pool when run tests
      // if tests are executed from Ant script, for speed reason all ran into a single virtual machine
      // every test requiring database connection will acquire connection but do no dispose till virtual machine end
      // leading to connections pool exhaust and test suite halt
      properties.setProperty("hibernate.connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider");
      properties.setProperty("hibernate.c3p0.idle_test_period", TEST_PERIOD);
      properties.setProperty("hibernate.c3p0.preferredTestQuery", TEST_QUERY);
    }

    // Hibernate configuration class is the session factory builder
    Configuration configuration = new Configuration();
    configuration.setProperties(properties);

    properties.setProperty("hibernate.transaction.timeout", Integer.toString(transactionTimeout));
    if(max_size != 0) {
      configuration.setProperty("hibernate.c3p0.max_size", Integer.toString(max_size));
      properties.setProperty("hibernate.c3p0.max_size", Integer.toString(max_size));
    }
    if(min_size != 0) {
      configuration.setProperty("hibernate.c3p0.min_size", Integer.toString(min_size));
      properties.setProperty("hibernate.c3p0.min_size", Integer.toString(min_size));
    }
    if(max_statements != 0) {
      configuration.setProperty("hibernate.c3p0.max_statements", Integer.toString(max_statements));
      properties.setProperty("hibernate.c3p0.max_statements", Integer.toString(max_statements));
    }

    // traverse all packages from <mappings> elements and add mapping files to configuration
    for(Config mappings : config.findChildren("mappings")) {
      String filesPattern = mappings.getAttribute("files-pattern", "*.hbm.xml");
      String packageName = mappings.getAttribute("package");
      if(packageName == null) {
        continue;
      }
      for(String resource : Classes.listPackageResources(packageName, filesPattern)) {
        configuration.addResource(resource);
      }
    }

    String driverClassName = config.getProperty("hibernate.connection.driver_class");
    log.debug("Load database driver |%s|.", driverClassName);
    Classes.forName(driverClassName);

    log.debug("Create Hibernate session factory.");
    sessionFactory = configuration.buildSessionFactory();
  }

  /**
   * Get session factory.
   * 
   * @return session factory.
   */
  public SessionFactory getSessionFactory()
  {
    if(sessionFactory == null) {
      throw new BugError("Null Hibernate session factory. Most likely Hibernate adapter is not configured, see #config(Config).");
    }
    return sessionFactory;
  }

  /**
   * Create a database handler. Create a new {@link TransactionImpl} and store it on current thread so that it can be
   * retrieved by application code. This method is invoked by {@link TransactionManagerImpl} when start a new
   * transaction. Note that created handler is valid only on current transaction boundaries.
   * 
   * @param readOnly if this flag is true create a read-only transaction, supporting only database select operations.
   * @return newly created database handler.
   */
  public TransactionImpl createTransaction(boolean readOnly)
  {
    TransactionImpl transaction = transactionsCache.get();
    if(transaction != null) {
      transaction.incrementTransactionNestingLevel();
    }
    else {
      transaction = new TransactionImpl(this, transactionTimeout, readOnly);
      transactionsCache.set(transaction);
    }
    return transaction;
  }

  /** Remote transaction from transactions cache. */
  public void destroyTransaction()
  {
    transactionsCache.set(null);
  }

  @SuppressWarnings("unchecked")
  public <T> T getSession()
  {
    TransactionImpl handler = transactionsCache.get();
    if(handler == null) {
      throw new TransactionException("Missing transaction handler. Probably attempt to use Hibernate session outside a transaction boundaries.");
    }
    return (T)handler.getSession();
  }

  /** Destroy Hibernate session factory and releases database resources. */
  public void destroy()
  {
    log.debug("Close Hibernate session factory and releases caches and database connections.");
    // access session factory through getter to have validity check
    getSessionFactory().close();
  }
}
