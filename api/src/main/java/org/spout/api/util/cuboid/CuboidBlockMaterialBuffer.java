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

import java.util.Arrays;

import org.spout.api.material.BlockMaterial;
import org.spout.api.math.Vector3;

public class CuboidBlockMaterialBuffer extends ImmutableCuboidBlockMaterialBuffer {
	private CuboidBlockMaterialBuffer source;
	private final ImmutableCuboidBlockMaterialBuffer backBuffer;

	public CuboidBlockMaterialBuffer(CuboidBlockMaterialBuffer buffer) {
		this(buffer, false);
	}

	public CuboidBlockMaterialBuffer(CuboidBlockMaterialBuffer buffer, boolean backBuffer) {
		super(buffer);
		if (backBuffer) {
			this.backBuffer = new ImmutableCuboidBlockMaterialBuffer(this);
		} else {
			this.backBuffer = null;
		}
	}

	public CuboidBlockMaterialBuffer(int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ, short[] id, short[] data) {
		this(baseX, baseY, baseZ, sizeX, sizeY, sizeZ, id, data, false);
	}

	public CuboidBlockMaterialBuffer(int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ, short[] id, short[] data, boolean backBuffer) {
		super(baseX, baseY, baseZ, sizeX, sizeY, sizeZ, id, data);
		if (backBuffer) {
			this.backBuffer = new ImmutableCuboidBlockMaterialBuffer(this);
		} else {
			this.backBuffer = null;
		}
	}

	public CuboidBlockMaterialBuffer(int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ) {
		this(baseX, baseY, baseZ, sizeX, sizeY, sizeZ, false);
	}

	public CuboidBlockMaterialBuffer(int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ, boolean backBuffer) {
		super(baseX, baseY, baseZ, sizeX, sizeY, sizeZ);
		if (backBuffer) {
			this.backBuffer = new ImmutableCuboidBlockMaterialBuffer(this);
		} else {
			this.backBuffer = null;
		}
	}

	public CuboidBlockMaterialBuffer(double baseX, double baseY, double baseZ, double sizeX, double sizeY, double sizeZ) {
		this(baseX, baseY, baseZ, sizeX, sizeY, sizeZ, false);
	}

	public CuboidBlockMaterialBuffer(double baseX, double baseY, double baseZ, double sizeX, double sizeY, double sizeZ, boolean backBuffer) {
		super(baseX, baseY, baseZ, sizeX, sizeY, sizeZ);
		if (backBuffer) {
			this.backBuffer = new ImmutableCuboidBlockMaterialBuffer(this);
		} else {
			this.backBuffer = null;
		}
	}

	public CuboidBlockMaterialBuffer(Vector3 base, Vector3 size) {
		this(base, size, false);
	}

	public CuboidBlockMaterialBuffer(Vector3 base, Vector3 size, boolean backBuffer) {
		super(base, size);
		if (backBuffer) {
			this.backBuffer = new ImmutableCuboidBlockMaterialBuffer(this);
		} else {
			this.backBuffer = null;
		}
	}

	@Override
	public void copyElement(int thisIndex, int sourceIndex, int runLength) {
		final int end = thisIndex + runLength;
		for (; thisIndex < end; thisIndex++) {
			id[thisIndex] = source.id[sourceIndex];
			data[thisIndex] = source.data[sourceIndex++];
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

	/**
	 * Sets a horizontal layer of blocks to a given material
	 *
	 * @param y - coordinate of the start of the layer
	 * @param height of the layer
	 * @param material to set to
	 */
	public void setHorizontalLayer(int y, int height, BlockMaterial material) {
		setHorizontalLayer(y, height, material.getId(), material.getData());
	}

	/**
	 * Sets a horizontal layer of blocks to a given material id and data
	 *
	 * @param y - coordinate of the start of the layer
	 * @param height of the layer
	 * @param id of the material to set to
	 * @param data to set to
	 */
	public void setHorizontalLayer(int y, int height, short id, short data) {
		final int startIndex = getIndex(this.baseX, y, this.baseZ);
		final int endIndex = getIndex(this.topX - 1, y + height - 1, this.topZ - 1) + 1;
		if (startIndex < 0 || endIndex <= 0) {
			throw new IllegalArgumentException("Layer Y-Coordinate (y=" + y + ", height=" + height + ") are outside the buffer");
		}
		Arrays.fill(this.id, startIndex, endIndex, id);
		Arrays.fill(this.data, startIndex, endIndex, data);
	}

	/**
	 * Sets a single block material
	 *
	 * @param x - coordinate of the block
	 * @param y - coordinate of the block
	 * @param z - coordinate of the block
	 * @param material to set to
	 */
	public void set(int x, int y, int z, BlockMaterial material) {
		int index = getIndex(x, y, z);
		if (index < 0) {
			throw new IllegalArgumentException("Coordinate (" + x + ", " + y + ", " + z + ") is outside the buffer");
		}

		this.id[index] = material.getId();
		this.data[index] = material.getData();
	}

	/**
	 * Sets a single block material id and data
	 *
	 * @param x - coordinate of the block
	 * @param y - coordinate of the block
	 * @param z - coordinate of the block
	 * @param id of the material to set to
	 * @param data to set to
	 */
	public void set(int x, int y, int z, short id, short data) {
		int index = getIndex(x, y, z);
		if (index < 0) {
			throw new IllegalArgumentException("Coordinate (" + x + ", " + y + ", " + z + ") is outside the buffer");
		}

		this.id[index] = id;
		this.data[index] = data;
	}

	public void flood(BlockMaterial material) {
		for (int i = 0; i < id.length; i++) {
			this.id[i] = material.getId();
			this.data[i] = material.getData();
		}
	}

	public short[] getRawId() {
		return id;
	}

	public short[] getRawData() {
		return data;
	}

	public ImmutableCuboidBlockMaterialBuffer getBackBuffer() {
		return backBuffer == null ? this : backBuffer;
	}
}
