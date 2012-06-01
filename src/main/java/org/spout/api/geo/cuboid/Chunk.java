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

import java.io.Serializable;
import java.util.Set;

import org.spout.api.entity.Entity;
import org.spout.api.generator.biome.Biome;
import org.spout.api.geo.AreaBlockAccess;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.map.DefaultedMap;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;

/**
 * Represents a cube containing 16x16x16 Blocks
 */
public abstract class Chunk extends Cube implements AreaBlockAccess {

	/**
	 * Internal size of a side of a chunk
	 */
	public final static int CHUNK_SIZE = 16;

	/**
	 * Internal size of an entire chunk's contents
	 */
	public static final int CHUNK_VOLUME = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;
	/**
	 * Number of bits on the side of a chunk
	 */
	public final static int CHUNK_SIZE_BITS = 4;
	/**
	 * Mask to convert a block integer coordinate into the chunk's base
	 */
	public final static int BASE_MASK = CHUNK_SIZE - 1;
	/**
	 * Mask to convert a block integer coordinate into the point base
	 */
	public final static int POINT_BASE_MASK = -CHUNK_SIZE;
	
	private final int blockX;
	private final int blockY;
	private final int blockZ;
	
	public Chunk(World world, float x, float y, float z) {
		super(new Point(world, x, y, z), CHUNK_SIZE, true);
		blockX = super.getX() << CHUNK_SIZE_BITS;
		blockY = super.getY() << CHUNK_SIZE_BITS;
		blockZ = super.getZ() << CHUNK_SIZE_BITS;
	}

	/**
	 * Performs the necessary tasks to unload this chunk from the world.
	 *
	 * @param save whether the chunk data should be saved.
	 */
	public abstract void unload(boolean save);

	/**
	 * Performs the necessary tasks to save this chunk.
	 */
	public abstract void save();

	/**
	 * Gets a snapshot of the data for the chunk.
	 *
	 * This process may result in tearing if called during potential updates
	 *
	 * @return the snapshot
	 */
	@LiveRead
	public abstract ChunkSnapshot getSnapshot();

	/**
	 * Gets a snapshot of the data for the chunk.
	 *
	 * This process may result in tearing if called during potential updates
	 *
	 * @param entities whether to include entity data in the snapshot
	 * @return the snapshot
	 */
	@LiveRead
	public abstract ChunkSnapshot getSnapshot(boolean entities);

	/**
	 * Refresh the distance between a player and the chunk, and adds the player
	 * as an observer if not previously observing.
	 *
	 * @param player the player
	 * @return false if the player was already observing the chunk
	 */
	@DelayedWrite
	public abstract boolean refreshObserver(Entity player);

	/**
	 * De-register a player as observing the chunk.
	 *
	 * @param player the player
	 * @return true if the player was observing the chunk
	 */
	@DelayedWrite
	public abstract boolean removeObserver(Entity player);

	/**
	 * Gets the region that this chunk is located in
	 *
	 * @return region
	 */
	public abstract Region getRegion();

	/**
	 * Tests if the chunk is currently loaded
	 *
	 * Chunks may be unloaded at the end of each tick
	 */
	public abstract boolean isLoaded();

	/**
	 * Gets the biome type at the coordinates,
	 * if the world generator used uses biomes.
	 *
	 * @return The biome type at the location
	 */
	public abstract Biome getBiomeType(int x, int y, int z);

	/**
	 * Populates the chunk with all the Populators attached to the
	 * WorldGenerator of its world.
	 */
	public abstract void populate();

	/**
	 * Initializes the lighting in this chunk
	 */
	public abstract void initLighting();

	/**
	 * Gets whether this chunk is ready to be sent to a player<br>
	 * It will return False when sending it could cause incomplete chunk information
	 * @return True if it can be sent, False if not
	 */
	public abstract boolean canSend();

	/**
	 * Populates the chunk with all the Populators attached to the
	 * WorldGenerator of its world.
	 *
	 * @param force forces to populate the chunk even if it already has been
	 *            populated.
	 */
	public abstract void populate(boolean force);

	/**
	 * Gets if this chunk already has been populated.
	 *
	 * @return if the chunk is populated.
	 */
	public abstract boolean isPopulated();

	/**
	 * Gets the entities in the chunk at the last snapshot
	 *
	 * @return the entities
	 */
	@SnapshotRead
	public abstract Set<Entity> getEntities();

	/**
	 * Gets the entities currently in the chunk
	 *
	 * @return the entities
	 */
	@LiveRead
	public abstract Set<Entity> getLiveEntities();

	/**
	 * A thread-safe map of data attached to the chunk, which will be persisted between saves.
	 *
	 * Can be used to attach and store any persistent chunk related data.
	 *
	 * @return data map
	 */
	public abstract DefaultedMap<String, Serializable> getDataMap();

	/**
	 * Gets whether the given block coordinates is inside this chunk
	 */
	public boolean containsBlock(int x, int y, int z) {
		return x >> Chunk.CHUNK_SIZE_BITS == this.getX() && y >> Chunk.CHUNK_SIZE_BITS == this.getY() && z >> Chunk.CHUNK_SIZE_BITS == this.getZ();
	}

	/**
	 * Gets the x-coordinate of this chunk as a Block coordinate
	 * @return the x-coordinate of the first block in this chunk
	 */
	public int getBlockX() {
		return blockX;
	}

	/**
	 * Gets the y-coordinate of this chunk as a Block coordinate
	 * @return the y-coordinate of the first block in this chunk
	 */
	public int getBlockY() {
		return blockY;
	}

	/**
	 * Gets the z-coordinate of this chunk as a Block coordinate
	 * @return the z-coordinate of the first block in this chunk
	 */
	public int getBlockZ() {
		return blockZ;
	}

	/**
	 * Gets a chunk relative to this chunk
	 * @param offset of the chunk relative to this chunk
	 * @param load True to load the chunk if it is not yet loaded
	 * @return The Chunk, or null if not loaded and load is False
	 */
	public Chunk getRelative(Vector3 offset, boolean load) {
		return this.getWorld().getChunk(this.getX() + (int) offset.getX(), this.getY() + (int) offset.getY(), this.getZ() + (int) offset.getZ(), load);
	}

	/**
	 * Gets a chunk relative to this chunk, loads if needed
	 * @param offset of the chunk relative to this chunk
	 * @return The Chunk
	 */
	public Chunk getRelative(Vector3 offset) {
		return this.getRelative(offset, true);
	}

	/**
	 * Gets a chunk relative to this chunk
	 * @param offset of the chunk relative to this chunk
	 * @param load True to load the chunk if it is not yet loaded
	 * @return The Chunk, or null if not loaded and load is False
	 */
	public Chunk getRelative(BlockFace offset, boolean load) {
		return this.getRelative(offset.getOffset(), load);
	}

	/**
	 * Gets a chunk relative to this chunk, loads if needed
	 * @param offset of the chunk relative to this chunk
	 * @return The Chunk
	 */
	public Chunk getRelative(BlockFace offset) {
		return this.getRelative(offset.getOffset());
	}

	/**
	 * Converts a point in such a way that it points to the first block (the base block) of the chunk<br>
	 * This is similar to performing the following operation on the x, y and z coordinate:<br>
	 * - Convert to the chunk coordinate<br>
	 * - Multiply by chunk size
	 *
	 * @param p
	 * @return
	 */
	public static Point pointToBase(Point p) {
		return new Point(p.getWorld(), (int) p.getX() & POINT_BASE_MASK, (int) p.getY() & POINT_BASE_MASK, (int) p.getZ() & POINT_BASE_MASK);
	}
}
