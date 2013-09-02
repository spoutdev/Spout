/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
// Copyright (c) 1999 CERN - European Organization for Nuclear Research.

// Permission to use, copy, modify, distribute and sell this software and
// its documentation for any purpose is hereby granted without fee,
// provided that the above copyright notice appear in all copies and that
// both that copyright notice and this permission notice appear in
// supporting documentation. CERN makes no representations about the
// suitability of this software for any purpose. It is provided "as is"
// without expressed or implied warranty.

package org.spout.math;

/**
 * Provides various hash functions.
 *
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public final class HashFunctions {
	/**
	 * Returns a hashcode for the specified value.
	 *
	 * @return  a hash code value for the specified value.
	 */
	public static int hash(double value) {
		assert !Double.isNaN(value) : "Values of NaN are not supported.";

		long bits = Double.doubleToLongBits(value);
		return (int)(bits ^ (bits >>> 32));
		//return (int) Double.doubleToLongBits(value*663608941.737);
		//this avoids excessive hashCollisions in the case values are
		//of the form (1.0, 2.0, 3.0, ...)
	}

	/**
	 * Returns a hashcode for the specified value.
	 *
	 * @return  a hash code value for the specified value.
	 */
	public static int hash(float value) {
		assert !Float.isNaN(value) : "Values of NaN are not supported.";

		return Float.floatToIntBits(value*663608941.737f);
		// this avoids excessive hashCollisions in the case values are
		// of the form (1.0, 2.0, 3.0, ...)
	}

	/**
	 * Returns a hashcode for the specified value.
	 *
	 * @return  a hash code value for the specified value.
	 */
	public static int hash(int value) {
		return value;
	}

	/**
	 * Returns a hashcode for the specified value.
	 *
	 * @return  a hash code value for the specified value.
	 */
	public static int hash(long value) {
		return ((int)(value ^ (value >>> 32)));
	}

	/**
	 * Returns a hashcode for the specified object.
	 *
	 * @return  a hash code value for the specified object.
	 */
	public static int hash(Object object) {
		return object==null ? 0 : object.hashCode();
	}


	/**
	 * In profiling, it has been found to be faster to have our own local implementation
	 * of "ceil" rather than to call to {@link Math#ceil(double)}.
	 */
	public static int fastCeil( float v ) {
		int possible_result = ( int ) v;
		if ( v - possible_result > 0 ) {
			possible_result++;
		}
		return possible_result;
	}
}
