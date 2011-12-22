package org.getspout.unchecked.api;

public class SpoutRuntimeException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 6240793608065769585L;

	public SpoutRuntimeException(String msg) {
		super(msg);
	}

	public SpoutRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public SpoutRuntimeException(Throwable cause) {
		super(cause);
	}
}
