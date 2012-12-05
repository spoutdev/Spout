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

import org.lwjgl.opengl.GL11;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Cube;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Matrix;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.effect.SnapshotBatch;
import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.renderer.BufferContainer;

/**
 * Represents a group of chunk meshes to be rendered.
 */
public class ChunkMeshBatchAggregator extends Cube {

	private int count = 0;

	private BatchVertexRenderer renderer = (BatchVertexRenderer) BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);

	public final static Matrix model = MathHelper.createIdentity();
	private final RenderMaterial material;
	private boolean generated = false;
	private boolean closed = false;

	private BufferContainer bufferContainer;

	public ChunkMeshBatchAggregator(World world, int x, int y, int z, RenderMaterial material) {
		super(new Point(world, x << Chunk.BLOCKS.BITS, y << Chunk.BLOCKS.BITS, z << Chunk.BLOCKS.BITS), Chunk.BLOCKS.SIZE);
		this.material = material;
	}

	public void update() {
		if (closed) {
			throw new IllegalStateException("Already closed");
		}

		renderer.begin();

		SnapshotBatch snapshotBatch = new SnapshotBatch(material);

		material.preBatch(snapshotBatch);
		((BatchVertexRenderer)renderer).setBufferContainer(bufferContainer);
		material.postBatch(snapshotBatch);

		renderer.end();

		generated = true;
	}

	public void render(RenderMaterial material) {
		if (closed) {
			throw new IllegalStateException("Already closed");
		}

		if(generated){
			renderer.render(material);
		}
	}

	public void finalize() {
		if (closed) {
			throw new IllegalStateException("Already closed");
		}

		bufferContainer = null;
		((BatchVertexRenderer)renderer).release();

		closed = true;
	}

	@Override
	public String toString() {
		return "ChunkMeshBatch [base=" + getBase() + ", size=" + getSize() + "]";
	}

	public void setSubBatch(BufferContainer bufferContainer) {
		this.bufferContainer = bufferContainer;
	}

	public RenderMaterial getMaterial() {
		return material;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	/**
	 * We can have only one instance of ChunkMeshBatchAggregator at one position and material
	 * So we need to override the equals of extended class cuboid
	 */
	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}

}