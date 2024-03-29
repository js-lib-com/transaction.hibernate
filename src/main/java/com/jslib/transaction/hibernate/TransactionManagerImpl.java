package com.jslib.transaction.hibernate;

import org.hibernate.Session;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.api.transaction.Transaction;
import com.jslib.api.transaction.TransactionException;
import com.jslib.api.transaction.TransactionManager;
import com.jslib.api.transaction.WorkingUnit;

/**
 * Implementation for {@link TransactionManager} interface.
 * 
 * @author Iulian Rotaru
 * @version draft
 */
public final class TransactionManagerImpl implements TransactionManager
{
  private static final Log log = LogFactory.getLog(TransactionManagerImpl.class);

  private HibernateAdapter adapter;

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
      o = (T)workingUnit.exec((S)t.getResourceManager(), args);
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
