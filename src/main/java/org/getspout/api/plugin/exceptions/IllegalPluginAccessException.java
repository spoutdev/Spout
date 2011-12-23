package org.getspout.api.plugin.exceptions;

public class IllegalPluginAccessException extends RuntimeException {

	private static final long serialVersionUID = -3181800705877027623L;

	public IllegalPluginAccessException() {
	}

	public IllegalPluginAccessException(String msg) {
		super(msg);
	}

}
