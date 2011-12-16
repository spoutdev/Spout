package org.getspout.api.event;

@SuppressWarnings("serial")
public class EventException extends Exception {

	public EventException(String message) {
		super(message);
	}

	public EventException(String message, Throwable cause) {
		super(message, cause);
	}

	public EventException(Throwable cause) {
		super(cause);
	}
}
