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

import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * A 3d int based Object map that is backed by AtomicReferenceArrays arranged in
 * a tree structure.<br>
 * <br>
 * The Map operates in a tree structure.  The bits variable indicates the
 * number of bits used per level of the tree.  This is used to mask the input
 * coordinates.<br>
 * <br>
 * The length of the internal arrays are determined by the bits parameter.<br>
 * <br>
 * If bits is set to 4, then each coordinate provides 4 bits for the array index.
 * That gives a total array length of 16 * 16 * 16 = 4096.<br>
 * <br>
 * Inserting a new object with a random key would probably require new arrays to 
 * be created for the entire depth of the tree.<br>
 * <br>
 * A given depth can be guaranteed by keeping ensuring that all elements are within
 * a cube that has an edge of 2 ^ (depth * bits) or smaller.  Increasing the bits 
 * variable reduces the depth of the internal tree at the expense of more memory
 * used per array.<br>
 * <br>
 * The map is thread-safe.  Map update operations can not be carried out by more
 * than one thread at the same time.  However, read operations are concurrent.<br>
 * <br>
 * The map is optimised for use where all the coordinates occur in a small number of
 * contiguous cuboids.
 * 
 * @param <T> the value type
 */

public class TripleIntObjectReferenceArrayMap<T> implements TripleIntObjectMap<T> {
	
	private final int bits;
	private final int doubleBits;
	private final int bitMask;
	private final int arraySize;
	
	private final AtomicReference<Entry<T>> root;
	private final LinkedHashSet<T> values;
	private final LinkedHashSet<LeafEntry> leafEntries;
	
	private int removed = 0;
	
	public TripleIntObjectReferenceArrayMap(int bits) {
		this(bits, 1);
	}
	
	private TripleIntObjectReferenceArrayMap(int bits, int depth) {
		this.bits = bits;
		this.doubleBits = bits << 1;
		int width = 1 << bits;
		this.bitMask = width - 1;
		this.arraySize = (width) * (width) * (width);
		this.root = new AtomicReference<Entry<T>>();
		this.root.set(new AtomicReferenceArrayEntry(depth));
		this.values = new LinkedHashSet<T>();
		this.leafEntries = new LinkedHashSet<LeafEntry>();
	}

	@Override
	public T get(int x, int y, int z) {
		Entry<T> entry = getEntryRaw(x, y, z);
		if (entry != null) {
			return entry.getValue();
		} else {
			return null;
		}
	}

	@Override
	public synchronized T remove(int x, int y, int z) {
		Entry<T> entry = getEntryRaw(x, y, z);
		if (entry != null) {
			T value = entry.remove();
			if (value != null) {
				removed++;
				if (!values.remove(value)) {
					throw new IllegalStateException("Item removed from map was not in item set");
				}
			}
			return value;
		} else {
			return null;
		}
	}

	@Override
	public synchronized boolean remove(int x, int y, int z, T value) {
		Entry<T> entry = getEntryRaw(x, y, z);
		if (entry != null) {
			boolean b = entry.remove(value);
			if (b) {
				removed++;
				if (!values.remove(value)) {
					throw new IllegalStateException("Item removed from map was not in item set");
				}
			}
			return b;
		} else {
			return false;
		}
	}

	@Override
	public synchronized T put(int x, int y, int z, T value) {
		if (value == null) {
			throw new NullPointerException("Null values are not permitted");
		}
		Entry<T> entry = getOrCreateEntry(x, y, z);
		if (entry != null) {
			T old = entry.put(value);
			if (!values.add(value)) {
				throw new IllegalStateException("Failed to add item to the value set, items may only be added once to the map");
			}
			if (old != null) {
				if (!values.remove(old)) {
					throw new IllegalStateException("Item removed from map was not in item set");
				}
			}
			return old;
		} else {
			throw new IllegalStateException("Unable to create entry for put");
		}
	}

