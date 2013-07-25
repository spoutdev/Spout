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
package org.spout.api.util.map;

import java.util.HashMap;
import java.util.Map;

/**
 * A sub-class of TInt21TripleObjectHashMap that provides methods that support the cleanup of unused keys and simplistic value manipulation in a map-of-maps system.
 */
public class TInt21TripleObjectHashMapOfMaps<K, V> extends TInt21TripleObjectHashMap<Map<K, V>> {
	public V put(int x, int y, int z, K key, V value) {
		Map<K, V> get = super.get(x, y, z);
		if (get == null) {
			get = new HashMap<K, V>();
			super.put(x, y, z, get);
		}
		return get.put(key, value);
	}

	public V get(int x, int y, int z, K key) {
		Map<K, V> get = super.get(x, y, z);
		if (get == null) {
			return null;
		}
		return get.get(key);
	}

	public V remove(int x, int y, int z, K key) {
		Map<K, V> get = super.get(x, y, z);
		if (get == null) {
			return null;
		}
		V remove = get.remove(key);
		if (get.isEmpty()) {
			super.remove(x, y, z);
		}
		return remove;
	}
}
