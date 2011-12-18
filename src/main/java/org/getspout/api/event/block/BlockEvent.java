/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.event.block;

import org.getspout.api.block.Block;
import org.getspout.api.event.Event;
import org.getspout.api.event.EventSource;

/**
 * Block-related event.
 */
public abstract class BlockEvent extends Event {
	private Block block;
	private EventSource source;
	public BlockEvent(Block block, EventSource source) {
		this.block = block;
		this.source = source;
	}
	
	/**
	 * Gets the source of this event. It may be a player, an entity, the world, a plugin, etc.
	 * @return event source
	 */
	public final EventSource getSource() {
		return source;
	}
	
	protected void setSource(EventSource source) {
		this.source = source;
	}

	/**
	 * Gets the block involved in this event.
	 * @return
	 */
	public final Block getBlock() {
		return block;
	}

	protected void setBlock(Block block) {
		this.block = block;
	}

}
