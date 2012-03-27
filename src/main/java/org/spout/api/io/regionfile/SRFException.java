package org.spout.api.io.regionfile;

import org.spout.api.io.bytearrayarray.BAAException;

public class SRFException extends BAAException {

	private static final long serialVersionUID = 1L;

	public SRFException(String message, Throwable t) {
		super(message, t);
	}
	
	public SRFException(String message) {
		super(message);
	}
	
}
