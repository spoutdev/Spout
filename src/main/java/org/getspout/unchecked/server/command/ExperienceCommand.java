package org.getspout.unchecked.server.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import org.getspout.unchecked.server.SpoutServer;

/**
 * A built-in command to control a player's experience
 */
public class ExperienceCommand extends SpoutCommand {

	public ExperienceCommand(SpoutServer server) {
		super(server, "xp", "Give experience to a player", "<player> <amount>");
	}

	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
		if (!checkArgs(sender, args, 1, 2)) {
			return false;
		}
		String name = null;
		String xpToParse = null;
		if (args.length == 1) {
			xpToParse = args[0];
			if (!(sender instanceof Player)) {
				sender.sendMessage("You can only give in-game players experience.");
				return false;
			}
			name = ((Player) sender).getName();
		} else {
			name = args[0];
			xpToParse = args[1];
		}
		try {
			int xp = Integer.parseInt(xpToParse);
			Player toGive = server.getPlayer(name);
			if (toGive != null) {
				String phrase = " was given " + xp + " experience.";
				if (xp < 0) {
					phrase = " had " + ("" + xp).replace("-", "") + " experience taken away.";
				}
				toGive.setExp(toGive.getExp() + xp);
				server.broadcastMessage(toGive.getDisplayName() + phrase);
			} else {
				sender.sendMessage("The player '" + name + "' does not exist or is not online.");
			}
		} catch (NumberFormatException ex) {
			sender.sendMessage("'" + xpToParse + "' is not a valid amount of experience.");
			return false;
		}
		return true;
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}
}
