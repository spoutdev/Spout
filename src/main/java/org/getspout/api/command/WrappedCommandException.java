package org.getspout.api.command;

/**
 * Thrown to wrap any exceptions caught during execution of a command
 */
public class WrappedCommandException extends CommandException {

	/**
	 *
	 */
	private static final long serialVersionUID = 9124773905653368232L;

	public WrappedCommandException(Throwable cause) {
		super(cause);
	}
}
