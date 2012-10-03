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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spout.api.math.MathHelper;

public class AtomicIntPaletteArray {

	/**
	 * This is the main store.  It includes a variable width integer array containing ids and an AtomicIntegerArray for the palette.
	 */
	private final AtomicReference<StoreHolder> storeHolder = new AtomicReference<StoreHolder>();
	
	/**
	 * This map is used to get the id for a given value.  This is required when updating the array.
	 */
	private final ConcurrentHashMap<Integer, Integer> idLookup = new ConcurrentHashMap<Integer, Integer>();
	
	/**
	 * This variables is only ever updated when the update lock is held.<br>
	 * It stores the next free id.  However, if the array is resized or compressed, ids may become stale.
	 */
	private final AtomicInteger nextId = new AtomicInteger();
	
	/**
	 * This variable is only ever updated when the resize lock is held.<br>
	 * It stores the size of the palette array.
	 */
	private final AtomicInteger valuesSize = new AtomicInteger();
	
	/**
	 * Locks<br>
	 * A ReadWrite lock is used to managing locking<br>
	 * When resizing the array, updates must be stopped.  The write lock is used as the resize lock.<br>
	 * When making changes to the data stored in the array, that do not require resizing, multiple threads can access the array concurrently.  The read lock is used for the update lock.
	 * Reads to the array are atomic and do not require any locking.
	 * <br>
	 */
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock resizeLock = lock.writeLock();
	private final Lock updateLock = lock.readLock();
	
	private final int length;
	private final int lengthMul1p5;
	
	public AtomicIntPaletteArray(int length) {
		this.length = length;
		this.lengthMul1p5 = length + (length >> 1);
		this.storeHolder.set(new StoreHolder(new AtomicVariableWidthArray(length, 1), new AtomicIntegerArray(2)));
		updateLock.lock();
		try {
			this.valuesSize.set(this.storeHolder.get().getValues().length());
			getId(0);
		} finally {
			updateLock.unlock();
		}
	}
	
	/**
	 * Gets the width of the internal array
	 * 
	 * @return the width
	 */
	public final int width() {
		return storeHolder.get().getStore().width();
	}
	
	/**
	 * Gets the size of the internal palette
	 * 
	 * @return the palette size
	 */
	public final int getPaletteSize() {
		return valuesSize.get();
	}
	
	/**
	 * Gets the number of palette entries in use
	 * 
	 * @return the number of entries
	 */
	public final int getPaletteUsage() {
		return nextId.get();
	}
	
	/**
	 * Gets an element from the array at a given index
	 *
	 * @param i the index
	 * @return the element
	 */
	public final int get(int i) {
		StoreHolder holder = storeHolder.get();
		return holder.getValues().get(holder.getStore().get(i));
	}
	
