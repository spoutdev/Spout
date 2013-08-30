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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Client;
import org.spout.api.Platform;
import org.spout.api.Server;
import org.spout.api.ServerOnly;
import org.spout.api.Spout;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.math.IntVector3;
import org.spout.api.math.Vector3;
import org.spout.api.protocol.ClientSession;
import org.spout.api.protocol.ServerSession;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.event.ChunkFreeEvent;
import org.spout.api.protocol.event.ChunkSendEvent;
import org.spout.api.protocol.event.EntityUpdateEvent;
import org.spout.api.protocol.event.WorldChangeProtocolEvent;
import org.spout.api.protocol.reposition.NullRepositionManager;
import org.spout.api.protocol.reposition.RepositionManager;
import org.spout.api.util.OutwardIterator;
import org.spout.api.util.SyncedStringMap;
import org.spout.api.util.set.concurrent.TSyncIntHashSet;

/**
 * The networking behind {@link org.spout.api.entity.Player}s. This component holds the {@link Session} which is the connection the Player has to the server.
 */
public class PlayerNetworkComponent extends NetworkComponent implements Listener {
	private static final SyncedStringMap protocolMap = SyncedStringMap.create(null, new MemoryStore<Integer>(), 0, 256, "componentProtocols");
	protected static final int CHUNKS_PER_TICK = 20;
	private final AtomicReference<Session> session = new AtomicReference<>(null);
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
	protected volatile boolean worldChanged = false;
	/**
	 * Includes chunks that need to be observed. When observation is successfully attained or no longer wanted, point is removed
	 */
	private final Set<Point> chunksToObserve = new LinkedHashSet<>();
	private boolean sync = false;
	protected int tickCounter = 0;
	private int chunksSent = 0;
	private Set<Point> unsendable = new HashSet<>();
	private final AtomicReference<RepositionManager> rm = new AtomicReference<>(NullRepositionManager.getInstance());

	@Override
	public void onAttached() {
		if (!(getOwner() instanceof Player)) {
			throw new IllegalStateException("The PlayerNetworkComponent may only be given to Players");
		}
		super.onAttached();
		setObserver(true);
	}

	/**
	 * Returns the {@link Session} representing the connection to the server.
	 *
	 * @return The session
	 */
	public final Session getSession() {
		return session.get();
	}

	/**
	 * Sets the session this Player has to the server.
	 *
	 * @param session The session to the server
	 */
	public final void setSession(Session session) {
		if (getEngine() instanceof Client && !(session instanceof ClientSession)) {
			throw new IllegalStateException("The client may only have a ClientSession");
		}

		if (getEngine() instanceof Server && !(session instanceof ServerSession)) {
			throw new IllegalStateException("The server may only have a ServerSession");
		}

		if (!this.session.compareAndSet(null, session)) {
			throw new IllegalStateException("Once set, the session may not be re-set until a new connection is made");
		}
	}

	/**
	 * Gets the {@link InetAddress} of the session
	 *
	 * @return The adress of the session
	 */
	public final InetAddress getAddress() {
		return getSession().getAddress().getAddress();
	}

	/**
	 * Registers the protocol name and gets the id assigned.
	 *
	 * @param protocolName The name of the protocol class to get an id for
	 * @return The id for the specified protocol class
	 */
	public static int getProtocolId(String protocolName) {
		return protocolMap.register(protocolName);
	}

