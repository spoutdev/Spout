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
package org.spout.api.datatable;

import java.io.IOException;
import java.io.Serializable;

import org.spout.api.map.DefaultedMap;

public interface SerializableMap extends DefaultedMap<String, Serializable> {

	/**
	 * Serializes the information in this map into an array of bytes.
	 * 
	 * This information can be used to reconstruct a deep copy of the map, or for persistence.
	 * 
	 * @return serialized bytes
	 */
	public byte[] serialize();

	/**
	 * Deserializes the array of information into the contents of the map.
	 * This will wipe all previous data in the map.
	 * 
	 * @throws IOException if an error in deserialization occurred
	 * @param data to deserialize
	 */
	public void deserialize(byte[] data) throws IOException;

	/**
	 * Deserializes the array of information into the contents of the map.
	 * 
	 * @throws IOException if an error in deserialization occurred
	 * @param data to deserialize
	 * @param wipe true if the previous data in the map should be wiped
	 */
	public void deserialize(byte[] data, boolean wipe) throws IOException;

	/**
	 * Returns a deep copy of this map
	 * 
	 * @return deep copy
	 */
	public SerializableMap deepCopy();

	 /**
	  * Returns the value to which the specified key is mapped,
	  * or {@code null} if this map contains no mapping for the key, 
	  * or the value is not a type or subtype of the given class.

	  * @param key the key whose associated value is to be returned
	  * @return the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key
	  */
	public <T> T get(String key, Class<T> clazz);
}
