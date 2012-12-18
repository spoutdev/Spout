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

import org.spout.api.material.BlockMaterial;
import org.spout.api.math.Vector3;

public class CuboidBlockMaterialBuffer extends CuboidBuffer {
	private final BlockMaterial[] buffer;
	private final short[] data;
	private CuboidBlockMaterialBuffer source;

	public CuboidBlockMaterialBuffer(int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ, BlockMaterial[] buffer, short[] data) {
		super(baseX, baseY, baseZ, sizeX, sizeY, sizeZ);
		this.buffer = buffer;
		this.data = data;
	}

	public CuboidBlockMaterialBuffer(int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ) {
		this(baseX, baseY, baseZ, sizeX, sizeY, sizeZ, new BlockMaterial[sizeX * sizeY * sizeZ], new short[sizeX * sizeY * sizeZ]);
	}

	public CuboidBlockMaterialBuffer(double baseX, double baseY, double baseZ, double sizeX, double sizeY, double sizeZ) {
		this((int) baseX, (int) baseY, (int) baseZ, (int) sizeX, (int) sizeY, (int) sizeZ, new BlockMaterial[(int) (sizeX * sizeY * sizeZ)], new short[(int) (sizeX * sizeY * sizeZ)]);
	}

	public CuboidBlockMaterialBuffer(Vector3 base, Vector3 size) {
		this((int) base.getX(), (int) base.getY(), (int) base.getZ(), (int) size.getX(), (int) size.getY(), (int) size.getZ(), new BlockMaterial[(int) (size.getX() * size.getY() * size.getZ())], new short[(int) (size.getX() * size.getY() * size.getZ())]);
	}

	@Override
	public void copyElement(int thisIndex, int sourceIndex, int runLength) {
		final int end = thisIndex + runLength;
		for (; thisIndex < end; thisIndex++) {
			buffer[thisIndex] = source.buffer[sourceIndex++];
		}
	}

	@Override
	public void setSource(CuboidBuffer source) {
		if (source instanceof CuboidBlockMaterialBuffer) {
			this.source = (CuboidBlockMaterialBuffer) source;
		} else {
			throw new IllegalArgumentException("Only CuboidShortBuffers may be used as the data source when copying to a CuboidShortBuffer");
		}
	}

	public void set(int x, int y, int z, BlockMaterial material, short data) {
		int index = getIndex(x, y, z);
		if (index < 0) {
			throw new IllegalArgumentException("Coordinate (" + x + ", " + y + ", " + z + ") is outside the buffer");
		}

		this.buffer[index] = material;
		this.data[index] = data;
	}

	public BlockMaterial get(int x, int y, int z) {
		int index = getIndex(x, y, z);
		if (index < 0) {
			throw new IllegalArgumentException("Coordinate (" + x + ", " + y + ", " + z + ") is outside the buffer");
		}

		return buffer[index];
	}
	
	public short getData(int x, int y, int z) {
		int index = getIndex(x, y, z);
		if (index < 0) {
			throw new IllegalArgumentException("Coordinate (" + x + ", " + y + ", " + z + ") is outside the buffer");
		}

		return data[index];
	}


	public void flood(BlockMaterial material, short data) {
		for (int i = 0; i < buffer.length; i++) {
			this.buffer[i] = material;
			this.data[i] = data;
		}
	}
}
