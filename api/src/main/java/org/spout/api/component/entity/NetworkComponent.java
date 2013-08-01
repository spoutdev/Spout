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
package org.spout.api.component.entity;

import java.util.List;

import org.spout.api.entity.Player;
import org.spout.api.event.ProtocolEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Platform;
import org.spout.api.ServerOnly;
import org.spout.api.Spout;
import org.spout.api.entity.Entity;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.map.DefaultedKey;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.protocol.Message;
import org.spout.api.math.IntVector3;
import org.spout.api.math.Vector3;
import org.spout.api.protocol.event.ChunkFreeEvent;
import org.spout.api.protocol.event.ChunkSendEvent;
import org.spout.api.protocol.event.UpdateEntityEvent;
import org.spout.api.protocol.event.WorldChangeProtocolEvent;
import org.spout.api.protocol.reposition.NullRepositionManager;
import org.spout.api.protocol.reposition.RepositionManager;
import org.spout.api.util.OutwardIterator;
import org.spout.api.util.set.concurrent.TSyncIntHashSet;

/**
 * The networking behind {@link org.spout.api.entity.Entity}s.
 */
public abstract class NetworkComponent extends EntityComponent {
	//TODO: Move all observer code to NetworkComponent
	public final DefaultedKey<Boolean> IS_OBSERVER = new DefaultedKeyImpl<>("IS_OBSERVER", false);
	/** In chunks */
	public final DefaultedKey<Integer> SYNC_DISTANCE = new DefaultedKeyImpl<>("SYNC_DISTANCE", 10);
	private final AtomicReference<RepositionManager> rm = new AtomicReference<>(NullRepositionManager.getInstance());

	protected final TSyncIntHashSet synchronizedEntities = new TSyncIntHashSet();
	private Point lastChunkCheck = Point.invalid;
	// Base points used so as not to load chunks unnecessarily
	private final Set<Point> chunkInitQueue = new LinkedHashSet<>();
	private final Set<Point> priorityChunkSendQueue = new LinkedHashSet<>();
	private final Set<Point> chunkSendQueue = new LinkedHashSet<>();
	private final Set<Point> chunkFreeQueue = new LinkedHashSet<>();
	/**
	 * Chunks that have initialized on the client. May also have chunks that have been sent.
	 */
	private final Set<Point> initializedChunks = new LinkedHashSet<>();
	/**
	 * Chunks that have been sent to the client
	 */
	private final Set<Point> activeChunks = new LinkedHashSet<>();
	private volatile boolean worldChanged = true;
	private final LinkedHashSet<Chunk> observed = new LinkedHashSet<>();
	/**
	 * Includes chunks that need to be observed. When observation is successfully attained or no longer wanted, point is removed
	 */
	private final Set<Point> chunksToObserve = new LinkedHashSet<>();
	private boolean sync = false;
	protected int tickCounter = 0;
	protected final static int CHUNKS_PER_TICK = 20;

	@Override
	public final boolean canTick() {
		return false;
	}

	/**
	 * Returns if the owning {@link org.spout.api.entity.Entity} is an observer.
	 * <p/>
	 * Observer means the Entity can trigger network updates (such as chunk creation) within its sync distance.
	 *
	 * @return True if observer, false if not
	 */
	public boolean isObserver() {
		return getData().get(IS_OBSERVER);
	}

	/**
	 * Sets the observer status for the owning {@link org.spout.api.entity.Entity}.
	 *
	 * @param observer True if observer, false if not
	 */
	public void setObserver(final boolean observer) {
		getData().put(IS_OBSERVER, observer);
	}

	/**
	 * Gets the sync distance of the owning {@link org.spout.api.entity.Entity}.
	 * </p>
	 * Sync distance is a value indicating the radius outwards from the entity where network updates (such as chunk creation) will be triggered.
	 *
	 * @return The current sync distance
	 */
	public int getSyncDistance() {
		return getData().get(SYNC_DISTANCE);
	}

