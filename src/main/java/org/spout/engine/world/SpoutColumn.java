/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.generator.biome.BiomeGenerator;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.math.BitSize;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.cuboid.ImmutableHeightMapBuffer;
import org.spout.api.util.list.concurrent.setqueue.SetQueue;
import org.spout.api.util.list.concurrent.setqueue.SetQueueElement;
import org.spout.engine.filesystem.versioned.ColumnFiles;

public class SpoutColumn {
	/**
	 * Stores the size of the amount of blocks in this Column
	 */
	public static BitSize BLOCKS = Chunk.BLOCKS;

	private final SpoutWorld world;
	private final int x;
	private final int z;
	private final AtomicInteger activeChunks = new AtomicInteger(0);
	private final AtomicInteger[][] heightMap;
	private final int[][] heightMapSnapshot = new int[BLOCKS.SIZE][BLOCKS.SIZE];
	private final AtomicInteger dirtyColumns = new AtomicInteger(0);
	private final AtomicInteger lowestY = new AtomicInteger();
	private final AtomicInteger highestY = new AtomicInteger();
	private final AtomicReference<int[][]> heights = new AtomicReference<int[][]>();
	private final AtomicBoolean dirty = new AtomicBoolean(false);
	private final AtomicBoolean dirtyArray[][];
	private final BlockMaterial[][] topmostBlocks;
	private final AtomicReference<BiomeManager> biomes = new AtomicReference<BiomeManager>();
	private final SetQueueElement<SpoutColumn> heightDirtyQueue;

	public SpoutColumn(InputStream in, SpoutWorld world, int x, int z) {
		this(in, null, world, x, z);
	}
	
	public 	SpoutColumn(int[][] heights, SpoutWorld world, int x, int z) {
		this(null, heights, world, x, z);
	}
	
	private SpoutColumn(InputStream in, int[][] heights, SpoutWorld world, int x, int z) {
		
		if (heights != null && in != null) {
			throw new IllegalArgumentException("Both heights and input stream were non-null");
		}
		
		this.world = world;
		this.x = x;
		this.z = z;
		this.heightMap = new AtomicInteger[BLOCKS.SIZE][BLOCKS.SIZE];
		this.dirtyArray = new AtomicBoolean[BLOCKS.SIZE][BLOCKS.SIZE];
		this.topmostBlocks = new BlockMaterial[BLOCKS.SIZE][BLOCKS.SIZE];
		
		this.heightDirtyQueue = new ColumnSetQueueElement(world.getColumnDirtyQueue(x >> Region.CHUNKS.BITS, z >> Region.CHUNKS.BITS), this);

		for (int xx = 0; xx < BLOCKS.SIZE; xx++) {
			for (int zz = 0; zz < BLOCKS.SIZE; zz++) {
				heightMap[xx][zz] = new AtomicInteger(heights == null ? 0 : heights[xx][zz]);
				dirtyArray[xx][zz] = new AtomicBoolean(false);
			}
		}

		lowestY.set(Integer.MAX_VALUE);

		if (heights == null) {
			ColumnFiles.readColumn(in, this, this.lowestY, this.highestY, topmostBlocks);
		}
		//Could not load biomes from column, so calculate them
		if (biomes.get() == null) {
			if (world.getGenerator() instanceof BiomeGenerator) {
				BiomeGenerator generator = (BiomeGenerator)world.getGenerator();
				setBiomeManager(generator.generateBiomes(x, z, world));
			}
		}
		copySnapshot();
	}
	
	public void copySnapshot() {
		for (int xx = 0; xx < BLOCKS.SIZE; xx++) {
			for (int zz = 0; zz < BLOCKS.SIZE; zz++) {
				heightMapSnapshot[xx][zz] = heightMap[xx][zz].get();
			}
		}
	}

