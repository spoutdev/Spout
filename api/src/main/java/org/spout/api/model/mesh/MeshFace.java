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
package org.spout.api.model.mesh;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.collections.iterators.ArrayIterator;

import org.spout.math.vector.Vector3;
import org.spout.api.util.StringUtil;
import org.spout.math.vector.Vector2;

/**
 * Represents a Triangle for a model face
 */
public class MeshFace implements Iterable<Vertex>, Serializable {
	private static final long serialVersionUID = 1L;
	Vertex[] verts = new Vertex[3];

	public MeshFace(Vertex v1, Vertex v2, Vertex v3) {
		verts[0] = v1;
		verts[1] = v2;
		verts[2] = v3;
	}

	/**
	 * Recalculates the normals for this triangle. All points must be 0'd before this.
	 */
	protected void doRecalculateNormals() {
		Vector3 trinormal = verts[0].position.sub(verts[1].position).cross(verts[1].position.sub(verts[2].position)).normalize();
		verts[0].normal = verts[0].normal.add(trinormal).normalize();
		verts[1].normal = verts[1].normal.add(trinormal).normalize();
		verts[2].normal = verts[2].normal.add(trinormal).normalize();
	}

	Vector3[] getPositions() {
		return new Vector3[] {verts[0].position, verts[1].position, verts[2].position};
	}

	Vector3[] getNormals() {
		return new Vector3[] {verts[0].normal, verts[1].normal, verts[2].normal};
	}

	Vector2[] getUVs() {
		return new Vector2[] {verts[0].texCoord0, verts[1].texCoord0, verts[2].texCoord0};
	}

	@Override
	public String toString() {
		return StringUtil.toNamedString(this, verts[0], verts[1], verts[2]);
	}

	@SuppressWarnings ("unchecked")
	@Override
	public Iterator<Vertex> iterator() {
		return new ArrayIterator(verts);
	}
}
