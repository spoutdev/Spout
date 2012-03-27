package org.spout.api.io.persistentbytearraymap;

import java.io.IOException;

/**
 * This exception is thrown when by a PersistentByteArrayMap.
 */
public class PBAAException extends IOException {
	private static final long serialVersionUID = 1L;

	public PBAAException(String message, Throwable t) {
		super(message, t);
	}
	
	public PBAAException(String message) {
		super(message);
	}
}
