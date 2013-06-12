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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.spout.api.Spout;
import org.spout.api.entity.Entity;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.IntVector3;
import org.spout.api.math.Quaternion;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.set.concurrent.TSyncIntHashSet;

public abstract class ServerNetworkSynchronizer extends NetworkSynchronizer {

	private final static int CHUNKS_PER_TICK = 20;

	private int viewDistance;
	private final int blockMinimumViewDistance;

	private Point lastChunkCheck =  Point.invalid;

	// Base points used so as not to load chunks unnecessarily
	private final Set<Point> chunkInitQueue = new LinkedHashSet<Point>();
	private final Set<Point> priorityChunkSendQueue = new LinkedHashSet<Point>();
	private final Set<Point> chunkSendQueue = new LinkedHashSet<Point>();
	private final Set<Point> chunkFreeQueue = new LinkedHashSet<Point>();

	private final Set<Point> initializedChunks = new LinkedHashSet<Point>();
	private final Set<Point> activeChunks = new LinkedHashSet<Point>();

	private boolean removed = false;
	private boolean first = true;
	private volatile boolean teleported = false;
	private volatile boolean teleportPending = false;
	private volatile boolean worldChanged = false;
	private Point lastPosition = null;
	private Point holdingPosition = null;
	private final LinkedHashSet<Chunk> observed = new LinkedHashSet<Chunk>();
	private final Set<Point> chunksToObserve = new LinkedHashSet<Point>();

	//Holds all entities that have ever been sync'd to this Synchronizer
	private final TSyncIntHashSet synchronizedEntities = new TSyncIntHashSet();
	
	protected int tickCounter = 0;

	public ServerNetworkSynchronizer(Session session, int minViewDistance) {
		super(session);
		if (player != null) {
			viewDistance = player.getViewDistance() >> Chunk.BLOCKS.BITS;
		} else {
			viewDistance = minViewDistance;
		}

		blockMinimumViewDistance = minViewDistance * Chunk.BLOCKS.SIZE;
	}

	public void setRespawned() {
		first = true;
		worldChanged = true;
		setPositionDirty();
	}

	public void setPositionDirty() {
		teleported = true;
		teleportPending = true;
	}
	
	public boolean isTeleportPending() {
		return teleportPending;
	}
	
	public void clearTeleportPending() {
		if (!teleported) {
			teleportPending = false;
		}
	}

	public void onRemoved() {
		TickStage.checkStage(TickStage.FINALIZE);
		removed = true;
		clearObservers();
	}
	
	private void clearObservers() {
		TickStage.checkStage(TickStage.FINALIZE);
		chunksToObserve.clear();
		for (Point p : initializedChunks) {
			removeObserver(p);
		}
	}

	/**
	 * Called just before the pre-snapshot stage.<br>
	 * This stage can make changes but they should be checked to make sure they
	 * are non-conflicting.
	 */
	@Override
	public void finalizeTick() {
		tickCounter++;
		if (removed) {
			return;
		}

		//TODO: update chunk lists?
		final int prevViewDistance = viewDistance;
		final int currentViewDistance = player.getViewDistance() >> Chunk.BLOCKS.BITS;
		if (viewDistance != currentViewDistance) {
			viewDistance = currentViewDistance;
		}

		Point currentPosition = player.getScene().getPosition();
		if (currentPosition != null) {
			if (prevViewDistance != currentViewDistance || worldChanged || (!currentPosition.equals(lastChunkCheck) &&	currentPosition.getManhattanDistance(lastChunkCheck) > Chunk.BLOCKS.SIZE >> 1)) {
				checkChunkUpdates(currentPosition);
				lastChunkCheck = currentPosition;
				worldChanged = false;
			}
			if (first || lastPosition == null || lastPosition.getWorld() != currentPosition.getWorld()) {
				clearObservers();
				worldChanged = true;
				setPositionDirty();
			}

		}

		lastPosition = currentPosition;

		if (!teleportPending) {
			holdingPosition = currentPosition;
		}

		if (!worldChanged) {
			for (Point p : chunkFreeQueue) {
				if (initializedChunks.contains(p)) {
					removeObserver(p);
				}
			}

			for (Point p : chunkInitQueue) {
				if (!initializedChunks.contains(p)) {
					addObserver(p);
				}
			}

			checkObserverUpdateQueue();
		}

	}
	
	
	/**
	 * Resets all chunk stores for the client.  This method is only called during the pre-snapshot part of the tick.
	 */
	protected void resetChunks() {
		priorityChunkSendQueue.clear();
		chunkSendQueue.clear();
		chunkFreeQueue.clear();
		chunkInitQueue.clear();
		activeChunks.clear();
		initializedChunks.clear();
		lastChunkCheck = Point.invalid;
		synchronizedEntities.clear();
	}
	
