package org.getspout.server.command;

import org.getspout.api.ChatColor;
import org.getspout.api.Spout;
import org.getspout.api.command.Command;
import org.getspout.api.command.CommandContext;
import org.getspout.api.command.CommandException;
import org.getspout.api.command.CommandExecutor;
import org.getspout.api.command.CommandSource;
import org.getspout.api.player.Player;

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
