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
package org.spout.api.render.effect;

import gnu.trove.map.hash.TIntObjectHashMap;

import org.spout.api.material.Material;
import org.spout.api.math.Vector3;
import org.spout.api.render.shader.VertexBuffer;

public class SnapshotBatch {

	private Material material;
	private Vector3 position;
	private TIntObjectHashMap<VertexBuffer> vertexBuffers = new TIntObjectHashMap<VertexBuffer>();

	public SnapshotBatch(Material material, Vector3 position){
		this.material = material;
		this.position = position;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public TIntObjectHashMap<VertexBuffer> getVertexBuffers() {
		return vertexBuffers;
	}

	public void addVertexBuffers(VertexBuffer vertexBuffer) {
		vertexBuffers.put(vertexBuffer.getLayout(),vertexBuffer);
	}

}
