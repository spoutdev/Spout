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
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.spout.api.math.MathHelper;

/**
 * This class implements a variable width Atomic array.  It is backed by an AtomicInteger array.<br>
 * <br>
 * Entries widths can be a power of 2 from 1 to 32
 */
public class AtomicVariableWidthArray implements Serializable {
	
	private static final long serialVersionUID = 423785245671235L;
	
	private final static int[] log2 = new int[33];
	
	static {
		log2[1] = 0;
		log2[2] = 1;
		log2[4] = 2;
		log2[8] = 3;
		log2[16] = 4;
		log2[32] = 5;
	}
	
	private final boolean fullWidth;

	private final int indexShift;
	private final int subIndexMask;
	private final int[] valueBitmask;
	private final int[] valueShift;
	private final int maxValue;
	private final int width;

	private AtomicIntegerArray array;

	private final int length;
	
	/**
	 * Creates a variable Atomic array.  The width must be a power of two from 1 to 32 and the length must be a multiple of the number of elements that fit in an int after packing.
	 * 
	 * @param length the length of the array
	 * @param width the number of bits in each entry
	 */
	public AtomicVariableWidthArray(int length, int width) {
		if (MathHelper.roundUpPow2(width) != width || width < 1 || width > 32) {
			throw new IllegalArgumentException("Width must be a power of 2 between 1 and 32 " + width);
		}
		
		indexShift = 5 - log2[width];
		subIndexMask = (1 << indexShift) - 1;
		
		int valuesPerInt = 32 / width;
		
		valueBitmask = new int[valuesPerInt];
		valueShift = new int[valuesPerInt];
		
		for (int i = 0; i < valuesPerInt; i++) {
			valueShift[i] = i * width;
			valueBitmask[i] = ((1 << width) - 1) << valueShift[i];
		}
		
		this.length = length;
		
		int newLength = length / valuesPerInt;
		
		if (newLength * valuesPerInt != length) {
			throw new IllegalArgumentException("The length must be a multiple of " + valuesPerInt + " for arrays of width " + width);
		}
		
		this.array = new AtomicIntegerArray(newLength);
		
		this.fullWidth = width == 32;
		
		this.maxValue = this.fullWidth ? -1 : valueBitmask[0];
		
		this.width = width;
	}
	
	/**
	 * Gets the maximum unsigned value that can be stored in the array
	 * 
	 * @return the max value
	 */
	public int getMaxValue() {
		return maxValue;
	}

	/**
	 * Gets an element from the array at a given index
	 *
	 * @param i the index
	 * @return the element
	 */
	public final int get(int i) {
		if (fullWidth) {
			return array.get(i);
		}

		return unPack(array.get(getIndex(i)), getSubIndex(i));
	}
	
	/**
	 * Sets an element to the given value
	 *
	 * @param i the index
	 * @param newValue the new value
	 */
	public final void set(int i, int newValue) {
		if (fullWidth) {
			array.set(i,  newValue);
			return;
		}

		boolean success = false;
		int index = getIndex(i);
		int subIndex = getSubIndex(i);
		while (!success) {
			int prev = array.get(index);
			int next = pack(prev, newValue, subIndex);
			success = array.compareAndSet(index, prev, next);
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
		if (fullWidth) {
			return array.compareAndSet(i, expect, update);
		}

		boolean success = false;
		int index = getIndex(i);
		int subIndex = getSubIndex(i);
		while (!success) {
			int prev = array.get(index);
			if (unPack(prev, subIndex) != expect) {
				return false;
			}

			int next = pack(prev, update, subIndex);
			success = array.compareAndSet(index, prev, next);
		}
		return true;
	}
	
	/**
	 * Sets an element in the array at a given index and returns the old value
	 *
	 * @param i the index
	 * @param newValue the new value
	 * @return the old value
	 */
	public final int getAndSet(int i, int newValue) {
		if (fullWidth) {
			return array.getAndSet(i, newValue);
		}

		boolean success = false;
		int index = getIndex(i);
		int subIndex = getSubIndex(i);
		int prev = 0;
		while (!success) {
			prev = array.get(index);
			int next = pack(prev, newValue, subIndex);
			success = array.compareAndSet(index, prev, next);
		}
		return unPack(prev, subIndex);
	}
	
	private final int addAndGet(int i, int delta, boolean old) {
		if (fullWidth) {
			if (old) {
				return array.getAndAdd(i, delta);
			} else {
				return array.addAndGet(i, delta);
			}
		}

		boolean success = false;
		int index = getIndex(i);
		int subIndex = getSubIndex(i);
		int prev = 0;
		int prevValue = 0;
		int newValue = 0;
		while (!success) {
			prev = array.get(index);
			prevValue = unPack(prev, subIndex);
			newValue = prevValue + delta;
			int next = pack(prev, newValue, subIndex);
			success = array.compareAndSet(index, prev, next);
		}
		return (old ? prevValue : newValue) & valueBitmask[0];
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
	 * Gets the width of the array
	 * 
	 * @return the width
	 */
	public final int width() {
		return width;
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
	
	/*
	 * Remaining methods use the above methods
	 */
	
	public int addAndGet(int i, int delta) {
		return addAndGet(i, delta, false);
	}
	
	public int getAndAdd(int i, int delta) {
		return addAndGet(i, delta, true);
	}
	
	public int incrementAndGet(int i) {
		return addAndGet(i, 1);
	}
	
	public int decrementAndGet(int i) {
		return addAndGet(i, -1);
	}
	
	public int getAndIncrement(int i) {
		return getAndAdd(i, 1);
	}
	
	public int getAndDecrement(int i) {
		return getAndAdd(i, -1);
	}
	
	private final int getIndex(int i) {
		return i >> indexShift;
	}
	
	private final int getSubIndex(int i) {
		return subIndexMask & i;
	}
	
	private final int unPack(int packed, int subIndex) {
		int v = (packed & valueBitmask[subIndex]) >>> valueShift[subIndex];
		return v;
	}
	
	private final int pack(int prev, int newValue, int subIndex) {
		int bitmask = valueBitmask[subIndex];
		int shift = valueShift[subIndex];
		int result = (prev & ~bitmask) | (bitmask & (newValue << shift));
		return result;
	}
	
}
