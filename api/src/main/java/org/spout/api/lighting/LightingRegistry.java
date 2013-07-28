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
package org.spout.api.lighting;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import static org.spout.api.Platform.CLIENT;
import static org.spout.api.Platform.SERVER;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.io.store.simple.BinaryFileStore;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.util.SyncedStringMap;

public class LightingRegistry {
	private final static int MAX_SIZE = 1 << 16;
	@SuppressWarnings ("unchecked")
	private final static AtomicReference<LightingManager<?>>[] lightingLookup = new AtomicReference[MAX_SIZE];
	private final static AtomicReference<LightingManager<?>[]> managerArray = new AtomicReference<>();
	private static boolean setup = false;
	private static SyncedStringMap lightingRegistry;

	static {
		for (int i = 0; i < lightingLookup.length; i++) {
			lightingLookup[i] = new AtomicReference<>();
		}
	}

	/**
	 * Sets up the lighting registry for its first use. May not be called more than once.<br/> This attempts to load the lighting.dat file from the 'worlds' directory into memory.<br/>
	 *
	 * Can throw an {@link IllegalStateException} if the material registry has already been setup.
	 *
	 * @return StringToUniqueIntegerMap of registered materials
	 */
	public static SyncedStringMap setupRegistry() {
		if (setup) {
			throw new IllegalStateException("Can not setup lighting registry twice!");
		}
		switch (Spout.getPlatform()) {
			case SERVER:
				File lightingStoreFile = new File(((Server) Spout.getEngine()).getWorldFolder(), "lighting.dat");
				final BinaryFileStore store = new BinaryFileStore(lightingStoreFile);
				if (lightingStoreFile.exists()) {
					store.load();
				}
				lightingRegistry = SyncedStringMap.create(null, store, 0, Short.MAX_VALUE, LightingManager.class.getName());
				break;
			case CLIENT:
				lightingRegistry = SyncedStringMap.create(null, new MemoryStore<Integer>(), 0, Short.MAX_VALUE, LightingManager.class.getName());
				break;
		}
		setup = true;
		return lightingRegistry;
	}

	/**
	 * Registers the LightingManager in the Lighting lookup service
	 *
	 * @param material to register
	 * @return id of the material registered
	 */
	public static int register(LightingManager<?> manager) {
		if (!setup) {
			throw new IllegalStateException("Tried to access LightingRegistry before it's registered!");
		}
		int id = lightingRegistry.register(formatName(manager.getName()));
		if (!lightingLookup[id].compareAndSet(null, manager)) {
			throw new IllegalArgumentException(lightingLookup[id].get() + " is already mapped to id: " + manager.getId() + "!");
		}
		System.out.println("Registered manager " + manager.getName() + " with Id " + id);

		addToManagerArray(manager);
		return id;
	}

	/**
	 * Gets the LightingManager from the given id
	 *
	 * @param id to get
	 * @return manager or null if none found
	 */
	public static LightingManager<?> get(short id) {
		if (id < 0 || id >= lightingLookup.length) {
			return null;
		}
		return lightingLookup[id].get();
	}

	/**
	 * Gets the LightingManager by its name. Case-insensitive.
	 *
	 * @param name to lookup
	 * @return LightingManager, or null if none found
	 */
	public static LightingManager<?> get(String name) {
		return lightingLookup[lightingRegistry.getValue(formatName(name))].get();
	}

	/**
	 * Returns a human legible name from the full LightingManager name.
	 *
	 * This will strip out extra whitespace, strip any other spaces and replace with '_', and lowercase the LightingManager name.
	 *
	 * @return human legible name of the material.
	 */
	private static String formatName(String name) {
		return name.trim().replaceAll(" ", "_").toLowerCase();
	}

	public static LightingManager<?>[] getManagers() {
		return managerArray.get();
	}

	private static void addToManagerArray(LightingManager<?> manager) {
		boolean success = false;
		while (!success) {
			LightingManager<?>[] oldArray = managerArray.get();
			LightingManager<?>[] newArray;
			if (oldArray == null) {
				newArray = new LightingManager<?>[] {manager};
			} else {
				newArray = new LightingManager<?>[oldArray.length + 1];
				for (int i = 0; i < oldArray.length; i++) {
					if (oldArray[i] == manager) {
						throw new IllegalArgumentException(manager.getName() + " added to the Lighting Registry more than once");
					}
					newArray[i] = oldArray[i];
				}
				newArray[oldArray.length] = manager;
			}
			success = managerArray.compareAndSet(oldArray, newArray);
		}
	}
}
