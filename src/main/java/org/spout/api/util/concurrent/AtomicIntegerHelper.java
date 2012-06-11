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
package org.spout.api.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerHelper {
	
	/**
	 * Atomically sets a bit (or bits) for an AtomicInteger.  If the bit(s) are already set, the method returns false<br>
	 * If any of the bits are one, then the method will fail and set none of the bits
	 * @param i the AtomicInteger
	 * @param mask the bits to set should be ones
	 * @return false if the bits were successfully set
	 */
	public static boolean setBit(AtomicInteger i, int mask) {
		boolean success = false;
		while (!success) {
			int current = i.get();
			if ((current & mask) != 0) {
				return false;
			}
			int next = current | mask;
			success = i.compareAndSet(current, next);
		}
		return true;
	}
	
	/**
	 * Atomically clears a bit (or bits) for an AtomicInteger.  If the bit(s) are already cleared, the method returns false<br>
	 * If any of the bits are zero, then the method will fail and clear none of the bits
	 * @param i the AtomicInteger
	 * @param mask the bits to clear should be ones
	 * @return false if the bit were successfully cleared
	 */
	public static boolean clearBit(AtomicInteger i, int mask) {
		boolean success = false;
		while (!success) {
			int current = i.get();
			if ((current & mask) != mask) {
				return false;
			}
			int next = current & (~mask);
			success = i.compareAndSet(current, next);
		}
		return true;
	}
	
	/**
	 * Atomically sets a field of bits for an AtomicInteger.  The mask parameter indicates which
	 * bits are part of the field.  Only these bits in expect are compared with the current value 
	 * and updated if there is a match
	 * @param i the AtomicInteger
	 * @param mask the bits of the field
	 * @param expect the expected value (only the masked bits)
	 * @param update the updated value
	 * @return false if the bit were successfully cleared
	 */
	public static boolean setField(AtomicInteger i, int mask, int expect, int update) {
		boolean success = false;
		while (!success) {
			int current = i.get();
			if ((expect & mask) != (current & mask)) {
				return false;
			}
			int next = (current & (~mask)) | (update & (mask));
			success = i.compareAndSet(current, next);
		}
		return true;
	}

}
