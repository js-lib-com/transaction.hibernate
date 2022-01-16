package js.transaction.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import js.lang.BugError;
import js.lang.Config;
import js.log.Log;
import js.log.LogFactory;
import js.transaction.TransactionException;

/**
 * Hibernate database adapter creates session factory and use it to create transactions. Keeps a transactions cache
 * stored on thread local variables and use it to retrieve currently executed Hibernate session, see
 * {@link #getSession()}.
 * <p>
 * Session factory is configured from provided configuration object, see {@link Config} class for format description.
 * 
 * @author Iulian Rotaru
 */
final class HibernateAdapter
{
  private static final Log log = LogFactory.getLog(HibernateAdapter.class);

  public static final String CONFIG_FILE = "hibernate.cfg.xml";

  /** Keep current on working transaction on thread local variable. */
  private final ThreadLocal<TransactionImpl> transactionsCache = new ThreadLocal<>();

  /**
   * Hibernate session factory. This factory creates Hibernate session which is the native interface of
   * {@link Transaction} implementation.
   */
  private final SessionFactory sessionFactory;

  /**
   * Hibernate transaction timeout. This value is global per database adapter, i.e. on application. Value expressed in
   * seconds. Default value is zero which means blocking connection. It is loaded from
   * <code>hibernate.transaction.timeout</code> property, if exists.
   */
  private int transactionTimeout;

  public HibernateAdapter()
  {
    log.trace("HibernateAdapter()");

    Configuration configuration = new Configuration();
    configuration.configure(CONFIG_FILE);

    String timeout = configuration.getProperty("hibernate.transaction.timeout");
    this.transactionTimeout = timeout != null ? Integer.parseInt(timeout) : 0;

    log.debug("Create Hibernate session factory.");
    sessionFactory = configuration.buildSessionFactory();
  }

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

  public void destroyTransaction()
  {
    transactionsCache.set(null);
  }

  @SuppressWarnings("unchecked")
  public <T> T getSession()
  {
    TransactionImpl transaction = transactionsCache.get();
    if(transaction == null) {
      throw new TransactionException("Missing transaction. Probably attempt to use Hibernate session outside a transaction boundaries.");
    }
    return (T)transaction.getResourceManager();
  }

  public void destroy()
  {
    log.debug("Close Hibernate session factory and releases caches and database connections.");
    // access session factory through getter to have validity check
    SessionFactory sessionFactory = getSessionFactory();
    if(sessionFactory != null) {
      sessionFactory.close();
    }
  }
}
