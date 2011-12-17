package org.getspout.api;

public class SpoutRuntimeException extends RuntimeException {

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
