/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.component.widget;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.gui.render.RenderPartPack;
import org.spout.api.map.DefaultedKey;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.math.Rectangle;
import org.spout.api.render.Font;

/**
 * Represents an element that contains characters.
 */
public class LabelComponent extends WidgetComponent {
	private static final DefaultedKey<String> KEY_TEXT = new DefaultedKeyImpl<String>("text", "(your text here)");
	private static final DefaultedKey<Color> KEY_COLOR = new DefaultedKeyImpl<Color>("text-color", Color.black);
	private static final DefaultedKey<Font> KEY_FONT = new DefaultedKeyImpl<Font>("font", (Font) Spout.getFileSystem().getResource("font://Spout/fonts/ubuntu/Ubuntu-M.ttf"));

	@Override
	public List<RenderPartPack> getRenderPartPacks() {
		List<RenderPartPack> ret = new LinkedList<RenderPartPack>();

		if (getFont() == null) {
			return ret;
		}

		Color color = getColor();
		Font font = getFont();
		RenderPartPack textPack = new RenderPartPack(font.getMaterial());

		float w = font.getWidth();
		float h = font.getHeight();

		float xCursor = 0;
		float yCursor = 0;

		float screenWidth = ((Client) Spout.getEngine()).getResolution().getX();
		float screenHeight = ((Client) Spout.getEngine()).getResolution().getY();

		for (char c : getText().toCharArray()) {
			if (!font.isValidChar(c)) continue; // invalid char for the font
			if (c == ' ') {
				xCursor += font.getSpaceWidth() / screenWidth;
			} else if (c == '\n') {
				xCursor = 0;
				yCursor -= font.getCharHeight() / screenHeight;
			} else {
				java.awt.Rectangle r = font.getPixelBounds(c);

				RenderPart part = new RenderPart();
				part.setColor(color);
				part.setSprite(new Rectangle(xCursor, yCursor, (float) r.width / screenWidth, h / screenHeight));
				part.setSource(new Rectangle(r.x / w, 0f, r.width / w, 1f));
				part.setZIndex(0);

				xCursor += (float) font.getAdvance(c) / screenWidth;

				textPack.add(part);
			}
		}

		ret.add(textPack);
		return ret;
	}

	/**
	 * Sets the font of the label.
	 * @param font of label
	 */
	public void setFont(Font font) {
		getDatatable().put(KEY_FONT, font);
		getOwner().update();
	}

	/**
	 * Returns the font of the label
	 * @return font of label
	 */
	public Font getFont() {
		return getDatatable().get(KEY_FONT);
	}

	/**
	 * Returns the text on the label.
	 * @return text on label
	 */
	public String getText() {
		return getDatatable().get(KEY_TEXT);
	}

	/**
	 * Sets the text on the label
	 * @param text on label
	 */
	public void setText(String text) {
		getDatatable().put(KEY_TEXT, text);
		getOwner().update();
	}

	/**
	 * Clears all text on the label.
	 */
	public void clear() {
		setText("");
	}

	/**
	 * Removes the last character.
	 */
	public void backspace() {
		String str = getText();
		if (!str.isEmpty()) {
			setText(str.substring(0, str.length() - 1));
		}
	}

	/**
	 * Adds a character.
	 * @param c character to add
	 */
	public void append(char c) {
		setText(getText() + c);
	}

	/**
	 * Adds a string to the end of this label.
	 * @param str string to add
	 */
	public void append(String str) {
		setText(getText() + str);
	}

	/**
	 * Creates a new line on this label.
	 */
	public void newLine() {
		append('\n');
	}

	/**
	 * Returns the color of the text.
	 * @return text of color
	 */
	public Color getColor() {
		return getDatatable().get(KEY_COLOR);
	}

	/**
	 * Sets the color of the text
	 * @param color of text
	 */
	public void setColor(Color color) {
		getDatatable().put(KEY_COLOR, color);
		getOwner().update();
	}

	/**
	 * Whether the specified char can be added to the label with the current
	 * font.
	 * @param c char to check
	 * @return true if char can be added to label
	 */
	public boolean isValidChar(char c) {
		try {
			getFont().getPixelBounds(c);
			return true;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
}
