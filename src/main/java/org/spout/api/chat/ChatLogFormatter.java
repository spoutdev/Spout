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
package org.spout.api.chat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import org.spout.api.chat.style.ChatStyle;
import org.spout.api.chat.style.fallback.DefaultStyleHandler;

public class ChatLogFormatter extends SimpleFormatter  {
	private static final Placeholder LEVEL = new Placeholder("level"), MESSAGE = new Placeholder("message");
	private static final ChatTemplate LOG_TEMPLATE = new ChatTemplate(new ChatArguments("[", LEVEL, "] ", MESSAGE));
	private final int handlerId;
	public ChatLogFormatter() {
		this(DefaultStyleHandler.ID);
	}

	public ChatLogFormatter(int handlerId) {
		this.handlerId = handlerId;
	}

	public ChatArguments colorizeLevel(Level level) {
		ChatStyle color;
		if (level.intValue() >= Level.SEVERE.intValue()) {
			color = ChatStyle.RED;
		} else if (level.intValue() >= Level.WARNING.intValue()) {
			color = ChatStyle.YELLOW;
		} else if (level.intValue() >= Level.INFO.intValue()) {
			color = ChatStyle.DARK_GREEN;
		} else {
			color = ChatStyle.GRAY;
		}
		return new ChatArguments(color, level, ChatStyle.RESET);
	}

	@Override
	public String format(LogRecord record) {
		ChatArguments args = LOG_TEMPLATE.getArguments();
		ChatArguments level = colorizeLevel(record.getLevel());
		args.setPlaceHolder(LEVEL, level);
		if (record instanceof FormattedLogRecord) {
			args.setPlaceHolder(MESSAGE, ((FormattedLogRecord) record).getFormattedMessage());
		} else {
			args.setPlaceHolder(MESSAGE, new ChatArguments(super.formatMessage(record) + '\n'));
		}
		
		if (record.getThrown() != null) {
			StringWriter writer = new StringWriter();
			record.getThrown().printStackTrace(new PrintWriter(writer));
			String[] lines = writer.getBuffer().toString().split("\n");
			for (String line : lines) {
				args.append(LOG_TEMPLATE.getArguments().setPlaceHolder(LEVEL, level).setPlaceHolder(MESSAGE, new ChatArguments(line)).asString(handlerId));
				args.append('\n');
			}
		}
		args.append(ChatStyle.RESET);
		return args.asString(handlerId);
	}
}
