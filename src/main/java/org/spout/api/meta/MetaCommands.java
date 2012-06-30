package org.spout.api.meta;

import org.spout.api.Spout;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;

public class MetaCommands {
	
	/**
	 * Interface for runtime variable setting
	 * 
	 */
	@Command(aliases = {"set"}, usage = "<variable name> <value>", desc = "Sets an engine variable", min = 2, max = 2)
	@CommandPermissions("spout.console.set")
	public void set(CommandContext args, CommandSource source) throws CommandException{
		Spout.getEngine().setVariable(args.getString(0), args.getString(1));		
	}
	
	
}
