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
package org.spout.engine.world;

import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.ChunkSnapshotGroup;
import org.spout.api.material.BlockMaterial;

//just need to,bottom,east,west,south,north, not diagonal neighbour it's 8 snapshot useless

/**
 * Stores 9 chunk snapshots (1 middle chunk and 8 neighbours) for quick access
 */
public class SpoutChunkSnapshotGroup implements ChunkSnapshotGroup, Comparable<SpoutChunkSnapshotGroup> {
	private static final AtomicInteger idCounter = new AtomicInteger(0);
	private final int cx, cy, cz;
	private final ChunkSnapshot[][][] chunks;
	private ChunkSnapshot center;
	private final boolean unload;
	private final int distance;
	private final int id;
	/**
	 * Time of the SpoutChunkSnapshotGroup creation To benchmark purpose
	 */
	private final long time;
	/**
	 * Indicates that the renderer has not received a model for this chunk yet
	 */
	private boolean first;
	private final SpoutWorld world;

	public SpoutChunkSnapshotGroup(SpoutWorld world, int cx, int cy, int cz, boolean unload, long time) {
		this(world, cx, cy, cz, unload, null, 0, false, time);
	}

	public SpoutChunkSnapshotGroup(SpoutWorld world, int cx, int cy, int cz, ChunkSnapshot[][][] chunks, int distance, boolean first, long time) {
		this(world, cx, cy, cz, false, chunks, distance, first, time);
	}

	private SpoutChunkSnapshotGroup(SpoutWorld world, int cx, int cy, int cz, boolean unload, ChunkSnapshot[][][] chunks, int distance, boolean first, long time) {
		this.world = world;
		this.cx = cx;
		this.cy = cy;
		this.cz = cz;
		this.chunks = chunks;
		this.center = chunks != null ? chunks[1][1][1] : null;
		this.unload = unload;
		this.distance = distance;
		this.id = idCounter.getAndIncrement();
		this.time = time;
		this.first = first;
	}

	@Override
	public int getX() {
		return cx;
	}

	@Override
	public int getY() {
		return cy;
	}

	@Override
	public int getZ() {
		return cz;
	}

	public int getDistance() {
		return distance;
	}

	/**
	 * Gets if the chunk was unloaded.  Unload models only indicate an unload occurred and contain no data.
	 */
	@Override
	public boolean isUnload() {
		return unload;
	}

	/**
	 * Gets the current center chunk of this model
	 */
	@Override
	public ChunkSnapshot getCenter() {
		return this.center;
	}

	/**
	 * Clears all references to live chunks and regions
	 */
	@Override
	public void cleanUp() {
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				for (int z = 0; z < 3; z++) {
					this.chunks[x][y][z] = null;
				}
			}
		}
		this.center = null;
	}

	/**
	 * Gets the chunk at world chunk coordinates<br> Note: Coordinates must be within this model, or index out of bounds will be thrown.
	 *
	 * @param cx coordinate of the chunk
	 * @param cy coordinate of the chunk
	 * @param cz coordinate of the chunk
	 * @return The chunk, or null if not available
	 */
	@Override
	public ChunkSnapshot getChunk(int cx, int cy, int cz) {
		return this.chunks[cx - this.cx + 1][cy - this.cy + 1][cz - this.cz + 1];
	}

	/**
	 * Gets the chunk at world block coordinates<br> Note: Coordinates must be within this model, or index out of bounds will be thrown.
	 *
	 * @param bx coordinate of the block
	 * @param by coordinate of the block
	 * @param bz coordinate of the block
	 * @return The chunk, or null if not available
	 */
	@Override
	public ChunkSnapshot getChunkFromBlock(int bx, int by, int bz) {
		return getChunk(bx >> Chunk.BLOCKS.BITS, by >> Chunk.BLOCKS.BITS, bz >> Chunk.BLOCKS.BITS);
	}

	@Override
	public BlockMaterial getBlock(int bx, int by, int bz) {
		return getChunkFromBlock(bx, by, bz).getBlockMaterial(bx, by, bz);
	}

	@Override
	public int compareTo(final SpoutChunkSnapshotGroup o) {
		int d1 = getDistance();
		int d2 = o.getDistance();

		if (d1 == d2) {
			return id - o.id;
		} else {
			return d1 - d2;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SpoutChunkSnapshotGroup other = (SpoutChunkSnapshotGroup) obj;
		if (this.cx != other.cx) {
			return false;
		}
		if (this.cy != other.cy) {
			return false;
		}
		if (this.cz != other.cz) {
			return false;
		}
		return true;
	}

	public boolean isFirst() {
		return first;
	}

	public long getTime() {
		return time;
	}

	public World getWorld() {
		return world;
	}
}

