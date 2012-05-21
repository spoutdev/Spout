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
package org.spout.api.event.player;

import org.spout.api.event.HandlerList;
import org.spout.api.player.Player;

/**
 * Stores details for players attempting to log in
 */
public class PlayerLoginEvent extends PlayerEvent {
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
	public void setMessage(final String message) {
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
	 * @param result New result for disallowing the player
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
