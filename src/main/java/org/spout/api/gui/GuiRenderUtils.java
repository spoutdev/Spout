package org.spout.api.gui;

import java.awt.Color;
import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;

public class GuiRenderUtils {
	
	/**
	 * sets a java.awt.Color to the GL color buffer
	 * @param color the color to set
	 */
	public static void glColor(Color color) {
		GL11.glColor4b((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) color.getAlpha());
	}
	
	/**
	 * Renders the given text within the given boundaries with the given options
	 * @param text the text to draw
	 * @param options align, color and font
	 * @param bounds the boundaries
	 */
	public static void renderText(String text, TextProperties options, Rectangle bounds) {
		int x = bounds.x;
		int y = bounds.y;
		int width = bounds.width;
		int height = bounds.height;
		int stringWidth = options.getFont().getWidth(text);
		int stringHeight = options.getFont().getHeight(text);
		
		if(hasFlag(options.getAlign(), Align.ALIGN_CENTER)) {
			x = x + (width - x) / 2 + stringWidth / 2;
		}
		if(hasFlag(options.getAlign(), Align.ALIGN_RIGHT)) {
			x = x + width - stringWidth;
		}
		
		if(hasFlag(options.getAlign(), Align.ALIGN_MIDDLE)) {
			y = y + (height - y) / 2 + stringHeight / 2;
		}
		if(hasFlag(options.getAlign(), Align.ALIGN_BOTTOM)) {
			y = y + height - stringHeight;
		}
		
		glColor(options.getColor());
		
		options.getFont().drawString(x, y, text);
	}
	
	private static boolean hasFlag(int value, int flag) {
		return (value & flag) == flag;
	}
}
