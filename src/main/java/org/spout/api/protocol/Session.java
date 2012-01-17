/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.protocol;

import java.net.InetSocketAddress;
import org.spout.api.Game;

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
	 * @param protocol the protocol (Bootstrap until set)
	 */
	public void setProtocol(Protocol protocol);

	/**
	 * Returns the {@link PlayerProtocol} associated with this session.
	 * Returns bootstrap until another protocol is set.
	 */
	public PlayerProtocol getPlayerProtocol();

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

	/**
	 * Gets the id for this session
	 *
	 * @return session id
	 */
	public String getSessionId();

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
