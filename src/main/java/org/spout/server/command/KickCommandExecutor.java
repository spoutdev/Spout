/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.server.command;

import org.spout.api.ChatColor;
import org.spout.api.Spout;
import org.spout.api.command.Command;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandException;
import org.spout.api.command.CommandExecutor;
import org.spout.api.command.CommandSource;
import org.spout.api.player.Player;

public class KickCommandExecutor implements CommandExecutor {

	@Override
	public boolean processCommand(CommandSource source, Command command, CommandContext args) throws CommandException {
		if(source.hasPermission("spout.admin.kick")) {
			if(args.length() >= 1) {
				String playerName = args.getString(0);
				String message = "You have been kicked from the server.";
				if(args.length() >= 2) {
					message += " '" + args.getJoinedString(1) + "'";
				}
				Player player = Spout.getGame().getPlayer(playerName, true);
				if(player != null) {
					player.kick(message);
					source.sendMessage(ChatColor.BRIGHT_GREEN + "Kicked player '"+player.getName()+ (!message.isEmpty()?"' for reason '" + message + "'":"'"));
				}
			}
		} else {
			source.sendMessage(ChatColor.RED + "You don't have permission.");
		}
		return false;
	}

}
