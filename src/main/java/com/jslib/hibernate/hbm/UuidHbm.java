package com.jslib.hibernate.hbm;

import java.util.UUID;

/**
 * UUID Hibernate user defined type.
 * 
 * @author Iulian Rotaru
 */
public class UuidHbm extends ObjectHbm<UUID> {
	/** Create user defined type for Java UUID. */
	public UuidHbm() {
		super(UUID.class);
	}
}
