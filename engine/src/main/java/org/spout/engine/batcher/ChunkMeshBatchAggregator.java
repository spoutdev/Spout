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
package org.spout.engine.batcher;

import org.lwjgl.opengl.GL11;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Cuboid;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Matrix;
import org.spout.api.math.MatrixMath;
import org.spout.api.math.Vector3;
import org.spout.api.render.BufferContainer;
import org.spout.api.render.RenderMaterial;
import org.spout.engine.mesh.ChunkMesh;
import org.spout.engine.renderer.BatchVertexRenderer;

/**
 * Represents a group of chunk meshes to be rendered.
 */
public class ChunkMeshBatchAggregator extends Cuboid {
	private boolean queued = false;
	public final static int SIZE_X = 1;
	public final static int SIZE_Y = 8;
	public final static int SIZE_Z = 1;
	public final static Vector3 SIZE = new Vector3(SIZE_X, SIZE_Y, SIZE_Z);
	public final static int COUNT = SIZE_X * SIZE_Y * SIZE_Z;
	private int count = 0;
	private BatchVertexRenderer renderer = (BatchVertexRenderer) BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
	public final static Matrix model = MatrixMath.createIdentity();
	private final RenderMaterial material;
	private boolean dataSent = false;
	private boolean ready = false;
	private final BufferContainer bufferContainer[] = new BufferContainer[COUNT];

	/**
	 * Gets the linear position of a local 3D coord
	 */
	private int getIndex(int x, int y, int z) {
		if (x > SIZE_X || x < 1) {
			throw new IllegalArgumentException("x must be between 1 (inclusive) and SIZE_X (" + SIZE_X + ") (inclusive). It was " + x);
		}
		if (y > SIZE_Y || y < 1) {
			throw new IllegalArgumentException("x must be between 1 (inclusive) and SIZE_Y (" + SIZE_Y + ") (inclusive). It was " + y);
		}
		if (z > SIZE_Z || z < 1) {
			throw new IllegalArgumentException("x must be between 1 (inclusive) and SIZE_Z (" + SIZE_Z + ") (inclusive). It was " + z);
		}
		int index = (x - 1) * SIZE_Y * SIZE_Z + (y - 1) * SIZE_Z + (z - 1);
		return index;
	}

	/**
	 * Gets the base of a chunk mesh, which is the coord of the ChunkMeshBatchAggregator
	 */
	public static Vector3 getBaseFromChunkMesh(ChunkMesh mesh) {
		int localX = (mesh.getChunkX() >= 0 ? mesh.getChunkX() + 1 : Math.abs(mesh.getChunkX())) - 1;
		int localY = (mesh.getChunkY() >= 0 ? mesh.getChunkY() + 1 : Math.abs(mesh.getChunkY())) - 1;
		int localZ = (mesh.getChunkZ() >= 0 ? mesh.getChunkZ() + 1 : Math.abs(mesh.getChunkZ())) - 1;
		int x = (int) (Math.floor((float) localX / SIZE_X) * SIZE_X);
		int y = (int) (Math.floor((float) localY / SIZE_Y) * SIZE_Y);
		int z = (int) (Math.floor((float) localZ / SIZE_Z) * SIZE_Z);
		x = (mesh.getChunkX() >= 0 ? x : (-1 * x) - 1);
		y = (mesh.getChunkY() >= 0 ? y : (-1 * y) - 1);
		z = (mesh.getChunkZ() >= 0 ? z : (-1 * z) - 1);
		return new Vector3(x, y, z);
	}

	/**
	 * Returns the local position of a ChunkMesh with a ChunkMeshBatchAggregator
	 */
	public static Vector3 getLocalCoordFromChunkMesh(ChunkMesh mesh) {
		int localX = (mesh.getChunkX() >= 0 ? mesh.getChunkX() + 1 : Math.abs(mesh.getChunkX())) - 1;
		int localY = (mesh.getChunkY() >= 0 ? mesh.getChunkY() + 1 : Math.abs(mesh.getChunkY())) - 1;
		int localZ = (mesh.getChunkZ() >= 0 ? mesh.getChunkZ() + 1 : Math.abs(mesh.getChunkZ())) - 1;
		int x = localX % SIZE_X + 1;
		int y = localY % SIZE_Y + 1;
		int z = localZ % SIZE_Z + 1;

		return new Vector3(x, y, z);
	}

	public ChunkMeshBatchAggregator(World world, int x, int y, int z, RenderMaterial material) {
		super(new Point(world, x << Chunk.BLOCKS.BITS, y << Chunk.BLOCKS.BITS, z << Chunk.BLOCKS.BITS), SIZE.multiply(Chunk.BLOCKS.SIZE));
		this.material = material;
	}

	public boolean update() {
		//Send data
		if (!dataSent) {
			renderer.setBufferContainers(bufferContainer);
			dataSent = true;
		}

		//Start to flush
		if (renderer.flush(false)) {
			ready = true;
			return true;
		} else {
			return false;
		}
	}

	public void render(RenderMaterial material) {
		renderer.draw(material);
	}

	@Override
	public String toString() {
		return "ChunkMeshBatch [base=" + getBase() + ", size=" + getSize() + "]";
	}

	public void setSubBatch(BufferContainer bufferContainer, int x, int y, int z) {
		int index = getIndex(x, y, z);

		if (bufferContainer == null && this.bufferContainer[index] != null) {
			count--;
		} else if (bufferContainer != null && this.bufferContainer[index] == null) {
			count++;
		}

		this.bufferContainer[index] = bufferContainer;
		dataSent = false;
	}

	public RenderMaterial getMaterial() {
		return material;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	/**
	 * We can have only one instance of ChunkMeshBatchAggregator at one position and material So we need to override the equals of extended class cuboid
	 */
	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}

	public void setQueued(boolean queued) {
		this.queued = queued;
	}

	public boolean isQueued() {
		return queued;
	}

	public boolean isReady() {
		return ready;
	}

	public void preRender() {
		renderer.preDraw();
	}

	public void postRender() {
		renderer.postDraw();
	}

	public void clear() {
		renderer.release();
	}
}