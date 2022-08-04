package js.transaction.hibernate;

import java.io.Serializable;
import java.util.Collection;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;

import jakarta.inject.Inject;
import js.lang.BugError;
import js.transaction.TransactionContext;
import js.util.Classes;
import js.util.Strings;

/**
 * Implementation of {@link SessionManager} interface.
 * 
 * @author Iulian Rotaru
 */
public final class SessionManagerImpl implements SessionManager {
	/** Class logger. */
	private static final Log log = LogFactory.getLog(SessionManagerImpl.class);

	/** Transaction executed in current thread. Used to retrieve hibernate session instance. */
	private final TransactionContext context;

	/**
	 * Construct session manager instance and inject transaction context dependency.
	 * 
	 * @param context transaction executed in current thread.
	 */
	@Inject
	public SessionManagerImpl(TransactionContext context) {
		log.trace("SessionManagerImpl(TransactionContext)");
		this.context = context;
	}

	@Override
	public void save(Object object) {
		getSession().saveOrUpdate(object);
	}

	@Override
	public void save(String entity, Object object) {
		getSession().saveOrUpdate(entity, object);
	}

	@Override
	public <T> void save(Collection<T> collection) {
		for (T t : collection) {
			save(t);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Class<T> type, Serializable id, String... lazyFields) {
		T t = (T) getSession().get(type, id);
		if (t == null) {
			return null;
		}

		for (String lazyField : lazyFields) {
			Hibernate.initialize(Classes.getFieldValue(t, lazyField));
		}
		return t;
	}

	@Override
	public void delete(Class<?> type, Object id) {
		delete(Strings.last(type.getName(), '.'), id);
	}

	@Override
	public void delete(String entityName, Object id) {
		StringBuilder hql = new StringBuilder("delete from ");
		hql.append(entityName);
		hql.append(" where id=?");
		Query q = getSession().createQuery(hql.toString());
		q.setParameter(0, id);
		q.executeUpdate();
	}

	@Override
	public void delete(Class<?> type, Collection<Integer> ids) {
		delete(Strings.last(type.getName(), '.'), ids);
	}

	@Override
	public void delete(String entityName, Collection<Integer> ids) {
		if (ids.isEmpty()) {
			return;
		}
		StringBuilder hql = new StringBuilder("delete from ");
		hql.append(entityName);
		hql.append(" where id in(:ids)");
		Query q = getSession().createQuery(hql.toString());
		q.setParameterList("ids", ids);
		q.executeUpdate();
	}

	@Override
	public boolean exists(String existsClause, Object... parameters) {
		String query = "select 1 from " + existsClause;
		HqlQuery hql = new HqlQueryImpl(getSession(), query, parameters);
		return hql.object() != null;
	}

	@Override
	public HqlQuery HQL(String hql, Object... parameters) {
		return new HqlQueryImpl(getSession(), hql, parameters);
	}

	@Override
	public SqlQuery SQL(String sql, Object... parameters) {
		return new SqlQueryImpl(getSession(), sql, parameters);
	}

	@Override
	public Session getSession() {
		Session session = context.getResourceManager();
		if (session == null) {
			throw new BugError("Attempt to get session object outside a transaction.");
		}
		return session;
	}
}
