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
package org.spout.api.event.player;

import org.spout.api.entity.Player;
import org.spout.api.event.HandlerList;

/**
 * Called when a player is attempting to log in, this event can be disallowed to prevent the Player from logging in.<br/>
 */
public class PlayerLoginEvent extends AbstractPlayerEvent {
	private static HandlerList handlers = new HandlerList();
	private String message;
	private boolean allowed = true;

	public PlayerLoginEvent(Player p) {
		super(p);
	}

	public boolean isAllowed() {
		return allowed;
	}

	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}

	/**
	 * Gets the message to use if the player cannot log in.
	 *
	 * @return Current message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message to use if the player cannot log in.
	 *
	 * @param message The message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Allows the player to log in
	 */
	public void allow() {
		allowed = true;
	}

	/**
	 * Disallows the player from logging in, with the given reason
	 *
	 * @param message Kick message to display to the user
	 */
	public void disallow(String message) {
		allowed = false;
		this.message = message;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
