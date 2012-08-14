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

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleOperations;
import jline.ConsoleReader;
import jline.NullCompletor;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.spout.api.chat.ChatArguments;
import org.spout.engine.SpoutEngine;
import org.spout.engine.chat.style.JansiStyleHandler;

/**
 * A console backed by JLine
 */
public class JLineConsole extends AbstractConsole {
	private final SpoutEngine engine;
	private final ConsoleReader reader;
	private final OutputStreamWriter writer;

	public JLineConsole(SpoutEngine engine) {
		this.engine = engine;
		setDateFormat(new SimpleDateFormat("E HH:mm:ss"));

		try {
			reader = new ConsoleReader();
			writer = new OutputStreamWriter(AnsiConsole.out);
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}

		@SuppressWarnings("unchecked")
		final Collection<Completor> completors = reader.getCompletors();
		for (Completor c : new ArrayList<Completor>(completors)) {
			reader.removeCompletor(c);
		}
		Completor[] list = new Completor[] {new SpoutCommandCompletor(engine), new NullCompletor()};
		reader.addCompletor(new ArgumentCompletor(list));
	}

	private String stringify(ChatArguments message) {
		if (Ansi.isEnabled()) {
			return Ansi.ansi().a(message.asString(JansiStyleHandler.ID)).reset().toString();
		} else {
			return message.asString();
		}
	}

	protected void initImpl() {
		ConsoleCommandThread commandThread = new ConsoleCommandThread();
		commandThread.setDaemon(true);
		commandThread.start();
	}

	protected void closeImpl() {
		try {
			reader.killLine();
			reader.flushConsole();
		} catch (IOException e) {
		}
	}

	public void addMessage(ChatArguments message) {
		try {
			synchronized (writer) {
				reader.printString(ConsoleOperations.RESET_LINE + "");
				reader.flushConsole();
				appendDateFormat(writer);
				for (String line : stringify(message).split("\n")) {
					if (line.trim().length() > 0) {
						writer.write(line.replaceAll("[\r\n]", "") + '\n');
					}
				}
				writer.flush();

				try {
					reader.drawLine();
				} catch (Throwable ex) {
					reader.getCursorBuffer().clearBuffer();
				}
				reader.flushConsole();
			}
		} catch (IOException e) {
			engine.getLogger().severe("I/O exception flushing console output");
			e.printStackTrace();
		}
	}

	private class ConsoleCommandThread extends Thread {

		public ConsoleCommandThread() {
			super("ConsoleCommandThread");
		}

		@Override
		public void run() {
			String command;
			while (isInitialized()) {
				try {
					command = reader.readLine(">", null);

					if (command == null || command.trim().length() == 0) {
						continue;
					}

					engine.getScheduler().scheduleSyncDelayedTask(null, new CommandTask(command.trim()));
				} catch (Exception ex) {
					engine.getLogger().severe("Exception that shouldn't happen while executing command: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	private class CommandTask implements Runnable {
		private final String command;
		private final ChatArguments arguments;

		public CommandTask(String commandLine) {
			int spaceIndex = commandLine.indexOf(" ");
			if (spaceIndex != -1) {
				command = commandLine.substring(0, spaceIndex);
				arguments = new ChatArguments(commandLine.substring(spaceIndex + 1));
			} else {
				command = commandLine;
				arguments = new ChatArguments();
			}
		}

		public CommandTask(String command, ChatArguments arguments) {
			this.command = command;
			this.arguments = arguments;
		}

		@Override
		public void run() {
			engine.getCommandSource().sendCommand(command, arguments);
		}
	}
}
