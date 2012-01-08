/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.io.nbt;

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
