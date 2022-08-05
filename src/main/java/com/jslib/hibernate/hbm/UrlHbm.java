package com.jslib.hibernate.hbm;

import java.net.URL;

/**
 * URL value Hibernate user defined type.
 * 
 * @author Iulian Rotaru
 */
public class UrlHbm extends ObjectHbm<URL> {
	/** Create user defined type for Java URL. */
	public UrlHbm() {
		super(URL.class);
	}
}
