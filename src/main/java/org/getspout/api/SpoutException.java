package org.getspout.api;

public class SpoutException extends Exception {
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
