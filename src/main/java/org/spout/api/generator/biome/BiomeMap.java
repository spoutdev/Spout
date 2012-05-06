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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.spout.api.io.store.map.MemoryStoreMap;
import org.spout.api.io.store.map.SimpleStoreMap;
import org.spout.api.math.Vector3;

/**
 * A simple store wrapper that holds biomes and the selector.
 */
public final class BiomeMap {
	private final SimpleStoreMap<Vector3, Biome> biomeOverrides;
	private final SimpleStoreMap<Integer, Biome> map;
	private final SimpleStoreMap<Vector3, Biome> cache1;
	private final SimpleStoreMap<Vector3, Biome> cache0;
	private final int CACHE_TIMEOUT = 30000;
	private final AtomicLong lastCacheClear = new AtomicLong(0);
	private final AtomicBoolean cacheSelect = new AtomicBoolean(false);
	private BiomeSelector selector;

	public BiomeMap() {
		//Todo: Make this saveable
		map = new MemoryStoreMap<Integer, Biome>();
		biomeOverrides = new MemoryStoreMap<Vector3, Biome>();
		cache1 = new MemoryStoreMap<Vector3, Biome>();
		cache0 = new MemoryStoreMap<Vector3, Biome>();
	}
	
	public Biome getBiomeRaw(int index){
		return map.get(Math.abs(index) % map.getSize());
	}

	public void setSelector(BiomeSelector selector) {
		this.selector = selector;
		selector.parent = this;
	}

	public void addBiome(Biome biome) {
		map.set(map.getSize(), biome);
	}

	public void setBiome(Vector3 loc, Biome biome) {
		biomeOverrides.set(loc, biome);
	}

	/**
	 * TODO This needs to generate a noise function relying on x and z to generate a map that is [0-map.getSize()] so that we can select
	 * Biomes for the biome generator
	 */
	public Biome getBiome(int x, int z, long seed) {
		return getBiome(x, 0, z, seed);
	}

	public Biome getBiome(int x, int y, int z, long seed) {
		return getBiome(new Vector3(x,y,z), seed);
	}

	/**
	 * Returns the biome at the current location.  If the position has a override, that override is used.
	 * @param position
	 * @param seed
	 * @return
	 */
	public Biome getBiome(Vector3 position, long seed) {
		if(selector == null) throw new IllegalStateException("Biome Selector is null and cannot set a selector");
		Biome biome = biomeOverrides.get(position);
		
		SimpleStoreMap<Vector3, Biome> cacheNew;
		SimpleStoreMap<Vector3, Biome> cacheOld;
		
		if (cacheSelect.get()) {
			cacheNew = cache1;
			cacheOld = cache0;
		} else {
			cacheNew = cache0;
			cacheOld = cache1;
		}
		
		if (biome == null) {
			biome = cacheNew.get(position);
			if (biome == null) {
				biome = cacheOld.get(position);
				if (biome == null) {
					biome = selector.pickBiome((int)position.getX(), (int)position.getY(), (int)position.getZ(), seed);
				}
				cacheNew.set(position, biome);
			}
		}
		long currentTime = System.currentTimeMillis();
		long lastClearTime = lastCacheClear.get();
		if (currentTime - lastClearTime > this.CACHE_TIMEOUT) {
			if (lastCacheClear.compareAndSet(lastClearTime, currentTime)) {
				cacheSelect.set(!cacheSelect.get());
				cacheOld.clear();
			}
		}
		return biome;
	}
	
	public Collection<Biome> getBiomes() {
		Set<Biome> biomes = new HashSet<Biome> (map.getValues());
		biomes.addAll(biomeOverrides.getValues());
		return biomes;
	}
	
	public int indexOf(Biome biome) {
		if(map.reverseGet(biome) != null) {
			return map.reverseGet(biome);
		} else {
			return -1;
		}
	}
}
