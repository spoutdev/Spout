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

import java.util.concurrent.atomic.AtomicInteger;

import org.spout.math.vector.Vector3f;

public class ImmutableHeightMapBuffer extends CuboidBuffer {
	protected final int[] heightMap;

	public ImmutableHeightMapBuffer(ImmutableHeightMapBuffer buffer) {
		super(buffer.getBase().getFloorX(), 0, buffer.getBase().getFloorZ(), buffer.getSize().getFloorX(), 1, buffer.getSize().getFloorZ());
		this.heightMap = new int[buffer.heightMap.length];
		System.arraycopy(buffer.heightMap, 0, this.heightMap, 0, buffer.heightMap.length);
	}

	public ImmutableHeightMapBuffer(int baseX, int baseZ, int sizeX, int sizeZ, int[] heightMap) {
		super(baseX, 0, baseZ, sizeX, 1, sizeZ);
		this.heightMap = heightMap;
	}

	public ImmutableHeightMapBuffer(int baseX, int baseZ, int sizeX, int sizeZ, AtomicInteger[][] heightMap) {
		super(baseX, 0, baseZ, sizeX, 1, sizeZ);
		this.heightMap = new int[sizeX * sizeZ];
		int i = 0;
		for (int z = 0; z < sizeZ; z++) {
			for (int x = 0; x < sizeX; x++) {
				this.heightMap[i++] = heightMap[x][z].get();
			}
		}
	}

	public ImmutableHeightMapBuffer(int baseX, int baseZ, int sizeX, int sizeZ) {
		this(baseX, baseZ, sizeX, sizeZ, new int[sizeX * sizeZ]);
	}

	public ImmutableHeightMapBuffer(double baseX, double baseZ, double sizeX, double sizeZ) {
		this((int) baseX, (int) baseZ, (int) sizeX, (int) sizeZ, new int[(int) (sizeX * sizeZ)]);
	}

	public ImmutableHeightMapBuffer(Vector3f base, Vector3f size) {
		this((int) base.getX(), (int) base.getZ(), (int) size.getX(), (int) size.getZ(), new int[(int) (size.getX() * size.getZ())]);
	}

	@Override
	public void copyElement(int thisIndex, int sourceIndex, int runLength) {
		throw new UnsupportedOperationException("This buffer is immutable");
	}

	@Override
	public void setSource(CuboidBuffer source) {
	}

	public int get(int x, int z) {
		int index = getIndex(x, 0, z);
		if (index < 0) {
			throw new IllegalArgumentException("Coordinate (" + x + ", " + z + ") is outside the buffer");
		}

		return heightMap[index];
	}
}
