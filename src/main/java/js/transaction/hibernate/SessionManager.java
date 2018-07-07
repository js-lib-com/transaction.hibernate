package js.transaction.hibernate;

import java.io.Serializable;
import java.util.Collection;

import org.hibernate.Session;

/**
 * Hibernate session manager present a simplified alternative for Hibernate session interface. It is used to save, query
 * and delete objects and collections to/from persistence layer. Direct object and collection handling is performed by
 * this interface; for more complex queries delegate query interfaces, see {@link HqlQuery} and {@link SqlQuery}.
 * <p>
 * Delete methods provided by this interface work only if entity ID property is named <code>id</code>. Behavior is not
 * specified if entity uses alternative names.
 */
public interface SessionManager
{
  /**
   * Save or update the object. This method uses default entity name inferred from object class simple name.
   * 
   * @param object object to save.
   */
  void save(Object object);

  /**
   * Save or update object instance mapped to requested entity name.
   * 
   * @param entity mapped entity name,
   * @param object object instance to persist.
   */
  void save(String entity, Object object);

  /**
   * Convenient way to save or update collection of objects. This method simply traverse the collection invoking
   * {@link #save(Object)} for every object.
   * 
   * @param collection collection of objects to persist.
   * @param <T> collection type.
   */
  <T> void save(Collection<T> collection);

  /**
   * Query to retrieve object by ID. Expected type should be properly mapped and supports lazy fields. By default fields
   * declared as lazy into mapping are not loaded from database and are left null into returned instance. Anyway, this
   * method has an optional lazy field names argument that is used to force fields loading.
   * 
   * @param type expected object type,
   * @param id desired object ID,
   * @param lazyFields optional lazy mapped fields to be initialized.
   * @param <T> class type.
   * @return persisted instance or null.
   */
  <T> T get(Class<T> type, Serializable id, String... lazyFields);

  /**
   * Delete the object identified by <code>id</code>, of requested <code>type</code>. Type is used to infer entity name
   * that on its turn determine the table on which delete is performed.
   * <p>
   * This method works only if entity ID property is named <code>id</code>. This method behavior is not specified if
   * entity uses alternative names.
   * 
   * @param type object type,
   * @param id object id.
   */
  void delete(Class<?> type, Object id);

  /**
   * Delete the object identified by <code>id</code> and mapped to requested entity. Entity is used to determine the
   * table on which delete is performed.
   * <p>
   * This method works only if entity ID property is named <code>id</code>. This method behavior is not specified if
   * entity uses alternative names.
   * 
   * @param entityName entity name,
   * @param id object id.
   */
  void delete(String entityName, Object id);

  /**
   * Delete collection of objects of requested type, in a single database query. This method uses type to infer entity
   * name, that is, uses type simple name. Note that this method does not repeatedly invoke
   * {@link #delete(Class, Object)} on collection id values; it executes delete in a single database statement using
   * <code>IN</code> clause.
   * <p>
   * This method works only if entity ID property is named <code>id</code>. This method behavior is not specified if
   * entity uses alternative names.
   * 
   * @param type object type,
   * @param ids collection of id values.
   */
  void delete(Class<?> type, Collection<Integer> ids);

  /**
   * Delete collection of objects mapped to requested entity, in a single database query. Note that this method does not
   * repeatedly invoke {@link #delete(String, Object)} on collection id values; it executes delete in a single database
   * statement using <code>IN</code> clause.
   * <p>
   * This method works only if entity ID property is named <code>id</code>. This method behavior is not specified if
   * entity uses alternative names.
   * 
   * @param entityName Hibernate entity name,
   * @param ids collection of id values.
   */
  void delete(String entityName, Collection<Integer> ids);

  boolean exists(String existsClause, Object... parameters);

  /**
   * Create a HQL query instance wrapping given query string.
   * 
   * @param hql HQL statement with optional positioned parameters markup, that is question mark sign,
   * @param parameters variable number of parameters mapped by position.
   * @return HQL query instance.
   */
  HqlQuery HQL(String hql, Object... parameters);

  /**
   * Create a SQL query instance wrapping given query string.
   * 
   * @param sql SQL statement with optional positioned parameters markup, that is question mark sign,
   * @param parameters variable number of parameters mapped by position.
   * @return SQL query instance.
   */
  SqlQuery SQL(String sql, Object... parameters);

  /**
   * Access to <em>low level</em> ;-) Hibernate session.
   * 
   * @return Hibernate session.
   */
  Session getSession();
}
