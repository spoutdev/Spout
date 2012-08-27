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
package org.spout.api.util.map.concurrent;

import org.spout.api.util.map.TIntPairObjectHashMap;

/**
 * A simplistic map that supports 2 integers for keys, using a trove
 * long Object hashmap in the backend.
 *
 * This map is backed by a read/write lock synchronised map.
 *
 * @param <K> the value type
 */
public class TSyncIntPairObjectHashMap<K> extends TIntPairObjectHashMap<K> {
	/**
	 * Creates a new <code>TSyncIntPairObjectHashMap</code> instance backend by a synchronized (thread-safe) {@see TSyncLongObjectHashMap} instance with an capacity of 100 and the default load factor.
	 */
	public TSyncIntPairObjectHashMap() {
		map = new TSyncLongObjectHashMap<K>(100);
	}

	/**
	 * Creates a new <code>TSyncIntPairObjectHashMap</code> instance backend by a synchronized (thread-safe) {@see TSyncLongObjectHashMap} instance with a prime capacity equal to or greater than <code>capacity</code> and with the default load factor.
	 *
	 * @param capacity an <code>int</code> value
	 */
	public TSyncIntPairObjectHashMap(int capacity) {
		map = new TSyncLongObjectHashMap<K>(capacity);
	}

	/**
	 * Creates a new <code>TSyncIntPairObjectHashMap</code> instance backend by <code>map</code>
	 *
	 * @param map
	 */
	public TSyncIntPairObjectHashMap(TSyncLongObjectMap<K> map) {
		if (map == null) {
			throw new IllegalArgumentException("The backend can not be null.");
		}
		this.map = map;
	}

	/**
	 * Removes a {@see #key(int, int) key}/value pair from the map, but only if <code>key(x, z)</code> is mapped to a given value
	 *
	 * @see #key(int, int, int)
	 * @param x an <code>int</code> value
	 * @param z an <code>int</code> value
	 * @param value the expected value
	 * @return <code>true</code> if on success
	 */
	public boolean remove(int x, int z, K value) {
		long key = key(x, z);
		return ((TSyncLongObjectHashMap<K>) map).remove(key, value);
	}

	/**
	 * Inserts a {@see #key(int, int) key}/value pair into the map if the specified <code>key(x, z)</code> is not already associated with a value.
	 *
	 * @see #key(int, int, int)
	 * @param x an <code>int</code> value
	 * @param z an <code>int</code> value
	 * @param value an <code>V</code> value to be associated with the specified key
	 * @return the previous value associated with <code>key(x, y, z)</code>, or <code>null</code> if none was found.
	 */
	public K putIfAbsent(int x, int z, K value) {
		long key = key(x, z);
		return ((TSyncLongObjectHashMap<K>) map).putIfAbsent(key, value);
	}
}
