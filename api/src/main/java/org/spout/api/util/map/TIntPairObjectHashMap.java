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

import java.util.Collection;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;

import org.spout.api.util.hashing.IntPairHashed;

/**
 * A simplistic map that supports a pair of integers for keys, using a trove long object hashmap in the backend.
 */
public class TIntPairObjectHashMap<K> extends IntPairHashed {
	protected TLongObjectMap<K> map;

	public TIntPairObjectHashMap() {
		map = new TLongObjectHashMap<>(100);
	}

	public TIntPairObjectHashMap(int capacity) {
		map = new TLongObjectHashMap<>(capacity);
	}

	public K put(int key1, int key2, K value) {
		long key = key(key1, key2);
		return map.put(key, value);
	}

	public K get(int key1, int key2) {
		long key = key(key1, key2);
		return map.get(key);
	}

	public boolean containsKey(int key1, int key2) {
		long key = key(key1, key2);
		return map.containsKey(key);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsValue(K val) {
		return map.containsValue(val);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TLongObjectIterator<K> iterator() {
		return map.iterator();
	}

	public TLongSet keySet() {
		return map.keySet();
	}

	public long[] keys() {
		return map.keys();
	}

	public K remove(int key1, int key2) {
		long key = key(key1, key2);
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection<K> valueCollection() {
		return map.valueCollection();
	}

	@SuppressWarnings ("unchecked")
	public K[] values() {
		return (K[]) map.values();
	}
}
