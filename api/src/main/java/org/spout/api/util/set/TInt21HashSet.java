/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.util.set;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

/**
 * A hash set that uses three 21bit integers as key, backed by a long trove hashset. 1 bit is wasted.
 */
public class TInt21HashSet {
	protected final TLongSet set;

	/**
	 * Creates a new <code>TInt21HashSet</code> instance backend by a {@see TLongHashSet} instance with an capacity of 100 and the default load factor.
	 */
	public TInt21HashSet() {
		set = new TLongHashSet(100);
	}

	/**
	 * Creates a new <code>TInt21HashSet</code> instance backend by a {@see TLongHashSet} instance with a prime capacity equal to or greater than <code>capacity</code> and with the default load factor.
	 *
	 * @param capacity an <code>int</code> value
	 */
	public TInt21HashSet(int capacity) {
		set = new TLongHashSet(capacity);
	}

	/**
	 * Creates a new <code>TInt21HashSet</code> instance backend by <code>set</code>
	 */
	public TInt21HashSet(TLongSet set) {
		if (set == null) {
			throw new IllegalArgumentException("The backend can not be null.");
		}

		this.set = set;
	}

	/**
	 * Insert <code>key(x, y, z)</code> into the backend set.
	 *
	 * @param x an <code>int</code> value
	 * @param y an <code>int</code> value
	 * @param z an <code>int</code> value
	 * @return <code>true</code> if the set was modified by the add operation
	 * @see #key(int, int, int)
	 */
	public boolean add(int x, int y, int z) {
		long key = key(x, y, z);
		return set.add(key);
	}

	/**
	 * Returns <code>true</code> if the backend set contains <code>key(x, y, z)</code>.
	 *
	 * @param x an <code>int</code> value
	 * @param y an <code>int</code> value
	 * @param z an <code>int</code> value
	 * @return <code>true</code> if the backend set contains <code>key(x, y, z)</code.
	 * @see #key(int, int, int)
	 */
	public boolean contains(int x, int y, int z) {
		long key = key(x, y, z);
		return set.contains(key);
	}

	/**
	 * Empties the set.
	 */
	public void clear() {
		set.clear();
	}

	/**
	 * Returns <code>true</code> if this set contains no elements.
	 *
	 * @return <code>true</code> if this set contains no elements.
	 */
	public boolean isEmpty() {
		return set.isEmpty();
	}

	/**
	 * Creates an iterator over the values of the set. The iterator supports element deletion.
	 *
	 * @return an <code>TLongIterator</code> value.
	 */
	public TLongIterator iterator() {
		return set.iterator();
	}

	/**
	 * Removes <code>key(x, y, z)</code>} from the backend set.
	 *
	 * @param x an <code>int</code> value
	 * @param y an <code>int</code> value
	 * @param z an <code>int</code> value
	 * @return true if the backend set was modified by the remove operation.
	 * @see #key(int, int, int)
	 */
	public boolean remove(int x, int y, int z) {
		long key = key(x, y, z);
		return set.remove(key);
	}

	/**
	 * Returns the number of elements in the backend set (its cardinality). If the backend set contains more than <code>Integer.MAX_VALUE</code> elements, returns <code>Integer.MAX_VALUE</code>.
	 *
	 * @return the number of elements in the backend set (its cardinality).
	 */
	public int size() {
		return set.size();
	}

	/**
	 * Returns an array containing all of the elements in the backend set. If the backend set makes any guarantees as to what order its elements are returned by its iterator, this method must return the
	 * elements in the same order. <p> The returned array will be "safe" in that no references to it are maintained by the backend set. (In other words, this method must allocate a new array even if the
	 * backend set is backed by an array). The caller is thus free to modify the returned array. <p> This method acts as bridge between array-based and collection-based APIs.
	 *
	 * @return an array containing all the elements in the backend set.
	 */
	public long[] toArray() {
		return set.toArray();
	}

	/**
	 * Packs the most significant and the twenty least significant of each int pinto a <code>long</code>
	 *
	 * @param x an <code>int</code> value
	 * @param y an <code>int</code> value
	 * @param z an <code>int</code> value
	 * @return the most significant and the twenty least significant of each int packed into a <code>long</code>
	 */
	protected static long key(int x, int y, int z) {
		return ((long) ((x >> 11) & 0x100000 | x & 0xFFFFF)) << 42 | ((long) ((y >> 11) & 0x100000 | y & 0xFFFFF)) << 21 | ((z >> 11) & 0x100000 | z & 0xFFFFF);
	}
}
