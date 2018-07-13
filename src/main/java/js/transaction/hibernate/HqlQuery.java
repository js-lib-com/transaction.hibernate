package js.transaction.hibernate;

import java.util.Collection;
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
   * Bind collection to named query parameter and select behavior for empty value. The same rule for positioned
   * parameters applies to this method, see {@link #param(String, Object)}.
   * <p>
   * The third argument is used to select this HQL query behavior when <code>value</code> argument is an empty
   * collection. Not sure if depends on database driver or is by specification, but observed on some: there is exception
   * when use empty collection parameter with <code>IN</code> clause. If true, argument <code>forceEmpty</code> will
   * force {@link #list()} and {@link #list(Class)} to return empty result; otherwise provided collection should not be
   * empty.
   * 
   * @param name parameter name,
   * @param value parameter value,
   * @param forceEmpty force {@link #list()} and {@link #list(Class)} to return empty result if <code>value</code> is
   *          empty.
   * @return this pointer.
   * @throws IllegalArgumentException if parameter value is null.
   */
  HqlQuery param(String name, Collection<?> value, boolean forceEmpty);

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
   * Variant of {@link #list()} that return list of entities of specified type. This variant is especially useful when
   * want to map columns to arbitrary user defined types. Selected column alias is used to invoke setter on user defined
   * type. Column alias should be present even if the same as column name; it is possible for database driver to force
   * column names as upper case if if in HWL query is lower.
   * <p>
   * Usage pattern is like below:
   * 
   * <pre>
   * class Person {
   *    private String name;
   *    private int age;
   *    ...
   *    // properties setters
   * }
   * 
   * List&lt;Person&gt; persons = sm.HQL(&quot;select column1 as name, column2 as age from ... &quot;).list(Person.class);
   * </pre>
   * 
   * Implementation uses Transformers.aliasToBean(type) to bind a built-in result transformer. It is caller
   * responsibility to ensure type compatibility between selected columns and type fields. For example if database
   * numeric value is LONG there will be exception when try to set <code>age</code> value.
   * 
   * @param type requested entity type.
   * @param <T> list type.
   * @return list of entities, possible empty.
   */
  <T> List<T> list(Class<T> type);

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
