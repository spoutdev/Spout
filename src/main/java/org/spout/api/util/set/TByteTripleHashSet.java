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
package org.spout.api.util.set;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 * A hash set that uses three 8 bit integers as key, backed by a int trove
 * hashset.
 */
public class TByteTripleHashSet {
	protected TIntSet set;

	public TByteTripleHashSet() {
		set = new TIntHashSet(100);
	}

	public TByteTripleHashSet(int capacity) {
		set = new TIntHashSet(capacity);
	}

	public TByteTripleHashSet(TIntSet set) {
		this.set = set;
	}

	public boolean add(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
		return set.add(key);
	}

	public boolean contains(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
		return set.contains(key);
	}

	public void clear() {
		set.clear();
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	public TIntIterator iterator() {
		return set.iterator();
	}

	public boolean remove(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
		return set.remove(key);
	}

	public int size() {
		return set.size();
	}

	public int[] toArray() {
		return set.toArray();
	}

	protected static final int key(int x, int y, int z) {
		return (x & 0xFF) << 16 | (z & 0xFF) << 8 | y & 0xFF;
	}

	public static final int key1(int key) {
		return key >> 16 & 0xFF;
	}

	public static final int key2(int key) {
		return key & 0xFF;
	}

	public static final int key3(int key) {
		return key >> 8 & 0xFF;
	}
}
