package org.spout.api.event.server;

import org.spout.api.event.Event;
import org.spout.api.event.HandlerList;

/**
 * Called when the server has received the command to stop, but before it has begun the shutdown process.
 */
public class ServerStopEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private String message;

	public ServerStopEvent(String message) {
		this.setMessage(message);
	}

	/**
	 * Returns the message that will be sent to players when the server stops.
	 * @return the message to send.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message that players will see when the server stops.
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
