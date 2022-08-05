package com.jslib.hibernate.hbm;

import java.io.File;

/**
 * File Hibernate user defined type.
 * 
 * @author Iulian Rotaru
 */
public class FileHbm extends ObjectHbm<File> {
	/** Create Hibernate user defined type for Java file. */
	public FileHbm() {
		super(File.class);
	}
}
