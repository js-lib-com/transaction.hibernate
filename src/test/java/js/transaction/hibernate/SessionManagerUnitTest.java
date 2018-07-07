package js.transaction.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import js.transaction.TransactionContext;
import js.util.Classes;
import js.util.Types;
import junit.framework.TestCase;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class SessionManagerUnitTest extends TestCase {
	private static final String SESSION_IMPL = "js.transaction.hibernate.SessionManagerImpl";

	private SessionManager sm;
	private MockHibernateSession session = new MockHibernateSession();

	@Override
	protected void setUp() throws Exception {
		sm = Classes.newInstance(SESSION_IMPL, new MoackTransactionContext(session));
	}

	public void testSaveObject() {
		Person p = Person.instance(0);
		assertEquals(0, p.id);
		sm.save(p);
		assertEquals(1964, p.id);
	}

	public void testSaveCollection() {
		Collection<Person> persons = Person.list();
		sm.save(persons);
		for (Person p : persons) {
			assertEquals(1964, p.id);
		}
	}

	public void testGetObjectById() {
		Person p = sm.get(Person.class, 1964);
		assertEquals(1964, p.id);
	}

	public void testDeleteByType() {
		sm.delete(Person.class, 1964);
		assertEquals("delete from Person where id=1964", session.hql.statement);
	}

	public void testDeleteByEntity() {
		sm.delete("Person", 1964);
		assertEquals("delete from Person where id=1964", session.hql.statement);
	}

	public void testHqlQuery() {
		sm.HQL("select p from Person p where p.id=?", 1964).object();
		assertEquals("select p from Person p where p.id=1964", session.hql.statement);

		sm.HQL("select p from Person p where p.age>?", 40).list();
		assertEquals("select p from Person p where p.age>40", session.hql.statement);
	}

	public void testSqlQuery() {
		sm.SQL("SELECT p.* FROM person p WHERE p.id=?", 1964).object(Person.class);
		assertEquals("SELECT p.* FROM person p WHERE p.id=1964", session.sql.statement);

		sm.SQL("SELECT p.* FROM person p WHERE p.age>?", 40).list(Person.class);
		assertEquals("SELECT p.* FROM person p WHERE p.age>40", session.sql.statement);
	}

	public void testHibernateSessionGetter() {
		Session s = sm.getSession();
		assertNotNull("Session should be not null.", s);
		assertTrue("Session should implement org.hibnernate.Session interface.", Types.isInstanceOf(s, Session.class));
	}

	private static class MoackTransactionContext implements TransactionContext {
		private MockHibernateSession session;

		public MoackTransactionContext(MockHibernateSession session) {
			this.session = session;
		}

		@SuppressWarnings("unchecked")
		public <T> T getSession() {
			return (T) session;
		}
	}

	private static class MockHibernateSession extends HibernateSessionStub {
		private static final long serialVersionUID = 8587551647205460382L;
		private MockHibernateQuery hql;
		private MockHibernateSQLQuery sql;

		@Override
		public void saveOrUpdate(Object object) throws HibernateException {
			Classes.setFieldValue(object, "id", 1964);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Object get(Class clazz, Serializable id) throws HibernateException {
			Object o = Person.instance();
			Classes.setFieldValue(o, "id", id);
			return o;
		}

		@Override
		public Query createQuery(String hql) throws HibernateException {
			this.hql = new MockHibernateQuery(hql);
			return this.hql;
		}

		@Override
		public SQLQuery createSQLQuery(String sql) throws HibernateException {
			this.sql = new MockHibernateSQLQuery(sql);
			return this.sql;
		}
	}

	private static class MockHibernateQuery extends HibernateQueryStub {
		private StatementImpl statementImpl;
		private String statement;

		public MockHibernateQuery(String statement) {
			this.statementImpl = new StatementImpl(statement);
		}

		@Override
		public Query setParameter(int index, Object parameter) throws HibernateException {
			this.statementImpl.setParameter(index, parameter);
			return this;
		}

		@Override
		public int executeUpdate() throws HibernateException {
			this.statement = this.statementImpl.prepare();
			return 1;
		}

		@Override
		public Object uniqueResult() throws HibernateException {
			this.statement = this.statementImpl.prepare();
			return Person.instance();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public List list() throws HibernateException {
			this.statement = this.statementImpl.prepare();
			return Person.list();
		}
	}

	private static class MockHibernateSQLQuery extends HibernateSQLQueryStub {
		private StatementImpl statementImpl;
		private String statement;

		public MockHibernateSQLQuery(String statement) {
			this.statementImpl = new StatementImpl(statement);
		}

		@Override
		public Query setParameter(int index, Object parameter) throws HibernateException {
			this.statementImpl.setParameter(index, parameter);
			return this;
		}

		@Override
		public Object uniqueResult() throws HibernateException {
			this.statement = this.statementImpl.prepare();
			return Person.instance();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public SQLQuery addEntity(Class entityType) {
			if (entityType != Person.class) {
				throw new IllegalArgumentException();
			}
			return this;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public List list() throws HibernateException {
			this.statement = this.statementImpl.prepare();
			return Person.list();
		}
	}

	private static class StatementImpl {
		private String statement;
		private List<Object> indexedParameters = new ArrayList<Object>();

		public StatementImpl(String statement) {
			this.statement = statement;
		}

		public void setParameter(int index, Object parameter) throws HibernateException {
			while (index >= this.indexedParameters.size()) {
				this.indexedParameters.add(null);
			}
			this.indexedParameters.set(index, parameter);
		}

		public String prepare() throws HibernateException {
			StringBuilder sb = new StringBuilder();
			int i = 0, j;
			for (Object parameter : this.indexedParameters) {
				j = this.statement.indexOf('?');
				if (j == -1) {
					j = this.statement.length();
				}
				sb.append(this.statement.substring(i, j));
				if (j == this.statement.length()) {
					break;
				}
				sb.append(parameter.toString());
				i = j + 1;
			}
			return sb.toString();
		}
	}
}
