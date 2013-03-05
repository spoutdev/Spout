/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Spout;
import org.spout.api.io.store.simple.BinaryFileStore;
import org.spout.api.util.StringMap;

public class LightingRegistry {

	private final static ConcurrentHashMap<String, LightingManager<?>> nameLookup = new ConcurrentHashMap<String, LightingManager<?>>(10);
	private final static int MAX_SIZE = 1 << 16;
	@SuppressWarnings("unchecked")
	private final static AtomicReference<LightingManager<?>>[] lightingLookup = new AtomicReference[MAX_SIZE];
	private final static AtomicReference<LightingManager<?>[]> managerArray = new AtomicReference<LightingManager<?>[]>();
	private static boolean setup = false;
	private final static BinaryFileStore store = new BinaryFileStore();
	private final static StringMap lightingRegistry = new StringMap(null, store, 1, Short.MAX_VALUE, LightingManager.class.getName());
	
	static {
		for (int i = 0; i < lightingLookup.length; i++) {
			lightingLookup[i] = new AtomicReference<LightingManager<?>>();
		}
	}

	/**
	 * Sets up the lighting registry for its first use. May not be called more than once.<br/>
	 * This attempts to load the lighting.dat file from the 'worlds' directory into memory.<br/>
	 * 
	 * Can throw an {@link IllegalStateException} if the material registry has already been setup.
	 * 
	 * @return StringMap of registered materials
	 */
	public static StringMap setupRegistry() {
		if (setup) {
			throw new IllegalStateException("Can not setup material registry twice!");
		}

		File serverItemMap = new File(new File(Spout.getEngine().getWorldFolder(), "worlds"), "lighting.dat");
		store.setFile(serverItemMap);
		if (serverItemMap.exists()) {
			store.load();
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
		int id = lightingRegistry.register(manager.getName());
		if (!lightingLookup[id].compareAndSet(null, manager)) {
			throw new IllegalArgumentException(lightingLookup[id].get() + " is already mapped to id: " + manager.getId() + "!");
		}

		nameLookup.put(formatName(manager.getName()), manager);
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
		return nameLookup.get(formatName(name));
	}
	
	/**
	 * Gets the LightingManager by a portion of its name. Case-insensitive.
	 *
	 * @param name to lookup
	 * @return LightingManager, or null if none found
	 */
	public static LightingManager<?> getContains(String name) {
		String formatName = formatName(name);
		Iterator<Entry<String, LightingManager<?>>> itr = nameLookup.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, LightingManager<?>> entry = itr.next();
			if (entry.getKey() != null && entry.getKey().contains(formatName)) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Returns a human legible name from the full LightingManager name.
	 * 
	 * This will strip out extra whitespace, strip any other spaces and replace with '_', and lowercase the LightingManager name.
	 *
	 * @param name
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
