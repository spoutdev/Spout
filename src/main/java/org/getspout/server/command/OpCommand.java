package org.getspout.server.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import org.getspout.server.SpoutServer;

/**
 * A built-in command to give a player OP status.
 */
public class OpCommand extends SpoutCommand {
	public OpCommand(SpoutServer server) {
		super(server, "op", "Gives a player OP status", "<player>");
	}

	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
		if (!checkArgs(sender, args, 1)) {
			return false;
		} else {
			server.getOpsList().add(args[0]);
			Player target = server.getPlayer(args[0]);
			if (target != null) {
				target.sendMessage(ChatColor.GRAY + target.getName() + ": You are now op");
			}
			return tellOps(sender, "Opping " + args[0]);
		}
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}
}
