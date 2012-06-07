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

import org.spout.api.ChatColor;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;
import org.spout.api.player.Player;

import org.spout.engine.SpoutServer;

/**
 * @author zml2008
 */
public class MessagingCommands {
	private final SpoutServer server;

	public MessagingCommands(SpoutServer server) {
		this.server = server;
	}

	@Command(aliases = {"say", "chat"}, usage = "[message]", desc = "Say something!", min = 1, max = -1)
	@CommandPermissions("spout.command.say")
	public void say(CommandContext args, CommandSource source) {
		String message = args.getJoinedString(0);
		if (!message.isEmpty()) {
			if (source instanceof Player) {
				((Player) source).chat(message);
			} else {
				server.broadcastMessage("<" + source.getName() + "> " + message);
			}
		}
	}

	@Command(aliases = {"tell", "msg"}, usage = "<target> <message>", desc = "Tell a message to a specific user", min = 2)
	@CommandPermissions("spout.command.tell")
	public void tell(CommandContext args, CommandSource source) throws CommandException {
		String playerName = args.getString(0);
		String message = args.getJoinedString(1);
		Player player = server.getPlayer(playerName, false);
		if (player == source) {
			source.sendMessage("Forever alone.");
		} else if (player != null) {
			source.sendMessage("To " + ChatColor.BRIGHT_GREEN + player.getName() + ChatColor.WHITE + ": " + message);
			player.sendMessage("From " + ChatColor.BRIGHT_GREEN + source.getName() + ChatColor.WHITE + ": " + message);
		} else {
			throw new CommandException(ChatColor.RED + "Player '" + playerName + "' not found.");
		}
	}
}
