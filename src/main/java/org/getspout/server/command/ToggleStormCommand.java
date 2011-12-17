package org.getspout.server.command;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.getspout.server.SpoutServer;

/**
 * A built-in command to toggle a storm in a world
 */
public class ToggleStormCommand extends SpoutCommand {
	public ToggleStormCommand(SpoutServer server) {
		super(server, "togglestorm", "Toggles a storm in a world", "<world>");
	}

	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
		if (!checkArgs(sender, args, 0, 1)) {
			return false;
		}
		World world = null;
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You must specify a world.");
				return false;
			}
			world = ((Player) sender).getWorld();
		} else {
			world = server.getWorld(args[0]);
			if (world == null) {
				sender.sendMessage("The world '" + args[0] + "' does not exist.");
				return false;
			}
		}
		server.broadcastMessage((sender instanceof Player ? ((Player) sender).getDisplayName() : "Console")
				+ " toggled a storm in the world '" + world.getName() + "'.");
		world.setStorm(!world.hasStorm());
		return true;
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}
}
