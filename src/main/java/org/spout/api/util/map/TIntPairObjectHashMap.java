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
package org.spout.api.util.map;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;

import java.util.Collection;

/**
 * A simplistic map that supports a pair of integers for keys, using a trove
 * long object hashmap in the backend.
 *
 * @author Afforess
 *
 */
public class TIntPairObjectHashMap<K> {
	protected TLongObjectMap<K> map;

	public TIntPairObjectHashMap() {
		map = new TLongObjectHashMap<K>(100);
	}

	public TIntPairObjectHashMap(int capacity) {
		map = new TLongObjectHashMap<K>(capacity);
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

	public K[] values() {
		return map.values();
	}
	
	/**
	 * Creates a long key from 2 ints
	 *
	 * @param key1 an <code>int</code> value
	 * @param key2 an <code>int</code> value
	 * @return a long which is the concatenation of key1 and key2
	 */
	protected static final long key(int key1, int key2) {
		return (long) key1 << 32 | key2 & 0xFFFFFFFFL;
	}
}
