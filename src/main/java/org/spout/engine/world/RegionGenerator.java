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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.Spout;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.lighting.LightingManager;
import org.spout.api.math.GenericMath;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;
import org.spout.api.util.cuboid.CuboidLightBuffer;
import org.spout.api.util.thread.DaemonThreadPool;

public class RegionGenerator {
	
	private final static DaemonThreadPool pool = new DaemonThreadPool("Region generator");
	
	private final SpoutRegion region;
	private final SpoutWorld world;
	private final AtomicBoolean[][] generatedColumns;
	private final int shift;
	private final int width;
	private final int mask;
	private final static AtomicInteger generationCounter = new AtomicInteger(1);
	private final int cx;
	private final int cy;
	private final int cz;
	
	public RegionGenerator(SpoutRegion region, int width) {
		if (GenericMath.roundUpPow2(width) != width || width > Region.CHUNKS.SIZE || width < 0) {
			throw new IllegalArgumentException("Width must be a power of 2 and can't be more than one region width");
		}
		
		int sections = Region.CHUNKS.SIZE / width;
		
		this.width = width;
		this.mask = width - 1;
		this.generatedColumns = new AtomicBoolean[sections][sections];
		
		for (int x = 0; x < sections; x++) {
			for (int z = 0; z < sections; z++) {
				this.generatedColumns[x][z] = new AtomicBoolean(false);
			}
		}
		
		this.shift = GenericMath.multiplyToShift(width);
		this.region = region;
		this.world = region.getWorld();
		this.cx = region.getChunkX();
		this.cy = region.getChunkY();
		this.cz = region.getChunkZ();
	}
	
	public void generateColumn(int x, int z) {
		
		x &= ~mask;
		z &= ~mask;
		
		AtomicBoolean generated = generatedColumns[x >> shift][z >> shift];
		if (generated.get()) {
			return;
		}
		
		int generationIndex = generationCounter.getAndIncrement();
		
		while (generationIndex == -1) {
			Spout.getLogger().info("Ran out of generation index ids, starting again");
			generationIndex = generationCounter.getAndIncrement();
		}
		
		synchronized(generated) {
			if (generated.get()) {
				return;
			}
			
			int cxx = cx + x;
			int czz = cz + z;

			final CuboidBlockMaterialBuffer buffer = new CuboidBlockMaterialBuffer(cxx << Chunk.BLOCKS.BITS, cy << Chunk.BLOCKS.BITS, czz << Chunk.BLOCKS.BITS, Chunk.BLOCKS.SIZE << shift, Region.BLOCKS.SIZE, Chunk.BLOCKS.SIZE << shift);
			world.getGenerator().generate(buffer, cxx, cy, czz, world);
			
			LightingManager<?>[] managers = world.getLightingManagers();

			CuboidLightBuffer[][][][] buffers = new CuboidLightBuffer[managers.length][][][];
			
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
						SpoutChunk newChunk = new SpoutChunk(world, region, cxx, cyy, czz, chunk.getRawId(), chunk.getRawData(), null);
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
							newChunk.compressRaw();
							newChunk.setModified();
						}
					}
				}
			}
			if (!generated.compareAndSet(false, true)) {
				throw new IllegalStateException("Column " + x + ", " + z + " int region " + region.getBase().toBlockString() + " generated twice");
			}
		}

	}

}
