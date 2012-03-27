package org.spout.api.io.regionfile;

import org.spout.api.io.persistentbytearraymap.PBAAClosedException;


public class SRFClosedException extends PBAAClosedException {

	private static final long serialVersionUID = 1L;

	public SRFClosedException(String message, Throwable t) {
		super(message, t);
	}
	
	public SRFClosedException(String message) {
		super(message);
	}
	
}