	public void onFinalize() {
		TickStage.checkStage(TickStage.FINALIZE);
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

	public void registerCuboid(int minY, int maxY) {
		boolean success = false;
		while (!success) {
			int oldLowestY = lowestY.get();
			if (minY < oldLowestY) {
				success = lowestY.compareAndSet(oldLowestY, minY);
			} else {
				success = true;
			}
		}
		success = false;
		while (!success) {
			int oldHighestY = highestY.get();
			if (maxY > oldHighestY) {
				success = highestY.compareAndSet(oldHighestY, maxY);
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
				syncSave();
				((SpoutWorld) world).removeColumn(x, z, this);
			}
		} else {
			activeChunks.decrementAndGet();
		}
	}
	
	public synchronized void syncSave() {
		OutputStream out = ((SpoutWorld) world).getHeightMapOutputStream(x, z);
		try {
			ColumnFiles.writeColumn(out, this, lowestY, highestY, topmostBlocks);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}
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

		if (((SpoutChunk) c).isBlockUniform()) {
			//simplified version
			if (!isAir(c, x, maxY, z)) {
				notifyBlockChange(v, x, maxY, z);
			}
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
		AtomicInteger v = getAtomicInteger(x, z);
		notifyBlockChange(v, x, y, z);
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
		while (true) {
			int value = v.get();
			if (y < value) {
				return;
			} else if (y == value) {
				falling(x, v, z);
				return;
			} else {
				if (!isAir(x, y, z)) {
					if (!v.compareAndSet(value, y)) {
						continue;
					}
					setDirty(x, z);
					falling(x, v, z);
					return;
				} else {
					return;
				}
			}
		}
	}

	private void falling(int x, AtomicInteger v, int z) {
		boolean dirty = false;
		try {
			while (true) {
				int value = v.get();
				if (!isAir(x, value, z)) {
					return;
				}

				if (v.compareAndSet(value, value - 1)) {
					dirty = true;
				}
			}
		} finally {
			if (dirty) {
				setDirty(x, z);
			}
		}
	}

	private boolean isAir(int x, int y, int z) {
		int xx = (this.x << BLOCKS.BITS) + (x & BLOCKS.MASK);
		int yy = y;
		int zz = (this.z << BLOCKS.BITS) + (z & BLOCKS.MASK);
		LoadOption opt = LoadOption.LOAD_ONLY;
		Chunk c = world.getChunkFromBlock(xx, yy, zz, opt);
		if (c == null) {
			return false;
		} else {
			return isAir(c.getBlockFullState(xx, yy, zz));
		}
	}

	private boolean isAir(Chunk c, int x, int y, int z) {
		return isAir(c.getBlockFullState(x, y, z));
	}

	private boolean isAir(int fullData) {
		BlockMaterial material = BlockFullState.getMaterial(fullData);
		return !material.isSurface();
	}

	public AtomicInteger getAtomicInteger(int x, int z) {
		return heightMap[x & BLOCKS.MASK][z & BLOCKS.MASK];
	}

	private AtomicBoolean getDirtyFlag(int x, int z) {
		return dirtyArray[x & BLOCKS.MASK][z & BLOCKS.MASK];
	}
	
	public int fillDirty(int pos, int x[], int[] newHeight, int[] oldHeight, int[] z, int minY, int maxY) {
		TickStage.checkStage(TickStage.LIGHTING);
		int bx = getX() << BLOCKS.BITS;
		int bz = getZ() << BLOCKS.BITS;

		for (int xx = 0; xx < BLOCKS.SIZE; xx++) {
			for (int zz = 0; zz < BLOCKS.SIZE; zz++) {
				if (getDirtyFlag(xx, zz).get() && heightMapSnapshot[xx][zz] != Integer.MIN_VALUE) {
					x[pos] = bx + xx;
					z[pos] = bz + zz;
					int nh = heightMap[xx][zz].get();
					int oh = heightMapSnapshot[xx][zz];
					if ((nh >= maxY && oh >= maxY) || (nh < minY && oh < minY)) {
						continue;
					}
					newHeight[pos] = nh;
					oldHeight[pos] = oh;
					pos++;
				}
			}
		}
		return pos;
	}
	
	public int getDirtyColumns() {
		TickStage.checkStage(TickStage.LIGHTING);
		return Math.min(256, dirtyColumns.get());
	}

	public void setDirty(int x, int z) {
		TickStage.checkStage(~TickStage.LIGHTING);
		dirtyColumns.incrementAndGet();
		heightDirtyQueue.add();
		getDirtyFlag(x, z).set(true);
		setDirty();
	}
	
	public void setDirty() {
		dirty.set(true);
	}
	
	public BiomeManager getBiomeManager() {
		return biomes.get();
	}
	
	public boolean setBiomeManager(BiomeManager manager) {
		if (biomes.compareAndSet(null, manager)) {
			return true;
		}
		return false;
	}
	
	public ImmutableHeightMapBuffer getHeightMapBuffer() {
		return new ImmutableHeightMapBuffer(getX() << BLOCKS.BITS, getZ() << BLOCKS.BITS, SpoutColumn.BLOCKS.SIZE, SpoutColumn.BLOCKS.SIZE, heightMap);
	}
	
	@Override
	public String toString() {
		return "SpoutColumn{ " + getX() + ", " + getZ() + "}";
	}
	
	private class ColumnSetQueueElement extends SetQueueElement<SpoutColumn> {
		
		public ColumnSetQueueElement(SetQueue<SpoutColumn> queue, SpoutColumn value) {
			super(queue, value);
		}

		@Override
		protected boolean isValid() {
			return true;
		}
		
	}
	
}
