package org.getspout.api.event.entity;

import org.getspout.api.event.HandlerList;

/**
 * Called when an entity is about to be destroyed
 * This is the opposite of Spawn
 *
 */
public class EntityDespawnEvent extends EntityEvent {
	private static HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}


}
