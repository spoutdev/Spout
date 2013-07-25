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
package org.spout.api.plugin;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PluginLogger extends Logger {
	private final Plugin plugin;
	private String tag;

	public PluginLogger(Plugin plugin) {
		super(plugin.getClass().getCanonicalName(), null);
		setLevel(Level.ALL);
		setParent(plugin.getEngine().getLogger());
		tag = "[" + plugin.getDescription().getName() + "]";
		this.plugin = plugin;
	}

	@Override
	public void log(LogRecord logRecord) {
		/* TODO: Fix logging
		final FormattedLogRecord record = new FormattedLogRecord(logRecord.getLevel(), new ChatArguments(tag, logRecord.getMessage()));
		record.setLoggerName(logRecord.getLoggerName());
		record.setMillis(logRecord.getMillis());
		record.setParameters(logRecord.getParameters());
		record.setResourceBundle(logRecord.getResourceBundle());
		record.setResourceBundleName(logRecord.getResourceBundleName());
		record.setSequenceNumber(logRecord.getSequenceNumber());
		record.setSourceClassName(logRecord.getSourceClassName());
		record.setSourceMethodName(logRecord.getSourceMethodName());
		record.setThreadID(logRecord.getThreadID());
		record.setThrown(logRecord.getThrown());
		super.log(record); */
	}

	/**
	 * Gets the Plugin associated with this Logger
	 *
	 * @return the Plugin
	 */
	public Plugin getPlugin() {
		return this.plugin;
	}

	/**
	 * Sets the tag prefix'd to the plugin's logger.
	 *
	 * @param tag The new tag for the logger
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * Gets the tag prefix'd to the plugin's logger.
	 *
	 * @return The tag
	 */
	public String getTag() {
		return tag;
	}
}
