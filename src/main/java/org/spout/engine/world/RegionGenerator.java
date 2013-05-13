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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.spout.api.Spout;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.lighting.LightingManager;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;
import org.spout.api.math.GenericMath;
import org.spout.api.math.IntVector3;
import org.spout.api.scheduler.SnapshotLock;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;
import org.spout.api.util.cuboid.CuboidLightBuffer;
import org.spout.api.util.thread.DaemonThreadPool;

public class RegionGenerator {
	
	private final static DaemonThreadPool pool = new DaemonThreadPool("Region generator", -1);
	
	private final SpoutRegion region;
	private final SpoutWorld world;
	private final ReentrantLock[][] columnLocks;
	private final AtomicReference<GenerateState>[][] generatedColumns;
	private final int shift;
	private final int width;
	private final int mask;
	private final static AtomicInteger generationCounter = new AtomicInteger(1);
	private final int cx;
	private final int cy;
	private final int cz;
	
	@SuppressWarnings("unchecked")
	public RegionGenerator(SpoutRegion region, int width) {
		if (GenericMath.roundUpPow2(width) != width || width > Region.CHUNKS.SIZE || width < 0) {
			throw new IllegalArgumentException("Width must be a power of 2 and can't be more than one region width");
		}
		
		int sections = Region.CHUNKS.SIZE / width;
		
		this.width = width;
		this.mask = width - 1;
		this.generatedColumns = new AtomicReference[sections][sections];
		this.columnLocks = new ReentrantLock[sections][sections];
		
		for (int x = 0; x < sections; x++) {
			for (int z = 0; z < sections; z++) {
				this.generatedColumns[x][z] = new AtomicReference<GenerateState>(GenerateState.NONE);
				this.columnLocks[x][z] = new ReentrantLock();
			}
		}
		
		this.shift = GenericMath.multiplyToShift(width);
		this.region = region;
		this.world = region.getWorld();
		this.cx = region.getChunkX();
		this.cy = region.getChunkY();
		this.cz = region.getChunkZ();
	}

	public void generateColumn(final int chunkX, final int chunkZ) {
		generateColumn(chunkX, chunkZ, true);
	}
	
