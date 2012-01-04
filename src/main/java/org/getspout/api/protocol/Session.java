package org.getspout.api.protocol;

import java.net.InetSocketAddress;
import org.getspout.api.Game;

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
	
	/**
	 * Gets the state of this session.
	 *
	 * @return The session's state.
	 */
	public State getState();
	
	/**
	 * Sets the state of this session.
	 *
	 * @param state The new state.
	 */
	public void setState(State state);
	
	/**
	 * Sends a message to the client.
	 *
	 * @param message The message.
	 */
	public void send(Message message);
	
	/**
	 * Disconnects the session with the specified reason. This causes a
	 * {@link KickMessage} to be sent. When it has been delivered, the channel
	 * is closed.
	 *
	 * @param reason The reason for disconnection.
	 */
	public void disconnect(String reason);
	
	/**
	 * Returns the address of this session.
	 *
	 * @return The remote address.
	 */
	public InetSocketAddress getAddress();
	
	public enum State {

		/**
		 * In the exchange handshake state, the server is waiting for the client
		 * to send its initial handshake packet.
		 */
		EXCHANGE_HANDSHAKE,

		/**
		 * In the exchange identification state, the server is waiting for the
		 * client to send its identification packet.
		 */
		EXCHANGE_IDENTIFICATION,

		/**
		 * In the game state the session has an associated player.
		 */
		GAME;
	}
	
	public Game getGame();

}
