package org.spout.api.io.bytearrayarray;

import java.io.IOException;

/**
 * This exception is thrown when by a PersistentByteArrayMap.
 */
public class BAAException extends IOException {
	private static final long serialVersionUID = 1L;

	public BAAException(String message, Throwable t) {
		super(message, t);
	}
	
	public BAAException(String message) {
		super(message);
	}
}
