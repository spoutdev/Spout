/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.io.store.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.Validate;

/**
 * This implements a SimpleStore that is stored in memory. It is not persisted between restarts.
 */
public class MemoryStoreMap<K, V> implements SimpleStoreMap<K, V> {
	private final Map<K, V> map;
	private final Map<V, K> reverseMap;

	public MemoryStoreMap() {
		map = new HashMap<>();
		reverseMap = new HashMap<>();
	}

	@Override
	public synchronized boolean save() {
		return true;
	}

	@Override
	public synchronized boolean load() {
		return true;
	}

	@Override
	public synchronized Collection<K> getKeys() {
		return map.keySet();
	}

	@Override
	public synchronized Collection<V> getValues() {
		return map.values();
	}

	@Override
	public synchronized Set<Entry<K, V>> getEntrySet() {
		return map.entrySet();
	}

	@Override
	public synchronized int getSize() {
		return map.size();
	}

	@Override
	public synchronized boolean clear() {
		map.clear();
		return true;
	}

	@Override
	public synchronized V get(K key) {
		return map.get(key);
	}

	@Override
	public synchronized K reverseGet(V value) {
		return reverseMap.get(value);
	}

	@Override
	public synchronized V get(K key, V def) {
		V value = get(key);
		if (value == null) {
			return def;
		}

		return value;
	}

	@Override
	public synchronized V remove(K key) {
		V value = map.remove(key);
		if (value != null) {
			reverseMap.remove(value);
		}
		return value;
	}

	@Override
	public synchronized V set(K key, V value) {
		Validate.notNull(key);
		Validate.notNull(value);

		V oldValue = map.put(key, value);
		if (oldValue != null) {
			reverseMap.remove(oldValue);
		}
		reverseMap.put(value, key);
		return oldValue;
	}

	@Override
	public synchronized boolean setIfAbsent(K key, V value) {
		if (map.get(key) != null) {
			return false;
		}

		if (reverseMap.get(value) != null) {
			return false;
		}

		set(key, value);
		return true;
	}
}
