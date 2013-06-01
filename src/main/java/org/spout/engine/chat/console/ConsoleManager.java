/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine.chat.console;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import jline.Completor;
import org.spout.api.Engine;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.ChatLogFormatter;
import org.spout.engine.SpoutEngine;
import org.spout.engine.chat.style.JansiStyleHandler;
import org.spout.logging.LoggerOutputStream;
import org.spout.logging.file.RotatingFileHandler;
import org.spout.logging.jline.CommandCallback;
import org.spout.logging.jline.JLineHandler;

/**
 * A meta-class to handle all logging and input-related console improvements.
 */
public final class ConsoleManager {
	private final Engine engine;
	private final ConsoleCommandSource source;

	public ConsoleManager(SpoutEngine engine) {
		this.engine = engine;
		source = new ConsoleCommandSource(engine);

		Runtime.getRuntime().addShutdownHook(new ServerShutdownThread(engine));
	}

	public ConsoleCommandSource getCommandSource() {
		return source;
	}

	public void setupConsole() {
		Logger logger = Logger.getLogger("");
		for (Handler h : logger.getHandlers()) {
			logger.removeHandler(h);
		}

		Handler jLineHandler = new JLineHandler(new CommandTask(), Arrays.asList(new Completor[]{new SpoutCommandCompletor(engine)}));
		jLineHandler.setFormatter(new ChatLogFormatter(JansiStyleHandler.ID));
		logger.addHandler(jLineHandler);

		Handler fileHandler = new RotatingFileHandler(new File("logs"), engine.getLogFile(), engine.debugMode());
		fileHandler.setFormatter(new ChatLogFormatter());
		logger.addHandler(fileHandler);

		System.setOut(new PrintStream(new LoggerOutputStream(logger, Level.INFO), true));
		System.setErr(new PrintStream(new LoggerOutputStream(logger, Level.SEVERE), true));
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

	private class CommandTask implements Runnable, CommandCallback {
		private final String command;
		private final ChatArguments arguments;

		public CommandTask() {
			command = null;
			arguments = null;
		}

		public CommandTask(String commandLine) {
			int spaceIndex = commandLine.indexOf(' ');
			if (spaceIndex != -1) {
				command = commandLine.substring(0, spaceIndex);
				arguments = new ChatArguments(commandLine.substring(spaceIndex + 1));
			} else {
				command = commandLine;
				arguments = new ChatArguments();
			}
		}

		@Override
		public void run() {
			engine.getCommandSource().sendCommand(command, arguments);
		}

		@Override
		public void handleCommand(String command) {
			engine.getScheduler().scheduleSyncDelayedTask(null, new CommandTask(command.trim()));
		}
	}
}
