/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
		
		if (hasFlag(options.getAlign(), Align.ALIGN_CENTER)) {
			x = x + (width - x) / 2 + stringWidth / 2;
		}
		if (hasFlag(options.getAlign(), Align.ALIGN_RIGHT)) {
			x = x + width - stringWidth;
		}
		
		if (hasFlag(options.getAlign(), Align.ALIGN_MIDDLE)) {
			y = y + (height - y) / 2 + stringHeight / 2;
		}
		if (hasFlag(options.getAlign(), Align.ALIGN_BOTTOM)) {
			y = y + height - stringHeight;
		}
				
		options.getFont().drawString(x, y, text, getSlickColor(options.getColor()));
	}
	
	private static org.newdawn.slick.Color getSlickColor(Color color) {
		return new org.newdawn.slick.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	private static boolean hasFlag(int value, int flag) {
		return (value & flag) == flag;
	}
}
