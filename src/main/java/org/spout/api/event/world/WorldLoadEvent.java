package org.spout.api.event.world;

import org.spout.api.event.HandlerList;
import org.spout.api.geo.World;

/**
 * Called when a world is loaded into the server
 */
public class WorldLoadEvent extends WorldEvent {
	private static final HandlerList handlers = new HandlerList();

	public WorldLoadEvent(World p) {
		super(p);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
