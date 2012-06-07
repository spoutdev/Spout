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

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;

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
public class TSyncIntIntHashMap implements TSyncIntIntMap {
	private final int mapCount;
	private final int mapMask;
	private final int hashScramble;
	private final int noEntryKey;
	private final int noEntryValue;
	private final ReadWriteLock[] lockArray;
	private final TIntIntMap[] mapArray;
	private final AtomicInteger totalKeys = new AtomicInteger(0);

	/**
	 * Creates a synchronised map based on the Trove int object map
	 */
	public TSyncIntIntHashMap() {
		this(16);
	}

	/**
	 * Creates a synchronised map based on the Trove int object map
	 *
	 * @param mapCount the number of sub-maps
	 */
	public TSyncIntIntHashMap(int mapCount) {
		this(mapCount, 32);
	}

	/**
	 * Creates a synchronised map based on the Trove int object map
	 *
	 * @param mapCount the number of sub-maps
	 * @param initialCapacity the initial capacity of the map
	 */
	public TSyncIntIntHashMap(int mapCount, int initialCapacity) {
		this(mapCount, initialCapacity, 0.5F);
	}

	/**
	 * Creates a synchronised map based on the Trove int object map
	 *
	 * @param mapCount the number of sub-maps
	 * @param initialCapacity the initial capacity of the map
	 * @param loadFactor the load factor for the map
	 */
	public TSyncIntIntHashMap(int mapCount, int initialCapacity, float loadFactor) {
		this(mapCount, initialCapacity, loadFactor, 0);
	}

	/**
	 * Creates a synchronised map based on the Trove int object map
	 *
	 * @param mapCount the number of sub-maps
	 * @param initialCapacity the initial capacity of the map
	 * @param loadFactor the load factor for the map
	 * @param noEntryKey the key used to indicate a null key
	 */
	public TSyncIntIntHashMap(int mapCount, int initialCapacity, float loadFactor, int noEntryKey) {
		this(mapCount, initialCapacity, loadFactor, noEntryKey, 0);
	}
	
	/**
	 * Creates a synchronised map based on the Trove int object map
	 *
	 * @param mapCount the number of sub-maps
	 * @param initialCapacity the initial capacity of the map
	 * @param loadFactor the load factor for the map
	 * @param noEntryKey the key used to indicate a null key
	 * @param noEntryValue the value used to indicate a null value
	 */
	public TSyncIntIntHashMap(int mapCount, int initialCapacity, float loadFactor, int noEntryKey, int noEntryValue) {
		if (mapCount > 0x100000) {
			throw new IllegalArgumentException("Map count exceeds valid range");
		}
		mapCount = MathHelper.roundUpPow2(mapCount);
		mapMask = mapCount - 1;
		this.mapCount = mapCount;
		this.hashScramble = (mapCount << 8) + 1;
		mapArray = new TIntIntHashMap[mapCount];
		lockArray = new ReadWriteLock[mapCount];
		for (int i = 0; i < mapCount; i++) {
			mapArray[i] = new TIntIntHashMap(initialCapacity / mapCount, loadFactor, noEntryKey, noEntryValue);
			lockArray[i] = new ReentrantReadWriteLock();
		}
		this.noEntryKey = noEntryKey;
		this.noEntryValue = noEntryValue;
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

	public boolean containsValue(int value) {
		for (int m = 0; m < mapCount; m++) {
			if (containsValue(m, value)) {
				return true;
			}
		}
		return false;
	}

	private boolean containsValue(int m, int value) {
		Lock lock = lockArray[m].readLock();
		lock.lock();
		try {
			return mapArray[m].containsValue(value);
		} finally {
			lock.unlock();
		}
	}

	public int get(int key) {
		int m = mapHash(key);
		Lock lock = lockArray[m].readLock();
		lock.lock();
		try {
			return mapArray[m].get(key);
		} finally {
			lock.unlock();
		}
	}

	public boolean isEmpty() {
		return totalKeys.get() == 0;
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

	public int put(int key, int value) {
		int m = mapHash(key);
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			TIntIntMap map = mapArray[m];
			if (!map.containsKey(key)) {
				totalKeys.incrementAndGet();
			}
			return map.put(key, value);
		} finally {
			lock.unlock();
		}
	}

	public int putIfAbsent(int key, int value) {
		int m = mapHash(key);
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			TIntIntMap map = mapArray[m];
			if (!map.containsKey(key)) {
				totalKeys.incrementAndGet();
			}
			return map.putIfAbsent(key, value);
		} finally {
			lock.unlock();
		}
	}

	public int remove(int key) {
		int m = mapHash(key);
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			TIntIntMap map = mapArray[m];
			if (map.containsKey(key)) {
				totalKeys.decrementAndGet();
			}
			return map.remove(key);
		} finally {
			lock.unlock();
		}
	}

	public boolean remove(int key, int value) {
		int m = mapHash(key);
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			TIntIntMap map = mapArray[m];
			if (!map.containsKey(key) || map.get(key) != value) {
				return false;
			}

			totalKeys.decrementAndGet();
			map.remove(key);
			return true;
		} finally {
			lock.unlock();
		}
	}

	public int size() {
		return totalKeys.get();
	}

	private int mapHash(int key) {
		int intKey = key >> 32 ^ key;

		return (0x7FFFFFFF & intKey) % hashScramble & mapMask;
	}

	@Override
	public int getNoEntryValue() {
		return noEntryValue;
	}

	@Override
	public void putAll(Map<? extends Integer, ? extends Integer> map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(TIntIntMap map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TIntSet keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public TIntCollection valueCollection() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] values(int[] array) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TIntIntIterator iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean forEachKey(TIntProcedure procedure) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean forEachValue(TIntProcedure procedure) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean forEachEntry(TIntIntProcedure procedure) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void transformValues(TIntFunction function) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainEntries(TIntIntProcedure procedure) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean increment(int key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean adjustValue(int key, int amount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int adjustOrPutValue(int key, int adjust_amount, int put_amount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getNoEntryKey() {
		return noEntryKey;
	}
}
