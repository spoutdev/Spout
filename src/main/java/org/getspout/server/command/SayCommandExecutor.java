package org.getspout.server.command;

import org.getspout.api.Spout;
import org.getspout.api.command.Command;
import org.getspout.api.command.CommandContext;
import org.getspout.api.command.CommandException;
import org.getspout.api.command.CommandExecutor;
import org.getspout.api.command.CommandSource;
import org.getspout.api.player.Player;

public class SayCommandExecutor implements CommandExecutor {

	@Override
	public boolean processCommand(CommandSource source, Command command, CommandContext args) throws CommandException {
		if(args.length() == 0) {
			return false;
		}
		String message = args.getJoinedString(0);
		if(!message.isEmpty()) {
			if(source instanceof Player) {
				((Player)source).chat(message);
			} else {
				Spout.getGame().broadcastMessage("<" + source.getName() + "> " + message);
			}
			return true;
		}
		return false;
	}

}