	@Override
	public synchronized T putIfAbsent(int x, int y, int z, T value) {
		if (value == null) {
			throw new NullPointerException("Null values are not permitted");
		}
		Entry<T> entry = getOrCreateEntry(x, y, z);
		if (entry != null) {
			T old = entry.putIfAbsent(value);
			if (old == null) {
				if (!values.add(value)) {
					throw new IllegalStateException("Failed to add item to the value set, items may only be added once to the map");
				}
			}
			return old;
		} else {
			throw new IllegalStateException("Unable to create entry for put");
		}
	}
	
	private synchronized Entry<T> getOrCreateEntry(int x, int y, int z) {
		Entry<T> entry = getEntryRaw(x, y, z);
		if (entry != null) {
			return entry;
		}
		entry = this.root.get();
		int depth = entry.getDepth();
		int shift = entry.getInitialShift();
		Entry<T> prevEntry = null;
		
		int keyDepth = 0;
		while (entry != null) {
			prevEntry = entry;
			entry = entry.getSubEntry(x, y, z, shift);
			shift -= bits;
			if (entry == null) {
				break;
			}
			keyDepth++;
		}
		
		// Map must be resized if we hit a leaf node (collision) or the map was already at max depth
		if (keyDepth > depth || prevEntry instanceof TripleIntObjectReferenceArrayMap.LeafEntry) {
			resizeMap();
			return getOrCreateEntry(x, y, z);
		} else if (keyDepth > depth) {
			throw new IllegalStateException("Map has a depth that exceeds the depth variable");
		}
		
		entry = prevEntry;
		shift += bits;

		for (int i = keyDepth; i < depth; i++) {
			Entry<T> newEntry = new AtomicReferenceArrayEntry(depth);
			if (!((AtomicReferenceArrayEntry) entry).addNewEntry(x, y, z, shift, newEntry)) {
				throw new IllegalStateException("Unable to add new map entry");
			}
			shift -= bits;
			entry = newEntry;
		}
		if (shift != 0) {
			throw new IllegalStateException("Shift counter in illegal state: " + shift);
		}
		LeafEntry newEntry = new LeafEntry(x, y, z);
		if (!((AtomicReferenceArrayEntry) entry).addNewEntry(x, y, z, 0, newEntry)) {
			throw new IllegalStateException("Unable to add new leaf entry");
		}
		this.leafEntries.add(newEntry);
		return newEntry;
	}
	
	private synchronized void resizeMap() {
		TripleIntObjectReferenceArrayMap.Entry<T> oldRoot = this.root.get();
		int newDepth = oldRoot.getDepth() + 1;
		TripleIntObjectReferenceArrayMap<T> temp = new TripleIntObjectReferenceArrayMap<T>(bits, newDepth);
		
		for (LeafEntry le : leafEntries) {
			T value = le.getValue();
			if (value != null) {
				int x = le.getX();
				int y = le.getY();
				int z = le.getZ();
				temp.put(x, y, z, value);
				if (!values.remove(value)) {
					throw new IllegalStateException("Value moved to other map on resize was not in value list");
				}
			}
		}
		
		if (!this.values.isEmpty()) {
			throw new IllegalStateException("Some values were not transferred to the new map on resize");
		}
		
		this.leafEntries.clear();
		this.leafEntries.addAll(temp.leafEntries);
		
		this.values.addAll(temp.values);
		if (!this.root.compareAndSet(oldRoot, temp.root.get())) {
			throw new IllegalStateException("Old root changed while resizing");
		}
	}
	
	private Entry<T> getEntryRaw(int x, int y, int z) {
		Entry<T> entry = this.root.get();
		int depth = entry.getDepth();
		int shift = entry.getInitialShift();
		
		for (int i = 0; i <= depth; i++) {
			entry = entry.getSubEntry(x, y, z, shift);
			if (entry == null) {
				return null;
			}
			shift -= bits;
		}
		if (entry.testKey(x, y, z)) {
			return entry;
		} else {
			return null;
		}
	}
	
	
	private static interface Entry<T> {
		
