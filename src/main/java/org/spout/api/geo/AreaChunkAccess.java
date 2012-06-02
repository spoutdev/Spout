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
package org.spout.api.geo;

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.math.Vector3;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;

public interface AreaChunkAccess extends AreaBlockAccess {

	/**
	 * Gets the {@link Chunk} at chunk coordinates (x, y, z)
	 *
	 * @param x coordinate of the chunk
	 * @param y coordinate of the chunk
	 * @param z coordinate of the chunk
	 * @return the chunk
	 */
	@LiveRead
	public Chunk getChunk(int x, int y, int z);

	/**
	 * Gets the {@link Chunk} at chunk coordinates (x, y, z)
	 *
	 * @param x coordinate of the chunk
	 * @param y coordinate of the chunk
	 * @param z coordinate of the chunk
	 * @param loadopt to control whether to load and/or generate the chunk, if needed
	 * @return the chunk
	 */
	@LiveRead
	public Chunk getChunk(int x, int y, int z, LoadOption loadopt);

	/**
	 * Gets if a chunk is contained in this area
	 * @param x coordinate of the chunk
	 * @param y coordinate of the chunk
	 * @param z coordinate of the chunk
	 * @return True if it is contained, False if not
	 */
	public boolean containsChunk(int x, int y, int z);

	/**
	 * Gets the {@link Chunk} at block coordinates (x, y, z)
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return the chunk
	 */
	@LiveRead
	public Chunk getChunkFromBlock(int x, int y, int z);

	/**
	 * Gets the {@link Chunk} at block coordinates (x, y, z)
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @param loadopt to control whether to load and/or generate the chunk, if needed
	 * @return the chunk
	 */
	@LiveRead
	public Chunk getChunkFromBlock(int x, int y, int z, LoadOption loadopt);

	/**
	 * Gets the {@link Chunk} at the given position
	 *
	 * @param position of the block
	 * @return the chunk
	 */
	@LiveRead
	public Chunk getChunkFromBlock(Vector3 position);

	/**
	 * Gets the {@link Chunk} at the given position
	 *
	 * @param position of the block
	 * @param loadopt to control whether to load and/or generate the chunk, if needed
	 * @return the chunk
	 */
	@LiveRead
	public Chunk getChunkFromBlock(Vector3 position, LoadOption loadopt);

	/**
	 * True if the region has a loaded chunk at the (x, y, z).
	 *
	 * @param x coordinate of the chunk
	 * @param y coordinate of the chunk
	 * @param z coordinate of the chunk
	 * @return true if chunk exists
	 */
	@LiveRead
	public boolean hasChunk(int x, int y, int z);

	/**
	 * True if the region has a loaded chunk at the block coordinates (x, y, z).
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return true if chunk exists
	 */
	@LiveRead
	public boolean hasChunkAtBlock(int x, int y, int z);

	/**
	 * Queues a chunk for saving at the next available opportunity.
	 *
	 * @param x coordinate of the chunk
	 * @param y coordinate of the chunk
	 * @param z coordinate of the chunk
	 */
	@DelayedWrite
	public void saveChunk(int x, int y, int z);

	/**
	 * Unloads a chunk, and queues it for saving, if requested.
	 *
	 * @param x coordinate of the chunk
	 * @param y coordinate of the chunk
	 * @param z coordinate of the chunk
	 * @Param whether to save this chunk
	 */
	@DelayedWrite
	public void unloadChunk(int x, int y, int z, boolean save);
	
	/**
	 * Gets the number of currently loaded chunks
	 * 
	 * @return number of loaded chunks
	 */
	@SnapshotRead
	public int getNumLoadedChunks();
}
