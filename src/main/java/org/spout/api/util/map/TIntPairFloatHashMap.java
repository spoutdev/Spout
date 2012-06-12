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

import gnu.trove.TFloatCollection;
import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.map.hash.TLongFloatHashMap;
import gnu.trove.set.TLongSet;

/**
 * A simplistic map that supports a pair of integers for keys, using a trove
 * long float hashmap in the backend.
 *
 * @author Afforess
 *
 */
public class TIntPairFloatHashMap extends IntPairHashed {
	protected final TLongFloatHashMap map;

	public TIntPairFloatHashMap() {
		map = new TLongFloatHashMap(100);
	}

	public TIntPairFloatHashMap(int capacity) {
		map = new TLongFloatHashMap(capacity);
	}

	public float put(int key1, int key2, float value) {
		return map.put(key(key1, key2), value);
	}

	public float get(int key1, int key2) {
		return map.get(key(key1, key2));
	}

	public boolean containsKey(int key1, int key2) {
		return map.containsKey(key(key1, key2));
	}

	public void clear() {
		map.clear();
	}

	public boolean containsValue(float val) {
		return map.containsValue(val);
	}

	public boolean increment(int key1, int key2) {
		return map.increment(key(key1, key2));
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TLongFloatIterator iterator() {
		return map.iterator();
	}

	public TLongSet keySet() {
		return map.keySet();
	}

	public long[] keys() {
		return map.keys();
	}

	public float remove(int key1, int key2) {
		return map.remove(key(key1, key2));
	}

	public int size() {
		return map.size();
	}

	public TFloatCollection valueCollection() {
		return map.valueCollection();
	}

	public float[] values() {
		return map.values();
	}
}
