package org.getspout.api;

public class SpoutException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 450854545313111159L;

	public SpoutException(String msg) {
		super(msg);
	}

	public SpoutException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public SpoutException(Throwable cause) {
		super(cause);
	}
}
