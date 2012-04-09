package org.spout.api.event.server;

import org.spout.api.event.Event;
import org.spout.api.event.HandlerList;

/**
 * Called when a player/ip is banned/unbanned
 */
public class BanChangeEvent extends Event {

	private static HandlerList handlers = new HandlerList();

	private BanType type;
	private String changed;
	private boolean banned;
	
	public BanChangeEvent(BanType type, String changed, boolean banned) {
		this.type = type;
		this.changed = changed;
		this.banned = banned;
	}
	
	/**
	 * Gets the type of ban that changed
	 *
	 * @return ban type
	 */
	public BanType getBanType() {
		return type;
	}
	
	/**
	 * Gets whether the change is a ban
	 *
	 * @return whether the change is a ban
	 */
	public boolean isBanned() {
		return banned;
	}
	
	/**
	 * Sets whether the change is a ban
	 *
	 * @param whether the change is a ban
	 */
	public void setBanned(boolean banned) {
		this.banned = banned;
	}
	
	/**
	 * Gets the ip/player the change was done to
	 *
	 * @return ban type
	 */
	public String getChanged() {
		return changed;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public enum BanType {
		IP,
		PLAYER;
	}

}