	/**
	 * Instructs the client to update the entities state and position<br><br>
	 *
	 * @param event {@link EntitySyncEvent}
	 */
	@EventHandler(order = Order.EARLIEST)
	public final void syncEntityEarliest(EntityUpdateEvent event) {
		if (Spout.getPlatform() != Platform.SERVER) {
			return;
		}
		switch (event.getAction()) {
			case ADD:
				synchronizedEntities.add(event.getEntityId());
				break;
			case REMOVE:
				synchronizedEntities.remove(event.getEntityId());
				break;
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
	 * Called when the owner is set to be synchronized to other NetworkComponents.
	 *
	 * TODO: Common logic between Spout and a plugin needing to implement this component? TODO: Add sequence checks to the PhysicsComponent to prevent updates to live?
	 *
	 * @param live A copy of the owner's live transform state
	 */
	@Override
	public void finalizeRun(final Transform live) {
		super.finalizeRun(live);
		if (Spout.getPlatform() != Platform.SERVER || session.get().getState() != Session.State.GAME) {
			return;
		}
		tickCounter++;
		//TODO: update chunk lists?
		final int prevSyncDistance = getSyncDistance();
		final int currentSyncDistance = getSyncDistance();
		// TODO: live values for syncdistance?
		//final int prevViewDistance = player.getViewDistance();
		//final int currentViewDistance = ((SpoutPlayer) player).getViewDistanceLive() >> Chunk.BLOCKS.BITS;
		final Point currentPosition = live.getPosition();
		if (getOwner().getPhysics().isWorldDirty()) {
			clearObservers();
			worldChanged = true;
		}
		if (prevSyncDistance != currentSyncDistance || worldChanged || (!currentPosition.equals(lastChunkCheck) && currentPosition.getManhattanDistance(lastChunkCheck) > (Chunk.BLOCKS.SIZE / 2))) {
			checkChunkUpdates(currentPosition);
			lastChunkCheck = currentPosition;
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
	 * Called just before a snapshot is taken of the owner.
	 *
	 * TODO: Add sequence checks to the PhysicsComponent to prevent updates to live?
	 *
	 * @param live A copy of the owner's live transform state
	 */
	@Override
	public void preSnapshotRun(final Transform live) {
		if (Spout.getPlatform() != Platform.SERVER || session.get().getState() != Session.State.GAME) {
			return;
		}
		super.preSnapshotRun(live);

		if (worldChanged) {
			Point ep = getOwner().getPhysics().getPosition();
			resetChunks();
			callProtocolEvent(new WorldChangeProtocolEvent(ep.getWorld()), getOwner());
			worldChanged = false;
		} else {
			// Free chunks first
			freeChunks();

			// Then initialize new ones
			initChunks();

			List<Point> prevActive = new ArrayList<>(activeChunks);

			// Now send new chunks
			chunksSent = 0;
			unsendable.clear();

			// Send priority chunks first
			sendPriorityChunks();

			// If we didn't send all the priority chunks, don't send position or regular chunks yet
			if (priorityChunkSendQueue.isEmpty()) {
				// Send position
				sendPositionUpdates(live);

				// Then regular chunks
				sendRegularChunks();
			}

			// Check all active old chunks for updates
			for (Point p : prevActive) {
				Chunk chunk = p.getChunk(LoadOption.LOAD_ONLY);
				if (chunk == null) {
					continue;
				}
				chunk.sync(this);
			}
		}
	}

	private void freeChunks() {
		for (Point p : chunkFreeQueue) {
			if (initializedChunks.remove(p)) {
				callProtocolEvent(new ChunkFreeEvent(p), getOwner());
				activeChunks.remove(p);
			}
		}
		chunkFreeQueue.clear();
	}

	private void initChunks() {
		for (Point p : chunkInitQueue) {
			if (initializedChunks.add(p)) {
				// TODO: protocol - init chunks?
			}
		}
		chunkInitQueue.clear();
	}

	private void sendPriorityChunks() {
		Iterator<Point> i = priorityChunkSendQueue.iterator();
		while (i.hasNext() && chunksSent < CHUNKS_PER_TICK) {
			Point p = i.next();
			if (attemptSendChunk(p)) {
				i.remove();
			}
		}
	}

	private void sendPositionUpdates(Transform live) {
		if (getOwner().getPhysics().isTransformDirty() && sync) {
			callProtocolEvent(new EntityUpdateEvent(getOwner(), live, EntityUpdateEvent.UpdateAction.TRANSFORM, getRepositionManager()), getOwner());
			sync = false;
		}
	}

	private void sendRegularChunks() {
		Iterator<Point> i = chunkSendQueue.iterator();
		while (i.hasNext() && chunksSent < CHUNKS_PER_TICK && Spout.getScheduler().getRemainingTickTime() > 0) {
			Point p = i.next();
			if (attemptSendChunk(p)) {
				i.remove();
			}
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

	protected boolean canSendChunk(Chunk c) {
		return true;
	}

	private boolean attemptSendChunk(Point p) {
		Chunk c = p.getWorld().getChunkFromBlock(p, LoadOption.LOAD_ONLY);
		if (c == null) {
			unsendable.add(p);
			return false;
		}
		if (unsendable.contains(p)) {
			return false;
		}
		if (!canSendChunk(c)) {
			unsendable.add(p);
			return false;
		}

		callProtocolEvent(new ChunkSendEvent(c), getOwner());
		Point base = c.getBase();
		if (initializedChunks.contains(base)) {
			activeChunks.add(base);
		}
		chunksSent++;
		return true;
	}

	private void checkObserverUpdateQueue() {
		Iterator<Point> i = chunksToObserve.iterator();
		while (i.hasNext()) {
			Point p = i.next();
			if (!chunkInitQueue.contains(p) && !this.initializedChunks.contains(p)) {
				i.remove();
			} else {
				Chunk c = p.getChunk(LoadOption.NO_LOAD);
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
		c.removeObserver(getOwner());
	}

	/**
	 * Gets the viewable volume centered on the given chunk coordinates and the given view distance
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
	 * Checks for chunk updates that might have from movement.
	 */
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
		// TODO: could we use getSyncIterator?
		Iterator<IntVector3> itr = getViewableVolume(cx, cy, cz, getSyncDistance());
		while (itr.hasNext()) {
			IntVector3 v = itr.next();
			Point base = new Point(world, v.getX() << Chunk.BLOCKS.BITS, v.getY() << Chunk.BLOCKS.BITS, v.getZ() << Chunk.BLOCKS.BITS);
			boolean inTargetArea = playerChunkBase.getMaxDistance(base) <= (getSyncDistance() / 2); // TODO: do we need to move blockMinViewDistance?
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

	@Override
	public void onDetached() {
		super.onDetached();
		session.set(null);
	}
}
