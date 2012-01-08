/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
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
package org.getspout.api.util.map;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.hash.TLongHashSet;

/**
 * A simplistic set that supports 2 ints for one value inside the set.
 *
 * @author Afforess
 *
 */
public class TIntPairHashSet {
	private TLongHashSet set;

	public TIntPairHashSet() {
		this(100);
	}

	public TIntPairHashSet(int capacity) {
		set = new TLongHashSet(capacity);
	}

	public TIntPairHashSet(TIntPairHashSet other) {
		set = new TLongHashSet(other.set);
	}

	public boolean add(int key1, int key2) {
		long key = (long) key1 << 32 | key2 & 0xFFFFFFFFL;
		return set.add(key);
	}

	public boolean contains(int key1, int key2) {
		long key = (long) key1 << 32 | key2 & 0xFFFFFFFFL;
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
	
	public void addAll(TIntPairHashSet other) {
		set.addAll(other.set);
	}

	public boolean remove(int key1, int key2) {
		long key = (long) key1 << 32 | key2 & 0xFFFFFFFFL;
		return set.remove(key);
	}

	public int size() {
		return set.size();
	}

	public static int longToKey1(long composite) {
		return (int) (composite >> 32 & 4294967295L);
	}

	public static int longToKey2(long composite) {
		return (int) (composite & 4294967295L);
	}

	public static long keysToLong(int key1, int key2) {
		return (long) key1 << 32 | key2 & 0xFFFFFFFFL;
	}
}
