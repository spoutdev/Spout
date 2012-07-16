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
package org.spout.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class LogicUtil {

	/**
	 * Removes duplicate values by using the objects' equals function<br>
	 * Note that this function does not use a Set, because no hashcode is used.
	 * 
	 * @param input collection
	 * @return the input collection
	 */
	public static <T extends Collection<?>> T removeDuplicates(T input) {
		List<Object> unique = new ArrayList<Object>();
		for (Iterator<?> iter = input.iterator(); iter.hasNext();) {
			Object next = iter.next();
			if (unique.contains(next)) {
				iter.remove();
			} else {
				unique.add(next);
			}
		}
		return input;
	}

	/**
	 * Checks if the object equals one of the other objects given
	 * @param object to check
	 * @param objects to use equals against
	 * @return True if one of the objects equal the object
	 */
	public static <A, B> boolean equalsAny(A object, B... objects) {
		for (B o : objects) {
			if (bothNullOrEqual(o, object)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if object a and b are both null or are equal
	 * 
	 * @param a
	 * @param b
	 * @return if both null or equal
	 */
	public static boolean bothNullOrEqual(Object a, Object b) {
		return (a == null || b == null) ? (a == b) : a.equals(b);
	}

	/**
	 * Gets if a bit is set in an integer value
	 * @param value to get it of
	 * @param bit to check
	 * @return True if the bit is set
	 */
	public static boolean getBit(int value, int bit) {
		return (value & bit) == bit;
	}

	/**
	 * Sets a single bit in a byte value
	 * @param value to set a bit of
	 * @param bit to set
	 * @param state to enable or disable the bit
	 * @return the resulting value
	 */
	public static byte setBit(byte value, int bit, boolean state) {
		return (byte) setBit((int) value, bit, state);
	}

	/**
	 * Sets a single bit in a short value
	 * @param value to set a bit of
	 * @param bit to set
	 * @param state to enable or disable the bit
	 * @return the resulting value
	 */
	public static short setBit(short value, int bit, boolean state) {
		return (short) setBit((int) value, bit, state);
	}

	/**
	 * Sets a single bit in an integer value
	 * @param value to set a bit of
	 * @param bit to set
	 * @param state to enable or disable the bit
	 * @return the resulting value
	 */
	public static int setBit(int value, int bit, boolean state) {
		return state ? value | bit : value & ~bit;
	}
}
