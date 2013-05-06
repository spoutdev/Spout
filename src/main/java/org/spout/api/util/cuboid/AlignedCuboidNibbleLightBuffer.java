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
package org.spout.api.util.cuboid;

import org.spout.api.lighting.Modifiable;
import org.spout.api.math.GenericMath;


public class AlignedCuboidNibbleLightBuffer extends CuboidNibbleLightBuffer {
	
	private final int mask;
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
		this.mask = GenericMath.roundUpPow2(sizeX) - 1;
		if (this.mask != sizeX - 1) {
			throw new IllegalArgumentException("Aligned buffers must have a power of 2 size, " + sizeX + ", expected " + this.mask);
		}
		if ((baseX & (~mask)) != baseX || (baseY & (~mask)) != baseY || (baseZ & (~mask)) != baseZ) {
			throw new IllegalArgumentException("Aligned buffers must have aligned base coords");
		}
		xShift = GenericMath.multiplyToShift(super.Xinc);
		yShift = GenericMath.multiplyToShift(super.Yinc);
		zShift = GenericMath.multiplyToShift(super.Zinc);
	}
	
	@Override
	public int getIndex(int x, int y, int z) {
		x &= mask;
		y &= mask;
		z &= mask;
		x <<= xShift;
		y <<= yShift;
		z <<= zShift;
		return x | y | z;
	}
	
	@Override
	public AlignedCuboidNibbleLightBuffer copy() {
		return new AlignedCuboidNibbleLightBuffer(this);
	}

}
