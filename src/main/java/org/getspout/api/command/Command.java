package org.getspout.api.command;

import java.util.List;
import java.util.Set;

import org.getspout.api.util.Named;

/**
 * Provides support for setting up commands for Plugins
 *
 * This uses chaining to allow concise setup of commands
 *
 * Commands could be registered using the following structure.
 *
 * <pre>
 * Game.getCommandRoot().sub(&quot;preferredname&quot;).alias(&quot;alias1&quot;, &quot;alias2&quot;).help(&quot;This is the main command for MyPlugin&quot;).executor(myExecutor).sub(&quot;subcommand&quot;).help(&quot;This is a sub command of main command&quot;).executor(myExecutor).closeSub().closeSub();
 * </pre>
 */

public interface Command extends RawCommandExecutor{

	/**
	 * Creates a command and adds it as a sub-command to the active Command.
	 *
	 * The sub-command is linked to the given String, made the active command
	 * and added to the top of the Command stack
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
	 *
	 * @param owner The owner of these commands
	 * @param object The {@link T} used by the CommandRegistrationFactory to
	 *            register commands.
	 * @param factory The {@link CommandRegistrationsFactory} used to convert
	 *            {@code object} into a {@link java.util.List} of
	 *            {@link Command}s
	 * @param <T> The type that {@code factory} accepts
	 * @return
	 */
	public <T> Command addSubCommands(Named owner, T object, CommandRegistrationsFactory<T> factory);

	/**
	 * Completes creation of a sub-command. There should be a matching call of
	 * this method for every call to addSubCommand.
	 *
	 * The topmost Command on the stack is removed and the next highest becomes
	 * the active command.
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
	 * Adds an alias to the active Command.
	 *
	 * The first free name will be used for the command name. If no alias is
	 * free, then the command will not be registered.
	 *
	 * Commands can always be accessed using the "plugin-name:primary-name".
	 *
	 * @param names the aliases
	 * @return the active Command
	 */
	public Command addAlias(String... names);

	/**
	 * Alias for addAlias
	 *
	 * @param names the aliases to add
	 * @return the active Command
	 */
	public Command alias(String... names);

	/**
	 * Sets the help string for the active Command.
	 *
	 * If this is called more than once for a Command, subsequent calls will
	 * overwrite previous calls.
	 *
	 * @param help the help string
	 * @return the active Command
	 */
	public Command setHelp(String help);

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
	 * If this is called more than once for a Command, subsequent calls will
	 * overwrite previous calls.
	 *
	 * @param usage the usage string
	 * @return the active Command
	 */
	public Command setUsage(String usage);

	/**
	 * Alias for setHelpString
	 *
	 * @see #setUsage(String)
	 */
	public Command usage(String usage);

	/**
	 * Sets the Executor for the active Command.
	 *
	 * If this is called more than once for a Command, subsequent calls will
	 * overwrite previous calls.
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
	 * Adds flags to this Command's list of allowed flags. Flags are given in
	 * the format of a String containing the allowed flag characters, where
	 * value flag characters are followed by a :.
	 *
	 * @param flags The flags to add to this command's list of allowed flags.
	 * @return The active command
	 */
	public Command addFlags(String flags);

	/**
	 * Alias for #addFlags(String)
	 *
	 * @param flags The flags to add to this command's list of allowed flags.
	 * @return The active command
	 */
	public Command flags(String flags);

	/**
	 * Gets the usage message for the command.
	 *
	 * @param input The raw input that was given
	 * @return the command's usage message
	 */
	public String getUsage(String[] input, int baseIndex);

	/**
	 * Gets the command's preferred name
	 *
	 * @return the preferred name
	 */
	public String getPreferredName();

	/**
	 *
	 * @return all children commands nested with this command.
	 */
	public Set<Command> getChildCommands();

	/**
	 *
	 * @return the names of all children commands registered with this command.
	 */
	public Set<String> getChildNames();

	/**
	 * Returns the registered names for this command. This includes the primary
	 * name and aliases.
	 *
	 * @return the registered names for this command.
	 */
	public List<String> getNames();

	/**
	 * Removes this command from the list of children.
	 *
	 * @param cmd The command to remove
	 * @return The active Command
	 */
	public Command removeChild(Command cmd);

	/**
	 * Removes a child command named {@code name} from this command
	 *
	 * @param name The name to remove
	 * @return The active command
	 */
	public Command removeChild(String name);

	/**
	 * Removes an alias
	 *
	 * @param name The name of the alias to remove.
	 * @return the active Command
	 */
	public Command removeAlias(String name);

	/**
	 * Locks the command to prevent it from being modified by other owners.
	 *
	 * @param owner The owner of this command.
	 * @return Whether this operation was successful
	 */
	public boolean lock(Named owner);

	/**
	 * Unlocks this command so that it can be modified again.
	 *
	 * @param owner The owner of this command to attempt to unlock it.
	 * @return Whether this operation was successful
	 */
	public boolean unlock(Named owner);

	/**
	 * @return whether this command is locked.
	 */
	public boolean isLocked();

	/**
	 * Updates the aliases list for this child command
	 *
	 * @param child The child command to update.
	 * @return Whether any aliases were changed.
	 */
	public boolean updateAliases(Command child);

	/**
	 * @param name The name to check
	 * @return whether this Command has a child named {@code name}
	 */
	public boolean hasChild(String name);

	/**
	 * Sets {@code parent} as this Command's parent if this command does not
	 * already have a parent
	 *
	 * @param parent The command to set as this command's parent command.
	 * @return The active command.
	 */
	public Command setParent(Command parent);
}
