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

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.entity.Player;
import org.spout.api.event.ProtocolEvent;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.IntVector3;
import org.spout.api.protocol.reposition.NullRepositionManager;
import org.spout.api.protocol.reposition.RepositionManager;
import org.spout.api.util.OutwardIterator;

public abstract class NetworkSynchronizer {
	protected final Player player;
	protected final Session session;
	protected final AtomicReference<Protocol> protocol = new AtomicReference<Protocol>(null);
	private final AtomicReference<RepositionManager> rm = new AtomicReference<RepositionManager>(NullRepositionManager.getInstance());

	public NetworkSynchronizer(Session session) {
		this.session = session;
		player = session.getPlayer();
		if (player != null) {
			// TODO this shouldn't be needed because setObserver(true) is in SpoutPlayer; is there a reason?
			player.setObserver(true);
		}
	}

	public Player getPlayer() {
		return player;
	}

	public void callProtocolEvent(ProtocolEvent event) {
		session.getEngine().getEventManager().callEvent(event.setTarget(player));
	}

	/**
	 * Called just before the pre-snapshot stage.<br> This stage can make changes but they should be checked to make sure they are non-conflicting.
	 */
	public void finalizeTick() {
	}

	public void preSnapshot() {
	}

	/**
	 * Gets the viewable volume centred on the given chunk coordinates and the given view distance
	 */
	public Iterator<IntVector3> getViewableVolume(int cx, int cy, int cz, int viewDistance) {
		return new OutwardIterator(cx, cy, cz, viewDistance);
	}

	/**
	 * Test if a given chunk base is in the view volume for a given player chunk base point
	 *
	 * @return true if in the view volume
	 */
	public boolean isInViewVolume(Point playerChunkBase, Point testChunkBase, int viewDistance) {
		return testChunkBase.getManhattanDistance(playerChunkBase) <= (viewDistance << Chunk.BLOCKS.BITS);
	}

	/**
	 * Sets the protocol associated with this network synchronizer
	 */
	// TODO simplify this process; shouldn't need to be set
	public void setProtocol(Protocol protocol) {
		if (protocol == null) {
			throw new IllegalArgumentException("Protocol may not be null");
		} else if (!this.protocol.compareAndSet(null, protocol)) {
			throw new IllegalStateException("Protocol may not be set twice for a network synchronizer");
		}
	}

	/**
	 * Gets the reposition manager that converts local coordinates into remote coordinates
	 */
	public RepositionManager getRepositionManager() {
		return rm.get();
	}

	public void setRepositionManager(RepositionManager rm) {
		if (rm == null) {
			this.rm.set(NullRepositionManager.getInstance());
		} else {
			this.rm.set(rm);
		}
	}
}
