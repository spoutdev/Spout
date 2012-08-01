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
package org.spout.api.protocol;

public abstract class MessageHandler<T extends Message> {
	
	/**
	 * Handles a message. If the message is a one way method, then this method
	 * can be overriden.
	 *
	 * Otherwise, it will call handleServer or handleClient as required.
	 *
	 * @param upstream true, if the connection is to a server
	 * @param session the network session
	 * @param player the player
	 * @param message the message that was received
	 */
	public void handle(boolean upstream, Session session, T message) {
		if (upstream) {
			handleClient(session, message);
		} else {
			handleServer(session, message);
		}
	}
	
	/**
	 * Handles a message.
	 *
	 * If handle is not overriden, then this method is called when a packet is
	 * received from the client by the server.
	 *
	 * @param session the network session
	 * @param player the player
	 * @param message the message that was received
	 */
	public void handleServer(Session session, T message) {
	}

	/**
	 * Handles a message.
	 *
	 * If handle is not overriden, then this method is called when a packet is
	 * received from the server by the client.
	 *
	 * @param session the network session
	 * @param player the player
	 * @param message the message that was received
	 */
	public void handleClient(Session session, T message) {
	}
}
