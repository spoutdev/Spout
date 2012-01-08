/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.util;

import java.lang.reflect.Array;

/**
 * Class containing various static methods to emulate > 1.5 functionality
 */
public class MiscCompatibilityUtils {

	/**
	 * Equivalent to the Arrays.copyOfRange() method.
	 * 
	 * The elements at index from to the element at index (to - 1) are copied to a new array.
	 * 
	 * If the to index is out of range, zero or equivalent values are used.
	 * 
	 * @param <T> The type of the source and destination arrays
	 * @param original The source array
	 * @param from The initial index
	 * @param to the final index
	 * @return the new array
	 */
	public static <T> T[] arrayCopyOfRange(T[] original, int from, int to) {
		if (original == null) {
			throw new NullPointerException("Original array is null");
		} else if (from < 0) {
			throw new ArrayIndexOutOfBoundsException("From less than zero (" + from + ")");
		} else if (from > original.length) {
			throw new ArrayIndexOutOfBoundsException("From (" + from + ") greater than the length of the original string + (" + original.length + ")");
		} else if (from > to) {
			throw new IllegalArgumentException("From (" + from + ") exceeds to (" + to + ")");
		}
		
		@SuppressWarnings("unchecked")
		Class<T[]> clazz = (Class<T[]>)original.getClass();
		@SuppressWarnings("unchecked")
		Class<T> clazzComponent = (Class<T>)clazz.getComponentType();
	
		int length = to - from;
		
		T[] newArray = clazz.cast(Array.newInstance(clazzComponent, length));
		
		int d = 0;
		
		int originalLength = original.length;
		
		for (int s = from; s < to && s < originalLength; s++) {
			newArray[d++] = original[s];
		}
		
		return newArray;
	}
	
}
