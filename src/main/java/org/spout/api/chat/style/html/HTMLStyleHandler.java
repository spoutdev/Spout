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
package org.spout.api.chat.style.html;

import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.chat.style.StyleHandler;

/**
 * A StyleHandler that formats ChatStyles with HTML
 */
public class HTMLStyleHandler extends StyleHandler {
	public static final HTMLStyleHandler INSTANCE = new HTMLStyleHandler();
	public static final int ID = register(INSTANCE);

	public HTMLStyleHandler() {
		registerFormatter(ChatStyle.BLACK, new ColorHTMLStyleFormatter("black"));
		registerFormatter(ChatStyle.DARK_BLUE, new ColorHTMLStyleFormatter("navy"));
		registerFormatter(ChatStyle.DARK_GREEN, new ColorHTMLStyleFormatter("green"));
		registerFormatter(ChatStyle.DARK_CYAN, new ColorHTMLStyleFormatter("teal"));
		registerFormatter(ChatStyle.DARK_RED, new ColorHTMLStyleFormatter("maroon"));
		registerFormatter(ChatStyle.PURPLE, new ColorHTMLStyleFormatter("purple"));
		registerFormatter(ChatStyle.GOLD, new ColorHTMLStyleFormatter("olive"));
		registerFormatter(ChatStyle.GRAY, new ColorHTMLStyleFormatter("silver"));
		registerFormatter(ChatStyle.DARK_GRAY, new ColorHTMLStyleFormatter("gray"));
		registerFormatter(ChatStyle.BLUE, new ColorHTMLStyleFormatter("blue"));
		registerFormatter(ChatStyle.BRIGHT_GREEN, new ColorHTMLStyleFormatter("lime"));
		registerFormatter(ChatStyle.CYAN, new ColorHTMLStyleFormatter("aqua"));
		registerFormatter(ChatStyle.RED, new ColorHTMLStyleFormatter("red"));
		registerFormatter(ChatStyle.PINK, new ColorHTMLStyleFormatter("fuchsia"));
		registerFormatter(ChatStyle.YELLOW, new ColorHTMLStyleFormatter("yellow"));
		registerFormatter(ChatStyle.WHITE, new ColorHTMLStyleFormatter("white"));
		registerFormatter(ChatStyle.CONCEAL, new HTMLStyleFormatter("u"));
		registerFormatter(ChatStyle.BOLD, new HTMLStyleFormatter("span", "style", "font-weight: bold;"));
		registerFormatter(ChatStyle.STRIKE_THROUGH, new HTMLStyleFormatter("span", "style", "text-decoration: line-through;"));
		registerFormatter(ChatStyle.UNDERLINE, new HTMLStyleFormatter("span", "style", "text-decoration: underline;"));
		registerFormatter(ChatStyle.ITALIC, new HTMLStyleFormatter("span", "style", "font-style: italic;"));
	}

	public String stripStyle(String formatted) {
		return formatted.replaceAll("<[^>+]>", "");
	}

	public String escapeString(String unformatted) {
		return unformatted.replace("&", "&amp;").replace("<", "&lt;");
	}

	public ChatArguments extractArguments(String str) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
