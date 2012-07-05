/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.chat.style;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiString;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.chat.style.StyleHandler;

/**
 * StyleHandler that applies styles with Jansi
 */
public class JansiStyleHandler extends StyleHandler {
	public static final JansiStyleHandler INSTANCE = new JansiStyleHandler();
	public static final int ID = register(INSTANCE);
	static {
		AnsiConsole.systemInstall();
	}
	public JansiStyleHandler() {
		super();
		registerFormatter(ChatStyle.RED, new JansiColorFormatter(Ansi.Color.RED, true));
		registerFormatter(ChatStyle.YELLOW, new JansiColorFormatter(Ansi.Color.YELLOW, true));
		registerFormatter(ChatStyle.BRIGHT_GREEN, new JansiColorFormatter(Ansi.Color.GREEN, true));
		registerFormatter(ChatStyle.CYAN, new JansiColorFormatter(Ansi.Color.CYAN, true));
		registerFormatter(ChatStyle.BLUE, new JansiColorFormatter(Ansi.Color.BLUE, true));
		registerFormatter(ChatStyle.PINK, new JansiColorFormatter(Ansi.Color.MAGENTA, true));
		registerFormatter(ChatStyle.BLACK, new JansiColorFormatter(Ansi.Color.BLACK));
		registerFormatter(ChatStyle.DARK_GRAY, new JansiColorFormatter(Ansi.Color.BLACK, true));
		registerFormatter(ChatStyle.DARK_RED, new JansiColorFormatter(Ansi.Color.RED));
		registerFormatter(ChatStyle.GOLD, new JansiColorFormatter(Ansi.Color.YELLOW));
		registerFormatter(ChatStyle.DARK_GREEN, new JansiColorFormatter(Ansi.Color.GREEN));
		registerFormatter(ChatStyle.DARK_CYAN, new JansiColorFormatter(Ansi.Color.CYAN));
		registerFormatter(ChatStyle.DARK_BLUE, new JansiColorFormatter(Ansi.Color.BLUE));
		registerFormatter(ChatStyle.PURPLE, new JansiColorFormatter(Ansi.Color.MAGENTA));
		registerFormatter(ChatStyle.GRAY, new JansiColorFormatter(Ansi.Color.WHITE));
		registerFormatter(ChatStyle.WHITE, new JansiColorFormatter(Ansi.Color.WHITE, true));
		registerFormatter(ChatStyle.ITALIC, new AttributeJansiFormatter(Ansi.Attribute.ITALIC));
		registerFormatter(ChatStyle.UNDERLINE, new AttributeJansiFormatter(Ansi.Attribute.UNDERLINE));
		registerFormatter(ChatStyle.STRIKE_THROUGH, new AttributeJansiFormatter(Ansi.Attribute.STRIKETHROUGH_ON));
		registerFormatter(ChatStyle.BOLD, new BoldJansiFormatter());
		registerFormatter(ChatStyle.RESET, new ResetJansiFormatter());
	}

	public String stripStyle(String formatted) {
		return new AnsiString(formatted).getPlain().toString();
	}
}
