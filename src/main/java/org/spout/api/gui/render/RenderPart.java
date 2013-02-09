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
package org.spout.api.gui.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.spout.api.math.Rectangle;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.model.mesh.Vertex;

public class RenderPart implements Comparable<RenderPart> {
	private Rectangle source = Rectangle.ZERO;
	private Rectangle sprite = Rectangle.ZERO;
	private int zIndex = 0;
	private Color color = Color.WHITE;
	
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

	public List<Vertex> getVertices() {
		List<Vertex> face = new ArrayList<Vertex>();
		Vector3 p1 = new Vector3(sprite.getX(), sprite.getY(), 0);
		Vector3 p2 = new Vector3(sprite.getX() + sprite.getWidth(), sprite.getY(), 0);
		Vector3 p3 = new Vector3(sprite.getX() + sprite.getWidth(), sprite.getY() - sprite.getHeight(), 0);
		Vector3 p4 = new Vector3(sprite.getX(), sprite.getY() - sprite.getHeight(), 0);

		Vector2 t1 = new Vector2(source.getX(), source.getY());
		Vector2 t2 = new Vector2(source.getX() + source.getWidth(), source.getY());
		Vector2 t3 = new Vector2(source.getX() + source.getWidth(), source.getY() + source.getHeight());
		Vector2 t4 = new Vector2(source.getX(), source.getY() + source.getHeight());

		face.add(Vertex.createVertexPositionTexture0(p1, t1));
		face.add(Vertex.createVertexPositionTexture0(p2, t2));
		face.add(Vertex.createVertexPositionTexture0(p3, t3));
		face.add(Vertex.createVertexPositionTexture0(p4, t4));

		for (Vertex v : face) {
			v.color = color;
		}

		return face;
	}

	@Override
	public int compareTo(RenderPart arg0) {
		return arg0.getZIndex() - getZIndex();
	}
}
