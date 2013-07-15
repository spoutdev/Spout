/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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

import java.util.Collection;
import java.util.Set;

import org.spout.api.entity.Entity;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.Quaternion;
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

	/**
	 * Called just before the pre-snapshot stage.<br>
	 * This stage can make changes but they should be checked to make sure they
	 * are non-conflicting.
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

	protected boolean canSendChunk(Chunk c) {
		return true;
	}

	/**
	 * Returns a copy of all currently active sent chunks to this player
	 *
	 * @return active chunks
	 */
	public abstract Set<Chunk> getActiveChunks();

	/**
	 * Sends a chunk to the client.
	 *
	 * This method is called during the startSnapshot stage of the tick.
	 *
	 * This is a MONITOR method, for sending network updates, no changes should
	 * be made to the chunk
	 *
	 * While always called during the startSnapshot part of the tick, it may be called from
	 * multiple threads
	 *
	 * @param c the chunk
	 * @return chunks that were sent
	 */
	public final Collection<Chunk> sendChunk(Chunk c) {
		return sendChunk(c, false);
	}
	
	/**
	 * Sends a chunk to the client.
	 *
	 * This method is called during the startSnapshot stage of the tick.
	 *
	 * This is a MONITOR method, for sending network updates, no changes should
	 * be made to the chunk
	 *
	 * While always called during the startSnapshot part of the tick, it may be called from
	 * multiple threads
	 *
	 * @param c the chunk
	 * @param force forces sending of the chunk without checking the canSendChunk method
	 * @return true if the chunk was send
	 */
	public final Collection<Chunk> sendChunk(Chunk c, boolean force) {
		if (force || canSendChunk(c)) {
			return doSendChunk(c);
		} else {
			return null;
		}
	}

	protected abstract Collection<Chunk> doSendChunk(Chunk c);

	/**
	 * Inits a chunk on the client.
	 *
	 * This method is called during the startSnapshot stage of the tick.
	 *
	 * This is a MONITOR method, for sending network updates, no changes should
	 * be made to the chunk.
	 *
	 * All calls to this method are made from the thread managing the player
	 *
	 * @param p the base Point for the chunk
	 */
	//TODO: is this needed?
	protected abstract void initChunk(Point p);

	/**
	 * Frees a chunk on the client.
	 *
	 * This method is called during the startSnapshot stage of the tick.
	 *
	 * This is a MONITOR method, for sending network updates, no changes should
	 * be made to the chunk
	 *
	 * All calls to this method are made from the thread managing the player
	 *
	 * @param p the base Point for the chunk
	 */
	protected abstract void freeChunk(Point p);

	/**
	 * Sends the player's position to the client
	 *
	 * This method is called during the startSnapshot stage of the tick.
	 *
	 * This is a MONITOR method, for sending network updates, no changes should
	 * be made to the chunk
	 *
	 * @param p position to send
	 * @param rot rotation to send
	 */
	protected abstract void sendPosition(Point p, Quaternion rot);

	/**
	 * Called when the player's world changes.
	 *
	 * This method is called during the startSnapshot stage of the tick.
	 *
	 * This is a MONITOR method, for sending network updates, no changes should
	 * be made to the chunk
	 *
	 * @param world the world
	 */
	protected abstract void worldChanged(World world);

	/**
	 * Called when a block in a chunk that the player is observing changes.<br>
	 * <br>
	 * Note: The coordinates of the block are chunk relative and the world field
	 * is undefined.
	 *
	 * @param chunk the chunk
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 */
	public void updateBlock(Chunk chunk, int x, int y, int z) {
		updateBlock(chunk, x, y, z, chunk.getBlockMaterial(x, y, z), chunk.getBlockData(x, y, z));
	}

	/**
	 * Called when a block in a chunk that the player is observing changes.<br>
	 * <br>
	 * Note: The coordinates of the block are chunk relative and the world field
	 * is undefined.
	 *
	 * @param chunk the chunk
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @param material to send in the update
	 * @param data to send in the update
	 */
	public abstract void updateBlock(Chunk chunk, int x, int y, int z, BlockMaterial material, short data);

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
