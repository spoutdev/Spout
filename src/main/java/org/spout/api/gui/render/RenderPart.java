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
package org.spout.api.gui.render;

import java.awt.Color;

import org.spout.api.math.Rectangle;
import org.spout.api.render.RenderMaterial;

public abstract class RenderPart implements Comparable<RenderPart> {
	Rectangle source;
	Rectangle sprite;
	int zIndex = 0;
	Color color;
	RenderMaterial material;
	
	public void setSource(Rectangle source) {
		this.source = source;
	}
	
	public void setSprite(Rectangle sprite) {
		this.sprite = sprite;
	}
	
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}
	
	public int getZIndex() {
		return zIndex;
	}
	
	public Rectangle getSource() {
		return source;
	}
	
	public Rectangle getSprite() {
		return sprite;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	@Override
	public int compareTo(RenderPart arg0) {
		return arg0.getZIndex() - getZIndex();
	}
	
	public RenderMaterial getRenderMaterial() {
		return material;
	}
	
	public void setRenderMaterial(RenderMaterial material) {
		this.material = material;
	}
}
