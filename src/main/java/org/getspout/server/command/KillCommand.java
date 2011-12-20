package org.getspout.server.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.getspout.server.SpoutServer;

public class KillCommand extends SpoutCommand {

	public KillCommand(SpoutServer server) {
		super(server, "kill", "Kill a player", "<player>");
	}

	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
		if (!checkArgs(sender, args, 0, 1)) {
			return false;
		}
		Player toKill = null;
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You must specify a player.");
				return false;
			}
			toKill = (Player) sender;
		} else {
			toKill = server.getPlayer(args[0]);
		}
		if (toKill != null) {
			toKill.damage(1000);
			return true;
		} else {
			sender.sendMessage("The player '" + args[0] + "' does not exist or is not online.");
		}
		return false;
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.TRUE;
	}
}
