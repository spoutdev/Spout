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

import java.util.Collection;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;

/**
 * A simplistic map that supports a 3 21 bit integers for keys, using a trove
 * long Object hashmap in the backend. 1 bit is wasted.
 */
public class TInt21TripleObjectHashMap<K> {
	protected TLongObjectMap<K> map;

	/**
	 * Creates a new <code>TInt21TripleObjectHashMap</code> instance backend by a {@see TLongObjectHashMap} instance with an capacity of 100 and the default load factor.
	 */
	public TInt21TripleObjectHashMap() {
		map = new TLongObjectHashMap<K>(100);
	}

	/**
	 * Creates a new <code>TInt21TripleObjectHashMap</code> instance backend by a {@see TLongObjectHashMap} instance with a prime capacity equal to or greater than <code>capacity</code> and with the default load factor.
	 *
	 * @param capacity an <code>int</code> value
	 */
	public TInt21TripleObjectHashMap(int capacity) {
		map = new TLongObjectHashMap<K>(capacity);
	}

	/**
	 * Creates a new <code>TInt21TripleObjectHashMap</code> instance backend by <code>map</code>
	 *
	 * @param map
	 */
	public TInt21TripleObjectHashMap(TLongObjectMap<K> map) {
		if (map == null) {
			throw new IllegalArgumentException("The backend can not be null.");
		}

		this.map = map;
	}

	/**
	 * Associates the specified value with the specified key in this map (optional operation).
	 * If the map previously contained a mapping for the key, the old value is replaced by the specified value.
	 * (A map m is said to contain a mapping for a key k if and only if {@see #containsKey(int, int, int) m.containsKey(k)} would return <code>true</code>.)
	 *
	 * @see #key(int, int, int)
	 * @param x an <code>int</code> value
	 * @param y an <code>int</code> value
	 * @param z an <code>int</code> value
	 * @param value
	 * @return the previous value associated with <code>key(x, y, z)</code>, or no_entry_value if there was no mapping for <code>key(x, y, z)</code>.
	 * (A no_entry_value return can also indicate that the map previously associated <code>null</code> with key, if the implementation supports <code>null</code> values.)
	 */
	public K put(int x, int y, int z, K value) {
		long key = key(x, y, z);
		return map.put(key, value);
	}

	/**
	 * Returns the value to which the specified key is mapped, or <code>null</code> if this map contains no mapping for the key.
	 * <p/>
	 * More formally, if this map contains a mapping from a key <code>k</code> to a value <code>v</code> such that <code>(key==null ? k==null : key.equals(k))</code>, then this method returns <code>v</code>; otherwise it returns <code>null</code>.
	 * (There can be at most one such mapping.)
	 * <p/>
	 * If this map permits <code>null</code> values, then a return value of <code>null</code> does not <i>necessarily</i> indicate that the map contains no mapping for the key; it's also possible that the map explicitly maps the key to <code>null</code>.
	 * The {@see #containsKey(int, int, int) containsKey} operation may be used to distinguish these two cases.
	 *
	 * @see #key(int, int, int)
	 * @param x an <code>int</code> value
	 * @param y an <code>int</code> value
	 * @param z an <code>int</code> value
	 * @return the value to which the specified <code>key(x, y, z)</code> is mapped, or <code>null</code> if this map contains no mapping for the key.
	 */
	public K get(int x, int y, int z) {
		long key = key(x, y, z);
		return map.get(key);
	}

	/**
	 * Returns true if this map contains a mapping for the specified key.
	 * More formally, returns <code>true</code> if and only if this map contains a mapping for a key <code>k</code> such that <code>key.equals(k)</code>.
	 * (There can be at most one such mapping.)
	 *
	 * @see #key(int, int, int)
	 * @param x an <code>int</code> value
	 * @param y an <code>int</code> value
	 * @param z an <code>int</code> value
	 * @return <code>true</code> if this map contains a mapping for the specified <code>key(x, y, z)</code>.
	 */
	public boolean containsKey(int x, int y, int z) {
		long key = key(x, y, z);
		return map.containsKey(key);
	}

	/**
	 * Removes all of the mappings from this map (optional operation).
	 * The map will be empty after this call returns.
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * Returns <code>true</code> if this map contains a mapping for the specified key.
	 * More formally, returns <code>true</code> if and only if this map contains a mapping for a key <code>k</code> such that <code>key.equals(k)</code>.
	 * (There can be at most one such mapping.)
	 *
	 * @param val value whose presence in this map is to be tested
	 * @return <code>true</code> if this map maps one or more keys to the specified value
	 */
	public boolean containsValue(K val) {
		return map.containsValue(val);
	}