	/**
	 * Sets the sync distance of the owning {@link org.spout.api.entity.Entity}.
	 *
	 * @param syncDistance The new sync distance
	 */
	public void setSyncDistance(final int syncDistance) {
		//TODO: Enforce server maximum (but that is set in Spout...)
		getData().put(SYNC_DISTANCE, syncDistance);
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

	/**
	 * Instructs the client to update the entities state and position<br><br>
	 *
	 * TODO change this to a protocol event maybe?
	 *
	 * @param e the entity
	 * @param liveTransform the live transform (latest) for the entity
	 * @param spawn is True when the entity just spawned
	 * @param destroy is True when the entity just got destroyed
	 * @param update is True when the entity is being updated
	 */
	@ServerOnly
	public void syncEntity(Entity e, Transform liveTransform, boolean spawn, boolean destroy, boolean update) {
		if (spawn) {
			synchronizedEntities.add(e.getId());
		} else if (destroy) {
			synchronizedEntities.remove(e.getId());
		}
	}

	@ServerOnly
	public boolean hasSpawned(Entity e) {
		return synchronizedEntities.contains(e.getId());
	}

	@ServerOnly
	public void forceRespawn() {
		worldChanged = true;
	}

	@ServerOnly
	protected void clearObservers() {
		chunksToObserve.clear();
		for (Point p : initializedChunks) {
			removeObserver(p);
		}
	}

	@ServerOnly
	public void forceSync() {
		sync = true;
	}

	/**
	 * Called when the owner is set to be synchronized to other NetworkComponents.
	 *
	 * TODO: Common logic between Spout and a plugin needing to implement this component?
	 * TODO: Add sequence checks to the PhysicsComponent to prevent updates to live?
	 *
	 * @param live A copy of the owner's live transform state
	 */
	@ServerOnly
	public void finalizeRun(final Transform live) {
		if (Spout.getPlatform() != Platform.SERVER) {
			return;
		}
		tickCounter++;

		//TODO: update chunk lists?
		final int prevSyncDistance = getSyncDistance();
		final int currentSyncDistance = getSyncDistance();
		// TODO: live values for syncdistance?
		//final int prevViewDistance = player.getViewDistance();
		//final int currentViewDistance = ((SpoutPlayer) player).getViewDistanceLive() >> Chunk.BLOCKS.BITS;

		final Point lastPosition = getOwner().getPhysics().getTransform().getPosition();
		final Point currentPosition = live.getPosition();

		if (lastPosition == null || (currentPosition != null && getOwner().getPhysics().isWorldDirty())) {
			clearObservers();
			worldChanged = true;
		}
		if (currentPosition != null) {
			if (prevSyncDistance != currentSyncDistance || worldChanged || (!currentPosition.equals(lastChunkCheck) && currentPosition.getManhattanDistance(lastChunkCheck) > (Chunk.BLOCKS.SIZE / 2))) {
				checkChunkUpdates(currentPosition);
				lastChunkCheck = currentPosition;
			}
		}

		if (!worldChanged) {
			for (Point p : chunkFreeQueue) {
				if (initializedChunks.contains(p)) {
					removeObserver(p);
				}
			}

			for (Point p : chunkInitQueue) {
				if (!initializedChunks.contains(p)) {
					observe(p);
				}
			}

			checkObserverUpdateQueue();
		}
	}

	/**
	 * Resets all chunk stores for the client.  This method is only called during the pre-snapshot part of the tick.
	 */
	@ServerOnly
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
	private Set<Point> unsendable = new HashSet<>();

	/**
	 * Called just before a snapshot is taken of the owner.
	 *
	 * TODO: Add sequence checks to the PhysicsComponent to prevent updates to live?
	 *
	 * @param live A copy of the owner's live transform state
	 */
	@ServerOnly
	public void preSnapshot(final Transform live) {
		if (Spout.getPlatform() != Platform.SERVER) {
			return;
		}
		if (worldChanged) {
			Point ep = getOwner().getPhysics().getPosition();
			resetChunks();
			callProtocolEvent(new WorldChangeProtocolEvent(ep.getWorld()), getOwner());
			worldChanged = false;
		} else {
			unsendable.clear();

			for (Point p : chunkFreeQueue) {
				if (initializedChunks.remove(p)) {
					callProtocolEvent(new ChunkFreeEvent(p), getOwner());
					activeChunks.remove(p);
				}
			}

			chunkFreeQueue.clear();

			int modifiedChunksPerTick = (!priorityChunkSendQueue.isEmpty() ? 4 : 1) * CHUNKS_PER_TICK;
			chunksSent = Math.max(0, chunksSent - modifiedChunksPerTick);

			for (Point p : chunkInitQueue) {
				if (initializedChunks.add(p)) {
					// TODO: protocol - init chunks?
				}
			}

			chunkInitQueue.clear();

			Iterator<Point> i;

			i = priorityChunkSendQueue.iterator();
			while (i.hasNext() && chunksSent < CHUNKS_PER_TICK) {
				Point p = i.next();
				i = attemptSendChunk(i, priorityChunkSendQueue, p);
			}

			if (!priorityChunkSendQueue.isEmpty()) {
				return;
			}

			if (getOwner().getPhysics().isTransformDirty() && sync) {
				callProtocolEvent(new UpdateEntityEvent(getOwner().getId(), new Transform(getOwner().getPhysics().getPosition(), getOwner().getPhysics().getRotation(), Vector3.ONE), UpdateEntityEvent.UpdateAction.TRANSFORM, getRepositionManager()), getOwner());
				sync = false;
			}

			boolean tickTimeRemaining = Spout.getScheduler().getRemainingTickTime() > 0;

			i = chunkSendQueue.iterator();
			while (i.hasNext() && chunksSent < CHUNKS_PER_TICK && tickTimeRemaining) {
				Point p = i.next();
				i = attemptSendChunk(i, chunkSendQueue, p);
				tickTimeRemaining = Spout.getScheduler().getRemainingTickTime() > 0;
			}
		}
	}

	private Iterator<Point> attemptSendChunk(Iterator<Point> i, Iterable<Point> queue, Point p) {
		Chunk c = p.getWorld().getChunkFromBlock(p, LoadOption.LOAD_ONLY);
		if (c == null) {
			unsendable.add(p);
			return i;
		}
		if (unsendable.contains(p)) {
			return i;
		}
		callProtocolEvent(new ChunkSendEvent(c), getOwner());
		activeChunks.add(c.getBase());
		i.remove();
		Point base = c.getBase();
		boolean removed = priorityChunkSendQueue.remove(base);
		removed |= chunkSendQueue.remove(base);
		if (removed) {
			if (initializedChunks.contains(base)) {
				activeChunks.add(base);
			}
			chunksSent++;
			i = queue.iterator();
		}
		chunksSent++;
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
					observe(c);
					i.remove();
				}
			}
		}
	}

	private void observe(Point p) {
		Chunk c = p.getWorld().getChunkFromBlock(p, LoadOption.NO_LOAD);
		if (c != null) {
			observe(c);
		} else {
			chunksToObserve.add(p);
		}
	}

	private void observe(Chunk c) {
		observed.add(c);
		c.refreshObserver(getOwner());
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
		c.removeObserver(getOwner());
	}

	/**
	 * Gets the viewable volume centered on the given chunk coordinates and the given view distance
	 */
	public static Iterator<IntVector3> getViewableVolume(int cx, int cy, int cz, int viewDistance) {
		return new OutwardIterator(cx, cy, cz, viewDistance);
	}

	/**
	 * Test if a given chunk base is in the view volume for a given player chunk base point
	 *
	 * @return true if in the view volume
	 */
	public static boolean isInViewVolume(Point playerChunkBase, Point testChunkBase, int viewDistance) {
		return testChunkBase.getManhattanDistance(playerChunkBase) <= (viewDistance << Chunk.BLOCKS.BITS);
	}

	private void checkChunkUpdates(Point currentPosition) {

		// Recalculating these
		priorityChunkSendQueue.clear();
		chunkSendQueue.clear();
		chunkFreeQueue.clear();
		chunkInitQueue.clear();

		final World world = currentPosition.getWorld();
		final int bx = (int) currentPosition.getX();
		final int by = (int) currentPosition.getY();
		final int bz = (int) currentPosition.getZ();

		final int cx = bx >> Chunk.BLOCKS.BITS;
		final int cy = by >> Chunk.BLOCKS.BITS;
		final int cz = bz >> Chunk.BLOCKS.BITS;

		Point playerChunkBase = Chunk.pointToBase(currentPosition);

		for (Point p : initializedChunks) {
			if (!isInViewVolume(p, playerChunkBase, getSyncDistance())) {
				chunkFreeQueue.add(p);
			}
		}

		Iterator<IntVector3> itr = getViewableVolume(cx, cy, cz, getSyncDistance());

		while (itr.hasNext()) {
			IntVector3 v = itr.next();
			Point base = new Point(world, v.getX() << Chunk.BLOCKS.BITS, v.getY() << Chunk.BLOCKS.BITS, v.getZ() << Chunk.BLOCKS.BITS);
			boolean inTargetArea = playerChunkBase.getMaxDistance(base) <= (getSyncDistance() / 2);// TODO: do we need to move blockMinViewDistance?
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
	@ServerOnly
	public Set<Chunk> getActiveChunks() {
		HashSet<Chunk> chunks = new HashSet<>();
		for (Point p : activeChunks) {
			chunks.add(p.getWorld().getChunkFromBlock(p));
		}
		return chunks;
	}

	/**
	 * Calls a {@link org.spout.api.event.ProtocolEvent} for all {@link org.spout.api.entity.Player}s in-which the owning {@link org.spout.api.entity.Entity} is within their sync distance
	 * <p/>
	 * If the owning Entity is a Player, it will receive the event as well.
	 *
	 * @param event to send
	 */
	public final void callProtocolEvent(final ProtocolEvent event) {
		callProtocolEvent(event, false);
	}

	/**
	 * Calls a {@link ProtocolEvent} for all {@link org.spout.api.entity.Player}s in-which the owning {@link org.spout.api.entity.Entity} is within their sync distance
	 *
	 * @param event to send
	 * @param ignoreOwner True to ignore the owning Entity, false to also send it to the Entity (if the Entity is also a Player)
	 */
	public final void callProtocolEvent(final ProtocolEvent event, final boolean ignoreOwner) {
		final List<Player> players = getOwner().getWorld().getPlayers();
		final Point position = getOwner().getPhysics().getPosition();
		final List<Message> messages = getEngine().getEventManager().callEvent(event).getMessages();

		for (final Player player : players) {
			if (ignoreOwner && getOwner() == player) {
				continue;
			}
			final Point otherPosition = player.getPhysics().getPosition();
			//TODO: Verify this math
			if (position.subtract(otherPosition).fastLength() > player.getNetwork().getSyncDistance()) {
				continue;
			}
			for (final Message message : messages) {
				player.getNetwork().getSession().send(false, message);
			}
		}
	}

	/**
	 * Calls a {@link ProtocolEvent} for all {@link Player}s provided.
	 *
	 * @param event to send
	 * @param players to send to
	 */
	public final void callProtocolEvent(final ProtocolEvent event, final Player... players) {
		final List<Message> messages = getEngine().getEventManager().callEvent(event).getMessages();
		for (final Player player : players) {
			for (final Message message : messages) {
				player.getNetwork().getSession().send(false, message);
			}
		}
	}

	/**
	 * Calls a {@link ProtocolEvent} for all the given {@link Enitity}s.
	 * For every {@link Entity} that is a {@link Player}, any messages from the event will be sent to that Player's session.
	 * Any non-player entities can use the event for custom handling.
	 *
	 * @param event to send
	 * @param entities to send to
	 */
	public final void callProtocolEvent(final ProtocolEvent event, final Entity... entities) {
		final List<Message> messages = getEngine().getEventManager().callEvent(event).getMessages();
		for (final Entity entity : entities) {
			if (!(entity instanceof Player)) continue;
			for (final Message message : messages) {
				((Player) entity).getNetwork().getSession().send(false, message);
			}
		}
	}
}
