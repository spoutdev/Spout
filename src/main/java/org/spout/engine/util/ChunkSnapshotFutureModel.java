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

import java.util.concurrent.Future;

import org.spout.api.Spout;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.ChunkSnapshot.EntityType;
import org.spout.api.geo.cuboid.ChunkSnapshot.ExtraData;
import org.spout.api.geo.cuboid.ChunkSnapshot.SnapshotType;
import org.spout.engine.util.thread.lock.SpoutSnapshotLock;
import org.spout.engine.world.SpoutChunk;

//just need to,bottom,east,west,south,north, not diagonal neigbour it's 8 snapshot useless
/**
 * Stores 9 chunk snapshots (1 middle chunk and 8 neighbours) for quick access
 */
public class ChunkSnapshotFutureModel{
	private final World world;
	private final int cx, cy, cz;
	@SuppressWarnings("unchecked")
	private Future<ChunkSnapshot>[][][] chunks = new Future[3][3][3];

	public ChunkSnapshotFutureModel(World world,int cx, int cy, int cz) {
		this.world = world;
		this.cx = cx;
		this.cy = cy;
		this.cz = cz;
	}

	public boolean isDone(){
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				for (int z = 0; z < 3; z++) {
					if(chunks[x][y][z]!=null && !chunks[x][y][z].isDone())
						return false;
				}
			}
		}
		return true;
	}
	
	public ChunkSnapshotModel get(){
		if(!isDone())
			return null;
		return new ChunkSnapshotModel(cx, cy, cz,chunks);
	}

	public ChunkSnapshotFutureModel load() {
		SpoutSnapshotLock lock = (SpoutSnapshotLock) Spout.getEngine().getScheduler().getSnapshotLock();
		lock.coreReadLock("Load snapshots");
		try {
			//center
			//this.chunks[1][1][1] = ((SpoutChunk)this.world.getChunk(cx, cy, cz)).getFutureSnapshot(SnapshotType.BOTH,EntityType.NO_ENTITIES,ExtraData.NO_EXTRA_DATA, true);
			this.chunks[1][1][1] = this.world.getChunk(cx, cy, cz).getFutureSnapshot();

			//Top & Bottom
			this.chunks[1][2][1] = this.world.getChunk(cx, cy + 1, cz).getFutureSnapshot();
			this.chunks[1][0][1] = this.world.getChunk(cx, cy - 1, cz).getFutureSnapshot();

			//East & west
			this.chunks[2][1][1] = this.world.getChunk(cx + 1, cy, cz).getFutureSnapshot();
			this.chunks[0][1][1] = this.world.getChunk(cx - 1, cy, cz).getFutureSnapshot();

			//North & South
			this.chunks[1][1][2] = this.world.getChunk(cx, cy, cz + 1).getFutureSnapshot();
			this.chunks[1][1][0] = this.world.getChunk(cx, cy, cz - 1).getFutureSnapshot();
		} finally {
			lock.coreReadUnlock("Load snapshots");
		}

		return this;
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
	public Future<ChunkSnapshot> getChunk(int cx, int cy, int cz) {
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
	public Future<ChunkSnapshot> getChunkFromBlock(int bx, int by, int bz) {
		return getChunk(bx >> Chunk.BLOCKS.BITS, by >> Chunk.BLOCKS.BITS, bz >> Chunk.BLOCKS.BITS);
	}
}
