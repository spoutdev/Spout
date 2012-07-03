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
package org.spout.api.model;

import java.awt.Color;

import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.util.StringUtil;

public class Vertex {
	public static final int SIZE_FLOAT = 4;
	
	public Vector3 position;
	public Color color;
	public Vector3 normal;
	public Vector2 texCoord0;
	public Vector2 texCoord1;
	
	public Vertex(Vector3 position, Vector3 normal, Vector2 texture) {
		this.position = position;
		this.normal = normal;
		this.texCoord0 = texture;
	}
	
	public Vertex(Vector3 position, Vector3 normal){
		this(position, normal, Vector2.ZERO);
	}
	
	public Vertex(Vector3 position) {
		this(position, Vector3.ZERO, Vector2.ZERO);
	}
	
	public Vertex(Vector3 position, Vector2 texture){
		this(position, Vector3.ZERO, texture);
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
	
	public String toString(){
		return StringUtil.toNamedString(this, position, normal, texCoord0);
	}
	
}
