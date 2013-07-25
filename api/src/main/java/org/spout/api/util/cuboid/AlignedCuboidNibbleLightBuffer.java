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
package org.spout.api.util.cuboid;

import org.spout.api.lighting.Modifiable;
import org.spout.api.math.GenericMath;

public class AlignedCuboidNibbleLightBuffer extends CuboidNibbleLightBuffer {
	private final int xMask;
	private final int yMask;
	private final int zMask;
	private final int xShift;
	private final int yShift;
	private final int zShift;

	protected AlignedCuboidNibbleLightBuffer(AlignedCuboidNibbleLightBuffer buffer) {
		this(buffer.holder, buffer.getManagerId(), buffer.baseX, buffer.baseY, buffer.baseZ, buffer.sizeX, buffer.sizeY, buffer.sizeZ, buffer.lightData);
	}

	protected AlignedCuboidNibbleLightBuffer(Modifiable holder, int id, int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ) {
		this(holder, id, baseX, baseY, baseZ, sizeX, sizeY, sizeZ, null);
	}

	protected AlignedCuboidNibbleLightBuffer(Modifiable holder, int id, int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ, byte[] data) {
		super(holder, id, baseX, baseY, baseZ, sizeX, sizeY, sizeZ, data);
		this.xMask = GenericMath.roundUpPow2(sizeX) - 1;
		this.yMask = GenericMath.roundUpPow2(sizeY) - 1;
		this.zMask = GenericMath.roundUpPow2(sizeZ) - 1;
		if (this.xMask != sizeX - 1) {
			throw new IllegalArgumentException("Aligned buffers must have a power of 2 size, got a size x of " + sizeX + ", expected " + this.xMask);
		}
		if (this.yMask != sizeY - 1) {
			throw new IllegalArgumentException("Aligned buffers must have a power of 2 size, got a size y of " + sizeY + ", expected " + this.yMask);
		}
		if (this.zMask != sizeZ - 1) {
			throw new IllegalArgumentException("Aligned buffers must have a power of 2 size, got a size z of " + sizeZ + ", expected " + this.zMask);
		}
		if ((baseX & (~xMask)) != baseX || (baseY & (~yMask)) != baseY || (baseZ & (~zMask)) != baseZ) {
			throw new IllegalArgumentException("Aligned buffers must have aligned base coords");
		}
		xShift = GenericMath.multiplyToShift(super.Xinc);
		yShift = GenericMath.multiplyToShift(super.Yinc);
		zShift = GenericMath.multiplyToShift(super.Zinc);
	}

	@Override
	public int getIndex(int x, int y, int z) {
		x &= xMask;
		y &= yMask;
		z &= zMask;
		x <<= xShift;
		y <<= yShift;
		z <<= zShift;
		return x | y | z;
	}

	@Override
	public AlignedCuboidNibbleLightBuffer copy() {
		return new AlignedCuboidNibbleLightBuffer(this);
	}

	/**
	 * Copies data from an array into a Z row.
	 *
	 * @param start the first element to copy
	 * @param end the last element to copy (exclusive)
	 */
	public void copyZRow(int x, int y, int z, int start, int end, int[] values) {
		int index = getIndex(x, y, z);

		int inc = (Zinc >> 1);

		if (isEven(index)) {
			index >>= 1;

			while (start < end) {
				lightData[index] = (byte) ((lightData[index] & 0xF0) | (values[start] & 0xF));
				start++;
				index += inc;
			}
		} else {
			index >>= 1;

			while (start < end) {
				lightData[index] = (byte) ((lightData[index] & 0x0F) | (values[start] << 4));
				start++;
				index += inc;
			}
		}
	}
}
