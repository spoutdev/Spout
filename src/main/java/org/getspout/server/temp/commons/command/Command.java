package org.getspout.server.temp.commons.command;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.Plugin;

import org.getspout.server.temp.commons.util.InputCertification;

/**
 *
 *
 */
public abstract class Command {
	protected Map<String, Command> subCommands = new LinkedHashMap<String, Command>();
	protected CommandExecutor executor;
	private String aliasUsed;

	/**
	 * Gets the Plugin associated with this Command
	 *
	 * @return the Plugin associated with this Command
	 */
	public abstract Plugin getPlugin();

	/**
	 * Gets an ordered List of aliases for the command.
	 *
	 * An Iterator on the list should order the Strings from most preferred to least preferred.
	 *
	 * @return the List of aliases or null for no aliases
	 */
	public abstract List<String> getAliases();

	/**
	 * Gets the preferred name for the command.
	 *
	 * @return the preferred name for the command
	 */
	public final String getName() {
		return getAliases().iterator().next();
	}

	/**
	 * Gets an enum representing this Command.
	 *
	 * This enum is passed to the Executor and can be used to distinguish multiple commands being handled by the same Executor
	 *
	 * @return the preferred name for the command
	 */
	public abstract Enum<?> getCommandType();

	/**
	 * Gets a map of sub-Commands for this Command.
	 *
	 * @return the List of sub-Commands
	 */
	public final Map<String, Command> getSubCommands() {
		return subCommands;
	}

	/**
	 * Adds a Sub-Command to this Command
	 *
	 * @return false if the name was already in use
	 */
	public final boolean addSubCommand(Command subCommand) {
		if (subCommand.aliasUsed != null) {
			throw new IllegalArgumentException("A Command may not be linked to more than 1 super Command");
		}
		boolean success = false;
		for (String name : subCommand.getAliases()) {
			if (!InputCertification.isAlphaNumberic(name)) {
				throw new IllegalArgumentException("Command names are restricted to numbers and letters only");
			}
			if (!subCommands.containsKey(name)) {
				subCommands.put(name, subCommand);
				aliasUsed = name;
				success = true;
			}
		}
		String fullName = getFullName(subCommand);
		if (subCommands.containsKey(fullName)) {
			throw new IllegalArgumentException("Plugin attempted to register two commands with the same fully qualified name: " + fullName);
		} else {
			subCommands.put(fullName, subCommand);
		}
		return success;
	}

	/**
	 * Removes a Sub-Command to this Command.  If this command only removes a fully qualified name, it will also return false
	 *
	 * @return true if the command was removed
	 */
	public final boolean removeSubCommand(Command subCommand) {
		String fullName = getFullName(subCommand);
		subCommands.remove(fullName);

		if (subCommand.aliasUsed == null) {
			return false;
		}

		boolean success = subCommands.remove(aliasUsed) != null;

		if (success) {
			subCommand.aliasUsed = null;
		}

		return success;
	}

	/**
	 * Gets the alias used for this command
	 *
	 * @param alias
	 * @return the alias that is use or null if none is in use
	 */
	public String getAliasUsed(String alias) {
		return aliasUsed;
	}

	private String getFullName(Command command) {
		return command.getPlugin().getDescription().getName() + "." + command.getName();
		// TODO - move away from complex name determination -> commons plugins
	}

	/**
	 * Gets the executor for this Command
	 *
	 * @return the executor for this Command, or null if there is no executor
	 */
	public final CommandExecutor getExecutor() {
		return executor;
	}
}
