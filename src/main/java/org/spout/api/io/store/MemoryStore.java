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
package org.spout.api.io.store;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.Validate;

/**
 * This implements a SimpleStore that is stored in memory. It is not persisted
 * between restarts.
 */

public class MemoryStore<T> implements SimpleStore<T> {

	private final Map<String, T> map;
	private final Map<T, String> reverseMap;

	public MemoryStore() {
		map = new HashMap<String, T>();
		reverseMap = new HashMap<T, String>();
	}

	public synchronized boolean save() {
		return true;
	}

	public synchronized boolean load() {
		return true;
	}

	public synchronized Collection<String> getKeys() {
		return map.keySet();
	}

	public synchronized Set<Entry<String, T>> getEntrySet() {
		return map.entrySet();
	}

	public synchronized int getSize() {
		return map.size();
	}

	public synchronized boolean clear() {
		map.clear();
		return true;
	}

	public synchronized T get(String key) {
		return map.get(key);
	}

	public synchronized String reverseGet(T value) {
		return reverseMap.get(value);
	}

	public synchronized T get(String key, T def) {
		T value = get(key);
		if (value == null) {
			return def;
		} else {
			return value;
		}
	}

	public synchronized T remove(String key) {
		T value = map.remove(key);
		if (value != null) {
			reverseMap.remove(value);
		}
		return value;
	}

	public synchronized T set(String key, T value) {
		Validate.notNull(key);
		Validate.notNull(value);
		
		T oldValue = map.put(key, value);
		if (oldValue != null) {
			reverseMap.remove(oldValue);
		}
		reverseMap.put(value, key);
		return oldValue;
	}
	
	public synchronized boolean setIfAbsent(String key, T value) {
		if (map.get(key) != null || reverseMap.get(value) != null) {
			return false;
		} else {
			set(key, value);
			return true;
		}
	}

}
