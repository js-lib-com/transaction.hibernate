package js.hibernate.hbm;

/**
 * Hibernate user defined type for Java class.
 * 
 * @author Iulian Rotaru
 */
@SuppressWarnings("rawtypes")
public class ClassHbm extends ObjectHbm<Class> {
	/** Create user defined type for Java class. */
	public ClassHbm() {
		super(Class.class);
	}
}
