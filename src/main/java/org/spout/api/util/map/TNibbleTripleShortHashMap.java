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

import gnu.trove.TShortCollection;
import gnu.trove.iterator.TShortShortIterator;
import gnu.trove.map.TShortShortMap;
import gnu.trove.map.hash.TShortShortHashMap;
import gnu.trove.set.TShortSet;

/**
 * A simplistic map that supports a 3 nibbles (4 bits) for keys, using a trove
 * short short hashmap in the backend.
 */
public class TNibbleTripleShortHashMap {
	protected TShortShortMap map;

	public TNibbleTripleShortHashMap() {
		map = new TShortShortHashMap(100);
	}

	public TNibbleTripleShortHashMap(int capacity) {
		map = new TShortShortHashMap(capacity);
	}

	public TNibbleTripleShortHashMap(TShortShortMap map) {
		this.map = map;
	}

	public short put(byte key1, byte key2, byte key3, short value) {
		short key = key(key1, key2, key3);
		return map.put(key, value);
	}

	public short get(byte key1, byte key2, byte key3) {
		short key = key(key1, key2, key3);
		return map.get(key);
	}

	public boolean containsKey(byte key1, byte key2, byte key3) {
		short key = key(key1, key2, key3);
		return map.containsKey(key);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsValue(short val) {
		return map.containsValue(val);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TShortShortIterator iterator() {
		return map.iterator();
	}

	public TShortSet keySet() {
		return map.keySet();
	}

	public short[] keys() {
		return map.keys();
	}

	public short remove(byte key1, byte key2, byte key3) {
		short key = key(key1, key2, key3);
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public TShortCollection valueCollection() {
		return map.valueCollection();
	}

	public short[] values() {
		return map.values();
	}

	protected static final short key(byte x, byte y, byte z) {
		return (short) ((x & 0xF) << 8 | (y & 0xF) << 4 | z & 0xF);
	}
}
