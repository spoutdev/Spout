package org.getspout.api.material;

import java.util.HashMap;
import org.getspout.api.util.map.TIntPairObjectHashMap;

public class MaterialData {
	private final static TIntPairObjectHashMap<Material> idLookup = new TIntPairObjectHashMap<Material>(1000);
	private final static HashMap<String, Material> nameLookup = new HashMap<String, Material>(1000);

	/**
	 * Registers a material with the material lookup service
	 *
	 * @param item to add
	 */
	public static void registerMaterial(Material mat) {
		idLookup.put(mat.getRawId(), mat.getRawData(), mat);
		nameLookup.put(mat.getName().toLowerCase(), mat);
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
	public static Material getMaterial(short id, byte data) {
		Material mat = idLookup.get(id, data);
		if (mat != null) {
			return mat;
		}
		return idLookup.get(id, 0);
	}

	/**
	 * Gets the block at the given id, or null if none found
	 *
	 * @param id to get
	 * @return block, or null if none found
	 */
	public static BlockMaterial getBlock(short id) {
		return getBlock(id, (byte) 0);
	}

	/**
	 * Gets the block at the given id and data, or null if none found
	 *
	 * @param id to get
	 * @param data to get
	 * @return block, or null if none found
	 */
	public static BlockMaterial getBlock(short id, byte data) {
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
		return getItem(id, (byte) 0);
	}

	/**
	 * Gets the item at the given id and data, or null if none found
	 *
	 * @param id to get
	 * @param data to get
	 * @return item or null if none found
	 */
	public static ItemMaterial getItem(short id, byte data) {
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
		return idLookup.values();
	}

	/**
	 * Gets the associated material with it's name. Case-insensitive.
	 *
	 * @param name to lookup
	 * @return material, or null if none found
	 */
	public static Material getMaterial(String name) {
		return nameLookup.get(name.toLowerCase());
	}
}
