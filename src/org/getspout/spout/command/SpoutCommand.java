package org.getspout.spout.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spout.Spout;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutCommand implements CommandExecutor {

	private final Spout p;

	public SpoutCommand(Spout p) {
		this.p = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!sender.isOp()) {
			sender.sendMessage("[Spout] This command is Op only");
			return true;
		}
		
		if (args.length == 0) {
			return false;
		}
		
		sender.sendMessage("[Spout] Server version: " + p.getDescription().getVersion());
		
		String c = args[0];

		if (c.equals("version")) {
			
			CommandSender target = sender;
			
			if (args.length > 1) {
				target = p.getServer().getPlayer(args[1]);
				if (target == null) {
					sender.sendMessage("[Spout] Unknown player: " + args[1]);
					return true;
				}
			}

			if (!(target instanceof Player)) {
				sender.sendMessage("[Spout] Client version: no client");
			} if (!(target instanceof SpoutPlayer)) {
				sender.sendMessage("[Spout] Client version: standard client");
			} else {
				SpoutCraftPlayer sp = (SpoutCraftPlayer)target;
				if (!sp.isSpoutCraftEnabled()) {
					sender.sendMessage("[Spout] Client version: Not a Spout client");
				} else {
					sender.sendMessage("[Spout] Client version: " + sp.getVersionString());
				}
			}
			return true;
		}
		
		return false;
		
	}

}
