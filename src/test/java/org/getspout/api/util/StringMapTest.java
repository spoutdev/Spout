package org.getspout.api.util;

import static org.junit.Assert.assertTrue;

import org.getspout.api.io.store.MemoryStore;
import org.junit.Before;
import org.junit.Test;

public class StringMapTest {
	
	private StringMap testMap;
	private MemoryStore<Integer> store;
	
	@Before
	public void setUp() {
		store = new MemoryStore<Integer>();
		testMap = new StringMap(null, store, 0, 100);
	}

	@Test
	public void testRegistry() {
		testMap.register("key1");
		final int idOne = store.get("key1");
		testMap.register("key1");
		assertTrue(idOne == store.get("key1"));
		testMap.register("key2");
		assertTrue(idOne != store.get("key2"));
	}
}
