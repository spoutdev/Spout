package org.spout.api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.spout.api.io.store.MemoryStore;
import org.junit.Before;
import org.junit.Test;

public class StringMapTest {
	
	private StringMap subject;
	private MemoryStore<Integer> store;
	
	@Before
	public void setUp() {
		store = new MemoryStore<Integer>();
		subject = new StringMap(null, store, 0, 100);
	}

	@Test
	public void testKeyRegistries() {
		
		// Check that the store is returning null on an unused key
		assertNull(store.get("key1"));
		
		// Check if the first registered value is the first possible entry, 0
		subject.register("key1");
		assertEquals(store.get("key1").intValue(), 0);
		
		// Check if registering multiple times and multiple keys does not interfere with previously
		// registered values.
		subject.register("key1");
		subject.register("key2");
		assertEquals(store.get("key1").intValue(), 0);
		assertEquals(store.get("key2").intValue(), 1);
	}
}
