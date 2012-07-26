package org.spout.api.event.server;

import org.spout.api.event.Event;
import org.spout.api.event.HandlerList;

/**
 * Called when the server has finished starting, but before the server has begun processing anything else.
 */
public class ServerStartEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
