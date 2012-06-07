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

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TIntSet;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spout.api.math.MathHelper;

/**
 * This is a synchronised version of the Trove IntObjectHashMap.
 *
 * Read/write locks are used to synchronise access.
 *
 * By default, it creates 16 sub-maps and there is a separate read/write lock
 * for each submap.
 *
 * @param <V> the value type
 */
public class TSyncIntObjectHashMap<V> implements TSyncIntObjectMap<V> {
	private final int mapCount;
	private final int mapMask;
	private final int hashScramble;
	private final ReadWriteLock[] lockArray;
	private final TIntObjectHashMap<V>[] mapArray;
	private final int no_entry_key;
	private final AtomicInteger totalKeys = new AtomicInteger(0);

	/**
	 * Creates a synchronised map based on the Trove int object map
	 */
	public TSyncIntObjectHashMap() {
		this(16);
	}

	/**
	 * Creates a synchronised map based on the Trove int object map
	 *
	 * @param mapCount the number of sub-maps
	 */
	public TSyncIntObjectHashMap(int mapCount) {
		this(mapCount, 32);
	}

	/**
	 * Creates a synchronised map based on the Trove int object map
	 *
	 * @param mapCount the number of sub-maps
	 * @param initialCapacity the initial capacity of the map
	 */
	public TSyncIntObjectHashMap(int mapCount, int initialCapacity) {
		this(mapCount, initialCapacity, 0.5F);
	}

	/**
	 * Creates a synchronised map based on the Trove int object map
	 *
	 * @param mapCount the number of sub-maps
	 * @param initialCapacity the initial capacity of the map
	 * @param loadFactor the load factor for the map
	 */
	public TSyncIntObjectHashMap(int mapCount, int initialCapacity, float loadFactor) {
		this(mapCount, initialCapacity, loadFactor, Constants.DEFAULT_INT_NO_ENTRY_VALUE);
	}

	/**
	 * Creates a synchronised map based on the Trove int object map
	 *
	 * @param mapCount the number of sub-maps
	 * @param initialCapacity the initial capacity of the map
	 * @param loadFactor the load factor for the map
	 * @param noEntryKey the key used to indicate a null key
	 */
	@SuppressWarnings("unchecked")
	public TSyncIntObjectHashMap(int mapCount, int initialCapacity, float loadFactor, int noEntryKey) {
		if (mapCount > 0x100000) {
			throw new IllegalArgumentException("Map count exceeds valid range");
		}
		mapCount = MathHelper.roundUpPow2(mapCount);
		mapMask = mapCount - 1;
		this.mapCount = mapCount;
		this.hashScramble = (mapCount << 8) + 1;
		mapArray = new TIntObjectHashMap[mapCount];
		lockArray = new ReadWriteLock[mapCount];
		for (int i = 0; i < mapCount; i++) {
			mapArray[i] = new TIntObjectHashMap<V>(initialCapacity / mapCount, loadFactor, noEntryKey);
			lockArray[i] = new ReentrantReadWriteLock();
		}
		this.no_entry_key = noEntryKey;
	}

	public void clear() {
		for (int m = 0; m < mapCount; m++) {
			clear(m);
		}
	}

