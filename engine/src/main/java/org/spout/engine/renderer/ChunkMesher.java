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
package org.spout.engine.renderer;

import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshotGroup;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;

import org.spout.math.vector.Vector3;
import org.spout.renderer.data.VertexAttribute;
import org.spout.renderer.data.VertexAttribute.DataType;
import org.spout.renderer.data.VertexData;

public class ChunkMesher {
	public VertexData mesh(ChunkSnapshotGroup chunkGroup) {
		final int x = chunkGroup.getX() << Chunk.BLOCKS.BITS;
		final int y = chunkGroup.getY() << Chunk.BLOCKS.BITS;
		final int z = chunkGroup.getZ() << Chunk.BLOCKS.BITS;

		final VertexData vertexData = new VertexData();

		final TFloatList positions = new TFloatArrayList();
		final TIntList indices = vertexData.getIndices();

		int index = 0;

		for (int xx = 0; xx < Chunk.BLOCKS.SIZE; xx++) {
			for (int zz = 0; zz < Chunk.BLOCKS.SIZE; zz++) {
				BlockMaterial lastMaterial = null;
				for (int yy = -1; yy < Chunk.BLOCKS.SIZE + 1; yy++) {
					final BlockMaterial currentMaterial = chunkGroup.getBlock(x + xx, y + yy, z + zz);

					if (lastMaterial == null) {
						lastMaterial = currentMaterial;
						continue;
					}

					if (yy != -1) {
						if (faceNeeded(lastMaterial, currentMaterial, BlockFace.TOP)) {
							final Vector3 offset = BlockFace.TOP.getOffset();
							final float fx = xx + (offset.getX() + 1) / 2;
							final float fy = yy + (offset.getY() + 1) / 2;
							final float fz = zz + (offset.getZ() + 1) / 2;
							add(positions, fx, fy, fz);
							add(positions, fx + 1, fy, fz);
							add(positions, fx, fy, fz + 1);
							add(positions, fx + 1, fy, fz + 1);
							add(indices, index + 3, index + 1, index + 2, index + 2, index + 1, index);
							index += 4;
						}
					}

					if (yy != Chunk.BLOCKS.SIZE + 1) {
						if (faceNeeded(currentMaterial, lastMaterial, BlockFace.BOTTOM)) {
							final Vector3 offset = BlockFace.BOTTOM.getOffset();
							final float fx = xx + (offset.getX() + 1) / 2;
							final float fy = yy + (offset.getY() + 1) / 2;
							final float fz = zz + (offset.getZ() + 1) / 2;
							add(positions, fx, fy, fz);
							add(positions, fx + 1, fy, fz);
							add(positions, fx, fy, fz + 1);
							add(positions, fx + 1, fy, fz + 1);
							add(indices, index + 3, index + 2, index + 1, index + 2, index, index + 1);
							index += 4;
						}
					}

					lastMaterial = currentMaterial;
				}
			}
		}

		final VertexAttribute attribute = new VertexAttribute("position", DataType.FLOAT, 3);
		attribute.setData(positions);
		vertexData.addAttribute(0, attribute);

		return vertexData;
	}

	private boolean faceNeeded(BlockMaterial current, BlockMaterial last, BlockFace face) {
		return !current.isInvisible() && current.isFaceRendered(face, last) && !last.getOcclusion(last.getData()).get(face.getOpposite());
	}

	private static void add(TFloatList list, float... vals) {
		list.add(vals);
	}

	private static void add(TIntList list, int... vals) {
		list.add(vals);
	}
}
