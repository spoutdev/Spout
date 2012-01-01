package org.getspout.api.event.block;

import org.getspout.api.event.Event;
import org.getspout.api.event.EventSource;
import org.getspout.api.geo.cuboid.Block;

public abstract class BlockEvent extends Event {
	
	private final Block block;
	private final EventSource source;
	
	protected BlockEvent(Block block, EventSource source) {
		this.block = block;
		this.source = source;
	}
	
	/**
	 * Gets the block corresponding to this event
	 * 
	 * @return the block
	 */
	public Block getBlock() {
		return block;
	}
	
	/**
	 * Gets the source of this event
	 * 
	 * @return the event source
	 */
	public EventSource getSource() {
		return source;
	}

}
