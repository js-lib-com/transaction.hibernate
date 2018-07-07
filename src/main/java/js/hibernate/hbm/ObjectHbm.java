package js.hibernate.hbm;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import js.converter.Converter;
import js.converter.ConverterRegistry;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * Generic object for Hibernate user defined type.
 * 
 * @author Iulian Rotaru
 * @param <T> Java type to create Hibernate user defined type for.
 */
public abstract class ObjectHbm<T> implements UserType {
	/** Converter instance. */
	private Converter converter;
	/** Type to create Hibernate user type for. */
	private Class<T> clazz;

	/**
	 * Create Hibernate user defined type for request Java class.
	 * 
	 * @param clazz Java class to create Hibernate user type for.
	 */
	public ObjectHbm(Class<T> clazz) {
		this.converter = ConverterRegistry.getConverter();
		this.clazz = clazz;
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	@Override
	public Object deepCopy(Object object) throws HibernateException {
		return object;
	}

	@Override
	public Serializable disassemble(Object object) throws HibernateException {
		return (Serializable) object;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y) {
			return true;
		}
		if (null == x || null == y) {
			return false;
		}
		return x.equals(y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object nullSafeGet(ResultSet resultSet, String[] values, Object owner) throws HibernateException, SQLException {
		String value = resultSet.getString(values[0]);
		Object object = null;
		if (!resultSet.wasNull()) {
			if (value.trim().length() != 0) {
				if (clazz.isEnum()) {
					object = Enum.valueOf((Class<? extends Enum>) clazz, value);
				} else {
					object = converter.asObject(value, clazz);
				}
			}
		}
		return object;
	}

	@Override
	public void nullSafeSet(PreparedStatement preparedStatement, Object object, int index) throws HibernateException, SQLException {
		if (object == null) {
			preparedStatement.setNull(index, Types.VARCHAR);
			return;
		}
		if (clazz.isEnum()) {
			preparedStatement.setString(index, ((Enum<?>) object).name());
			return;
		}
		String value = converter.asString(object);
		if (value == null) {
			preparedStatement.setNull(index, Types.VARCHAR);
			return;
		}
		preparedStatement.setString(index, value);
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

	@Override
	public Class<?> returnedClass() {
		return clazz;
	}

	/** Supported SQL types. */
	private static final int[] SQL_TYPES = { Types.VARCHAR };

	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}
}
