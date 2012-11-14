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
package org.spout.engine.entity.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.spout.api.chat.style.ColorChatStyle;
import org.spout.api.chat.style.ResetChatStyle;
import org.spout.api.component.components.TextModelComponent;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.math.Rectangle;
import org.spout.api.model.mesh.MeshFace;
import org.spout.api.model.mesh.Vertex;
import org.spout.api.render.Camera;
import org.spout.api.render.Font;
import org.spout.engine.mesh.BaseMesh;

public class ClientTextModelComponent extends TextModelComponent {
	private BaseMesh mesh;
	
	public void updateMesh() {
		ArrayList<MeshFace> faces = new ArrayList<MeshFace>();
		
		Font font = getFont();
		Color color = Color.black;
		
		float ratio = 30f/size;
		
		float w = font.getWidth();
		float h = font.getHeight();
		
		float xCursor = 0;
		float yCursor = 0;
		
		for (Object arg : getText().getArguments()) {
			if (arg instanceof String) {
				String txt = (String) arg;
				for (int i=0 ; i<txt.length() ; i++) {
					char c = txt.charAt(i);
					if (c==' ') {
						xCursor += font.getSpaceWidth() / ratio;
					} else if (c == '\n') {
						xCursor = 0;
						yCursor -= font.getCharHeight() / ratio;
					} else {
						java.awt.Rectangle r = font.getPixelBounds(c);

						RenderPart part = new RenderPart();
						part.setRenderMaterial(font.getMaterial());
						part.setColor(color);
						part.setSprite(new Rectangle(xCursor, yCursor, (float) r.width / ratio, h / ratio));
						part.setSource(new Rectangle(r.x / w, 0f, r.width / w, 1f));

						xCursor += (float) font.getAdvance(c) / ratio;

						List<Vertex> v = part.getVertices();
						
						faces.add(new MeshFace(v.get(0), v.get(1), v.get(3)));
						faces.add(new MeshFace(v.get(2), v.get(3), v.get(1)));
					}
				}
			} else if (arg instanceof ColorChatStyle) {
				color = ((ColorChatStyle) arg).getColor();
			} else if (arg instanceof ResetChatStyle) {
				color = Color.black;
			}
		}
		
		translation = translation.subtract(xCursor/2.f,0,0);
		
		mesh = new BaseMesh(faces);
		mesh.batch();
	}
	
	public void render(Camera camera) {
		if (dirty) {
			dirty = false;
			updateMesh();
		}
		
		Transform mt = getOwner().getTransform().getTransform();
		mt.setPosition(mt.getPosition().add(translation));
		
		//TODO: Implements lookCamera, basicaly its the inverse of the camera's rotation
		
		getFont().getMaterial().getShader().setUniform("View", camera.getView());
		getFont().getMaterial().getShader().setUniform("Projection", camera.getProjection());
		getFont().getMaterial().getShader().setUniform("Model", mt.toMatrix());
		
		mesh.render(getFont().getMaterial());
	}
}
