/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.event.chunk;

import java.util.List;

import org.spout.api.event.HandlerList;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.math.Vector3;

/**
 * Called when {@link Block} data in {@link Chunk} has been updated.
 */
public class ChunkUpdatedEvent extends ChunkEvent{
	private static HandlerList handlers = new HandlerList();
	
	private final List<Vector3> blocks;
	
	public ChunkUpdatedEvent(Chunk chunk, List<Vector3> blocks) {
		super(chunk);
		this.blocks = blocks;
	}

	/**
	 * Test if whole chunk was invalidated, or too many updates occurred to be listed.
	 * 
	 * @return true if whole chunk invalidated, false if one or more blocks
	 */
	public boolean isWholeChunkUpdate() {
		return blocks == null;
	}
	
	/**
	 * Get number of blocks updated.
	 * 
	 * @return number of blocks, or -1 if whole chunk was invalidated
	 */
	public int getBlockUpdateCount() {
		if (blocks == null) {
			return -1;
		}

		return blocks.size();
	}
	/**
	 * Get chunk-relative coordinates of Nth updated block
	 * 
	 * @param n index of block update
	 * @return chunk-relative coordinates of updated block, or null if bad index or whole chunk update
	 */
	public Vector3 getBlockUpdate(int n) {
		if ((blocks != null) && (n >= 0) && (n < blocks.size())) {
			return blocks.get(n);
		}
		return null;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
