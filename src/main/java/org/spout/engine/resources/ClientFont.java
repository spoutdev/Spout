package org.spout.engine.resources;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class ClientFont extends ClientTexture implements org.spout.api.render.Font {
	private static final String asciiset;
	private static final FontRenderContext DEFAULT_CONTEXT = new FontRenderContext(null, true, true);

	static {
		asciiset = getASCII();
	}

	private static final String getASCII() {
		StringBuilder sb = new StringBuilder();
		for (int i = 32; i < 127; i++) {
			sb.append((char) i).append(" ");
		}
		return sb.toString();
	}

	public static final String getCharset() {
		return asciiset;
	}

	Font ttfFont;
	private GlyphVector vec;
	float imageWidth;
	float imageHeight;
	float charTop;
	float charHeight;
	float spaceWidth = 0.0f;

	public ClientFont(Font f) {
		this.ttfFont = f;

		//because getStringBounds(" ") returns 0
		spaceWidth = (float) (ttfFont.getStringBounds("a a", DEFAULT_CONTEXT).getWidth() - ttfFont.getStringBounds("aa", DEFAULT_CONTEXT).getWidth());

		vec = ttfFont.createGlyphVector(DEFAULT_CONTEXT, asciiset);
		Rectangle2D bounds = ttfFont.getStringBounds(asciiset, DEFAULT_CONTEXT);

		//Create the font's bitmaptexture
		BufferedImage image = new BufferedImage((int) bounds.getWidth(), (int) bounds.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.white);
		g.drawGlyphVector(vec, 0, ttfFont.getSize());
		g.dispose();

		charHeight = (float) bounds.getHeight();
		charTop = (float) (ttfFont.getSize() + bounds.getY());

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

	public Rectangle getPixelBounds(char c) {
		return vec.getGlyphPixelBounds(asciiset.indexOf(c), DEFAULT_CONTEXT, 0, ttfFont.getSize());
	}
}
