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
public class LocalRegionChunkCuboidBlockMaterialBufferWrapper extends ImmutableCuboidBlockMaterialBuffer {
	
	private final ThreadLocal<LastBufferEntry> lastBufferCache = new ThreadLocal<LastBufferEntry>() {
		@Override
		public LastBufferEntry initialValue() {
			return new LastBufferEntry(LocalRegionChunkCuboidBlockMaterialBufferWrapper.this);
		}
	};
	
	private static final int SINGLE = Region.BLOCKS.SIZE;
	private static final int TRIPLE = SINGLE * 3;
	
	private final Region r;
	private final LoadOption loadOpt;
	private final BlockMaterial nullMaterial;
	
	protected final int cTx;
	protected final int cTy;
	protected final int cTz;
	protected final int cSx;
	protected final int cSy;
	protected final int cSz;
	
	private final ImmutableCuboidBlockMaterialBuffer[][][] cache;

	public LocalRegionChunkCuboidBlockMaterialBufferWrapper(Region r, LoadOption loadOpt, BlockMaterial nullMaterial) {
		this(r.getBlockX() - SINGLE, r.getBlockY() - SINGLE, r.getBlockZ() - SINGLE, TRIPLE, TRIPLE, TRIPLE, r, loadOpt, nullMaterial);
	}
	
	private LocalRegionChunkCuboidBlockMaterialBufferWrapper(int bx, int by, int bz, int sx, int sy, int sz, Region r, LoadOption loadOpt, BlockMaterial nullMaterial) {
		super(bx, by, bz, sx, sy, sz, null, null);
		this.cTx = (bx + sx) >> Chunk.BLOCKS.BITS;
		this.cTy = (by + sy) >> Chunk.BLOCKS.BITS;
		this.cTz = (bz + sz) >> Chunk.BLOCKS.BITS;
		this.cSx = sx >> Chunk.BLOCKS.BITS;
		this.cSy = sy >> Chunk.BLOCKS.BITS;
		this.cSz = sz >> Chunk.BLOCKS.BITS;
		cache = new ImmutableCuboidBlockMaterialBuffer[cSx][cSy][cSz];
		this.r = r;
		this.loadOpt = loadOpt;
		this.nullMaterial = nullMaterial;
	}

	/**
	 * Gets the sub-buffer corresponding to the given block location.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private ImmutableCuboidBlockMaterialBuffer getBlockMaterialBuffer(int x, int y, int z) {
		int cx = (x - baseX) >> Chunk.BLOCKS.BITS;
		int cy = (y - baseY) >> Chunk.BLOCKS.BITS;
		int cz = (z - baseZ) >> Chunk.BLOCKS.BITS;
		if (cx < 0 || cy < 0 || cz < 0 || cx >= cSx || cy >= cSy || cz >= cSz) {
			throw new IllegalArgumentException("Chunk Coordinate (" + cx + ", " + cy + ", " + cz + ") is outside the buffer");	
		}
		ImmutableCuboidBlockMaterialBuffer o = cache[cx][cy][cz];
		if (o != null) {
			return o;
		}
		o = getBlockMaterialBufferRaw(x, y, z);
		if (o == null) {
			throw new IllegalArgumentException("Unable to get sub-buffer for block " + x + ", " + y + ", " + z);
		}
		cache[cx][cy][cz] = o;
		return o;
	}
	
	/**
	 * Clears the cache containing sub-buffers.
	 */
	public void clear() {
		for (int x = 0; x < cSx; x++) {
			for (int y = 0; y < cSy; y++) {
				for (int z = 0; z < cSz; z++) {
					cache[x][y][z] = null;
				}
			}
		}
	}

	private ImmutableCuboidBlockMaterialBuffer getBlockMaterialBufferRaw(int x, int y, int z) {
		if (x < baseX || x >= topX || y < baseY || y >= topY || z < baseZ || z >= topZ) {
			throw new IllegalArgumentException("Coordinate (" + x + ", " + y + ", " + z + ") is outside the buffer");
		}
		int cx = (x - baseX - Region.BLOCKS.SIZE) >> Chunk.BLOCKS.BITS;
		int cy = (y - baseY - Region.BLOCKS.SIZE) >> Chunk.BLOCKS.BITS;
		int cz = (z - baseZ - Region.BLOCKS.SIZE) >> Chunk.BLOCKS.BITS;
		Chunk c = r.getLocalChunk(cx, cy, cz, loadOpt);
		if (c == null) {
			cx = x >> Chunk.BLOCKS.BITS;
			cy = y >> Chunk.BLOCKS.BITS;
			cz = z >> Chunk.BLOCKS.BITS;
			int bx = cx << Chunk.BLOCKS.BITS;
			int by = cy << Chunk.BLOCKS.BITS;
			int bz = cz << Chunk.BLOCKS.BITS;
			return new UniformImmutableCuboidBlockMaterialBuffer(bx, by, bz, nullMaterial);
		}
		return c.getCuboid(false);
	}
	
	@Override
	public BlockMaterial get(int x, int y, int z) {
		return lastBufferCache.get().get(x, y, z).get(x, y, z);
	}

	public short getId(int x, int y, int z) {
		return lastBufferCache.get().get(x, y, z).getId(x, y, z);
	}
	
	public short getData(int x, int y, int z) {
		return lastBufferCache.get().get(x, y, z).getData(x, y, z);
	}

	public short[] getRawId() {
		throw new UnsupportedOperationException("Buffer is a buffer wrapper, there is no id array");
	}

	public short[] getRawData() {
		throw new UnsupportedOperationException("Buffer is a buffer wrapper, there is no data array");
	}
	
	private static class LastBufferEntry {	
		private final LocalRegionChunkCuboidBlockMaterialBufferWrapper parent;
		
		private int x;
		private int y;
		private int z;
		private ImmutableCuboidBlockMaterialBuffer buffer;
		
		public LastBufferEntry(LocalRegionChunkCuboidBlockMaterialBufferWrapper parent) {
			this.parent = parent;
		}
		
		public ImmutableCuboidBlockMaterialBuffer get(int x, int y, int z) {
			if ((!chunkMatch(x, this.x)) || (!chunkMatch(y, this.y)) || (!chunkMatch(z, this.z))){
				this.x = x;
				this.y = y;
				this.z = z;
				buffer = parent.getBlockMaterialBuffer(x, y, z);
			}
			return buffer;
		}
		
		private static boolean chunkMatch(int a, int b) {
			return ((a ^ b) & (~Chunk.BLOCKS.MASK)) == 0;
		}
		
	}
	
}
