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


import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.Command;
import org.spout.api.command.CommandSource;
import org.spout.api.data.ValueHolder;
import org.spout.api.geo.World;
import org.spout.api.lang.Locale;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.SpoutEngine;

/**
 * An implementation of CommandSource that sends to and receives from various consoles.
 */
public class ConsoleCommandSource implements CommandSource {
	private final SpoutEngine engine;
	private Locale preferredLocale = null;

	public ConsoleCommandSource(SpoutEngine engine) {
		this.engine = engine;
	}

	@Override
	public String getName() {
		return "Console";
	}

	@Override
	public boolean sendMessage(Object... text) {
		return sendMessage(new ChatArguments(text));
	}

	public void sendCommand(String command, ChatArguments arguments) {
		processCommand(command, arguments);
	}

	public void processCommand(String commandName, ChatArguments arguments) {
		Command command = engine.getRootCommand().getChild(commandName);
		if (command == null) {
			sendMessage(ChatStyle.RED, "Unknown command: " + commandName);
		} else {
			command.process(this, commandName, arguments, false);
		}
	}

	public boolean sendMessage(ChatArguments message) {
		return sendRawMessage(message);
	}

	@Override
	public boolean sendRawMessage(Object... text) {
		return sendRawMessage(new ChatArguments(text));
	}

	public boolean sendRawMessage(ChatArguments message) {
		engine.getConsole().addMessage(message);
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
		if (preferredLocale == null) {
			preferredLocale = Locale.getByCode(SpoutConfiguration.DEFAULT_LANGUAGE.getString());
		}
		return preferredLocale;
	}
}
