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
import org.spout.api.util.map.concurrent.palette.AtomicShortIntArray;

public class AtomicShortIntArrayTest {
	
	final AtomicShortIntArray a = new AtomicShortIntArray(256);
	final int[] copy = new int[256];
	final int COUNT = 16384;
	final int THREADS = 8;
	final int THREAD_REPEATS = 8;

	@Test
	public void repeatTest() {
		
		printTest("Repeat Test");
		
		for (int i = 0; i < a.length(); i++) {
			set(i, 12345);
		}
		
		checkWidth(1);
		
		for (int i = 0; i < a.length(); i+=2) {
			set(i, 54321);
		}
		
		checkWidth(2);
		
		printPaletteUse("after 2 unique entries");
		
		for (int i = 0; i < a.length(); i++) {
			check(i);
		}
	}
	
	@Test
	public void rampTest() {
		
		printTest("Ramp Test");
		
		for (int i = 0; i < a.length(); i++) {
			set(i, i);
		}
		
		printPaletteUse("after ramp: ");
		
		checkWidth(8);
		
		for (int i = 0; i < a.length(); i++) {
			check(i);
		}
	}
	
	@Test
	public void randomTest() {
		
		printTest("Random Test");
		
		Random r = new Random();
		
		System.out.println("Setting 1024 random values");
		
		for (int i = 0; i < 1024; i++) {
			set(r.nextInt(256), r.nextInt());
		}
		
		for (int i = 0; i < 256; i++) {
			check(i);
		}
		
		printPaletteUse("after setting 1024 values: ");
		
		checkWidth(8);
		
		a.compress();
		
		checkWidth(8);
		
		printPaletteUse("after compression: ");
		
		System.out.println("Checking array");
		
		for (int i = 0; i < 256; i++) {
			check(i);
		}
		
		System.out.println("Setting 1024 random values");
		
		for (int i = 0; i < 1024; i++) {
			set(r.nextInt(256), r.nextInt());
		}
		
		checkWidth(8);
		
		for (int i = 0; i < 256; i++) {
			check(i);
		}
		
		printPaletteUse(" after 1024 random values: ");
		
		System.out.println("Setting 128 equal values");
		
		for (int i = 0; i < 128; i++) {
			set(i, 123);
		}
		
		printPaletteUse("after 128 equal values: ");

		System.out.println("Compressing");
		
		a.compress();
		
		checkWidth(8);
		
		printPaletteUse("after compression: ");
		
		System.out.println("Setting 246 equal values");
		
		for (int i = 0; i < 256; i++) {
			if (i < 246) {
				set(i, 321);
			} else {
				set(i, i);
			}
		}
		
		printPaletteUse("after 246 equal values: ");

		System.out.println("Compressing");
		
		a.compress();
		
		checkWidth(4);
		
		printPaletteUse("after compression: ");
		
		System.out.println("Checking array");
		
		for (int i = 0; i < 256; i++) {
			check(i);
		}
	}
	
	@Test
	public void compressionTest() {

		printTest("Compression Test");
		
		Random r = new Random();
		
		checkCompress(1, 1, r.nextInt());
		
		checkCompress(4, 2, r.nextInt());
		
		checkCompress(15, 4, r.nextInt());
		
		checkCompress(8, 4, r.nextInt());
		
		checkCompress(256, 8, r.nextInt());

		checkCompress(16, 4, r.nextInt());
		
		checkCompress(9, 4, r.nextInt());
		
		checkCompress(64, 8, r.nextInt());
		
		checkCompress(17, 8, r.nextInt());
		
		checkCompress(8, 4, r.nextInt());
		
		checkCompress(4, 2, r.nextInt());
		
		checkCompress(128, 8, r.nextInt());
		
		System.out.println("Checking array");
		
		for (int i = 0; i < 256; i++) {
			check(i);
		}
	}
	
	private void checkCompress(int unique, int expWidth, int base) {
		System.out.println("Setting 256 values from a set of " + unique);
		
		for (int i = 0; i < 256; i++) {
			set(i, base + (i % unique));
		}
		
		printPaletteUse("before compression: ");

		a.compress();
		
		printPaletteUse("after compression: ");
		
		checkWidth(expWidth);
		
		System.out.println("");
	}
	
	Exception parallelException = null;
	Error parallelError = null;
	
	@Test
	public void parallel() {
		
		printTest("Parallel Test");
		
		for (int i = 0; i < THREAD_REPEATS; i++) {
			parallelRun(i);
		}
	}
	
	private void parallelRun(int repeat) {
		
		for (int i = 0; i < 256; i++) {
			a.set(i, 0);
		}
		a.compress();
		
		Thread[] thread = new Thread[THREADS];
		
		for (int i = 0; i < THREADS; i++) {
			thread[i] = new ArrayTest(i, THREADS, COUNT);
		}
		
		long start = System.nanoTime();
		
		for (int i = 0; i < THREADS; i++) {
			thread[i].start();
		}
		
		for (int i = 0; i < THREADS; i++) {
			try {
				thread[i].join();
			} catch (InterruptedException ie) {
				throw new RuntimeException(ie);
			}
		}
		
		System.out.println(repeat + ") Thread runtime: " + (((System.nanoTime() - start) / 1000) / 1000.0F) + " ms");
		
		if (parallelException != null) {
			throw new RuntimeException("Exception thrown by thread", parallelException);
		}
		
		if (parallelError != null) {
			throw new RuntimeException("Error thrown by thread", parallelError);
		}
		
	}
	
	private void printPaletteUse(String message) {
		System.out.println("Palette usage " + message + " " + a.getPaletteUsage() + " / " + a.getPaletteSize());
	}
	
	private void checkWidth(int exp) {
		assertTrue("Internal array has wrong width, got " + a.width() + ", exp " + exp, a.width() == exp);
	}
	
	private void set(int i, int value) {
		check(i);
		a.set(i, value);
		copy[i] = value;
		check(i);
	}
	
	private int get(int i) {
		check(i);
		return a.get(i);
	}
	
	private void check(int i) {
		int old = a.get(i);
		assertTrue("Old value did not match expected at position " + i + " (got " + old + ", expected " + copy[i] + ")", old == copy[i]);
	}
	
	private class ArrayTest extends Thread {
		
		public final int count;
		
		public final int base;
		public final int step;
		
		public int[] value = new int[a.length()];
		
		public ArrayTest(int base, int step, int count) {
			this.base = base;
			this.step = step;
			this.count = count;
		}
		
		public void run() {
			try {
				int length = a.length();
				Random r = new Random();
				int pos = base;
				for (int i = 0; i < count; i++) {
					int got = a.get(pos);
					int exp = value[pos];
					assertTrue("Element mismatch at " + pos + " got=" + got + ", exp=" + exp, exp == got);
					int val = r.nextInt();
					value[pos] = val;
					a.set(pos, val);

					pos += step;
					if (pos >= length) {
						pos -= length;
					}
				}
				for (int i = base; i < length; i += step) {
					assertTrue("Element mismatch during final check at " + i + " got=" + a.get(i) + ", exp=" + value[i], a.get(i) == value[i]);
				}
			} catch (Exception exp) {
				exp.printStackTrace();
				parallelException = exp;
			} catch (Error err) {
				err.printStackTrace();
				parallelError = err;
			}
		}
		
	}
	
	private static void printTest(String name) {
		System.out.println("");
		System.out.println("<<<<  " + name + "  >>>>");
		System.out.println("");
	}
	
}
