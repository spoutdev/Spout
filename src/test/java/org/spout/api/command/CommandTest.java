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
package org.spout.api.command;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.ExpectedException;
import org.spout.api.command.filter.PlayerFilter;
import org.spout.api.data.ValueHolder;
import org.spout.api.exception.CommandException;
import org.spout.api.geo.World;
import org.spout.api.lang.Locale;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class CommandTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testCommand() throws CommandException {
		CommandManager cm = new CommandManager();
		CommandSource testSource = new TestCommandSource(cm);
		Command cmd = cm.getCommand("test1")
				.addAlias("t1")
				.setPermission("test.1")
				.setExecutor(new Executor() {
					@Override
					public void execute(CommandSource source, Command command, CommandArguments args) throws CommandException {
						args.popString("testArg1");
						args.popString("testArg2", "Not specified");
						args.assertCompletelyParsed();
					}
				});

		// execute with success
		cmd.process(testSource, "foo");
		cmd.process(testSource, "foo", "bar");

		// execute with failure
		thrown.expect(CommandException.class);
		cmd.process(testSource, "foo", "bar", "baz");
		cmd.process(testSource);
	}

	@Test
	public void testChildren() {
		CommandManager cm = new CommandManager();
		CommandSource testSource = new TestCommandSource(cm);
		cm.getCommand("test2")
				.getChild("foo")
				.getChild("bar")
				.getChild("baz")
				.setExecutor(new Executor() {
					@Override
					public void execute(CommandSource source, Command command, CommandArguments args) throws CommandException {
						assertEquals("baz", command.getName());
						assertEquals("hello", args.popString("testStr"));
					}
				});

		testSource.processCommand("test2", "foo", "bar", "baz", "hello");
	}

	@Test
	public void testFilter() {
		CommandManager cm = new CommandManager();
		CommandSource source = new TestCommandSource(cm);
		cm.getCommand("test3").addFilter(new PlayerFilter()).setExecutor(new Executor() {
			@Override
			public void execute(CommandSource source, Command command, CommandArguments args) throws CommandException {
			}
		});

		thrown.expectCause(isA(CommandException.class));
		thrown.expectMessage("You must be a Player to execute this command");
		source.processCommand("test3");
	}

	private static class TestCommandSource implements CommandSource {
		private final CommandManager cm;

		public TestCommandSource(CommandManager cm) {
			this.cm = cm;
		}

		@Override
		public void sendMessage(String message) {
			System.out.println(message);
		}

		@Override
		public void sendCommand(String command, String... args) {
		}

		@Override
		public void processCommand(String command, String... args) {
			Command cmd = cm.getCommand(command, false);
			if (cmd == null) {
				sendMessage("Unknown command: '" + command + "'.");
				return;
			}

			try {
				cmd.process(this, args);
			} catch (CommandException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Locale getPreferredLocale() {
			return Locale.ENGLISH_US;
		}

		@Override
		public boolean hasPermission(String node) {
			return true;
		}

		@Override
		public boolean hasPermission(World world, String node) {
			return true;
		}

		@Override
		public boolean isInGroup(String group) {
			return false;
		}

		@Override
		public boolean isInGroup(World world, String group) {
			return false;
		}

		@Override
		public String[] getGroups() {
			return new String[0];
		}

		@Override
		public String[] getGroups(World world) {
			return new String[0];
		}

		@Override
		public ValueHolder getData(String node) {
			return null;
		}

		@Override
		public ValueHolder getData(World world, String node) {
			return null;
		}

		@Override
		public boolean hasData(String node) {
			return false;
		}

		@Override
		public boolean hasData(World world, String node) {
			return false;
		}

		@Override
		public String getName() {
			return null;
		}
	}
}
