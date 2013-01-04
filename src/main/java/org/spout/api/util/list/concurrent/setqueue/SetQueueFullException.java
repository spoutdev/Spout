package org.spout.api.util.list.concurrent.setqueue;

public class SetQueueFullException extends IllegalStateException {

	private static final long serialVersionUID = 1L;
	
	public SetQueueFullException(String message) {
		super(message);
	}

}
