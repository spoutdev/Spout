package org.getspout.api.command;

import org.getspout.api.util.Named;

import java.util.Set;

public interface CommandRegistrationsFactory<T> {

	/**
	 * Creates a command structure from the given {@link T} for usage when registering commands with a CommandsManager
	 *
	 * @param commands The commands objest that commands are created from.
	 * @param parent
	 * @return A {@link Set} of commands that was successfully registered.
	 */
	public boolean create(Named owner, T commands, Command parent);
}
