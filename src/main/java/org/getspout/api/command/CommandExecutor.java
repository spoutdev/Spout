package org.getspout.api.command;

public interface CommandExecutor {

	/**
	 * Processes a command
	 * 
	 * @param source the source of the command
	 * @param args the command line arguments
	 * @return
	 */
	public boolean processCommand(CommandSource source, Command command, CommandContext args);

}
