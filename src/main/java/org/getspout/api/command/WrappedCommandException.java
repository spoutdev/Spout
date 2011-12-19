package org.getspout.api.command;

/**
 * Thrown to wrap any exceptions caught during execution of a command
 */
public class WrappedCommandException extends CommandException {

	public WrappedCommandException(Throwable cause) {
		super(cause);
	}
}
