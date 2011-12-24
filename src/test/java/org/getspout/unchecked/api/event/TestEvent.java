package org.getspout.unchecked.api.event;

public class TestEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers;}
}
