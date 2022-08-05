package com.jslib.transaction.hibernate;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import com.jslib.util.Params;
import com.jslib.util.Types;

/**
 * Implementation of {@link SqlQuery} interface.
 * 
 * @author Iulian Rotaru
 */
final class SqlQueryImpl implements SqlQuery {
	/** Hibernate session. */
	private Session session;
	/** SQL query. */
	private String sql;
	/** Indexed parameters into order declared by SQL query. */
	private Object[] positionedParameters;
	/** Named parameters in no particular order. */
	private Map<String, Object> namedParameters = new HashMap<String, Object>();
	/** First result offset. */
	private int offset;
	/** Maximum allowed number of items into results set. */
	private int rowsCount;
	/** Map of Java type associated with returned column name. */
	private Map<String, Type> scalars = new HashMap<String, Type>();

	/**
	 * Construct SQL query instance. Is caller responsibility to ensure the number and order of the given positioned parameters
	 * match query wild cards.
	 * 
	 * @param session database session,
	 * @param sql SQL query,
	 * @param positionedParameters variable number of positional parameters.
	 */
	public SqlQueryImpl(Session session, String sql, Object... positionedParameters) {
		this.session = session;
		this.sql = sql;
		this.positionedParameters = positionedParameters;
	}

	@Override
	public SqlQuery param(String name, Object value) {
		Params.notNull(value, "Value");
		namedParameters.put(name, value);
		return this;
	}

	@Override
	public SqlQuery limit(int rowsCount) {
		if (rowsCount <= 0) {
			throw new IllegalArgumentException("Not strict positive rows count.");
		}
		this.rowsCount = rowsCount;
		return this;
	}

	@Override
	public SqlQuery limit(int offset, int rowsCount) {
		Params.positive(offset, "First record offset");
		Params.strictPositive(rowsCount, "Maximum rows count");
		this.offset = offset;
		this.rowsCount = rowsCount;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T object(String alias, Class<T> type) {
		Params.notNull(alias, "Column name");
		scalar(alias, type);
		return (T) query().uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T object(String alias, Class<T> type, Object defaultValue) {
		Params.notNull(alias, "Column name");
		Params.notNull(type, "Column Type");
		Object object = object(alias, type);
		return (T) (object != null ? object : defaultValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T object(Class<T> entity) {
		Params.notNull(entity, "Entity type");
		return (T) query(entity).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> list(String alias, Class<T> type) {
		Params.notNull(alias, "Column name");
		Params.notNull(type, "Column Type");
		scalar(alias, type);
		return query().list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> list(Class<T> entity) {
		Params.notNull(entity, "Entity type");
		return query(entity).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> Map<K, V> map() {
		SQLQuery query = query();

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

	@Override
	public void update() {
		query().executeUpdate();
	}

	@Override
	public void delete() {
		query().executeUpdate();
	}

	/**
	 * Map of Java class to Hibernate standard basic types. Currently supported Java types are Java boxing primitives and
	 * date/times.
	 */
	private static Map<Class<?>, Type> types = new HashMap<Class<?>, Type>();
	static {
		types.put(Byte.class, StandardBasicTypes.BYTE);
		types.put(Short.class, StandardBasicTypes.SHORT);
		types.put(Integer.class, StandardBasicTypes.INTEGER);
		types.put(Long.class, StandardBasicTypes.LONG);
		types.put(Float.class, StandardBasicTypes.FLOAT);
		types.put(Double.class, StandardBasicTypes.DOUBLE);
		types.put(String.class, StandardBasicTypes.STRING);
		types.put(Boolean.class, StandardBasicTypes.BOOLEAN);
		types.put(Date.class, StandardBasicTypes.DATE);
		types.put(java.sql.Date.class, StandardBasicTypes.DATE);
		types.put(Time.class, StandardBasicTypes.TIME);
		types.put(Timestamp.class, StandardBasicTypes.TIMESTAMP);
	}

	/**
	 * Convenient method to convert Java type to Hibernate standard basic type and insert into scalars map. See {@link #types}
	 * for supported Java types.
	 * 
	 * @param alias column name or alias,
	 * @param type Java type.
	 * @throws IllegalArgumentException if requested Java type is not supported.
	 */
	private void scalar(String alias, Class<?> type) {
		Type t = types.get(type);
		if (t == null) {
			throw new IllegalArgumentException("Unsupported type " + type);
		}
		scalars.put(alias, t);
	}

	/**
	 * Create SQL query object and initialize it from this helper properties.
	 * 
	 * @param entity optional Hibernate entity.
	 * @return newly create SQL object.
	 */
	private SQLQuery query(Class<?>... entity) {
		SQLQuery q = session.createSQLQuery(sql);
		if (entity.length == 1) {
			q.addEntity(entity[0]);
		}
		for (Map.Entry<String, Type> scalar : scalars.entrySet()) {
			q.addScalar(scalar.getKey(), scalar.getValue());
		}
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
