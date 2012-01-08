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

public class TellCommandExecutor implements CommandExecutor {

	@Override
	public boolean processCommand(CommandSource source, Command command, CommandContext args) throws CommandException {
		if(args.length() >= 2) {
			String playerName = args.getString(0);
			String message = args.getJoinedString(1);
			Player player = Spout.getGame().getPlayer(playerName, false);
			if(player == source) {
				source.sendMessage("Forever alone.");
			} else if(player != null) {
				source.sendMessage("To " + ChatColor.BRIGHT_GREEN + player.getName() + ChatColor.WHITE + ": " + message);
				player.sendMessage("From " + ChatColor.BRIGHT_GREEN + source.getName() + ChatColor.WHITE + ": " + message);
			} else {
				source.sendMessage(ChatColor.RED + "Player '" + playerName + "' not found.");
			}
			return true;
		} else if(args.length() == 1) {
			source.sendMessage(ChatColor.RED + "No message given.");
		} else if(args.length() == 0) {
			source.sendMessage(ChatColor.RED + "No player and message given.");
		}
		return false;
	}

}
