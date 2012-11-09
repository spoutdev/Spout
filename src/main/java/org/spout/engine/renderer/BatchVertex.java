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
package org.spout.engine.renderer;

import gnu.trove.list.array.TFloatArrayList;

import java.awt.Color;

import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;

public class BatchVertex{

	//Using FloatArrayList because I need O(1) access time
	//and fast ToArray()
	public TFloatArrayList vertexBuffer = new TFloatArrayList();
	public TFloatArrayList colorBuffer = new TFloatArrayList();
	public TFloatArrayList normalBuffer = new TFloatArrayList();
	public TFloatArrayList uvBuffer = new TFloatArrayList();
	int numVertices = 0;
	boolean useColors = false;
	boolean useNormals = false;
	boolean useTextures = false;
	
	public BatchVertex() {
	}

	public int getVertexCount(){
		return numVertices;
	}

	public void addVertex(float x, float y, float z, float w) {
		vertexBuffer.add(x);
		vertexBuffer.add(y);
		vertexBuffer.add(z);
		vertexBuffer.add(w);

		numVertices++;
	}

	public void addVertex(float x, float y, float z) {
		addVertex(x, y, z, 1.0f);
	}

	public void addVertex(float x, float y) {
		addVertex(x, y, 1.0f, 1.0f);
	}

	public void addVertex(Vector3 vertex) {
		addVertex(vertex.getX(), vertex.getY(), vertex.getZ());
	}

	public void addVertex(Vector2 vertex) {
		addVertex(vertex.getX(), vertex.getY());
	}

	public void addVertex(Vector4 vertex) {
		addVertex(vertex.getX(), vertex.getY(), vertex.getZ(), vertex.getZ());
	}

	public void addColor(float r, float g, float b) {
		addColor(r, g, b, 1.0f);
	}

	public void addColor(float r, float g, float b, float a) {
		useColors = true;
		colorBuffer.add(r);
		colorBuffer.add(g);
		colorBuffer.add(b);
		colorBuffer.add(a);
	}

	public void addColor(int r, int g, int b, int a) {
		addColor(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
	}

	public void addColor(Color color) {
		addColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public void addNormal(float x, float y, float z, float w) {
		useNormals = true;
		normalBuffer.add(x);
		normalBuffer.add(y);
		normalBuffer.add(z);
		normalBuffer.add(w);
	}

	public void addNormal(float x, float y, float z) {
		addNormal(x, y, z, 1.0f);
	}

	public void addNormal(Vector3 vertex) {
		addNormal(vertex.getX(), vertex.getY(), vertex.getZ());
	}

	public void addNormal(Vector4 vertex) {
		addNormal(vertex.getX(), vertex.getY(), vertex.getZ(), vertex.getZ());
	}

	public void addTexCoord(float u, float v) {
		useTextures = true;
		uvBuffer.add(u);
		uvBuffer.add(v);
	}

	public void addTexCoord(Vector2 uv) {
		addTexCoord(uv.getX(), uv.getY());
	}

	public void dumpBuffers() {
		System.out.println("BatchVertexRenderer Debug Ouput: Verts: " + numVertices + " Using {colors, normal, textures} {" + useColors + ", " + useNormals + ", " + useTextures + "}");
		System.out.println("colors:"+colorBuffer.size()+", normals:"+normalBuffer.size()+", vertices:"+vertexBuffer.size());
		for (int i = 0; i < numVertices; i++) {
			int index = i * 4;

			if (useColors) {
				System.out.print("Color: {" + colorBuffer.get(index) + " " + colorBuffer.get(index + 1) + " " + colorBuffer.get(index + 2) + " " + colorBuffer.get(index + 3) + "}\t");
			}
			if (useNormals) {
				System.out.print("Normal : {" + normalBuffer.get(index) + " " + normalBuffer.get(index + 1) + " " + normalBuffer.get(index + 2) + "}");
			}
			if (useTextures) {
				System.out.print("TexCoord0 : {" + uvBuffer.get((i * 2)) + " " + uvBuffer.get((i * 2) + 1) + "}");
			}
			System.out.println("Vertex : {" + vertexBuffer.get(index) + " " + vertexBuffer.get(index + 1) + " " + vertexBuffer.get(index + 2) + " " + vertexBuffer.get(index + 3) + "}");
		}
	}
}
