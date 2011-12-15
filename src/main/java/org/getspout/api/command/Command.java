package org.getspout.api.command;

/**
 * Provides support for setting up commands for Plugins 
 * 
 * This uses chaining to allow concise setup of commands
 * 
 * Commands could be registered using the following structure.
 * 
 * Game.getCommandRoot()
 *     .s(MyPlugin.MAIN_COMMAND)
 *         .n("preferredname").a("alias1").a("alias2")
 *         .h("This is the main command for MyPlugin")
 *         .e(myExecutor)
 *         .s(MyPlugin.SUB_COMMAND)
 *             .n("subcommand")
 *             .h("This is a sub command of main command")
 *             .e(myExecutor)
 *         .c()
 *     .c();
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
	 * @param commandEnum the Enum to link sub-comamnd to
	 * @return the new sub-command
	 */
	public Command addSubCommand(Enum<?> commandEnum);
	
	/**
	 * Alias for addSubCommand
	 * 
	 * @param commandEnum the Enum to link sub-comamnd to
	 * @return the new sub-command
	 */
	public Command s(Enum<?> command);
	
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
	public Command c();
	
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
	public Command setCommandName(String name);
	
	/**
	 * Alias for setCommandName
	 * 
	 * @param name the name or alias
	 * @return the active Command
	 */
	public Command n(String name);
	
	/**
	 * Sets the help string for the active Command.  
	 * 
	 * If this is called more than once for a Command, subsequent calls will overwrite previous calls.
	 * 
	 * @param the help string
	 * @return the active Command
	 */
	public Command setHelpString(String name);
	
	/**
	 * Alias for setHelpString
	 * 
	 * @param the help string
	 * @return the active Command
	 */
	public Command h(String name);
	
	/**
	 * Sets the Executor for the active Command.  
	 * 
	 * If this is called more than once for a Command, subsequent calls will overwrite previous calls.
	 * 
	 * @param the help string
	 * @return the active Command
	 */
	public Command setExecutor(CommandExecutor executor);
	
	/**
	 * Alias for setExecutor
	 * 
	 * @param the help string
	 * @return the active Command
	 */
	public Command e(String name);
	
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
