package org.spout.api.event.player;

import org.spout.api.entity.Player;
import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;
import org.spout.api.input.Keyboard;

/**
 * Event is called when a key is pressed/held/or released from the client
 */
public class PlayerKeyboardEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final Keyboard key;
	private final String rawCommand;
	private final boolean released;

	public PlayerKeyboardEvent(Player p, Keyboard key, boolean released, String rawCommand) {
		super(p);
		this.key = key;
		this.released = released;
		this.rawCommand = rawCommand;
	}

	/**
	 * Returns the key that triggered this event.
	 * @return The keyboard key.
	 */
	public Keyboard getKey() {
		return key;
	}

	/**
	 * Determines if the key was released.
	 * @return True if released, false if first pressed
	 */
	public boolean isReleased() {
		return released;
	}

	/**
	 * Gets the raw command bound to this key.
	 * @return The raw command bound to the key
	 */
	public String getRawCommand() {
		return rawCommand;
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
