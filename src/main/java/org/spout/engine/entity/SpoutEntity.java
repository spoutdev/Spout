/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.entity;

import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

import org.spout.api.Source;
import org.spout.api.component.BaseComponentHolder;
import org.spout.api.component.Component;
import org.spout.api.component.components.EntityComponent;
import org.spout.api.component.components.NetworkComponent;
import org.spout.api.component.components.TransformComponent;
import org.spout.api.entity.Entity;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.IntVector3;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.OutwardIterator;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.SnapshotRead;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.Snapshotable;
import org.spout.engine.util.thread.snapshotable.SnapshotableBoolean;
import org.spout.engine.util.thread.snapshotable.SnapshotableInt;
import org.spout.engine.util.thread.snapshotable.SnapshotableReference;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;

public class SpoutEntity extends BaseComponentHolder implements Entity, Snapshotable {
	public static final int NOTSPAWNEDID = -1;
	
	private final SnapshotManager snapshotManager = new SnapshotManager();
	//Snapshotable fields
	private final SnapshotableReference<EntityManager> entityManager = new SnapshotableReference<EntityManager>(snapshotManager, null);
	private final SnapshotableBoolean observer = new SnapshotableBoolean(snapshotManager, false);
	private final SnapshotableBoolean save = new SnapshotableBoolean(snapshotManager, false);
	private final SnapshotableInt id = new SnapshotableInt(snapshotManager, NOTSPAWNEDID);
	private final SnapshotableInt viewDistance = new SnapshotableInt(snapshotManager, 10);

	private volatile boolean remove = false;

	//Other
	private final Set<SpoutChunk> observingChunks = new HashSet<SpoutChunk>();
	private final UUID uid;
	protected boolean justSpawned = true;

	public SpoutEntity(Transform transform, int viewDistance, UUID uid, boolean load) {
		id.set(NOTSPAWNEDID);
		add(TransformComponent.class);
		add(NetworkComponent.class);
		
		if (uid != null) {
			this.uid = uid;
		} else {
			this.uid = UUID.randomUUID();
		}

		if (transform != null && load) {
			getTransform().setTransform(transform);
			setupInitialChunk(transform);
			getTransform().copySnapshot();
		}

		int maxViewDistance = SpoutConfiguration.VIEW_DISTANCE.getInt() * Chunk.BLOCKS.SIZE;

		if (viewDistance < 0) {
			viewDistance = maxViewDistance;
		} else if (viewDistance > maxViewDistance) {
			viewDistance = maxViewDistance;
		}

		setViewDistance(viewDistance);

		//Set all the initial snapshot values
		//Ensures there are no null/wrong snapshot values for the first tick
		snapshotManager.copyAllSnapshots();
	}

	public SpoutEntity(Transform transform, int viewDistance) {
		this(transform, viewDistance, null, true);
	}

	public SpoutEntity(Transform transform) {
		this(transform, -1);
	}

	public SpoutEntity(Point point) {
		this(new Transform(point, Quaternion.IDENTITY, Vector3.ONE));
	}

	@Override
	public void onTick(float dt) {
		for (Component component : values()) {
			component.tick(dt);
		}
	}

	@Override
	public boolean canTick() {
		return !isRemoved();
	}

	@Override
	public void tick(float dt) {
		if (canTick()) {
			onTick(dt);
		}
	}

	@Override
	public int getId() {
		return id.get();
	}

	public void setId(int id) {
		this.id.set(id);
	}

	@Override
	public boolean isSpawned() {
		return id.get() != NOTSPAWNEDID;
	}

	public void preSnapshotRun() {
		//Stubbed out in case it is needed for Entities, meanwhile SpoutPlayer overrides this.
	}

	public void finalizeRun() {
		//Entity was removed so automatically remove observer/components
		if (isRemoved()) {
			removeObserver();
			//Call onRemoved for Components and remove them
			for (Component component : values()) {
				detach(component.getClass());
			}
			return;
		}

		Chunk chunk = getChunk();
		SpoutChunk chunkLive = (SpoutChunk) getChunkLive();

		//Move entity from Region A to Region B
		if (chunkLive != null && (chunk == null || chunk.getRegion() != chunkLive.getRegion())) {
			entityManager.get().removeEntity(this);
			//Only allow non removed entities to move to new region
			if (!isRemoved()) {
				//Set the new EntityManager for the new region
				entityManager.set(chunkLive.getRegion().getEntityManager());
				//Add entity to Region B
				entityManager.getLive().addEntity(this);
			}
		}

		//Entity changed chunks as observer OR observer status changed so update
		if ((chunk != chunkLive && observer.getLive()) || observer.isDirty()) {
			updateObserver();
		}
	}

