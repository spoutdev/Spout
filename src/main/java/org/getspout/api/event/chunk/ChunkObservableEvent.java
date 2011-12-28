package org.getspout.api.event.chunk;

import org.getspout.api.event.HandlerList;
import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.player.Player;
import org.getspout.api.util.cuboid.CuboidBuffer;

/**
 * Represents a change in the ability for a player to observe a chunk
 */
public class ChunkObservableEvent extends ChunkEvent {
	
	private final CuboidBuffer buffer;
	private final Player player;
	private final boolean observable;

	public ChunkObservableEvent(Player player, Chunk chunk, CuboidBuffer buffer, boolean observable) {
		super(chunk);
		this.player = player;
		this.observable = observable;
		if (observable) {
			this.buffer = buffer;
		} else {
			this.buffer = null;
		}
	}
	
	private static HandlerList handlers;
	
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
	
	/**
	 * Indicates if the chunk has become observable or non-observable
	 * 
	 * @return true if the chunk is now observable
	 */
	public boolean getObservable() {
		return observable;
	}
	
	/**
	 * Gets the new chunk data.
	 * 
	 * TODO - need to harden against modification
	 * 
	 * @return CuboidBuffer containing the new chunk data
	 */
	public CuboidBuffer getChunkData() {
		return buffer;
	}
	
	/**
	 * Gets the player 
	 */
	public Player getPlayer() {
		return player;
	}
}
