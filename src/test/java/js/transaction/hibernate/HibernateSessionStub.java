package js.transaction.hibernate;

import java.io.Serializable;
import java.sql.Connection;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LobHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.TypeHelper;
import org.hibernate.UnknownProfileException;
import org.hibernate.jdbc.Work;
import org.hibernate.stat.SessionStatistics;

@SuppressWarnings("rawtypes")
public abstract class HibernateSessionStub implements Session
{
  private static final long serialVersionUID = -459130387525348085L;

  @Override
  public Transaction beginTransaction() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public LockRequest buildLockRequest(LockOptions arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void cancelQuery() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Connection close() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Connection connection() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(Object arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Criteria createCriteria(Class arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Criteria createCriteria(String arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Criteria createCriteria(Class arg0, String arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Criteria createCriteria(String arg0, String arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query createFilter(Object arg0, String arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query createQuery(String arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public SQLQuery createSQLQuery(String arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(Object arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(String arg0, Object arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void disableFetchProfile(String arg0) throws UnknownProfileException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void disableFilter(String arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Connection disconnect() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void doWork(Work arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void enableFetchProfile(String arg0) throws UnknownProfileException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Filter enableFilter(String arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void evict(Object arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void flush() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object get(Class arg0, Serializable arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object get(String arg0, Serializable arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object get(Class arg0, Serializable arg1, LockMode arg2) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object get(Class arg0, Serializable arg1, LockOptions arg2) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object get(String arg0, Serializable arg1, LockMode arg2) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object get(String arg0, Serializable arg1, LockOptions arg2) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CacheMode getCacheMode()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public LockMode getCurrentLockMode(Object arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Filter getEnabledFilter(String arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public EntityMode getEntityMode()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getEntityName(Object arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public FlushMode getFlushMode()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Serializable getIdentifier(Object arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public LobHelper getLobHelper()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query getNamedQuery(String arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Session getSession(EntityMode arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public SessionFactory getSessionFactory()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public SessionStatistics getStatistics()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Transaction getTransaction()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public TypeHelper getTypeHelper()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isConnected()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isDefaultReadOnly()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isDirty() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isFetchProfileEnabled(String arg0) throws UnknownProfileException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isOpen()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isReadOnly(Object arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object load(Class arg0, Serializable arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object load(String arg0, Serializable arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void load(Object arg0, Serializable arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object load(Class arg0, Serializable arg1, LockMode arg2) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object load(Class arg0, Serializable arg1, LockOptions arg2) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object load(String arg0, Serializable arg1, LockMode arg2) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object load(String arg0, Serializable arg1, LockOptions arg2) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void lock(Object arg0, LockMode arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void lock(String arg0, Object arg1, LockMode arg2) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object merge(Object arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object merge(String arg0, Object arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void persist(Object arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void persist(String arg0, Object arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void reconnect() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void reconnect(Connection arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void refresh(Object arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void refresh(Object arg0, LockMode arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void refresh(Object arg0, LockOptions arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void replicate(Object arg0, ReplicationMode arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void replicate(String arg0, Object arg1, ReplicationMode arg2) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Serializable save(Object arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Serializable save(String arg0, Object arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void saveOrUpdate(Object arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void saveOrUpdate(String arg0, Object arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setCacheMode(CacheMode arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setDefaultReadOnly(boolean arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setFlushMode(FlushMode arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setReadOnly(Object arg0, boolean arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void update(Object arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void update(String arg0, Object arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }
}
