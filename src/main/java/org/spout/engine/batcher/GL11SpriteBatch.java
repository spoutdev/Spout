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

import org.lwjgl.opengl.GL11;

public class GL11SpriteBatch extends SpriteBatch {

	public GL11SpriteBatch(float screenW, float screenH) {
		super(screenW, screenH);
	}
	
	public void render() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		for(int i = 0; i < sprites.size(); i++) {
			TextureRectangle rect = sprites.get(i);
			
			rect.material.getShader().setUniform("View", this.view);
			rect.material.getShader().setUniform("Projection", this.projection);
			rect.material.getShader().setUniform("Model", this.view); //View is always an identity matrix.
			
			rect.material.assign();
			
			GL11.glBegin(GL11.GL_TRIANGLES);
			
			GL11.glTexCoord2f(rect.source.getX(), rect.source.getY());	
			GL11.glVertex3f(rect.destination.getX(), rect.destination.getY() + rect.destination.getHeight(), 0.f);
			
			GL11.glTexCoord2f(rect.source.getX(), rect.source.getY() + rect.source.getHeight());
			GL11.glVertex3f(rect.destination.getX(), rect.destination.getY(), 0.f);	

			GL11.glTexCoord2f(rect.source.getX() + rect.source.getWidth(), rect.source.getY() + rect.source.getHeight());
			GL11.glVertex3f(rect.destination.getX() + rect.destination.getWidth(), rect.destination.getY(), 0.f);
			
			
			GL11.glTexCoord2f(rect.source.getX(), rect.source.getY());	
			GL11.glVertex3f(rect.destination.getX(), rect.destination.getY() + rect.destination.getHeight(), 0.f);		
						
			GL11.glTexCoord2f(rect.source.getX() + rect.source.getWidth(), rect.source.getY() + rect.source.getHeight());
			GL11.glVertex3f(rect.destination.getX() + rect.destination.getWidth(), rect.destination.getY(), 0.f);
			
			GL11.glTexCoord2f(rect.source.getX() + rect.source.getWidth(), rect.source.getY());
			GL11.glVertex3f(rect.destination.getX() + rect.destination.getWidth(), rect.destination.getY() + rect.destination.getHeight(), 0.f);		
			
			GL11.glEnd();
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
}
