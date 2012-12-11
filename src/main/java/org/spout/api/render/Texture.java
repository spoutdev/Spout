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
package org.spout.api.render;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.spout.api.ClientOnly;
import org.spout.api.resource.Resource;

public abstract class Texture extends Resource {

	protected int[] image;
	protected int width;
	protected int height;

	public Texture(int[] baseImage, int width, int height) {
		this.image = baseImage;
		this.width = width;
		this.height = height;
	}

	public final int getHeight() {
		return this.height;
	}

	public final int getWidth() {
		return this.width;
	}

	public int[] getImage() {
		int[] colorCopy = new int[image.length];
		System.arraycopy(image, 0, colorCopy, 0, image.length);
		return colorCopy;
	}
	
	public final void setColors(Color[] colors, int offset, int num){
		for(int i = 0; i < num; i++) {
			this.image[offset + i] = colors[i].getRGB(); 
		}
	}
	
	public final void setColors(Color[] colors){
		for(int i = 0; i < colors.length; i++) {
			this.image[i] = colors[i].getRGB();
		}
	}

	public abstract Texture subTexture(int x, int y, int w, int h);

	@ClientOnly
	public abstract void writeGPU();

	@ClientOnly
	public abstract void bind();

	@ClientOnly
	public abstract boolean isLoaded();

	public static Color[] convertFromIntArray(int[] rgba){
		final Color[] colors = new Color[rgba.length];
		
		for(int i = 0; i < rgba.length; i++){
			colors[i] = new Color(rgba[i], true);
		}
		
		return colors;		
	}
	
	public static int[] converToIntArray(Color[] colors) {
		int[] rgba = new int[colors.length];
		
		for (int i = 0; i < rgba.length; i++){
			rgba[i] = colors[i].getRGB();
		}
		
		return rgba;
	}
	
}
