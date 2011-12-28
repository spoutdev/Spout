package org.getspout.api.protocol;


public interface Session {
	
	/**
	 * Passes a message to a session for processing.
	 * 
	 * @param message message to be processed
	 */
	public <T extends Message> void messageReceived(T message);
	
	/**
	 * Disposes of this session by destroying the associated player, if there is one.
	 * 
	 * @param broadcastQuit true if a quit message should be sent
	 */
	public void dispose(boolean broadcastQuit);
	
	/**
	 * Sets the protocol associated with this session.
	 * 
	 * @param codecLookupService the protocol (Bootstrap until set)
	 */
	public void setProtocol(Protocol protocol);

}
