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
package org.spout.api.map;

import java.io.Serializable;
import java.util.Map;

/**
 * An extension of the default Java.util Map interface, that allows
 * a default value to be returned when keys are not present in the map.
 */
public interface DefaultedMap<K, V> extends Map<K, V> {

	/**
	 * Returns the value to which the specified key is mapped,
	 * or the default value if this map contains no mapping for the key.
	 *
	 * @param key the key whose associated value is to be returned
	 * @param defaultValue to be returned if the key is not found
	 * @return the value the key is mapped to or the default value
	 */
	public <T extends V> T get(Object key, T defaultValue);

	/**
	 * Returns the value to which the String for the specified key is mapped,
	 * or the default value given by the key, if this map contains no mapping
	 * for the key.<br>
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value the key is mapped to or the default value
	 */
	public <T extends Serializable> T get(DefaultedKey<T> key);

	/**
	 * Associates the specified value with the String for the given key and returns
	 * the previous value, or null if there was no previous mapping
	 *
	 * @param key the key whose associated value is to be returned
	 * @param value the value the key is to be mapped
	 * @return the previous value, or null if none
	 */
	public <T extends Serializable> T put(DefaultedKey<T> key, T value);
	
	/**
	 * Associates the specified value with the String for the given key if there is no
	 * value associated with that key already and returns
	 * the previous value, or null if there was no previous mapping
	 *
	 * @param key the key whose associated value is to be returned
	 * @param value the value the key is to be mapped
	 * @return the previous value, or null if none
	 */
	public <T extends Serializable> T putIfAbsent(DefaultedKey<T> key, T value);
	
	/**
	 * Associates the specified value with the key if there is no
	 * value associated with that key already and returns
	 * the previous value, or null if there was no previous mapping
	 *
	 * @param key the key whose associated value is to be returned
	 * @param value the value the key is to be mapped
	 * @return the previous value, or null if none
	 */
	public V putIfAbsent(K key, V value);

}
