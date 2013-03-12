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

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class FormattedLogRecord extends LogRecord {
	private static final long serialVersionUID = 1L;
	/**
	 * Construct a LogRecord with the given level and message values.
	 * <p/>
	 * The sequence property will be initialized with a new unique value.
	 * These sequence values are allocated in increasing order within a VM.
	 * <p/>
	 * The millis property will be initialized to the current time.
	 * <p/>
	 * The thread ID property will be initialized with a unique ID for
	 * the current thread.
	 * <p/>
	 * All other properties will be initialized to "null".
	 * @param level a logging level value
	 * @param msg the raw non-localized logging message (may be null)
	 */
	private final ChatArguments arguments;

	public FormattedLogRecord(Level level, ChatArguments arguments) {
		super(level, arguments.getPlainString());
		this.arguments = arguments;
	}

	public FormattedLogRecord(Level level, String msg) {
		this(level, new ChatArguments(msg));
	}

	@Override
	public String getMessage() {
		return arguments.asString();
	}

	public ChatArguments getFormattedMessage() {
		return arguments;
	}
}
