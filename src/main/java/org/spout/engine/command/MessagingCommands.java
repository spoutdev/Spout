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

import org.spout.api.Server;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.Permissible;
import org.spout.api.command.annotated.Platform;
import org.spout.api.entity.Player;
import org.spout.api.exception.CommandException;

import org.spout.engine.SpoutEngine;

/**
 * Commands relating to messaging
 */
public class MessagingCommands  {
	private final SpoutEngine engine;

	public MessagingCommands(SpoutEngine engine) {
		this.engine = engine;
	}

	@Command(aliases = {"tell", "msg"}, usage = "<target> <message>", desc = "Tell a message to a specific user", min = 2)
	@Permissible("spout.command.tell")
	public void tell(CommandSource source, CommandArguments args) throws CommandException {
		String playerName = args.getString(0);
		String message = args.getJoinedString(1);
		Player player = args.getPlayer(0, false);
		if (player == source) {
			source.sendMessage("Forever alone.");
		} else if (player != null) {
			source.sendMessage("To " + player.getName() + ": " + message);
			player.sendMessage("From " + source.getName() + ": " + message);
		} else {
			throw new CommandException("Player '" + playerName + "' not found.");
		}
	}

	@Command(aliases = {"emote", "me", "action"}, usage = "<action>", desc = "Emote in the third person", min = 1)
	@Permissible("spout.command.emote")
	@Platform({org.spout.api.Platform.SERVER, org.spout.api.Platform.PROXY})
	public void emote(CommandSource source, CommandArguments args) {
		((Server) engine).broadcastMessage(source.getName() + " " + args.getJoinedString(0));
	}
}
