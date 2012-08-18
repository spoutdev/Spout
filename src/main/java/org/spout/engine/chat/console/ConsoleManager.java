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
package org.spout.engine.chat.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.spout.api.chat.ChatArguments;
import org.spout.api.Engine;
import org.spout.api.chat.ChatTemplate;
import org.spout.api.chat.console.Console;
import org.spout.api.chat.Placeholder;
import org.spout.api.chat.style.ChatStyle;
import org.spout.engine.SpoutEngine;
/**
 * A meta-class to handle all logging and input-related console improvements.
 */
public final class ConsoleManager {
	private final Engine engine;
	private final ConsoleCommandSource source;
	private SpoutHandler handler;

	public ConsoleManager(SpoutEngine engine) {
		this.engine = engine;
		source = new ConsoleCommandSource(engine);

		Runtime.getRuntime().addShutdownHook(new ServerShutdownThread(engine));
	}

	public ConsoleCommandSource getCommandSource() {
		return source;
	}

	public void stop() {
		handler.close();
	}

	public void setupConsole(Console console) {
		handler = new SpoutHandler(console);

		Logger logger = Logger.getLogger("");
		for (Handler h : logger.getHandlers()) {
			logger.removeHandler(h);
		}
		console.init();
		logger.addHandler(handler);
		System.setOut(new PrintStream(new LoggerOutputStream(Level.INFO), true));
		System.setErr(new PrintStream(new LoggerOutputStream(Level.INFO), true));
	}

	private static class ServerShutdownThread extends Thread {
		private static final AtomicInteger COUNT = new AtomicInteger(1);
		private final SpoutEngine engine;
		public ServerShutdownThread(SpoutEngine engine) {
			super("ServerShutdownThread-" + COUNT.getAndIncrement());
			this.engine = engine;
		}

		@Override
		public void run() {
			engine.stop();
		}
	}

	private class LoggerOutputStream extends ByteArrayOutputStream {
		private final String separator = System.getProperty("line.separator");
		private final Level level;

		public LoggerOutputStream(Level level) {
			super();
			this.level = level;
		}

		@Override
		public synchronized void flush() throws IOException {
			super.flush();
			String record = this.toString();
			super.reset();

			if (record.length() > 0 && !record.equals(separator)) {
				engine.getLogger().logp(level, "LoggerOutputStream", "log" + level, record);
			}
		}
	}

	private static class SpoutHandler extends Handler {
		private static final Placeholder LEVEL = new Placeholder("level"), MESSAGE = new Placeholder("message");
		private static final ChatTemplate LOG_TEMPLATE = new ChatTemplate(new ChatArguments("[", LEVEL, "] ", MESSAGE));

		private final Console console;

		public SpoutHandler(Console console) {
			this.console = console;
			setFormatter(new SimpleFormatter());
		}

		public void publish(LogRecord record) {
			ChatArguments args = LOG_TEMPLATE.getArguments();
			ChatArguments level = colorizeLevel(record.getLevel());
			args.setPlaceHolder(LEVEL, level);
			args.setPlaceHolder(MESSAGE, new ChatArguments(getFormatter().formatMessage(record)));
			console.addMessage(args);

			if (record.getThrown() != null) {
				StringWriter writer = new StringWriter();
				record.getThrown().printStackTrace(new PrintWriter(writer));
				String[] lines = writer.getBuffer().toString().split("\n");
				for (String line : lines) {
					console.addMessage(LOG_TEMPLATE.getArguments().setPlaceHolder(LEVEL, level).setPlaceHolder(MESSAGE, new ChatArguments(line)));
				}
			}
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

		public void flush() {
		}

		public void close() {
			console.close();
		}
	}
}
