/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api;

import java.util.Collection;
import java.util.List;

/**
 * Represents the server-specific implementation of Minecraft.
 */
public interface Server extends Game{
	
	/**
	 * Returns the name this server, if set.
	 * 
	 * @return server name
	 */
	public String getName();
	
	/**
	 * Returns true if this server is using a whitelist.
	 * 
	 * @return whitelist enabled
	 */
	public boolean isWhitelist();
	
	/**
	 * Sets the whitelist value of this server.
	 * 
	 * @param whitelist value to set
	 */
	public void setWhitelist(boolean whitelist);
	
	/**
	 * Reads the whitelist file from the disk, updating the players that are allowed to join where nessecary.
	 */
	public void updateWhitelist();
	
	/**
	 * Gets an array of all of the player names that are on the whitelist.
	 * 
	 * @return whitelisted player names
	 */
	public String[] getWhitelistedPlayers();
	
	/**
	 * Gets a list of all active and loaded worlds in memory
	 * 
	 * @return list of loaded worlds
	 */
	public List<World> getWorlds();
	
	/**
	 * Unloads this world from memory. 
	 * <br/><br/>
	 * <b>Note: </b>Worlds can not be unloaded if players are currently on them.
	 * 
	 * @param name of the world to unload
	 * @param save whether or not to save the world state to file
	 * @return true if the world was unloaded, false if not
	 */
	public boolean unloadWorld(String name, boolean save);
	
	/**
	 * Unloads this world from memory. 
	 * <br/><br/>
	 * <b>Note: </b>Worlds can not be unloaded if players are currently on them.
	 * 
	 * @param world to unload
	 * @param save whether or not to save the world state to file
	 * @return true if the world was unloaded, false if not
	 */
	public boolean unloadWorld(World world, boolean save);
	
	/**
	 * True if this server is checking minecraft.net's authentication servers to verify that players own a copy of the game.
	 * @return online mode.
	 */
	public boolean isOnlineMode();
	
	/**
	 * Gets an instance of the OfflinePlayer object for manipulation.
	 *  <br/><br/>
	 * Searches all online players first, and returns any online players that match the name. If no player online matches, an OfflinePlayer is created.
	 * 
	 * An offline player may be an instance of a {@link Player}.
	 * 
	 * @param name to search for
	 * @return OfflinePlayer
	 */
	public OfflinePlayer getOfflinePlayer(String name);
	
	/**
	 * Gets a collection of all banned IP's, in string format.
	 * 
	 * @return banned IP addresses
	 */
	public Collection<String> getIPBans();
	
	/**
	 * Adds the address to the ban list. Any players that log in with the address will be unable to join.
	 * @param address to ban
	 */
	public void ban(String address);
	
	/**
	 * Removes the address from the ban list. If the address was not on the ban list, nothing happens.
	 * @param address to unban
	 */
	public void unban(String address);
	
	/**
	 * Returns a collection of all banned players
	 * @return banned players
	 */
	public Collection<OfflinePlayer> getBannedPlayers();
	
	/**
	 * Returns a collection of all server operators
	 * @return operators
	 */
	public Collection<OfflinePlayer> getOps();
}
