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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import org.spout.api.protocol.ClientSession;
import org.spout.api.protocol.ServerSession;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.event.ChunkFreeEvent;
import org.spout.api.protocol.event.ChunkSendEvent;
import org.spout.api.protocol.event.EntityUpdateEvent;
import org.spout.api.protocol.event.WorldChangeProtocolEvent;
import org.spout.api.protocol.reposition.NullRepositionManager;
import org.spout.api.protocol.reposition.RepositionManager;
import org.spout.api.util.ChunkReference;
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

	private final Set<ChunkReference> chunkSendQueuePriority = new LinkedHashSet<>();
	private final Set<ChunkReference> chunkSendQueueRegular = new LinkedHashSet<>();
	private final Set<ChunkReference> chunkFreeQueue = new LinkedHashSet<>();
	/**
	 * Chunks that have been sent to the client
	 */
	private final Set<ChunkReference> activeChunks = new LinkedHashSet<>();
	/**
	 * Includes chunks that need to be sent.
	 */
	private final Set<ChunkReference> futureChunksToSend = new LinkedHashSet<>();

	protected volatile boolean worldChanged = false;
	private boolean sync = false;
	protected int tickCounter = 0;
	private int chunksSent = 0;
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
	 * @return The address of the session
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
	 * Checks for chunk updates that might have from movement.
	 */
	private void checkChunkUpdates(Point currentPosition) {
		// Recalculating these
		if (!chunkFreeQueue.isEmpty()) {
			throw new IllegalStateException("chunkFreeQueue is not empty!");
		}
		chunkSendQueuePriority.clear();
		chunkSendQueueRegular.clear();
		futureChunksToSend.clear();

		final World world = currentPosition.getWorld();
		final int bx = (int) currentPosition.getX();
		final int by = (int) currentPosition.getY();
		final int bz = (int) currentPosition.getZ();
		final int cx = bx >> Chunk.BLOCKS.BITS;
		final int cy = by >> Chunk.BLOCKS.BITS;
		final int cz = bz >> Chunk.BLOCKS.BITS;
		Point playerChunkBase = Chunk.pointToBase(currentPosition);
		for (ChunkReference ref : activeChunks) {
			Point p = ref.getBase();
			if (!isInViewVolume(p, playerChunkBase, getSyncDistance())) {
				chunkFreeQueue.add(ref);
			}
		}

		// TODO: could we use getSyncIterator?
		Iterator<IntVector3> itr = getViewableVolume(cx, cy, cz, getSyncDistance());
		while (itr.hasNext()) {
			IntVector3 v = itr.next();
			Point base = new Point(world, v.getX() << Chunk.BLOCKS.BITS, v.getY() << Chunk.BLOCKS.BITS, v.getZ() << Chunk.BLOCKS.BITS);
			ChunkReference ref = new ChunkReference(base);
			if (activeChunks.contains(ref)) {
				continue;
			}
			boolean inTargetArea = playerChunkBase.getMaxDistance(base) <= (getSyncDistance() / 2); // TODO: do we need to move blockMinViewDistance?
			boolean needGen = !inTargetArea;
			// If it's in the target area, we first check if we can just load it. If so, do that
			// If not, queue it for LOAD_GEN, but don't wait
			// If it's not in the target area, don't even wait for load
			if (inTargetArea && ref.refresh(LoadOption.LOAD_ONLY) == null) {
					needGen = true;
			}
			if (needGen) {
				ref.refresh(LoadOption.LOAD_GEN_NOWAIT);
			}

			futureChunksToSend.add(ref);
		}
	
	}

	private void updateSendLists(Point currentPosition) {
		Point playerChunkBase = Chunk.pointToBase(currentPosition);
		for (Iterator<ChunkReference> it = futureChunksToSend.iterator(); it.hasNext();) {
			ChunkReference ref = it.next();
			if (ref.refresh(LoadOption.NO_LOAD) == null) continue;
			it.remove();
			boolean inTargetArea = playerChunkBase.getMaxDistance(ref.getBase()) <= (getSyncDistance() / 2);
			if (inTargetArea) {
				chunkSendQueuePriority.add(ref);
			} else {
				chunkSendQueueRegular.add(ref);
			}
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
			worldChanged = true;
		}
		if (prevSyncDistance != currentSyncDistance || worldChanged || (!currentPosition.equals(lastChunkCheck) && currentPosition.getManhattanDistance(lastChunkCheck) > (Chunk.BLOCKS.SIZE / 2))) {
			checkChunkUpdates(currentPosition);
			lastChunkCheck = currentPosition;
		}

		updateSendLists(currentPosition);
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
		if (Spout.getPlatform() == Platform.CLIENT) {
			// TODO: protocol - hacky fix
			if (!((Client) Spout.getEngine()).getWorld().getName().equalsIgnoreCase("NullWorld")) {
				// Client always syncs to server
				sync = true;
				sendPositionUpdates(live);
			}
			return;
		}
		if (session.get().getState() != Session.State.GAME) {
			return;
		}
		super.preSnapshotRun(live);

		if (worldChanged) {
			Point ep = getOwner().getPhysics().getPosition();
			resetChunks();
			callProtocolEvent(new WorldChangeProtocolEvent(ep.getWorld()), getOwner());
			worldChanged = false;
			sync = true;
		} else {
			// We will update old chunks, but not new ones
			Set<ChunkReference> toSync = new LinkedHashSet<>(activeChunks);

			// Now send new chunks
			chunksSent = 0;

			// Send priority chunks first
			sendChunks(chunkSendQueuePriority.iterator(), true);

			// If we didn't send all the priority chunks, don't send position or regular chunks yet
			if (chunkSendQueuePriority.isEmpty()) {
				// Send position
				sendPositionUpdates(live);

				// Then regular chunks
				sendChunks(chunkSendQueueRegular.iterator(), false);
			}

			Set<ChunkReference> freeChunks = freeChunks();
			if (!freeChunks.isEmpty() && !toSync.removeAll(freeChunks)) {
				throw new IllegalStateException("There were freed chunks, but they were not removed.");
			}

			for (Iterator<ChunkReference> it = toSync.iterator(); it.hasNext();) {
				ChunkReference ref = it.next();
				Chunk chunk = ref.get();
				if (chunk == null) {
					System.out.println("Active chunk (" + ref.getBase().getChunkX() + " " + ref.getBase().getChunkY() + " " + ref.getBase().getChunkZ() + ") has been unloaded! Adding toChunkFreeQueue");
					chunkFreeQueue.add(ref);
					continue;
				}
				chunk.sync(this);
			}

			// We run another free to be sure all chunks that became free are now freed, this tick
			freeChunks();
		}
	}

	private Set<ChunkReference> freeChunks() {
		HashSet<ChunkReference> freed = new HashSet<>();
		for (ChunkReference ref : chunkFreeQueue) {
			callProtocolEvent(new ChunkFreeEvent(ref.getBase()), getOwner());
			freed.add(ref);
			activeChunks.remove(ref);
		}
		chunkFreeQueue.clear();
		return freed;
	}

	private void sendChunks(Iterator<ChunkReference> i, boolean priority) {
		while (i.hasNext() && chunksSent < CHUNKS_PER_TICK && (!priority || Spout.getScheduler().getRemainingTickTime() > 0)) {
			Chunk c = i.next().get();
			if (c == null || attemptSendChunk(c)) {
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

	/**
	 * Resets all chunk stores for the client.  This method is only called during the pre-snapshot part of the tick.
	 */
	@ServerOnly
	protected void resetChunks() {
		futureChunksToSend.clear();
		chunkSendQueuePriority.clear();
		chunkSendQueueRegular.clear();
		chunkFreeQueue.clear();
		activeChunks.clear();
		lastChunkCheck = Point.invalid;
		synchronizedEntities.clear();
	}

	protected boolean canSendChunk(Chunk c) {
		return true;
	}

	private boolean attemptSendChunk(Chunk c) {
		if (!canSendChunk(c)) {
			return false;
		}

		callProtocolEvent(new ChunkSendEvent(c), getOwner());
		ChunkReference ref = new ChunkReference(c);
		activeChunks.add(ref);
		chunksSent++;
		return true;
	}

	/**
	 * Returns a copy of all currently active sent chunks to this player
	 *
	 * @return active chunks
	 */
	@ServerOnly
	public Set<Chunk> getActiveChunks() {
		HashSet<Chunk> chunks = new HashSet<>();
		for (ChunkReference p : activeChunks) {
			Chunk get = p.get();
			if (get != null) {
				chunks.add(get);
			}
		}
		return chunks;
	}

	@Override
	public void onDetached() {
		super.onDetached();
		session.set(null);
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
}
