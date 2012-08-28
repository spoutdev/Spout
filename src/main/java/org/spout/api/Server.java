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
package org.spout.api;

import java.util.Collection;
import java.util.List;

import org.spout.api.chat.ChatArguments;
import org.spout.api.entity.Player;
import org.spout.api.protocol.PortBinding;
import org.spout.api.util.access.AccessManager;

/**
 * Represents the server-specific implementation of Minecraft.
 */
public interface Server extends Engine {
	/**
	 * Returns all player names that have ever played on this Game, whether they are online or not.
	 *
	 * @return all the player names
	 */
	public List<String> getAllPlayers();

	/**
	 * Gets all players currently online
	 *
	 * @return array of all active players
	 */
	public Player[] getOnlinePlayers();

	/**
	 * Gets the maximum number of players this game can host, or -1 if infinite
	 *
	 * @return max players
	 */
	public int getMaxPlayers();

	/**
	 * Broadcasts the given message to all players
	 *
	 * The implementation of broadcast is identical to iterating over
	 * {@link #getOnlinePlayers()} and invoking {@link Player#sendMessage(Object...)} for
	 * each player.
	 *
	 * @param message to send
	 */
	public void broadcastMessage(Object... message);

	/**
	 * Broadcasts the given message to all players
	 *
	 * The implementation of broadcast is identical to calling a {@link org.spout.api.event.server.permissions.PermissionGetAllWithNodeEvent}
	 * event, iterating over each element in getReceivers, invoking {@link org.spout.api.command.CommandSource#sendMessage(Object...)} for
	 * each CommandSource.
	 *
	 * @param message to send
	 */
	public void broadcastMessage(String permission, Object... message);

	/**
	 * Gets the {@link Player} by the given username. <br/>
	 * <br/>
	 * If searching for the exact name, this method will iterate and check for
	 * exact matches. <br/>
	 * <br/>
	 * Otherwise, this method will iterate over over all players and find the closest match
	 * to the given name, by comparing the length of other player names that
	 * start with the given parameter. <br/>
	 * <br/>
	 * This method is case-insensitive.
	 *
	 * @param name to look up
	 * @param exact Whether to use exact lookup
	 * @return Player if found, else null
	 */
	public Player getPlayer(String name, boolean exact);

	/**
	 * Matches the given username to all players that contain it in their name.
	 *
	 * If no matches are found, an empty collection will be returned. The return
	 * will always be non-null.
	 *
	 * @param name to match
	 * @return Collection of all possible matches
	 */
	public Collection<Player> matchPlayer(String name);

	/**
	 * Gets the {@link AccessManager} of the Server. The access manager handles who can join the server and who cannot.
	 *
	 * @return access manager
	 */
	public AccessManager getAccessManager();

	/**
	 * True if this server does not check if players are flying or not.
	 *
	 * If disabled, the server will attempt to verify that players are not
	 * flying, and kick any players that are flying.
	 *
	 * @return allow flight
	 */
	public boolean allowFlight();

	/**
	 * Returns all IP addresses being listened to.
	 * The returned collection is unmodifiable.
	 *
	 * @return address
	 */
	public List<PortBinding> getBoundAddresses();

	/**
	 * Binds the server to a certain address
	 *
	 * @param binding The address and protocol to bind to.
	 * @return true if successful
	 */
	public boolean bind(PortBinding binding);

	/**
	 * Maps a port for both TCP and UDP communication for Universal Plug and Play enabled InternetGatewayDevices
	 *
	 * @param port the port to be mapped
	 * @return the session
	 */
	public void mapUPnPPort(int port);

	/**
	 * Maps a port for both TCP and UDP communication for Universal Plug and Play enabled InternetGatewayDevices
	 *
	 * @param port the port to be mapped
	 * @param description the description for this mapping
	 * @return the session
	 */
	public void mapUPnPPort(int port, String description);

	/**
	 * Maps a port for TCP communication for Universal Plug and Play enabled InternetGatewayDevices
	 *
	 * @param port the port to be mapped
	 * @return the session
	 */
	public void mapTCPPort(int port);

	/**
	 * Maps a port for TCP communication for Universal Plug and Play enabled InternetGatewayDevices
	 *
	 * @param port the port to be mapped
	 * @param description the description for this mapping
	 * @return the session
	 */
	public void mapTCPPort(int port, String description);

	/**
	 * Maps a port for TCP communication for Universal Plug and Play enabled InternetGatewayDevices
	 *
	 * @param port the port to be mapped
	 * @return the session
	 */
	public void mapUDPPort(int port);

	/**
	 * Maps a port for TCP communication for Universal Plug and Play enabled InternetGatewayDevices
	 *
	 * @param port the port to be mapped
	 * @param description the description for this mapping
	 * @return the session
	 */
	public void mapUDPPort(int port, String description);
}
