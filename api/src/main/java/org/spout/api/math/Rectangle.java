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
package org.spout.api.math;

import java.io.Serializable;
import org.spout.math.vector.Vector2f;

public class Rectangle implements Serializable {
	private static final long serialVersionUID = 2080093328836030546L;
	public static final Rectangle ZERO = new Rectangle(0, 0, 0, 0);
	final Vector2f position;
	final Vector2f extents;

	public Rectangle(Vector2f position, Vector2f extents) {
		this.position = position;
		this.extents = extents;
	}

	public Rectangle(float x, float y, float w, float h) {
		this(new Vector2f(x, y), new Vector2f(w, h));
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getExtents() {
		return extents;
	}

	public float getX() {
		return position.getX();
	}

	public float getY() {
		return position.getY();
	}

	public float getWidth() {
		return extents.getX();
	}

	public float getHeight() {
		return extents.getY();
	}

	public Rectangle multiply(Vector2f that) {
		return new Rectangle(position.mul(that), extents.mul(that));
	}

	public Rectangle divide(Vector2f by) {
		return new Rectangle(position.div(by), extents.div(by));
	}

	/**
	 * Creates a rectangle representing the texturecoordinates from a square matrix.
	 *
	 * @param textureSize Side length of the individual subtexture
	 * @param texturesX number of textures in the x direction
	 * @param texturesY number of textures in the y direction
	 * @param textureId the texture you want to extract the texcoords from
	 */
	public static Rectangle coordsFromSquareAtlas(int textureSize, int texturesInX, int texturesInY, int textureId) {

		//Calculate the size of the texture
		float textureWidth = textureSize * texturesInX;
		float textureHeight = textureSize * texturesInY;

		//Calculate the width and height of the individual texture

		float subtextureWidth = textureSize / textureWidth;
		float subtextureHeight = textureSize / textureHeight;

		//Calculate the starting coordinates for the texture
		float subtextureXId = textureId % texturesInX;
		float subtextureYId = textureId / texturesInY;

		float subtextureX = subtextureXId * subtextureWidth;
		float subtextureY = subtextureYId * subtextureHeight;

		return new Rectangle(subtextureX, subtextureY, subtextureWidth, subtextureHeight);
	}
}
