/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
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
