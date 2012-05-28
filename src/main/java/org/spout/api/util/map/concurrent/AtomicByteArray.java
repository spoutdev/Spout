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

import java.io.Serializable;
import java.util.Arrays;

public class AtomicByteArray implements Serializable {
	private static final long serialVersionUID = 12344553523475L;
	private final int length;
	private final int backingArraySize;
	private final AtomicShortArray backingArray;

	/**
	 * Creates an atomic short array of a given length
	 *
	 * @param length the length of the array
	 */
	public AtomicByteArray(int length) {
		this.length = length;
		backingArraySize = (length & 1) + (length >> 1);
		backingArray = new AtomicShortArray(backingArraySize);
	}

	/**
	 * Creates an atomic short array that is equal to a given array
	 *
	 * @param initial the initial array
	 */
	public AtomicByteArray(short[] initial) {
		this(initial.length);
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
	public final byte get(int index) {
		short packed = getPacked(index);
		return unpack(packed, index);
	}

	/**
	 * Sets an element in the array at a given index and returns the old value
	 *
	 * @param index the index
	 * @param value the new value
	 * @return the old value
	 */
	public final int getAndSet(int index, byte value) {
		boolean success = false;
		byte odd = 0;
		byte even = 0;
		short oldValue = 0;
		int backingIndex = index >> 1;
		boolean evenIndex = isEven(index);
		while (!success) {
			short oldPacked = backingArray.get(backingIndex);
			if (evenIndex) {
				oldValue = unpackEven(oldPacked);
				even = value;
				odd = unpackOdd(oldPacked);
			} else {
				oldValue = unpackOdd(oldPacked);
				even = unpackEven(oldPacked);
				odd = value;
			}
			short newPacked = pack(even, odd);
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
	public final void set(int index, byte even, byte odd) {
		if ((index & 1) != 0) {
			throw new IllegalArgumentException("When setting 2 elements at once, the index must be even");
		}
		backingArray.set(index >> 1, pack(even, odd));
	}

	/**
	 * Sets the element at the given index, but only if the previous value was
	 * the expected value.
	 *
	 * @param index the index
	 * @param expected the expected value
	 * @param newValue the new value
	 * @return true on success
	 */
	public final boolean compareAndSet(int index, byte expected, byte newValue) {
		boolean success = false;
		byte odd = 0;
		byte even = 0;
		short oldValue = 0;
		int backingIndex = index >> 1;
		boolean evenIndex = isEven(index);
		while (!success) {
			short oldPacked = backingArray.get(backingIndex);
			if (evenIndex) {
				oldValue = unpackEven(oldPacked);
				even = newValue;
				odd = unpackOdd(oldPacked);
			} else {
				oldValue = unpackOdd(oldPacked);
				even = unpackEven(oldPacked);
				odd = newValue;
			}
			if (oldValue != expected) {
				return false;
			}
			short newPacked = pack(even, odd);
			success = backingArray.compareAndSet(backingIndex, oldPacked, newPacked);
		}
		return true;
	}

	private final byte addAndGet(int index, byte delta, boolean old) {
		boolean success = false;
		byte newValue = 0;
		byte oldValue = 0;
		while (!success) {
			oldValue = get(index);
			newValue = (byte) (oldValue + delta);
			success = compareAndSet(index, oldValue, newValue);
		}
		return old ? oldValue : newValue;
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
	public byte[] getArray(byte[] array) {
		if (array == null || array.length != length) {
			array = new byte[length];
		}
		for (int i = 0; i < length; i += 2) {
			short packed = getPacked(i);
			array[i] = unpack(packed, i);
			if (i + 1 < length) {
				array[i + 1] = unpack(packed, i + 1);
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
	public final void set(int index, byte value) {
		getAndSet(index, value);
	}

	/**
	 * Sets an element to the given value, but the update may not happen
	 * immediately
	 *
	 * @param index the index
	 * @param value the new value
	 */
	public final void lazySet(int index, byte value) {
		set(index, value);
	}

	/**
	 * Sets the element at the given index, but only if the previous value was
	 * the expected value. This may fail spuriously.
	 *
	 * @param index the index
	 * @param expected the expected value
	 * @param newValue the new value
	 * @return true on success
	 */
	public final boolean weakCompareAndSet(int index, byte expected, byte newValue) {
		return compareAndSet(index, expected, newValue);
	}

	/**
	 * Atomically adds a delta to an element, and gets the new value.
	 *
	 * @param index the index
	 * @param delta the delta to add to the element
	 * @return the new value
	 */
	public final byte addAndGet(int index, byte delta) {
		return addAndGet(index, delta, false);
	}

	/**
	 * Atomically adds a delta to an element, and gets the old value.
	 *
	 * @param index the index
	 * @param delta the delta to add to the element
	 * @return the old value
	 */
	public final byte getAndAdd(int index, byte delta) {
		return addAndGet(index, delta, true);
	}

	/**
	 * Atomically increments an element and returns the old value.
	 *
	 * @param index the index
	 * @return the old value
	 */
	public final byte getAndIncrement(int index) {
		return getAndAdd(index, (byte) 1);
	}

	/**
	 * Atomically decrements an element and returns the old value.
	 *
	 * @param index the index
	 * @return the old value
	 */
	public final byte getAndDecrement(int index) {
		return getAndAdd(index, (byte) -1);
	}

	/**
	 * Atomically increments an element and returns the new value.
	 *
	 * @param index the index
	 * @return the new value
	 */
	public final byte incrementAndGet(int index) {
		return addAndGet(index, (byte) 1);
	}

	/**
	 * Atomically decrements an element and returns the new value.
	 *
	 * @param index the index
	 * @return the new value
	 */
	public final byte decrementAndGet(int index) {
		return addAndGet(index, (byte) -1);
	}

	/**
	 * Gets an array containing all the values in the array.
	 *
	 * The returned values are not guaranteed to be from the same time instant.
	 *
	 * @return the array
	 */
	public byte[] getArray() {
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
		byte[] array = getArray();
		return Arrays.toString(array);
	}

	private short getPacked(int index) {
		return backingArray.get(index >> 1);
	}

	private short pack(byte even, byte odd) {
		return (short) (even << 8 | odd & 0xFF);
	}

	private byte unpack(short packed, int index) {
		boolean even = (index & 1) == 0;
		if (even) {
			return (byte) (packed >> 8);
		} else {
			return (byte) packed;
		}
	}

	private byte unpackEven(short packed) {
		return (byte) (packed >> 8);
	}

	private byte unpackOdd(short packed) {
		return (byte) packed;
	}

	private boolean isEven(int index) {
		return (index & 1) == 0;
	}
}
