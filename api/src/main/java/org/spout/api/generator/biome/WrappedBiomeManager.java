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
package org.spout.api.generator.biome;

import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.math.Vector2;

/**
 * Wraps multiple BiomeManagers into a single BiomeManager
 */
public class WrappedBiomeManager extends BiomeManager {
	private final BiomeManager[][] subManagers;

	/**
	 * Construct a new WrappedBiomeManager which will wrap biome managers on plane starting at (x, z) and ending at (x + sizeX, z + sizeZ). All coordinates are in chunks.
	 *
	 * @param world The world which will provide the biome data.
	 * @param x The starting x coordinate in chunks.
	 * @param z The starting z coordinate in chunks.
	 * @param sizeX The size on x in chunks.
	 * @param sizeZ The size on z in chunks.
	 */
	public WrappedBiomeManager(BiomeGenerator generator, World world, int x, int z, int sizeX, int sizeZ) {
		super(x, z);
		subManagers = new BiomeManager[sizeX][sizeZ];
		for (int xx = 0; xx < sizeX; xx++) {
			for (int zz = 0; zz < sizeZ; zz++) {
				subManagers[xx][zz] = world.getBiomeManager(x + xx << Chunk.BLOCKS.BITS, z + zz << Chunk.BLOCKS.BITS, LoadOption.NO_LOAD);
				if (subManagers[xx][zz] == null) {
					subManagers[xx][zz] = generator.generateBiomes(x + xx, z + zz, world);
				}
			}
		}
	}

	@Override
	public Biome getBiome(int x, int y, int z) {
		final BiomeManager bm = subManagers[x >> Chunk.BLOCKS.BITS][z >> Chunk.BLOCKS.BITS];
		if (bm == null) {
			return null;
		}
		return bm.getBiome(x & Chunk.BLOCKS.MASK, y, z & Chunk.BLOCKS.MASK);
	}

	/**
	 * Gets the size of this biome manager in chunks. Returns a new Vector2.
	 *
	 * @return The vector representing the size of this wrapped biome manager, in chunks.
	 */
	public Vector2 getChunkSize() {
		return new Vector2(subManagers.length, subManagers[0].length);
	}

	/**
	 * Gets the size of this biome manager in blocks. Returns a new Vector2.
	 *
	 * @return The vector representing the size of this wrapped biome manager, in block.
	 */
	public Vector2 getSize() {
		return getChunkSize().multiply(Chunk.BLOCKS.SIZE);
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