	private void clear(int m) {
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			totalKeys.addAndGet(-mapArray[m].size());
			mapArray[m].clear();
		} finally {
			lock.unlock();
		}
	}

	public boolean containsKey(int key) {
		int m = mapHash(key);
		Lock lock = lockArray[m].readLock();
		lock.lock();
		try {
			return mapArray[m].containsKey(key);
		} finally {
			lock.unlock();
		}
	}

	public boolean containsValue(Object value) {
		for (int m = 0; m < mapCount; m++) {
			if (containsValue(m, value)) {
				return true;
			}
		}
		return false;
	}

	private boolean containsValue(int m, Object value) {
		Lock lock = lockArray[m].readLock();
		lock.lock();
		try {
			return mapArray[m].containsValue(value);
		} finally {
			lock.unlock();
		}
	}

	public boolean forEachEntry(TIntObjectProcedure<? super V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public boolean forEachKey(TIntProcedure arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public boolean forEachValue(TObjectProcedure<? super V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public V get(int key) {
		int m = mapHash(key);
		Lock lock = lockArray[m].readLock();
		lock.lock();
		try {
			return mapArray[m].get(key);
		} finally {
			lock.unlock();
		}
	}

	public int getNoEntryKey() {
		return no_entry_key;
	}

	public boolean isEmpty() {
		return totalKeys.get() == 0;
	}

	public TIntObjectIterator<V> iterator() {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public TIntSet keySet() {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public int[] keys(int[] dest) {
		for (int m = 0; m < mapCount; m++) {
			lockArray[m].readLock().lock();
		}
		try {
			int localSize = totalKeys.get();
			int[] keys;
			if (dest == null || dest.length < localSize) {
				keys = new int[localSize];
			} else {
				keys = dest;
			}
			int position = 0;
			for (int m = 0; m < mapCount; m++) {
				int[] mapKeys = mapArray[m].keys();
				for (int mapKey : mapKeys) {
					keys[position++] = mapKey;
				}
			}
			if (position != localSize) {
				throw new IllegalStateException("Key counter does not match actual total map size");
			}
			return keys;
		} finally {
			for (int m = 0; m < mapCount; m++) {
				lockArray[m].readLock().unlock();
			}
		}
	}

	public int[] keys() {
		return keys(null);
	}

	public V put(int key, V value) {
		int m = mapHash(key);
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			V previous = mapArray[m].put(key, value);
			if (previous == null && value != null) {
				totalKeys.incrementAndGet();
			}
			return previous;
		} finally {
			lock.unlock();
		}
	}

	// TODO - these two methods could be easily implemented
	public void putAll(Map<? extends Integer, ? extends V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public void putAll(TIntObjectMap<? extends V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public V putIfAbsent(int key, V value) {
		int m = mapHash(key);
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			V previous = mapArray[m].putIfAbsent(key, value);
			if (previous == null && value != null) {
				totalKeys.incrementAndGet();
			}
			return previous;
		} finally {
			lock.unlock();
		}
	}

	public V remove(int key) {
		int m = mapHash(key);
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			V previous = mapArray[m].remove(key);
			if (previous != null) {
				totalKeys.decrementAndGet();
			}
			return previous;
		} finally {
			lock.unlock();
		}
	}

	public boolean remove(int key, V value) {
		if (value == null) {
			throw new IllegalArgumentException("Cannot remove null values");
		}
		int m = mapHash(key);
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			V current = mapArray[m].get(key);
			if (current != value) {
				return false;
			}

			totalKeys.decrementAndGet();
			mapArray[m].remove(key);
			return true;
		} finally {
			lock.unlock();
		}
	}

	public boolean retainEntries(TIntObjectProcedure<? super V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public int size() {
		return totalKeys.get();
	}

	public void transformValues(TObjectFunction<V, V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	public Collection<V> valueCollection() {
		HashSet<V> collection = new HashSet<V>();
		for (int m = 0; m < mapCount; m++) {
			lockArray[m].readLock().lock();
		}
		try {
			for (int m = 0; m < mapCount; m++) {
				collection.addAll(mapArray[m].valueCollection());
			}
		} finally {
			for (int m = 0; m < mapCount; m++) {
				lockArray[m].readLock().unlock();
			}
		}
		return Collections.unmodifiableCollection(collection);
	}

	public V[] values() {
		return values(null);
	}

	@SuppressWarnings("unchecked")
	public V[] values(V[] dest) {
		for (int m = 0; m < mapCount; m++) {
			lockArray[m].readLock().lock();
		}
		try {
			int localSize = totalKeys.get();
			V[] values;
			if (dest == null) {
				values = (V[]) new Object[localSize];
			} else if (dest.length == localSize) {
				values = dest;
			} else {
				values = (V[]) Array.newInstance(dest.getClass().getComponentType(), localSize);
			}
			int position = 0;
			for (int m = 0; m < mapCount; m++) {
				V[] mapValues = mapArray[m].values();
				for (V mapValue : mapValues) {
					values[position++] = mapValue;
				}
			}
			if (position != localSize) {
				throw new IllegalStateException("Key counter does not match actual total map size");
			}
			return values;
		} finally {
			for (int m = 0; m < mapCount; m++) {
				lockArray[m].readLock().unlock();
			}
		}
	}

	private int mapHash(int key) {
		int intKey = (int) (key >> 32 ^ key);

		return (0x7FFFFFFF & intKey) % hashScramble & mapMask;
	}
}
