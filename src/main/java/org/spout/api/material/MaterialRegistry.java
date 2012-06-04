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
package org.spout.api.material;

import java.io.File;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Spout;
import org.spout.api.io.store.simple.BinaryFileStore;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.math.MathHelper;
import org.spout.api.util.StringMap;

public abstract class MaterialRegistry {
	private final static ConcurrentHashMap<String, Material> nameLookup = new ConcurrentHashMap<String, Material>(1000);
	private final static int MAX_SIZE = 1 << 16;
	@SuppressWarnings("unchecked")
	private final static AtomicReference<Material>[] materialLookup = new AtomicReference[MAX_SIZE];
	private static boolean setup = false;
	private final static BinaryFileStore store = new BinaryFileStore();
	private final static StringMap materialRegistry = new StringMap(null, store, 1, Short.MAX_VALUE);;

	static {
		for (int i = 0; i < materialLookup.length; i++) {
			materialLookup[i] = new AtomicReference<Material>();
		}
	}
	
	/**
	 * Sets up the material registry for it's first use. May not be called more than once.
	 */
	public static StringMap setupRegistry() {
		if (!setup) {
			File serverItemMap = new File(new File(Spout.getEngine().getWorldFolder(), "worlds"), "materials.dat");
			store.setFile(serverItemMap);
			if (serverItemMap.exists()) {
				store.load();
			}
			setup = true;
			return materialRegistry;
		} else {
			throw new IllegalStateException("Can not setup material registry twice!");
		}
	}

	/**
	 * Registers the material in the material lookup service
	 * 
	 * @param material to register
	 */
	protected static int register(Material material) {
		if (material.isSubMaterial()) {
			material.getParentMaterial().registerSubMaterial(material);
			nameLookup.put(material.getName().toLowerCase(), material);
			return material.getParentMaterial().getId();
		} else {
			int id = materialRegistry.register(material.getName());
			if (!materialLookup[id].compareAndSet(null, material)) {
				throw new IllegalArgumentException(materialLookup[id].get() + " is already mapped to id: " + material.getId() + "!");
			} else {
				nameLookup.put(material.getName().toLowerCase(), material);
				return id;
			}
		}
	}
	
	/**
	 * Registers the material in the material lookup service
	 * 
	 * @param material to register
	 */
	protected static int register(Material material, int id) {
		materialRegistry.register(material.getName(), id);
		if (!materialLookup[id].compareAndSet(null, material)) {
			throw new IllegalArgumentException(materialLookup[id].get() + " is already mapped to id: " + material.getId() + "!");
		} else {
			nameLookup.put(material.getName().toLowerCase(), material);
			return id;
		}
	}

	/**
	 * Gets the material from the given id
	 *
	 * @param id to get
	 * @return material or null if none found
	 */
	public static Material get(short id) {
		if (id < 0 || id >= materialLookup.length) {
			return null;
		}
		return materialLookup[id].get();
	}
	
	/**
	 * Gets the material for the given BlockFullState
	 * 
	 * @param state the full state of the block
	 */
	public static Material get(BlockFullState state) {
		return get(state.getPacked());
	}
		
	/**
	 * Gets the material for the given packed full state
	 * 
	 * @param state the full state of the block
	 */
	public static Material get(int packedState) {
		short id = BlockFullState.getId(packedState);
		if (id < 0 || id >= materialLookup.length) {
			return null;
		}
		Material material = materialLookup[id].get();
		if (material == null) {
			return null;
		} else {
			return material.getSubMaterial(BlockFullState.getData(packedState));
		}
	}

	/**
	 * Returns all current materials in the game
	 *
	 * @return an array of all materials
	 */
	public static Material[] values() {
		//TODO: This is wrong, need to count # of registered materials
		HashSet<Material> set = new HashSet<Material>(1000);
		for (int i = 0; i < materialLookup.length; i++) {
			if (materialLookup[i].get() != null) {
				set.add(materialLookup[i].get());
			}
		}
		return set.toArray(new Material[0]);

	}

	/**
	 * Gets the associated material with it's name. Case-insensitive.
	 *
	 * @param name to lookup
	 * @return material, or null if none found
	 */
	public static Material get(String name) {
		return nameLookup.get(name.toLowerCase());
	}

	/**
	 * Gets the minimum data mask required to account for all sub-materials of the material
	 * 
	 * @param m the material
	 * @return the minimum data mask
	 */
	public static short getMinimumDatamask(Material m) {
		Material root = m;
		while (root.isSubMaterial()) {
			root = m.getParentMaterial();
		}

		if (root.getData() != 0) {
			throw new IllegalStateException("Root materials must have data set to zero");
		}
		Material[] subMaterials = root.getSubMaterials();

		short minimumMask = 0;

		for (Material sm : subMaterials) {
			minimumMask |= sm.getData() & 0xFFFF;
		}

		minimumMask = (short) (MathHelper.roundUpPow2(minimumMask + 1) - 1);

		return minimumMask;
	}
}
