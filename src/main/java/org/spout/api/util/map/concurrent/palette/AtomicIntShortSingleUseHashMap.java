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

import java.util.concurrent.atomic.AtomicLongArray;

/**
 * An atomic HashMap that maps integers to positive short values<br>
 * <br>
 * Once a key value pair is set, it cannot be changed again
 */
public class AtomicIntShortSingleUseHashMap {
	
	private final static short EMPTY_VALUE = -1;
	private final static long EMPTY_ENTRY = 0xFFFF000000000000L;
	
	private final AtomicLongArray array;
	private final int length;
	
	AtomicIntShortSingleUseHashMap(int length) {
		this.array = new AtomicLongArray(length);
		for (int i = 0; i < length; i++) {
			this.array.set(i, EMPTY_ENTRY);
		}
		this.length = length;
	}
	
	public short get(int key) {
		int hashed = hash(key);
		int index = hashed;
		long probedEntry;
		boolean empty;
		while (!(empty = isEmpty(probedEntry = array.get(index))) && getKey(probedEntry) != key) {
			index = (index + 1) % length;
			if (index == hashed) {
				return EMPTY_VALUE;
			}
		}
		if (!empty) {
			return getValue(probedEntry);
		} else {
			return EMPTY_VALUE;
		}
	}
	
	public short putIfAbsent(int key, short value) {
		int hashed = hash(key);
		int index = hashed;
		long probedEntry = 0; // Doesn't actually need initialization
		boolean entrySet;
		while (!(entrySet = setEntry(index, key, value)) && getKey(probedEntry = array.get(index)) != key) {
			index = (index + 1) % length;
			if (index == hashed) {
				throw new IllegalStateException("Map is full");
			}
		}
		if (entrySet) {
			return EMPTY_VALUE;
		} else {
			return getValue(probedEntry);
		}
	}
	
	public boolean isEmptyValue(short value) {
		return value == EMPTY_VALUE;
	}
	
	private boolean setEntry(int index, int key, short value) {
		return array.compareAndSet(index, EMPTY_ENTRY, pack(key, value));
	}

	private int hash(int h) {
		  h ^= (h >>> 20) ^ (h >>> 12);
		  h = (h ^ (h >>> 7) ^ (h >>> 4));
		  h = (h & 0x7FFFFFFF) % length;
		  return h;
	}
	
	private static int getKey(long entry) {
		return (int) (entry >> 16);
	}
	
	private static short getValue(long entry) {
		return (short) entry;
	}
	
	private static boolean isEmpty(long entry) {
		return entry == EMPTY_ENTRY;
	}
	
	private static long pack(int key, short value) {
		return ((key & 0xFFFFFFFFL) << 16) | (value & 0xFFFFL);
	}
}
