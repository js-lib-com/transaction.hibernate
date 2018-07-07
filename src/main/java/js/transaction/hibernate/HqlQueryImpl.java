package js.transaction.hibernate;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import js.util.Classes;
import js.util.Params;
import js.util.Types;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

/**
 * Implementation of {@link HqlQuery} interface.
 * 
 * @author Iulian Rotaru
 */
final class HqlQueryImpl implements HqlQuery {
	/** Hibernate session. */
	private Session session;
	/** Hibernate query. */
	private String hql;
	/** Positioned parameters into order declared by Hibernate query. */
	private Object[] positionedParameters;
	/** Named parameters in no particular order. */
	private Map<String, Object> namedParameters = new HashMap<String, Object>();
	/** Name for entity fields that should be explicitly loaded. */
	private String[] lazyFields;
	/** First result offset. */
	private int offset;
	/** Maximum allowed number of items into results set. */
	private int rowsCount;

	/**
	 * Create Hibernate query with optional indexed parameters. Is caller responsibility to ensure the number and order of the
	 * given positioned parameters match query wild cards.
	 * 
	 * @param session Hibernate session,
	 * @param hql Hibernate query,
	 * @param positionedParameters variable number of positioned parameters.
	 */
	public HqlQueryImpl(Session session, String hql, Object... positionedParameters) {
		this.session = session;
		this.hql = hql;
		this.positionedParameters = positionedParameters;
	}

	@Override
	public HqlQuery param(String name, Object value) {
		Params.notNull(value, "Value");
		namedParameters.put(name, value);
		return this;
	}

	@Override
	public HqlQuery limit(int rowsCount) {
		this.rowsCount = rowsCount;
		return this;
	}

	@Override
	public HqlQuery limit(int offset, int rowsCount) {
		Params.positive(offset, "First record offset");
		Params.strictPositive(rowsCount, "Maximum rows count");
		this.offset = offset;
		this.rowsCount = rowsCount;
		return this;
	}

	@Override
	public HqlQuery load(String... lazyFields) {
		Params.isTrue(lazyFields.length > 0, "No lazy field argument.");
		this.lazyFields = lazyFields;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> list() {
		List<T> list = query(hql).list();
		if (list.isEmpty()) {
			return list;
		}
		if (lazyFields == null) {
			return list;
		}

		// if there are lazy fields load them explicitly, for all results items

		Class<T> type = (Class<T>) list.get(0).getClass();
		for (String lazyField : lazyFields) {
			Field f = Classes.getField(type, lazyField);
			for (T t : list) {
				Hibernate.initialize(Classes.getFieldValue(t, f));
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> Map<K, V> map() {
		org.hibernate.Query query = query(hql);

		query.setReadOnly(true);
		query.setFetchSize(Integer.MIN_VALUE); // MUST use Integer.MIN_VALUE, other value=fetch all
		ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

		// iterate over results
		Map<K, V> map = new HashMap<K, V>();
		while (results.next()) {
			Object[] row = results.get();
			map.put((K) row[0], (V) row[1]);
		}

		results.close();
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T object() {
		T t = (T) query(hql).uniqueResult();
		if (t == null) {
			return null;
		}
		if (lazyFields == null) {
			return t;
		}

		for (String lazyField : lazyFields) {
			Hibernate.initialize(Classes.getFieldValue(t, lazyField));
		}
		return t;
	}

	@Override
	public void update() {
		query(hql).executeUpdate();
	}

	@Override
	public void delete() {
		query(hql).executeUpdate();
	}

	/**
	 * Create Hibernate query object and initialize it from this helper properties.
	 * 
	 * @param hql query string expressed in HQL.
	 * @return newly created Hibernate query object.
	 */
	private org.hibernate.Query query(String hql) {
		org.hibernate.Query q = session.createQuery(hql);
		for (int i = 0; i < positionedParameters.length; i++) {
			q.setParameter(i, positionedParameters[i]);
		}
		for (Map.Entry<String, Object> entry : namedParameters.entrySet()) {
			Object value = entry.getValue();
			if (Types.isArray(value)) {
				Object[] parameter = (Object[]) value;
				if (parameter.length == 0) {
					throw new HibernateException(String.format("Invalid named parameter |%s|. Empty array.", entry.getKey()));
				}
				q.setParameterList(entry.getKey(), parameter);
			} else if (Types.isCollection(value)) {
				Collection<?> parameter = (Collection<?>) value;
				if (parameter.isEmpty()) {
					throw new HibernateException(String.format("Invalid named parameter |%s|. Empty list.", entry.getKey()));
				}
				q.setParameterList(entry.getKey(), parameter);
			} else {
				q.setParameter(entry.getKey(), value);
			}
		}
		if (offset > 0) {
			q.setFirstResult(offset);
		}
		if (rowsCount > 0) {
			q.setMaxResults(rowsCount);
		}
		return q;
	}
}
