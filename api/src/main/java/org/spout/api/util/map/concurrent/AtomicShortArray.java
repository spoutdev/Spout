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

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.spout.api.util.hashing.ShortPairHashed;

public class AtomicShortArray implements Serializable {
	private static final long serialVersionUID = 12344553523475L;
	private final int length;
	private final int backingArraySize;
	private final AtomicIntegerArray backingArray;

	/**
	 * Creates an atomic short array of a given length
	 *
	 * @param length the length of the array
	 */
	public AtomicShortArray(int length) {
		this.length = length;
		backingArraySize = (length & 1) + (length >> 1);
		backingArray = new AtomicIntegerArray(backingArraySize);
	}

	/**
	 * Creates an atomic short array of a given length that is equal to a given array
	 *
	 * @param length the length of the array
	 * @param initial the initial array, is allowed to be null
	 */
	public AtomicShortArray(int length, short[] initial) {
		this.length = length;
		backingArraySize = (length & 1) + (length >> 1);
		if (initial == null) {
			backingArray = new AtomicIntegerArray(backingArraySize);
			return;
		}
		// Store the initial data in the backing array
		int[] data = new int[backingArraySize];
		int initIndex = 0;
		for (int i = 0; i < this.backingArraySize; i++) {
			if (initIndex + 1 < initial.length) {
				// both elements available for squashing
				data[i] = ShortPairHashed.key(initial[initIndex], initial[initIndex + 1]);
			} else {
				// first element is last element
				data[i] = ShortPairHashed.key(initial[initIndex], (short) 0);
			}
			initIndex += 2;
		}
		backingArray = new AtomicIntegerArray(data);
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
	 * Gets an element from the array at a given index
	 *
	 * @param index the index
	 * @return the element
	 */
	public final short get(int index) {
		int packed = getPacked(index);
		return isEven(index) ? ShortPairHashed.key1(packed) : ShortPairHashed.key2(packed);
	}

	/**
	 * Sets an element in the array at a given index and returns the old value
	 *
	 * @param index the index
	 * @param value the new value
	 * @return the old value
	 */
	public final int getAndSet(int index, short value) {
		boolean success = false;
		short odd = 0;
		short even = 0;
		short oldValue = 0;
		int backingIndex = index >> 1;
		boolean evenIndex = isEven(index);
		while (!success) {
			int oldPacked = backingArray.get(backingIndex);
			if (evenIndex) {
				oldValue = ShortPairHashed.key1(oldPacked);
				even = value;
				odd = ShortPairHashed.key2(oldPacked);
			} else {
				oldValue = ShortPairHashed.key2(oldPacked);
				even = ShortPairHashed.key1(oldPacked);
				odd = value;
			}
			int newPacked = ShortPairHashed.key(even, odd);
			success = backingArray.compareAndSet(backingIndex, oldPacked, newPacked);
		}
		return oldValue;
	}

	/**
	 * Sets two elements in the array at once. The index must be even.
	 *
	 * @param index the index
	 * @param even the new value for the element at (index)
	 * @param odd the new value for the element at (index + 1)
	 */
	public final void set(int index, short even, short odd) {
		if ((index & 1) != 0) {
			throw new IllegalArgumentException("When setting 2 elements at once, the index must be even");
		}
		backingArray.set(index >> 1, ShortPairHashed.key(even, odd));
	}

	/**
	 * Sets the element at the given index, but only if the previous value was the expected value.
	 *
	 * @param index the index
	 * @param expected the expected value
	 * @param newValue the new value
	 * @return true on success
	 */
	public final boolean compareAndSet(int index, short expected, short newValue) {
		boolean success = false;
		short odd = 0;
		short even = 0;
		short oldValue = 0;
		int backingIndex = index >> 1;
		boolean evenIndex = isEven(index);
		while (!success) {
			int oldPacked = backingArray.get(backingIndex);
			if (evenIndex) {
				oldValue = ShortPairHashed.key1(oldPacked);
				even = newValue;
				odd = ShortPairHashed.key2(oldPacked);
			} else {
				oldValue = ShortPairHashed.key2(oldPacked);
				even = ShortPairHashed.key1(oldPacked);
				odd = newValue;
			}
			if (oldValue != expected) {
				return false;
			}
			int newPacked = ShortPairHashed.key(even, odd);
			success = backingArray.compareAndSet(backingIndex, oldPacked, newPacked);
		}
		return true;
	}

	private short addAndGet(int index, short delta, boolean old) {
		boolean success = false;
		short newValue = 0;
		short oldValue = 0;
		while (!success) {
			oldValue = get(index);
			newValue = (short) (oldValue + delta);
			success = compareAndSet(index, oldValue, newValue);
		}
		return old ? oldValue : newValue;
	}

	/**
	 * Gets an array containing all the values in the array. The returned values are not guaranteed to be from the same time instant.
	 *
	 * If an array is provided and it is the correct length, then that array will be used as the destination array.
	 *
	 * @param array the provided array
	 * @return an array containing the values in the array
	 */
	public short[] getArray(short[] array) {
		if (array == null || array.length != length) {
			array = new short[length];
		}
		for (int i = 0; i < length; i += 2) {
			int packed = getPacked(i);
			array[i] = ShortPairHashed.key1(packed);
			if (i + 1 < length) {
				array[i + 1] = ShortPairHashed.key2(packed);
			}
		}
		return array;
	}

	/*
	 * The remaining methods use the above methods
	 */

	/**
	 * Sets an element to the given value
	 *
	 * @param index the index
	 * @param value the new value
	 */
	public final void set(int index, short value) {
		getAndSet(index, value);
	}

	/**
	 * Sets an element to the given value, but the update may not happen immediately
	 *
	 * @param index the index
	 * @param value the new value
	 */
	public final void lazySet(int index, short value) {
		set(index, value);
	}

	/**
	 * Sets the element at the given index, but only if the previous value was the expected value. This may fail spuriously.
	 *
	 * @param index the index
	 * @param expected the expected value
	 * @param newValue the new value
	 * @return true on success
	 */
	public final boolean weakCompareAndSet(int index, short expected, short newValue) {
		return compareAndSet(index, expected, newValue);
	}

	/**
	 * Atomically adds a delta to an element, and gets the new value.
	 *
	 * @param index the index
	 * @param delta the delta to add to the element
	 * @return the new value
	 */
	public final short addAndGet(int index, short delta) {
		return addAndGet(index, delta, false);
	}

	/**
	 * Atomically adds a delta to an element, and gets the old value.
	 *
	 * @param index the index
	 * @param delta the delta to add to the element
	 * @return the old value
	 */
	public final short getAndAdd(int index, short delta) {
		return addAndGet(index, delta, true);
	}

	/**
	 * Atomically increments an element and returns the old value.
	 *
	 * @param index the index
	 * @return the old value
	 */
	public final short getAndIncrement(int index) {
		return getAndAdd(index, (short) 1);
	}

	/**
	 * Atomically decrements an element and returns the old value.
	 *
	 * @param index the index
	 * @return the old value
	 */
	public final short getAndDecrement(int index) {
		return getAndAdd(index, (short) -1);
	}

	/**
	 * Atomically increments an element and returns the new value.
	 *
	 * @param index the index
	 * @return the new value
	 */
	public final short incrementAndGet(int index) {
		return addAndGet(index, (short) 1);
	}

	/**
	 * Atomically decrements an element and returns the new value.
	 *
	 * @param index the index
	 * @return the new value
	 */
	public final short decrementAndGet(int index) {
		return addAndGet(index, (short) -1);
	}

	/**
	 * Gets an array containing all the values in the array.
	 *
	 * The returned values are not guaranteed to be from the same time instant.
	 *
	 * @return the array
	 */
	public short[] getArray() {
		return getArray(null);
	}

	/**
	 * Returns a string representation of the array.
	 *
	 * The returned values are not guaranteed to be from the same time instant.
	 *
	 * @return the String
	 */
	@Override
	public String toString() {
		short[] array = getArray();
		return Arrays.toString(array);
	}

	private int getPacked(int index) {
		return backingArray.get(index >> 1);
	}

	private static boolean isEven(int index) {
		return (index & 1) == 0;
	}
}
