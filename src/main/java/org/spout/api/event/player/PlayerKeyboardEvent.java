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
	private Keyboard key;
	private boolean released;

	public PlayerKeyboardEvent(Player p, Keyboard key, boolean released) {
		super(p);
		this.key = key;
		this.released = released;
	}

	/**
	 * Returns the key that triggered this event.
	 * @return The keyboard key.
	 */
	public Keyboard getKey() {
		return key;
	}

	/**
	 * Sets the key for this event firing.
	 *
	 * Ex. If you press W but want the player to trigger S instead.
	 * @param key The actual key of the event
	 */
	public void setKey(Keyboard key) {
		this.key = key;
	}

	/**
	 * Determines if the key was released.
	 * @return True if released, false if first pressed
	 */
	public boolean isReleased() {
		return released;
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