		public Entry<T> getSubEntry(int x, int y, int z, int shift);
		
		public boolean testKey(int x, int y, int z);
		
		public T getValue();
		
		public T remove();
		
		public boolean remove(T value);
		
		public T putIfAbsent(T value);
		
		public T put(T value);
		
		public int getDepth();
		
		public int getInitialShift();
		
	}
	
	private class AtomicReferenceArrayEntry implements Entry<T> {
		
		private final int depth;
		private final int initialShift;
		private final AtomicReferenceArray<Entry<T>> array;
		
		public AtomicReferenceArrayEntry(int depth) {
			this.depth = depth;
			this.array = new AtomicReferenceArray<Entry<T>>(arraySize);
			this.initialShift = depth * bits;
		}

		@Override
		public Entry<T> getSubEntry(int x, int y, int z, int shift) {
			return array.get(getIndex(x, y, z, shift));
		}

		@Override
		public T getValue() {
			throw new UnsupportedOperationException("The AtomicReferenceArrayEntry class cannot store values directly");
		}

		@Override
		public int getDepth() {
			return depth;
		}

		@Override
		public int getInitialShift() {
			return initialShift;
		}

		@Override
		public boolean testKey(int x, int y, int z) {
			throw new UnsupportedOperationException("The AtomicReferenceArrayEntry class does not contain key/value pairs");
		}

		@Override
		public T remove() {
			throw new UnsupportedOperationException("The AtomicReferenceArrayEntry class does not contain key/value pairs");
		}

		@Override
		public boolean remove(T value) {
			throw new UnsupportedOperationException("The AtomicReferenceArrayEntry class does not contain key/value pairs");
		}

		@Override
		public T putIfAbsent(T value) {
			throw new UnsupportedOperationException("The AtomicReferenceArrayEntry class does not contain key/value pairs");
		}

		@Override
		public T put(T value) {
			throw new UnsupportedOperationException("The AtomicReferenceArrayEntry class does not contain key/value pairs");
		}
		
		public boolean addNewEntry(int x, int y, int z, int shift, Entry<T> subEntry) {
			int index = getIndex(x, y, z, shift);
			return array.compareAndSet(index, null, subEntry);
		}
		
	}
	
	private class LeafEntry implements Entry<T> {
		
		private final AtomicReference<T> value;
		private final int x;
		private final int y;
		private final int z;
		
		public LeafEntry(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.value = new AtomicReference<T>();
		}

		@Override
		public Entry<T> getSubEntry(int x, int y, int z, int shift) {
			return null;
		}

		@Override
		public boolean testKey(int x, int y, int z) {
			return (x == this.x) && (y == this.y) && (z == this.z);
		}

		@Override
		public T getValue() {
			return this.value.get();
		}

		@Override
		public T remove() {
			return this.value.getAndSet(null);
		}

		@Override
		public boolean remove(T value) {
			return this.value.compareAndSet(value, null);
		}

		@Override
		public T putIfAbsent(T value) {
			while (true) {
				T old = this.value.get();
				if (old != null) {
					return old;
				}
				if (this.value.compareAndSet(null, value)) {
					return null;
				}
			}
		}

		@Override
		public T put(T value) {
			return this.value.getAndSet(value);
		}

		@Override
		public int getDepth() {
			throw new UnsupportedOperationException("The LeafEntry class does not support this method");
		}

		@Override
		public int getInitialShift() {
			throw new UnsupportedOperationException("The LeafEntry class does not support this method");
		}
		
		@Override
		public String toString() {
			return "{" + x + ", " + y + ", " + z + "}";
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public int getZ() {
			return z;
		}
		
	}
	
	private int getIndex(int x, int y, int z, int shift) {
		x = x >> shift;
		y = y >> shift;
		z = z >> shift;
		x &= bitMask;
		y &= bitMask;
		z &= bitMask;
		return ((x & bitMask) << doubleBits) | ((y & bitMask) << bits) | (z & bitMask);
	}
	
}
