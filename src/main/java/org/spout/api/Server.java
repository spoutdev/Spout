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
package org.spout.api;

import java.io.File;
import java.net.SocketAddress;
import java.util.Collection;

import org.spout.api.geo.World;
import org.spout.api.player.Player;
import org.spout.api.protocol.bootstrap.BootstrapProtocol;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.SnapshotRead;

/**
 * Represents the server-specific implementation of Minecraft.
 */
public interface Server extends Game {
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
	 * Reads the whitelist file from the disk, updating the players that are
	 * allowed to join where nessecary.
	 */
	public void updateWhitelist();

	/**
	 * Gets an array of all of the player names that are on the whitelist.
	 *
	 * @return whitelisted player names
	 */
	public String[] getWhitelistedPlayers();

	/**
	 * Adds the given player to the list of whitelisted players
	 *
	 * @param player to whitelist
	 */
	public void whitelist(String player);

	/**
	 * Removes the given player from the list of whitelisted players
	 *
	 * @param player to remove from whitelist
	 */
	public void unWhitelist(String player);

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
	 * Gets the default world.
	 *
	 * @return the default world
	 */
	@SnapshotRead
	public World getDefaultWorld();

	/**
	 * Gets a collection of all banned IP's, in string format.
	 *
	 * @return banned IP addresses
	 */
	public Collection<String> getIPBans();

	/**
	 * Adds the address to the ban list. Any players that log in with the
	 * address will be unable to join.
	 *
	 * @param address to ban
	 */
	public void ban(String address);

	/**
	 * Removes the address from the ban list. If the address was not on the ban
	 * list, nothing happens.
	 *
	 * @param address to unban
	 */
	public void unban(String address);

	/**
	 * Returns a collection of all banned players
	 *
	 * @return banned players
	 */
	public Collection<Player> getBannedPlayers();

	/**
	 * Returns a collection of all server operators
	 *
	 * @return operators
	 */
	public Collection<Player> getOps();


	/**
	 * Gets the server's configuration directory
	 *
	 * @return the config directory
	 */
	public File getConfigDirectory();

	/**
	 * Gets the server's log file
	 *
	 * @return the log filename
	 */
	public String getLogFile();

	/**
	* Gets a list of available commands from the command map.
	*
	* @return A list of all commands at the time.
	*/
	public String[] getAllCommands();

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
	 * Binds the server to a certain address
	 * @param address The address to bind to.
	 * @param bootstrapProtocol The bootstrap protocol to use for connections to this binding
	 * @return true if successful
	 */
	public boolean bind(SocketAddress address, BootstrapProtocol bootstrapProtocol);
}
