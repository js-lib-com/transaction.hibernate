package js.transaction.hibernate;

import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

/**
 * SQL helper interface.
 * 
 * @author Iulian Rotaru
 */
public interface SqlQuery
{
  /**
   * Bind a not null value to a named query parameter. Please remember that if mix positioned and named parameters
   * former must appear first into query; more specifically, all positioned parameters MUST precede first named one.
   * 
   * @param name parameter name,
   * @param value parameter value.
   * @return this pointer.
   * @throws IllegalArgumentException if parameter value is null.
   */
  SqlQuery param(String name, Object value);

  /**
   * Set the maximum number of rows this query may return. Internally uses
   * {@link org.hibernate.Query#setMaxResults(int)}.
   * 
   * @param rowsCount maximum number of rows count.
   * @return this pointer.
   * @throws IllegalArgumentException if rows count argument is not strict positive.
   */
  SqlQuery limit(int rowsCount);

  /**
   * Set first record offset and maximum number of rows this query may return. Internally uses
   * {@link org.hibernate.Query#setFirstResult(int)} and {@link org.hibernate.Query#setMaxResults(int)}.
   * 
   * @param offset first record offset, starting with 0,
   * @param rowsCount the number of maximum rows count.
   * @return this pointer.
   * @throws IllegalArgumentException if any given argument is negative or rows count is zero.
   */
  SqlQuery limit(int offset, int rowsCount);

  /**
   * Execute scalar query. Execute this query select and convert result to given scalar value type. Results set must
   * have at most one record with a single column; if more Hibernate runtime exception is thrown.
   * <p>
   * In this interface context scalar type is a simple Java class that wrap a single value, that is, is not an aggregate
   * object or collection, e.g. {@link java.io.File} that wraps a file path. Internally uses a hash to bind Java type to
   * {@link StandardBasicTypes} then delegate {@link SQLQuery#addScalar(String, Type)}.
   * 
   * <pre>
   * Integer count = sm.SQL(&quot;SELECT COUNT(*) AS count FROM table&quot;).object(&quot;count&quot;, Integer.class);
   * </pre>
   * 
   * This method returns scalar value or null. If result is to be assigned to a primitive type uses
   * {@link #object(String, Class, Object) variant} with default value.
   * 
   * @param alias result column name or alias,
   * @param type scalar value type to bind.
   * @param <T> instance type.
   * @return scalar value or null.
   * @throws NonUniqueObjectException if query returns more than one record.
   * @throws HibernateException for Hibernate related fails like bad query syntax.
   */
  <T> T object(String alias, Class<T> type);

  /**
   * Convenient alternative to {@link #object(String, Class)} method returning given default value instead of null. This
   * is especially useful when assign scalar type to primitive Java types.
   * 
   * <pre>
   * int count = sm.SQL(&quot;SELECT COUNT(*) AS count FROM table&quot;).object(&quot;count&quot;, Integer.class, 0);
   * </pre>
   * 
   * @param alias result column name or alias,
   * @param type scalar value type to bind,
   * @param defaultValue value to return if query returns null.
   * @param <T> instance type.
   * @return scalar value or default.
   * @throws NonUniqueObjectException if query returns more than one record.
   * @throws HibernateException for Hibernate related fails like bad query syntax.
   */
  <T> T object(String alias, Class<T> type, Object defaultValue);

  /**
   * Execute database select and return single entity. In the scope of this interface an entity is a Java type with
   * defined ORM mapping. Hibernate engine will rise exception if given entity argument is not defined.
   * 
   * <pre>
   * class Person {
   *    int id;
   *    String name;
   * }
   * . . .
   * Person person = sm.SQL(&quot;SELECT id,name FROM person WHERE id=?&quot;, personId).object(Person.class);
   * </pre>
   * 
   * In above, Person class has mapping defined; not all class fields must be present into mapping definition but all
   * mapped fields, which actually defined the entity, must be present into query result set. Also query must return at
   * most one record into results set; there is Hibernate runtime exception if more records. This method uses internally
   * {@link org.hibernate.SQLQuery#uniqueResult()}.
   * 
   * @param entity entity to retrieve.
   * @param <T> instance type.
   * @return instance of given entity.
   * @throws NonUniqueObjectException if query returns more than one record.
   * @throws HibernateException for Hibernate related fails like bad query syntax or missing entity field from results
   *           set.
   */
  <T> T object(Class<T> entity);

  /**
   * Execute database select and return list of scalar value. See {@link #object(String, Class)} for scalar value
   * definition. This method uses internally {@link Query#list()}.
   * 
   * <pre>
   * List&lt;Integer&gt; prices = sm.SQL(&quot;SELECT price FROM stock&quot;).list(&quot;price&quot;, Integer.class);
   * </pre>
   * 
   * @param alias result column name or alias,
   * @param type scalar value type to bind.
   * @param <T> list type.
   * @return list of scalar type.
   * @throws HibernateException for Hibernate related fails like bad query syntax.
   */
  <T> List<T> list(String alias, Class<T> type);

  /**
   * Execute database select and return list of entities. See {@link #object(Class)} for a discussion about entities.
   * This method uses internally {@link Query#list()}. Usage pattern is like below:
   * 
   * <pre>
   * List&lt;Person&gt; persons = sm.SQL(&quot;SELECT id,name FROM person&quot;).list(Person.class);
   * </pre>
   * 
   * @param entity entity to retrieve.
   * @param <T> list type.
   * @return list of entities, possible empty.
   * @throws HibernateException for Hibernate related fails like bad query syntax.
   */
  <T> List<T> list(Class<T> entity);

  /**
   * Execute database select on two columns and returns them as key/values map, first column in query being the map key.
   * It is user code responsibility to ensure columns to map key/value type compatibility.
   * 
   * <pre>
   * Map&lt;String, Integer&gt; serialMap = sm.SQL(&quot;SELECT serial,id FROM ticket WHERE county=?&quot;, county).map();
   * </pre>
   * 
   * Note that in above example database ID is map value, not necessarily map key. Key and value are determined by
   * column position in query, first is key and the second is value.
   * 
   * @param <K> map key type.
   * @param <V> map value type.
   * @return maps of key/values possible empty.
   * @throws HibernateException for Hibernate related fails like bad query syntax or entity not defined.
   */
  <K, V> Map<K, V> map();

  /** Execute update designated by this SQL query. */
  void update();

  /** Surrogate for update used for delete queries. For now just delegate {@link #update()}. */
  void delete();
}
