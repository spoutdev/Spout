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
package org.spout.api.util.set.concurrent;

import gnu.trove.TLongCollection;
import gnu.trove.impl.Constants;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spout.api.math.MathHelper;

/**
 * This is a synchronised version of the Trove LongObjectHashSet.
 *
 * Read/write locks are used to synchronise access.
 *
 * By default, it creates 16 sub-maps and there is a separate read/write lock
 * for each submap.
 */
public class TSyncLongHashSet implements TLongSet {
	private final int setCount;
	private final int setMask;
	private final int hashScramble;
	private final ReadWriteLock[] lockArray;
	private final TLongHashSet[] setArray;
	private final long no_entry_value;
	private final AtomicInteger totalValues = new AtomicInteger(0);

	/**
	 * Creates a synchronised map based on the Trove long object map
	 */
	public TSyncLongHashSet() {
		this(16);
	}

	/**
	 * Creates a synchronised set based on the Trove long object map
	 *
	 * @param setCount the number of sub-sets
	 */
	public TSyncLongHashSet(int setCount) {
		this(setCount, 32);
	}

	/**
	 * Creates a synchronised set based on the Trove long object map
	 *
	 * @param setCount the number of sub-maps
	 * @param initialCapacity the initial capacity of the map
	 */
	public TSyncLongHashSet(int setCount, int initialCapacity) {
		this(setCount, initialCapacity, 0.5F);
	}

	/**
	 * Creates a synchronised set based on the Trove long object map
	 *
	 * @param setCount the number of sub-maps
	 * @param initialCapacity the initial capacity of the map
	 * @param loadFactor the load factor for the map
	 */
	public TSyncLongHashSet(int setCount, int initialCapacity, float loadFactor) {
		this(setCount, initialCapacity, loadFactor, Constants.DEFAULT_LONG_NO_ENTRY_VALUE);
	}

	/**
	 * Creates a synchronised set based on the Trove long object map
	 *
	 * @param setCount the number of sub-maps
	 * @param initialCapacity the initial capacity of the map
	 * @param loadFactor the load factor for the map
	 * @param noEntryValue the value used to indicate a null key
	 */
	public TSyncLongHashSet(int setCount, int initialCapacity, float loadFactor, long noEntryValue) {
		if (setCount > 0x100000) {
			throw new IllegalArgumentException("Set count exceeds valid range");
		}
		setCount = MathHelper.roundUpPow2(setCount);
		setMask = setCount - 1;
		this.setCount = setCount;
		this.hashScramble = (setCount << 8) + 1;
		setArray = new TLongHashSet[setCount];
		lockArray = new ReadWriteLock[setCount];
		for (int i = 0; i < setCount; i++) {
			setArray[i] = new TLongHashSet(initialCapacity / setCount, loadFactor, noEntryValue);
			lockArray[i] = new ReentrantReadWriteLock();
		}
		this.no_entry_value = noEntryValue;
	}

	@Override
	public void clear() {
		for (int m = 0; m < setCount; m++) {
			clear(m);
		}
	}

	private void clear(int m) {
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			totalValues.addAndGet(-setArray[m].size());
			setArray[m].clear();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean contains(long value) {
		for (int m = 0; m < setCount; m++) {
			if (containsValue(m, value)) {
				return true;
			}
		}
		return false;
	}

	private boolean containsValue(int m, long value) {
		Lock lock = lockArray[m].readLock();
		lock.lock();
		try {
			return setArray[m].contains(value);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public long getNoEntryValue() {
		return no_entry_value;
	}

	@Override
	public boolean isEmpty() {
		return totalValues.get() == 0;
	}

	@Override
	public long[] toArray(long[] dest) {
		for (int m = 0; m < setCount; m++) {
			lockArray[m].readLock().lock();
		}
		try {
			int localSize = totalValues.get();
			long[] keys;
			if (dest == null || dest.length < localSize) {
				keys = new long[localSize];
			} else {
				keys = dest;
			}
			int position = 0;
			for (int m = 0; m < setCount; m++) {
				long[] mapKeys = setArray[m].toArray();
				for (long mapKey : mapKeys) {
					keys[position++] = mapKey;
				}
			}
			if (position != localSize) {
				throw new IllegalStateException("Key counter does not match actual total map size");
			}
			return keys;
		} finally {
			for (int m = 0; m < setCount; m++) {
				lockArray[m].readLock().unlock();
			}
		}
	}

	@Override
	public long[] toArray() {
		return toArray(null);
	}

	@Override
	public boolean add(long entry) {
		int m = setHash(entry);
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			boolean success = setArray[m].add(entry);
			if (success) {
				totalValues.incrementAndGet();
			}
			return success;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean remove(long key) {
		int m = setHash(key);
		Lock lock = lockArray[m].writeLock();
		lock.lock();
		try {
			boolean success = setArray[m].remove(key);
			if (success) {
				totalValues.decrementAndGet();
			}
			return success;
		} finally {
			lock.unlock();
		}
	}

	public int size() {
		return totalValues.get();
	}

	private int setHash(long key) {
		int intKey = (int) (key >> 32 ^ key);

		return (0x7FFFFFFF & intKey) % hashScramble & setMask;
	}

	@Override
	public TLongIterator iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(TLongCollection collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(long[] array) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends Long> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(TLongCollection collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(long[] array) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(TLongCollection collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(long[] array) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(TLongCollection collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(long[] array) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean forEach(TLongProcedure procedure) {
		throw new UnsupportedOperationException();
	}
}
