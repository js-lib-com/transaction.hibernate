package com.jslib.hibernate.hbm;

import java.util.TimeZone;

/**
 * Time zone Hibernate user defined type.
 * 
 * @author Iulian Rotaru
 */
public class TimeZoneHbm extends ObjectHbm<TimeZone> {
	/** Create user defined type for Java time zone. */
	public TimeZoneHbm() {
		super(TimeZone.class);
	}
}
