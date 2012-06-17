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
package org.spout.api;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A style of chat for the client to implement.
 * FontRenderer. Names are from <a href="http://wiki.vg/Chat">http://wiki.vg/Chat</a>
 */
public class ChatStyle {
	public static final ChatStyle BLACK = new ChatStyle("Black", '0', 0x0);
	public static final ChatStyle DARK_BLUE = new ChatStyle("Dark Blue", '1', 0x1);
	public static final ChatStyle DARK_GREEN = new ChatStyle("Dark Green", '2', 0x2);
	public static final ChatStyle DARK_CYAN = new ChatStyle("Dark Cyan", '3', 0x3);
	public static final ChatStyle DARK_RED = new ChatStyle("Dark Red", '4', 0x4);
	public static final ChatStyle PURPLE = new ChatStyle("Purple", '5', 0x5);
	public static final ChatStyle GOLD = new ChatStyle("Gold", '6', 0x6);
	public static final ChatStyle GRAY = new ChatStyle("Gray", '7', 0x7);
	public static final ChatStyle DARK_GRAY = new ChatStyle("Dark Gray", '8', 0x8);
	public static final ChatStyle BLUE = new ChatStyle("Blue", '9', 0x9);
	public static final ChatStyle BRIGHT_GREEN = new ChatStyle("Bright Green", 'a', 0xA);
	public static final ChatStyle CYAN = new ChatStyle("Cyan", 'b', 0xB);
	public static final ChatStyle RED = new ChatStyle("Red", 'c', 0xC);
	public static final ChatStyle PINK = new ChatStyle("Pink", 'd', 0xD);
	public static final ChatStyle YELLOW = new ChatStyle("Yellow", 'e', 0xE);
	public static final ChatStyle WHITE = new ChatStyle("White", 'f', 0xF);
	public static final ChatStyle RANDOM = new ChatStyle("Random", 'k', 0x10);
	public static final ChatStyle BOLD = new ChatStyle("Bold", 'l', 0x11);
	public static final ChatStyle STRIKE_THROUGH = new ChatStyle("Strike Through", 'm', 0x12);
	public static final ChatStyle UNDERLINE = new ChatStyle("Underline", 'n', 0x13);
	public static final ChatStyle ITALIC = new ChatStyle("Italic", 'o', 0x14);
	public static final ChatStyle PLAIN_WHITE = new ChatStyle("Plain White", 'r', 0x15);

	private static final Map<Character, ChatStyle> charLookup = new HashMap<Character, ChatStyle>();
	private static final Map<Integer, ChatStyle> codeLookup = new HashMap<Integer, ChatStyle>();
	private static final Set<ChatStyle> values = new HashSet<ChatStyle>();

	static {
		for (Field field : ChatStyle.class.getFields()) {
			field.setAccessible(true);
			if (Modifier.isStatic(field.getModifiers()) && ChatStyle.class.isAssignableFrom(field.getType())) {
				try {
					ChatStyle style = (ChatStyle) field.get(null);
					values.add(style);
					charLookup.put(style.getChar(), style);
					codeLookup.put(style.getCode(), style);
				} catch (IllegalAccessException ignored) {
				}
			}
		}
	}

	public static ChatStyle get(int code) {
		return codeLookup.get(code);
	}

	public static ChatStyle get(char c) {
		return charLookup.get(c);
	}

	public static Set<ChatStyle> getValues() {
		return values;
	}

	public static String strip(String str) {
		return str.replaceAll("\\u00A7[0-9a-fA-FkKl-oL-OrR]", "");
	}

	private final String name;
	private final char c;
	private final int code;

	public ChatStyle(String name, char c, int code) {
		this.name = name;
		this.c = c;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public int getCode() {
		return code;
	}

	public char getChar() {
		return c;
	}

	@Override
	public String toString() {
		return "\u00A7" + c;
	}
}
