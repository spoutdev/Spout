package org.getspout.api.command;

/**
 * Exception thrown when invalid command usage is given for a command.
 */

public class CommandUsageException extends CommandException {

	public CommandUsageException(String msg) {
		super(msg);
	}
	
	public CommandUsageException(Command cmd) {
		super(cmd.getUsage(new String[] {cmd.getPreferredName()}));
	}
}
