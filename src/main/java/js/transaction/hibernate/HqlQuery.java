package js.transaction.hibernate;

import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;

/**
 * Hibernate query language helper.
 * 
 * @author Iulian Rotaru
 */
public interface HqlQuery
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
  HqlQuery param(String name, Object value);

  /**
   * Set the maximum number of rows this query may return. Internally uses
   * {@link org.hibernate.Query#setMaxResults(int)}.
   * 
   * @param rowsCount maximum number of rows count.
   * @return this pointer.
   * @throws IllegalArgumentException if rows count argument is not strict positive.
   */
  HqlQuery limit(int rowsCount);

  /**
   * Set first record offset and maximum number of rows this query may return. Internally uses
   * {@link org.hibernate.Query#setFirstResult(int)} and {@link org.hibernate.Query#setMaxResults(int)}.
   * 
   * @param offset first record offset, starting with 0,
   * @param rowsCount the number of maximum rows count.
   * @return this pointer.
   * @throws IllegalArgumentException if any given argument is negative or rows count is zero.
   */
  HqlQuery limit(int offset, int rowsCount);

  /**
   * Load fields declared as lazy into mapping definition. This method uses internally
   * {@link org.hibernate.Hibernate#initialize(Object)}.
   * 
   * @param lazyFields variable number of field names.
   * @return this pointer.
   * @throws IllegalArgumentException if no lazy field argument supplied.
   */
  HqlQuery load(String... lazyFields);

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
   * Person person = sm.HQL(&quot;from Person where id=?&quot;, personId).object();
   * </pre>
   * 
   * In above, Person class has mapping defined and entity has the same name. Query must return at most one record into
   * results set; there is Hibernate runtime exception if more records. This method uses internally
   * {@link org.hibernate.Query#uniqueResult()}.
   *
   * @param <T> instance type.
   * @return instance of given entity.
   * @throws NonUniqueObjectException if query returns more than one record.
   * @throws HibernateException for Hibernate related fails like bad query syntax or entity not defined.
   */
  <T> T object();

  /**
   * Execute database select and return list of entities. See {@link #object()} for a discussion about entities. This
   * method uses internally {@link org.hibernate.Query#list()}. Usage pattern is like below:
   * 
   * <pre>
   * List&lt;Person&gt; persons = sm.HQL(&quot;from Person&quot;).list();
   * </pre>
   * 
   * @param <T> list type.
   * @return list of entities, possible empty.
   * @throws HibernateException for Hibernate related fails like bad query syntax or entity not defined.
   */
  <T> List<T> list();

  /**
   * Execute database select on two properties and returns them as key/values map, first property in query being the map
   * key. It is user code responsibility to ensure properties to map key/value type compatibility.
   * 
   * <pre>
   * Map&lt;String, Integer&gt; serialMap = sm.HQL(&quot;select serial,id from Ticket where county=?&quot;, county).map();
   * </pre>
   * 
   * Note that in above example database ID is map value, not necessarily map key. Key and value are determined by
   * property position in query, first is key and the second is value.
   * 
   * @param <K> map key type.
   * @param <V> map value type.
   * @return maps of key/values possible empty.
   * @throws HibernateException for Hibernate related fails like bad query syntax or entity not defined.
   */
  <K, V> Map<K, V> map();

  /** Execute update designated by this HQL query. */
  void update();

  /** Surrogate for update used for delete queries. For now just delegate {@link #update()}. */
  void delete();
}
