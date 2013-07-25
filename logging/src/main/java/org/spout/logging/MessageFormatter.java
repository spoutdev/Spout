/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MessageFormatter extends Formatter {
	@Override
	public String format(LogRecord record) {
		String message = record.getMessage();
		if (record.getParameters() != null) {
			for (int i = 0; i < record.getParameters().length; i++) {
				message = message.replaceAll(new StringBuilder("\\{").append(i).append("}").toString(), (record.getParameters()[i] == null ? "null" : record.getParameters()[i].toString()));
			}
		}
		if (record.getThrown() != null) {
			StringWriter sink = new StringWriter();
			record.getThrown().printStackTrace(new PrintWriter(sink, true));
			message += "\n\t" + sink.toString();
		}
		return message;
	}
}
