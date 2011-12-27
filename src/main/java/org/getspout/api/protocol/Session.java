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
	 * Gets the codec lookup service associated with this session.
	 * 
	 * @return the codex lookup service (BookstrapCodec until set)
	 */
	public CodecLookupService getCodecLookupService();
	
	/**
	 * Sets the codec lookup service associated with this session.
	 * 
	 * @param codecLookupService the codec lookup service (Bootstrap until set)
	 */
	public void setCodecLookupService(CodecLookupService codecLookupService);
	
	/**
	 * Sets the handler lookup service associated with this session.
	 * 
	 * @param handlerLookupService the handler lookup service (Bootstrap until set)
	 */
	public void setHandlerLookupService(HandlerLookupService codecLookupService);

}
