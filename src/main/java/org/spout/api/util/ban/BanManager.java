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
/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.api.util.ban;

import java.util.Set;

import org.spout.api.chat.ChatArguments;

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
	 * @param player
	 * @return if the name is banned
	 */
	public boolean isBanned(String player);

	/**
	 * Set a name as banned or unbanned
	 * @param player
	 * @param banned
	 */
	public void setBanned(String player, boolean banned);

	/**
	 * Returns a string set of currently banned names
	 * @return
	 */
	public Set<String> getBannedPlayers();

	/**
	 * Returns the default ban message
	 *
	 * @return default ban message
	 */
	public ChatArguments getBanMessage();

	/**
	 * Sets the default ban message
	 *
	 * @param message to set
	 */
	public void setBanMessage(Object... message);

	/**
	 * Check if an address is banned
	 * @param address
	 * @return if the address is banned
	 */
	public boolean isIpBanned(String address);

	/**
	 * Set an address as banned or unbanned
	 * @param address
	 * @param banned
	 * @return if the address's banned state was changed by this operation
	 */
	public void setIpBanned(String address, boolean banned);

	/**
	 * Returns a string set of currently banned addresses
	 * @return
	 */
	public Set<String> getBannedIps();

	/**
	 * Gets the default ban message for IP addresses
	 *
	 * @return default ban message
	 */
	public ChatArguments getIpBanMessage();

	/**
	 * Sets the default ban message for IP addresses
	 *
	 * @param message to set to default
	 */
	public void setIpBanMessage(Object... message);
}
