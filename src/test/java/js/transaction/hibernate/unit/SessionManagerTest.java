package js.transaction.hibernate.unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import js.transaction.TransactionContext;
import js.transaction.hibernate.SessionManager;
import js.transaction.hibernate.SessionManagerImpl;

@RunWith(MockitoJUnitRunner.class)
public class SessionManagerTest
{
  @Mock
  private TransactionContext context;

  @Mock
  private Session session;

  @Mock
  private Query query;

  @Mock
  private SQLQuery sqlQuery;

  private SessionManager sm;

  @Before
  public void beforeTest() throws Exception
  {
    when(context.getSession()).thenReturn(session);
    sm = new SessionManagerImpl(context);
  }

  @Test
  public void saveObject()
  {
    doAnswer(new Answer<Void>()
    {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable
      {
        Person p = invocation.getArgument(0);
        p.setId(1964);
        return null;
      }

    }).when(session).saveOrUpdate(any(Person.class));

    Person p = new Person();
    assertThat(p.id, equalTo(0));
    sm.save(p);

    verify(session, times(1)).saveOrUpdate(p);
    assertThat(p.id, equalTo(1964));
  }

  @Test
  public void saveCollection()
  {
    final AtomicInteger index = new AtomicInteger(1);
    doAnswer(new Answer<Void>()
    {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable
      {
        Person p = invocation.getArgument(0);
        p.setId(index.getAndIncrement());
        return null;
      }
    }).when(session).saveOrUpdate(any(Person.class));

    List<Person> persons = Arrays.asList(new Person(), new Person());
    sm.save(persons);

    verify(session, times(1)).saveOrUpdate(persons.get(0));
    assertThat(persons.get(0).id, equalTo(1));

    verify(session, times(1)).saveOrUpdate(persons.get(1));
    assertThat(persons.get(1).id, equalTo(2));
  }

  @Test
  public void getObjectById()
  {
    when(session.get(Person.class, 1964)).thenReturn(new Person(1964));

    Person p = sm.get(Person.class, 1964);
    assertThat(p, notNullValue());
    assertThat(p.id, equalTo(1964));
  }

  @Test
  public void deleteByType()
  {
    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    when(session.createQuery(queryCaptor.capture())).thenReturn(query);

    ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
    when(query.setParameter(eq(0), idCaptor.capture())).thenReturn(query);

    sm.delete(Person.class, 1964);

    verify(query, times(1)).executeUpdate();
    assertThat(queryCaptor.getValue(), equalTo("delete from SessionManagerTest$Person where id=?"));
    assertThat(idCaptor.getValue(), equalTo(1964));
  }

  @Test
  public void testDeleteByEntity()
  {
    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    when(session.createQuery(queryCaptor.capture())).thenReturn(query);

    ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
    when(query.setParameter(eq(0), idCaptor.capture())).thenReturn(query);

    sm.delete("Person", 1964);

    verify(query, times(1)).executeUpdate();
    assertThat(queryCaptor.getValue(), equalTo("delete from Person where id=?"));
    assertThat(idCaptor.getValue(), equalTo(1964));
  }

  @Test
  public void hqlQuery()
  {
    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    when(session.createQuery(queryCaptor.capture())).thenReturn(query);

    ArgumentCaptor<Integer> positionCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
    when(query.setParameter(positionCaptor.capture(), idCaptor.capture())).thenReturn(query);

    sm.HQL("select p from Person p where p.id=?", 1964).object();

    assertThat(queryCaptor.getValue(), equalTo("select p from Person p where p.id=?"));
    assertThat(positionCaptor.getValue(), equalTo(0));
    assertThat(idCaptor.getValue(), equalTo(1964));

    // there is no named parameter
    verify(query, times(0)).setParameter(any(String.class), any(Object.class));
  }

  @Test
  public void sqlQuery()
  {
    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    when(session.createSQLQuery(queryCaptor.capture())).thenReturn(sqlQuery);

    ArgumentCaptor<Integer> positionCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Integer> parameterCaptor = ArgumentCaptor.forClass(Integer.class);
    when(sqlQuery.setParameter(positionCaptor.capture(), parameterCaptor.capture())).thenReturn(sqlQuery);

    sm.SQL("SELECT p.* FROM person p WHERE p.id=?", 1964).object(Person.class);

    assertThat(queryCaptor.getValue(), equalTo("SELECT p.* FROM person p WHERE p.id=?"));
    assertThat(positionCaptor.getValue(), equalTo(0));
    assertThat(parameterCaptor.getValue(), equalTo(1964));

    // there is no named parameter
    verify(sqlQuery, times(0)).setParameter(any(String.class), any(Object.class));
  }

  @Test
  public void hibernateSessionGetter()
  {
    Session s = sm.getSession();
    assertThat(s, notNullValue());
    assertThat(s, instanceOf(Session.class));
  }

  // ----------------------------------------------------------------------------------------------
  // FIXTURE

  private static final class Person
  {
    int id;

    public Person()
    {
    }

    public Person(int id)
    {
      this.id = id;
    }

    public void setId(int id)
    {
      this.id = id;
    }
  }
}
