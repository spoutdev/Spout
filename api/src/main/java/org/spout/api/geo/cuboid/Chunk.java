/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.geo.cuboid;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Future;

import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.geo.AreaBlockAccess;
import org.spout.api.geo.AreaPhysicsAccess;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.ChunkSnapshot.EntityType;
import org.spout.api.geo.cuboid.ChunkSnapshot.ExtraData;
import org.spout.api.geo.cuboid.ChunkSnapshot.SnapshotType;
import org.spout.api.geo.discrete.Point;
import org.spout.api.lighting.LightingManager;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.BitSize;
import org.spout.math.vector.Vector3;
import org.spout.api.util.cuboid.CuboidLightBuffer;
import org.spout.api.util.thread.annotation.DelayedWrite;
import org.spout.api.util.thread.annotation.LiveRead;
import org.spout.api.util.thread.annotation.SnapshotRead;

/**
 * Represents a cube containing 16x16x16 Blocks
 */
public abstract class Chunk extends Cube implements AreaBlockAccess, AreaPhysicsAccess {
	/**
	 * Stores the size of the amount of blocks in this Chunk
	 */
	public static final BitSize BLOCKS = new BitSize(4);
	/**
	 * Mask to convert a block integer coordinate into the point base
	 */
	public final static int POINT_BASE_MASK = -BLOCKS.SIZE;
	private final int blockX;
	private final int blockY;
	private final int blockZ;

