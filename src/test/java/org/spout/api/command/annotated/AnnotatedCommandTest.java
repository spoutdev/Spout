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
package org.spout.api.command.annotated;

import org.junit.Assert;
import org.junit.Test;
import org.spout.api.Spout;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandManager;
import org.spout.api.command.CommandSource;
import org.spout.api.command.Executor;
import org.spout.api.faker.EngineFaker;

public class AnnotatedCommandTest {

	@Test
	public void testAnnotatedCommands() {
		EngineFaker.setupEngine();

		CommandManager cm = Spout.getEngine().getCommandManager();
		Executor exec1 = AnnotatedCommandExecutorFactory.create(new CommandsTest());
		Executor exec2 = AnnotatedCommandExecutorFactory.create(new BarCommand(), cm.getCommand("foo"));
		Executor exec3 = AnnotatedCommandExecutorFactory.create(ClassCommandsTest.class);

		org.spout.api.command.Command foo = cm.getCommand("foo");
		org.spout.api.command.Command test = cm.getCommand("test");
		org.spout.api.command.Command bar = foo.getChild("bar");
		org.spout.api.command.Command herp = cm.getCommand("herp");
		org.spout.api.command.Command backflip = cm.getCommand("backflip");

		Assert.assertSame(exec1, foo.getExecutor());
		Assert.assertEquals("foo", foo.getName());
		Assert.assertEquals("foo main command", foo.getHelp());
		Assert.assertEquals("<bar>", foo.getUsage());
		Assert.assertArrayEquals(foo.getAliases().toArray(), new String[] {"foo", "foo"});

		Assert.assertSame(exec1, test.getExecutor());
		Assert.assertEquals("test", test.getName());
		Assert.assertEquals("Stuff.", test.getHelp());
		Assert.assertEquals("", test.getUsage());
		Assert.assertArrayEquals(test.getAliases().toArray(), new String[] {"test", "test", "t"});

		Assert.assertSame(exec2, bar.getExecutor());
		Assert.assertEquals("bar", bar.getName());
		Assert.assertEquals("Does a bar with an optionnal baz", bar.getHelp());
		Assert.assertEquals("[baz]", bar.getUsage());
		Assert.assertArrayEquals(bar.getAliases().toArray(), new String[] {"bar", "bar"});

		Assert.assertSame(exec3, herp.getExecutor());
		Assert.assertEquals("herp", herp.getName());
		Assert.assertEquals("herps a derp.", herp.getHelp());
		Assert.assertEquals("<derp>", herp.getUsage());
		Assert.assertArrayEquals(herp.getAliases().toArray(), new String[] {"herp", "herp"});

		Assert.assertSame(exec3, backflip.getExecutor());
		Assert.assertEquals("backflip", backflip.getName());
		Assert.assertEquals("Makes the server do a backflip", backflip.getHelp());
		Assert.assertEquals("", backflip.getUsage());
		Assert.assertArrayEquals(backflip.getAliases().toArray(), new String[] {"backflip", "backflip", "bflip"});
	}

	private static class CommandsTest {
		@CommandDescription(aliases = "foo", desc = "foo main command", usage = "<bar>")
		public void foo(CommandSource source, CommandArguments args) {
		}

		@CommandDescription(aliases = {"test", "t"}, desc = "Stuff.")
		public void test(CommandSource source, CommandArguments args) {
		}
	}

	private static class BarCommand {
		@CommandDescription(aliases = "bar", desc = "Does a bar with an optionnal baz", usage = "[baz]")
		public void bar(CommandSource source, CommandArguments args) {
		}
	}

	private static final class ClassCommandsTest {
		private ClassCommandsTest() {
		}

		@CommandDescription(aliases = "herp", desc = "herps a derp.", usage = "<derp>")
		public static void herp(CommandSource source, CommandArguments args) {
		}

		@CommandDescription(aliases = {"backflip", "bflip"}, desc = "Makes the server do a backflip")
		public static void backflip(CommandSource source, CommandArguments args) {
		}
	}
}
