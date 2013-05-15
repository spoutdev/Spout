package org.spout.api.event.world;

import org.spout.api.entity.Player;
import org.spout.api.event.HandlerList;
import org.spout.api.geo.World;

/**
 * Called when a {@link Player} enters a world.
 */
public class PlayerEnterWorldEvent extends WorldEvent {
	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	
	public PlayerEnterWorldEvent(World world, Player player) {
		super(world);
		this.player = player;
	}
	
	/**
	 * Returns the player entering the world.
	 * @return player
	 */
	public Player getPlayer() {
		return player;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
