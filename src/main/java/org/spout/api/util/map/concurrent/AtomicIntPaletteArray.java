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

import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spout.api.math.MathHelper;

public class AtomicIntPaletteArray {

	private final AtomicReference<AtomicVariableWidthArray> store = new AtomicReference<AtomicVariableWidthArray>();

	private final AtomicReference<AtomicIntegerArray> values = new AtomicReference<AtomicIntegerArray>();
	
	private final AtomicInteger valuesSize = new AtomicInteger();
	
	private final ConcurrentHashMap<Integer, Integer> idLookup = new ConcurrentHashMap<Integer, Integer>();
	
	private final AtomicInteger nextId = new AtomicInteger();
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock resizeLock = lock.writeLock();
	private final Lock updateLock = lock.readLock();
	
	private final int length;
	
	public AtomicIntPaletteArray(int length) {
		this.length = length;
		this.store.set(new AtomicVariableWidthArray(length, 1));
		this.values.set(new AtomicIntegerArray(2));
		this.valuesSize.set(this.values.get().length());
		getNextId(0);
	}
	
	/**
	 * Gets the width of the internal array
	 * 
	 * @return the width
	 */
	public final int width() {
		return store.get().width();
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
		return values.get().get(store.get().get(i));
	}
	
	/**
	 * Sets an element to the given value
	 *
	 * @param i the index
	 * @param newValue the new value
	 */
	public final void set(int i, int newValue) {
		Integer id = idLookup.get(newValue);
		if (id == null) {
			id = getNextId(newValue);
		}
		updateLock.lock();
		try {
			store.get().set(i, id);
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

		Integer e = idLookup.get(expect);
		if (e == null || store.get().get(i) != e) {
			return false;
		}
		
		Integer u = idLookup.get(update);
		if (u == null) {
			u = getNextId(update);
		}
		
		updateLock.lock();
		try {
			return store.get().compareAndSet(i, e, u);
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
		updateLock.lock();
		try {
			LinkedHashMap<Integer, Integer> inUseIds = new LinkedHashMap<Integer, Integer>();
			
			AtomicVariableWidthArray storeArray = store.get();
			int len2 = storeArray.length();
			for (int i = 0; i < len2; i++) {
				int id = storeArray.get(i);
				inUseIds.put(id, null);
			}

			int len = valuesSize.get();
			AtomicIntegerArray valueArray = values.get();
			int lastFree = 0;
			for (int i = 0; i < len; i++) {
				if (!inUseIds.containsKey(i)) {
					continue;
				}
				while (inUseIds.containsKey(lastFree) && lastFree < i) {
					lastFree++;
				}
				if (lastFree == i) {
					inUseIds.put(i, i);
					lastFree++;
					continue;
				}
				inUseIds.put(i, lastFree);
				
				int value = valueArray.get(i);
				valueArray.set(lastFree, value);
				
				idLookup.put(value, lastFree);
				
				lastFree++;
			}
			for (int i = 0; i < len2; i++) {
				int oldId = storeArray.get(i);
				Integer newId = inUseIds.get(oldId);
				if (newId == null) {
					throw new IllegalStateException("No new id for " + oldId);
				}
				storeArray.set(i, newId);
			}
			int newWidth = roundUpWidth(lastFree);
			resizeArray(newWidth);
			nextId.set(lastFree);
		} finally {
			updateLock.unlock();
		}
	}
	
	private int getNextId(int value) {
		int id = nextId.getAndIncrement();
		if (id >= valuesSize.get()) {
			expandMap(id);
		}
		updateLock.lock();
		try {
			idLookup.put(value, id);
			values.get().set(id, value);
		} finally {
			updateLock.unlock();
		}
		return id;
	}
	
	private void expandMap(int id) {
		resizeLock.lock();
		try {
			int newWidth = roundUpWidth(id);
			
			int newLength = 1 << newWidth;
			
			if (newLength <= valuesSize.get()) {
				return;
			}
			
			resizeArray(newWidth);
		} finally {
			resizeLock.unlock();
		}
	} 
	
	private void resizeArray(int newWidth) {
		int newLength = 1 << newWidth;
		
		AtomicVariableWidthArray oldStore = store.get();
		
		AtomicIntegerArray oldArray = values.get();
		
		AtomicIntegerArray newArray = new AtomicIntegerArray(newLength);
		
		int len = oldArray.length();
		int len2 = newLength;
		for (int i = 0; i < len && i < len2; i++) {
			newArray.set(i, oldArray.get(i));
		}
		values.set(newArray);
		
		AtomicVariableWidthArray newStore = new AtomicVariableWidthArray(this.length, newWidth);
		
		for (int i = 0; i < this.length; i++) {
			newStore.set(i, oldStore.get(i));
		}

		store.set(newStore);
		
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
	
}
