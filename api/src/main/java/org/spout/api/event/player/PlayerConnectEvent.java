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

import org.spout.api.event.Event;
import org.spout.api.event.HandlerList;
import org.spout.api.protocol.ServerSession;

/**
 * Called when a player connects to the server.
 */
public class PlayerConnectEvent extends Event {
	private static HandlerList handlers = new HandlerList();
	private final ServerSession session;
	private final String playerName;
	private final int viewDistance;

	public PlayerConnectEvent(ServerSession session, String playerName, int viewDistance) {
		this.session = session;
		this.playerName = playerName;
		this.viewDistance = viewDistance;
	}

	/**
	 * The player's session
	 *
	 * @return the session
	 */
	public ServerSession getSession() {
		return session;
	}

	/**
	 * @return the name of the player
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * How many {@link Chunk}s the player can see into the distance.
	 *
	 * @return how far the player can view.
	 */
	public int getViewDistance() {
		return viewDistance;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
