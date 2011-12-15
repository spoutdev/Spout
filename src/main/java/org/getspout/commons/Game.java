package org.getspout.commons;

import java.io.File;
import java.util.Collection;
import java.util.logging.Logger;

import org.getspout.commons.command.AddonCommand;
import org.getspout.commons.command.CommandSender;
import org.getspout.commons.entity.Player;
import org.getspout.commons.plugin.PluginManager;

/**
 * Represents the abstract, non-specific implementation of Minecraft.
 */
public interface Game {

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
	public long getVersion();
	
	/**
	 * Gets all players currently active
	 * 
	 * @return array of all active players 
	 */
	public Player[] getPlayers();
	
	/**
	 * Gets the maximum number of players this game can host, or -1 if infinite
	 * @return max players
	 */
	public int getMaxPlayers();
	
	/**
	 * Returns the current IP address.
	 * 
	 *  If this game is a server, this is the address being broadcasted.
	 *  
	 *  If this game is a client, and connected to a server, this is the address connected to.
	 *  
	 *  If neither, this is null.
	 *  
	 *   Address may be in "x.x.x.x:port", "x.x.x.x", or null format.
	 * @return address
	 */
	public String getAddress();
	
	/**
	 * True if this game allows the Nether environment to exist.
	 * 
	 * @return whether the Nether exists
	 */
	public boolean hasNether();
	
	/**
	 * True if this game allows 'The End' environment to exist.
	 * 
	 * @return whether the Nether exists
	 */
	public boolean hasTheEnd();
	
	/**
	 * Broadcasts the given message to all players
	 * 
	 * The implementation of broadcast is identical to iterating over {@link #getPlayers()} and invoking {@link Player#sendMessage(String)} for each player.
	 * 
	 * @param message to send
	 */
	public void broadcastMessage(String message);

	/**
	 * Gets singleton instance of the plugin manager, used to interact with other plugins and register events.
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

	public AddonCommand getAddonCommand(String name);

	/**
	 * Sends a command from the given command sender. The command will be handled as if the sender has sent it itself.
	 * 
	 * @param sender that is responsible for the command
	 * @param commandLine text
	 * @return true if dispatched
	 */
	public boolean dispatchCommand(CommandSender sender, String commandLine);

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
	 * Gets a player by the given username.
	 *<br/><br/>
	 * This method will iterate over over all players and find the closest match to the given
	 * name, by comparing the length of other player names that start with the given parameter.
	 * <br/><br/>
	 * This method is case-insensitive.
	 * 
	 * @param name to look up
	 * @return Player if found, else null
	 */
	public Player getPlayer(String name);

	/**
	 * Gets the player bythe given username.
	 * <br/><br/>
	 * If searching for the exact name, this method will iterate and check for exact matches, ignoring case.
	 * <br/><br/>
	 * Otherwise, this method's implementation is described by {@link #getPlayer(String)}
	 *
	 * @param name to look up
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
}
