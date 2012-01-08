package org.getspout.server.command;

import org.getspout.api.ChatColor;
import org.getspout.api.Spout;
import org.getspout.api.command.Command;
import org.getspout.api.command.CommandContext;
import org.getspout.api.command.CommandException;
import org.getspout.api.command.CommandExecutor;
import org.getspout.api.command.CommandSource;
import org.getspout.api.player.Player;

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
