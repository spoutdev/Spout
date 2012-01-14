package org.spout.api.util.map.concurrent;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;
import org.spout.api.basic.blocks.BlockFullState;

public class AtomicIntBlockStoreTest {

	private final static int MAX_SIZE = 4096;

	private AtomicBlockStore<Integer> store = new AtomicBlockStore<Integer>(4);

	private short[] ids = new short[MAX_SIZE];
	private short[] data = new short[MAX_SIZE];;
	private Integer[] auxData = new Integer[MAX_SIZE];

	int arraySize = -1;
	
	@Test
	public void testArray() {
		
		Random rand = new Random();
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 16; y++) {
					short id = (short)(rand.nextInt());
					short data = (short)(((rand.nextInt() & 0x3) != 0) ? (0) : (rand.nextInt()));
					Integer auxData = (((rand.nextInt() & 0x3) != 0) ? (null) : (rand.nextInt()));
					set(x, y, z, id, data, auxData);
				}
			}
		}
		
		System.out.println("Starting check - pass 1");

		BlockFullState<Integer> fullData = new BlockFullState<Integer>();
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 16; y++) {
					check(x, y, z, fullData);
				}
			}
		}
		
		System.out.println("Starting random access");

		for (int i = 0; i < 32768; i++) {
			short id = (short)(rand.nextInt());
			short data = (short)(((rand.nextInt() & 0x3) != 0) ? (0) : (rand.nextInt()));
			Integer auxData = (((rand.nextInt() & 0x3) != 0) ? (null) : (rand.nextInt()));
			int x = rand.nextInt() & 0xF;
			int y = rand.nextInt() & 0xF;
			int z = rand.nextInt() & 0xF;
			if (rand.nextBoolean()) {
				set(x, y, z, id, data, auxData);
			} else {
				compareAndSet(x, y, z, (short)id, (short)data, auxData, rand.nextBoolean(), rand);
			}
			check(x, y, z, fullData);
		}
		
		System.out.println("Starting check - pass 2");
		
		for (int x = 15; x >= 0; x--) {
			for (int y = 15; y >= 0; y--) {
				for (int z = 15; z >= 0; z--) {
					check(x, y, z, fullData);
				}
			}
		}
		
		int entries = 0;
		for (int x = 15; x >= 0; x--) {
			for (int y = 15; y >= 0; y--) {
				for (int z = 15; z >= 0; z--) {
					fullData = store.getFullData(x, y, z, null);
					if (fullData.getAuxData() != null || fullData.getData() != 0 || (fullData.getId() & 0xC000) == 0xC000 ) {
						entries++;
					}
				}
			}
		}
		int actualEntries = store.getEntries();
		
		assertTrue("Memory leak in the store, expected " + entries + " entries but got " + actualEntries + " entries", entries == actualEntries);

		System.out.println("Store length: " + store.getSize());
		
	}
	
	private void set(int x, int y, int z, int id, int data, Integer auxData) {
		
		int index = getIndex(x, y, z);
		
		this.ids[index] = (short)id;
		this.data[index] = (short)data;
		this.auxData[index] = auxData;
		
		store.setBlock(x, y, z, (short)id, (short)data, auxData);
		
		checkForResize();
		
	}
	
	private void compareAndSet(int x, int y, int z, short id, short data, Integer auxData, boolean useCorrectExpect, Random rand) {
		
		int index = getIndex(x, y, z);

		short expectId = this.ids[index];
		short expectData = this.data[index];
		Integer expectAuxData = this.auxData[index];

		if (!useCorrectExpect) {
			switch (rand.nextInt(3)) {
			case 0: expectId++; break;
			case 1: expectData++; break;
			case 2: expectAuxData = (expectAuxData == null) ? 0 : (expectAuxData + 1); break;
			}
		}
		
		boolean success = store.compareAndSetBlock(x, y, z, expectId, expectData, expectAuxData, id, data, auxData);
		
		if (useCorrectExpect) {
			assertTrue("Compare and set with correct expect was unsuccessful", success);
			this.ids[index] = (short)id;
			this.data[index] = (short)data;
			this.auxData[index] = auxData;
		} else {
			assertTrue("Compare and set with incorrect expect was successful", !success);
		}
		
		checkForResize();
		
	}
	
	private void check(int x, int y, int z, BlockFullState<Integer> fullData) {
		int index = getIndex(x, y, z);
		
		fullData = store.getFullData(x, y, z, null);
		assertTrue("Record read at " + x + ", " + y + ", " + z + " has wrong short data", fullData.getData() == data[index]);
		assertTrue("Record read at " + x + ", " + y + ", " + z + " has wrong short data", fullData.getId() == ids[index]);
		assertTrue("Record read at " + x + ", " + y + ", " + z + " has wrong short data", fullData.getAuxData() == auxData[index]);		
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
		}
		
	}
	

}
