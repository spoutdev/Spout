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
package org.spout.engine.world;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Spout;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;
import org.spout.api.material.block.BlockFaces;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.math.BitSize;
import org.spout.api.scheduler.TickStage;
import org.spout.engine.filesystem.WorldFiles;

public class SpoutColumn {
	/**
	 * Number of bits on the side of a column
	 */
	private static int FILE_VERSION = 3;
	/**
	 * Stores the size of the amount of blocks in this Column
	 */
	public static BitSize BLOCKS = Chunk.BLOCKS;

	private final SpoutWorld world;
	private final int x;
	private final int z;
	private final AtomicInteger activeChunks = new AtomicInteger(0);
	private final AtomicInteger[][] heightMap;
	private final AtomicInteger lowestY = new AtomicInteger();
	private final AtomicReference<int[][]> heights = new AtomicReference<int[][]>();
	private final AtomicBoolean dirty = new AtomicBoolean(false);
	private final AtomicBoolean dirtyArray[][];
	private final BlockMaterial[][] topmostBlocks;
	private final Thread worldThread;

	public SpoutColumn(SpoutWorld world, int x, int z) {
		this.world = world;
		this.x = x;
		this.z = z;
		this.heightMap = new AtomicInteger[BLOCKS.SIZE][BLOCKS.SIZE];
		this.dirtyArray = new AtomicBoolean[BLOCKS.SIZE][BLOCKS.SIZE];
		this.topmostBlocks = new BlockMaterial[BLOCKS.SIZE][BLOCKS.SIZE];
		this.worldThread = (Thread) (((SpoutWorld) world).getExecutor());

		for (int xx = 0; xx < BLOCKS.SIZE; xx++) {
			for (int zz = 0; zz < BLOCKS.SIZE; zz++) {
				heightMap[xx][zz] = new AtomicInteger(0);
				dirtyArray[xx][zz] = new AtomicBoolean(false);
			}
		}

		lowestY.set(Integer.MAX_VALUE);

		WorldFiles.readColumn(((SpoutWorld) world).getHeightMapInputStream(x, z), this, this.lowestY, topmostBlocks);
	}

	public void onFinalize() {
		TickStage.checkStage(TickStage.FINALIZE, worldThread);
		if (dirty.compareAndSet(true, false)) {
			int wx = (this.x << BLOCKS.BITS);
			int wz = (this.z << BLOCKS.BITS);
			for (int xx = 0; xx < BLOCKS.SIZE; xx++) {
				for (int zz = 0; zz < BLOCKS.SIZE; zz++) {
					if (getDirtyFlag(xx, zz).compareAndSet(true, false)) {
						int y = getAtomicInteger(xx, zz).get();
						int wxx = wx + xx;
						int wzz = wz + zz;
						Chunk c = world.getChunkFromBlock(wxx, y, wzz, LoadOption.LOAD_ONLY);
						BlockMaterial bm = null;
						if (c != null) {
							 bm = c.getBlockMaterial(wx + xx, y, wz + zz);
						}
						topmostBlocks[xx][zz] = bm;
					}
				}
			}
		}
	}

	public void registerChunk(int y) {
		boolean success = false;
		while (!success) {
			int oldLowestY = lowestY.get();
			if (y < oldLowestY) {
				success = lowestY.compareAndSet(oldLowestY, y);
			} else {
				success = true;
			}
		}
		activeChunks.incrementAndGet();
	}

	public void deregisterChunk(boolean save) {
		if (save) {
			TickStage.checkStage(TickStage.SNAPSHOT);
			if (activeChunks.decrementAndGet() == 0) {
				OutputStream out = ((SpoutWorld) world).getHeightMapOutputStream(x, z);
				try {
					WorldFiles.writeColumn(out, this, lowestY, topmostBlocks);
				} finally {
					try {
						out.close();
					} catch (IOException e) {
					}
				}
				((SpoutWorld) world).removeColumn(x, z, this);
			}
		} else {
			activeChunks.decrementAndGet();
		}
	}

	public boolean activeChunks() {
		return activeChunks.get() > 0;
	}

	public int getSurfaceHeight(int x, int z) {
		final int height = getAtomicInteger(x, z).get();
		if (height != Integer.MIN_VALUE) {
			// height known
			return height;
		}

		return getGeneratorHeight(x, z);

	}

	public BlockMaterial getTopmostBlock(int x, int z) {
		TickStage.checkStage(TickStage.SNAPSHOT | TickStage.PRESNAPSHOT);
		int cx = x & BLOCKS.MASK;
		int cz = z & BLOCKS.MASK;
		BlockMaterial m = this.topmostBlocks[cx][cz];
		return m;
	}

	private int getGeneratorHeight(int x, int z) {
		int[][] h = heights.get();
		if (h == null) {
			h = world.getGenerator().getSurfaceHeight(world, this.x, this.z);
			heights.set(h);
		}

		if (h == null) {
			return lowestY.get();
		}

		return h[x & BLOCKS.MASK][z & BLOCKS.MASK];
	}

	public void notifyChunkAdded(Chunk c, int x, int z) {
		int y = c.getBlockY();
		int maxY = y + Chunk.BLOCKS.SIZE - 1;
		AtomicInteger v = getAtomicInteger(x, z);

		if (maxY < v.get()) {
			return;
		}

		if (c instanceof FilteredChunk) {
			if (((FilteredChunk) c).isUniform()) {
				//simplified version
				if (!isAir(c, x, maxY, z)) {
					notifyBlockChange(v, x, maxY, z);
				}
				return;
			}
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

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public SpoutWorld getWorld() {
		return world;
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
				setDirty(x, z);
				falling(x, v, z);
			}
		}
	}

	private void falling(int x, AtomicInteger v, int z) {
		while (true) {
			int value = v.get();
			if (!isAir(x, value, z)) {
				return;
			}

			if (v.compareAndSet(value, value - 1)) {
				setDirty(x, z);
			}
		}
	}

	private boolean isAir(int x, int y, int z) {
		int xx = (this.x << BLOCKS.BITS) + (x & BLOCKS.MASK);
		int yy = y;
		int zz = (this.z << BLOCKS.BITS) + (z & BLOCKS.MASK);
		LoadOption opt = y < getGeneratorHeight(x, y) - Chunk.BLOCKS.DOUBLE_SIZE ?
				LoadOption.LOAD_ONLY : LoadOption.LOAD_GEN;
		Chunk c = world.getChunkFromBlock(xx, yy, zz, opt);
		if (c == null) {
			return false;
		} else {
			return isAir(world.getBlockFullState(xx, yy, zz));
		}
	}

	private boolean isAir(Chunk c, int x, int y, int z) {
		return isAir(c.getBlockFullState(x, y, z));
	}

	private boolean isAir(int fullData) {
		BlockMaterial material = BlockFullState.getMaterial(fullData);
		short data = BlockFullState.getData(fullData);
		return material.isTransparent() && !material.getOcclusion(data).getAny(BlockFaces.BT);
	}

	public AtomicInteger getAtomicInteger(int x, int z) {
		return heightMap[x & BLOCKS.MASK][z & BLOCKS.MASK];
	}

	private AtomicBoolean getDirtyFlag(int x, int z) {
		return dirtyArray[x & BLOCKS.MASK][z & BLOCKS.MASK];
	}

	public void setDirty(int x, int z) {
		getDirtyFlag(x, z).set(true);
		dirty.set(true);
	}
}
