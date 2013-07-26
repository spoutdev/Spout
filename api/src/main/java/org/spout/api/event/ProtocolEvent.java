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
package org.spout.api.event;

import java.util.List;

import org.spout.api.entity.Player;
import org.spout.api.protocol.Message;
import org.spout.api.util.list.concurrent.ConcurrentList;

public abstract class ProtocolEvent extends Event {
	private List<Message> messages = new ConcurrentList<>();
	private Player player;

	public ProtocolEvent(Player player) {
		this.player = player;
	}

	public ProtocolEvent() {
	}

	/**
	 * The returned List is mutable.
	 *
	 * @return the messages that will be sent or queued in MONITOR
	 */
	public List<Message> getMessages() {
		return messages;
	}

	/**
	 *
	 * @return the player that the event is for
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 *
	 * @param player the new player for this event
	 * @return this instance
	 */
	public ProtocolEvent setPlayer(Player player) {
		this.player = player;
		return this;
	}
}
