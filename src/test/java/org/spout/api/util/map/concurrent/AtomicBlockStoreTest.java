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
import org.spout.api.material.block.BlockFullState;

public class AtomicBlockStoreTest {
	private final static int MAX_SIZE = 4096;

	private AtomicBlockStore<Integer> store = new AtomicBlockStore<Integer>(4);

	private short[] ids = new short[MAX_SIZE];
	private short[] data = new short[MAX_SIZE];

	int arraySize = -1;

	@Test
	public void testArray() {
		Random rand = new Random();

		System.out.println("-- Filling store with random data --");

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 16; y++) {
					short id = (short)(rand.nextInt());
					short data = (short)(((rand.nextInt() & 0x3) != 0) ? (0) : (rand.nextInt()));
					set(x, y, z, id, data);
				}
			}
		}

		System.out.println();
		System.out.println("-- Starting check - pass 1 --");

		checkStoreValues();

		System.out.println();
		System.out.println("-- Starting random access --");

		for (int i = 0; i < 32768; i++) {
			short id = (short)(rand.nextInt());
			short data = (short)(((rand.nextInt() & 0x3) != 0) ? (0) : (rand.nextInt()));
			int x = rand.nextInt() & 0xF;
			int y = rand.nextInt() & 0xF;
			int z = rand.nextInt() & 0xF;
			if (rand.nextBoolean()) {
				set(x, y, z, id, data);
			} else {
				compareAndSet(x, y, z, id, data, rand.nextBoolean(), rand);
			}
			check(x, y, z);
		}

		System.out.println();
		System.out.println("-- Starting check - pass 2 --");

		checkStoreValues();

		checkStoreLeaks();

		System.out.println();
		System.out.println("-- Starting element removal test --");

		for (int pass = 0; pass < 10; pass++) {
			System.out.println("Removing 1/3 of the entries");
			for (int x = 15; x >= 0; x--) {
				for (int y = 15; y >= 0; y--) {
					for (int z = 15; z >= 0; z--) {
						if (rand.nextInt(3) == 0) {
							set(x, y, z, 0, 0);
						}
					}
				}
			}
			checkStoreValues();

			checkStoreLeaks();

			if (store.needsCompression()) {
				System.out.println("Compressing store");
				int size = store.getSize();
				store.compress();
				int newSize = store.getSize();
				System.out.println("Size change: " + size + "->" + newSize);
				assertTrue("Compression didn't reduce the store size when needsCompression returned true.", size > newSize);
				checkStoreValues();

				checkStoreLeaks();
			} else {
				System.out.println("Compression is not needed");
			}

			checkStoreCompressed();
		}
	}

	private void set(int x, int y, int z, int id, int data) {
		int index = getIndex(x, y, z);

		this.ids[index] = (short)id;
		this.data[index] = (short)data;

		store.setBlock(x, y, z, (short)id, (short)data);

		checkForResize();
	}

	private void compareAndSet(int x, int y, int z, short id, short data, boolean useCorrectExpect, Random rand) {
		int index = getIndex(x, y, z);

		short expectId = this.ids[index];
		short expectData = this.data[index];

		if (!useCorrectExpect) {
			switch (rand.nextInt(2)) {
				case 0: expectId++;
					break;
				case 1: expectData++;
					break;
			}
		}

		boolean success = store.compareAndSetBlock(x, y, z, expectId, expectData, id, data);

		if (useCorrectExpect) {
			assertTrue("Compare and set with correct expect was unsuccessful", success);
			this.ids[index] = id;
			this.data[index] = data;
		} else {
			assertTrue("Compare and set with incorrect expect was successful", !success);
		}

		checkForResize();
	}

	private void check(int x, int y, int z) {
		int index = getIndex(x, y, z);

		BlockFullState fullData = store.getFullData(x, y, z);
		assertTrue("Record read at " + x + ", " + y + ", " + z + " has wrong short data", fullData.getData() == data[index]);
		assertTrue("Record read at " + x + ", " + y + ", " + z + " has wrong short data", fullData.getId() == ids[index]);
	}

	private final static int getIndex(int x, int y, int z) {
		int index = 0;
		index = (index << 4) | (x & 0xF);
		index = (index << 4) | (z & 0xF);
		index = (index << 4) | (y & 0xF);
		return index;
	}

	private void checkForResize() {
		int storeLength = store.getSize();

		if (arraySize != storeLength) {
			arraySize = storeLength;
			System.out.println("Store length changed to " + storeLength + ", entries = " + store.getEntries());
			System.out.println();
		}
	}

	private void checkStoreLeaks() {
		int entries = 0;
		for (int x = 15; x >= 0; x--) {
			for (int y = 15; y >= 0; y--) {
				for (int z = 15; z >= 0; z--) {
					BlockFullState fullData = store.getFullData(x, y, z);
					if (fullData.getData() != 0 || (fullData.getId() & 0xC000) == 0xC000 ) {
						entries++;
					}
				}
			}
		}
		int actualEntries = store.getEntries();

		System.out.println("Memory leak test");
		System.out.println("Expected array usage: " + store.getEntries() + "/" + store.getSize());
		System.out.println("Actual array usage:   " + store.getEntries() + "/" + store.getSize());
		System.out.println();
		assertTrue("Memory leak in the store, expected " + entries + " entries but got " + actualEntries + " entries", entries == actualEntries);
	}

	private void checkStoreValues() {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 16; y++) {
					check(x, y, z);
				}
			}
		}
		System.out.println("Value check test passed");
		System.out.println();
	}

	private void checkStoreCompressed() {
		double loadFactor = store.getEntries() / (double)store.getSize();
		System.out.println("Load factor test: " + store.getEntries() + "/" + store.getSize() + " (0.37 < " + loadFactor + " < 0.76)");
		System.out.println();
		// Technically the range is 0.375 to 0.75, but this covers rounding error
		assertTrue("Load factor out of range after compression " + loadFactor, loadFactor > 0.37 && loadFactor < 0.76);
	}
}
