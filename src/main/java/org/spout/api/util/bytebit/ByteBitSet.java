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
package org.spout.api.util.bytebit;

import org.spout.api.util.LogicUtil;

public class ByteBitSet {

	private byte flag;

	public ByteBitSet() {
		this.flag = 0;
	}

	public ByteBitSet(int flag) {
		this.flag = (byte) flag;
	}

	public ByteBitSet(ByteBitMask flag) {
		this.flag = flag.getMask();
	}

	public ByteBitSet(ByteBitMask... flags) {
		this.flag = 0;
		for (ByteBitMask flag : flags) {
			this.flag |= flag.getMask();
		}
	}

	/**
	 * Checks if a bit in this flag is set
	 */
	public boolean isDirty() {
		return this.flag != 0;
	}

	/**
	 * Gets the current flag as a byte
	 * @return the current flag
	 */
	public byte get() {
		return this.flag;
	}

	/**
	 * Gets the current state of one or more bits using a mask<br>
	 * If multiple bits are contained in the mask, all bits have to match
	 * 
	 * @param mask to use
	 * @return True if all of the bits in the mask are set, False if not
	 */
	public boolean get(ByteBitMask mask) {
		return LogicUtil.getBit(this.flag, mask.getMask());
	}

	/**
	 * Gets the current state of one or more bits using a mask<br>
	 * If multiple bits are contained in the mask, one of these bits have to match
	 * 
	 * @param mask containing the bit flags
	 * @return True if one of the bits in the mask are set, False if not
	 */
	public boolean getAny(ByteBitMask mask) {
		return (this.flag & mask.getMask()) != 0;
	}

	/**
	 * Sets the current flag
	 * @param flag to set to
	 */
	public void set(ByteBitMask mask) {
		this.flag = mask.getMask();
	}

	/**
	 * Sets the current flag
	 * @param flag to set to
	 */
	public void set(byte flag) {
		this.flag = flag;
	}

	/**
	 * Sets the current state of a bit using a mask
	 * @param mask to use
	 * @param value to set the bit to
	 */
	public void set(ByteBitMask mask, boolean value) {
		this.flag = LogicUtil.setBit(this.flag, mask.getMask(), value);
	}
}
