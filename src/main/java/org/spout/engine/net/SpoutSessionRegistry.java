/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.net;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.spout.api.player.Player;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.SessionRegistry;

/**
 * A list of all the sessions which provides a convenient {@link #pulse()}
 * method to pulse every session in one operation.
 *
 */
public final class SpoutSessionRegistry implements SessionRegistry {
	/**
	 * A list of the sessions.
	 */
	private final ConcurrentMap<SpoutSession, Boolean> sessions = new ConcurrentHashMap<SpoutSession, Boolean>();

	/**
	 * Pulses all the sessions not managed by a player.
	 */
	public void pulse() {
		for (SpoutSession session : sessions.keySet()) {
			Player player = session.getPlayer();
			if (player == null || player.getEntity() == null || player.getEntity().isDead()) {
				session.pulse();
			}
		}
	}

	/**
	 * Adds a new session.
	 *
	 * @param session The session to add.
	 */
	public void add(Session session) {
		if (session instanceof SpoutSession) {
			sessions.put((SpoutSession) session, true);
		} else if (session != null) {
			throw new IllegalArgumentException("This session registry can only handle SpoutSessions: ");
		}
	}

	/**
	 * Removes a session.
	 *
	 * @param session The session to remove.
	 */
	public void remove(Session session) {
		if (session instanceof SpoutSession) {
			sessions.remove(session);
		} else if (session != null) {
			throw new IllegalArgumentException("This session registry can only handle SpoutSessions");
		}
	}
}
