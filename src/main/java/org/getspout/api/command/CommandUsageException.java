package org.getspout.api.command;

/**
 * Exception thrown when invalid command usage is given for a command.
 */

public class CommandUsageException extends CommandException {

	/**
	 *
	 */
	private static final long serialVersionUID = 5251205509609168547L;

	public CommandUsageException(String msg) {
		super(msg);
	}

	public CommandUsageException(Command cmd) {
		super(cmd.getUsage(new String[] {cmd.getPreferredName()}));
	}
}
