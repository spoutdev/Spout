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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.List;
import java.util.Set;

import org.spout.api.entity.Player;
import org.spout.api.event.ProtocolEvent;
import org.spout.api.entity.Entity;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.map.DefaultedKey;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.math.IntVector3;
import org.spout.api.math.Vector3;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.reposition.NullRepositionManager;
import org.spout.api.protocol.reposition.RepositionManager;
import org.spout.api.util.OutwardIterator;

/**
 * The networking behind {@link org.spout.api.entity.Entity}s.
 */
public class NetworkComponent extends EntityComponent {
	private static final WrappedSerizableIterator INITIAL_TICK = new WrappedSerizableIterator(null);
	//TODO: Move all observer code to NetworkComponent
	public final DefaultedKey<Boolean> IS_OBSERVER = new DefaultedKeyImpl<>("IS_OBSERVER", false);
	/**
	 * null means use SYNC_DISTANCE and is generated each update; not observing is {@code new OutwardIterator(0, 0, 0)}; custom Iterators can be used for others
	 * We want default to be null so that when it is default observer, it returns null
	 */
	public final DefaultedKey<WrappedSerizableIterator> OBSERVER_ITERATOR = new DefaultedKeyImpl<>("OBSERVER_ITERATOR", null);
	/** In chunks */
	public final DefaultedKey<Integer> SYNC_DISTANCE = new DefaultedKeyImpl<>("SYNC_DISTANCE", 10);
	private final AtomicReference<RepositionManager> rm = new AtomicReference<>(NullRepositionManager.getInstance());

	private final Set<Chunk> observingChunks = new HashSet<>();
	private AtomicReference<WrappedSerizableIterator> liveObserverIterator = new AtomicReference<>(new WrappedSerizableIterator(new OutwardIterator(0, 0, 0, 0)));
	private boolean observeChunksFailed = false;


	public static class WrappedSerizableIterator implements Serializable, Iterator<IntVector3> {
		private static final long serialVersionUID = 1L;
		private final Iterator<IntVector3> object;

		public <T extends Iterator<IntVector3> & Serializable> WrappedSerizableIterator(T object) {
			this.object = object;
		}

		@Override
		public boolean hasNext() {
			return object.hasNext();
		}

		@Override
		public IntVector3 next() {
			return object.next();
		}

		@Override
		public void remove() {
			object.remove();
		}
		
		private void writeObject(ObjectOutputStream stream) throws IOException {
			stream.defaultWriteObject();
		}
		
		private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
			stream.defaultReadObject();
		}
		
	}

	@Override
	public void onAttached() {
		getData().put(OBSERVER_ITERATOR, INITIAL_TICK);
	}

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
	 * If there was a custom observer iterator being used, passing {@code true} will cause it to reset to the default observer iterator.
	 *
	 * @param observer True if observer, false if not
	 */
	public void setObserver(final boolean observer) {
		getData().put(IS_OBSERVER, observer);
		if (observer) {
			liveObserverIterator.set(null);
		} else {
			liveObserverIterator.set(new WrappedSerizableIterator(new OutwardIterator(0, 0, 0, 0)));
		}
	}

	public <T extends Iterator<IntVector3> & Serializable> void setObserver(T custom) {
		if (custom == null) {
			setObserver(false);
		} else {
			getData().put(IS_OBSERVER, true);
			liveObserverIterator.set(new WrappedSerizableIterator(custom));
		}
	}

	public Iterator<IntVector3> getSyncIterator() {
		WrappedSerizableIterator get = getData().get(OBSERVER_ITERATOR);
		if (get != null) return get.object;
		Transform t = getOwner().getPhysics().getTransform();
		Point p = t.getPosition();
		int cx = p.getChunkX();
		int cy = p.getChunkY();
		int cz = p.getChunkZ();
		return new OutwardIterator(cx, cy, cz, getSyncDistance());
	}

	/**
	 * Gets the sync distance in {@link Chunk}s of the owning {@link org.spout.api.entity.Entity}.
	 * </p>
	 * Sync distance is a value indicating the radius outwards from the entity where network updates (such as chunk creation) will be triggered.
	 *
	 * @return The current sync distance
	 */
	public int getSyncDistance() {
		return getData().get(SYNC_DISTANCE);
	}

	/**
	 * Sets the sync distance in {@link Chunk}s of the owning {@link org.spout.api.entity.Entity}.
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
				player.getNetwork().getSession().send(event.isForced(), message);
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
				player.getNetwork().getSession().send(event.isForced(), message);
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
				((Player) entity).getNetwork().getSession().send(event.isForced(), message);
			}
		}
	}

	private boolean first = true;

	/**
	 * Called when the owner is set to be synchronized to other NetworkComponents.
	 *
	 * TODO: Common logic between Spout and a plugin needing to implement this component?
	 * TODO: Add sequence checks to the PhysicsComponent to prevent updates to live?
	 *
	 * @param live A copy of the owner's live transform state
	 */
	public void finalizeRun(final Transform live) {
		//Entity changed chunks as observer OR observer status changed so update
		WrappedSerizableIterator old = getData().get(OBSERVER_ITERATOR);
		if (getOwner().getPhysics().getPosition().getChunk(LoadOption.NO_LOAD) != live.getPosition().getChunk(LoadOption.NO_LOAD) && isObserver()
			|| liveObserverIterator.get() != old
			|| old == INITIAL_TICK
			|| observeChunksFailed) {
			updateObserver();
		}
	}

	@Override
	public void onDetached() {
		for (Chunk chunk : observingChunks) {
			// TODO: it shouldn't matter if the chunk is loaded?
			if (chunk.isLoaded()) {
				chunk.removeObserver(getOwner());
			}
		}
		observingChunks.clear();
	}

	protected void updateObserver() {
		first = false;
		List<Vector3> ungenerated = new ArrayList<>();
		final int syncDistance = getSyncDistance();
		World w = getOwner().getWorld();
		Transform t = getOwner().getPhysics().getTransform();
		Point p = t.getPosition();
		int cx = p.getChunkX();
		int cy = p.getChunkY();
		int cz = p.getChunkZ();

		HashSet<Chunk> observing = new HashSet<>((syncDistance * syncDistance * syncDistance * 3) / 2);
		Iterator<IntVector3> itr = liveObserverIterator.get();
		if (itr == null) {
			itr = new OutwardIterator(cx, cy, cz, syncDistance);
		}
		observeChunksFailed = false;
		while (itr.hasNext()) {
			IntVector3 v = itr.next();
			Chunk chunk = w.getChunk(v.getX(), v.getY(), v.getZ(), LoadOption.LOAD_ONLY);
			if (chunk != null) {
				chunk.refreshObserver(getOwner());
				observing.add(chunk);
			} else {
				ungenerated.add(new Vector3(v));
				observeChunksFailed = true;
			}
		}
		observingChunks.removeAll(observing);
		// For every chunk that we were observing but not anymore
		for (Chunk chunk : observingChunks) {
			// TODO: it shouldn't matter if the chunk is loaded?
			if (chunk.isLoaded()) {
				chunk.removeObserver(getOwner());
			}
		}
		observingChunks.clear();
		observingChunks.addAll(observing);
		if (!ungenerated.isEmpty()) {
			w.queueChunksForGeneration(ungenerated);
		}
	}

	public void copySnapshot() {
		if (first) return;
		getData().put(OBSERVER_ITERATOR, liveObserverIterator.get());
	}
}
