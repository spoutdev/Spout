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

import org.getspout.api.event.EventSource;
import org.getspout.api.geo.cuboid.Block;
import org.getspout.api.material.block.BlockSnapshot;

/**
 * Called when a block changes its state.
 */
public abstract class BlockChangeEvent extends BlockEvent {
	public BlockChangeEvent(Block block, EventSource source) {
		super(block, source);
	}

	private BlockSnapshot snapshot;

	/**
	 * Gets the new state for the block, once this event has completed
	 *
	 * @return final block state
	 */
	public BlockSnapshot getSnapshot() {
		return snapshot;
	}

	/**
	 * Sets the final block state.
	 *
	 * @param state to set
	 */
	public void setNewState(BlockSnapshot newState) {
		snapshot = newState;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

}
