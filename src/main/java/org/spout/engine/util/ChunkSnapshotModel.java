/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.util;

import org.spout.api.Spout;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.engine.util.thread.lock.SpoutSnapshotLock;

/**
 * Stores 9 chunk snapshots (1 middle chunk and 8 neighbours) for quick access
 */
public class ChunkSnapshotModel {
	private final World world;
	private int cx, cy, cz;
	private ChunkSnapshot[][][] chunks = new ChunkSnapshot[3][3][3];
	private ChunkSnapshot center;

	public ChunkSnapshotModel(World world) {
		this.world = world;
	}

	public ChunkSnapshotModel load(int cx, int cy, int cz) {
		this.cx = cx;
		this.cy = cy;
		this.cz = cz;

		SpoutSnapshotLock lock = (SpoutSnapshotLock) Spout.getEngine().getScheduler().getSnapshotLock();
		lock.coreReadLock("Load snapshots");
		try {
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					for (int z = 0; z < 3; z++) {
						this.chunks[x][y][z] = this.world.getChunk(cx + x - 1, cy + y - 1, cz + z - 1).getSnapshot();
					}
				}
			}
		} finally {
			lock.coreReadUnlock("Load snapshots");
		}

		this.center = this.chunks[1][1][1];
		return this;
	}

	/**
	 * Gets the current center chunk of this model
	 * 
	 * @return
	 */
	public ChunkSnapshot getCenter() {
		return this.center;
	}

	/**
	 * Clears all references to live chunks and regions
	 */
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
	 * Gets the chunk at world chunk coordinates<br>
	 * Note: Coordinates must be within this model, or index out of bounds will
	 * be thrown.
	 * 
	 * @param cx
	 *            coordinate of the chunk
	 * @param cy
	 *            coordinate of the chunk
	 * @param cz
	 *            coordinate of the chunk
	 * @return The chunk, or null if not available
	 */
	public ChunkSnapshot getChunk(int cx, int cy, int cz) {
		return this.chunks[cx - this.cx + 1][cy - this.cy + 1][cz - this.cz + 1];
	}

	/**
	 * Gets the chunk at world block coordinates<br>
	 * Note: Coordinates must be within this model, or index out of bounds will
	 * be thrown.
	 * 
	 * @param bx
	 *            coordinate of the block
	 * @param by
	 *            coordinate of the block
	 * @param bz
	 *            coordinate of the block
	 * @return The chunk, or null if not available
	 */
	public ChunkSnapshot getChunkFromBlock(int bx, int by, int bz) {
		return getChunk(bx >> Chunk.BLOCKS.BITS, by >> Chunk.BLOCKS.BITS, bz >> Chunk.BLOCKS.BITS);
	}
}
