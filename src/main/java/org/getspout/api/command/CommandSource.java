package org.getspout.api.command;

public interface CommandSource {

	/**
	 * Sends a text message to the source of the command.
	 *
	 * @param message the message to send
	 * @return whether the message was sent correctly
	 */
	public boolean sendMessage(String message);

	/**
	 * Sends a message to the client without any processing by the server, except to prevent exploits.
	 * @param message The message to send
	 * @return whether the message was sent correctly
	 */
	public boolean sendRawMessage(String message);


}
