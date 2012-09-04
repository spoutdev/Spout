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
import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;

import org.spout.api.render.Font;
import org.spout.api.render.Renderer;
import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.resources.ClientRenderMaterial;

public class GuiRenderer {
	static Renderer renderer;
	static ClientRenderMaterial guiMaterial;
	static ClientRenderMaterial textMaterial;
	
	
	public static void init(){
		renderer = BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
		
	}

	private static void TexturedQuad(float x, float y, float width, float height){
		
	}
	
	
	public static void renderString(String s, float x, float y, Font font, Color color){
		
		
		renderer.begin();
		float lx = x;
		float height = font.getCharHeight();
		for(char c : s.toCharArray()){
			if(c == ' '){
				lx += (font.getSpaceWidth());
				lx -= (height * 0.03);
				continue;
			}
			Rectangle bounds = font.getPixelBounds(c);
			float width = (float)bounds.getWidth();
			
			
			
			
			
			
		}
		
		
	}
	

}
