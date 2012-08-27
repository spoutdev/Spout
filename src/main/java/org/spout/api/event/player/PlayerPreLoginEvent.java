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

import java.net.InetAddress;

import org.spout.api.event.Event;
import org.spout.api.event.HandlerList;

/**
 * Called when a player is attempting to log in.<br/>
 * This is called prior to both the {@link PlayerLoginEvent} and the {@link PlayerJoinEvent}.
 */
public class PlayerPreLoginEvent extends Event {
	private static HandlerList handlers = new HandlerList();
	private final String name;
	private final InetAddress address;

	public PlayerPreLoginEvent(String n, InetAddress a) {
		this.name = n;
		this.address = a;
	}
	
	/**
	 * Gets the player's name that is attempting to login.
	 *
	 * @return the player's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the player IP address.
	 *
	 * @return The IP address
	 */
	public InetAddress getAddress() {
		return address;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
