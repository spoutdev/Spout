package org.spout.api.io.persistentbytearraymap;

/**
 * This exception is thrown when a PersistentByteArrayMap is accessed after it has been closed.
 */
public class PBAAClosedException extends PBAAException {
	private static final long serialVersionUID = 1L;

	public PBAAClosedException(String message, Throwable t) {
		super(message, t);
	}
	
	public PBAAClosedException(String message) {
		super(message);
	}
}
