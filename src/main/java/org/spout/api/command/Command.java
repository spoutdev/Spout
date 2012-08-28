/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.command;

import java.util.List;
import java.util.Set;

import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.ChatSection;
import org.spout.api.chat.completion.CompletionRequest;
import org.spout.api.chat.completion.CompletionResponse;
import org.spout.api.chat.completion.Completor;
import org.spout.api.exception.CommandException;
import org.spout.api.plugin.Platform;
import org.spout.api.util.Named;

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
public interface Command extends Completor {
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
	 * Executes a command based on the provided arguments.
	 *
	 * The base index is equal to the number of arguments that have already been
	 * processed by super commands.
	 *
	 * @param source the {@link CommandSource} that sent this command.
	 * @param name The alias used to look this command up
	 * @param args the command arguments
	 * @param baseIndex the arguments that have already been processed by
	 * @param fuzzyLookup Whether to use levenschtein distance while looking up
	 *            commands.
	 * @throws org.spout.api.exception.CommandException when invalid arguments were passed to the command or an error occurred
	 */
	public void execute(CommandSource source, String name, List<ChatSection> args, int baseIndex, boolean fuzzyLookup) throws CommandException;

	/**
	 * Executes a command with the provided arguments
	 * @param source The {@link CommandSource} that executed this command
	 * @param name The alias used to look this command up
	 * @param args The arguments provided
	 * @param fuzzyLookup Whether to use levenschtein distance while looking up
	 *            commands.
	 * @return Whether execution was successful.
	 */
	public boolean process(CommandSource source, String name, ChatArguments args, boolean fuzzyLookup);

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
	 * Sets the Executor for the active Command for the specified platform.
	 *
	 * If this is called more than once for a Command, subsequent calls will
	 * overwrite previous calls.
	 *
	 * @param executor the help string
	 * @return the active Command
	 */
	public Command setExecutor(Platform platform, CommandExecutor executor);

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
	 * Returns this command's help. The help may contain multiple lines, split by \n.
	 * @return this command's help
	 */
	public String getHelp();

	/**
	 * Returns the usage for this command with {@link #getPreferredName()}
	 * as the only element in the input array and a baseIndex of zero
	 * @return The usage for this command
	 */
	public String getUsage();

	/**
	 * Gets the usage message for the command.
	 *
	 * @param name The name used to execute this command
	 * @param args The arguments passed to this command
	 * @param baseIndex The index where command arguments begin in input
	 * @return the command's usage message
	 */
	public String getUsage(String name, List<ChatSection> args, int baseIndex);

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
	 * Removes children from this command who are owned by {@code owner}
	 * @param owner The owner to remove the children of
	 * @return The active command
	 */
	public Command removeChildren(Named owner);

	/**
	 * Removes an alias
	 *
	 * @param name The name of the alias to remove.
	 * @return the active Command
	 */
	public Command removeAlias(String name);

	/**
	 * Return the int id of the command
	 *
	 * @return The command's id
	 */
	public int getId();

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
	 * Check if {@code owner} owns this command
	 * @param owner The {@link Named} to check
	 * @return Whether owner is the owner
	 */
	public boolean isOwnedBy(Named owner);

	/**
	 * Returns the name of this command's owner
	 * @return The name of this command's owner
	 */
	public String getOwnerName();

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

	/**
	 * Sets a raw command executor that overrides Spout's built-in nested
	 * command and command flags handling.
	 *
	 * @param rawExecutor The command's raw executor.
	 * @return The active command
	 */
	public Command setRawExecutor(RawCommandExecutor rawExecutor);

	/**
	 * Sets the permissions required to use this command.
	 *
	 * @param requireAll Whether to require all of the listed permissions to
	 *            execute the command.
	 * @param permissions The permissions required
	 * @return The active command
	 */
	public Command setPermissions(boolean requireAll, String... permissions);

	/**
	 * Sets the arg limits for a command.
	 *
	 * @param min Minimum arg length. Cannot be less than 0.
	 * @param max Maximum argument length. -1 for unlimited
	 * @return The active command
	 */
	public Command setArgBounds(int min, int max);

	/**
	 * Return a child of this command registered with {@code name}
	 * @param name The name of the child command requested
	 * @return The child command, or null if it does not exist
	 */
	public Command getChild(String name);

	/**
	 * Checks whether {@code source} has the necessary permissions to use this command,
	 * using the same method used for executing this command
	 * @param source The sender of this command
	 * @return Whether {@code source} can use this command
	 */
	public boolean hasPermission(CommandSource source);

	/**
	 * Get the completions for a specified command.
	 * @param request The requset for completion
	 * @param baseIndex The base arg index
	 * @return The responses to complete. If this is a client and a request to the server is needed, return null
	 */
	public CompletionResponse getCompletion(CompletionRequest request, int baseIndex);
}
