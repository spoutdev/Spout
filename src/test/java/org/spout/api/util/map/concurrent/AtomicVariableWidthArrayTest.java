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

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class AtomicVariableWidthArrayTest {
	private final static int LENGTH = 16384;

	private AtomicVariableWidthArray array;
	private int valueMask;
	private int width;

	private int[] arrayData;
	private int[] arrayIndex;

	public void setup(int width) {
		if (width == 32) {
			valueMask = -1;
		} else {
			valueMask = (1 << width) - 1;
		}
		this.width = width;
		
		array = new AtomicVariableWidthArray(LENGTH, width);
		
		Random rand = new Random();

		arrayData = new int[LENGTH];
		arrayIndex = new int[LENGTH];

		for (int i = 0; i < LENGTH; i++) {
			arrayData[i] = (short)rand.nextInt() & valueMask;
			arrayIndex[i] = i;
		}

		shuffle(arrayIndex);
	}

	private void shuffle(int[] deck) {
		Random rand = new Random();

		for (int placed = 0; placed < deck.length; placed++) {
			int remaining = deck.length - placed;
			int newIndex = rand.nextInt(remaining);
			swap(deck, placed, newIndex + placed);
		}
	}

	private void swap(int[] array, int i1, int i2) {
		int temp = array[i1];
		array[i1] = array[i2];
		array[i2] = temp;
	}

	@Test
	public void testArray() {
		for (int i = 1; i <= 32; i = i << 1) {
			setup(i);
			testArray(i);
		}
	}
		
	public void testArray(int width) {
		Random rand = new Random();

		for (int i = 0; i < LENGTH; i++) {
			int index = arrayIndex[i];
			array.set(index, arrayData[index]);
		}

		for (int i = 0; i < LENGTH; i++) {
			assertTrue("Width = " + width + " Array data mismatch: " + array.get(i) + ":" + arrayData[i], array.get(i) == arrayData[i]);
		}

		for (int i = 0; i < LENGTH; i++) {
			compareAndSetTrue(rand.nextInt(LENGTH), (short)rand.nextInt());
			compareAndSetFalse(rand.nextInt(LENGTH), (short)rand.nextInt());
		}

		for (int i = 0; i < LENGTH; i++) {
			assertTrue("Width = " + width + " Array data mismatch after compare and set updates", array.get(i) == arrayData[i]);
		}
	}

	private void compareAndSetTrue(int index, int value) {
		assertTrue("Width = " + width + " Compare and set attempt failed, index = " + index + ", expected value incorrect " + array.get(index) + " expected " + value, array.compareAndSet(index, arrayData[index], value));
		arrayData[index] = value  & valueMask;
	}

	private void compareAndSetFalse(int index, int value) {
		assertTrue("Width = " + width + " Compare and set attempt succeeded, index = " + index + ", when it should have failed", !array.compareAndSet(index, (short)(1 + arrayData[index]), value));
	}
}
