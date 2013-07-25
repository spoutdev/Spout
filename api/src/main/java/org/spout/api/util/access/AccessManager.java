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
package org.spout.api.util.access;

import java.util.Collection;

public interface AccessManager {
	/**
	 * Load the access manager
	 */
	public void load();

	/**
	 * Returns true if this server is using a whitelist.
	 *
	 * @return whitelist enabled
	 */
	public boolean isWhitelistEnabled();

	/**
	 * Sets the whitelist value of this server.
	 *
	 * @param enabled value to set
	 */
	public void setWhitelistEnabled(boolean enabled);

	/**
	 * Gets an array of all of the player names that are on the whitelist.
	 *
	 * @return whitelisted player names
	 */
	public Collection<String> getWhitelistedPlayers();

	/**
	 * Whether or not the player is whitelisted.
	 *
	 * @param player in question
	 * @return true if whitelisted.
	 */
	public boolean isWhitelisted(String player);

	/**
	 * Whitelists a player
	 */
	public void whitelist(String player);

	/**
	 * Unwhitelists a player
	 */
	public void unwhitelist(String player);

	/**
	 * Unwhitelists a player
	 */
	public void unwhitelist(String player, boolean kick);

	/**
	 * Unwhitelists a player
	 */
	public void unwhitelist(String player, boolean kick, String reason);

	/**
	 * Gets the message that is displayed when a player is not whitelisted.
	 *
	 * @return whitelist message
	 */
	public String getWhitelistMessage();

	/**
	 * Sets the message that is displayed when a player is not whitelisted.
	 */
	public void setWhitelistMessage(String message);

	/**
	 * Bans the specified from the server.
	 *
	 * @param type of ban
	 * @param s to ban
	 */
	public void ban(BanType type, String s);

	/**
	 * Bans the specified from the server.
	 */
	public void ban(BanType type, String s, boolean kick);

	/**
	 * Bans the specified from the server.
	 */
	public void ban(BanType type, String s, boolean kick, String reason);

	/**
	 * Unbans the specified from the server.
	 */
	public void unban(BanType type, String s);

	/**
	 * Gets all banned of the designated type
	 *
	 * @return all bans
	 */
	public Collection<String> getBanned(BanType type);

	/**
	 * Whether or not the subject is banned
	 *
	 * @return true if banned
	 */
	public boolean isBanned(BanType type, String s);

	/**
	 * Gets the ban message for the designated type.
	 *
	 * @return message
	 */
	public String getBanMessage(BanType type);

	/**
	 * Sets the ban message
	 */
	public void setBanMessage(BanType type, String message);
}
