package org.getspout.server.command;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import org.getspout.server.SpoutServer;

/**
 * A built-in command to stop the Spout server.
 */
public class StopCommand extends SpoutCommand {
	public StopCommand(SpoutServer server) {
		super(server, "stop", "Stops the server", "");
	}

	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
		if (!checkArgs(sender, args, 0)) {
			return false;
		} else {
			server.shutdown();
			return tellOps(sender, "Stopping the server");
		}
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}
}
