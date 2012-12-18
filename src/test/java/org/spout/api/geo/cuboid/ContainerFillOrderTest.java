/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.geo.cuboid;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;
import org.spout.api.geo.cuboid.ContainerFillOrder;
import org.spout.api.util.hashing.ByteTripleHashed;

public class ContainerFillOrderTest {
	
	private static final int REPEATS = 20;

	private static final int SIZE_X = 4;
	private static final int SIZE_Y = 3;
	private static final int SIZE_Z = 7;
	
	private static final int VOLUME = SIZE_X * SIZE_Y * SIZE_Z;
	
	private static ContainerFillOrder[] values = ContainerFillOrder.values();
	

	@Test
	public void test() {
		
		int[] source = new int[VOLUME];
		int[] dest = new int[VOLUME];
		
		init(source); // inits to XYZ

		Random random = new Random();
		
		ContainerFillOrder current = ContainerFillOrder.XYZ;
		for (int i = 0; i < REPEATS; i++) {
			current = convert(source, dest, current, random);
		}
	}
	
	private static ContainerFillOrder convert(int[] source, int[] dest, ContainerFillOrder old, Random random) {
		ContainerFillOrder midOrder = values[random.nextInt(values.length)];
		ContainerFillOrder newOrder = values[random.nextInt(values.length)];
		
		System.out.println("Checking conversion from " + old + " to " + midOrder + " to " + newOrder);
		
		copy(source, old, dest, midOrder);
		
		checkArray(dest, midOrder);
		
		copy(dest, midOrder, source, newOrder);
		
		checkArray(source, newOrder);
		
		return newOrder;
	}
	
	private static void init(int[] source) {
		int index = 0;
		for (int z = 0; z < SIZE_Z; z++) {
			for (int y = 0; y < SIZE_Y; y++) {
				for (int x = 0; x < SIZE_X; x++) {
					source[index++] = ByteTripleHashed.key(x, y, z);
				}
			}
		}
	}
	
	private static void copy(int[] source, ContainerFillOrder sourceOrder, int[] dest, ContainerFillOrder destOrder) {
		int sourceIndex = 0;
		int targetIndex = 0;

		int thirdStep = destOrder.thirdStep(sourceOrder, SIZE_X, SIZE_Y, SIZE_Z);
		int secondStep = destOrder.secondStep(sourceOrder, SIZE_X, SIZE_Y, SIZE_Z);
		int firstStep = destOrder.firstStep(sourceOrder, SIZE_X, SIZE_Y, SIZE_Z);

		int thirdMax = destOrder.getThirdSize(SIZE_X, SIZE_Y, SIZE_Z);
		int secondMax = destOrder.getSecondSize(SIZE_X, SIZE_Y, SIZE_Z);
		int firstMax = destOrder.getFirstSize(SIZE_X, SIZE_Y, SIZE_Z);
		
		for (int third = 0; third < thirdMax; third++) {
			int secondStart = sourceIndex;
			for (int second = 0; second < secondMax; second++) {
				int firstStart = sourceIndex;
				for (int first = 0; first < firstMax; first++) {
					dest[targetIndex++] = source[sourceIndex];
					sourceIndex += firstStep;
				}
				sourceIndex = firstStart + secondStep;
			}
			sourceIndex = secondStart + thirdStep;
		}
	}
	
	private static void checkArray(int[] array, ContainerFillOrder expected) {
		for (int i = 0; i < array.length; i++) {
			checkFirst(array, i, expected);	
			checkSecond(array, i, expected);	
			checkThird(array, i, expected);	
		}
	}
	
	private static void checkFirst(int [] array, int i, ContainerFillOrder expectedOrder) {
		int firstRepeat = expectedOrder.getFirstSize(SIZE_X, SIZE_Y, SIZE_Z);
		int exp = i % firstRepeat;
		int got;
		switch (expectedOrder) {
			case XYZ:
			case XZY: got = ByteTripleHashed.key1(array[i]); break;
			case YZX:
			case YXZ: got = ByteTripleHashed.key2(array[i]); break;
			case ZXY:
			case ZYX: got = ByteTripleHashed.key3(array[i]); break;
			default: assertTrue(false); return;
		}
		assertTrue("First element mismatch converting to " + expectedOrder + " expected " + exp + ", got " + got, exp == got);
	}
	
	private static void checkSecond(int [] array, int i, ContainerFillOrder expectedOrder) {
		int firstRepeat = expectedOrder.getFirstSize(SIZE_X, SIZE_Y, SIZE_Z);
		int secondRepeat = expectedOrder.getSecondSize(SIZE_X, SIZE_Y, SIZE_Z);
		int exp = (i / firstRepeat) % secondRepeat;
		int got;
		switch (expectedOrder) {
			case ZXY:
			case YXZ: got = ByteTripleHashed.key1(array[i]); break;
			case XYZ:
			case ZYX: got = ByteTripleHashed.key2(array[i]); break;
			case XZY:
			case YZX: got = ByteTripleHashed.key3(array[i]); break;
			default: assertTrue(false); return;
		}
		assertTrue("Second element mismatch converting to " + expectedOrder + " expected " + exp + ", got " + got, exp == got);
	}
	
	private static void checkThird(int [] array, int i, ContainerFillOrder expectedOrder) {
		int firstRepeat = expectedOrder.getFirstSize(SIZE_X, SIZE_Y, SIZE_Z);
		int secondRepeat = expectedOrder.getSecondSize(SIZE_X, SIZE_Y, SIZE_Z);
		int thirdRepeat = expectedOrder.getThirdSize(SIZE_X, SIZE_Y, SIZE_Z);
		int exp = (i / firstRepeat / secondRepeat) % thirdRepeat;
		int got;
		switch (expectedOrder) {
			case ZYX:
			case YZX: got = ByteTripleHashed.key1(array[i]); break;
			case XZY:
			case ZXY: got = ByteTripleHashed.key2(array[i]); break;
			case XYZ:
			case YXZ: got = ByteTripleHashed.key3(array[i]); break;
			default: assertTrue(false); return;
		}
		assertTrue("Third element mismatch converting to " + expectedOrder + " expected " + exp + ", got " + got, exp == got);
	}
	
}
