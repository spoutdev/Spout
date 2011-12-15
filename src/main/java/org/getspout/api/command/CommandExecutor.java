package org.getspout.api.command;

public interface CommandExecutor {

	/**
	 * Processes a command
	 * 
	 * @param source the source of the command
	 * @param commandEnum the Enum matching the desired command
	 * @param args the command line arguments
	 * @param baseIndex the number of arguments processed by super-commands
	 * @return
	 */
	public boolean processCommand (CommandSource source, Command command, Enum<?> commandEnum, String[] args, int baseIndex);

}
