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
package org.spout.api.util.sort;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class TripleIntArraySortTest {
	
	@Test
	public void testRandom() {
		
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			test(r.nextInt(100) + 10);
		}
		
	}
	
	@Test
	public void testSorted() {
		
		Random r = new Random();
		
		int[] first = new int[50 + r.nextInt(20)];
		int[] second = new int[50 + r.nextInt(20)];
		int[] third = new int[50 + r.nextInt(20)];
		
		addRun(first, second, third, 0, 50);
		
		int levels = TripleIntArraySort.tripleIntArraySort(first, second, third, 50);

		assertEquals("Sort has unexpected depth", levels, 0);
	}
	
	@Test
	public void testSortedRuns() {
		
		Random r = new Random();
		
		int[] first = new int[50 + r.nextInt(20)];
		int[] second = new int[50 + r.nextInt(20)];
		int[] third = new int[50 + r.nextInt(20)];
		
		testRun(first, second, third, 1, 0, 20, 50);
		
		testRun(first, second, third, 1, 0, 30, 50);
		
		testRun(first, second, third, 2, 0, 10, 50);
		
		testRun(first, second, third, 1, 0, 40, 50);
		
		testRun(first, second, third, 3, 0, 10, 20, 35, 50);
		
	}
	
	private void test(int length) {
		
		Random r = new Random();
		
		int[] first = new int[length + r.nextInt(20)];
		int[] second = new int[length + r.nextInt(20)];
		int[] third = new int[length + r.nextInt(20)];
		
		for (int i = 0; i < length; i++) {
			if (r.nextBoolean() || i == 0) {
				first[i] = r.nextInt();
				second[i] = r.nextInt();
				third[i] = r.nextInt();
			} else if (r.nextBoolean()) {
				first[i] = first[i - 1];
				second[i] = r.nextInt();
				third[i] = r.nextInt();
			} else {
				first[i] = first[i - 1];
				second[i] = second[i - 1];
				third[i] = r.nextInt();
			}
		}
		
		TripleIntArraySort.tripleIntArraySort(first, second, third, length);

		testSorted(first, second, third, length);
		
	}
	
	@SuppressWarnings("unused")
	private void printList(int[] first, int[] second, int[] third, int length) {
		for (int i = 0; i < length; i++) {
			System.out.println(getString(first, second, third, i));
		}
	}
	
	private void testSorted(int[] first, int[] second, int[] third, int length) {
		
		for (int i = 1; i < length; i++) {
			if (first[i] != first[i - 1]) {
				assertTrue("Primary key ordering invalid " + getString(first, second, third, i, i - 1), first[i] >= first[i-1]);
			} else if (second[i] != second[i - 1]) {
				assertTrue("Secondary key ordering invalid " + getString(first, second, third, i, i - 1), second[i] >= second[i-1]);
			} else if (second[i] != second[i - 1]) {
				assertTrue("Tertiary key ordering invalid " + getString(first, second, third, i, i - 1), third[i] >= third[i-1]);
			}
		}
		
	}
	
	private void addRun(int[] first, int[] second, int[] third, int start, int end) {
		Random r = new Random();
		
		int f = 0;
		int s = 0;
		int t = 0;
		for (int i = start; i < end; i++) {
			if (r.nextBoolean()) {
				t++;
			} else if (r.nextBoolean()) {
				s++;
				t = 0;
			} else if (r.nextBoolean()) {
				f++;
				s = 0;
				t = 0;
			}
			
			first[i] = f;
			second[i] = s;
			third[i] = t;
		}
	}

	private void testRun(int[] first, int[] second, int[] third, int depth, int ... points) {

		for (int i = 0; i < points.length - 1; i++) {	
			addRun(first, second, third, points[i], points[i + 1]);
		}

		int levels = TripleIntArraySort.tripleIntArraySort(first, second, third, 50);

		assertEquals("Sort has unexpected depth", depth, levels);

		testSorted(first, second, third, 50);
	}
	
	private String getString(int[] first, int[] second, int[] third, int i) {
		return "{" + i + ": " + first[i] + ", " + second[i] + ", " + third[i] + "}";
	}
	
	private String getString(int[] first, int[] second, int[] third, int i, int j) {
		return "{" + getString(first, second, third, i) + ", " + getString(first, second, third, j) + "}";
	}

}
