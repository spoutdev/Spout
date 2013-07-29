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

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Platform;

import org.spout.api.Spout;
import org.spout.api.entity.Player;
import org.spout.api.event.ProtocolEvent;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.Session;
import org.spout.api.util.SyncedStringMap;

/**
 * The networking behind {@link org.spout.api.entity.Entity}s.
 */
public abstract class NetworkComponent extends EntityComponent {
	private static final SyncedStringMap protocolMap = SyncedStringMap.create(null, new MemoryStore<Integer>(), 0, 256, "componentProtocols");
	//TODO: Move all observer code to NetworkComponent
	private AtomicBoolean observer = new AtomicBoolean(false);
	private AtomicInteger syncDistance = new AtomicInteger(0);

	@Override
	public boolean isDetachable() {
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
		return observer.get();
	}

	/**
	 * Sets the observer status for the owning {@link org.spout.api.entity.Entity}.
	 *
	 * @param observer True if observer, false if not
	 */
	public void setObserver(final boolean observer) {
		this.observer.set(observer);
	}

	/**
	 * Gets the sync distance of the owning {@link org.spout.api.entity.Entity}.
	 * </p>
	 * Sync distance is a value indicating the radius outwards from the entity where network updates (such as chunk creation) will be triggered.
	 * @return The current sync distance
	 */
	public int getSyncDistance() {
		return syncDistance.get();
	}

	/**
	 * Sets the sync distance of the owning {@link org.spout.api.entity.Entity}.
	 *
	 * @param syncDistance The new sync distance
	 */
	public void setSyncDistance(final int syncDistance) {
		//TODO: Enforce server maximum (but that is set in Spout...)
		this.syncDistance.set(syncDistance * Chunk.BLOCKS.SIZE);
	}

	/**
	 * Called when the owner is set to be synchronized to other NetworkComponents.
	 *
	 * TODO: Common logic between Spout and a plugin needing to implement this component?
	 * TODO: Add sequence checks to the PhysicsComponent to prevent updates to live?
	 *
	 * @param live A copy of the owner's live transform state
	 */
	public void finalizeRun(final Transform live) {

	}

	/**
	 * Called just before a snapshot is taken of the owner.
	 *
	 * TODO: Add sequence checks to the PhysicsComponent to prevent updates to live?
	 *
	 * @param live A copy of the owner's live transform state
	 */
	public void preSnapshot(final Transform live) {

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
}
