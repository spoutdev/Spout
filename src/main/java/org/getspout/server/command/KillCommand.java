package org.getspout.server.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.getspout.server.SpoutServer;

public class KillCommand extends SpoutCommand{

	public KillCommand(SpoutServer server) {
		super(server, "kill", "Gives the player a damage of 1000", "");
	}

	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You need to be a player to do this command.");
			return false;
		}
		Player player = (Player) sender;
		player.damage(1000);
		return true;
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}

}
