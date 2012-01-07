package org.getspout.api.util.map.concurrent;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class AtomicShortArrayTest {

	private final static int LENGTH = 10000;
	
	private AtomicShortArray array = new AtomicShortArray(LENGTH);
	
	private short[] arrayData;
	private int[] arrayIndex;
	
	@Before
	public void setUp() {
		
		Random rand = new Random();
		
		arrayData = new short[LENGTH];
		arrayIndex = new int[LENGTH];
		
		for (int i = 0; i < LENGTH; i++) {
			arrayData[i] = (short)rand.nextInt();
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

		Random rand = new Random();
		
		for (int i = 0; i < LENGTH; i++) {
			int index = arrayIndex[i];
			array.set(index, arrayData[index]);
		}
		
		for (int i = 0; i < LENGTH; i++) {
			assertTrue("Array data mismatch", array.get(i) == arrayData[i]);
		}
		
		for (int i = 0; i < LENGTH; i++) {
			compareAndSetTrue(rand.nextInt(LENGTH), (short)rand.nextInt());
			compareAndSetFalse(rand.nextInt(LENGTH), (short)rand.nextInt());
		}
		
		for (int i = 0; i < LENGTH; i++) {
			assertTrue("Array data mismatch after compare and set updates", array.get(i) == arrayData[i]);
		}

	}
	
	private void compareAndSetTrue(int index, short value) {
		assertTrue("Compare and set attempt failed, expected value incorrect", array.compareAndSet(index, arrayData[index], value));
		arrayData[index] = value;
	}
	
	private void compareAndSetFalse(int index, short value) {
		assertTrue("Compare and set attempt succeeded when it should have failed", !array.compareAndSet(index, (short)(1 + arrayData[index]), value));
	}
	
}
