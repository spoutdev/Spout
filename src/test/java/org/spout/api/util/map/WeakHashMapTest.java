package org.spout.api.util.map;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WeakHashMapTest {
	
	private final static boolean RUN_TEST = true;
	private final static int ARRAY_SIZE = 10 * 1024 * 1024;
	
	@Test
	public void test() {
		
		if (!RUN_TEST) {
			return;
		}
		
		WeakHashMap<Integer, byte[]> map = new WeakHashMap<Integer, byte[]>();
		
		HashMap<Integer, WeakHashMap<Integer, byte[]>.KeyReference> internalMap = map.map;
		
		byte[][] hardArray = new byte[10][];
		
		for (int i = 0; i < 100; i++) {
			byte[] arr = new byte[ARRAY_SIZE];
			if (i < 10) {
				hardArray[i] = arr;
			}
			map.put(i, arr);
		}
		
		for (int i = 0; i < 20; i++) {
			System.gc();
			map.pollQueue();
		}
		
		for (int i = 0; i < 10; i++) {
			assertTrue("Hard linked array lost " + i, map.get(i).equals(hardArray[i]));
		}
		
		assertTrue("Weak references weren't completely garbage collected", internalMap.size() == 10);
		
	}

}
