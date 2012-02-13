/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.spout.api.entity.Entity;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Pointm;
import org.spout.api.geo.discrete.atomic.Transform;
import org.spout.api.player.Player;

public class NetworkSynchronizer {
	protected final Player owner;
	protected Entity entity;
	protected final Session session;

	public NetworkSynchronizer(Player owner) {
		this(owner, null);
	}

	public NetworkSynchronizer(Player owner, Entity entity) {
		this.owner = owner;
		this.entity = entity;
		session = owner.getSession();
	}

	private final static int TARGET_SIZE = 5 * Chunk.CHUNK_SIZE;
	private final static int CHUNKS_PER_TICK = 200;

	private final int viewDistance = 5;
	private final int blockViewDistance = viewDistance * Chunk.CHUNK_SIZE;

	private final Pointm lastChunkCheck = new Pointm();

	// Base points used so as not to load chunks unnecessarily
	private final Set<Point> chunkInitQueue = new LinkedHashSet<Point>();
	private final Set<Point> priorityChunkSendQueue = new LinkedHashSet<Point>();
	private final Set<Point> chunkSendQueue = new LinkedHashSet<Point>();
	private final Set<Point> chunkFreeQueue = new LinkedHashSet<Point>();

	private final Set<Point> initializedChunks = new LinkedHashSet<Point>();
	private final Set<Point> activeChunks = new LinkedHashSet<Point>();

	private boolean death = false;
	private boolean first = true;
	private volatile boolean teleported = false;

	private LinkedHashSet<Chunk> observed = new LinkedHashSet<Chunk>();

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public void onDeath() {
		death = true;
		entity = null;
		for (Point p : initializedChunks) {
			Chunk c = p.getWorld().getChunk(p, false);
			if (c != null) {
				removeObserver(c);
			}
		}
	}

	/**
	 * Called just before the pre-snapshot stage.<br>
	 * This stage can make changes but they should be checked to make sure they
	 * are non-conflicting.
	 */
	public void finalizeTick() {
		if (entity == null) {
			return;
		}

		// TODO teleport smoothing

		Transform lastTransform = entity.getTransform();
		Transform liveTransform = entity.getLiveTransform();

		if (liveTransform != null) {
			Point currentPosition = liveTransform.getPosition();
			if (currentPosition.getManhattanDistance(lastChunkCheck) > Chunk.CHUNK_SIZE >> 1) {
				checkChunkUpdates(currentPosition);
				lastChunkCheck.set(currentPosition);
			}

			if (first || lastTransform == null || lastTransform.getPosition().getWorld() != liveTransform.getPosition().getWorld()) {
				worldChanged(liveTransform.getPosition().getWorld());
				teleported = true;
			}
		}

		for (Point p : chunkFreeQueue) {
			if (initializedChunks.contains(p)) {
				Chunk c = p.getWorld().getChunk(p, false);
				if (c != null) {
					removeObserver(c);
				}
			}
		}

		for (Point p : chunkInitQueue) {
			if (!initializedChunks.contains(p)) {
				Chunk c = p.getWorld().getChunk(p, true);
				addObserver(c);
			}
		}

	}

	public void preSnapshot() {

		if (death) {
			death = false;
			for (Point p : initializedChunks) {
				freeChunk(p);
			}
		} else {

			for (Point p : chunkFreeQueue) {
				if (initializedChunks.remove(p)) {
					freeChunk(p);
					activeChunks.remove(p);
				}
			}

			chunkFreeQueue.clear();

			for (Point p : chunkInitQueue) {
				if (initializedChunks.add(p)) {
					initChunk(p);
				}
			}

			chunkInitQueue.clear();

			int chunksSent = 0;

			Iterator<Point> i;

			i = priorityChunkSendQueue.iterator();
			while (i.hasNext() && chunksSent < CHUNKS_PER_TICK) {
				Point p = i.next();
				Chunk c = p.getWorld().getChunk(p, true);
				sendChunk(c);
				activeChunks.add(p);
				i.remove();
				chunksSent++;
			}

			i = chunkSendQueue.iterator();
			while (i.hasNext() && chunksSent < CHUNKS_PER_TICK) {
				Point p = i.next();
				Chunk c = p.getWorld().getChunk(p, true);
				sendChunk(c);
				activeChunks.add(p);
				i.remove();
				chunksSent++;
			}

			if (teleported && entity != null) {
				Transform liveTransform = entity.getLiveTransform();
				sendPosition(liveTransform);
				first = false;
				teleported = false;
			}
		}

	}

	private void addObserver(Chunk c) {
		observed.add(c);
		c.refreshObserver(owner);
	}

	private void removeObserver(Chunk c) {
		observed.remove(c);
		c.removeObserver(owner);
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

		Point playerChunkBase = Chunk.pointToBase(currentPosition);

		for (Point p : initializedChunks) {
			if (p.getManhattanDistance(playerChunkBase) > blockViewDistance) {
				chunkFreeQueue.add(p);
			}
		}

		int cx = bx >> Chunk.CHUNK_SIZE_BITS;
		int cy = by >> Chunk.CHUNK_SIZE_BITS;
		int cz = bz >> Chunk.CHUNK_SIZE_BITS;

		// TODO - circle loading
		for (int x = cx - viewDistance; x < cx + viewDistance; x++) {
			for (int y = cy - viewDistance; y < cy + viewDistance; y++) {
				for (int z = cz - viewDistance; z < cz + viewDistance; z++) {
					Point base = new Point(world, x << Chunk.CHUNK_SIZE_BITS, y << Chunk.CHUNK_SIZE_BITS, z << Chunk.CHUNK_SIZE_BITS);
					double distance = base.getManhattanDistance(playerChunkBase);
					if (distance <= blockViewDistance) {
						if (!activeChunks.contains(base)) {
							if (distance <= TARGET_SIZE) {
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
			}
		}
	}

	/**
	 * Gets the entity protocol manager
	 *
	 * @return the entity protocol manager
	 */
	public EntityProtocol getEntityProtocol() {
		throw new IllegalStateException("No entity protocol available for core class");
	}

	/**
	 * Sends a chunk to the client.
	 *
	 * This method is called during the startSnapshot stage of the tick.
	 *
	 * This is a MONITOR method, for sending network updates, no changes should
	 * be made to the chunk
	 *
	 * @param c the chunk
	 */
	public void sendChunk(Chunk c) {
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
	 * @param t the transform
	 */
	protected void sendPosition(Transform t) {
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
	 * @param t the transform
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
	 * @param block the block
	 */
	public void updateBlock(Chunk chunk, Block block) {
	}

	/**
	 * Instructs the client to spawn the entity
	 *
	 * @param e the entity
	 */
	public void spawnEntity(Entity e) {
	}

	/**
	 * Instructs the client to destroy the entity
	 *
	 * @param e the entity
	 */
	public void destroyEntity(Entity e) {
	}

	/**
	 * Instructs the client to update the entities state and position
	 *
	 * @param e the entity
	 */
	public void syncEntity(Entity e) {
	}
}
