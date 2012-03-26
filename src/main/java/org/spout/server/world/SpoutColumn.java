/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.server.world;

import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.material.BlockMaterial;

public class SpoutColumn {

	/**
	 * Internal size of a side of a column
	 */
	public final static int COLUMN_SIZE = Chunk.CHUNK_SIZE;

	/**
	 * Internal size of a side of a column
	 */
	public final static int BIT_MASK = COLUMN_SIZE - 1;

	/**
	 * Number of bits on the side of a column
	 */
	public final static int COLUMN_SIZE_BITS = Chunk.CHUNK_SIZE_BITS;

	private final World world;
	private final int x;
	private final int z;
	private final AtomicInteger activeChunks = new AtomicInteger(0);
	private final AtomicInteger[][] heightMap;

	public SpoutColumn(World world, int x, int z) {
		this(world, x, z, null);
	}

	public SpoutColumn(World world, int x, int z, int[][] initial) {
		this.world = world;
		this.x = x;
		this.z = z;
		this.heightMap = new AtomicInteger[COLUMN_SIZE][COLUMN_SIZE];
		for (int xx = 0; xx < COLUMN_SIZE; xx++) {
			for (int zz = 0; zz < COLUMN_SIZE; zz++) {
				int value = initial == null ? Integer.MIN_VALUE : initial[xx][zz];
				heightMap[xx][zz] = new AtomicInteger(value);
			}
		}

	}

	public void registerChunk() {
		activeChunks.incrementAndGet();
	}

	public void deregisterChunk() {
		if (activeChunks.decrementAndGet() == 0) {
			System.out.println("All chunks in column " + x + ", " + z + " are unloaded");
		}
	}

	public boolean activeChunks() {
		return activeChunks.get() > 0;
	}

	public int getSurfaceHeight(int x, int z) {
		AtomicInteger v = getAtomicInteger(x, z);
		return v.get();
	}
	
	public void notifyChunkAdded(Chunk c, int x, int z) {
		int y = c.getY() << Chunk.CHUNK_SIZE_BITS;
		
		AtomicInteger v = getAtomicInteger(x, z);
		int maxY = y + Chunk.CHUNK_SIZE - 1;
		
		if (maxY < v.get()) {
			return;
		}
		
		for (int yy = maxY; yy >= y; yy--) {
			if (!isAir(c, x, yy, z)) {
				notifyBlockChange(v, x, yy, z);
				return;
			}
		}
	}
	
	public void notifyBlockChange(int x, int y, int z) {
		//System.out.println("Notify block change:       " + x + ", " + y + ", " + z);
		AtomicInteger v = getAtomicInteger(x, z);
		notifyBlockChange(v, x, y, z);
		//System.out.println("Notify block change ended: " + x + ", " + y + ", " + z);
	}
	
	private void notifyBlockChange(AtomicInteger v, int x, int y, int z) {
		int value = v.get();
		if (y < value) {
			return;
		} else if (y == value) {
			falling(x, v, z);
		} else {
			if (!isAir(x, y, z)) {
				v.set(y);
				falling(x, v, z);
			}
		}
	}

	private void falling(int x, AtomicInteger v, int z) {
		while (true) {
			int value = v.get();
			if (!isAir(x, value, z)) {
				return;
			} else {
				v.compareAndSet(value, value - 1);
			}
		}
	}

	private boolean isAir(int x, int y, int z) {
		int xx = (this.x << COLUMN_SIZE_BITS) + (x & BIT_MASK);
		int yy = y;
		int zz = (this.z << COLUMN_SIZE_BITS) + (z & BIT_MASK);
		return isAir(world.getBlockMaterial(xx, yy, zz));
		
	}
	
	private boolean isAir(Chunk c, int x, int y, int z) {
		return isAir(c.getBlockMaterial(x, y, z));
	}
	
	private boolean isAir(BlockMaterial m) {
		if (m == null) {
			return false;
		} else {
			return m.getOpacity() == 0;
		}
	}

	private AtomicInteger getAtomicInteger(int x, int z) {
		return heightMap[x & BIT_MASK][z & BIT_MASK];
	}

}
