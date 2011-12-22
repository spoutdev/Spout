package org.getspout.server.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.getspout.server.SpoutServer;

public class TeleportCommand extends SpoutCommand{

	public TeleportCommand(SpoutServer server) {
		super(server, "teleport", "Teleport players to each other.", "<playerfrom> [playerto]", "tp");
	}

	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
		if(!checkArgs(sender, args, 1, 2)) {
			return false;
		}
		String sfrom;
		String sto;
		if(args.length<2) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use this without a player name.");
				return false;
			}
			sfrom = sender.getName();
			sto = args[0];
		}
		else {
			sfrom = args[0];
			sto = args[1];
		}
		Player from = server.getPlayerExact(sfrom);
		Player to = server.getPlayerExact(sto);
		if(from==null) {
			sender.sendMessage(ChatColor.GRAY + "Cannot find user " + sfrom + ". No teleport.");
			return false;
		}
		else if(to==null) {
			sender.sendMessage(ChatColor.GRAY + "Cannot find user " + sto + ". No teleport.");
			return false;
		}
		if(from.teleport(to)) {
			sender.sendMessage(ChatColor.GRAY + "(" + sender.getName() + ": Teleporting " + sfrom + " to " + sto + ".");
			return true;
		}
		else {
			sender.sendMessage(ChatColor.GRAY + "Unable to teleport.");
		}
		return false;
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}

}
