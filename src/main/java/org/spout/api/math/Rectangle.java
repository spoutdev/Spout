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
package org.spout.api.math;

public class Rectangle {
	final Vector2 position;
	final Vector2 extents;
	
	public Rectangle(Vector2 position, Vector2 extents){
		this.position = position;
		this.extents = extents;
	}
	
	public Rectangle(float x, float y, float w, float h){
		this(new Vector2(x,y), new Vector2(w,h));
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public Vector2 getExtents() {
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
	
	/**
	 * Creates a rectangle representing the texturecoordinates from a square matrix.  
	 * 
	 * 
	 * @param textureSize Side length of the individual subtexture
	 * @param texturesX number of textures in the x direction
	 * @param texturesY number of textures in the y direction
	 * @param textureId the texture you want to extract the texcoords from
	 
	 * @return
	 */
	public static Rectangle coordsFromSquareAtlas(int textureSize, int texturesInX, int texturesInY, int textureId){
		
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
