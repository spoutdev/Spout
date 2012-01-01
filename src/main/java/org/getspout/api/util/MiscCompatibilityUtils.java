package org.getspout.api.util;

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
	
		int length = to - from;
		
		T[] newArray = clazz.cast(Array.newInstance(clazz, length));
		
		int d = 0;
		
		int originalLength = original.length;
		
		for (int s = from; s < to && s < originalLength; s++) {
			newArray[d++] = original[s];
		}
		
		return newArray;
	}
	
}
