/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
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
package org.getspout.api.player;

import org.getspout.api.command.CommandSource;
import org.getspout.api.entity.Entity;
import org.getspout.api.protocol.Session;

import java.net.InetAddress;
import org.getspout.api.data.DataSubject;
import org.getspout.api.permissions.PermissionsSubject;

public interface Player extends CommandSource, PermissionsSubject, DataSubject {
	
	/**
	 * Gets the player's name
	 * 
	 * @return the player's name
	 */
	public String getName();

	/**
	 * Sends a message as if the player had typed it into their chat gui.
	 *
	 * @param message The message to send
	 */
	public void chat(String message);
	
	/**
	 * Gets the entity corresponding to the player
	 * 
	 * @return the entity, or null if the player is offline
	 */
	public Entity getEntity();
	
	/**
	 * Gets the session associated with the Player.
	 * 
	 * @return the session, or null if the player is offline
	 */
	public Session getSession();
	
	/**
	 * Gets if the player is online
	 * 
	 * @return true if online
	 */
	public boolean isOnline();

	/**
	 * Gets the sessions address
	 * This is equivalent to getSession().getAddress().getAddress();
	 * @return The session's address
	 */
	public InetAddress getAddress();
	
	
}
