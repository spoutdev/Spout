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
package org.spout.api.util.set;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

/**
 * A hash set that uses three 21bit integers as key, backed by a long trove hashset. 1 bit is wasted.
 */
public class TInt21HashSet {
	protected TLongSet set;

	public TInt21HashSet() {
		set = new TLongHashSet(100);
	}

	public TInt21HashSet(int capacity) {
		set = new TLongHashSet(capacity);
	}

	public TInt21HashSet(TLongSet set) {
		this.set = set;
	}

	public boolean add(int key1, int key2, int key3) {
		long key = key(key1, key2, key3);
		return set.add(key);
	}

	public boolean contains(int key1, int key2, int key3) {
		long key = key(key1, key2, key3);
		return set.contains(key);
	}

	public void clear() {
		set.clear();
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	public TLongIterator iterator() {
		return set.iterator();
	}

	public boolean remove(int key1, int key2, int key3) {
		long key = key(key1, key2, key3);
		return set.remove(key);
	}

	public int size() {
		return set.size();
	}

	public long[] toArray() {
		return set.toArray();
	}

	protected static final long key(int x, int y, int z) {
		return (((long)x) & 0x1FFFFF) << 42 | (((long)z) & 0x1FFFFF) << 21 | ((long)y) & 0x1FFFFF;
	}
}
