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
		if(args.length<1) {
			sender.sendMessage(ChatColor.GRAY + "This command needs atleast 1 argument.");
			return false;
		}
		Player player1 = null;
		Player player2 = null;
		if(args.length<2) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.GRAY + "You must be a player to use this command without a player name.");
				return false;
			}
			player1 = (Player) sender;
			player2 = server.getPlayerExact(args[0]);
		}
		else {
			player1 = server.getPlayerExact(args[0]);
			player2 = server.getPlayerExact(args[1]);
		}
		
		if(player1==null) {
			sender.sendMessage(ChatColor.GRAY + "Cannot find user " + args[0] + ". No teleport.");
			return false;
		}
		else if(player2==null) {
			String playername = "UNDEFINED";
			if(args.length<2) playername = args[0];
			else if(args.length>=2) playername = args[1];
			sender.sendMessage(ChatColor.GRAY + "Cannot find user " + playername + ". No teleport.");
			return false;
		}
		
		if(player1.teleport(player2, TeleportCause.COMMAND)) {
			server.broadcastMessage(ChatColor.GRAY + "(" + sender.getName() + ": Teleporting " + player1.getName() + " to " + player2.getName() + ".");
			return true;
		}
		else {
			sender.sendMessage(ChatColor.GRAY + "Unable to teleport " + player1.getName() + " to " + player2.getName() + ".");
		}
		return false;
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}

}
