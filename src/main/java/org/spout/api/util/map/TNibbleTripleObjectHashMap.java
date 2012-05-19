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

import org.spout.api.util.hashing.TNibbleTripleHashed;

import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TShortObjectHashMap;
import gnu.trove.set.TShortSet;

/**
 * A simplistic map that supports a 3 nibbles (4 bits) for keys, using a trove
 * short object hashmap in the backend.
 */
public class TNibbleTripleObjectHashMap<K> extends TNibbleTripleHashed {
	protected TShortObjectMap<K> map;

	public TNibbleTripleObjectHashMap() {
		map = new TShortObjectHashMap<K>(100);
	}

	public TNibbleTripleObjectHashMap(int capacity) {
		map = new TShortObjectHashMap<K>(capacity);
	}

	public TNibbleTripleObjectHashMap(TShortObjectMap<K> map) {
		this.map = map;
	}

	public K put(byte key1, byte key2, byte key3, K value) {
		short key = key(key1, key2, key3);
		return map.put(key, value);
	}

	public K get(byte key1, byte key2, byte key3) {
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

	public boolean containsValue(K val) {
		return map.containsValue(val);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TShortObjectIterator<K> iterator() {
		return map.iterator();
	}

	public TShortSet keySet() {
		return map.keySet();
	}

	public short[] keys() {
		return map.keys();
	}

	public K remove(byte key1, byte key2, byte key3) {
		short key = key(key1, key2, key3);
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
}