	/**
	 * Returns <code>true</code> if this map contains no key-value mappings.
	 *
	 * @return <code>true</code> if this map contains no key-value mappings.
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Returns a {@see TLongObjectIterator} with access to this map's keys and values.
	 *
	 * @return a {@see TLongObjectIterator} with access to this map's keys and values.
	 */
	public TLongObjectIterator<K> iterator() {
		return map.iterator();
	}

	/**
	 * Returns a {@see TLongSet} view of the keys contained in this map.
	 * The set is backed by the map, so changes to the map are reflected in the set, and vice-versa.
	 * If the map is modified while an iteration over the set is in progress (except through the iterator's own remove operation), the results of the iteration are undefined.
	 * The set supports element removal, which removes the corresponding mapping from the map, via the <code>Iterator.remove</code>, <code>Set.remove</code>, <code>removeAll</code>, <code>retainAll</code>, and <code>clear</code> operations.
	 * It does not support the add or addAll operations.
	 *
	 * @return a set view of the keys contained in this map.
	 */
	public TLongSet keySet() {
		return map.keySet();
	}

	/**
	 * Returns a copy of the keys of the map as an array.
	 * Changes to the array of keys will not be reflected in the map nor vice-versa.
	 *
	 * @return a copy of the keys of the map as an array.
	 */
	public long[] keys() {
		return map.keys();
	}

	/**
	 * Removes the mapping for a key from this map if it is present (optional operation).
	 * More formally, if this map contains a mapping from key <code>k</code> to value <code>v</code> such that <code>key.equals(k)</code>, that mapping is removed.
	 * (The map can contain at most one such mapping.)
	 * <p/>
	 * Returns the value to which this map previously associated the key, or <code>null</code> if the map contained no mapping for the key.
	 * </p>
	 * If this map permits null values, then a return value of <code>null</code> does not <i>necessarily</i> indicate that the map contained no mapping for the key; it's also possible that the map explicitly mapped the key to <code>null</code>.
	 * <p/>
	 * The map will not contain a mapping for the specified key once the call returns.
	 *
	 * @see #key(int, int, int)
	 * @param x an <code>int</code> value
	 * @param y an <code>int</code> value
	 * @param z an <code>int</code> value
	 * @return the previous <code>long</code> value associated with <code>key(x, y, z)</code>, or <code>null</code> if there was no mapping for key.
	 */
	public K remove(int x, int y, int z) {
		long key = key(x, y, z);
		return map.remove(key);
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 * If the map contains more than <code>Integer.MAX_VALUE</code> elements, returns <code>Integer.MAX_VALUE</code>.
	 *
	 * @return the number of key-value mappings in this map
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Returns a {@see Collection} view of the values contained in this map.
	 * The collection is backed by the map, so changes to the map are reflected in the collection, and vice-versa.
	 * If the map is modified while an iteration over the collection is in progress (except through the iterator's own remove operation), the results of the iteration are undefined.
	 * The collection supports element removal, which removes the corresponding mapping from the map, via the <code>Iterator.remove</code>, <code>Collection.remove</code>, <code>removeAll</code>, <code>retainAll</code> and <code>clear</code> operations.
	 * It does not support the <code>add</code> or <code>addAll</code> operations.
	 *
	 * @return
	 */
	public Collection<K> valueCollection() {
		return map.valueCollection();
	}

	/**
	 * Returns the values of the map as an array of <code>long</code> values.
	 * Changes to the array of values will not be reflected in the map nor vice-versa.
	 *
	 * @return the values of the map as an array of <code>long</code> values.
	 */
	public K[] values() {
		return map.values();
	}

	/**
	 * Returns the internal {@see TLongObjectMap}<code>&lt;K&gt;</code> instance.
	 *
	 * @return the internal {@see TLongObjectMap}<code>&lt;K&gt;</code> instance.
	 */
	protected TLongObjectMap<K> getInternalMap() {
		return map;
	}

	/**
	 * Packs the most significant and the twenty least significant of each int pinto a <code>long</code>
	 *
	 * @param x an <code>int</code> value
	 * @param y an <code>int</code> value
	 * @param z an <code>int</code> value
	 * @return the most significant and the twenty least significant of each int packed into a <code>long</code>
	 */
	protected static final long key(int x, int y, int z) {
		return ((long) ((x >> 11) & 0x100000 | x & 0xFFFFF)) << 42 | ((long) ((y >> 11) & 0x100000 | y & 0xFFFFF)) << 21 | ((z >> 11) & 0x100000 | z & 0xFFFFF);
	}
}
