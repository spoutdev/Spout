/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.mesh;

import java.awt.Color;
import java.util.List;

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;
import org.spout.api.model.ModelFace;
import org.spout.api.model.Vertex;
import org.spout.engine.util.ChunkSnapshotModel;
import org.spout.engine.world.SpoutChunk;

import com.google.common.collect.Lists;

/**
 * Represents a mesh for a chunk.
 */
public class ChunkMesh extends BaseMesh {
	/**
	 * Faces that you can render.
	 */
	private static final List<BlockFace> renderableFaces = Lists.newArrayList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.TOP, BlockFace.BOTTOM);

	private final ChunkSnapshotModel chunkModel;
	private ChunkSnapshot center;

	/**
	 * Private constructor.
	 */
	private ChunkMesh(ChunkSnapshotModel chunkModel) {
		this.chunkModel = chunkModel;
		center = chunkModel.getCenter();
	}

	public static ChunkMesh generateFromChunk(SpoutChunk chunk) {
		ChunkSnapshotModel model = new ChunkSnapshotModel(chunk.getWorld());
		model.load(chunk.getX(), chunk.getY(), chunk.getZ());
		ChunkMesh mesh = new ChunkMesh(model);

		for (int x = chunk.getBlockX(); x < chunk.getBlockX() + Chunk.BLOCKS.SIZE; x++) {
			for (int y = chunk.getBlockY(); y < chunk.getBlockY() + Chunk.BLOCKS.SIZE; y++) {
				for (int z = chunk.getBlockZ(); z < chunk.getBlockZ() + Chunk.BLOCKS.SIZE; z++) {
					mesh.generateBlockVertices(x, y, z);
				}
			}
		}

		return mesh;
	}

	/**
	 * Generates the vertices of the given block and adds them to the ChunkMesh.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	private void generateBlockVertices(int x, int y, int z) {
		BlockMaterial material = center.getBlockMaterial(x, y, z);

		if (material.isTransparent()) {
			return;
		}

		boolean[] shouldRender = new boolean[6];
		Vector3 position = new Vector3(x, y, z);
		for (BlockFace face : renderableFaces) {
			Vector3 facePos = position.add(face.getOffset());
			int x1 = facePos.getFloorX();
			int y1 = facePos.getFloorY();
			int z1 = facePos.getFloorZ();
			BlockMaterial neighbor = chunkModel.getChunkFromBlock(x1, y1, z1).getBlockMaterial(x1, y1, z1);

			if (neighbor.isTransparent()) {
				shouldRender[face.ordinal()] = true;
			} else {
				shouldRender[face.ordinal()] = false;
			}
		}

		for (BlockFace face : renderableFaces) {
			if (shouldRender[face.ordinal()]) {
				// Create a face -- temporary until we get some real models
				appendModelFaces(material, face, position);
			}
		}
	}

	/**
	 * Appends ModelFaces from the block face. This will likely be temporary.
	 * 
	 * @param face
	 * @param base
	 */
	private void appendModelFaces(BlockMaterial m, BlockFace face, Vector3 base) {
		Vector3 a = null;
		Vector3 b = null;
		Vector3 c = null;
		Vector3 d = null;

		switch (face) {
		case TOP:
			a = base.add(0, 1, 0);
			b = base.add(0, 1, 1);
			c = base.add(1, 1, 0);
			d = base.add(1, 1, 1);
			break;
		case BOTTOM:
			a = base.add(0, 0, 0);
			b = base.add(0, 0, 1);
			c = base.add(1, 0, 0);
			d = base.add(1, 0, 1);
			break;
		case NORTH:
			a = base.add(0, 0, 0);
			b = base.add(0, 0, 1);
			c = base.add(0, 1, 0);
			d = base.add(0, 1, 1);
			break;
		case SOUTH:
			a = base.add(1, 0, 0);
			b = base.add(1, 0, 1);
			c = base.add(1, 1, 0);
			d = base.add(1, 1, 1);
			break;
		case WEST:
			a = base.add(0, 0, 1);
			b = base.add(1, 0, 1);
			c = base.add(0, 1, 1);
			d = base.add(1, 1, 1);
			break;
		case EAST:
			a = base.add(0, 0, 0);
			b = base.add(1, 0, 0);
			c = base.add(0, 1, 0);
			d = base.add(1, 1, 0);
			break;
		}

		Color color = getColor(m); // Temporary testing color
		Vertex v1 = new Vertex(a, face.getOffset());
		v1.color = color;

		Vertex v2 = new Vertex(b, face.getOffset());
		v2.color = color;

		Vertex v3 = new Vertex(c, face.getOffset());
		v3.color = color;

		Vertex v4 = new Vertex(d, face.getOffset());
		v4.color = color;

		ModelFace f1 = new ModelFace(v1, v2, v3);
		ModelFace f2 = new ModelFace(v2, v3, v4);
		faces.add(f1);
		faces.add(f2);

		// System.out.println("appending face " + face + " for block at " + base
		// + " with color " + color);
	}

	private Color getColor(BlockMaterial m) {
		if (!m.isSolid()) {
			return new Color(0, 0, 0);
		}
		switch (m.getId()) {
		case 78:
			return new Color(255, 255, 255);
		case 24:
		case 12:
			return new Color(210, 210, 150);
		case 10:
			return new Color(200, 50, 50);
		case 9:
		case 8:
			return new Color(150, 150, 200);
		case 7:
			return new Color(50, 50, 50);
		case 4:
			return new Color(100, 100, 100);
		case 17:
		case 3:
			return new Color(110, 75, 35);
		case 18:
		case 2:
			return new Color(55, 140, 55);
		case 21:
		case 16:
		case 15:
		case 14:
		case 13:
		case 1:
		default:
			return new Color(150, 150, 150);
		}
	}

	@Override
	public String toString() {
		return "ChunkMesh [center=" + center + "]";
	}
}
