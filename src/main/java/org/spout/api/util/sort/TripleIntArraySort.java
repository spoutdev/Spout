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


public class TripleIntArraySort {
	
	public static void tripleIntArraySort(int[] first, int[] second, int[] third) {
		
		if (first.length != second.length || first.length != third.length) {
			throw new IllegalArgumentException("All three arrays must have the same number of elements");
		}
		
		tripleIntArraySort(first, second, third, first.length);
	}

	/**
	 * Merge sorts the triple array using dest for temporary storage
	 * 
	 * @param first the primary array
	 * @param second the secondary array
	 * @param third the tertiary array 
	 * @param length the length of the arrays to sort
	 * @return the depth of the sort
	 */
	public static int tripleIntArraySort(int[] first, int[] second, int[] third, int length) {
	
		int[] first2 = new int[length];
		int[] second2 = new int[length];
		int[] third2 = new int[length];
		
		TripleArray source = new TripleArray(first, second, third);
		TripleArray dest = new TripleArray(first2, second2, third2);
		
		return tripleIntArraySort(source, dest, 0, length);
	}
	
	/**
	 * Merge sorts the triple array using dest for temporary storage
	 * 
	 * @param source the array to be sorted
	 * @param dest the temporary array
	 * @param start the first index inclusive to sort
	 * @param end the last index exclusive to sort
	 */
	private static int tripleIntArraySort(TripleArray source, TripleArray dest, int start, int end) {
		
		if (start == end - 1) {
			return 0;
		} else if (end <= start) {
			throw new IllegalStateException("End pointer cannot be less than or equal to start");
		}
		
		int sorted = -1;
		for (int i = start + 1; i < end; i++) {
			if (source.compare(i - 1, i) > 0) {
				sorted = i;
			}
		}
		
		if (sorted == -1) {
			return 0;
		}
		
		int quarter = (start + (start << 1) + end) >> 2;
		int mid = (sorted > quarter) ? sorted : ((start + end) >> 1);
		
		int level;
		
		level = tripleIntArraySort(source, dest, start, mid) + 1;
		level = Math.max(tripleIntArraySort(source, dest, mid, end) + 1, level);

		int d = start;
		int s1 = start;
		int s2 = mid;
		
		while (s1 < mid && s2 < end) {
			if (source.compare(s1, s2) > 0) {
				dest.copy(source, s2, d);
				s2++;
			} else {
				dest.copy(source, s1, d);
				s1++;
			}
			d++;
		}
		
		while (s1 < mid) {
			dest.copy(source, s1, d);
			s1++;
			d++;
		}
		
		while (s2 < end) {
			dest.copy(source, s2, d);
			s2++;
			d++;
		}
		
		source.copyRange(dest, start, end);
		
		return level;
	}
	
	private static class TripleArray {
		
		private final int[] first;
		private final int[] second;
		private final int[] third;
		
		public TripleArray(int[] first, int[] second, int[] third) {
			this.first = first;
			this.second = second;
			this.third = third;
		}
		
		public int compare(int i, int j) {
			long c = compareRaw(i, j);
			return c > 0 ? 1 : c < 0 ? -1 : 0;
		}

		public long compareRaw(int i, int j) {
			if (first[i] != first[j]) {
				return ((long) first[i]) - first[j];
			} else if (second[i] != second[j]) {
				return ((long) second[i]) - second[j];
			} else {
				return ((long) third[i]) - third[j];
			}
		}
		
		public void copy(TripleArray source, int sourceIndex, int destIndex) {
			first[destIndex] = source.first[sourceIndex];
			second[destIndex] = source.second[sourceIndex];
			third[destIndex] = source.third[sourceIndex];
		}
		
		public void copyRange(TripleArray source, int start, int end) {
			System.arraycopy(source.first, start, first, start, end - start);
			System.arraycopy(source.second, start, second, start, end - start);
			System.arraycopy(source.third, start, third, start, end - start);
		}

	}

}
