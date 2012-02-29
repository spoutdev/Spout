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
package org.spout.api.generator.biome;

import org.spout.api.io.store.map.MemoryStoreMap;
import org.spout.api.io.store.map.SimpleStoreMap;

/**
 * A simple store wrapper that holds biomes and the selector.  
 * 
 *
 */
public final class BiomeMap {
	private final SimpleStoreMap<Integer, BiomeType> map;
	private BiomeSelector selector;

	public BiomeMap() {
		//Todo: Make this saveable
		map = new MemoryStoreMap<Integer, BiomeType>();
	}

	public void setSelector(BiomeSelector selector) {
		this.selector = selector;
	}

	public void addBiome(BiomeType biome) {
		map.set(map.getSize(), biome);
	}

	/**
	 * TODO This needs to generate a noise function relying on x and z to generate a map that is [0-map.getSize()] so that we can select
	 * Biomes for the biome generator
	 */
	public BiomeType getBiome(int x, int z, long seed) {
		return map.get(selector.pickBiome(x, z, map.getSize(), seed));
	}
	
	public BiomeType getBiome(int x, int y, int z, long seed) {
		return map.get(selector.pickBiome(x, y, z, map.getSize(), seed));
	}
}
