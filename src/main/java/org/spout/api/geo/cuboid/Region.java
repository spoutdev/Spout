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
package org.spout.api.geo.cuboid;

import java.util.Iterator;
import java.util.Set;

import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.geo.AreaChunkAccess;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.BitSize;
import org.spout.api.player.Player;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;

/**
 * Represents a cube containing 16x16x16 Chunks (256x256x256 Blocks)
 */
public abstract class Region extends Cube implements AreaChunkAccess, Iterable<Chunk>  {

	/**
	 * Stores the size of the amount of chunks in this Region
	 */
	public static final BitSize CHUNKS = new BitSize(4);

	/**
	 * Stores the size of the amount of blocks in this Region
	 */
	public static final BitSize BLOCKS = new BitSize(CHUNKS, Chunk.BLOCKS);

	private final int blockX;
	private final int blockY;
	private final int blockZ;
	private final int chunkX;
	private final int chunkY;
	private final int chunkZ;

	public Region(World world, float x, float y, float z) {
		super(new Point(world, x, y, z), BLOCKS.SIZE);
		this.blockX = super.getX() << BLOCKS.BITS;
		this.blockY = super.getY() << BLOCKS.BITS;
		this.blockZ = super.getZ() << BLOCKS.BITS;
		this.chunkX = super.getX() << CHUNKS.BITS;
		this.chunkY = super.getY() << CHUNKS.BITS;
		this.chunkZ = super.getZ() << CHUNKS.BITS;
	}

	/**
	 * Gets the x-coordinate of this region as a Block coordinate
	 * @return the x-coordinate of the first block in this region
	 */
	public int getBlockX() {
		return this.blockX;
	}

	/**
	 * Gets the y-coordinate of this region as a Block coordinate
	 * @return the y-coordinate of the first block in this region
	 */
	public int getBlockY() {
		return this.blockY;
	}

	/**
	 * Gets the z-coordinate of this region as a Block coordinate
	 * @return the z-coordinate of the first block in this region
	 */
	public int getBlockZ() {
		return this.blockZ;
	}

	/**
	 * Gets the x-coordinate of this region as a Chunk coordinate
	 * @return the x-coordinate of the first chunk in this region
	 */
	public int getChunkX() {
		return this.chunkX;
	}

	/**
	 * Gets the y-coordinate of this region as a Chunk coordinate
	 * @return the y-coordinate of the first chunk in this region
	 */
	public int getChunkY() {
		return this.chunkY;
	}

	/**
	 * Gets the z-coordinate of this region as a Chunk coordinate
	 * @return the z-coordinate of the first chunk in this region
	 */
	public int getChunkZ() {
		return this.chunkZ;
	}

	/**
	 * Gets the Block x-coordinate in the world
	 * @param x-coordinate within this Region
	 * @return x-coordinate within the World
	 */
	public int getBlockX(int x) {
		return this.blockX + x & BLOCKS.MASK;
	}

	/**
	 * Gets the Block y-coordinate in the world
	 * @param y-coordinate within this Region
	 * @return y-coordinate within the World
	 */
	public int getBlockY(int y) {
		return this.blockY + y & BLOCKS.MASK;
	}

	/**
	 * Gets the Block z-coordinate in the world
	 * @param z-coordinate within this Region
	 * @return z-coordinate within the World
	 */
	public int getBlockZ(int z) {
		return this.blockZ + z & BLOCKS.MASK;
	}

	/**
	 * Gets the Chunk x-coordinate in the world
	 * @param x-coordinate within this Region
	 * @return x-coordinate within the World
	 */
	public int getChunkX(int x) {
		return this.chunkX + x & CHUNKS.MASK;
	}

	/**
	 * Gets the Chunk y-coordinate in the world
	 * @param y-coordinate within this Region
	 * @return y-coordinate within the World
	 */
	public int getChunkY(int y) {
		return this.chunkY + y & CHUNKS.MASK;
	}

	/**
	 * Gets the Chunk z-coordinate in the world
	 * @param z-coordinate within this Region
	 * @return z-coordinate within the World
	 */
	public int getChunkZ(int z) {
		return this.chunkZ + z & CHUNKS.MASK;
	}

	@Override
	public boolean containsBlock(int x, int y, int z) {
		return x >> BLOCKS.BITS == this.getX() && y >> BLOCKS.BITS == this.getY() && z >> BLOCKS.BITS == this.getZ();
	}

	@Override
	public boolean containsChunk(int x, int y, int z) {
		return x >> CHUNKS.BITS == this.getX() && y >> CHUNKS.BITS == this.getY() && z >> CHUNKS.BITS == this.getZ();
	}

	/**
	 * Queues all chunks for saving at the next available opportunity.
	 */
	@DelayedWrite
	public abstract void save();

	/**
	 * Performs the nessecary tasks to unload this region from the world, and
	 * all associated chunks.
	 * @param save whether to save the region and associated data.
	 */
	@DelayedWrite
	public abstract void unload(boolean save);

	/**
	 * Gets all entities with the specified type.
	 * @param type The {@link Class} for the type.
	 * @param type The type of entity.
	 * @return A set of entities with the specified type.
	 */
	@SnapshotRead
	public abstract Set<Entity> getAll(Class<? extends Controller> type);

	/**
	 * Gets all entities.
	 * @return A collection of entities.
	 */
	@SnapshotRead
	public abstract Set<Entity> getAll();

	/**
	 * Gets an entity by its id.
	 * @param id The id.
	 * @return The entity, or {@code null} if it could not be found.
	 */
	@SnapshotRead
	public abstract Entity getEntity(int id);

	@LiveRead
	public abstract Set<Player> getPlayers();

	/**
	 * Gets the TaskManager associated with this region
	 */
	public abstract TaskManager getTaskManager();
	
	@Override
	public Iterator<Chunk> iterator() {
		return new ChunkIterator();
	}
	
	private class ChunkIterator implements Iterator<Chunk> {
		private Chunk next;
		public ChunkIterator() {
			for (int dx = 0; dx < CHUNKS.SIZE; dx++) {
				for (int dy = 0; dy < CHUNKS.SIZE; dy++) {
					for (int dz = 0; dz < CHUNKS.SIZE; dz++) {
						next = getChunk(dx, dy, dz, LoadOption.NO_LOAD);
						if (next != null) {
							break;
						}
					}
				}
			}
		}
		
		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Chunk next() {
			Chunk current = next;
			next = null;
			for (int dx = current.getX(); dx < CHUNKS.SIZE; dx++) {
				for (int dy = current.getY(); dy < CHUNKS.SIZE; dy++) {
					for (int dz = current.getZ(); dz < CHUNKS.SIZE; dz++) {
						next = getChunk(dx, dy, dz, LoadOption.NO_LOAD);
						if (next != null) {
							break;
						}
					}
				}
			}
			return current;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Operation not supported");
		}
		
	}
}
