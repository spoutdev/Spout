/*
 * This file is part of Math.
 *
 * Copyright (c) 2011-2013, Spout LLC <http://www.spout.org/>
 * Math is licensed under the Spout License Version 1.
 *
 * Math is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Math is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.math;

/**
 * Stores the size that spans a fixed amount of bits<br>
 * For example: 1, 2, 4, 8, 16 and 32 sizes match this description
 */
public class BitSize {
	public final int SIZE;
	public final int HALF_SIZE;
	public final int DOUBLE_SIZE;
	public final int MASK;
	public final int BITS;
	public final int DOUBLE_BITS;
	public final int AREA;
	public final int HALF_AREA;
	public final int DOUBLE_AREA;
	public final int VOLUME;
	public final int HALF_VOLUME;
	public final int DOUBLE_VOLUME;

	public BitSize(int bitCount) {
		this.BITS = bitCount;
		this.SIZE = 1 << bitCount;
		this.AREA = this.SIZE * this.SIZE;
		this.VOLUME = this.AREA * this.SIZE;
		this.HALF_SIZE = this.SIZE >> 1;
		this.HALF_AREA = this.AREA >> 1;
		this.HALF_VOLUME = this.VOLUME >> 1;
		this.DOUBLE_SIZE = this.SIZE << 1;
		this.DOUBLE_AREA = this.AREA << 1;
		this.DOUBLE_VOLUME = this.VOLUME << 1;
		this.DOUBLE_BITS = this.BITS << 1;
		this.MASK = this.SIZE - 1;
	}
}
