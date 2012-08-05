package org.spout.api.util.concurrent;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

public class AtomicFloatTest {

	@Test
	public void test() {
		assertEquals("Floats not equal", new AtomicFloat(0F), new AtomicFloat(0F));
		
		AtomicFloat value = new AtomicFloat(-1F);
		
		assertTrue("Float does not match expected value", 0F == value.addAndGet(1F));
		
		assertTrue("Float does not match expected value", 0F == value.getAndAdd(1F));
		
		assertTrue("Float does not match expected value", -1F == value.addAndGet(-2F));
		
		Random rand = new Random();
		float test = rand.nextFloat();
		value.set(test);
		assertTrue("Float does not match expected value", test == value.get());
		
		value.lazySet(0F);
		assertTrue("Float does not match expected value", 0F == value.get());
		
		if (value.compareAndSet(1F, 1F)) {
			fail("Compared and set did not accurately compare the current value");
		}
		
		if (!value.compareAndSet(0F, 1F)) {
			fail("Compared and set did not accurately compare the current value");
		}
	}

}
