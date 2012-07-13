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
package org.spout.api.chat.style;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.fallback.DefaultStyleHandler;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.util.StringMap;

/**
 * A style of chat for the client to implement.<br/>
 * FontRenderer. Names are from <a href="http://wiki.vg/Chat">http://wiki.vg/Chat</a>
 */
public abstract class ChatStyle {
	private static final StringMap ID_LOOKUP = new StringMap(null, new MemoryStore<Integer>(), 0, Integer.MAX_VALUE, ChatStyle.class.getName());
	private static final Map<String, ChatStyle> BY_NAME = new HashMap<String, ChatStyle>();
	private static final Set<ChatStyle> VALUES = new HashSet<ChatStyle>();

	public static final ChatStyle BLACK = new ColorChatStyle("Black");
	public static final ChatStyle DARK_BLUE = new ColorChatStyle("Dark Blue");
	public static final ChatStyle DARK_GREEN = new ColorChatStyle("Dark Green");
	public static final ChatStyle DARK_CYAN = new ColorChatStyle("Dark Cyan");
	public static final ChatStyle DARK_RED = new ColorChatStyle("Dark Red");
	public static final ChatStyle PURPLE = new ColorChatStyle("Purple");
	public static final ChatStyle GOLD = new ColorChatStyle("Gold");
	public static final ChatStyle GRAY = new ColorChatStyle("Gray");
	public static final ChatStyle DARK_GRAY = new ColorChatStyle("Dark Gray");
	public static final ChatStyle BLUE = new ColorChatStyle("Blue");
	public static final ChatStyle BRIGHT_GREEN = new ColorChatStyle("Bright Green");
	public static final ChatStyle CYAN = new ColorChatStyle("Cyan");
	public static final ChatStyle RED = new ColorChatStyle("Red");
	public static final ChatStyle PINK = new ColorChatStyle("Pink");
	public static final ChatStyle YELLOW = new ColorChatStyle("Yellow");
	public static final ChatStyle WHITE = new ColorChatStyle("White");
	public static final ChatStyle CONCEAL = new FormatChatStyle("Conceal");
	public static final ChatStyle BOLD = new FormatChatStyle("Bold");
	public static final ChatStyle STRIKE_THROUGH = new FormatChatStyle("Strikethrough");
	public static final ChatStyle UNDERLINE = new FormatChatStyle("Underline");
	public static final ChatStyle ITALIC = new FormatChatStyle("Italic");
	public static final ChatStyle RESET = new ResetChatStyle();

	public static Set<ChatStyle> getValues() {
		return VALUES;
	}

	/**
	 * Looks up a ChatStyle by it's ID.
	 *
	 * @param id
	 * @return the ChatStyle, or null if not found.
	 */
	public static ChatStyle byId(int id) {
		return byName(ID_LOOKUP.getString(id));
	}

	/**
	 * Looks up a ChatStyle by it's name.
	 *
	 * @param name
	 * @return the ChatStyle, or null if not found.
	 */
	public static ChatStyle byName(String name) {
		if (name == null) {
			return null;
		}
		return BY_NAME.get(name.toLowerCase().replace(' ', '_'));
	}

	/**
	 * Removes all ChatStyle's from the given string.
	 *
	 * @param str to strip.
	 * @return String with all ChatSyle's removed.
	 */
	public static String strip(String str) {
		for (StyleHandler handler : StyleHandler.getAll()) {
			str = handler.stripStyle(str);
		}
		return str;
	}

	public static String strip(int handlerId, String str) {
		return StyleHandler.get(handlerId).stripStyle(str);
	}

	private final String name;
	private final String lookupName;
	private final int id;

	public ChatStyle(String name) {
		this.name = name;
		this.lookupName = name.toLowerCase().replace(' ', '_');
		VALUES.add(this);
		BY_NAME.put(lookupName, this);
		id = ID_LOOKUP.register(lookupName);
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public abstract boolean conflictsWith(ChatStyle other);
}
