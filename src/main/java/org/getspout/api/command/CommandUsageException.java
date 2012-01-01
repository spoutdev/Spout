package org.getspout.api.command;

/**
 * Exception thrown when invalid command usage is given for a command.
 */
public class CommandUsageException extends CommandException {
	private static final long serialVersionUID = 5251205509609168547L;
	
	private final String usage;

	public CommandUsageException(String msg, String usage) {
		super(msg);
		this.usage = usage;
	}

	public CommandUsageException(String msg, Command cmd) {
		super(msg);
		this.usage = cmd.getUsage(new String[] {cmd.getPreferredName()}, 0);
	}
	
	public String getUsage() {
		return usage;
	}
}
