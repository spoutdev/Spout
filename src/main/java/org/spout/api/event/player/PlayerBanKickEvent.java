package org.spout.api.event.player;

import org.spout.api.event.HandlerList;
import org.spout.api.event.server.BanChangeEvent.BanType;
import org.spout.api.player.Player;

/**
 * Called when a player is kicked for being banned
 */
public class PlayerBanKickEvent extends PlayerEvent {

	private static HandlerList handlers = new HandlerList();

	private BanType type;
	private String message;
	
	public PlayerBanKickEvent(Player player, BanType type, String message) {
		super(player);
		
		this.type = type;
		this.message = message;
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
	 * Gets the ban's kick message
	 *
	 * @return the kick message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets the ban's kick message
	 *
	 * @return the kick message
	 */
	public void setMessage(String message) {
		this.message = message;
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

}