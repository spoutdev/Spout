/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.util.bans;

import java.util.Set;

/**
 * Represents a system capable of managing player and IP bans and messages.
 */
public interface BanManager {
	/**
	 * Load the ban manager
	 */
	public void load();

	/**
	 * Check if a name is banned
	 *
	 * @param player
	 * @return if the name is banned
	 */
	public boolean isBanned(String player);

	/**
	 * Set a name as banned or unbanned
	 *
	 * @param player
	 * @param banned
	 * @return if the name's banned state was changed by this operation
	 */
	public boolean setBanned(String player, boolean banned);

	/**
	 * Returns a string set of currently banned names
	 *
	 * @return
	 */
	public Set<String> getBans();

	/**
	 * Returns the ban message for the provided name
	 *
	 * @param name
	 * @return
	 */
	public String getBanMessage(String name);

	/**
	 * Check if an address is banned
	 *
	 * @param address
	 * @return if the address is banned
	 */
	public boolean isIpBanned(String address);

	/**
	 * Set an address as banned or unbanned
	 *
	 * @param address
	 * @param banned
	 * @return if the address's banned state was changed by this operation
	 */
	public boolean setIpBanned(String address, boolean banned);

	/**
	 * Returns a string set of currently banned addresses
	 *
	 * @return
	 */
	public Set<String> getIpBans();

	/**
	 * Returns the ban message for the provided address
	 *
	 * @param address
	 * @return
	 */
	public String getIpBanMessage(String address);

	/**
	 * Return if a name or address is banned
	 *
	 * @param player
	 * @param address
	 * @return
	 */
	public boolean isBanned(String player, String address);
}
