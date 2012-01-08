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
package org.spout.api.util.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.set.TIntSet;

/**
 * A simplistic map that supports a 3 bytes for keys, using a trove int double
 * hashmap in the backend.
 *
 * @author Afforess
 *
 */
public class TByteTripleDoubleHashMap {
	private TIntDoubleHashMap map;

	public TByteTripleDoubleHashMap() {
		map = new TIntDoubleHashMap(100);
	}

	public TByteTripleDoubleHashMap(int capacity) {
		map = new TIntDoubleHashMap(capacity);
	}

	public double put(byte key1, byte key2, byte key3, double value) {
		int key = key(key1, key2, key3);
		return map.put(key, value);
	}

	public double get(byte key1, byte key2, byte key3) {
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

	public boolean containsValue(double val) {
		return map.containsValue(val);
	}

	public boolean increment(byte key1, byte key2, byte key3) {
		int key = key(key1, key2, key3);
		return map.increment(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TIntDoubleIterator iterator() {
		return map.iterator();
	}

	public TIntSet keySet() {
		return map.keySet();
	}

	public int[] keys() {
		return map.keys();
	}

	public double remove(byte key1, byte key2, byte key3) {
		int key = key(key1, key2, key3);
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public TDoubleCollection valueCollection() {
		return map.valueCollection();
	}

	public double[] values() {
		return map.values();
	}

	private static final int key(int x, int y, int z) {
		return (x & 0xF) << 11 | (z & 0xF) << 7 | y & 0x7F;
	}
}
