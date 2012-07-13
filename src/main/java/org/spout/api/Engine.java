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

import java.io.File;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.jboss.netty.channel.group.ChannelGroup;

import org.spout.api.command.Command;
import org.spout.api.command.CommandSource;
import org.spout.api.command.RootCommand;
import org.spout.api.entity.Entity;
import org.spout.api.event.EventManager;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.World;
import org.spout.api.inventory.RecipeManager;
import org.spout.api.permissions.PermissionsSubject;
import org.spout.api.player.Player;
import org.spout.api.plugin.Platform;
import org.spout.api.plugin.PluginManager;
import org.spout.api.plugin.ServiceManager;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.SessionRegistry;
import org.spout.api.scheduler.Scheduler;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.util.Named;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;

/**
 * Represents the core of an implementation of an engine (powers a game).
 */
public interface Engine extends Named {
	/**
	 * The permissions to be used for standard broadcasts. Implementations should register
	 * this permissions with {@link org.spout.api.permissions.DefaultPermissions}
	 */
	public static final String STANDARD_BROADCAST_PERMISSION = "spout.broadcast.standard";

	/**
	 * Gets the name of this game's implementation
	 *
	 * @return name of the implementation
	 */
	public String getName();

	/**
	 * Gets the version of this game's implementation
	 *
	 * @return build version
	 */
	public String getVersion();

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
	 * Returns the current IP address.
	 *
	 * If this game is a server, this is the address being listened on.
	 *
	 * If this game is a client, and connected to a server, this is the address
	 * connected to.
	 *
	 * If neither, this is null.
	 *
	 * Address may be in "x.x.x.x:port", "x.x.x.x", or null format.
	 *
	 * @return address
	 */
	public String getAddress();

