/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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

import java.awt.Color;
import java.io.Serializable;

import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.util.StringUtil;

public class Vertex implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int SIZE_FLOAT = 4;

	public static final int VERTEX_LAYER = 0;
	public static final int COLOR_LAYER = 1;
	public static final int NORMAL_LAYER = 2;
	public static final int TEXTURE0_LAYER = 3;
	public static final int TEXTURE1_LAYER = 4;
	
	public Vector3 position;
	public Color color;
	public Vector3 normal;
	public Vector2 texCoord0;
	public Vector2 texCoord1;
	public int id;
	
	/**
	 * Create a vertex with a position
	 * @param position
	 * @return
	 */
	public static Vertex createVertexPosition(Vector3 position){
		return new Vertex(position, Vector3.ZERO, Vector2.ZERO, null, 0);
	}
	
	/**
	 * Create a vertex with a position and a normal
	 * @param position
	 * @param normal
	 * @return
	 */
	public static Vertex createVertexPositionNormal(Vector3 position, Vector3 normal){
		return new Vertex(position, normal, Vector2.ZERO, null, 0);
	}

	/**
	 * Create a vertex with a position and a texture
	 * @param position
	 * @param texture
	 * @return
	 */
	public static Vertex createVertexPositionTexture0(Vector3 position, Vector2 texture) {
		return new Vertex(position, Vector3.ZERO, texture, null, 0);
	}
	
	/**
	 * Create a vertex with a position, a normal and a color
	 * @param position
	 * @param normal
	 * @param color
	 * @return
	 */
	public static Vertex createVertexPositionNormalColor(Vector3 position, Vector3 normal, Color color){
		return new Vertex(position, normal, Vector2.ZERO, color, 0);
	}
	
	/**
	 * Create a vertex with a position, a normal and a texture
	 * @param position
	 * @param normal
	 * @param texture
	 * @return
	 */
	public static Vertex createVertexPositionNormaTexture0(Vector3 position, Vector3 normal, Vector2 texture){
		return new Vertex(position, normal, texture, null, 0);
	}
	
	/**
	 * Create a vertex with a position, a normal, a texture and a color
	 * @param position
	 * @param normal
	 * @param texture
	 * @param color
	 * @return
	 */
	public static Vertex createVertexPositionNormalTexture0Color(Vector3 position, Vector3 normal, Vector2 texture, Color color){
		return new Vertex(position, normal, texture, color, 0);
	}
	
	/**
	 * Create a vertex with a position, a normal, a texture and a vertice index
	 * @param position
	 * @param normal
	 * @param texture
	 * @param id
	 * @return
	 */
	public static Vertex createVertexPositionNormalTexture0Index(Vector3 position, Vector3 normal, Vector2 texture, int id){
		return new Vertex(position, normal, texture, null, id);
	}

	/**
	 * Create a vertex with a position, a normal and a vertice index
	 * @param position
	 * @param normal
	 * @param id
	 * @return
	 */
	public static Vertex createVertexPositionNormalIndex(Vector3 position, Vector3 normal, int id) {
		return new Vertex(position, normal, Vector2.ZERO, null, id);
	}

	/**
	 * Create a vertex with a position and a vertice index
	 * @param position
	 * @param id
	 * @return
	 */
	public static Vertex createVertexPositionIndex(Vector3 position, int id) {
		return new Vertex(position, Vector3.ZERO, Vector2.ZERO, null, id);
	}

	/**
	 * Create a vertex with a position, a texture and a vertice index
	 * @param position
	 * @param texture
	 * @param id
	 * @return
	 */
	public static Vertex createVertexPositionTexture0Index(Vector3 position, Vector2 texture, int id) {
		return new Vertex(position, Vector3.ZERO, texture, null, id);
	}
	
	public Vertex(Vector3 position, Vector3 normal, Vector2 texture, Color color, int id) {
		this.position = position;
		this.normal = normal;
		this.texCoord0 = texture;
		this.color = color == null ? null : new Color( color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		this.id = id;
	}
	
	public Vertex(Vertex v) {
		this.color = v.color == null? null : new Color(v.color.getRed(),v.color.getGreen(),v.color.getBlue(),v.color.getAlpha());
		this.position = v.position == null? null : new Vector3(v.position);
		this.normal = v.normal == null? null : new Vector3(v.normal);
		this.texCoord0 = v.texCoord0 == null? null : new Vector2(v.texCoord0);
		this.texCoord1 = v.texCoord1 == null? null : new Vector2(v.texCoord1);
		this.id = v.id;
	}

	public float[] toArray(){
		return new float[] { position.getX(), position.getY(), position.getZ(), 1.0f,
							color.getRed() / 255.0f, color.getBlue() / 255.0f, color.getGreen() / 255.0f, color.getAlpha() / 255.0f,
							normal.getX(), normal.getY(), normal.getZ(),
							texCoord0.getX(), texCoord0.getY(),
							texCoord1.getX(), texCoord1.getY()
							};		
	}
	
	public int getStride(){
		int stride = 0;
		stride += SIZE_FLOAT * 4; //number of floats in a vector4
		stride += SIZE_FLOAT * 4; //number of floats in a Color
		stride += SIZE_FLOAT * 3; //number of floats in a normal
		stride += SIZE_FLOAT * 2; //number of floats in a texcoord
		stride += SIZE_FLOAT * 2; //number of floats in a texcoord
		return stride;
	}
	
	@Override
	public String toString(){
		return StringUtil.toNamedString(this, position, normal, texCoord0);
	}
	
}
