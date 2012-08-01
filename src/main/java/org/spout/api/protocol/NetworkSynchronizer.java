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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Spout;
import org.spout.api.entity.Entity;
import org.spout.api.event.EventHandler;
import org.spout.api.exception.EventException;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.IntVector3;
import org.spout.api.math.Quaternion;
import org.spout.api.player.Player;
import org.spout.api.protocol.event.ProtocolEvent;
import org.spout.api.protocol.event.ProtocolEventExecutor;
import org.spout.api.protocol.event.ProtocolEventListener;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.OutwardIterator;

public abstract class NetworkSynchronizer {
	protected final Player owner;
	protected Entity entity;
	protected final Session session;
	protected final AtomicReference<Protocol> protocol = new AtomicReference<Protocol>(null);

	public NetworkSynchronizer(Player owner, Session session, Entity entity, int minimumViewRadius) {
		this.owner = owner;
		this.entity = entity;
		if (entity != null) {
			entity.setObserver(true);
			blockViewDistance = entity.getViewDistance();
		} else {
			blockViewDistance = 0;
		}
		this.session = session;
		viewDistance = blockViewDistance >> Chunk.BLOCKS.BITS;
		this.minimumViewRadius = minimumViewRadius * Chunk.BLOCKS.SIZE;
	}

	private final int minimumViewRadius;
	private final static int CHUNKS_PER_TICK = 200;

	private final int viewDistance;
	private final int blockViewDistance;

	private Point lastChunkCheck =  Point.invalid;

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
	private volatile boolean worldChanged = false;
	private Point lastPosition = null;
	private Point holdingPosition = null;
	private final LinkedHashSet<Chunk> observed = new LinkedHashSet<Chunk>();
	private final Set<Point> chunksToObserve = new LinkedHashSet<Point>();
	private final Map<Class<? extends ProtocolEvent>, ProtocolEventExecutor> protocolEventMapping = new HashMap<Class<? extends ProtocolEvent>, ProtocolEventExecutor>();

	public void setPositionDirty() {
		teleported = true;
	}

	public Entity getEntity() {
		return entity;
	}

	public Player getOwner() {
		return owner;
	}

