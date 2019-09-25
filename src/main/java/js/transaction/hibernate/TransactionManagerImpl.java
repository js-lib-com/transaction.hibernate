package js.transaction.hibernate;

import org.hibernate.Session;

import js.lang.Config;
import js.lang.ConfigException;
import js.log.Log;
import js.log.LogFactory;
import js.transaction.Transaction;
import js.transaction.TransactionException;
import js.transaction.TransactionManager;
import js.transaction.WorkingUnit;

/**
 * Implementation for {@link TransactionManager} interface.
 * 
 * @author Iulian Rotaru
 * @version draft
 */
public final class TransactionManagerImpl implements TransactionManager
{
  /** Class logger. */
  private static final Log log = LogFactory.getLog(TransactionManagerImpl.class);

  /** Adapter for Hibernate ORM engine. */
  private HibernateAdapter adapter;

  /** Create transaction manager instance. */
  public TransactionManagerImpl()
  {
    log.trace("TransactionManagerImpl()");
    this.adapter = new HibernateAdapter();
  }

  /**
   * Retrieve internal Hibernate session, for testing.
   * 
   * @return reference to Hibernate session.
   */
  public Session getSession()
  {
    return adapter.getSession();
  }

  @Override
  public void config(Config config) throws ConfigException
  {
    log.trace("config(Config)");
    adapter.config(config);
  }

  @Override
  public Transaction createTransaction(String schema)
  {
    return adapter.createTransaction(false);
  }

  @Override
  public Transaction createReadOnlyTransaction(String schema)
  {
    return adapter.createTransaction(true);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <S, T> T exec(String schema, WorkingUnit<S, T> workingUnit, Object... args)
  {
    Transaction t = createTransaction(schema);
    T o = null;
    try {
      o = (T)workingUnit.exec((S)t.getSession(), args);
      t.commit();
    }
    catch(Exception e) {
      t.rollback();
      throw new TransactionException(e);
    }
    finally {
      t.close();
    }
    return o;
  }

  @Override
  public <S, T> T exec(WorkingUnit<S, T> workingUnit, Object... args) throws TransactionException
  {
    return exec(null, workingUnit, args);
  }

  @Override
  public void destroy()
  {
    log.trace("destroy()");
    adapter.destroy();
  }
}
