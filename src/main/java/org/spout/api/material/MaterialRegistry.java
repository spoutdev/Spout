package org.spout.api.material;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class MaterialRegistry {
	private final static ConcurrentHashMap<String, Material> nameLookup = new ConcurrentHashMap<String, Material>(1000);
	private final static int MAX_SIZE = 1 << 16;
	@SuppressWarnings("unchecked")
	private final static AtomicReference<Material>[] materialLookup = new AtomicReference[MAX_SIZE];
	
	static {
		for (int i = 0; i < materialLookup.length; i++) {
			materialLookup[i] = new AtomicReference<Material>();
		}
	}

	/**
	 * Registers the material in the material lookup service
	 * 
	 * @param material to register
	 */
	public static <T extends Material> T register(T material) {		
		if (material.isSubMaterial()) {
			material.getParentMaterial().registerSubMaterial(material);
			nameLookup.put(material.getName().toLowerCase(), material);
			return material;
		} else {
			int id = material.getId();
			if (!materialLookup[id].compareAndSet(null, material)) {
				throw new IllegalArgumentException("Another material is already mapped to id: " + material.getId() + "!");
			} else {
				nameLookup.put(material.getName().toLowerCase(), material);
				return material;
			}
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
}
