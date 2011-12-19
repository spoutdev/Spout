package org.getspout.server.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.permissions.PermissionDefault;
import org.getspout.server.SpoutServer;

public class TeleportCommand extends SpoutCommand{

	public TeleportCommand(SpoutServer server) {
		super(server, "teleport", "Teleport players to each other", "[playerfrom] [playerto]", "tp", "tele");
	}

	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
		Player player1 = null;
		Player player2 = null;
		if(args.length<2) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use this without a player name");
				return false;
			}
			player1 = (Player) sender;
			player2 = server.getPlayerExact(args[0]);
		}
		else {
			player1 = server.getPlayerExact(args[0]);
			player2 = server.getPlayerExact(args[1]);
		}
		if(player1.teleport(player2, TeleportCause.COMMAND)) {
			sender.sendMessage(player1.getName() + " has been succesfully teleported to " + player2.getName() + ".");
			return true;
		}
		else {
			sender.sendMessage(ChatColor.RED + "Unable to teleport player.");
		}
		return false;
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}

}
