package org.getspout.api.command;

import java.util.Set;

public interface CommandRegistrationsFactory<T> {

	/**
	 * Creates a command structure from the given {@link T} for usage when registering commands with a CommandsManager
	 * @param commands The commands objest that commands are created from.
	 * @return A {@link Set} of commands that was successfully registered.
	 */
	public Set<Command> create(T commands);
}
