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

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.MaterialRegistry;

public class UniformImmutableCuboidBlockMaterialBuffer extends ImmutableCuboidBlockMaterialBuffer {
	private final BlockMaterial uniform;
	private final short id;
	private final short data;

	public UniformImmutableCuboidBlockMaterialBuffer(Chunk c, BlockMaterial uniform) {
		this(c.getBlockX(), c.getBlockY(), c.getBlockZ(), uniform, uniform.getId(), uniform.getData());
	}

	public UniformImmutableCuboidBlockMaterialBuffer(Chunk c, short id, short data) {
		this(c.getBlockX(), c.getBlockY(), c.getBlockZ(), (BlockMaterial) MaterialRegistry.get(id).getSubMaterial(data), id, data);
	}

	public UniformImmutableCuboidBlockMaterialBuffer(int bx, int by, int bz, BlockMaterial uniform) {
		this(bx, by, bz, uniform, uniform.getId(), uniform.getData());
	}

	private UniformImmutableCuboidBlockMaterialBuffer(int bx, int by, int bz, BlockMaterial uniform, short id, short data) {
		super(bx, by, bz, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE, Chunk.BLOCKS.SIZE, null, null);
		this.uniform = uniform;
		this.id = id;
		this.data = data;
	}

	@Override
	public void copyElement(int thisIndex, int sourceIndex, int runLength) {
		throw new UnsupportedOperationException("This buffer is immutable");
	}

	@Override
	public void setSource(CuboidBuffer source) {
	}

	public BlockMaterial get(int x, int y, int z) {
		return uniform;
	}

	public short getId(int x, int y, int z) {
		return id;
	}

	public short getData(int x, int y, int z) {
		return data;
	}

	public short[] getRawId() {
		throw new UnsupportedOperationException("Buffer is a uniform buffer, there is no array");
	}

	public short[] getRawData() {
		throw new UnsupportedOperationException("Buffer is a uniform buffer, there is no array");
	}
}
