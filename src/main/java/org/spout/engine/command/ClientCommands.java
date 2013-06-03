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
package org.spout.engine.command;

import java.net.InetSocketAddress;

import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.entity.Player;
import org.spout.api.exception.CommandException;
import org.spout.api.input.Binding;
import org.spout.api.input.Keyboard;
import org.spout.api.protocol.Protocol;

import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutEngine;
import org.spout.engine.protocol.PortBindingImpl;

public class ClientCommands extends CommonCommands {
	public ClientCommands(SpoutEngine engine) {
		super(engine);
	}

	@Override
	public SpoutClient getEngine() {
		return (SpoutClient) super.getEngine();
	}

	@Command(aliases = {"connect", "conn"}, desc = "Connect to a server", usage = "<protocol> <address> [port]", min = 2, max = 3)
	public void connectClient(CommandSource source, CommandArguments args) throws CommandException {
		Protocol protocol = Protocol.getProtocol(args.getString(0));
		if (protocol == null) {
			throw new CommandException("Unknown protocol: " + args.getString(0));
		}
		String address = args.getString(1);
		int port = args.length() > 2 ? args.getInteger(2) : protocol.getDefaultPort();
		getEngine().connect(new PortBindingImpl(protocol, new InetSocketAddress(address, port)));
		source.sendMessage("Connected to " + address + ":" + port + " with protocol " + protocol.getName());
	}

	@Command(aliases = "disconnect", desc = "Disconnect the client from the server", usage = "[message]", min = 0, max = -1)
	public void executeClient(CommandSource source, CommandArguments args) {
		if (source instanceof Player) {
			((Player) source).getSession().dispose();
		}
	}

	@Command(aliases = {"bind"}, usage = "bind <key> <command>", desc = "Binds a command to a key", min = 2)
	public void bind(CommandSource source, CommandArguments args) throws CommandException {
		getEngine().getInputManager().bind(new Binding(args.getJoinedString(1), Keyboard.get(args.getString(0))));
	}

	@Command(aliases = {"say", "chat"}, usage = "[message]", desc = "Say something!", min = 1, max = -1)
	public void clientSay(CommandSource source, CommandArguments args) {
		getEngine().getCommandSource().sendMessage(args.getJoinedString(0));
	}

	@Command(aliases = {"clear"}, usage = "[message]", desc = "Console Cleared", min = 0, max = 0)
	public void consoleClear(CommandSource source, CommandArguments args) {
		getEngine().getScreenStack().getConsole().clearConsole();
	}
}
