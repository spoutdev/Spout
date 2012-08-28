/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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

import java.io.Serializable;
import java.net.InetSocketAddress;

import org.jboss.netty.channel.Channel;
import org.spout.api.Engine;
import org.spout.api.map.DefaultedMap;
import org.spout.api.entity.Player;

/**
 * Represents a connection to the server.<br/>
 * Controls the state, protocol and channels of a connection to the server.
 */
public interface Session {
	/**
	 * Passes a message to a session for processing.
	 *
	 * @param upstream true if the message was received from a server
	 * @param message message to be processed
	 */
	public <T extends Message> void messageReceived(boolean upstream, T message);

	/**
	 * Disposes of this session by destroying the associated player, if there is
	 * one.
	 */
	public void dispose();

	/**
	 * Gets the protocol associated with this session.
	 *
	 * @return the protocol
	 */
	public Protocol getProtocol();

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
	 * @param upstream true if the message should be sent to the server
	 * @param message The message.
	 */
	public void send(boolean upstream, Message message);

	/**
	 * Sends a message to the client.
	 *
	 * @param upstream true if the message should be sent to the server
	 * @param force if this message is used in the identification stages of communication
	 * @param message The message.
	 */
	public void send(boolean upstream, boolean force, Message message);

	/**
	 * Sends any amount of messages to the client
	 * @param upstream true if the messages should be sent to the server
	 * @param messages the messages to send to the client
	 */
	public void sendAll(boolean upstream, Message... messages);

	/**
	 * Sends any amount of messages to the client.
	 * @param upstream true if the messages should be sent to the server
	 * @param force if the messages are used in the identification stages of communication
	 * @param messages the messages to send to the client
	 */
	public void sendAll(boolean upstream, boolean force, Message... messages);
	/**
	 * Disconnects the player as a kick. This is equivalent to calling disconnect(reason, true)
	 * @param reason The reason for disconnection
	 * @return Whether the player was actually disconnected
	 */
	public boolean disconnect(Object... reason);

	/**
	 * Disconnects the session with the specified reason. When the kick packet has been delivered,
	 * the channel is closed.
	 *
	 * @param reason The reason for disconnection.
	 * @param kick Whether this disconnection is caused by the player being kicked or the player quitting
	 *             Disconnects are only cancellable when the disconnection is a kick
	 * @return Whether the player was actually disconnected. This can be false if the kick event is cancelled or errors occur
	 */
	public boolean disconnect(boolean kick, Object... reason);
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

	/**
	 * Checks if this session has a player connected to it.
	 *
	 * @return true if this session has a player.
	 */
	public boolean hasPlayer();

	/**
	 * Gets the player associated with this session.
	 * @return Player
	 */
	public Player getPlayer();

	/**
	 * Sets aux Channel when operating as a proxy server.
	 */
	public void bindAuxChannel(Channel c);

	/**
	 * Closes aux Channel when operating as a proxy server
	 */
	public void closeAuxChannel();

	/**
	 * Checks if a channel is the primary channel.  The primary channel never changes for a Session.
	 * An auxiliary channel is used for proxy mode.
	 *
	 * @return the channel to test
	 * @return true if the channel is the primary channel
	 */
	public boolean isPrimary(Channel c);

	/**
	 * Sets the NetworkSynchronizer associated with this player.<br>
	 * <br>
	 * This can only be called once per player login.
	 *
	 * @param synchronizer the synchronizer
	 */
	public void setNetworkSynchronizer(NetworkSynchronizer synchronizer);

	/**
	 * Gets the NetworkSynchronizer associated with this player.<br>
	 *
	 * @return the synchronizer
	 */
	public NetworkSynchronizer getNetworkSynchronizer();

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
		 * In the exchange encryption state, the server is waiting for the
		 * client to send its encryption response packet.
		 */
		EXCHANGE_ENCRYPTION,

		/**
		 * In the game state the session has an associated player.
		 */
		GAME
	}

	public Engine getEngine();

	/**
	 * True if this session is open and connected.
	 * If the session is closed, all packets will be silently ignored.
	 *
	 * @return is connected
	 */
	public boolean isConnected();

	/**
	 * Gets a map of data attached to this session.
	 *
	 * @return data map
	 */
	public DefaultedMap<String, Serializable> getDataMap();
}