	protected void removeObserver() {
		for (SpoutChunk chunk : observingChunks) {
			if (chunk.isLoaded()) {
				chunk.removeObserver(this);
			}
		}
		observingChunks.clear();
	}

	protected void updateObserver() {
		final int viewDistance = getViewDistance() >> Chunk.BLOCKS.BITS;
		World w = getWorld();
		int cx = getChunkLive().getX();
		int cy = getChunkLive().getY();
		int cz = getChunkLive().getZ();
		HashSet<SpoutChunk> observing = new HashSet<SpoutChunk>((viewDistance * viewDistance * viewDistance * 3) / 2);
		OutwardIterator oi = new OutwardIterator(cx, cy, cz, viewDistance);
		while (oi.hasNext()) {
			IntVector3 v = oi.next();
			Chunk chunk = w.getChunk(v.getX(), v.getY(), v.getZ(), LoadOption.LOAD_GEN);
			chunk.refreshObserver(this);
			observing.add((SpoutChunk)chunk);
		}
		observingChunks.removeAll(observing);
		for (SpoutChunk chunk : observingChunks) {
			if (chunk.isLoaded()) {
				chunk.removeObserver(this);
			}
		}
		observingChunks.clear();
		observingChunks.addAll(observing);
	}

	@Override
	public Chunk getChunk() {
		return getTransform().getPosition().getChunk(LoadOption.NO_LOAD);
	}

	public Chunk getChunkLive() {
		return getTransform().getTransformLive().getPosition().getChunk(LoadOption.NO_LOAD);
	}

	@Override
	public Region getRegion() {
		return entityManager.get().getRegion();
	}

	@Override
	public World getWorld() {
		return entityManager.get().getRegion().getWorld();
	}

	public boolean justSpawned() {
		return justSpawned;
	}

	@Override
	public void setViewDistance(int distance) {
		viewDistance.set(distance);
	}

	@Override
	public int getViewDistance() {
		return viewDistance.getLive();
	}

	public int getPrevViewDistance() {
		return viewDistance.get();
	}

	@Override
	public void setObserver(boolean obs) {
		observer.set(obs);
	}

	@Override
	public boolean isObserver() {
		return observer.get();
	}

	@Override
	public String toString() {
		return "SpoutEntity - ID: " + this.getId() + " Position: " + getTransform().getPosition();
	}

	@Override
	public UUID getUID() {
		return uid;
	}

	/**
	 * Prevents stack overflow when creating an entity during chunk loading due to circle of calls
	 */
	public void setupInitialChunk(Transform transform) {
		Chunk chunk = getChunkLive();
		if (isObserver() && !chunk.isLoaded()) {
			updateObserver();
		}
		SpoutRegion region = (SpoutRegion) chunk.getRegion();
		entityManager.set(region.getEntityManager());
	}

	@Override
	public void copySnapshot() {
		snapshotManager.copyAllSnapshots();
		getTransform().copySnapshot();
		justSpawned = false;
	}

	@Override
	public void remove() {
		remove = true;
	}

	@Override
	public boolean isRemoved() {
		return remove;
	}

	@Override
	@DelayedWrite
	public void setSavable(boolean savable) {
		save.set(savable);
	}

	@Override
	@SnapshotRead
	public boolean isSavable() {
		return save.get();
	}

	@Override
	public NetworkComponent getNetwork() {
		return get(NetworkComponent.class);
	}
	
	@Override
	public TransformComponent getTransform() {
		return get(TransformComponent.class);
	}

	@Override
	public void interact(Action action, Source source) {
		for (Component component : this.values()) {
			if (component instanceof EntityComponent) {
				((EntityComponent)component).onInteract(action, source);
			}
		}
	}
}