	/**
	 * Sets an element to the given value
	 *
	 * @param i the index
	 * @param newValue the new value
	 */
	public final void set(int i, int newValue) {
		updateLock.lock();
		try {
			Integer id = idLookup.get(newValue);
			if (id == null) {
				id = getId(newValue);
			}
			storeHolder.get().getStore().set(i, id);
		} finally {
			updateLock.unlock();
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
	public final boolean compareAndSet(int i, int expect, int update) {
		updateLock.lock();
		try {
			Integer e = idLookup.get(expect);
			if (e == null || storeHolder.get().getStore().get(i) != e) {
				return false;
			}
			
			Integer u = idLookup.get(update);
			if (u == null) {
				u = getId(update);
				e = idLookup.get(expect);
				if (e == null) {
					return false;
				}
			}
			return storeHolder.get().getStore().compareAndSet(i, e, u);
		} finally {
			updateLock.unlock();
		}
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
	 * Gets an array containing all the values in the array. The returned values
	 * are not guaranteed to be from the same time instant.
	 *
	 * If an array is provided and it is the correct length, then that array
	 * will be used as the destination array.
	 *
	 * @param array the provided array
	 * @return an array containing the values in the array
	 */
	public final int[] getArray(int[] array) {
		if (array == null || array.length != length()) {
			array = new int[length()];
		}
		
		for (int i = 0; i < length(); i++) {
			array[i] = get(i);
		}
		
		return array;
	}
	
	public void compress() {
		resizeLock.lock();
		try {
			LinkedHashMap<Integer, Integer> oldToNewId = new LinkedHashMap<Integer, Integer>();
			HashSet<Integer> newIds = new HashSet<Integer>();
			
			AtomicVariableWidthArray storeArray = storeHolder.get().getStore();
			int len2 = storeArray.length();
			for (int i = 0; i < len2; i++) {
				int id = storeArray.get(i);
				oldToNewId.put(id, null);
			}

			int len = valuesSize.get();
			AtomicIntegerArray valueArray = storeHolder.get().getValues();
			int lastFree = 0;
			for (int i = 0; i < len; i++) {
				if (!oldToNewId.containsKey(i)) {
					continue;
				}
				while (oldToNewId.containsKey(lastFree) && lastFree < i) {
					lastFree++;
				}
				if (lastFree == i) {
					if (oldToNewId.put(i, i) != null) {
						throw new IllegalStateException("Id " + i + " added to map twice");
					}
					newIds.add(i);
					lastFree++;
					continue;
				}
				if (oldToNewId.put(i, lastFree) != null) {
					throw new IllegalStateException("Id " + i + " added to map twice");
				}
				newIds.add(lastFree);
				int value = valueArray.get(i);
				valueArray.set(lastFree, value);
				
				idLookup.put(value, lastFree);
				
				lastFree++;
			}
			for (int i = 0; i < len2; i++) {
				int oldId = storeArray.get(i);
				Integer newId = oldToNewId.get(oldId);
				if (newId == null) {
					throw new IllegalStateException("No new id for " + oldId);
				}
				storeArray.set(i, newId);
			}
			int maxId = lastFree;
			int newWidth = roundUpWidth(maxId);
			resizeArray(newWidth, true);
			nextId.set(maxId + 1);
			
			Iterator<Map.Entry<Integer, Integer>> itr = idLookup.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<Integer, Integer> entry = itr.next();
				if (!newIds.contains(entry.getValue())) {
					itr.remove();
				}
			}
		} finally {
			resizeLock.unlock();
		}
	}
	
	/**
	 * Gets an id for the given value.  This method will expand the internal arrays if required.  If the palette is expanded beyond 1.5 times the length of the array, it will be compressed.<br>
	 * <br>
	 * Note: the update lock MUST be held when calling this method
	 * 
	 * @param value the value to find an id for
	 * @return the id
	 */
	private int getId(int value) {
		int id = nextId.getAndIncrement();
		while (id >= valuesSize.get() || id > lengthMul1p5) {
			nextId.compareAndSet(id + 1, id);
			updateLock.unlock();
			try {
				if (id > lengthMul1p5) {
					compress();
				} else {
					expandMap(id);
				}
			} finally {
				updateLock.lock();
			}
			id = nextId.getAndIncrement();
		}
		idLookup.put(value, id);
		storeHolder.get().getValues().set(id, value);
		return id;
	}
	
	/**
	 * Expands the internal arrays so that they are at least (id + 1) elements long.
	 * 
	 * @param id
	 */
	private void expandMap(int id) {
		resizeLock.lock();
		try {
			int newWidth = roundUpWidth(id);
			
			int newLength = 1 << newWidth;
			
			if (newLength <= valuesSize.get()) {
				return;
			}
			
			resizeArray(newWidth, false);
		} finally {
			resizeLock.unlock();
		}
	} 
	
	/**
	 * Resizes the array. <br>
	 * <br>
	 * Note: The resize lock MUST be held when calling this method
	 * 
	 * @param newWidth
	 * @param force forces an update
	 */
	private void resizeArray(int newWidth, boolean forcePalette) {
		boolean updateStore = newWidth != width();
		boolean updatePalette = updateStore || forcePalette;
		if (!updateStore && !updatePalette) {
			return;
		}
		
		int newLength = 1 << newWidth;
		
		AtomicVariableWidthArray oldStore = storeHolder.get().getStore();
		
		AtomicIntegerArray oldArray = storeHolder.get().getValues();
		
		AtomicIntegerArray newArray;
		
		if (updatePalette) {
			newArray = new AtomicIntegerArray(newLength);

			int len = Math.min(oldArray.length(), newArray.length());
			for (int i = 0; i < len; i++) {
				newArray.set(i, oldArray.get(i));
			}
		} else {
			newArray = oldArray;
		}
		
		AtomicVariableWidthArray newStore;
		if (updateStore) {
			newStore = new AtomicVariableWidthArray(this.length, newWidth);

			for (int i = 0; i < this.length; i++) {
				newStore.set(i, oldStore.get(i));
			}
		} else {
			newStore = oldStore;
		}

		storeHolder.set(new StoreHolder(newStore, newArray));
		
		valuesSize.set(newLength);
	}
	
	private static final int[] roundLookup = new int[65537];
	
	static {
		roundLookup[0] = 0;
		roundLookup[1] = 1;
		roundLookup[2] = 1;
		roundLookup[4] = 2;
		roundLookup[8] = 4;
		roundLookup[16] = 4;
		roundLookup[32] = 8;
		roundLookup[64] = 8;
		roundLookup[128] = 8;
		roundLookup[256] = 8;
		roundLookup[512] = 16;
		roundLookup[1024] = 16;
		roundLookup[2048] = 16;
		roundLookup[4096] = 16;
		roundLookup[8192] = 16;
		roundLookup[16384] = 16;
	}
	
	
	public static int roundUpWidth(int i) {
		return roundLookup[MathHelper.roundUpPow2(i + 1)];
	}
	
	private static class StoreHolder {
		private final AtomicVariableWidthArray store;
		private final AtomicIntegerArray values;
		
		public StoreHolder(AtomicVariableWidthArray store, AtomicIntegerArray values) {
			this.store = store;
			this.values = values;
		}
		
		public AtomicVariableWidthArray getStore() {
			return store;
		}
		
		public AtomicIntegerArray getValues() {
			return values;
		}
	}
	
}
