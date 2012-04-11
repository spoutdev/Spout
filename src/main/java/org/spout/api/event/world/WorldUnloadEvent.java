package org.spout.api.event.world;

import org.spout.api.event.HandlerList;
import org.spout.api.geo.World;

/**
 * Called when a world is unloaded from the server
 */
public class WorldUnloadEvent extends WorldEvent {

	private static final HandlerList handlers = new HandlerList();

	public WorldUnloadEvent(World p) {
		super(p);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
