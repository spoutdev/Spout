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
package org.spout.api.protocol;

import org.spout.api.Platform;
import org.spout.api.Spout;

public abstract class MessageHandler<T extends Message> {
	/**
	 * Handles a message. If the message is a one way method, then this method can be overriden.
	 *
	 * Otherwise, it will call handleServer or handleClient as required.
	 *
	 * @param session the network session
	 * @param message the message that was received
	 */
	public void handle(Session session, T message) {
		if (message.requiresPlayer() && !session.hasPlayer()) {
			throw new IllegalStateException("Message sent when session has no player");
		}
		if (Spout.getPlatform() == Platform.CLIENT) {
			handleClient((ClientSession) session, message);
		} else {
			handleServer((ServerSession) session, message);
		}
	}

	/**
	 * Handles a message.
	 *
	 * If handle is not overriden, then this method is called when a packet is received from the client by the server.
	 *
	 * @param session the network session
	 * @param message the message that was received
	 */
	public void handleServer(ServerSession session, T message) {
		throw new UnsupportedOperationException("Tried to handle a message that did not have a handleServer implemented.");
	}

	/**
	 * Handles a message.
	 *
	 * If handle is not overriden, then this method is called when a packet is received from the server by the client.
	 *
	 * @param session the network session
	 * @param message the message that was received
	 */
	public void handleClient(ClientSession session, T message) {
		throw new UnsupportedOperationException("Tried to handle a message that did not have a handleClient implemented.");
	}
}
