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

import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;

/**
 * Stores 9 chunks (1 middle chunk and 8 neighbours) for quick access
 */
public class ChunkModel {
	private final SpoutWorld world;
	private int cx, cy, cz;
	private SpoutChunk[][][] chunks = new SpoutChunk[3][3][3];
	private boolean[][][] loaded = new boolean[3][3][3];
	private SpoutChunk center;
	private SpoutRegion centerRegion;
	private LoadOption loadOption;

	public ChunkModel(SpoutWorld world) {
		this.world = world;
	}

	public ChunkModel load(int cx, int cy, int cz, LoadOption loadOpt) {
		this.cx = cx;
		this.cy = cy;
		this.cz = cz;
		this.loadOption = loadOpt;
		this.center = this.world.getChunk(cx, cy, cz, loadOpt);
		this.centerRegion = this.center.getRegion();
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				for (int z = 0; z < 3; z++) {
					this.loaded[x][y][z] = false;
				}
			}
		}
		// set center
		this.loaded[1][1][1] = true;
		this.chunks[1][1][1] = this.center;
		return this;
	}

	/**
	 * Gets if the center chunk of this model was successfully loaded
	 */
	public boolean isLoaded() {
		return this.center != null && this.center.isLoaded();
	}

	/**
	 * Gets if the center chunk of this model was successfully loaded and has been populated
	 */
	public boolean isLoadedAndPopulated() {
		return this.isLoaded() && this.center.isPopulated();
	}

	/**
	 * Gets the current center chunk of this model
	 * @return
	 */
	public SpoutChunk getCenter() {
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
		this.centerRegion = null;
	}

	/**
	 * Gets the chunk at world chunk coordinates<br>
	 * Note: Coordinates must be within this model, or index out of bounds will be thrown.
	 * @param cx coordinate of the chunk
	 * @param cy coordinate of the chunk
	 * @param cz coordinate of the chunk
	 * @return The chunk, or null if not available
	 */
	public SpoutChunk getChunk(int cx, int cy, int cz) {
		int dx = cx - this.cx + 1;
		int dy = cy - this.cy + 1;
		int dz = cz - this.cz + 1;
		if (this.loaded[dx][dy][dz]) {
			return this.chunks[dx][dy][dz];
		} else {
			// Load chunk using options
			this.loaded[dx][dy][dz] = true;
			if (this.centerRegion.containsChunk(cx, cy, cz)) {
				return (this.chunks[dx][dy][dz] = this.centerRegion.getChunk(cx, cy, cz, this.loadOption));
			} else {
				return (this.chunks[dx][dy][dz] = this.world.getChunk(cx, cy, cz, this.loadOption));
			}
		}
	}

	/**
	 * Gets the chunk at world block coordinates<br>
	 * Note: Coordinates must be within this model, or index out of bounds will be thrown.
	 * @param bx coordinate of the block
	 * @param by coordinate of the block
	 * @param bz coordinate of the block
	 * @return The chunk, or null if not available
	 */
	public SpoutChunk getChunkFromBlock(int bx, int by, int bz) {
		return getChunk(bx >> Chunk.BLOCKS.BITS, by >> Chunk.BLOCKS.BITS, bz >> Chunk.BLOCKS.BITS);
	}
}
