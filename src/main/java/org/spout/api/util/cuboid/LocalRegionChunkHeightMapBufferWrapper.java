/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.util.cuboid;

import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.BlockMaterial;

/**
 * This class implements a Cuboid buffer wrapper.  Each sub-buffer must be exactly one
 * chunk in size.
 */
public class LocalRegionChunkHeightMapBufferWrapper extends ImmutableHeightMapBuffer {
	
	private static final int SINGLE = Region.BLOCKS.SIZE;
	private static final int TRIPLE = SINGLE * 3;
	
	private final Region r;
	private final LoadOption loadOpt;
	
	protected final int cTx;
	protected final int cTz;
	protected final int cSx;
	protected final int cSz;
	
	private final ImmutableHeightMapBuffer[][] cache;

	public LocalRegionChunkHeightMapBufferWrapper(Region r, LoadOption loadOpt) {
		this(r.getBlockX() - SINGLE, r.getBlockZ() - SINGLE, TRIPLE, TRIPLE, r, loadOpt);
	}
	
	private LocalRegionChunkHeightMapBufferWrapper(int bx, int bz, int sx, int sz, Region r, LoadOption loadOpt) {
		super(bx, bz, sx, sz, (int[]) null);
		this.cTx = (bx + sx) >> Chunk.BLOCKS.BITS;
		this.cTz = (bz + sz) >> Chunk.BLOCKS.BITS;
		this.cSx = sx >> Chunk.BLOCKS.BITS;
		this.cSz = sz >> Chunk.BLOCKS.BITS;
		cache = new ImmutableHeightMapBuffer[cSx][cSz];
		this.r = r;
		this.loadOpt = loadOpt;
	}

	/**
	 * Gets the sub-buffer corresponding to the given block location.
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	private ImmutableHeightMapBuffer getHeightMapBuffer(int x, int z) {
		int cx = (x - baseX) >> Chunk.BLOCKS.BITS;
		int cz = (z - baseZ) >> Chunk.BLOCKS.BITS;
		if (cx < 0 || cz < 0 || cx >= cSx || cz >= cSz) {
			throw new IllegalArgumentException("Chunk Coordinate (" + cx + ", " + cz + ") is outside the buffer");	
		}
		ImmutableHeightMapBuffer o = cache[cx][cz];
		if (o != null) {
			return o;
		}
		o = getHeightMapBufferRaw(x, z);
		if (o == null) {
			throw new IllegalArgumentException("Unable to get sub-buffer for block " + x + ", " + z);
		}
		cache[cx][cz] = o;
		return o;
	}
	
	/**
	 * Clears the cache containing sub-buffers.
	 */
	public void clear() {
		for (int x = 0; x < cSx; x++) {
			for (int z = 0; z < cSz; z++) {
				cache[x][z] = null;
			}
		}
	}

	private ImmutableHeightMapBuffer getHeightMapBufferRaw(int x, int z) {
		if (x < baseX || x >= topX || z < baseZ || z >= topZ) {
			throw new IllegalArgumentException("Coordinate (" + x + ", " + z + ") is outside the buffer");
		}
		int cx = (x - baseX - Region.BLOCKS.SIZE) >> Chunk.BLOCKS.BITS;
		int cz = (z - baseZ - Region.BLOCKS.SIZE) >> Chunk.BLOCKS.BITS;
		
		return r.getLocalHeightMap(cx, cz, loadOpt);
	}
	
	@Override
	public int get(int x, int z) {
		return getHeightMapBuffer(x, z).get(x, z);
	}
}
