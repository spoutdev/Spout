/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.generator.biome;

import org.spout.api.geo.cuboid.Chunk;

public class Simple3DBiomeManager extends BiomeManager{
	private byte[] biomes = new byte[Chunk.CHUNK_VOLUME];
	public Simple3DBiomeManager(int chunkX, int chunkY, int chunkZ) {
		super(chunkX, chunkY, chunkZ);
	}

	@Override
	public Biome getBiome(int x, int y, int z) {
		int index = y << 8 | z << 4 | x;
		return BiomeRegistry.getBiome(biomes[index]);
	}

	@Override
	public byte[] serialize() {
		byte[] data = new byte[Chunk.CHUNK_VOLUME];
		System.arraycopy(biomes, 0, data, 0, biomes.length);
		return data;
	}

	@Override
	public void deserialize(byte[] bytes) {
		this.biomes = bytes;
	}
	
	@Override
	public Simple3DBiomeManager clone() {
		Simple3DBiomeManager manager = new Simple3DBiomeManager(getChunkX(), getChunkY(), getChunkZ());
		manager.deserialize(serialize());
		return manager;
	}
}
