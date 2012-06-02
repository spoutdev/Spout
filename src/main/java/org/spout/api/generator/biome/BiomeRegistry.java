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

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Spout;
import org.spout.api.io.store.simple.BinaryFileStore;
import org.spout.api.util.StringMap;

public final class BiomeRegistry {
	private final static int MAX_BIOMES = 256;
	@SuppressWarnings("unchecked")
	private final static AtomicReference<Biome>[] biomes = new AtomicReference[MAX_BIOMES];
	private static boolean setup = false;
	private final static BinaryFileStore store = new BinaryFileStore();
	private final static StringMap biomeRegistry = new StringMap(null, store, 1, MAX_BIOMES);

	static {
		for (int i = 0; i < biomes.length; i++) {
			biomes[i] = new AtomicReference<Biome>();
		}
	}

	/**
	 * Sets up the biome registry for the first use
	 */
	public static StringMap setupRegistry() {
		if (!setup) {
			File biomeStoreFile = new File(new File(Spout.getEngine().getWorldFolder(), "worlds"), "biomes.dat");
			store.setFile(biomeStoreFile);
			if (biomeStoreFile.exists()) {
				store.load();
			}
			setup = true;
			return biomeRegistry;
		} else {
			throw new IllegalStateException("Can not setup biome registry twice!");
		}
	}

	/**
	 * Registers a biome and assigns it an id
	 *
	 * @param biome
	 */
	protected static void register(Biome biome) {
		int id = biomeRegistry.register(biome.getClass().getCanonicalName());
		biomes[id].set(biome);
		biome.setId(id);
	}

	/**
	 * Gets the biome associated with the biome id
	 *
	 * @param id
	 * @return biome
	 */
	public static Biome getBiome(int id) {
		return biomes[id].get();
	}
}
