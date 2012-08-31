/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.api.generator.biome;

import org.spout.api.geo.cuboid.Chunk;

public class Simple2DBiomeManager extends BiomeManager {
	private byte[] biomes = new byte[Chunk.BLOCKS.AREA];

	public Simple2DBiomeManager(int chunkX, int chunkY, int chunkZ) {
		super(chunkX, chunkY, chunkZ);
	}

	@Override
	public Biome getBiome(int x, int y, int z) {
		int index = z << 4 | x;
		return BiomeRegistry.getBiome(biomes[index]);
	}

	@Override
	public byte[] serialize() {
		byte[] data = new byte[Chunk.BLOCKS.AREA];
		System.arraycopy(biomes, 0, data, 0, biomes.length);
		return data;
	}

	@Override
	public void deserialize(byte[] bytes) {
		this.biomes = bytes;
	}

	@Override
	public Simple2DBiomeManager clone() {
		Simple2DBiomeManager manager = new Simple2DBiomeManager(getChunkX(), getChunkY(), getChunkZ());
		manager.deserialize(serialize());
		return manager;
	}
}
