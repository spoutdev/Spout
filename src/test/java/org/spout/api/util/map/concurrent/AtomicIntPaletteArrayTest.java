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

import org.junit.Before;
import org.junit.Test;

public class AtomicIntPaletteArrayTest {
	
	AtomicIntPaletteArray a = new AtomicIntPaletteArray(256);
	int[] copy = new int[256];

	@Test
	public void repeatTest() {
		
		for (int i = 0; i < a.length(); i++) {
			set(i, 12345);
		}
		
		checkWidth(1);
		
		for (int i = 0; i < a.length(); i+=2) {
			set(i, 54321);
		}
		
		checkWidth(2);
		
		for (int i = 0; i < a.length(); i++) {
			check(i);
		}
	}
	
	@Test
	public void rampTest() {
		
		for (int i = 0; i < a.length(); i++) {
			set(i, i);
		}
		
		checkWidth(8);
		
		for (int i = 0; i < a.length(); i++) {
			check(i);
		}
	}
	
	@Test
	public void randomTest() {
		
		Random r = new Random();
		
		System.out.println("Setting 1024 random values");
		
		for (int i = 0; i < 1024; i++) {
			set(r.nextInt(256), r.nextInt());
		}
		
		for (int i = 0; i < 256; i++) {
			check(i);
		}
		
		printPaletteUse();
		
		checkWidth(16);
		
		System.out.println("Compressing");
		
		a.compress();
		
		printPaletteUse();
		
		System.out.println("Checking array");
		
		for (int i = 0; i < 256; i++) {
			check(i);
		}
		
		System.out.println("Setting 1024 random values");
		
		for (int i = 0; i < 1024; i++) {
			set(r.nextInt(256), r.nextInt());
		}
		
		checkWidth(16);
		
		for (int i = 0; i < 256; i++) {
			check(i);
		}
		
		printPaletteUse();
		
		System.out.println("Setting 128 equal values");
		
		for (int i = 0; i < 128; i++) {
			set(i, 123);
		}
		
		printPaletteUse();

		System.out.println("Compressing");
		
		a.compress();
		
		printPaletteUse();
		
		System.out.println("Checking array");
		
		for (int i = 0; i < 256; i++) {
			check(i);
		}
	}
	
	private void printPaletteUse() {
		System.out.println("Palette usage: " + a.getPaletteUsage() + " / " + a.getPaletteSize());
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
	
}
