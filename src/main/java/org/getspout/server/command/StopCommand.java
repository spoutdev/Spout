package org.getspout.server.command;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.getspout.server.SpoutServer;

/**
 * A built-in command to stop the Spout server.
 */
public class StopCommand extends SpoutCommand {
	public StopCommand(SpoutServer server) {
		super(server, "stop", "Stops the server", "[message]");
	}

	@Override
	public boolean run(CommandSender sender, String commandLabel, String[] args) {
		if(args.length>0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < args.length; i++) {
				if(i!=0) sb.append(" ");
			    sb.append(args[i]);
			}
			String message = sb.toString();
			server.shutdown(message);
		}
		else {
			server.shutdown();
		}
		return tellOps(sender, "Stopping the server");
	}

	@Override
	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}
}
