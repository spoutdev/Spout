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
import org.spout.api.math.Vector3;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple store wrapper that holds biomes and the selector.
 */
public final class BiomeMap {
	private final SimpleStoreMap<Vector3, BiomeType> biomeOverrides;
	private final SimpleStoreMap<Integer, BiomeType> map;
	private BiomeSelector selector;

	public BiomeMap() {
		//Todo: Make this saveable
		map = new MemoryStoreMap<Integer, BiomeType>();
		biomeOverrides = new MemoryStoreMap<Vector3, BiomeType>();
	}

	public void setSelector(BiomeSelector selector) {
		this.selector = selector;
	}

	public void addBiome(BiomeType biome) {
		map.set(map.getSize(), biome);
	}

	public void setBiome(Vector3 loc, BiomeType biome) {
		biomeOverrides.set(loc, biome);
	}

	/**
	 * TODO This needs to generate a noise function relying on x and z to generate a map that is [0-map.getSize()] so that we can select
	 * Biomes for the biome generator
	 */
	public BiomeType getBiome(int x, int z, long seed) {
		return getBiome(x, 0, z, seed);
	}

	public BiomeType getBiome(int x, int y, int z, long seed) {
		BiomeType biome = biomeOverrides.get(new Vector3(x, y, z));
		if (biome == null) {
			biome = map.get(Math.abs(selector.pickBiome(x, y, z, seed)) % map.getSize());
		}
		return biome;
	}

	public Collection<BiomeType> getBiomes() {
		Set<BiomeType> biomes = new HashSet<BiomeType> (map.getValues());
		biomes.addAll(biomeOverrides.getValues());
		return biomes;
	}
	
	public int indexOf(BiomeType biome) {
		if(map.reverseGet(biome) != null) {
			return map.reverseGet(biome);
		} else {
			return -1;
		}
	}
}
