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

import org.spout.api.material.Material;
import org.spout.math.vector.Vector3;

public class CuboidShortBuffer extends CuboidBuffer {
	private final short[] buffer;
	private CuboidShortBuffer source;

	public CuboidShortBuffer(int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ, short[] buffer) {
		super(baseX, baseY, baseZ, sizeX, sizeY, sizeZ);
		this.buffer = buffer;
	}

	public CuboidShortBuffer(int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ) {
		this(baseX, baseY, baseZ, sizeX, sizeY, sizeZ, new short[sizeX * sizeY * sizeZ]);
	}

	public CuboidShortBuffer(double baseX, double baseY, double baseZ, double sizeX, double sizeY, double sizeZ) {
		this((int) baseX, (int) baseY, (int) baseZ, (int) sizeX, (int) sizeY, (int) sizeZ, new short[(int) (sizeX * sizeY * sizeZ)]);
	}

	public CuboidShortBuffer(Vector3 base, Vector3 size) {
		this((int) base.getX(), (int) base.getY(), (int) base.getZ(), (int) size.getX(), (int) size.getY(), (int) size.getZ(), new short[(int) (size.getX() * size.getY() * size.getZ())]);
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
		if (source instanceof CuboidShortBuffer) {
			this.source = (CuboidShortBuffer) source;
		} else {
			throw new IllegalArgumentException("Only CuboidShortBuffers may be used as the data source when copying to a CuboidShortBuffer");
		}
	}

	//This should be depricated in favor of the Material set.  
	public void set(int x, int y, int z, short data) {
		int index = getIndex(x, y, z);
		if (index < 0) {
			throw new IllegalArgumentException("Coordinate (" + x + ", " + y + ", " + z + ") is outside the buffer");
		}

		buffer[index] = data;
	}

	public void set(int x, int y, int z, Material material) {
		set(x, y, z, material.getId());
	}

	public short get(int x, int y, int z) {
		int index = getIndex(x, y, z);
		if (index < 0) {
			throw new IllegalArgumentException("Coordinate (" + x + ", " + y + ", " + z + ") is outside the buffer");
		}

		return buffer[index];
	}

	public short[] getRawArray() {
		return buffer;
	}

	public void flood(short id) {
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = id;
		}
	}
}
