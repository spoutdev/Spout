package org.getspout.api.command;

/**
 * Provides support for setting up commands for Plugins 
 * 
 * This uses chaining to allow concise setup of commands
 * 
 * Commands could be registered using the following structure.
 * 
 * Game.getCommandRoot()
 *     .sub("preferredname")
 *         .alias("alias1").alias("alias2")
 *         .help("This is the main command for MyPlugin")
 *         .executor(myExecutor)
 *         .sub("subcommand")
 *             .help("This is a sub command of main command")
 *             .e(myExecutor)
 *         .closeSub()
 *     .closeSub();
 * 
 */

public interface Command {

	/**
	 * Creates a command and adds it as a sub-command to the active Command.  
	 * 
	 * The sub-command is linked to the given Enum, made the active command and added to the top of the Command stack
	 * 
	 * Enums should be unique for every command
	 * 
	 * @param primaryName the name to link sub-comamnd to
	 * @return the new sub-command
	 */
	public Command addSubCommand(String primaryName);

	/**
	 * Alias for addSubCommand
	 *
	 * @param primaryName the Enum to link sub-comamnd to
	 * @return the new sub-command
	 */
	public Command sub(String primaryName);
	
	/**
	 * Completes creation of a sub-command.  There should be a matching call of this method for every call to addSubCommand.
	 * 
	 * The topmost Command on the stack is removed and the next highest becomes the active command.
	 * 
	 * @return the new active command or null if the stack was empty
	 */
	public Command closeSubCommand();

	/**
	 * Alias for closeSubCommand.
	 *
	 * @return the new active command
	 */
	public Command closeSub();

	/**
	 * Sets the name of the active Command.  If this is called more than once for a Command, subsequent calls will set aliases.
	 * 
	 * The first free name will be used for the command name.  If no alias is free, then the command will not be registered.  
	 * 
	 * Commands can always be accessed using the "plugin-name.command-name".
	 * 
	 * @param name the name or alias
	 * @return the active Command
	 */
	public Command addCommandName(String name);

	/**
	 * Alias for setCommandName
	 *
	 * @param name the name or alias
	 * @return the active Command
	 */
	public Command name(String name);

	/**
	 * Sets the help string for the active Command.  
	 * 
	 * If this is called more than once for a Command, subsequent calls will overwrite previous calls.
	 * 
	 * @param name the help string
	 * @return the active Command
	 */
	public Command setHelpString(String name);
	
	/**
	 * Alias for setHelpString
	 * 
	 * @param name the help string
	 * @return the active Command
	 */
	public Command help(String name);
	
	/**
	 * Sets the Executor for the active Command.  
	 * 
	 * If this is called more than once for a Command, subsequent calls will overwrite previous calls.
	 * 
	 * @param executor the help string
	 * @return the active Command
	 */
	public Command setExecutor(CommandExecutor executor);
	
	/**
	 * Alias for setExecutor
	 * 
	 * @param executor the help string
	 * @return the active Command
	 */
	public Command executor(CommandExecutor executor);
	
	/**
	 * Executes a command based on the provided arguments.  
	 * 
	 * The base index is equal to the number of arguments that have already been processed by super commands.
	 * 
	 * @param args the command arguments
	 * @param baseIndex the arguments that have already been processed by 
	 * @return true on success
	 */
	public boolean execute(String[] args, int baseIndex);
	
	/**
	 * Gets the usage message for the command.
	 * 
	 * @return the command's usage message
	 */
	public String getUsageMessage();
	
	/**
	 * Gets the commands preferred name
	 * 
	 * @return the preferred name
	 */
	public String getPreferredName();
}
