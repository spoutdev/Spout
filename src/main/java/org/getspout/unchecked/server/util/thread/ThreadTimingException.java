package org.getspout.unchecked.server.util.thread;

/**
 * An Exception thrown when pulsable thread timing assumption are violated
 */
public class ThreadTimingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ThreadTimingException(String message) {
		super(message);
	}

}
