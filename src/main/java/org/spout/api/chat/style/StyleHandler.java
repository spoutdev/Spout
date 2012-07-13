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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.fallback.DefaultStyleHandler;

/**
 * A StyleHandler handles converting arrays of objects into correctly formatted strings for display
 */
public abstract class StyleHandler {

	private static final List<StyleHandler> REGISTERED_HANDLERS = new ArrayList<StyleHandler>();

	public static int register(StyleHandler handler) {
		synchronized (REGISTERED_HANDLERS) {
			REGISTERED_HANDLERS.add(handler);
			return REGISTERED_HANDLERS.size() - 1;
		}
	}

	public static StyleHandler get(int index) {
		if (REGISTERED_HANDLERS.size() >= index + 1) {
			return REGISTERED_HANDLERS.get(index);
		}
		return DefaultStyleHandler.INSTANCE;
	}

	public static List<StyleHandler> getAll() {
		return Collections.unmodifiableList(REGISTERED_HANDLERS);
	}

	private final Map<ChatStyle, StyleFormatter> styleFormatters = new HashMap<ChatStyle, StyleFormatter>();

	public StyleFormatter getFormatter(ChatStyle style) {
		StyleFormatter formatter = styleFormatters.get(style);
		if (formatter == null) {
			formatter = getFallbackFormatter(style);
		}
		return formatter;
	}

	protected StyleFormatter getFallbackFormatter(ChatStyle style) {
		return DefaultStyleHandler.INSTANCE.getFormatter(style);
	}

	public List<StyleFormatter> getFormatters() {
		return new ArrayList<StyleFormatter>(styleFormatters.values());
	}

	protected void registerFormatter(ChatStyle style, StyleFormatter formatter) {
		styleFormatters.put(style, formatter);
	}

	/**
	 * Make sure no conflicting characters are present in the unformatted strings being passed through this {@link StyleHandler}
	 *
	 * @param unformatted The unformatted string
	 * @return The escaped unformatted string
	 */
	public String escapeString(String unformatted) {
		return unformatted;
	}

	public abstract String stripStyle(String formatted);

	public abstract ChatArguments extractArguments(String str);
}
