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
package org.spout.api.render.effect;

import java.util.List;

import org.spout.api.geo.cuboid.ChunkSnapshotModel;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.Material;
import org.spout.api.model.mesh.Mesh;
import org.spout.api.model.mesh.MeshFace;

public class SnapshotMesh {
	private Mesh mesh;
	private Material material;
	private ChunkSnapshotModel snapshotModel;
	private Point position;
	private boolean[] toRender;
	private List<MeshFace> faces;

	public SnapshotMesh(Material material, ChunkSnapshotModel snapshotModel, Point position, boolean toRender[]) {
		this.setMaterial(material);
		this.setSnapshotModel(snapshotModel);
		this.setPosition(position);
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

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public boolean[] getToRender() {
		return toRender;
	}

	public void setToRender(boolean[] toRender) {
		this.toRender = toRender;
	}

	public void setResult(List<MeshFace> faces) {
		this.faces = faces;
	}

	public List<MeshFace> getResult() {
		return faces;
	}
}
