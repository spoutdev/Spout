package org.getspout.api.command;

import org.getspout.api.util.Named;

/**
 * Provides support for setting up commands for Plugins 
 * 
 * This uses chaining to allow concise setup of commands
 * 
 * Commands could be registered using the following structure.
 * <pre>
 * Game.getCommandRoot()
 *     .sub("preferredname")
 *         .alias("alias1").alias("alias2")
 *         .help("This is the main command for MyPlugin")
 *         .executor(myExecutor)
 *         .sub("subcommand")
 *             .help("This is a sub command of main command")
 *             .executor(myExecutor)
 *         .closeSub()
 *     .closeSub();
 * </pre>
 */

public interface Command {

	/**
	 * Creates a command and adds it as a sub-command to the active Command.  
	 * 
	 * The sub-command is linked to the given String, made the active command and added to the top of the Command stack
	 * 
	 * @param primaryName the name to link sub-command to
	 * @return the new sub-command
	 */
	public Command addSubCommand(Named owner, String primaryName);

	/**
	 * Alias for addSubCommand
	 *
	 * @param primaryName the Enum to link sub-command to
	 * @return the new sub-command
	 */
	public Command sub(Named owner, String primaryName);

	/**
	 * Registers sub commands created by a {@link CommandRegistrationsFactory}
	 * @param owner The owner of these commands
	 * @param object The {@link T} used by the CommandRegistrationFactory to register commands.
	 * @param factory The {@link CommandRegistrationsFactory} used to convert {@code object} into a {@link java.util.List} of {@link Command}s
	 * @param <T> The type that {@code factory} accepts
	 * @return
	 */
	public <T> Command addSubCommands(Named owner, T object, CommandRegistrationsFactory<T> factory);
	
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
	 * Adds a name to the active Command.  If this is called more than once for a Command, subsequent calls will set aliases.
	 * 
	 * The first free name will be used for the command name.  If no alias is free, then the command will not be registered.  
	 * 
	 * Commands can always be accessed using the "plugin-name:command-name".
	 * 
	 * @param name the name or alias
	 * @return the active Command
	 */
	public Command addCommandName(String name);

	/**
	 * Alias for addCommandName
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
	 * @param help the help string
	 * @return the active Command
	 */
	public Command setHelpString(String help);
	
	/**
	 * Alias for setHelpString
	 * 
	 * @param help the help string
	 * @return the active Command
	 */
	public Command help(String help);

	/**
	 * Sets the usage string for the active Command.
	 *
	 * If this is called more than once for a Command, subsequent calls will overwrite previous calls.
	 *
	 * @param usage the usage string
	 * @return the active Command
	 */
	public Command setUsageString(String usage);

	/**
	 * Alias for setHelpString
	 *
	 * @see #setUsageString(String)
	 */
	public Command usage(String usage);
	
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
	 * @see #setExecutor(CommandExecutor)
	 */
	public Command executor(CommandExecutor executor);
	
	/**
	 * Executes a command based on the provided arguments.  
	 * 
	 * The base index is equal to the number of arguments that have already been processed by super commands.
	 *
	 * @param source the {@link CommandSource} that sent this command.
	 * @param args the command arguments
	 * @param baseIndex the arguments that have already been processed by 
	 * @return true on success
	 */
	public boolean execute(CommandSource source, String[] args, int baseIndex);
	
	/**
	 * Gets the usage message for the command.
	 *
	 * @param input The raw input that was given
	 * @return the command's usage message
	 */
	public String getUsageMessage(String[] input);
	
	/**
	 * Gets the command's preferred name
	 * 
	 * @return the preferred name
	 */
	public String getPreferredName();
}
