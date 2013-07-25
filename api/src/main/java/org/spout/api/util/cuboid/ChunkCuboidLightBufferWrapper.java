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
package org.spout.api.util.cuboid;

import org.spout.api.geo.cuboid.Chunk;

/**
 * This class implements a Cuboid buffer wrapper.  Each sub-buffer must be exactly one chunk in size.
 */
public abstract class ChunkCuboidLightBufferWrapper<T extends CuboidLightBuffer> extends CuboidLightBuffer {
	private static final CuboidLightBuffer NULL_BUFFER = new NullCuboidLightBuffer();
	protected final int cTx;
	protected final int cTy;
	protected final int cTz;
	protected final int cSx;
	protected final int cSy;
	protected final int cSz;
	private final CuboidLightBuffer[][][] cache;

	protected ChunkCuboidLightBufferWrapper(int bx, int by, int bz, int sx, int sy, int sz, short id) {
		super(null, id, bx, by, bz, sx, sy, sz);
		this.cTx = (bx + sx) >> Chunk.BLOCKS.BITS;
		this.cTy = (by + sy) >> Chunk.BLOCKS.BITS;
		this.cTz = (bz + sz) >> Chunk.BLOCKS.BITS;
		this.cSx = sx >> Chunk.BLOCKS.BITS;
		this.cSy = sy >> Chunk.BLOCKS.BITS;
		this.cSz = sz >> Chunk.BLOCKS.BITS;
		cache = new CuboidLightBuffer[cSx][cSy][cSz];
	}

	/**
	 * Gets the sub-buffer corresponding to the given block location.
	 */
	public T getLightBuffer(int x, int y, int z) {
		return getLightBuffer(x, y, z, false);
	}

	/**
	 * Gets the sub-buffer corresponding to the given block location.
	 */
	@SuppressWarnings ("unchecked")
	public T getLightBuffer(int x, int y, int z, boolean allowNull) {
		int cx = (x - baseX) >> Chunk.BLOCKS.BITS;
		int cy = (y - baseY) >> Chunk.BLOCKS.BITS;
		int cz = (z - baseZ) >> Chunk.BLOCKS.BITS;
		if (cx < 0 || cy < 0 || cz < 0 || cx >= cSx || cy >= cSy || cz >= cSz) {
			throw new IllegalArgumentException("Coordinate (" + x + ", " + y + ", " + z + ") is outside the buffer");
		}
		CuboidLightBuffer o = cache[cx][cy][cz];
		if (o != null) {
			if (o == NULL_BUFFER) {
				return null;
			} else {
				return (T) o;
			}
		}
		o = getLightBufferRaw(x, y, z, allowNull);
		if (o == null) {
			cache[cx][cy][cz] = NULL_BUFFER;
		} else {
			cache[cx][cy][cz] = o;
		}
		return (T) o;
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

	protected abstract T getLightBufferRaw(int x, int y, int z, boolean allowNull);

	@Override
	public CuboidLightBuffer copy() {
		throw new UnsupportedOperationException("Light buffer is a wrapper");
	}

	@Override
	public byte[] serialize() {
		throw new UnsupportedOperationException("Light buffer is a wrapper");
	}

	@Override
	public void copyElement(int thisIndex, int sourceIndex, int runLength) {
		throw new UnsupportedOperationException("Light buffer is a wrapper");
	}

	@Override
	public void setSource(CuboidBuffer source) {
		throw new UnsupportedOperationException("Light buffer is a wrapper");
	}

	private static class NullCuboidLightBuffer extends CuboidLightBuffer {
		protected NullCuboidLightBuffer() {
			super(null, 0, 0, 0, 0, 0, 0, 0);
		}

		@Override
		public CuboidLightBuffer copy() {
			throw new UnsupportedOperationException("Buffer is the null light buffer");
		}

		@Override
		public byte[] serialize() {
			throw new UnsupportedOperationException("Buffer is the null light buffer");
		}

		@Override
		public void copyElement(int thisIndex, int sourceIndex, int runLength) {
			throw new UnsupportedOperationException("Buffer is the null light buffer");
		}

		@Override
		public void setSource(CuboidBuffer source) {
			throw new UnsupportedOperationException("Buffer is the null light buffer");
		}
	}
}
