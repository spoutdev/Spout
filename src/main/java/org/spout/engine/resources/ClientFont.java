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
package org.spout.engine.resources;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


public class ClientFont extends ClientTexture implements org.spout.api.render.Font{
	private  static final String asciiset;
	private static final FontRenderContext DEFAULT_CONTEXT = new FontRenderContext(null, true, true);
	
	static {
		asciiset = getASCII();
		
	}
	
	private static final String getASCII(){
		StringBuilder sb = new StringBuilder();
		for(int i = 32; i < 127; i++) sb.append((char)i).append(" ");
		return sb.toString();
	}
	
	public static final String getCharset(){
		return asciiset;
	}
	
	
	
	Font ttfFont;
	
	
	private GlyphVector vec;


	float imageWidth;
	float imageHeight;
	float charTop;
	float charHeight;
	float spaceWidth = 0.0f;

	
	public ClientFont(Font f){
		this.ttfFont = f;
		
		//because getStringBounds(" ") returns 0
		spaceWidth = (float)(ttfFont.getStringBounds("a a", DEFAULT_CONTEXT).getWidth() - ttfFont.getStringBounds("aa", DEFAULT_CONTEXT).getWidth());
		
		
		vec = ttfFont.createGlyphVector(DEFAULT_CONTEXT, asciiset);
		Rectangle2D bounds = ttfFont.getStringBounds(asciiset, DEFAULT_CONTEXT);
		
		//Create the font's bitmaptexture
		BufferedImage image = new BufferedImage((int)bounds.getWidth(), (int)bounds.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.white);
		g.drawGlyphVector(vec, 0, ttfFont.getSize());
		g.dispose();
		
		
		
		charHeight = (float)bounds.getHeight();
		charTop = (float)(ttfFont.getSize() + bounds.getY());
		
		this.image = image;
		
		
	}
	
	
	public float getCharTop() {
		return charTop;
	}

	public float getCharHeight() {
		return charHeight;
	}

	public float getSpaceWidth() {
		return spaceWidth;
	}
	
	public Rectangle getPixelBounds(char c){
		return vec.getGlyphPixelBounds(asciiset.indexOf(c), DEFAULT_CONTEXT, 0, ttfFont.getSize());
	}
	
}
