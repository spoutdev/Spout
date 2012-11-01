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
package org.spout.engine.batcher;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Cuboid;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Matrix;
import org.spout.api.math.Vector3;
import org.spout.api.render.RenderMaterial;
import org.spout.engine.mesh.ComposedMesh;
import org.spout.engine.renderer.BatchVertexRenderer;

/**
 * Represents a group of chunk meshes to be rendered.
 */
public class ChunkMeshBatch extends Cuboid {
	
	private final static Vector3 SIZE = Vector3.ONE;
	
	private PrimitiveBatch renderer = new PrimitiveBatch();
	private ComposedMesh mesh = null;
	private boolean hasVertices = false;
	private Matrix modelMat = MathHelper.createIdentity();
	private final BlockFace face;
	private final RenderMaterial material;
	private long time;
	
	public ChunkMeshBatch(World world, int baseX, int baseY, int baseZ, BlockFace face, RenderMaterial material) {
		super(new Point(world, baseX, baseY, baseZ), SIZE);
		this.face = face;
		this.material = material;
		modelMat = MathHelper.translate(new Vector3(baseX * Chunk.BLOCKS.SIZE, baseY * Chunk.BLOCKS.SIZE, baseZ * Chunk.BLOCKS.SIZE));
	}

	public void update() {
		hasVertices = true;
		/*hasVertices = false;
			if (!mesh.getMesh().isEmpty()) {
				hasVertices = true;
				break;
			}
		if (!hasVertices) {
			return;
		}*/

		renderer.begin();

			material.preRender();
			renderer.addMesh(mesh.getMesh());
			material.postRender();

		renderer.end();
	}

	public boolean hasVertices() {
		return hasVertices;
	}

	public void render(RenderMaterial material) {
		if (hasVertices) {
			renderer.draw(material);
		}
	}

	public void finalize() {
		((BatchVertexRenderer)renderer.getRenderer()).finalize();
	}

	public Matrix getTransform() {
		return modelMat;
	}

	@Override
	public String toString() {
		return "ChunkMeshBatch [base=" + base + ", size=" + size + "]";
	}

	public void setMesh(ComposedMesh chunkMesh) {
		mesh = chunkMesh;
		time = chunkMesh.getTime();
	}

	public BlockFace getFace() {
		return face;
	}

	public RenderMaterial getMaterial() {
		return material;
	}

	public long getTime() {
		return time;
	}

}
