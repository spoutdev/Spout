/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.event.player;

import java.net.InetAddress;

import org.spout.api.event.Event;
import org.spout.api.event.HandlerList;

/**
 * Stores details for players attempting to log in
 */
public class PlayerPreLoginEvent extends Event {
	private static HandlerList handlers = new HandlerList();

	private String name;

	private InetAddress address;

	/**
	 * Gets the player's name.
	 *
	 * @return the player's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the player's name.
	 *
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the player IP address.
	 *
	 * @return The IP address
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * Sets the player IP address.
	 *
	 * @param ipAddress The IP Address to set.
	 */
	public void setAddress(InetAddress ipAddress) {
		address = ipAddress;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
