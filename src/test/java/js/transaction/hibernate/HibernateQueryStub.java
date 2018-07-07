package js.transaction.hibernate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;

@SuppressWarnings("rawtypes")
public abstract class HibernateQueryStub implements Query
{
  @Override
  public int executeUpdate() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String[] getNamedParameters() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getQueryString()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String[] getReturnAliases() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Type[] getReturnTypes() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isReadOnly()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator iterate() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List list() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public ScrollableResults scroll() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public ScrollableResults scroll(ScrollMode arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setBigDecimal(int arg0, BigDecimal arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setBigDecimal(String arg0, BigDecimal arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setBigInteger(int arg0, BigInteger arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setBigInteger(String arg0, BigInteger arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setBinary(int arg0, byte[] arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setBinary(String arg0, byte[] arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setBoolean(int arg0, boolean arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setBoolean(String arg0, boolean arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setByte(int arg0, byte arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setByte(String arg0, byte arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setCacheMode(CacheMode arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setCacheRegion(String arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setCacheable(boolean arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setCalendar(int arg0, Calendar arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setCalendar(String arg0, Calendar arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setCalendarDate(int arg0, Calendar arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setCalendarDate(String arg0, Calendar arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setCharacter(int arg0, char arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setCharacter(String arg0, char arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setComment(String arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setDate(int arg0, Date arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setDate(String arg0, Date arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setDouble(int arg0, double arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setDouble(String arg0, double arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setEntity(int arg0, Object arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setEntity(String arg0, Object arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setFetchSize(int arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setFirstResult(int arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setFloat(int arg0, float arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setFloat(String arg0, float arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setFlushMode(FlushMode arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setInteger(int arg0, int arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setInteger(String arg0, int arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setLocale(int arg0, Locale arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setLocale(String arg0, Locale arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setLockMode(String arg0, LockMode arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setLockOptions(LockOptions arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setLong(int arg0, long arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setLong(String arg0, long arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setMaxResults(int arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setParameter(int arg0, Object arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setParameter(String arg0, Object arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setParameter(int arg0, Object arg1, Type arg2)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setParameter(String arg0, Object arg1, Type arg2)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setParameterList(String arg0, Collection arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setParameterList(String arg0, Object[] arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setParameterList(String arg0, Collection arg1, Type arg2) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setParameterList(String arg0, Object[] arg1, Type arg2) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setParameters(Object[] arg0, Type[] arg1) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setProperties(Object arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setProperties(Map arg0) throws HibernateException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setReadOnly(boolean arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setResultTransformer(ResultTransformer arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setSerializable(int arg0, Serializable arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setSerializable(String arg0, Serializable arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setShort(int arg0, short arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setShort(String arg0, short arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setString(int arg0, String arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setString(String arg0, String arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setText(int arg0, String arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setText(String arg0, String arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setTime(int arg0, Date arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setTime(String arg0, Date arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setTimeout(int arg0)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setTimestamp(int arg0, Date arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query setTimestamp(String arg0, Date arg1)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object uniqueResult() throws HibernateException
  {
    throw new UnsupportedOperationException();
  }
}
