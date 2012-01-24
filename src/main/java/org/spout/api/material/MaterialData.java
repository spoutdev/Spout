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
package org.spout.api.material;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spout.api.datatable.DatatableMap;
import org.spout.api.util.map.TIntPairObjectHashMap;

public class MaterialData {
	private final static ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	private final static TIntPairObjectHashMap<Material> idLookup = new TIntPairObjectHashMap<Material>(1000);
	private final static HashMap<String, Material> nameLookup = new HashMap<String, Material>(1000);
	
	private final static int MAX_SIZE = 1 << 16 - 1 << 13;
	
	/**
	 * Performs quick lookup of materials based on only their id.
	 */
	private static Material[] quickMaterialLookup = new Material[1000];

	/**
	 * Registers a material with the material lookup service
	 *
	 * @param item to add
	 */
	public static void registerMaterial(Material mat) {
		lock.writeLock().lock();
		try {
			int id = mat.getId();
			idLookup.put(id, mat.getData(), mat);
			nameLookup.put(mat.getName().toLowerCase(), mat);
			expandQuickLookups(id);
			quickMaterialLookup[id] = mat;
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	private static void expandQuickLookups(int id) {
		if (id > quickMaterialLookup.length){
			int newSize = Math.min(MAX_SIZE,id * 3 / 2);
			Material[] expanded = new Material[newSize];
			for (int i = 0; i < quickMaterialLookup.length; i++) {
				expanded[i] = quickMaterialLookup[i];
			}
			quickMaterialLookup = expanded;
		}
	}

	/**
	 *
	 * @param Gets the material from the given id
	 *
	 * @return material, or null if none found
	 */
	public static Material getMaterial(short id) {
		return getMaterial(id, (byte) 0);
	}

	/**
	 * Gets the material from the given id and data.
	 *
	 * If a non-zero data value is given for a material with no subtypes, the
	 * material at the id and data value of zero will be returned instead.
	 *
	 * @param id to get
	 * @param data to get
	 * @return material or null if none found
	 */
	public static Material getMaterial(short id, short data) {
		return getMaterial(id, data, null);
	}

	/**
	 * Gets the material from the given id and data and auxiliary data
	 *
	 * If a non-zero data value is given for a material with no subtypes, the
	 * material at the id and data value of zero will be returned instead.
	 *
	 * @param id to get
	 * @param data to get
	 * @return material or null if none found
	 */
	public static Material getMaterial(short id, short data, DatatableMap auxData) {
		// TODO - look at the aux data ?
		lock.readLock().lock();
		try {
			if (data == 0) {
				return quickMaterialLookup[id];
			}
			Material mat = idLookup.get(id, data);
			if (mat != null) {
				return mat;
			}
			return quickMaterialLookup[id];
		}
		finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Gets the block at the given id, or null if none found
	 *
	 * @param id to get
	 * @return block, or null if none found
	 */
	public static BlockMaterial getBlock(short id) {
		return getBlock(id, (short) 0);
	}

	/**
	 * Gets the block at the given id and data, or null if none found
	 *
	 * @param id to get
	 * @param data to get
	 * @return block, or null if none found
	 */
	public static BlockMaterial getBlock(short id, short data) {
		return getBlock(id, data, null);
	}

	/**
	 * Gets the block at the given id and data and auxiliary data, or null if none found
	 *
	 * @param id to get
	 * @param data to get
	 * @return block, or null if none found
	 */
	public static BlockMaterial getBlock(short id, short data, DatatableMap auxData) {
		// TODO - look at the aux data ?
		Material mat = getMaterial(id, data);
		if (mat instanceof BlockMaterial) {
			return (BlockMaterial) mat;
		}
		return null;
	}

	/**
	 * Gets the item at the given id, or null if none found
	 *
	 * @param id to get
	 * @return item or null if none found
	 */
	public static ItemMaterial getItem(short id) {
		return getItem(id, (short)0);
	}

	/**
	 * Gets the item at the given id and data, or null if none found
	 *
	 * @param id to get
	 * @param data to get
	 * @return item or null if none found
	 */
	public static ItemMaterial getItem(short id, short data) {
		Material mat = getMaterial(id, data);
		if (mat instanceof ItemMaterial) {
			return (ItemMaterial) mat;
		}
		return null;
	}

	/**
	 * Returns a list of all the current materials in the game
	 *
	 * @return a list of all materials
	 */
	public static Material[] getMaterials() {
		lock.readLock().lock();
		try {
			return idLookup.values();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Gets the associated material with it's name. Case-insensitive.
	 *
	 * @param name to lookup
	 * @return material, or null if none found
	 */
	public static Material getMaterial(String name) {
		lock.readLock().lock();
		try {
			return nameLookup.get(name.toLowerCase());
		}
		finally {
			lock.readLock().unlock();
		}
	}
}
