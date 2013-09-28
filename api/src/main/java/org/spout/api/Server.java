/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import org.spout.api.entity.Player;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.World;
import org.spout.api.protocol.PortBinding;
import org.spout.api.protocol.ServerSession;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.SessionRegistry;
import org.spout.api.util.access.AccessManager;
import org.spout.api.util.thread.annotation.LiveRead;
import org.spout.api.util.thread.annotation.SnapshotRead;

/**
 * Represents the server-specific implementation.
 */
public interface Server extends Engine {
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
	 * Gets the network channel group.
	 *
	 * @return The {@link ChannelGroup}.
	 */
	public ChannelGroup getChannelGroup();

	/**
	 * Gets the session registry.
	 *
	 * @return The {@link SessionRegistry}.
	 */
	public SessionRegistry getSessionRegistry();

	/**
	 * Broadcasts the given message to all players
	 *
	 * The implementation of broadcast is identical to iterating over {@link #getOnlinePlayers()} and invoking {@link Player#sendMessage(String)} for each player.
	 *
	 * @param message to send
	 */
	public void broadcastMessage(String message);

	/**
	 * Broadcasts the given message to all players
	 *
	 * The implementation of broadcast is identical to calling a {@link org.spout.api.event.server.permissions.PermissionGetAllWithNodeEvent} event, iterating over each element in getReceivers, invoking
	 * {@link org.spout.api.command.CommandSource#sendMessage(String)} for each CommandSource.
	 *
	 * @param message to send
	 */
	public void broadcastMessage(String permission, String message);

	/**
	 * Gets the {@link Player} by the given username. <br/> <br/> If searching for the exact name, this method will iterate and check for exact matches. <br/> <br/> Otherwise, this method will iterate
	 * over over all players and find the closest match to the given name, by comparing the length of other player names that start with the given parameter. <br/> <br/> This method is case-insensitive.
	 *
	 * @param name to look up
	 * @param exact Whether to use exact lookup
	 * @return Player if found, else null
	 */
	public Player getPlayer(String name, boolean exact);

	/**
	 * Matches the given username to all players that contain it in their name.
	 *
	 * If no matches are found, an empty collection will be returned. The return will always be non-null.
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
	 * If disabled, the server will attempt to verify that players are not flying, and kick any players that are flying.
	 *
	 * @return allow flight
	 */
	public boolean allowFlight();

	/**
	 * Returns all IP addresses being listened to. The returned collection is unmodifiable.
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

	/**
	 * Gets the default world generator for this game. Specific generators can be specified when loading new worlds.
	 *
	 * @return default world generator.
	 */
	public WorldGenerator getDefaultGenerator();

	/**
	 * Searches for an actively loaded world that exactly matches the given name. <br/> <br/> The implementation is identical to iterating over {@link #getWorlds()} and checking for a world that matches
	 * {@link World#getName()}. <br/> <br/>
	 *
	 * Worlds are added to the list immediately, but removed at the end of a tick.
	 *
	 * @param name of the world to search for
	 * @return {@link World} if found, else null
	 */
	@LiveRead
	@SnapshotRead
	public World getWorld(String name);

	/**
	 * Searches for an actively loaded world that exactly matches the given name. <br/> <br/> If searching for the exact name, this method will iterate and check for exact matches. <br/> <br/> Otherwise,
	 * this method will iterate over over all worlds and find the closest match to the given name, by comparing the length of other player names that start with the given parameter. <br/> <br/>
	 *
	 * Worlds are added to the list immediately, but removed at the end of a tick.
	 *
	 * @param name of the world to search for
	 * @param exact Whether to use exact lookup
	 * @return world if found, else null
	 */
	@LiveRead
	@SnapshotRead
	@Override
	public World getWorld(String name, boolean exact);

	/**
	 * Searches for actively loaded worlds that matches the given name. <br/> <br/> The implementation is identical to iterating over {@link #getWorlds()} and checking for a world that matches {@link
	 * World#getName()} <br/> <br/>
	 *
	 * Worlds are added to the list immediately, but removed at the end of a tick.
	 *
	 * @param name of the world to search for, or part of it
	 * @return a collection of worlds that matched the name
	 */
	@LiveRead
	@SnapshotRead
	public Collection<World> matchWorld(String name);

	/**
	 * Searches for an actively loaded world has the given {@link UUID}. <br/> <br/> The implementation is identical to iterating over {@link #getWorlds()} and checking for a world that matches {@link
	 * World#getUID()}. <br/> <br/>
	 *
	 * Worlds are added to the list immediately, but removed at the end of a tick.
	 *
	 * @param uid of the world to search for
	 * @return {@link World} if found, else null
	 */
	@LiveRead
	@SnapshotRead
	public World getWorld(UUID uid);

	/**
	 * Gets a List of all currently loaded worlds <br/> Worlds are added to the list immediately, but removed at the end of a tick.
	 *
	 * @return {@link Collection} of actively loaded worlds
	 */
	@LiveRead
	@SnapshotRead
	@Override
	public Collection<World> getWorlds();

	/**
	 * Loads a {@link World} with the given name and {@link WorldGenerator}<br/> If the world doesn't exist on disk, it creates it.<br/> <br/> if the world is already loaded, this functions the same as
	 * {@link #getWorld(String)}
	 *
	 * @param name Name of the world
	 * @param generator World Generator
	 * @return {@link World} loaded or created.
	 */
	@LiveRead
	public World loadWorld(String name, WorldGenerator generator);

	/**
	 * Unloads this world from memory. <br/> <br/> <b>Note: </b>Worlds can not be unloaded if players are currently on them.
	 *
	 * @param name of the world to unload
	 * @param save whether or not to save the world state to file
	 * @return true if the world was unloaded, false if not
	 */
	public boolean unloadWorld(String name, boolean save);

	/**
	 * Unloads this world from memory. <br/> <br/> <b>Note: </b>Worlds can not be unloaded if players are currently on them.
	 *
	 * @param world to unload
	 * @param save whether or not to save the world state to file
	 * @return true if the world was unloaded, false if not
	 */
	public boolean unloadWorld(World world, boolean save);

	/**
	 * Initiates a save of the server state, including configuration files. <br/> <br/> It will save the state of the world, if specificed, and the state of players, if specified.
	 *
	 * @param worlds true to save the state of all active worlds
	 * @param players true to save the state of all active players
	 */
	public void save(boolean worlds, boolean players);

	/**
	 * Gets the world folders which match the world name.
	 *
	 * @param worldName to match the world folders with
	 * @return the world folders that match the world name
	 */
	public Collection<File> matchWorldFolder(String worldName);

	/**
	 * Gets all the individual world folders where world data is stored. <br/> <br/> This includes offline worlds.
	 *
	 * @return a list of available world folders
	 */
	public List<File> getWorldFolders();

	/**
	 * Gets the folder that contains the world save data. <br/> <br/> If the folder is unusued, the file path will be '.'
	 *
	 * @return world folder
	 */
	public File getWorldFolder();

	public ServerSession newSession(Channel c);

	/**
	 * Gets the server's name
	 * @return server name
	 */
	public String getName();
}
