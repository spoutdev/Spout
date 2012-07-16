package org.spout.engine.command;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.exception.CommandException;


public class InputCommands {
	
	@Command(aliases = {"bind"}, usage = "bind <key> <command>", desc = "Binds a command to a key", min = 2)
	public void bind(CommandContext args, CommandSource source) throws CommandException {
		((Client)Spout.getEngine()).getInput().bind(args.getString(0), args.getJoinedString(1));
	}
}
