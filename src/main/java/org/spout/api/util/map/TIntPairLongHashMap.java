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

import org.spout.api.util.hashing.IntPairHashed;

import gnu.trove.TLongCollection;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.set.TLongSet;

/**
 * A simplistic map that supports a pair of integers for keys, using a trove
 * long long hashmap in the backend.
 *
 * @author Afforess
 *
 */
public class TIntPairLongHashMap extends IntPairHashed {
	private TLongLongHashMap map;

	public TIntPairLongHashMap() {
		map = new TLongLongHashMap(100);
	}

	public TIntPairLongHashMap(int capacity) {
		map = new TLongLongHashMap(capacity);
	}

	public long put(int key1, int key2, long value) {
		long key = (long) key1 << 32 | key2 & 0xFFFFFFFFL;
		return map.put(key, value);
	}

	public long get(int key1, int key2) {
		long key = (long) key1 << 32 | key2 & 0xFFFFFFFFL;
		return map.get(key);
	}

	public boolean containsKey(int key1, int key2) {
		long key = (long) key1 << 32 | key2 & 0xFFFFFFFFL;
		return map.containsKey(key);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsValue(long val) {
		return map.containsValue(val);
	}

	public boolean increment(int key1, int key2) {
		long key = (long) key1 << 32 | key2 & 0xFFFFFFFFL;
		return map.increment(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TLongLongIterator iterator() {
		return map.iterator();
	}

	public TLongSet keySet() {
		return map.keySet();
	}

	public long[] keys() {
		return map.keys();
	}

	public long remove(int key1, int key2) {
		long key = (long) key1 << 32 | key2 & 0xFFFFFFFFL;
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public TLongCollection valueCollection() {
		return map.valueCollection();
	}

	public long[] values() {
		return map.values();
	}
}
