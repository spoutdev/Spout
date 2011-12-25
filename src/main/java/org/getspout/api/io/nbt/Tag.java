package org.getspout.api.io.nbt;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Represents a single NBT tag.
 * @author Graham Edgecombe
 */
public abstract class Tag {
	/**
	 * The name of this tag.
	 */
	private final String name;

	/**
	 * Creates the tag with no name.
	 */
	public Tag() {
		this("");
	}

	/**
	 * Creates the tag with the specified name.
	 * @param name The name.
	 */
	public Tag(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of this tag.
	 * @return The name of this tag.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets the value of this tag.
	 * @return The value of this tag.
	 */
	public abstract Object getValue();
	
	/**
	 * Clones a Map<String, Tag>
	 * 
	 * @param map the map
	 * @return a clone of the map
	 */
	public static Map<String, Tag> cloneMap(Map<String, Tag> map) {
		Map<String, Tag> newMap = new HashMap<String, Tag>();
		for (Entry<String, Tag> entry : map.entrySet()) {
			newMap.put(entry.getKey(), entry.getValue().clone());
		}
		return newMap;
	}
	
	/**
	 * Clones the Tag
	 * 
	 * @return the clone
	 */
	public abstract Tag clone();
}