	private int chunksSent = 0;
	private Set<Point> unsendable = new HashSet<Point>();

	@Override
	public void preSnapshot() {
		if (removed) {
			removed = false;
			for (Point p : initializedChunks) {
				freeChunk(p);
			}
		} else {
			if (worldChanged) {
				first = false;
				Point ep = player.getScene().getPosition();
				resetChunks();
				worldChanged(ep.getWorld());
			} else if (!worldChanged) {
				
				unsendable.clear();
				
				for (Point p : chunkFreeQueue) {
					if (initializedChunks.remove(p)) {
						freeChunk(p);
						activeChunks.remove(p);
					}
				}

				chunkFreeQueue.clear();

				int modifiedChunksPerTick = (!priorityChunkSendQueue.isEmpty() ? 4 : 1) * CHUNKS_PER_TICK;
				chunksSent = Math.max(0, chunksSent - modifiedChunksPerTick);

				for (Point p : chunkInitQueue) {
					if (initializedChunks.add(p)) {
						initChunk(p);
					}
				}

				chunkInitQueue.clear();
				
				Iterator<Point> i;
				
				i = priorityChunkSendQueue.iterator();
				while (i.hasNext() && chunksSent < CHUNKS_PER_TICK) {
					Point p = i.next();
					i = attemptSendChunk(i, priorityChunkSendQueue, p, unsendable);
				}
				
				if (!priorityChunkSendQueue.isEmpty()) {
					return;
				}

				if (teleported && !player.getScene().isTransformDirty()) {
					sendPosition(player.getScene().getPosition(), player.getScene().getRotation());
					teleported = false;
				}

				boolean tickTimeRemaining = Spout.getScheduler().getRemainingTickTime() > 0;

				i = chunkSendQueue.iterator();
				while (i.hasNext() && chunksSent < CHUNKS_PER_TICK && tickTimeRemaining) {
					Point p = i.next();
					i = attemptSendChunk(i, chunkSendQueue, p, unsendable);
					tickTimeRemaining = Spout.getScheduler().getRemainingTickTime() > 0;
				}
			}
		}

	}
	
	protected boolean canSendChunk(Chunk c) {
		return true;
	}

	private Iterator<Point> attemptSendChunk(Iterator<Point> i, Iterable<Point> queue, Point p, Set<Point> unsendable) {
		System.out.println("ATTEMPTING SEND CHUNK");
		Chunk c = p.getWorld().getChunkFromBlock(p, LoadOption.LOAD_ONLY);
		if (c == null) {
			unsendable.add(p);
			return i;
		}
		if (unsendable.contains(p)) {
			return i;
		}
		if (canSendChunk(c)) {
			Collection<Chunk> sent = sendChunk(c, true);
			activeChunks.add(c.getBase());
			i.remove();
			if (sent != null) {
				boolean updated = false;
				for (Chunk s : sent) {
					Point base = s.getBase();
					boolean removed = priorityChunkSendQueue.remove(base);
					removed |= chunkSendQueue.remove(base);
					if (removed) {
						updated = true;
						if (initializedChunks.contains(base)) {
							activeChunks.add(base);
						}
						chunksSent++;
					}
				}
				if (updated) {
					i = queue.iterator();
				}
			}
			chunksSent++;

		} else {
			unsendable.add(p);
		}
		return i;
	}

	private void checkObserverUpdateQueue() {
		Iterator<Point> i = chunksToObserve.iterator();
		while (i.hasNext()) {
			Point p = i.next();
			if (!chunkInitQueue.contains(p) && !this.initializedChunks.contains(p)) {
				i.remove();
			} else {
				Chunk c = p.getWorld().getChunkFromBlock(p, LoadOption.NO_LOAD);
				if (c != null) {
					addObserver(c);
					i.remove();
				}
			}
		}
	}

