package org.spout.api.io.bytearrayarray;

/**
 * This exception is thrown when a PersistentByteArrayMap is accessed after it has been closed.
 */
public class BAAClosedException extends BAAException {
	private static final long serialVersionUID = 1L;

	public BAAClosedException(String message, Throwable t) {
		super(message, t);
	}
	
	public BAAClosedException(String message) {
		super(message);
	}
}
