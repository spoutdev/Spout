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

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Platform;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.io.store.simple.BinaryFileStore;
import org.spout.api.util.StringToUniqueIntegerMap;

public final class BiomeRegistry {
	private final static int MAX_BIOMES = 256;
	@SuppressWarnings ("unchecked")
	private final static AtomicReference<Biome>[] biomes = new AtomicReference[MAX_BIOMES];
	private static boolean setup = false;
	private final static BinaryFileStore store = new BinaryFileStore();
	private final static StringToUniqueIntegerMap biomeRegistry = new StringToUniqueIntegerMap(null, store, 1, MAX_BIOMES, Biome.class.getName());

	static {
		for (int i = 0; i < biomes.length; i++) {
			biomes[i] = new AtomicReference<>();
		}
	}

	/**
	 * Sets up the biome registry for the first use
	 */
	public static StringToUniqueIntegerMap setupRegistry() {
		if (setup) {
			throw new IllegalStateException("Can not setup biome registry twice!");
		}
		if (Spout.getPlatform() != Platform.SERVER) {
			throw new UnsupportedOperationException("Cannot setup BiomeRegistry in Client mode!");
		}
		File biomeStoreFile = new File(new File(((Server) Spout.getEngine()).getWorldFolder(), "worlds"), "biomes.dat");
		store.setFile(biomeStoreFile);
		if (biomeStoreFile.exists()) {
			store.load();
		}
		setup = true;
		return biomeRegistry;
	}

	/**
	 * Registers a biome and assigns it an id
	 */
	protected static void register(Biome biome) {
		int id = biomeRegistry.register(biome.getClass().getCanonicalName());
		biomes[id].set(biome);
		biome.setId(id);
	}

	/**
	 * Gets the biome associated with the biome id
	 *
	 * @return biome
	 */
	public static Biome getBiome(int id) {
		return biomes[id].get();
	}
}
