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
package org.spout.api.entity;

import java.net.InetAddress;
import java.util.List;

import org.spout.api.command.CommandSource;
import org.spout.api.component.entity.PlayerNetworkComponent;
import org.spout.api.entity.state.PlayerInputState;
import org.spout.api.protocol.Session;
import org.spout.api.util.thread.annotation.Threadsafe;

public interface Player extends CommandSource, Entity {
	/**
	 * Gets the player's name.
	 *
	 * @return the player's name
	 */
	@Threadsafe
	@Override
	public String getName();

	/**
	 * Gets the player's display name.
	 *
	 * @return the player's display name
	 */
	@Threadsafe
	public String getDisplayName();

	/**
	 * Sets the player's display name.
	 *
	 * @param name the player's new display name
	 */
	@Threadsafe
	public void setDisplayName(String name);

	/**
	 * Gets if the player is online
	 *
	 * @return true if online
	 */
	public boolean isOnline();

	/**
	 * Kicks the player without giving a reason, or forcing it.
	 */
	public void kick();

	/**
	 * Kicks the player for the given reason.
	 *
	 * @param reason the message to send to the player.
	 */
	public void kick(String reason);

	/**
	 * Bans the player without giving a reason.
	 */
	public void ban();

	/**
	 * Bans the player for the given reason.
	 *
	 * @param kick whether to kick or not
	 */
	public void ban(boolean kick);

	/**
	 * Bans the player for the given reason.
	 *
	 * @param kick whether to kick or not
	 * @param reason for ban
	 */
	public void ban(boolean kick, String reason);

	/**
	 * Gets the current input state of the player
	 *
	 * @return current input state
	 */
	public PlayerInputState input();

	/**
	 * Immediately saves the players state to disk
	 *
	 * @return true if successful
	 */
	public boolean save();

	/**
	 * Gets the {@link PlayerNetworkComponent} of the player
	 *
	 * @return The {@link PlayerNetworkComponent}
	 */
	@Override
	public PlayerNetworkComponent getNetwork();

	/**
	 * If an entity is set as invisible, it will not be sent to the client.
	 */
	public void setVisible(Entity entity, boolean visible);

	/**
	 * Retrieves a list of all invisible {@link Entity}'s to the player
	 *
	 * @return {@link List<{@link Entity}>} of invisible {@link Entity}'s
	 */
	public List<Entity> getInvisibleEntities();

	/**
	 * Returns true if the {@link Entity} provided is invisible this this {@link Player}
	 *
	 * @param entity Entity to check if invisible to the {@link Player}
	 * @return true if the {@link Entity} is invisible
	 */
	public boolean isInvisible(Entity entity);

	/**
	 * Processes the input of this player
	 *
	 * @param state The {@link Player}'s input state
	 */
	public void processInput(PlayerInputState state);

	/**
	 * Creates an immutable snapshot of the player state at the time the method is called
	 *
	 * @return immutable snapshot
	 */
	@Override
	public PlayerSnapshot snapshot();

	/**
	 * Sends a command to be processed on the opposite Platform.
	 * This is basically a shortcut method to prevent the need to register a command locally with a {@link Command.NetworkSendType} of {@code SEND}.
	 *
	 * @param command to send
	 * @param args to send
	 */
	public void sendCommand(String command, String... args);
}
