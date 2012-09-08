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
package org.spout.api.entity;

import java.net.InetAddress;

import org.spout.api.command.CommandSource;
import org.spout.api.entity.state.PlayerInputState;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.protocol.Session;
import org.spout.api.util.thread.Threadsafe;

public interface Player extends CommandSource, Entity {
	/**
	 * Gets the player's name. This method is thread-safe.
	 * @return the player's name
	 */
	@Override
	@Threadsafe
	public String getName();

	/**
	 * Gets the player's display name. This method is thread-safe.
	 * @return the player's display name
	 */
	@Threadsafe
	public String getDisplayName();

	/**
	 * Sets the player's display name. This method is thread-safe.
	 * @param name the player's new display name
	 */
	@Threadsafe
	public void setDisplayName(String name);

	/**
	 * Gets the NetworkSynchronizer associated with this player.<br>
	 * @return the synchronizer
	 */
	public NetworkSynchronizer getNetworkSynchronizer();

	/**
	 * Gets the session associated with the Player.
	 * @return the session, or null if the player is offline
	 */
	public Session getSession();

	/**
	 * Gets if the player is online
	 * @return true if online
	 */
	public boolean isOnline();

	/**
	 * Gets the sessions address This is equivalent to
	 * getSession().getAddress().getAddress();
	 * @return The session's address
	 */
	public InetAddress getAddress();

	/**
	 * Kicks the player without giving a reason, or forcing it.
	 */
	public void kick();

	/**
	 * Kicks the player for the given reason.
	 * @param reason the message to send to the player.
	 */
	public void kick(Object... reason);

	/**
	 * Bans the player without giving a reason.
	 */
	public void ban();

	/**
	 * Bans the player for the given reason.
	 * @param kick whether to kick or not
	 */
	public void ban(boolean kick);

	/**
	 * Bans the player for the given reason.
	 * @param kick whether to kick or not
	 * @param reason for ban
	 */
	public void ban(boolean kick, Object... reason);

	/**
	 * Gets the current input state of the player
	 * @return current input state
	 */
	public PlayerInputState input();

	/**
	 * Immediately saves the players state to disk
	 * @return true if successful
	 */
	public boolean save();

	public void processInput(PlayerInputState state);
}
