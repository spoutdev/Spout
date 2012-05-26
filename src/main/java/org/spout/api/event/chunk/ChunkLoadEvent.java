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

import org.spout.api.event.HandlerList;
import org.spout.api.geo.cuboid.Chunk;

/**
 * Called when a chunk is loaded into the server
 */
public class ChunkLoadEvent extends ChunkEvent{
	private static HandlerList handlers = new HandlerList();
	
	private final boolean newChunk;
	
	public ChunkLoadEvent(Chunk chunk, boolean newChunk) {
		super(chunk);
		
		this.newChunk = newChunk;
	}
	
	/**
	 * Test if newly generated chunk
	 * 
	 * @return true if newly generated (not yet populated), false if existing chunk loaded (may or may not be populated)
	 */
	public boolean isNewChunk() {
		return newChunk;
	}
		
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