	private void addObserver(Point p) {
		Chunk c = p.getWorld().getChunkFromBlock(p, LoadOption.NO_LOAD);
		if (c != null) {
			addObserver(c);
		} else {
			chunksToObserve.add(p);
		}
	}

	private void addObserver(Chunk c) {
		observed.add(c);
		c.refreshObserver(player);
	}

	private void removeObserver(Point p) {
		Chunk c = p.getWorld().getChunkFromBlock(p, LoadOption.NO_LOAD);
		if (c != null) {
			removeObserver(c);
		}
		chunksToObserve.remove(p);
	}

	private void removeObserver(Chunk c) {
		observed.remove(c);
		c.removeObserver(player);
	}

	private void checkChunkUpdates(Point currentPosition) {

		// Recalculating these
		priorityChunkSendQueue.clear();
		chunkSendQueue.clear();
		chunkFreeQueue.clear();
		chunkInitQueue.clear();

		World world = currentPosition.getWorld();
		int bx = (int) currentPosition.getX();
		int by = (int) currentPosition.getY();
		int bz = (int) currentPosition.getZ();
		
		int cx = bx >> Chunk.BLOCKS.BITS;
		int cy = by >> Chunk.BLOCKS.BITS;
		int cz = bz >> Chunk.BLOCKS.BITS;

		Point playerChunkBase = Chunk.pointToBase(currentPosition);
		Point playerHoldingChunkBase = holdingPosition == null ? null : Chunk.pointToBase(holdingPosition);

		for (Point p : initializedChunks) {
			if (!isInViewVolume(p, playerChunkBase, viewDistance)) {
				if (playerHoldingChunkBase == null || p.getMaxDistance(playerHoldingChunkBase) > blockMinimumViewDistance) {
					chunkFreeQueue.add(p);
				}
			}
		}

		Iterator<IntVector3> itr = getViewableVolume(cx, cy, cz, viewDistance);

		priorityChunkSendQueue.clear();
		chunkSendQueue.clear();

		while (itr.hasNext()) {
			IntVector3 v = itr.next();
			Point base = new Point(world, v.getX() << Chunk.BLOCKS.BITS, v.getY() << Chunk.BLOCKS.BITS, v.getZ() << Chunk.BLOCKS.BITS);
			boolean inTargetArea = playerChunkBase.getMaxDistance(base) <= blockMinimumViewDistance;
			if (!activeChunks.contains(base)) {
				if (inTargetArea) {
					priorityChunkSendQueue.add(base);
				} else {
					chunkSendQueue.add(base);
				}
			}
			if (!initializedChunks.contains(base)) {
				chunkInitQueue.add(base);
			}
		}
	}

	/**
	 * Returns a copy of all currently active sent chunks to this player
	 *
	 * @return active chunks
	 */
	public Set<Chunk> getActiveChunks() {
		HashSet<Chunk> chunks = new HashSet<Chunk>();
		for (Point p : activeChunks) {
			chunks.add(p.getWorld().getChunkFromBlock(p));
		}
		return chunks;
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
	 * @return the chunks that were sent, or null if no chunk was sent
	 */
	public Collection<Chunk> sendChunk(Chunk c) {
		if (canSendChunk(c)) {
			return sendChunk(c, true);
		} else {
			return null;
		}
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
	 * @return the chunks that were sent, or null if no chunk was sent
	 */
	protected Collection<Chunk> sendChunk(Chunk c, boolean force) {
		return null;
	}

	/**
	 * Frees a chunk on the client.
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
	protected void initChunk(Point p) {
		//TODO: Implement Spout Protocol
	}

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
	protected void freeChunk(Point p) {
		//TODO: Inplement Spout Protocol
	}

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
	protected void sendPosition(Point p, Quaternion rot) {
		//TODO: Implement Spout Protocol
	}

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
	protected void worldChanged(World world) {
		//TODO: Implement Spout Protocol
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
	public void updateBlock(Chunk chunk, int x, int y, int z, BlockMaterial material, short data) {
	}

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