	public void generateColumn(final int chunkX, final int chunkZ, boolean sync) {

		final int x = chunkX & (~mask);
		final int z = chunkZ & (~mask);

		AtomicReference<GenerateState> generated = generatedColumns[x >> shift][z >> shift];
		if (generated.get().done(sync)) {
			return;
		}

		int generationIndex = generationCounter.getAndIncrement();

		while (generationIndex == -1) {
			Spout.getLogger().info("Ran out of generation index ids, starting again");
			generationIndex = generationCounter.getAndIncrement();
		}

		ReentrantLock colLock = columnLocks[x >> shift][z >> shift];

		if (sync) {
			if (Spout.getScheduler().getSnapshotLock().isReadLocked()) {
				throw new IllegalStateException("Attempt to sync generate a chunk during snapshot lock");
				// This code allows the sync thread to cancel an async generation.
				// However, sync generations should not happen during snapshot lock
				//
				// TODO - simplify this method, assuming no thread can hold a snapshot lock before it is called
				// generated.compareAndSet(GenerateState.IN_PROGRESS_ASYNC, GenerateState.IN_PROGRESS_SYNC);
			}
			colLock.lock();
		} else {
			if (!colLock.tryLock()) {
				return;
			}
		}

		short[][][][] rawIdArrays;
		short[][][][] rawDataArrays;

		LightingManager<?>[] managers;

		CuboidLightBuffer[][][][] buffers;
		
		boolean thrown = false;

		try {
			if (generated.get().done(sync)) {
				return;
			}
			
			if (!sync && generated.get().isAsyncInProgress()) {
				return;
			}
			
			rawIdArrays = new short[width][Region.CHUNKS.SIZE][width][];
			rawDataArrays = new short[width][Region.CHUNKS.SIZE][width][];

			managers = world.getLightingManagers();

			buffers = new CuboidLightBuffer[managers.length][][][];
			
			boolean success = false;
			success |= generated.compareAndSet(GenerateState.NONE, sync ? GenerateState.IN_PROGRESS_SYNC : GenerateState.IN_PROGRESS_ASYNC);

			if (sync && !success) {
				success |= generated.compareAndSet(GenerateState.IN_PROGRESS_SYNC, GenerateState.IN_PROGRESS_SYNC);
				if (!success) {
					success |= generated.compareAndSet(GenerateState.IN_PROGRESS_ASYNC, GenerateState.IN_PROGRESS_SYNC);
				}
			}

			if (!success) {
				throw new IllegalStateException("Unable to set generate state for column " + x + ", " + z + " int region " + region.getBase().toBlockString() + " to in progress, state is " + generated.get() + " sync is " + sync);
			}

			int cxx = cx + x;
			int czz = cz + z;

			final CuboidBlockMaterialBuffer buffer = new CuboidBlockMaterialBuffer(cxx << Chunk.BLOCKS.BITS, cy << Chunk.BLOCKS.BITS, czz << Chunk.BLOCKS.BITS, Chunk.BLOCKS.SIZE << shift, Region.BLOCKS.SIZE, Chunk.BLOCKS.SIZE << shift);
			world.getGenerator().generate(buffer, cxx, cy, czz, world);

			// TODO - probably need to harden this
			int[][] heights = new int[Chunk.BLOCKS.SIZE << shift][Chunk.BLOCKS.SIZE << shift];

			for (int xx = 0; xx < width; xx++) {
				for (int zz = 0; zz < width; zz++) {
					int[][] colHeights = world.getGenerator().getSurfaceHeight(world, cx, cz);
					int offX = xx << Chunk.BLOCKS.BITS;
					int offZ = zz << Chunk.BLOCKS.BITS;
					for (int colX = 0; colX < Chunk.BLOCKS.SIZE; colX++) {
						for (int colZ = 0; colZ < Chunk.BLOCKS.SIZE; colZ++) {
							heights[offX + colX][offZ + colZ] = colHeights[colX][colZ];
						}
					}
				}
			}

			for (int i = 0; i < managers.length; i++) {
				buffers[i] = managers[i].bulkInitializeUnchecked(buffer, heights);
			}

			for (int xx = 0; xx < width; xx++) {
				cxx = cx + x + xx;
				for (int zz = 0; zz < width; zz++) {
					czz = cz + z + zz;
					for (int yy = Region.CHUNKS.SIZE - 1; yy >= 0; yy--) {
						int cyy = cy + yy;
						final CuboidBlockMaterialBuffer chunk = new CuboidBlockMaterialBuffer(cxx << Chunk.BLOCKS.BITS, cyy << Chunk.BLOCKS.BITS, czz << Chunk.BLOCKS.BITS, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE);
						chunk.write(buffer);
						rawIdArrays[xx][yy][zz] = chunk.getRawId();
						rawDataArrays[xx][yy][zz] = chunk.getRawData();
					}
					if (generated.get().done(sync)) {
						return;
					}
				}
			}
		} catch (Throwable t) {
			thrown = true;
			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			} else {
				throw new RuntimeException(t);
			}
		} finally {
			// NOTE: Don't unlock column if in sync mode.  This needs to be handled carefully
			//       to make sure that the column lock is guaranteed to be released
			
			if (!sync || thrown) {
				colLock.unlock();
			}
		}

		// NOTE: No code may be run here if in sync mode
		//       An exception would cause the thread to die while holding the colLock

		SnapshotLock snapLock = null;
		if (!sync) {
			snapLock = Spout.getScheduler().getSnapshotLock();

			snapLock.readLock(this);
		}

