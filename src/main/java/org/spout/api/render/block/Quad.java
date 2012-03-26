/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.render.block;

import org.spout.api.render.SubTexture;
import org.spout.api.render.Vertex;

public class Quad {
	private int index;
	private SubTexture texture;
	private Vertex[] vertexes = new Vertex[4];

	/**
	 * Creates a new quad with the following vertexes at the specified index
	 *
	 * @param index of the quad
	 * @param texture Subtexture to use
	 * @param v1 first vertex
	 * @param v2 second vertex
	 * @param v3 third vertex
	 * @param v4 fourth vertex
	 */
	public Quad(int index, SubTexture texture, Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
		this(index, texture);
		vertexes[0] = v1;
		vertexes[1] = v2;
		vertexes[2] = v3;
		vertexes[3] = v4;
	}

	/**
	 * Creates an empty quad at index based on the SubTexture
	 *
	 * @param index of the quad
	 * @param texture
	 */
	public Quad(int index, SubTexture texture) {
		this.index = index;
		this.texture = texture;
	}

	/**
	 * Adds a vertex to the quad with the SubTexture properties of this quad
	 *
	 * @param index of the vertex
	 * @param x value of the vertex
	 * @param y value of the vertex
	 * @param z value of the vertex
	 * @return this
	 */
	public Quad addVertex(int vertex, float x, float y, float z) {
		if (vertex < 0 || vertex > 3) {
			throw new IllegalArgumentException("Invalid vertex index: " + vertex);
		}
		vertexes[vertex] = new Vertex(vertex, index, x, y, z, texture);
		return this;
	}

	/**
	 * Adds a vertex to the quad
	 *
	 * @param vertex to add
	 * @return this
	 */
	public Quad addVertex(Vertex vertex) {
		vertexes[vertex.getIndex()] = vertex;

		return this;
	}

	/**
	 * Gets the vertex of the specified index
	 *
	 * @param index of the vertex
	 * @return the vertex
	 */
	public Vertex getVertex(int index) {
		if (index < 0 || index > 3) {
			throw new IllegalArgumentException("Invalid vertex index: " + index);
		}

		return vertexes[index];
	}

	public int getIndex() {
		return index;
	}
}