	protected void registerProtocolEvents(final ProtocolEventListener listener) {
		for (final Method method : listener.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(EventHandler.class) && method.getParameterTypes().length == 1) {
				Class<?> clazz = method.getParameterTypes()[0];
				if (!ProtocolEvent.class.isAssignableFrom(clazz)) {
					session.getEngine().getLogger().warning("Invalid protocol event handler attempted to be registered for " + owner.getName());
					continue;
				}

				Class<?> returnType = method.getReturnType();
				if (returnType == null || returnType.equals(void.class)) {
					session.getEngine().getLogger().warning("Protocol event handler not returning a Message tried to be registered for " + owner.getName());
					session.getEngine().getLogger().warning("Please change the return type from 'void' to Message");
					continue;
				} else if (!Message.class.isAssignableFrom(returnType)) {
					Class<?> compType = returnType.getComponentType();
					if (compType == null || !Message.class.isAssignableFrom(compType)) {
						session.getEngine().getLogger().warning("Protocol event handler not returning a Message tried to be registered for " + owner.getName());
						continue;
					}
				}

				method.setAccessible(true);
				protocolEventMapping.put(clazz.asSubclass(ProtocolEvent.class), new ProtocolEventExecutor() {
					@Override
					public Message[] execute(ProtocolEvent event) throws EventException {
						try {
							Object obj = method.invoke(listener, event);
							if (obj == null) {
								return null;
							} else if (obj.getClass().isArray()) {
								return (Message[]) obj;
							} else if (Message.class.isAssignableFrom(obj.getClass())) {
								return new Message[] {(Message) obj};
							}
						} catch (InvocationTargetException e) {
							throw new EventException(e.getCause());
						} catch (IllegalAccessException e) {
							throw new EventException(e);
						}
						return null;
					}
				});
			}
		}
	}

	public boolean callProtocolEvent(ProtocolEvent event) {
		ProtocolEventExecutor executor = protocolEventMapping.get(event.getClass());
		if (executor != null) {
			try {
				Message[] messages = executor.execute(event);
				if (messages != null && messages.length > 0) {
					for (Message msg : messages) {
						session.send(false, msg);
					}
					return true;
				}
			} catch (EventException e) {
				if (e.getCause() != null) {
					Throwable t = e.getCause();
					session.getEngine().getLogger().severe("Error occurred while calling protocol event"
							+ event.getClass().getSimpleName() + " for player " + owner.getName() + ": " + t.getMessage());
					t.printStackTrace();
				}
			}
		}
		return false;
	}

	public void onDeath() {
		TickStage.checkStage(TickStage.FINALIZE);
		death = true;
		entity = null;
		for (Point p : initializedChunks) {
			removeObserver(p);
		}
	}

	/**
	 * Called just before the pre-snapshot stage.<br>
	 * This stage can make changes but they should be checked to make sure they
	 * are non-conflicting.
	 */
	public void finalizeTick() {
		if (entity == null || entity.isDead()) {
			return;
		}

		// TODO teleport smoothing
		Point currentPosition = entity.getPosition();
		if (currentPosition != null) {
			if (worldChanged || currentPosition.getManhattanDistance(lastChunkCheck) > Chunk.BLOCKS.SIZE >> 1) {
				checkChunkUpdates(currentPosition);
				lastChunkCheck = currentPosition;
				worldChanged = false;
			}

			if (first || lastPosition == null || lastPosition.getWorld() != currentPosition.getWorld()) {
				worldChanged = true;
				teleported = true;
			}

		}

		lastPosition = currentPosition;

		if (!teleported) {
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

	public void preSnapshot() {

		if (death) {
			death = false;
			for (Point p : initializedChunks) {
				freeChunk(p);
			}
		} else {
			if (worldChanged && entity != null) {
				first = false;
				Point ep = entity.getPosition();
				if (worldChanged) {
					worldChanged(ep.getWorld());
				}
			} else if (!worldChanged) {
				for (Point p : chunkFreeQueue) {
					if (initializedChunks.remove(p)) {
						freeChunk(p);
						activeChunks.remove(p);
					}
				}

				chunkFreeQueue.clear();

				int chunksSent = 0;

				for (Point p : chunkInitQueue) {
					if (initializedChunks.add(p)) {
						initChunk(p);
					}
				}

				chunkInitQueue.clear();

				Iterator<Point> i;

				i = priorityChunkSendQueue.iterator();
				while (i.hasNext()) {
					Point p = i.next();
					Chunk c = p.getWorld().getChunkFromBlock(p);
					if (c.canSend()) {
						sendChunk(c);
						activeChunks.add(p);
						i.remove();
						chunksSent++;
					}
				}

				if (priorityChunkSendQueue.isEmpty() && teleported && entity != null) {
					sendPosition(entity.getPosition(), entity.getRotation());
					teleported = false;
				}

				boolean tickTimeRemaining = Spout.getScheduler().getRemainingTickTime() > 0;

				i = chunkSendQueue.iterator();
				while (i.hasNext() && chunksSent < CHUNKS_PER_TICK && tickTimeRemaining) {
					Point p = i.next();
					Chunk c = p.getWorld().getChunkFromBlock(p);
					if (c.canSend()) {
						sendChunk(c);
						activeChunks.add(p);
						i.remove();
						chunksSent++;
					}
					tickTimeRemaining = Spout.getScheduler().getRemainingTickTime() > 0;
				}
			}
		}

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
		c.refreshObserver(owner);
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
		Point playerHoldingChunkBase = holdingPosition == null ? null : Chunk.pointToBase(holdingPosition);

		for (Point p : initializedChunks) {
			if (p.getManhattanDistance(playerChunkBase) > blockViewDistance) {
				if (playerHoldingChunkBase == null || p.getManhattanDistance(playerHoldingChunkBase) > minimumViewRadius) {
					chunkFreeQueue.add(p);
				}
			}
		}

		int cx = bx >> Chunk.BLOCKS.BITS;
		int cy = by >> Chunk.BLOCKS.BITS;
		int cz = bz >> Chunk.BLOCKS.BITS;

		Iterator<IntVector3> itr = new OutwardIterator(cx, cy, cz, viewDistance);

		priorityChunkSendQueue.clear();
		chunkSendQueue.clear();

		while (itr.hasNext()) {
			IntVector3 v = itr.next();
			Point base = new Point(world, v.getX() << Chunk.BLOCKS.BITS, v.getY() << Chunk.BLOCKS.BITS, v.getZ() << Chunk.BLOCKS.BITS);
			boolean inTargetArea = playerChunkBase.getMaxDistance(base) <= minimumViewRadius;
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
	 * While always called during the startSnapshot part of the tick, it may be called from
	 * multiple threads
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

	/**
	 * Sets the protocol associated with this network synchronizer
	 *
	 * @param protocol
	 */
	public void setProtocol(Protocol protocol) {
		if (protocol == null) {
			throw new IllegalArgumentException("Protocol may not be null");
		} else if (!this.protocol.compareAndSet(null, protocol)) {
			throw new IllegalStateException("Protocol may not be set twice for a network synchronizer");
		}
	}
}
