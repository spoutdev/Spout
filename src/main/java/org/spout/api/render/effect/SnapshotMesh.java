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

import org.spout.api.geo.cuboid.ChunkSnapshotModel;
import org.spout.api.material.Material;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;
import org.spout.api.model.mesh.Mesh;

public class SnapshotMesh {

	private Mesh mesh;
	private Material material;
	private ChunkSnapshotModel snapshotModel;
	private Vector3 position;
	private BlockFace face;
	private boolean[] toRender;

	public SnapshotMesh(Material material, ChunkSnapshotModel snapshotModel, Vector3 position, BlockFace face, boolean toRender[]){
		this.setMaterial(material);
		this.setSnapshotModel(snapshotModel);
		this.setPosition(position);
		this.setFace(face);
		this.setToRender(toRender);
		mesh = material.getModel().getMesh();
	}

	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public ChunkSnapshotModel getSnapshotModel() {
		return snapshotModel;
	}

	public void setSnapshotModel(ChunkSnapshotModel snapshotModel) {
		this.snapshotModel = snapshotModel;
	}

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public BlockFace getFace() {
		return face;
	}

	public void setFace(BlockFace face) {
		this.face = face;
	}

	public boolean[] getToRender() {
		return toRender;
	}

	public void setToRender(boolean[] toRender) {
		this.toRender = toRender;
	}

}
