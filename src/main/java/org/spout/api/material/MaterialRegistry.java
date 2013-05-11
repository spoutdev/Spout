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
package org.spout.api.material;

import java.io.File;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Spout;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.io.store.simple.BinaryFileStore;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.math.IntVector3;
import org.spout.api.math.GenericMath;
import org.spout.api.util.StringMap;

/**
 * Handles all registered materials on the server statically.
 *
 */
public abstract class MaterialRegistry {
	private final static ConcurrentHashMap<String, Material> nameLookup = new ConcurrentHashMap<String, Material>(1000);
	private final static int MAX_SIZE = 1 << 16;
	@SuppressWarnings("unchecked")
	private final static AtomicReference<Material[]>[] materialLookup = new AtomicReference[MAX_SIZE];
	private static boolean setup = false;
	private final static BinaryFileStore store = new BinaryFileStore();
	private final static StringMap materialRegistry = new StringMap(null, store, 1, Short.MAX_VALUE, Material.class.getName());
	private final static Material[] NULL_MATERIAL_ARRAY = new Material[] {null};

	static {
		for (int i = 0; i < materialLookup.length; i++) {
			materialLookup[i] = new AtomicReference<Material[]>();
			materialLookup[i].set(NULL_MATERIAL_ARRAY);
		}
	}

	/**
	 * Sets up the material registry for its first use. May not be called more than once.<br/>
	 * This attempts to load the materials.dat file from the 'worlds' directory into memory.<br/>
	 * 
	 * Can throw an {@link IllegalStateException} if the material registry has already been setup.
	 * 
	 * @return StringMap of registered materials
	 */
	public static StringMap setupRegistry() {
		if (setup) {
			throw new IllegalStateException("Can not setup material registry twice!");
		}

		File serverItemMap = new File(new File(Spout.getEngine().getWorldFolder(), "worlds"), "materials.dat");
		store.setFile(serverItemMap);
		if (serverItemMap.exists()) {
			store.load();
		}
		setup = true;
		return materialRegistry;
	}

	/**
	 * Registers the material in the material lookup service
	 *
	 * @param material to register
	 * @return id of the material registered
	 */
	protected static int register(Material material) {
		testEffectRanges(material);
		if (material.isSubMaterial()) {
			material.getParentMaterial().registerSubMaterial(material);
			nameLookup.put(formatName(material.getDisplayName()), material);
			return material.getParentMaterial().getId();
		} else {
			int id = materialRegistry.register(material.getName());
			Material[] subArray = new Material[] {material};
			if (!materialLookup[id].compareAndSet(NULL_MATERIAL_ARRAY, subArray)) {
				throw new IllegalArgumentException(materialLookup[id].get() + " is already mapped to id: " + material.getId() + "!");
			}

			nameLookup.put(formatName(material.getDisplayName()), material);
			return id;
		}
	}
	
	protected static AtomicReference<Material[]> getSubMaterialReference(short id) {
		return materialLookup[id];
	}

	/**
	 * Registers the material in the material lookup service
	 *
	 * @param material to register
	 * @return id of the material registered.
	 */
	protected static int register(Material material, int id) {
		materialRegistry.register(material.getName(), id);
		Material[] subArray = new Material[] {material};
		if (!materialLookup[id].compareAndSet(NULL_MATERIAL_ARRAY, subArray)) {
			throw new IllegalArgumentException(materialLookup[id].get()[0] + " is already mapped to id: " + material.getId() + "!");
		}

		nameLookup.put(formatName(material.getName()), material);
		return id;
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
		return materialLookup[id].get()[0];
	}
	
	/**
	 * Gets the material from the given id and data
	 *
	 * @param id to get
	 * @param data to get
	 * @return material or null if none found
	 */
	public static Material get(short id, short data) {
		if (id < 0 || id >= materialLookup.length) {
			return null;
		}
		Material[] parent = materialLookup[id].get();
		if (parent[0] == null) {
			return null;
		}
		
		data &= parent[0].getDataMask();
		return materialLookup[id].get()[data];
	}

	/**
	 * Gets the material for the given BlockFullState
	 *
	 * @param state the full state of the block
	 * @return Material of the BlockFullState
	 */
	public static Material get(BlockFullState state) {
		return get(state.getPacked());
	}

	/**
	 * Gets the material for the given packed full state
	 *
	 * @param state the full state of the block
	 * @return Material of the id
	 */
	public static BlockMaterial get(int packedState) {
		short id = BlockFullState.getId(packedState);
		if (id < 0 || id >= materialLookup.length) {
			return null;
		}
		Material[] material = materialLookup[id].get();
		if (material[0] == null) {
			return null;
		}
		return (BlockMaterial) material[BlockFullState.getData(packedState) & (material[0].getDataMask())];
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
				set.add(materialLookup[i].get()[0]);
			}
		}
		return set.toArray(new Material[0]);

	}

	/**
	 * Gets the associated material with its name. Case-insensitive.
	 *
	 * @param name to lookup
	 * @return material, or null if none found
	 */
	public static Material get(String name) {
		return nameLookup.get(formatName(name));
	}

	/**
	 * Returns a human legible material name from the full material.
	 * 
	 * This will strip any '_' and replace with spaces, strip out extra whitespace, and lowercase the material name.
	 *
	 * @param matName
	 * @return human legible name of the material.
	 */
	private static String formatName(String matName) {
		return matName.trim().replaceAll(" ", "_").toLowerCase();
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

		if (m.hasLSBDataMask()) {
			minimumMask = (short) (GenericMath.roundUpPow2(minimumMask + 1) - 1);
		}

		return minimumMask;
	}
	
	private static void testEffectRanges(Material m) {
		if (m instanceof DynamicMaterial) {
			testEffectRange(m, ((DynamicMaterial) m).getDynamicRange(), "Dynamic effect range");
		}
		if (m instanceof BlockMaterial) {
			testEffectRange(m, ((BlockMaterial) m).getMaximumPhysicsRange((short) 0), "Physics maximum effect range");
		}
	}
	
	private static void testEffectRange(Material m, Iterable<IntVector3> range, String rangeType) {
		if (range == null) {
			throw new NullPointerException(rangeType + " must be set before registering material");
		}
		int maxRange = Region.BLOCKS.SIZE;
		for (IntVector3 i : range) {
			int dx = i.getX();
			int dy = i.getY();
			int dz = i.getZ();
			if (dx < -maxRange || dx > maxRange || dy < -maxRange || dy > maxRange || dz < -maxRange || dz > maxRange) {
				throw new UnsupportedOperationException(rangeType + " for material " + m + " is more than 1 Region away for the source block " + dx + ", " + dy + ", " + dz);
			}
		}
	}
}
