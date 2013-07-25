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
package org.spout.engine.filesystem.resource;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.spout.api.Spout;
import org.spout.api.render.RenderMaterial;
import org.spout.engine.renderer.shader.ClientShader;

public class ClientFont extends ClientTexture implements org.spout.api.render.Font {
	private static final long serialVersionUID = 1L;
	private static final String asciiset;
	private static final FontRenderContext DEFAULT_CONTEXT = new FontRenderContext(null, true, true);
	private ClientRenderMaterial material;
	private Font ttfFont;
	private GlyphVector vec;
	private Rectangle[] glyphBounds = new Rectangle[95];
	private GlyphMetrics[] glyphMetrics = new GlyphMetrics[95];
	private int charTop;
	private int charHeight;
	private int spaceWidth;

	static {
		asciiset = getASCII();
	}

	private static String getASCII() {
		StringBuilder sb = new StringBuilder();
		for (int i = 32; i < 127; i++) {
			sb.append((char) i).append(" ");
		}
		return sb.toString();
	}

	public static String getCharset() {
		return asciiset;
	}

	private static int indexOf(char c) {
		return (((int) c) - 32);
	}

	public ClientFont(Font f) {
		super(null, 0, 0);
		this.ttfFont = f;
		init();
	}

	private void init() {
		//because getStringBounds(" ") returns 0
		spaceWidth = (int) (ttfFont.getStringBounds("a a", DEFAULT_CONTEXT).getWidth() - (int) ttfFont.getStringBounds("aa", DEFAULT_CONTEXT).getWidth());

		vec = ttfFont.createGlyphVector(DEFAULT_CONTEXT, asciiset);
		Rectangle2D bounds = ttfFont.getStringBounds(asciiset, DEFAULT_CONTEXT);

		for (int i = 0; i < 127 - 32; i++) {
			glyphBounds[i] = vec.getGlyphPixelBounds(i * 2, DEFAULT_CONTEXT, 0, ttfFont.getSize());
			glyphMetrics[i] = vec.getGlyphMetrics(i * 2);
		}

		//Create the font's bitmaptexture
		BufferedImage image = new BufferedImage((int) bounds.getWidth() + 2, (int) bounds.getHeight() + 2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.white);
		g.drawGlyphVector(vec, 0, ttfFont.getSize());
		g.dispose();

		charHeight = (int) bounds.getHeight();
		charTop = (int) (ttfFont.getSize() + bounds.getY());
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.image = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeObject(ttfFont);
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		this.ttfFont = (Font) ois.readObject();
		init();
	}

	@Override
	public void writeGPU() {
		super.writeGPU();
		Map<String, Object> params = new HashMap<>();
		params.put("Diffuse", this);
		material = new ClientRenderMaterial((ClientShader) Spout.getFileSystem().getResource("shader://Spout/shaders/textShader.ssf"), params);
	}

	@Override
	public RenderMaterial getMaterial() {
		/*if (material==null) {
			writeGPU();
		}*/
		//Shader is compilation is queue in render, so shader must be compile before that
		return material;
	}

	@Override
	public int getCharTop() {
		return charTop;
	}

	@Override
	public int getCharHeight() {
		return charHeight;
	}

	@Override
	public int getSpaceWidth() {
		return spaceWidth;
	}

	public float getBearingX(char c) {
		return glyphMetrics[indexOf(c)].getLSB();
	}

	public float getBearingY(char c) {
		AffineTransform ts = vec.getGlyphTransform(asciiset.indexOf(c));
		if (ts == null) {
			return 0;
		}
		return (float) ts.getTranslateY();
	}

	@Override
	public float getAdvance(char c) {
		return glyphMetrics[indexOf(c)].getAdvanceX();
	}

	@Override
	public boolean isValidChar(char c) {
		return indexOf(c) < glyphBounds.length;
	}

	@Override
	public Rectangle getPixelBounds(char c) {//
		return glyphBounds[indexOf(c)];
	}
}
