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
package org.spout.api.lighting;

import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.util.cuboid.ChunkCuboidLightBufferWrapper;
import org.spout.api.util.cuboid.CuboidLightBuffer;
import org.spout.api.util.cuboid.ImmutableCuboidBlockMaterialBuffer;
import org.spout.api.util.cuboid.ImmutableHeightMapBuffer;

@SuppressWarnings ("rawtypes")
public class FakeLightingManager extends LightingManager {
	private static final AtomicInteger count = new AtomicInteger();
	private final int id;

	public FakeLightingManager(int id) {
		super("FAKE_" + id + "_" + count.getAndIncrement());
		this.id = id;
	}

	@Override
	protected void resolve(ChunkCuboidLightBufferWrapper light, ImmutableCuboidBlockMaterialBuffer material, ImmutableHeightMapBuffer height, int[] x, int[] y, int[] z, int changedBlocks) {
		throw new UnsupportedOperationException("Attempt to use resolve() method on a FakeLightingManager");
	}

	@Override
	protected void initChunks(ChunkCuboidLightBufferWrapper light, ImmutableCuboidBlockMaterialBuffer material, ImmutableHeightMapBuffer height, Chunk[] chunks) {
		throw new UnsupportedOperationException("Attempt to use resolve() method on a FakeLightingManager");
	}

	@Override
	protected void resolveChunks(ChunkCuboidLightBufferWrapper light, ImmutableCuboidBlockMaterialBuffer material, ImmutableHeightMapBuffer height, int[] bx, int[] by, int[] bz, int[] tx, int[] ty, int[] tz, int changedCuboids) {
		throw new UnsupportedOperationException("Attempt to use resolve() method on a FakeLightingManager");
	}

	@Override
	protected void resolveColumns(ChunkCuboidLightBufferWrapper light, ImmutableCuboidBlockMaterialBuffer material, ImmutableHeightMapBuffer height, int[] hx, int[] hz, int[] oldHy, int[] newHy, int changedColumns) {
		throw new UnsupportedOperationException("Attempt to use resolve() method on a FakeLightingManager");
	}

	@Override
	public CuboidLightBuffer deserialize(Modifiable holder, int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ, byte[] data) {
		return new ByteArrayCuboidLightBuffer(id, baseX, baseY, baseZ, sizeX, sizeY, sizeZ, data);
	}

	@Override
	public short getId() {
		return (short) id;
	}

	@Override
	public CuboidLightBuffer[][][] bulkInitialize(ImmutableCuboidBlockMaterialBuffer buffer, int[][] height) {
		throw new UnsupportedOperationException("Attempt to use resolve() method on a FakeLightingManager");
	}
}
