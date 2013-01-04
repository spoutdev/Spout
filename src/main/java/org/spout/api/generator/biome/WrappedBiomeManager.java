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
package org.spout.api.generator.biome;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;

/**
 * Wraps multiple BiomeManagers into a single BiomeManager
 */
public class WrappedBiomeManager extends BiomeManager {
	
	private final int BLOCK_MASK = Chunk.BLOCKS.MASK;
	private final int BLOCK_BITS = Chunk.BLOCKS.BITS;
	
	private final BiomeManager[][] subManagers;
	
	public WrappedBiomeManager(World world, int bx, int bz, boolean load) {
		super(bx, bz);
		subManagers = new BiomeManager[Region.CHUNKS.SIZE][Region.CHUNKS.SIZE];
		int xx = 0;
		for (int x = 0; x < Region.CHUNKS.SIZE; x++) {
			int zz = 0;
			for (int z = 0; z < Region.CHUNKS.SIZE; z++) {
				subManagers[x][z] = world.getBiomeManager(xx + bx, zz + bz, load);
				zz += Chunk.BLOCKS.SIZE;
			}
			xx += Chunk.BLOCKS.SIZE;
		}
	}

	@Override
	public Biome getBiome(int x, int y, int z) {
		int bx = x & BLOCK_MASK;
		int cx = (x >> BLOCK_BITS);
		
		int bz = z & BLOCK_MASK;
		int cz = (z >> BLOCK_BITS);
		
		BiomeManager bm = subManagers[cx][cz];
		if (bm == null) {
			return null;
		}
		return bm.getBiome(bx, y, bz);
	}

	@Override
	public byte[] serialize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deserialize(byte[] bytes) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public BiomeManager clone() {
		throw new UnsupportedOperationException();
	}

}