	public Chunk(World world, float x, float y, float z) {
		super(new Point(world, x, y, z), BLOCKS.SIZE);
		blockX = super.getX() << BLOCKS.BITS;
		blockY = super.getY() << BLOCKS.BITS;
		blockZ = super.getZ() << BLOCKS.BITS;
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
	 * Gets a snapshot of the data for the chunk. <br/><br/> This process may result in tearing if called during potential updates <br/><br/> This is the same as calling getSnapshot(BOTH, WEAK_ENTITIES,
	 * NO_EXTRA_DATA)
	 *
	 * @return the snapshot
	 */
	@LiveRead
	public abstract ChunkSnapshot getSnapshot();

	/**
	 * Gets a snapshot of the data for the chunk. <br/><br/> This process may result in tearing if called during potential updates <br/><br/>
	 *
	 * @param the type of basic snapshot information to be stored
	 * @param entities whether to include entity data in the snapshot
	 * @param the extra data, if any, to be stored
	 * @return the snapshot
	 */
	@LiveRead
	public abstract ChunkSnapshot getSnapshot(SnapshotType type, EntityType entities, ExtraData data);

	/**
	 * Fills the given block container with the block data for this chunk
	 */
	public abstract void fillBlockContainer(BlockContainer container);

	/**
	 * Fills the given block component container with the block components for this chunk
	 */
	public abstract void fillBlockComponentContainer(BlockComponentContainer container);

	/**
	 * Gets a snapshot of the data for the chunk.  The snapshot will be taken at a stable moment in the tick. <br/><br/> This is the same as calling getFutureSnapshot(BOTH, WEAK_ENTITIES, NO_EXTRA_DATA)
	 *
	 * @return the snapshot
	 */
	@LiveRead
	public abstract Future<ChunkSnapshot> getFutureSnapshot();

	/**
	 * Gets a snapshot of the data for the chunk.  The snapshot will be taken at a stable moment in the tick.
	 *
	 * @param the type of basic snapshot information to be stored
	 * @param entities whether to include entity data in the snapshot
	 * @param the extra data, if any, to be stored
	 * @return the snapshot
	 */
	@LiveRead
	public abstract Future<ChunkSnapshot> getFutureSnapshot(SnapshotType type, EntityType entites, ExtraData data);

	/**
	 * Refresh the distance between a player and the chunk, and adds the player as an observer if not previously observing.
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
	 * Populates the chunk with all the Populators attached to the WorldGenerator of its world.
	 */
	public abstract boolean populate();

	/**
	 * Populates the chunk with all the Populators attached to the WorldGenerator of its world.
	 *
	 * @param force forces to populate the chunk even if it already has been populated.
	 */
	public abstract boolean populate(boolean force);

	/**
	 * Populates the chunk with all the Populators attached to the WorldGenerator of its world.<br> <br> Warning: populating with force observer should not be called from within populators as it could
	 * lead to a population cascade
	 *
	 * @param sync queues the population to occur at a later time
	 * @param observe forces the chunk to be observed for population
	 */
	public abstract void populate(boolean sync, boolean observe);

	/**
	 * Populates the chunk with all the Populators attached to the WorldGenerator of its world.<br> <br> Warning: populating with force observer should not be called from within populators as it could
	 * lead to a population cascade
	 *
	 * @param sync queues the population to occur at a later time
	 * @param observe forces the chunk to be observed for population
	 * @param priority adds the chunk to the high priority queue
	 */
	public abstract void populate(boolean sync, boolean observe, boolean priority);

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
	public abstract List<Entity> getEntities();

	/**
	 * Gets the entities currently in the chunk
	 *
	 * @return the entities
	 */
	@LiveRead
	public abstract List<Entity> getLiveEntities();

	/**
	 * Gets the number of observers viewing this chunk. If the number of observing entities falls to zero, this chunk may be reaped at any time.
	 *
	 * @return number of observers
	 */
	@LiveRead
	public abstract int getNumObservers();

	/**
	 * Gets the observing players of this chunk (done based on the player's view distance).
	 *
	 * @return List containing the observing players
	 */
	public abstract Set<? extends Player> getObservingPlayers();

	/**
	 * Gets the observers of this chunk
	 *
	 * @return Set containing the observing players
	 */
	public abstract Set<? extends Entity> getObservers();

	/**
	 * Gets the lighting buffer associated with the given manager
	 *
	 * @param manager the corresponding lighting manager
	 */
	public abstract <T extends CuboidLightBuffer> T getLightBuffer(LightingManager<T> manager);

	@Override
	public boolean containsBlock(int x, int y, int z) {
		return x >> BLOCKS.BITS == this.getX() && y >> BLOCKS.BITS == this.getY() && z >> BLOCKS.BITS == this.getZ();
	}

	/**
	 * Gets the x-coordinate of this chunk as a Block coordinate
	 *
	 * @return the x-coordinate of the first block in this chunk
	 */
	public int getBlockX() {
		return blockX;
	}

	/**
	 * Gets the y-coordinate of this chunk as a Block coordinate
	 *
	 * @return the y-coordinate of the first block in this chunk
	 */
	public int getBlockY() {
		return blockY;
	}

	/**
	 * Gets the z-coordinate of this chunk as a Block coordinate
	 *
	 * @return the z-coordinate of the first block in this chunk
	 */
	public int getBlockZ() {
		return blockZ;
	}

	/**
	 * Gets the Block x-coordinate in the world
	 *
	 * @param x-coordinate within this Chunk
	 * @return x-coordinate within the World
	 */
	public int getBlockX(int x) {
		return this.blockX + (x & BLOCKS.MASK);
	}

	/**
	 * Gets the Block x-coordinate in the world
	 *
	 * @param y-coordinate within this Chunk
	 * @return y-coordinate within the World
	 */
	public int getBlockY(int y) {
		return this.blockY + (y & BLOCKS.MASK);
	}

	/**
	 * Gets the Block x-coordinate in the world
	 *
	 * @param z-coordinate within this Chunk
	 * @return z-coordinate within the World
	 */
	public int getBlockZ(int z) {
		return this.blockZ + (z & BLOCKS.MASK);
	}

	/**
	 * Gets a random Block x-coordinate using a Random
	 *
	 * @param random to use
	 * @return x-coordinate within the World in this Chunk
	 */
	public int getBlockX(Random random) {
		return this.blockX + random.nextInt(BLOCKS.SIZE);
	}

	/**
	 * Gets a random Block y-coordinate using a Random
	 *
	 * @param random to use
	 * @return y-coordinate within the World in this Chunk
	 */
	public int getBlockY(Random random) {
		return this.blockY + random.nextInt(BLOCKS.SIZE);
	}

	/**
	 * Gets a random Block z-coordinate using a Random
	 *
	 * @param random to use
	 * @return z-coordinate within the World in this Chunk
	 */
	public int getBlockZ(Random random) {
		return this.blockZ + random.nextInt(BLOCKS.SIZE);
	}

	/**
	 * Gets a chunk relative to this chunk
	 *
	 * @param offset of the chunk relative to this chunk
	 * @param opt True to load the chunk if it is not yet loaded
	 * @return The Chunk, or null if not loaded and load is False
	 */
	public Chunk getRelative(Vector3 offset, LoadOption opt) {
		return this.getWorld().getChunk(this.getX() + (int) offset.getX(), this.getY() + (int) offset.getY(), this.getZ() + (int) offset.getZ(), opt);
	}

	/**
	 * Gets a chunk relative to this chunk, loads if needed
	 *
	 * @param offset of the chunk relative to this chunk
	 * @return The Chunk
	 */
	public Chunk getRelative(Vector3 offset) {
		return this.getRelative(offset, LoadOption.LOAD_GEN);
	}

	/**
	 * Gets a chunk relative to this chunk
	 *
	 * @param offset of the chunk relative to this chunk
	 * @param opt True to load the chunk if it is not yet loaded
	 * @return The Chunk, or null if not loaded and load is False
	 */
	public Chunk getRelative(BlockFace offset, LoadOption opt) {
		return this.getRelative(offset.getOffset(), opt);
	}

	/**
	 * Gets a chunk relative to this chunk, loads if needed
	 *
	 * @param offset of the chunk relative to this chunk
	 * @return The Chunk
	 */
	public Chunk getRelative(BlockFace offset) {
		return this.getRelative(offset.getOffset());
	}

	/**
	 * Gets the generation index for this chunk.  Only chunks generated as part of the same bulk initialize have the same index.
	 *
	 * @return a unique generation id, or -1 if the chunk was loaded from disk
	 */
	public abstract int getGenerationIndex();

	/**
	 * Converts a point in such a way that it points to the first block (the base block) of the chunk<br> This is similar to performing the following operation on the x, y and z coordinate:<br> - Convert
	 * to the chunk coordinate<br> - Multiply by chunk size
	 */
	public static Point pointToBase(Point p) {
		return new Point(p.getWorld(), (int) p.getX() & POINT_BASE_MASK, (int) p.getY() & POINT_BASE_MASK, (int) p.getZ() & POINT_BASE_MASK);
	}
}
