package org.spout.engine.batcher;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.lwjgl.opengl.GL11;

import org.spout.api.render.Font;
import org.spout.api.render.Renderer;
import org.spout.api.render.Texture;

import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.resources.ClientFont;
import org.spout.engine.resources.ClientRenderMaterial;

public class GuiRenderer {
	static Renderer renderer;
	static ClientRenderMaterial guiMaterial;
	static ClientRenderMaterial textMaterial;

	public static void init() {
		renderer = BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
	}

	private static void TexturedQuad(float x, float y, float width, float height) {

	}

	public static void renderString(String s, float x, float y, Font font, Color color) {

		renderer.begin(null);
		float lx = x;
		float height = font.getCharHeight();
		for (char c : s.toCharArray()) {
			if (c == ' ') {
				lx += (font.getSpaceWidth());
				lx -= (height * 0.03);
				continue;
			}
			Rectangle bounds = font.getPixelBounds(c);
			float width = (float) bounds.getWidth();
		}
	}
}
