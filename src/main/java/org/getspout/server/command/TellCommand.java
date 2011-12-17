package org.getspout.server.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.getspout.server.SpoutServer;

/**
 * A built-in command to private message players
 */
public class TellCommand extends SpoutCommand {

	public TellCommand(SpoutServer server) {
		super(server, "tell", "Whisper a message to a player", "<player> <msg>", "msg");
	}

	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("You must specify a player to whisper to.");
			return false;
		}
		Player toTell = server.getPlayer(args[0]);
		if (toTell == null) {
			sender.sendMessage("The player '" + args[0] + "' does not exist or is not online.");
			return false;
		}
		StringBuilder msg = new StringBuilder();
		for (int i = 1; i < args.length; i++) {
			msg.append(args[i] + " ");
		}
		toTell.sendMessage(ChatColor.GRAY + (sender instanceof Player ? ((Player) sender).getDisplayName() : "Console")
				+ " whispers: " + ChatColor.WHITE + msg.toString());
		return true;
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.TRUE;
	}
}
