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
package org.spout.api.gui.component;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ColorChatStyle;
import org.spout.api.chat.style.ResetChatStyle;
import org.spout.api.component.type.WidgetComponent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.map.DefaultedKey;
import org.spout.api.math.Rectangle;
import org.spout.api.render.Font;

public class LabelComponent extends WidgetComponent {
	private static final DefaultedKey<ChatArguments> KEY_TEXT = new DefaultedKey<ChatArguments>() {
		private final ChatArguments DEFAULT_VALUE = new ChatArguments("(your text here)");

		@Override
		public ChatArguments getDefaultValue() {
			return DEFAULT_VALUE;
		}

		@Override
		public String getKeyString() {
			return "text";
		}
	};
	private static final DefaultedKey<Color> KEY_COLOR = new DefaultedKey<Color>() {
		@Override
		public Color getDefaultValue() {
			return Color.black;
		}

		@Override
		public String getKeyString() {
			return "text-color";
		}
	};
	private static final DefaultedKey<Font> KEY_FONT = new DefaultedKey<Font>() {
		@Override
		public Font getDefaultValue() {
			return (Font) Spout.getFilesystem().getResource("font://Spout/fonts/ubuntu/Ubuntu-M.ttf");
		}

		@Override
		public String getKeyString() {
			return "font";
		}
	};

	@Override
	public List<RenderPart> getRenderParts() {
		List<RenderPart> ret = new LinkedList<RenderPart>();

		if (getFont() == null) {
			return ret;
		}

		Color color = getColor();
		Font font = getFont();

		float w = font.getWidth();
		float h = font.getHeight();

		float xCursor = 0;
		float yCursor = 0;

		float screenWidth = ((Client) Spout.getEngine()).getResolution().getX();
		float screenHeight = ((Client) Spout.getEngine()).getResolution().getY();

		for (Object arg : getText().getArguments()) {
			if (arg instanceof String) {
				String txt = (String) arg;
				for (int i = 0; i < txt.length(); i++) {
					char c = txt.charAt(i);
					if (c == ' ') {
						xCursor += font.getSpaceWidth() / screenWidth;
					} else if (c == '\n') {
						xCursor = 0;
						yCursor -= font.getCharHeight() / screenHeight;
					} else {
						java.awt.Rectangle r = font.getPixelBounds(c);

						RenderPart part = new RenderPart();
						part.setRenderMaterial(font.getMaterial());
						part.setColor(color);
						part.setSprite(new Rectangle(xCursor, yCursor, (float) r.width / screenWidth, h / screenHeight));
						part.setSource(new Rectangle(r.x / w, 0f, r.width / w, 1f));
						part.setZIndex(0);

						xCursor += (float) font.getAdvance(c) / screenWidth;

						ret.add(part);
					}
				}
			} else if (arg instanceof ColorChatStyle) {
				color = ((ColorChatStyle) arg).getColor();
			} else if (arg instanceof ResetChatStyle) {
				color = Color.black;
			}
		}

		return ret;
	}

	public void setFont(Font font) {
		getData().put(KEY_FONT, font);
		getOwner().update();
	}

	public Font getFont() {
		return getData().get(KEY_FONT);
	}

	public ChatArguments getText() {
		return getData().get(KEY_TEXT);
	}

	public void setText(ChatArguments text) {
		getData().put(KEY_TEXT, text);
		getOwner().update();
	}

	public void clear() {
		setText(new ChatArguments(""));
	}

	public void backspace() {
		String str = getText().getPlainString();
		if (!str.isEmpty()) {
			setText(new ChatArguments(str.substring(0, str.length() - 1)));
		}
	}

	public void append(char c) {
		setText(new ChatArguments(getText().append(c)));
	}

	public void append(String str) {
		setText(new ChatArguments(getText().append(str)));
	}

	public void newLine() {
		append('\n');
	}

	public Color getColor() {
		return getData().get(KEY_COLOR);
	}

	public void setColor(Color color) {
		getData().put(KEY_COLOR, color);
		getOwner().update();
	}

	/**
	 * Whether the specified char can be added to the label with the current
	 * font.
	 *
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
