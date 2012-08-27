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
package org.spout.api.command;


import org.junit.Before;
import org.junit.Test;

import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.ChatSection;
import org.spout.api.data.ValueHolder;
import org.spout.api.exception.CommandException;
import org.spout.api.exception.MissingCommandException;
import org.spout.api.geo.World;
import org.spout.api.lang.Locale;

public class SimpleCommandTest implements CommandSource {
	private SimpleCommand testCommand;

	@Before
	public void setUp() {
		testCommand = new SimpleCommand(this, "test1", "test2");
	}

	@Override
	public String getName() {
		return getClass().getName();
	}

	@Test(expected = MissingCommandException.class)
	public void testUnknownSubCommand() throws CommandException {
		testCommand.execute(this, "test1", new ChatArguments("hellothere").toSections(ChatSection.SplitType.WORD), -1, false);
	}

	@Override
	public boolean sendMessage(Object... message) {
		return sendRawMessage(message);
	}

	public void sendCommand(String command, ChatArguments arguments) {
		processCommand(command, arguments);
	}

	public void processCommand(String command, ChatArguments arguments) {
		return;
	}

	public boolean sendMessage(ChatArguments message) {
		return sendRawMessage(message);
	}

	@Override
	public boolean sendRawMessage(Object... message) {
		return sendRawMessage(new ChatArguments(message));
	}

	public boolean sendRawMessage(ChatArguments message) {
		System.out.println(message.asString());
		return true;
	}

	@Override
	public boolean hasPermission(String node) {
		return true;
	}

	@Override
	public boolean isInGroup(String group) {
		return false;
	}

	@Override
	public String[] getGroups() {
		return new String[0];
	}

	@Override
	public boolean isGroup() {
		return false;
	}

	@Override
	public boolean hasPermission(World world, String node) {
		return true;
	}

	@Override
	public ValueHolder getData(String node) {
		return null;
	}

	@Override
	public Locale getPreferredLocale() {
		return Locale.ENGLISH_US;
	}
}
