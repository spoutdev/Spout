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

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.TIntSet;

/**
 * A simplistic map that supports a 3 bytes for keys, using a trove int int
 * hashmap in the backend.
 *
 * @author Afforess
 *
 */
public class TByteTripleIntHashMap {
	private TIntIntHashMap map;

	public TByteTripleIntHashMap() {
		map = new TIntIntHashMap(100);
	}

	public TByteTripleIntHashMap(int capacity) {
		map = new TIntIntHashMap(capacity);
	}

	public int put(byte key1, byte key2, byte key3, int value) {
		int key = key(key1, key2, key3);
		return map.put(key, value);
	}

	public int get(byte key1, byte key2, byte key3) {
		int key = key(key1, key2, key3);
		return map.get(key);
	}

	public boolean containsKey(byte key1, byte key2, byte key3) {
		int key = key(key1, key2, key3);
		return map.containsKey(key);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsValue(int val) {
		return map.containsValue(val);
	}

	public boolean increment(byte key1, byte key2, byte key3) {
		int key = key(key1, key2, key3);
		return map.increment(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TIntIntIterator iterator() {
		return map.iterator();
	}

	public TIntSet keySet() {
		return map.keySet();
	}

	public int[] keys() {
		return map.keys();
	}

	public int remove(byte key1, byte key2, byte key3) {
		int key = key(key1, key2, key3);
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public TIntCollection valueCollection() {
		return map.valueCollection();
	}

	public int[] values() {
		return map.values();
	}

	private static final int key(int x, int y, int z) {
		return (x & 0xF) << 11 | (z & 0xF) << 7 | y & 0x7F;
	}
}
