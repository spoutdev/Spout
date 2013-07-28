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

import java.util.Set;

import org.spout.api.entity.Entity;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.set.concurrent.TSyncIntHashSet;

public abstract class ServerNetworkSynchronizer extends NetworkSynchronizer {
	protected final static int CHUNKS_PER_TICK = 20;
	protected int viewDistance;
	protected final int blockMinimumViewDistance;
	private boolean removed = false;
	//Holds all entities that have ever been sync'd to this Synchronizer
	protected final TSyncIntHashSet synchronizedEntities = new TSyncIntHashSet();

	public ServerNetworkSynchronizer(Session session, int minViewDistance) {
		super(session);
		if (player != null) {
			viewDistance = player.getViewDistance() >> Chunk.BLOCKS.BITS;
		} else {
			viewDistance = minViewDistance;
		}

		blockMinimumViewDistance = minViewDistance * Chunk.BLOCKS.SIZE;
	}

	/**
	 * No subclass should ever modify the value of removed
	 */
	public final boolean isRemoved() {
		return removed;
	}

	public final void onRemoved() {
		TickStage.checkStage(TickStage.FINALIZE);
		removed = true;
		clearObservers();
	}

	protected void clearObservers() {
		TickStage.checkStage(TickStage.FINALIZE);
	}

	public void forceRespawn() {
	}

	public void forceSync() {
	}

	/**
	 * Called just before the pre-snapshot stage.<br> This stage can make changes but they should be checked to make sure they are non-conflicting.
	 */
	@Override
	public void finalizeTick() {
		if (removed) {
			throw new IllegalStateException("Called finalizeTick() on a removed player.");
		}
	}

	@Override
	public void preSnapshot() {
		if (removed) {
			// TODO: confirm this is never going to be called and remove it
			throw new IllegalStateException("Removed in preSnapshot()");
		}
	}

	/**
	 * Returns a copy of all currently active sent chunks to this player
	 *
	 * @return active chunks
	 */
	public abstract Set<Chunk> getActiveChunks();

	/**
	 * Instructs the client to update the entities state and position<br><br>
	 *
	 * @param e the entity
	 * @param liveTransform the live transform (latest) for the entity
	 * @param spawn is True when the entity just spawned
	 * @param destroy is True when the entity just got destroyed
	 * @param update is True when the entity is being updated
	 */
	public void syncEntity(Entity e, Transform liveTransform, boolean spawn, boolean destroy, boolean update) {
		if (spawn) {
			synchronizedEntities.add(e.getId());
		} else if (destroy) {
			synchronizedEntities.remove(e.getId());
		}
	}

	public boolean hasSpawned(Entity e) {
		return synchronizedEntities.contains(e.getId());
	}
}
