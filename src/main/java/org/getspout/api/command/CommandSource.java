package org.getspout.api.command;

public interface CommandSource {

	/**
	 * Sends a text message to the source of the command.
	 * 
	 * @param message the message to send
	 * @return true if the message was sent correctly
	 */
	public boolean sendMessage(String message);
	
}
