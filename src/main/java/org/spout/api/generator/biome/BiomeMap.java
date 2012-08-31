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

import java.util.HashSet;
import java.util.Set;

import org.spout.api.io.store.map.MemoryStoreMap;
import org.spout.api.io.store.map.SimpleStoreMap;
import org.spout.api.math.Vector3;

/**
 * A simple store wrapper that holds biomes and the selector.
 */
public final class BiomeMap {
	private final SimpleStoreMap<Integer, Biome> map;
	private BiomeSelector selector;

	public BiomeMap() {
		map = new MemoryStoreMap<Integer, Biome>();
	}

	public Biome getBiomeRaw(int index) {
		return map.get(Math.abs(index) % map.getSize());
	}

	public void setSelector(BiomeSelector selector) {
		this.selector = selector;
		selector.parent = this;
	}

	public void addBiome(Biome biome) {
		map.set(map.getSize(), biome);
	}

	public Biome getBiome(int x, int z, long seed) {
		return getBiome(x, 0, z, seed);
	}

	public Biome getBiome(Vector3 position, long seed) {
		return getBiome((int) position.getX(), (int) position.getY(), (int) position.getZ(), seed);
	}

	public Biome getBiome(int x, int y, int z, long seed) {
		if (selector == null) {
			throw new IllegalStateException("Biome Selector is null and cannot set a selector");
		}
		return selector.pickBiome(x, y, z, seed);
	}

	public Set<Biome> getBiomes() {
		return new HashSet<Biome>(map.getValues());
	}

	public BiomeSelector getSelector() {
		return selector;
	}

	public int indexOf(Biome biome) {
		if (map.reverseGet(biome) == null) {
			return -1;
		}

		return map.reverseGet(biome);
	}
}
