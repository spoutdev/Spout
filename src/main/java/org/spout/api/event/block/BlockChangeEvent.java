/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.event.block;

import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;
import org.spout.api.event.Cause;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.block.BlockSnapshot;

/**
 * Called when a block changes its state.<br/>
 * Implements {@link Cancellable}, which allows this event's outcome to be cancelled.
 */
public class BlockChangeEvent extends BlockEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	public BlockChangeEvent(Block block, BlockSnapshot initial, Cause<?> reason) {
		super(block, reason);
		this.snapshot = initial;
	}

	private BlockSnapshot snapshot;

	/**
	 * Gets the new state for the block, once this event has completed
	 * @return final block state
	 */
	public BlockSnapshot getSnapshot() {
		return snapshot;
	}

	/**
	 * Sets the final block state.
	 * @param newState to set
	 */
	public void setNewState(BlockSnapshot newState) {
		snapshot = newState;
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
