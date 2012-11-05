/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.render;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.geo.cuboid.ChunkSnapshotModel;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;
import org.spout.api.model.MeshFace;
import org.spout.api.resource.Resource;

public abstract class RenderMaterial extends Resource implements Comparable<RenderMaterial> {
	
	private static final AtomicInteger idCounter = new AtomicInteger();
	
	private final int id;
	
	protected RenderMaterial() {
		this.id = idCounter.getAndIncrement();
	}
	
	/**
	 * Returns a material param or null if that doesn't exist
	 * @param name
	 * @return
	 */
	public abstract Object getValue(String name);
	
	/**
	 * Returns the shader specified in the material
	 * @return
	 */
	public abstract Shader getShader();
	/**
	 * Assigns the current shader and prepairs the material for rendering
	 */
	public abstract void assign();
	
	/**
	 * Called right before rendering
	 */
	public abstract void preRender();
	
	/**
	 * Called right after rendering
	 */
	public abstract void postRender();
	
	/**
	 * Called to render a block
	 */
	public abstract List<MeshFace> render(ChunkSnapshotModel chunkSnapshotModel, Vector3 position, List<BlockFace> faces);

	/**
	 * Called to render a block side
	 * @param chunkSnapshotModel
	 * @param position
	 * @param face
	 * @return
	 */
	public abstract List<MeshFace> render(ChunkSnapshotModel chunkSnapshotModel,
			Vector3 position, BlockFace face);

	/**
	 * Return the render pass order
	 * @return
	 */
	public abstract int getLayer();
	
	@Override
	public final int compareTo(RenderMaterial o) {
		if (o == this) {
			return 0;
		} else {
			int l1 = getLayer();
			int l2 = o.getLayer();
			if (l1 != l2) {
				return l1 - l2;
			} else {
				return id - o.id;
			}
		}
	}
	
	@Override
	public final boolean equals(Object o) {
		return o == this;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}