		try {
			if (!sync) {
				if (!colLock.tryLock()) {
					return;
				}
			}
			
			if (generated.get().done(sync)) {
				return;
			}

			try {
				for (int xx = 0; xx < width; xx++) {
					int cxx = cx + x + xx;
					for (int zz = 0; zz < width; zz++) {
						int czz = cz + z + zz;
						for (int yy = Region.CHUNKS.SIZE - 1; yy >= 0; yy--) {
							int cyy = cy + yy;
							SpoutChunk newChunk = new SpoutChunk(world, region, cxx, cyy, czz, rawIdArrays[xx][yy][zz], rawDataArrays[xx][yy][zz], null);
							newChunk.setGenerationIndex(generationIndex);
	
							for (int i = 0; i < managers.length; i++) {
								CuboidLightBuffer lightBuffer = buffers[i][xx][yy][zz];
								if (newChunk.setIfAbsentLightBuffer((short) lightBuffer.getManagerId(), lightBuffer) != lightBuffer) {
									Spout.getLogger().info("Unable to set light buffer for new chunk " + newChunk + " as the id is already in use, " + lightBuffer.getManagerId());
								}
							}
							SpoutChunk currentChunk = region.setChunkIfNotGenerated(newChunk, x + xx, yy, z + zz, null, true);
							if (currentChunk != newChunk) {
								if (currentChunk == null) {
									Spout.getLogger().info("Warning: Unable to set generated chunk, new Chunk " + newChunk + " chunk in memory " + currentChunk);
								}
							} else {
								newChunk.setModified();
								newChunk.compressRaw();
							}
						}
					}
				}

				boolean success = false;
				success = generated.compareAndSet(GenerateState.IN_PROGRESS_SYNC, GenerateState.COPIED);
				
				if (!sync) {
					success = generated.compareAndSet(GenerateState.IN_PROGRESS_ASYNC, GenerateState.COPIED);
				}
				if (!success) {
					throw new IllegalStateException("Column " + x + ", " + z + " rY=" + region.getChunkY() + " int region " + region.getBase().toBlockString() + " copied twice after generation, generation state is " + generated + " sync is " + sync);
				}

			} finally {
				colLock.unlock();
			}
		} finally {
			if (!sync) {
				snapLock.readUnlock(this);
			}
		}
		
		if (sync) {
			touchChunk(chunkX, 0, chunkZ);
		}
	}
	
	public void touchChunkNeighbors(SpoutChunk c) {
		
		if (!c.isObserved()) {
			Spout.getLogger().info("Touched chunk is not observed, " + c);
			return;
		}
		
		int x = c.getX() & Region.CHUNKS.MASK;
		int z = c.getZ() & Region.CHUNKS.MASK;
		int y = c.getZ() & Region.CHUNKS.MASK;
		
		touchChunkNeighbors(x, y, z);
		
	}
	
	private void touchChunkNeighbors(int x, int y, int z) {
		
		x &= Region.CHUNKS.MASK;
		z &= Region.CHUNKS.MASK;
		y &= Region.CHUNKS.MASK;
		
		BlockFace[] faces = BlockFaces.NESW.toArray();
		
		for (BlockFace face : faces) {
			IntVector3 v = face.getIntOffset();

			final int ox = x + (v.getX() << shift);

			final int oz = z + (v.getZ() << shift);
			
			if (ox < 0 || ox >= Region.CHUNKS.SIZE || oz < 0 || oz >= Region.CHUNKS.SIZE) {
				SpoutRegion newRegion = region.getLocalRegion(face, LoadOption.NO_LOAD);
				if (newRegion != null) {
					newRegion.getRegionGenerator().touchChunk(ox, y, oz);
				} else {
					final BlockFace finalFace = face;
					
					pool.add(new Runnable() {
						@Override
						public void run() {
							SnapshotLock lock = Spout.getScheduler().getSnapshotLock();
							lock.readLock(RegionGenerator.this);
							try {
								SpoutRegion newRegion = region.getLocalRegion(finalFace, LoadOption.LOAD_GEN);
								newRegion.getRegionGenerator().touchChunk(ox, 0, oz);
							} finally {
								lock.readUnlock(RegionGenerator.this);
							}
						}
					}, true);
				}
			} else {
				touchChunk(ox, y, oz);
			}
		}
	}
	
	protected void touchChunk(final int x, final int y, final int z) {

		final int mask = Region.CHUNKS.MASK;

		Chunk c = region.getChunk(x, y, z, LoadOption.NO_LOAD);

		if (c != null) {
			return;
		}

		if (!region.inputStreamExists(x, y, z)) {
			pool.add(new Runnable() {
				@Override
				public void run() {
					generateColumn(x & mask, z & mask, false);
				}
			}, true);
		}
	}
	
	private static enum GenerateState {
		
		NONE, IN_PROGRESS_ASYNC, IN_PROGRESS_SYNC, COPIED;
		
		public boolean done(boolean sync) {
			if (!sync) {
				return this == IN_PROGRESS_SYNC || this == COPIED;
			} else {
				return this == COPIED;
			}
		}
		
		public boolean isAsyncInProgress() {
			return this == IN_PROGRESS_ASYNC;
		}
	
	}

}
