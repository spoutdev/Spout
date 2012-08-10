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
package org.spout.api.chat.style.fallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.chat.style.StyleFormatter;
import org.spout.api.chat.style.StyleHandler;

/**
 * An implementation of StyleHandler with basic style handlers that use plain-text to apply some formatting attributes, for places where no better implementation exists.
 */
public class DefaultStyleHandler extends StyleHandler {
	public static final DefaultStyleHandler INSTANCE = new DefaultStyleHandler();
	public static final int ID = register(INSTANCE);

	public DefaultStyleHandler() {
		super();
		registerFormatter(ChatStyle.BOLD, new WrapperStyleFormatter("*", 2));
		registerFormatter(ChatStyle.ITALIC, new WrapperStyleFormatter("/"));
		registerFormatter(ChatStyle.CONCEAL, new ConcealStyleFormatter());
		// The two below don't work for some reason, not sure why. Might have to do with the OS X terminal, so somebody else not on OS X should check.
		/*registerFormatter(ChatStyle.UNDERLINE, new UnderlineStyleFormatter());
		registerFormatter(ChatStyle.STRIKE_THROUGH, new StrikethroughStyleFormatter());*/
	}

	@Override
	protected StyleFormatter getFallbackFormatter(ChatStyle style) {
		return DefaultStyleFormatter.INSTANCE;
	}

	// Since these defaults actually modify the text instead of
	// simply applying formatting characters, not much we can do.
	public String stripStyle(String formatted) {
		return formatted;
	}

	private static final Pattern STYLE_PATTERN = Pattern.compile("(?:\\{\\{([^\\}]+)\\}\\})?([^\\{]*)");
	public ChatArguments extractArguments(String str) {
		ChatArguments args = new ChatArguments();
		Matcher matcher = STYLE_PATTERN.matcher(str);
		while (matcher.find()) {
			if (matcher.group(1) != null) {
				ChatStyle style = ChatStyle.byName(matcher.group(1));
				if (style == null) {
					args.append(matcher.group(0));
					continue;
				}
				args.append(style);
			}
			args.append(matcher.group(2));
		}
		return args;
	}
}
