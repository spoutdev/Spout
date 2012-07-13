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
package org.spout.api.command.annotated;

import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.exception.CommandException;
import org.spout.api.plugin.Platform;

/**
 * An example of the modified commands setup
 */
public class ExampleCommands {
	static {
		// how to register - don't put this in a static init block like I have here
		Engine engine = Spout.getEngine();
		AnnotatedCommandRegistrationFactory regFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(engine));
		engine.getRootCommand().addSubCommands(engine, ExampleCommands.class, regFactory);
	}

	private final Engine engine;

	public ExampleCommands(Engine engine) {
		this.engine = engine;
	}

	@Command(aliases = "kick", usage = "<name> [reason...]", desc = "Kick a player", min = 1, max = -1)
	private class KickCommand {
		@Executor(Platform.SERVER)
		public void handleServer(CommandSource source, CommandContext args) throws CommandException {
			source.sendMessage(ChatStyle.BLUE, "This is being executed on the server.");
		}

		@Executor(Platform.CLIENT)
		public void handleClient(CommandSource source, CommandContext args) throws CommandException {
			source.sendMessage(ChatStyle.BLUE, "This is being executed on the client.");
		}
	}

	@Command(aliases = "echo", usage = "<message...>", desc = "Echo a message", min = 1, max = -1)
	private class EchoCommand {
		@Executor(Platform.SERVER)
		public void handleServer(CommandSource source, CommandContext args) throws CommandException {
			source.sendMessage(ChatStyle.RED, "This is being executed on the server.");
			source.sendMessage(args.getJoinedString(0));
		}

		@Executor(Platform.CLIENT)
		public void handleClient(CommandSource source, CommandContext args) throws CommandException {
			source.sendMessage(ChatStyle.RED, "This is being executed on the client.");
			source.sendMessage(args.getJoinedString(0));
		}
	}

	@Command(aliases = "children", desc = "A command with children")
	private class ChildrenCommand {
		@Command(aliases = "first", desc = "The first child command", min = 0)
		private class FirstCommand {
			@Executor
			public void handle(CommandSource source, CommandContext args) {
				source.sendMessage("This is the first subcommand");
			}
		}

		@Command(aliases = "second", desc = "The second child command", min = 0)
		private class SecondCommand {
			@Executor
			public void handle(CommandSource source, CommandContext args) {
				source.sendMessage("This is the second subcommand");
			}
		}
	}
}
