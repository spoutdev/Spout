package org.getspout.server.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import org.getspout.server.SpoutServer;

/**
 * A built-in command to kick a person off the server.
 */
public class KickCommand extends SpoutCommand {
	public KickCommand(SpoutServer server) {
		super(server, "kick", "Kick a player off the server", "<player>");
	}

	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
		if (!checkArgs(sender, args, 1)) {
			return false;
		}
		Player player = server.getPlayer(args[0]);
		if (player == null) {
			sender.sendMessage(ChatColor.GRAY + "No such player " + args[0]);
			return false;
		}

		String senderName = (sender instanceof Player ? ((Player) sender).getDisplayName() : "Console");
		player.kickPlayer("Kicked by " + senderName);
		server.broadcastMessage(ChatColor.YELLOW + args[0] + " has been kicked");
		return true;
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}
}
