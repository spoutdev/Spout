/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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

import java.util.Collection;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;

/**
 * A simplistic map that supports a 3 21 bit integers for keys, using a trove
 * long Object hashmap in the backend. 1 bit is wasted.
 */
public class TInt21TripleObjectHashMap<K> {
	protected TLongObjectMap<K> map;

	public TInt21TripleObjectHashMap() {
		map = new TLongObjectHashMap<K>(100);
	}

	public TInt21TripleObjectHashMap(int capacity) {
		map = new TLongObjectHashMap<K>(capacity);
	}

	public TInt21TripleObjectHashMap(TLongObjectMap<K> map) {
		this.map = map;
	}

	public K put(int key1, int key2, int key3, K value) {
		long key = key(key1, key2, key3);
		return map.put(key, value);
	}

	public K get(int key1, int key2, int key3) {
		long key = key(key1, key2, key3);
		return map.get(key);
	}

	public boolean containsKey(int key1, int key2, int key3) {
		long key = key(key1, key2, key3);
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

	public K remove(int key1, int key2, int key3) {
		long key = key(key1, key2, key3);
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

	protected TLongObjectMap<K> getInternalMap() {
		return map;
	}

	protected static final long key(int x, int y, int z) {
		return ((long) x & 0x1FFFFF) << 42 | ((long) z & 0x1FFFFF) << 21 | (long) y & 0x1FFFFF;
	}
}
