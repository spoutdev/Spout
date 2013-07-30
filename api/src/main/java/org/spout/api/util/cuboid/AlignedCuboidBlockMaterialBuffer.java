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

import org.spout.math.GenericMath;
import org.spout.math.vector.Vector3;

public class AlignedCuboidBlockMaterialBuffer extends CuboidBlockMaterialBuffer {
	private final int xMask;
	private final int yMask;
	private final int zMask;
	private final int xShift;
	private final int yShift;
	private final int zShift;

	public AlignedCuboidBlockMaterialBuffer(CuboidBlockMaterialBuffer buffer) {
		this(buffer.baseX, buffer.baseY, buffer.baseZ, buffer.sizeX, buffer.sizeY, buffer.sizeZ, new short[buffer.id.length], new short[buffer.data.length]);
		System.arraycopy(buffer.id, 0, id, 0, id.length);
		System.arraycopy(buffer.data, 0, data, 0, data.length);
	}

	public AlignedCuboidBlockMaterialBuffer(int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ) {
		this(baseX, baseY, baseZ, sizeX, sizeY, sizeZ, new short[sizeX * sizeY * sizeZ], new short[sizeX * sizeY * sizeZ]);
	}

	public AlignedCuboidBlockMaterialBuffer(double baseX, double baseY, double baseZ, double sizeX, double sizeY, double sizeZ) {
		this((int) baseX, (int) baseY, (int) baseZ, (int) sizeX, (int) sizeY, (int) sizeZ);
	}

	public AlignedCuboidBlockMaterialBuffer(Vector3 base, Vector3 size) {
		this(base.getX(), base.getY(), base.getZ(), size.getX(), size.getY(), size.getZ());
	}

	public AlignedCuboidBlockMaterialBuffer(int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ, short[] id, short[] data) {
		super(baseX, baseY, baseZ, sizeX, sizeY, sizeZ, id, data, false);
		if (sizeX != sizeY || sizeX != sizeZ) {
			throw new IllegalArgumentException("Aligned buffers must be cubic");
		}
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

	/**
	 * Gets the index for the given coords.  Unlike a normal cuboid buffer, no bounds checking is performed
	 */
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
}