	/**
	 * Returns all IP addresses in use.
	 *
	 * If this game is a server, this is the addresses being listened on.
	 *
	 * If this game is a client, and connected to a server, this is the address
	 * connected to.
	 *
	 * If neither, this is null.
	 *
	 * Address may be in "x.x.x.x:port", "x.x.x.x", or null format.
	 *
	 * @return address
	 */
	public String[] getAllAddresses();

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
	 * Broadcasts the given single-string message to all players.
	 * Should be implemented as <code>broadcastMessage(new Object[] {message})</code>
	 * This method is purely a workaround for the way Java chooses which method to call
	 * in ambiguous situations, which would result in calls with a single string intended for
	 * {@link #broadcastMessage(Object...)} having their string argument passed to
	 * {@link #broadcastMessage(String, Object...)} as the permission
	 *
 	 * @see #broadcastMessage(Object...)
	 * @param message The single-string message
	 */
	public void broadcastMessage(String message);

	/**
	 * Returns a Set of all permissions subjects with the provided node. Plugins wishing
	 * to modify the result of this event should listen to the {@link org.spout.api.event.server.permissions.PermissionGetAllWithNodeEvent} event.
	 *
	 * @param permission The permission to check
	 * @return Every {@link PermissionsSubject} with the specified node
	 */
	public Set<PermissionsSubject> getAllWithNode(String permission);

	/**
	 * Broadcasts the given message to all players
	 *
	 * The implementation of broadcast is identical to calling a {@link org.spout.api.event.server.permissions.PermissionGetAllWithNodeEvent}
	 * event, iterating over each element in getReceivers, invoking {@link CommandSource#sendMessage(Object...)} for
	 * each CommandSource.
	 *
	 * @param message to send
	 */
	public void broadcastMessage(String permission, Object... message);

	/**
	 * Gets singleton instance of the plugin manager, used to interact with
	 * other plugins and register events.
	 *
	 * @return plugin manager instance.
	 */
	public PluginManager getPluginManager();

	/**
	 * Gets the {@link Logger} instance that is used to write to the console.
	 *
	 * @return logger
	 */
	public Logger getLogger();

	/**
	 * Gets the update folder. The update folder is used to safely update
	 * plugins at the right moment on a plugin load.
	 *
	 * The update folder name is relative to the plugins folder.
	 *
	 * @return {@link File} of the update folder
	 */
	public File getUpdateFolder();

	/**
	 * Gets the configuration folder for the game
	 *
	 * @return {@link File} of the configuration folder
	 */
	public File getConfigFolder();

	/**
	 * Gets the folder which contains world, entity and player data.
	 *
	 * @return {@link File} of the data folder.
	 */
	public File getDataFolder();

	/**
	 * Gets the {@link Entity} with the matching unique id
	 * <br/> <br/>
	 * Performs a search on each world and then searches each world respectively
	 * for the entity, stopping when it is found, or after all the worlds have
	 * been searched upon failure.
	 *
	 * @param uid to search and match
	 * @return {@link entity} that matched the uid, or null if none was found
	 */
	@SnapshotRead
	public Entity getEntity(UUID uid);

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
	 * Searches for an actively loaded world that exactly matches the given
	 * name. <br/>
	 * <br/>
	 * If searching for the exact name, this method will iterate and check for
	 * exact matches. <br/>
	 * <br/>
	 * Otherwise, this method will iterate over over all worlds and find the closest match
	 * to the given name, by comparing the length of other player names that
	 * start with the given parameter. <br/>
	 * <br/>
	 *
	 * Worlds are added to the list immediately, but removed at the end of a tick.
	 *
	 * @param name of the world to search for
	 * @param exact Whether to use exact lookup
	 * @return world if found, else null
	 */
	@LiveRead
	@SnapshotRead
	public World getWorld(String name, boolean exact);

	/**
	 * Searches for an actively loaded world that exactly matches the given
	 * name. <br/>
	 * <br/>
	 * The implementation is identical to iterating over {@link #getWorlds()}
	 * and checking for a world that matches {@link World#getName()}. <br/>
	 * <br/>
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
	 * Searches for actively loaded worlds that matches the given
	 * name. <br/>
	 * <br/>
	 * The implementation is identical to iterating over {@link #getWorlds()}
	 * and checking for a world that matches {@link World#getName()} <br/>
	 * <br/>
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
	 * Searches for an actively loaded world has the given {@link UUID}. <br/>
	 * <br/>
	 * The implementation is identical to iterating over {@link #getWorlds()}
	 * and checking for a world that matches {@link World#getUID()}. <br/>
	 * <br/>
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
	 * Gets a List of all currently loaded worlds
	 *
	 * Worlds are added to the list immediately, but removed at the end of a tick.
	 *
	 * @return {@link Collection} of actively loaded worlds
	 */
	@LiveRead
	@SnapshotRead
	public Collection<World> getWorlds();

	/**
	 * Loads a {@link World} with the given name and {@link WorldGenerator}<br/>
	 * If the world doesn't exist on disk, it creates it.<br/>
	 * <br/>
	 * if the world is already loaded, this functions the same as {@link #getWorld(String)}
	 *
	 * @param name Name of the world
	 * @param generator World Generator
	 * @return {@link World} loaded or created.
	 */
	@LiveRead
	public World loadWorld(String name, WorldGenerator generator);

	/**
	 * Unloads this world from memory. <br/>
	 * <br/>
	 * <b>Note: </b>Worlds can not be unloaded if players are currently on them.
	 *
	 * @param name of the world to unload
	 * @param save whether or not to save the world state to file
	 * @return true if the world was unloaded, false if not
	 */
	public boolean unloadWorld(String name, boolean save);

	/**
	 * Unloads this world from memory. <br/>
	 * <br/>
	 * <b>Note: </b>Worlds can not be unloaded if players are currently on them.
	 *
	 * @param world to unload
	 * @param save whether or not to save the world state to file
	 * @return true if the world was unloaded, false if not
	 */
	public boolean unloadWorld(World world, boolean save);

	/**
	 * Initiates a save of the server state, including configuration files. <br/>
	 * <br/>
	 * It will save the state of the world, if specificed, and the state of
	 * players, if specified.
	 *
	 * @param worlds true to save the state of all active worlds
	 * @param players true to save the state of all active players
	 */
	public void save(boolean worlds, boolean players);

	/**
	 * Registers the recipe with the recipe database. <br/>
	 *
	 * @param recipe to register
	 * @return true if the recipe was registered, false if there was a conflict
	 *         with an existing recipe.
	 */
	//public boolean registerRecipe(Recipe recipe);

	/**
	 * Ends this game instance safely. All worlds, players, and configuration
	 * data is saved, and all threads are ended cleanly.<br/>
	 * <br/>
	 * Players will be sent a default disconnect message.
	 */
	public void stop();

	/**
	 * Ends this game instance safely. All worlds, players, and configuration
	 * data is saved, and all threads are ended cleanly.
	 * <br/>
	 * If any players are connected, will kick them with the given reason.
	 *
	 * @param reason for stopping the game instance
	 */
	public void stop(String reason);

	/**
	 * Gets the world folders which match the world name.
	 *
	 * @param name to match the world folders with
	 * @return the world folders that match the world name
	 */
	public Collection<File> matchWorldFolder(String worldName);

	/**
	 * Gets all the individual world folders where world data is stored. <br/>
	 * <br/>
	 * This includes offline worlds.
	 *
	 * @return a list of available world folders
	 */
	public List<File> getWorldFolders();

	/**
	 * Gets the folder that contains the world save data. <br/>
	 * <br/>
	 * If the folder is unusued, the file path will be '.'
	 *
	 * @return world folder
	 */
	public File getWorldFolder();

	/**
	 * Returns the game's root {@link Command}. <br/>
	 * <br/>
	 * All command registration and execution is performed through here.
	 *
	 * @return the {@link Engine}'s root {@link Command}
	 */
	public RootCommand getRootCommand();

	/**
	 * Returns the game's {@link EventManager} Event listener registration and
	 * calling is handled through this.
	 *
	 * @return Our EventManager instance
	 */
	public EventManager getEventManager();

	/**
	 * Returns the {@link Platform} that the game is currently running on.
	 *
	 * @return current platform type
	 */
	public Platform getPlatform();

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
	 * Gets the default world generator for this game. Specific generators can be specified when loading new worlds.
	 *
	 * @return default world generator.
	 */
	public WorldGenerator getDefaultGenerator();

	/**
	 * Gets the scheduler
	 *
	 * @return the scheduler
	 */
	public Scheduler getScheduler();

	/**
	 * Gets the task manager responsible for parallel region tasks.
	 * <br/>
	 * All tasks are submitted to all loaded regions at the start of the next tick.<br/>
	 * <br/>
	 * Repeating tasks are also submitted to all new regions when they are created.<br/>
	 * Repeated tasks are NOT guaranteed to happen in the same tick for all regions,
	 * as each task is submitted individually to each Region.<br/>
	 * <br/>
	 * This task manager does not support async tasks.
	 * <br/>
	 * If the Runnable for the task is a ParallelRunnable, then a new instance of the Runnable will be created for each region.
	 *
	 * @return the parallel {@link TaskManager} for the engine
	 */
	public TaskManager getParallelTaskManager();

	/**
	 * Returns the bootstrap protocol for {@code address}
	 *
	 * @param address The address
	 * @return The protocol
	 */
	public Protocol getProtocol(SocketAddress address);

	/**
	 * Gets the service manager
	 *
	 * @return ServiceManager
	 */
	public ServiceManager getServiceManager();

	/**
	 * Gets the recipe manager
	 *
	 * @return RecipeManager
	 */
	public RecipeManager getRecipeManager();

	/**
	 * Returns true if the game is running in debug mode <br/>
	 * <br/>
	 * To start debug mode, start Spout with -debug
	 *
	 * @return true if server is started with the -debug flag, false if not
	 */
	public boolean debugMode();

	/**
	 * Gets the main thread that is used to manage all execution on the server. <br/>
	 * <br/>
	 * Note: Interrupting the main thread will lead to undetermined behavior.
	 * @return main thread
	 */
	public Thread getMainThread();

	/**
	 * Sets the default world.
	 *
	 * The first loaded world will be set as the default world automatically.
	 *
	 * New players start in the default world.
	 *
	 * @param world the default world
	 * @return true on success
	 */
	@DelayedWrite
	public boolean setDefaultWorld(World world);

	/**
	 * Gets the default {@link World}.
	 *
	 * @return the default world
	 */
	@SnapshotRead
	public World getDefaultWorld();

	/**
	 * Gets the name of the server's log file
	 *
	 * @return the log filename
	 */
	public String getLogFile();

	/**
	 * Gets an array of available commands from the command map.
	 *
	 * @return An array of all command names currently registered on the server.
	 */
	public String[] getAllCommands();

	/**
	 * Gets an abstract representation of the engine Filesystem.
	 *
	 * The Filesystem handles the loading of all resources.
	 *
	 * On the client, loading a resource will load the resource from the harddrive.
	 * On the server, it will notify all clients to load the resource, as well as provide a representation of that resource.
	 *
	 */
	public FileSystem getFilesystem();

	public void setVariable(String key, String value);

	public String getVariable(String key);

}
