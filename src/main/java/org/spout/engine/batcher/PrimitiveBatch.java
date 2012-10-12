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

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import org.spout.api.math.Vector3;
import org.spout.api.model.MeshFace;
import org.spout.api.model.Vertex;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.Renderer;

import org.spout.engine.mesh.BaseMesh;
import org.spout.engine.renderer.BatchVertexRenderer;

public class PrimitiveBatch {
	private final Renderer renderer;
	private final Vector3[] cubeCorners = new Vector3[]{Vector3.ZERO, Vector3.UNIT_Y, new Vector3(0, 1, 1), Vector3.UNIT_Z,
			Vector3.UNIT_X, new Vector3(1, 1, 0), Vector3.ONE, new Vector3(1, 0, 1)};

	public PrimitiveBatch() {
		renderer = BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public void begin() {
		renderer.begin();
	}

	public void addCube(Vector3 location, Vector3 scale, Color c, boolean[] sides) {
		if (sides.length != 6) {
			throw new IllegalStateException("Must have 6 sides!");
		}
		
		Vector3 p0 = cubeCorners[0].multiply(scale).add(location);
		Vector3 p1 = cubeCorners[1].multiply(scale).add(location);
		Vector3 p2 = cubeCorners[2].multiply(scale).add(location);
		Vector3 p3 = cubeCorners[3].multiply(scale).add(location);
		Vector3 p4 = cubeCorners[4].multiply(scale).add(location);
		Vector3 p5 = cubeCorners[5].multiply(scale).add(location);
		Vector3 p6 = cubeCorners[6].multiply(scale).add(location);
		Vector3 p7 = cubeCorners[7].multiply(scale).add(location);
		
		/*   1--2
		 *  /| /|
		 * 5--6 |
		 * | 0|-3
		 * |/ |/
		 * 4--7
		 */
		
		if (sides[0]) {
			addQuad(p0, p1, p2, p3, c);
		}
		if (sides[1]) {
			addQuad(p7, p6, p5, p4, c);
		}
		if (sides[2]) {
			addQuad(p3, p2, p6, p7, c);
		}

		if (sides[3]) {
			addQuad(p4, p5, p1, p0, c);
		}
		if (sides[4]) {
			addQuad(p1, p5, p6, p2, c);
		}
		if (sides[5]) {
			addQuad(p4, p0, p3, p7, c);
		}
	}

	public void addQuad(Vector3 a, Vector3 b, Vector3 c, Vector3 d, Color col) {
		renderer.addTexCoord(0f, 0f);
		renderer.addColor(col);
		renderer.addVertex(a);
		renderer.addTexCoord(1f, 0f);
		renderer.addColor(col);
		renderer.addVertex(b);
		renderer.addTexCoord(1f, 1f);
		renderer.addColor(col);
		renderer.addVertex(c);

		renderer.addTexCoord(1f, 1f);
		renderer.addColor(col);
		renderer.addVertex(c);
		renderer.addTexCoord(0f, 1f);
		renderer.addColor(col);
		renderer.addVertex(d);
		renderer.addTexCoord(0f, 0f);
		renderer.addColor(col);
		renderer.addVertex(a);
	}

	
	public void addMesh(BaseMesh mesh) {
		for (MeshFace face : mesh) {
			for (Vertex vert : face) {
				renderer.addTexCoord(vert.texCoord0);
				renderer.addNormal(vert.normal);
				renderer.addColor(vert.color);
				renderer.addVertex(vert.position);
			}
		}
	}
	
	public void end() {
		renderer.end();
	}

	public void draw(RenderMaterial material) {
		renderer.render(material);
	}
}
