package org.getspout.api.event.chunk;

import org.getspout.api.event.Event;
import org.getspout.api.geo.cuboid.Chunk;

public abstract class ChunkEvent extends Event {
	
	protected ChunkEvent(Chunk chunk) {
		this.chunk = chunk;
	}
	
	private final Chunk chunk;
	
	/**
	 * Gets the chunk associated with this event
	 * 
	 * @return
	 */
	public Chunk getChunk() {
		return chunk;
	}

}
