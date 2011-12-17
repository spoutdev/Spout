package org.getspout.server.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import org.getspout.server.SpoutServer;

/**
 * A built-in command to display the message in form of * USER message
 */
public class MeCommand extends SpoutCommand {
	public MeCommand(SpoutServer server) {
		super(server, "me", "Displays message in form of * USER message", "<message>");
	}

	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
		if (args.length < 1) {
			return false;
		}

		String message = "";
		for (int i = 0; i < args.length; ++i) {
			message += " " + args[i];
		}
		String senderName = sender instanceof Player ? ((Player) sender).getDisplayName() : "Console";
		server.broadcastMessage(" * " + senderName + message);
		return true;
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.TRUE;
	}
}
