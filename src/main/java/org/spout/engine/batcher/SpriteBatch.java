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
package org.spout.engine.batcher;

import java.awt.Color;
import java.util.ArrayList;

import org.spout.api.math.MathHelper;
import org.spout.api.math.Rectangle;

import org.lwjgl.opengl.GL11;
import org.spout.api.math.Matrix;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.Renderer;
import org.spout.engine.renderer.BatchVertexRenderer;

public class SpriteBatch {
	private class TextureRectangle {
		public Rectangle destination;
		public Rectangle source;
		public RenderMaterial material;
	}
	
	
	Renderer renderer;
	ArrayList<TextureRectangle> sprites = new ArrayList<TextureRectangle>();
	Matrix view;
	Matrix projection;
	
	public SpriteBatch() {
		this.renderer = BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
		this.projection = MathHelper.createIdentity();
		this.view = MathHelper.createIdentity();
	}
	
	public void begin() {
		sprites.clear();
	}
	
	public void render() {
		renderer.begin();
		for(int i = 0; i < sprites.size(); i++) {
			TextureRectangle rect = sprites.get(i);

			renderer.addVertex(rect.destination.getX(), rect.destination.getY() + rect.destination.getHeight());
			renderer.addColor(Color.white);			
			renderer.addTexCoord(rect.source.getX(), rect.source.getY());			
			
			renderer.addVertex(rect.destination.getX(), rect.destination.getY());
			renderer.addColor(Color.white);
			renderer.addTexCoord(rect.source.getX(), rect.source.getY() + rect.source.getHeight());
			
			renderer.addVertex(rect.destination.getX() + rect.destination.getWidth(), rect.destination.getY());
			renderer.addColor(Color.white);			
			renderer.addTexCoord(rect.source.getX() + rect.source.getWidth(), rect.source.getY() + rect.source.getHeight());
			
			
			renderer.addVertex(rect.destination.getX(), rect.destination.getY() + rect.destination.getHeight());
			renderer.addColor(Color.white);			
			renderer.addTexCoord(rect.source.getX(), rect.source.getY());	
			
			renderer.addVertex(rect.destination.getX() + rect.destination.getWidth(), rect.destination.getY());
			renderer.addColor(Color.white);			
			renderer.addTexCoord(rect.source.getX() + rect.source.getWidth(), rect.source.getY() + rect.source.getHeight());
			
			renderer.addVertex(rect.destination.getX() + rect.destination.getWidth(), rect.destination.getY() + rect.destination.getHeight());
			renderer.addColor(Color.white);			
			renderer.addTexCoord(rect.source.getX() + rect.source.getWidth(), rect.source.getY());
			
			
		}
		renderer.end();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		for(int i = 0; i < sprites.size(); i++) {
			TextureRectangle rect = sprites.get(i);
			
			rect.material.getShader().setUniform("View", this.view);
			rect.material.getShader().setUniform("Projection", this.projection);
			rect.material.getShader().setUniform("Model", this.view); //View is always an identity matrix.
			renderer.render(rect.material, (i * 6), 6);			
			
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public void draw(RenderMaterial material, float x, float y, float w, float h) {
		TextureRectangle rect = new TextureRectangle();
		rect.destination = new Rectangle(x,y,w,h);
		rect.source = new Rectangle(0, 0, 1, 1);
		rect.material = material;
		sprites.add(rect);
	}
	
	
}
