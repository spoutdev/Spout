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

import java.util.ArrayList;
import java.util.Map.Entry;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Cuboid;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Matrix;
import org.spout.api.math.Vector3;
import org.spout.api.model.MeshFace;
import org.spout.api.render.RenderMaterial;
import org.spout.engine.mesh.ChunkMesh;
import org.spout.engine.renderer.BatchVertexRenderer;

/**
 * Represents a group of chunk meshes to be rendered.
 */
public class ChunkMeshBatch extends Cuboid {
	public static final int SIZE_X = 1;
	public static final int SIZE_Y = 1;
	public static final int SIZE_Z = 1;
	public static final Vector3 SIZE = new Vector3(SIZE_X, SIZE_Y, SIZE_Z);
	public static final int MESH_COUNT = SIZE_X * SIZE_Y * SIZE_Z;
	
	private PrimitiveBatch renderer = new PrimitiveBatch();
	private ChunkMesh[] meshes = new ChunkMesh[MESH_COUNT];
	private boolean hasVertices = false;
	private Matrix modelMat = MathHelper.createIdentity();
	
	public ChunkMeshBatch(World world, int baseX, int baseY, int baseZ) {
		super(new Point(world, baseX, baseY, baseZ), SIZE);
		modelMat = MathHelper.translate(new Vector3(baseX * SIZE_X, baseY * SIZE_Y, baseZ * SIZE_Z));
	}

	public void update() {
		hasVertices = false;
		for (ChunkMesh mesh : meshes) {
			if (mesh.hasVertices()) {
				hasVertices = true;
				continue;
			}
		}
		if (!hasVertices) {
			return;
		}

		renderer.begin();
		
		for (ChunkMesh chunkMesh : meshes) {
			for(Entry<RenderMaterial, ArrayList<MeshFace>> entry : chunkMesh.getOpaqueMesh().entrySet()){
				entry.getKey().preRender();
				renderer.addMesh(entry.getValue());
				entry.getKey().postRender();
			}
		}
		
		for (ChunkMesh chunkMesh : meshes) {
			for(Entry<RenderMaterial, ArrayList<MeshFace>> entry : chunkMesh.getTransparentMesh().entrySet()){
				entry.getKey().preRender();
				renderer.addMesh(entry.getValue());
				entry.getKey().postRender();
			}
		}
		
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

	/**
	 * Gets the coordinates of the given chunk's batcher
	 *
	 * @param chunkCoords
	 * @return the coords of the chunk's batcher
	 */
	public static Vector3 getBatchCoordinates(Vector3 chunkCoords) {
		return new Vector3(Math.floor(chunkCoords.getX() / (float) SIZE_X), Math.floor(chunkCoords.getY() / (float) SIZE_Y), Math.floor(chunkCoords.getZ() / (float) SIZE_Z));
	}

	/**
	 * Gets the coordinates of the given batcher's chunk
	 *
	 * @param batchCoords
	 * @return the coords of the batcher's chunk
	 */
	public static Vector3 getChunkCoordinates(Vector3 batchCoords) {
		return new Vector3(batchCoords.getX() * SIZE_X, batchCoords.getY() * SIZE_Y, batchCoords.getZ() * SIZE_Z);
	}

	private int getIndexFromChunkMesh(ChunkMesh chunkMesh) {
		// TODO : Implement it if SIZE != 1
		return 0;
	}

	public void addMesh(ChunkMesh chunkMesh) {
		meshes[getIndexFromChunkMesh(chunkMesh)] = chunkMesh;
	}

	public void removeMesh(ChunkMesh chunkMesh) {
		meshes[getIndexFromChunkMesh(chunkMesh)] = null;
	}
	
	public boolean isFull() {
		for(ChunkMesh mesh : meshes)
			if(mesh == null)
				return false;
		return true;
	}
	
	public boolean isEmpty() {
		for(ChunkMesh mesh : meshes)
			if(mesh != null)
				return false;
		return true;
	}

}
