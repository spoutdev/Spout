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
package org.spout.engine.command;

import java.net.InetSocketAddress;

import org.spout.api.chat.ChatArguments;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.Executor;
import org.spout.api.exception.CommandException;
import org.spout.api.player.Player;
import org.spout.api.plugin.Platform;
import org.spout.api.protocol.Protocol;
import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutEngine;
import org.spout.engine.protocol.PortBindingImpl;

/**
 * Commands to help with management of client connections
 */
public class ConnectionCommands {
	private final SpoutEngine engine;

	public ConnectionCommands(SpoutEngine engine) {
		this.engine = engine;
	}

	@Command(aliases = "disconnect", desc = "Disconnect the client from the server", usage = "[message]", min = 0, max = -1)
	public class DisconnectCommand {
		@Executor(Platform.SERVER)
		public void executeServer(CommandContext args, CommandSource source) throws CommandException {
			ChatArguments message;
			if (args.length() == 0) {
				message = new ChatArguments("Ciao!");
			} else {
				message = args.getJoinedString(0);
			}

			if (source instanceof Player) {
				((Player) source).getSession().disconnect(false, new Object[] {message});
			}
		}
		@Executor(Platform.CLIENT)
		public void executeClient(CommandContext args, CommandSource source) throws CommandException {
			if (source instanceof Player) {
				((Player) source).getSession().dispose();
			}
		}
	}

	@Command(aliases = {"connect", "conn"}, desc = "Connect to a server", usage = "<protocol> <address> [port]", min = 2, max = 3)
	public class ConnectCommand {
		@Executor(Platform.CLIENT)
		public void executeClient(CommandContext args, CommandSource source) throws CommandException {
			Protocol protocol = Protocol.getProtocol(args.getString(0));
			if (protocol == null) {
				throw new CommandException("Unknown protocol: " + args.getString(0));
			}
			String address = args.getString(1);
			int port = args.length() > 2 ? args.getInteger(2) : protocol.getDefaultPort();
			((SpoutClient) engine).connect(new PortBindingImpl(protocol, new InetSocketAddress(address, port)));
			source.sendMessage("Connected to ", address, ":", port, " with protocol ", protocol.getName());
		}
	}
}
