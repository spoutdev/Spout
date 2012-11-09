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

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.util.SpoutToStringStyle;
import org.spout.api.util.StringMap;

/**
 * A style of chat for the client to implement.<br/>
 * FontRenderer. Names are from <a href="http://wiki.vg/Chat">http://wiki.vg/Chat</a>
 */
public abstract class ChatStyle implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final StringMap ID_LOOKUP = new StringMap(null, new MemoryStore<Integer>(), 0, Integer.MAX_VALUE, ChatStyle.class.getCanonicalName());
	private static final Map<String, ChatStyle> BY_NAME = new HashMap<String, ChatStyle>();
	private static final Set<ChatStyle> VALUES = new HashSet<ChatStyle>();

	public static final ChatStyle BLACK = new ColorChatStyle("Black", Color.BLACK);
	public static final ChatStyle DARK_BLUE = new ColorChatStyle("Dark Blue", Color.BLUE.darker().darker());
	public static final ChatStyle DARK_GREEN = new ColorChatStyle("Dark Green", Color.GREEN.darker().darker());
	public static final ChatStyle DARK_CYAN = new ColorChatStyle("Dark Cyan", Color.CYAN.darker().darker());
	public static final ChatStyle DARK_RED = new ColorChatStyle("Dark Red", Color.RED.darker().darker());
	public static final ChatStyle PURPLE = new ColorChatStyle("Purple", Color.MAGENTA.darker());
	public static final ChatStyle GOLD = new ColorChatStyle("Gold", Color.YELLOW.darker());
	public static final ChatStyle GRAY = new ColorChatStyle("Gray", Color.GRAY);
	public static final ChatStyle DARK_GRAY = new ColorChatStyle("Dark Gray", Color.DARK_GRAY);
	public static final ChatStyle BLUE = new ColorChatStyle("Blue", Color.BLUE);
	public static final ChatStyle BRIGHT_GREEN = new ColorChatStyle("Bright Green", Color.GREEN);
	public static final ChatStyle CYAN = new ColorChatStyle("Cyan", Color.CYAN);
	public static final ChatStyle RED = new ColorChatStyle("Red", Color.RED);
	public static final ChatStyle PINK = new ColorChatStyle("Pink", Color.MAGENTA);
	public static final ChatStyle YELLOW = new ColorChatStyle("Yellow", Color.YELLOW);
	public static final ChatStyle WHITE = new ColorChatStyle("White", Color.WHITE);
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
	 * Looks up a ChatStyle by its ID.
	 *
	 * @param id
	 * @return the ChatStyle, or null if not found.
	 */
	public static ChatStyle byId(int id) {
		return byName(ID_LOOKUP.getString(id));
	}

	/**
	 * Looks up a ChatStyle by its name.
	 *
	 * @param name
	 * @return the ChatStyle, or null if not found.
	 */
	public static ChatStyle byName(String name) {
		if (name == null) {
			return null;
		}
		return BY_NAME.get(toLookupName(name));
	}

	/**
	 * Removes all ChatStyles from the given string.
	 *
	 * @param str to strip.
	 * @return String with all ChatStyles removed.
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

	private static String toLookupName(String name) {
		return name.toUpperCase().replace(' ', '_');
	}

	private final String name;
	private final String lookupName;
	private final int id;

	public ChatStyle(String name) {
		this.name = name;
		this.lookupName = toLookupName(name);
		VALUES.add(this);
		BY_NAME.put(lookupName, this);
		id = ID_LOOKUP.register(lookupName);
	}

	public String getName() {
		return name;
	}

	/**
	 * Returns the lookup name for this style. This is the name used for the style for lookups.
	 *
	 * @return The lookup name.
	 */
	public String getLookupName() {
		return lookupName;
	}

	public int getId() {
		return id;
	}

	public abstract boolean conflictsWith(ChatStyle other);

	protected void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeInt(id);
	}

	protected void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		int id = ois.readInt();
		String key = ID_LOOKUP.getString(id);
		ChatStyle style = BY_NAME.get(key);
		setField(ChatStyle.class, "id", id);
		setField(ChatStyle.class, "name", style.name);
		setField(ChatStyle.class, "lookupName", style.lookupName);
	
		System.out.println("Reading serialized chat style: " + getName());
	}

	protected void setField(Class<?> clazz, String field, Object value) {
		try {
			Field f = clazz.getDeclaredField(field);
			f.setAccessible(true);
			f.set(this, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("name", name)
				.toString();
	}
}
