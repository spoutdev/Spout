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
package org.spout.api.util.map.concurrent;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;

import org.spout.api.math.GenericMath;

/**
 * This is a synchronised version of the Trove LongObjectHashMap.
 *
 * Read/write locks are used to synchronise access.
 *
 * By default, it creates 16 sub-maps and there is a separate read/write lock for each submap.
 *
 * @param <V> the value type
 */
public class TSyncLongObjectHashMap<V> implements TSyncLongObjectMap<V> {
	private final int mapCount;
	private final int mapMask;
	private final int hashScramble;
	private final ReadWriteLock[] lockArray;
	private final TLongObjectHashMap<V>[] mapArray;
	private final long no_entry_key;
	private final AtomicInteger totalKeys = new AtomicInteger(0);

	/**
	 * Creates a synchronised map based on the Trove long object map
	 */
	public TSyncLongObjectHashMap() {
		this(16);
	}

	/**
	 * Creates a synchronised map based on the Trove long object map
	 *
	 * @param mapCount the number of sub-maps
	 */
	public TSyncLongObjectHashMap(int mapCount) {
		this(mapCount, 32);
	}

	/**
	 * Creates a synchronised map based on the Trove long object map
	 *
	 * @param mapCount the number of sub-maps
	 * @param initialCapacity the initial capacity of the map
	 */
	public TSyncLongObjectHashMap(int mapCount, int initialCapacity) {
		this(mapCount, initialCapacity, 0.5F);
	}

	/**
	 * Creates a synchronised map based on the Trove long object map
	 *
	 * @param mapCount the number of sub-maps
	 * @param initialCapacity the initial capacity of the map
	 * @param loadFactor the load factor for the map
	 */
	public TSyncLongObjectHashMap(int mapCount, int initialCapacity, float loadFactor) {
		this(mapCount, initialCapacity, loadFactor, Constants.DEFAULT_LONG_NO_ENTRY_VALUE);
	}

	/**
	 * Creates a synchronised map based on the Trove long object map
	 *
	 * @param mapCount the number of sub-maps
	 * @param initialCapacity the initial capacity of the map
	 * @param loadFactor the load factor for the map
	 * @param noEntryKey the key used to indicate a null key
	 */
	@SuppressWarnings ("unchecked")
	public TSyncLongObjectHashMap(int mapCount, int initialCapacity, float loadFactor, long noEntryKey) {
		if (mapCount > 0x100000) {
			throw new IllegalArgumentException("Map count exceeds valid range");
		}
		mapCount = GenericMath.roundUpPow2(mapCount);
		mapMask = mapCount - 1;
		this.mapCount = mapCount;
		this.hashScramble = (mapCount << 8) + 1;
		mapArray = new TLongObjectHashMap[mapCount];
		lockArray = new ReadWriteLock[mapCount];
		for (int i = 0; i < mapCount; i++) {
			mapArray[i] = new TLongObjectHashMap<>(initialCapacity / mapCount, loadFactor, noEntryKey);
			lockArray[i] = new ReentrantReadWriteLock();
		}
		this.no_entry_key = noEntryKey;
	}

	@Override
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

	@Override
	public boolean containsKey(long key) {
		int m = mapHash(key);
		Lock lock = lockArray[m].readLock();
		lock.lock();
		try {
			return mapArray[m].containsKey(key);
		} finally {
			lock.unlock();
		}
	}

	@Override
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

	@Override
	public boolean forEachEntry(TLongObjectProcedure<? super V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	@Override
	public boolean forEachKey(TLongProcedure arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	@Override
	public boolean forEachValue(TObjectProcedure<? super V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	@Override
	public V get(long key) {
		int m = mapHash(key);
		Lock lock = lockArray[m].readLock();
		lock.lock();
		try {
			return mapArray[m].get(key);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public long getNoEntryKey() {
		return no_entry_key;
	}

	@Override
	public boolean isEmpty() {
		return totalKeys.get() == 0;
	}

	@Override
	public TLongObjectIterator<V> iterator() {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	@Override
	public TLongSet keySet() {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	@Override
	public long[] keys(long[] dest) {
		for (int m = 0; m < mapCount; m++) {
			lockArray[m].readLock().lock();
		}
		try {
			int localSize = totalKeys.get();
			long[] keys;
			if (dest == null || dest.length < localSize) {
				keys = new long[localSize];
			} else {
				keys = dest;
			}
			int position = 0;
			for (int m = 0; m < mapCount; m++) {
				long[] mapKeys = mapArray[m].keys();
				for (long mapKey : mapKeys) {
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

	@Override
	public long[] keys() {
		return keys(null);
	}

	@Override
	public V put(long key, V value) {
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
	@Override
	public void putAll(Map<? extends Long, ? extends V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	@Override
	public void putAll(TLongObjectMap<? extends V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	@Override
	public V putIfAbsent(long key, V value) {
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

	@Override
	public V remove(long key) {
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

	@Override
	public boolean remove(long key, V value) {
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

	@Override
	public boolean retainEntries(TLongObjectProcedure<? super V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	@Override
	public int size() {
		return totalKeys.get();
	}

	@Override
	public void transformValues(TObjectFunction<V, V> arg0) {
		throw new UnsupportedOperationException("This operation is not supported");
	}

	@Override
	public Collection<V> valueCollection() {
		HashSet<V> collection = new HashSet<>();
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

	@Override
	public Object[] values() {
		for (int m = 0; m < mapCount; m++) {
			lockArray[m].readLock().lock();
		}
		try {
			int localSize = totalKeys.get();
			Object[] values = new Object[size()];
			int position = 0;
			for (int m = 0; m < mapCount; m++) {
				Object[] mapValues = mapArray[m].values();
				for (Object mapValue : mapValues) {
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

	@Override
	@SuppressWarnings ("unchecked")
	public V[] values(V[] dest) {
		if (dest == null) {
			throw new IllegalArgumentException("Destination array must not be null");
		}
		for (int m = 0; m < mapCount; m++) {
			lockArray[m].readLock().lock();
		}
		try {
			int localSize = totalKeys.get();
			V[] values;
			if (dest.length < localSize) {
				values = (V[]) Array.newInstance(dest.getClass().getComponentType(), size());
			} else {
				values = dest;
			}
			int position = 0;
			for (int m = 0; m < mapCount; m++) {
				V[] mapValues = (V[]) mapArray[m].values();
				for (V mapValue : mapValues) {
					values[position++] = mapValue;
				}
			}
			if (position != localSize) {
				throw new IllegalStateException("Key counter does not match actual total map size");
			}
			for (int i = position; i < dest.length; i++) {
				values[i] = null;
			}
			return values;
		} finally {
			for (int m = 0; m < mapCount; m++) {
				lockArray[m].readLock().unlock();
			}
		}
	}

	private int mapHash(long key) {
		int intKey = (int) (key >> 32 ^ key);

		return (0x7FFFFFFF & intKey) % hashScramble & mapMask;
	}
}
