package org.getspout.spout.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spout.Spout;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutVersionCommand implements CommandExecutor {

	private final Spout p;

	public SpoutVersionCommand(Spout p) {
		this.p = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!sender.isOp()) {
			sender.sendMessage("[Spout] This command is Op only");
			return true;
		}
		
		sender.sendMessage("[Spout] Server version: " + p.getDescription().getVersion());
		if (!(sender instanceof Player)) {
			sender.sendMessage("[Spout] Client version: no client");
		} if (!(sender instanceof SpoutPlayer)) {
			sender.sendMessage("[Spout] Client version: standard client");
		} else {
			SpoutCraftPlayer sp = (SpoutCraftPlayer)sender;
			sender.sendMessage("[Spout] Client version: " + sp.getVersionString());
		}
		
		return true;
		
	}

}
