/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.  
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license, 
 * including the MIT license.
 */
package org.spout.api;

import java.io.File;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.spout.api.command.Command;
import org.spout.api.command.CommandSource;
import org.spout.api.event.EventManager;
import org.spout.api.event.EventSource;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.World;
import org.spout.api.inventory.Recipe;
import org.spout.api.player.Player;
import org.spout.api.plugin.Platform;
import org.spout.api.plugin.Plugin;
import org.spout.api.plugin.PluginManager;
import org.spout.api.protocol.bootstrap.BootstrapProtocol;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.SessionRegistry;
import org.spout.api.scheduler.Scheduler;
import org.spout.api.util.Named;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;

/**
 * Represents the abstract, non-specific implementation of Minecraft.
 */
public interface Game extends Named, EventSource {

	/**
	 * Gets the name of this game's implementation
	 *
	 * @return name of the implementation
	 */

	public String getName();

	/**
	 * Gets the build version of this game's implementation
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
	 * {@link #getOnlinePlayers()} and invoking {@link Player#sendMessage(String)} for
	 * each player.
	 *
	 * @param message to send
	 */
	public void broadcastMessage(String message);

	/**
	 * Gets singleton instance of the plugin manager, used to interact with
	 * other plugins and register events.
	 *
	 * @return plugin manager instance.
	 */
	public PluginManager getPluginManager();

	/**
	 * Gets the logger instance that is used to write to the console.
	 *
	 * It should be identical to Logger.getLogger("minecraft");
	 *
	 * @return logger
	 */
	public Logger getLogger();

	/**
	 * Sends a command from the given command source. The command will be
	 * handled as if the sender has sent it itself.
	 *
	 * @param source that is responsible for the command
	 * @param commandLine text
	 * @return true if dispatched
	 */
	public void processCommand(CommandSource source, String commandLine);

	/**
	 * Gets the update folder. The update folder is used to safely update
	 * plugins at the right moment on a plugin load.
	 *
	 * The update folder name is relative to the plugins folder.
	 *
	 * @return The name of the update folder
	 */
	public File getUpdateFolder();

	/**
	 * Gets the config folder for the game
	 * 
	 * It's in the server root
	 * @return 
	 */
	public File getConfigFolder();

	/**
	 * Gets the player by the given username. <br/>
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
	 * The implementation is identical to iterating over {@link #getWorlds()}
	 * and checking for a world that matches {@link World#getName()}. <br/>
	 * <br/>
	 * 
	 * Worlds are added to the list immediately, but removed at the end of a tick.
	 *
	 * @param name of the world to search for
	 * @return world if found, else null
	 */
	@LiveRead
	@SnapshotRead
	public World getWorld(String name);

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
	 * @return world if found, else null
	 */
	@LiveRead
	@SnapshotRead
	public World getWorld(UUID uid);

	/**
	 * Gets a List of actively loaded worlds
	 * 
	 * Worlds are added to the list immediately, but removed at the end of a tick.
	 *
	 * @return a {@link List} of actively loaded worlds
	 */
	@LiveRead
	@SnapshotRead
	public Collection<World> getWorlds();
	
	/**
	 * Loads a world with the given name and generator
	 * If the world doesn't exist on disk, it creates it.
	 * 
	 * if the world is already loaded, this functions the same as {@link #getWorld(String)}
	 * 
	 * @param name Name of the world
	 * @param generator World Generator
	 * @return 
	 */
	@LiveRead
	public World loadWorld(String name, WorldGenerator generator);

	/**
	 * Initiates a save of the server state, including configuration files.
	 *
	 * It will save the state of the world, if specificed, and the state of
	 * players, if specified.
	 *
	 * @param worlds true to save the state of all active worlds
	 * @param players true to save the state of all active players
	 */
	public void save(boolean worlds, boolean players);

	/**
	 * Registers the recipe with the recipe database.
	 *
	 * @param recipe to register
	 * @return true if the recipe was registered, false if there was a conflict
	 *         with an existing recipe.
	 */
	//public boolean registerRecipe(Recipe recipe);

	/**
	 * Ends this game instance safely. All worlds, players, and configuration
	 * data is saved, and all threads are ended cleanly.
	 */
	public void stop();

	/**
	 * Gets the folder that contains the world save data.
	 *
	 * If the folder is unusued, the file path will be '.'
	 *
	 * @return world folder
	 */
	public File getWorldFolder();

	/**
	 * Returns the game's root {@link Command}.
	 *
	 * All command registration and execution is performed through here.
	 *
	 * @return the {@link Game}'s root {@link Command}
	 */
	public Command getRootCommand();

	/**
	 * Returns the game's {@link EventManager} Event listener registration and
	 * calling is handled through this. ß
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
	 * Creates a new Session
	 * 
	 * @param channel the associated channel
	 * @return the session
	 */
	public Session newSession(Channel channel);
	
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
	 * Sets the default world generator for this game. Any worlds without a specific world generator will use this generator.
	 * 
	 * @param generator to use by default
	 */
	public void setDefaultGenerator(WorldGenerator generator);
	
	/**
	 * Gets the scheduler
	 * 
	 * @return the scheduler
	 */
	public Scheduler getScheduler();

	/**
	 * Registers a recipe to this games recipe database, then stores the recipe in the associated plugins recipe.yml.
	 * If a recipe for that plugin of that name already exists, it will update the database and the recipe.yml
	 * 
	 * @param recipe to register
	 */
	public void addRecipe(Recipe recipe);
	
	/**
	 * Gets a recipe registered to this games recipe database, based on the plugin and name of the recipe.
	 * 
	 * @param plugin that the recipe belongs to
	 * @param recipe name
	 * @return the recipe if it's found, otherwise null
	 */
	public Recipe getRecipe(Plugin plugin, String recipe);
	
	/**
	 * Removes a recipe from the games recipes database, then returns the instance of it if you want to back it up.
	 * 
	 * *WARNING*
	 * This will also remove the recipe from the plugins recipe.yml!
	 * It returns a reference to the removed recipe if you want to back it up for safe keeping still.
	 * *WARNING*
	 * 
	 * @param plugin that the recipe belongs to
	 * @param recipe name
	 * @return recipe that was removed
	 */
	public Recipe removeRecipe(Plugin plugin, String recipe);

    /**
     * Returns the bootstrap protocol for {@code address}
     * @param address The address
     * @return The protocol
     */
    public BootstrapProtocol getBootstrapProtocol(SocketAddress address);
}