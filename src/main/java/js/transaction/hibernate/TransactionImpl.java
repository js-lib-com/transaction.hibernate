package js.transaction.hibernate;

import js.transaction.Transaction;
import js.transaction.TransactionException;

import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * Transaction implementation for Hibernate session.
 * 
 * @author Iulian Rotaru
 * @version draft
 */
final class TransactionImpl implements Transaction
{
  /** Hibernate session factory adapter. */
  private final HibernateAdapter adapter;

  /** Hibernate session. */
  private final Session session;

  /** Hibernate transaction. */
  private org.hibernate.Transaction transaction;

  /**
   * Nesting level count used to allow for nesting transactions. {@link HibernateAdapter#createTransaction(boolean)}
   * increments this counter if a transaction is already present on current thread but avoid creating a new one. On
   * {@link #close()} decrement nesting level and perform the actual close only if nesting level is 0.
   */
  private int nestingLevel;

  /** A read only transaction does not explicitly begin or commit/rollback but rely on database (driver). */
  private final boolean readOnly;

  /** Flag to detect if transaction working unit is actually using transactional session. */
  private boolean unused = true;

  /** Flag indicating that transaction was closes and is not longer legal to operate on it. */
  private boolean closed = false;

  /**
   * Create transaction instance.
   * 
   * @param adapter Hibernate adapter,
   * @param transactionTimeout transaction timeout, seconds,
   * @param readOnly flag true for read-only transactions.
   */
  public TransactionImpl(HibernateAdapter adapter, int transactionTimeout, boolean readOnly)
  {
    this.adapter = adapter;
    this.session = adapter.getSessionFactory().openSession();
    this.readOnly = readOnly;

    // do not create transaction boundaries if session is read-only
    if(!readOnly) {
      try {
        this.transaction = this.session.getTransaction();
        if(transactionTimeout > 0) {
          this.transaction.setTimeout(transactionTimeout);
        }
        this.transaction.begin();
      }
      catch(HibernateException e) {
        // ensure session is closed even if starting a new transaction fails
        this.session.close();
        throw new TransactionException(e);
      }
    }
  }

  @Override
  public void commit()
  {
    if(nestingLevel > 0) {
      return;
    }
    if(readOnly) {
      throw new IllegalStateException("Read-only transaction does not allow commit.");
    }
    try {
      transaction.commit();
    }
    catch(Exception e) {
      throw new TransactionException(e);
    }
    finally {
      close();
    }
  }

  @Override
  public void rollback()
  {
    if(nestingLevel > 0) {
      return;
    }
    if(readOnly) {
      throw new IllegalStateException("Read-only transaction does not allow rollback.");
    }
    try {
      if(session.isOpen()) {
        transaction.rollback();
      }
    }
    catch(Exception e) {
      throw new TransactionException(e);
    }
    finally {
      close();
    }
  }

  @Override
  public boolean close()
  {
    if(closed) {
      return true;
    }
    if(nestingLevel-- > 0) {
      return false;
    }
    closed = true;

    try {
      session.close();
    }
    catch(Exception e) {
      throw new TransactionException(e);
    }
    finally {
      adapter.destroyTransaction();
    }
    return true;
  }

  @Override
  public boolean unused()
  {
    return unused;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getSession()
  {
    if(closed) {
      throw new IllegalStateException("Closed Hibernate session.");
    }
    unused = false;
    return (T)session;
  }

  /**
   * Increment transaction nesting level. Invoked from {@link HibernateAdapter#createTransaction(boolean)}.
   * 
   * @see #nestingLevel
   */
  public void incrementTransactionNestingLevel()
  {
    nestingLevel++;
  }

  @Override
  protected void finalize() throws Throwable
  {
    close();
  }
}
