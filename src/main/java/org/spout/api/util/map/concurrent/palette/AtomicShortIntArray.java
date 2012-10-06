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
package org.spout.api.util.map.concurrent.palette;

import gnu.trove.set.hash.TIntHashSet;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An integer array that has a short index.  The array is atomic and is backed by a palette based lookup system.
 */
public class AtomicShortIntArray {
	
	/**
	 * The length of the array
	 */
	private final int length;
	
	/**
	 * A reference to the store.  When the palette fills, or when the store is compressed.  A new store is created.
	 */
	private final AtomicReference<AtomicShortIntBackingArray> store = new AtomicReference<AtomicShortIntBackingArray>();
	
	/**
	 * Locks<br>
	 * A ReadWrite lock is used to managing locking<br>
	 * When copying to a new store instance, and updating to new the store reference, all updates must be stopped.  The write lock is used as the resize lock.<br>
	 * When making changes to the data stored in an array instance, multiple threads can access the array concurrently.  The read lock is used for the update lock.
	 * Reads to the array are atomic and do not require any locking.
	 * <br>
	 */
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock resizeLock = lock.writeLock();
	private final Lock updateLock = lock.readLock();
	
	public AtomicShortIntArray(int length) {
		this.length = length;
		store.set(new AtomicShortIntPaletteBackingArray(length));
	}
	
	/**
	 * Gets the width of the internal array, in bits
	 * 
	 * @return the width
	 */
	public int width() {
		return store.get().width();
	}
	
	/**
	 * Gets the length of the array
	 *
	 * @return the length
	 */
	public final int length() {
		return length;
	}
	
	/**
	 * Gets the size of the internal palette
	 * 
	 * @return the palette size
	 */
	public int getPaletteSize() {
		return store.get().getPaletteSize();
	}
	
	/**
	 * Gets the number of palette entries in use
	 * 
	 * @return the number of entries
	 */
	public int getPaletteUsage() {
		return store.get().getPaletteUsage();
	}
	
	/**
	 * Gets an element from the array at a given index
	 *
	 * @param i the index
	 * @return the element
	 */
	public int get(int i) {
		return store.get().get(i);
	}
	
	/**
	 * Sets an element to the given value
	 *
	 * @param i the index
	 * @param newValue the new value
	 * @return the old value
	 */
	public int set(int i, int newValue) {
		while (true) {
			try {
				updateLock.lock();
				try {
					return store.get().set(i, newValue);
				} finally {
					updateLock.unlock();
				}
			} catch (PaletteFullException pfe) {
				resizeLock.lock();
				try {
					try {
						return store.get().set(i, newValue);
					} catch (PaletteFullException pfe2) {
						if (store.get().isPaletteMaxSize()) {
							store.set(new AtomicShortIntDirectBackingArray(store.get()));
						} else {
							store.set(new AtomicShortIntPaletteBackingArray(store.get(), true));
						}		
					}
				} finally {
					resizeLock.unlock();
				}
			}
		}
	}
	
	/**
	 * Sets the element at the given index, but only if the previous value was the expected value.
	 *
	 * @param i the index
	 * @param expect the expected value
	 * @param update the new value
	 * @return true on success
	 */
	public boolean compareAndSet(int i, int expect, int update) {
		while (true) {
			try {
				updateLock.lock();
				try {
					store.get().compareAndSet(i, expect, update);
				} finally {
					updateLock.unlock();
				}
			} catch (PaletteFullException pfe) {
				resizeLock.lock();
				try {
					if (store.get().isPaletteMaxSize()) {
						store.set(new AtomicShortIntDirectBackingArray(store.get()));
					} else {
						store.set(new AtomicShortIntPaletteBackingArray(store.get(), true));
					}
				} finally {
					resizeLock.unlock();
				}
			}
		}
	}
	
	/**
	 * Attempts to compress the array
	 */
	public void compress() {
		compress(new TIntHashSet());
	}
	
	/**
	 * Attempts to compress the array
	 * 
	 * @param set to use to store used ids
	 */
	public void compress(TIntHashSet inUseSet) {
		resizeLock.lock();
		try {
			AtomicShortIntBackingArray s = store.get();
			int unique = s.getUnique(inUseSet);
			if (AtomicShortIntPaletteBackingArray.roundUpWidth(unique - 1) >= s.width()) {
				return;
			}
			store.set(new AtomicShortIntPaletteBackingArray(s, length, true, false, unique));
		} finally {
			resizeLock.unlock();
		}
	}
	
	/**
	 * Gets the number of unique entries in the array
	 * 
	 * @return
	 */
	public int getUnique() {
		TIntHashSet inUse = new TIntHashSet();
		int unique = 0;
		for (int i = 0; i < length; i++) {
			if (inUse.add(get(i))) {
				unique++;
			}
		}
		return unique;
	}

}
