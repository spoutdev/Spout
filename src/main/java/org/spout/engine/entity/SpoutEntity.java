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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
import org.spout.api.math.MathHelper;
import org.spout.api.math.Matrix;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.meta.SpoutMetaPlugin;
import org.spout.api.util.OutwardIterator;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.SnapshotRead;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.util.thread.snapshotable.Snapshotable;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;

public class SpoutEntity extends BaseComponentHolder implements Entity, Snapshotable {
	public static final int NOTSPAWNEDID = -1;
	//Live
	private final AtomicReference<EntityManager> entityManagerLive;
	private final AtomicReference<Chunk> chunkLive;
	private final AtomicBoolean observerLive = new AtomicBoolean(false);	
	private final AtomicBoolean removeLive = new AtomicBoolean(false);
	private final AtomicBoolean saveLive = new AtomicBoolean(true);	
	private final AtomicInteger id = new AtomicInteger();
	private final AtomicInteger viewDistanceLive = new AtomicInteger();
	//Snapshot
	private Chunk chunk;
	private EntityManager entityManager;
	private boolean observer = false;
	private int viewDistance;
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

		chunkLive = new AtomicReference<Chunk>();
		entityManagerLive = new AtomicReference<EntityManager>();

		if (transform != null && load) {
			setupInitialChunk(transform);
			getTransform().setTransform(transform);
		}

		int maxViewDistance = SpoutConfiguration.VIEW_DISTANCE.getInt() * Chunk.BLOCKS.SIZE;

		if (viewDistance < 0) {
			viewDistance = maxViewDistance;
		} else if (viewDistance > maxViewDistance) {
			viewDistance = maxViewDistance;
		}

		setViewDistance(viewDistance);
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
		//If position is dirty, set chunk/manager live values
		if (getTransform().isDirty()) {
			chunkLive.set(getWorld().getChunkFromBlock(getTransform().getPosition(), LoadOption.NO_LOAD));
			entityManagerLive.set(((SpoutRegion) getRegion()).getEntityManager());
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

	public void finalizeRun() {
		//Move entity from Region A to Region B
		if (entityManager != entityManagerLive.get()) {
			entityManager.removeEntity(this);
			//Only allow non removed entities to move to new region
			if (!isRemoved()) {
				//Add entity to Region B
				entityManagerLive.get().addEntity(this);
			}
		}
		boolean isLiveObserver = observerLive.get();
		//Entity was removed so automatically remove observer/components
		if (isRemoved()) {
			removeObserver();
			//Call onRemoved for Components and remove them
			for (Component component : values()) {
				detach(component.getClass());
			}			
		//Entity changed chunks as observer OR observer status changed so update
		} else if ((chunkLive.get() != chunk && isLiveObserver) || isLiveObserver != observer) {
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
		int cx = chunkLive.get().getX();
		int cy = chunkLive.get().getY();
		int cz = chunkLive.get().getZ();
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
		return chunk;
	}

	public Chunk getChunkLive() {
		return chunkLive.get();
	}

	@Override
	public Region getRegion() {
		return entityManager.getRegion();
	}

	@Override
	public World getWorld() {
		return getTransform().getPosition().getWorld();
	}

	public boolean justSpawned() {
		return justSpawned;
	}

	@Override
	public void setViewDistance(int distance) {
		viewDistanceLive.set(distance);
	}

	@Override
	public int getViewDistance() {
		return viewDistanceLive.get();
	}

	public int getPrevViewDistance() {
		return viewDistance;
	}

	@Override
	public void setObserver(boolean obs) {
		observerLive.set(obs);
	}

	@Override
	public boolean isObserver() {
		return observer;
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
		chunkLive.set(transform.getPosition().getWorld().getChunkFromBlock(transform.getPosition()));
		entityManagerLive.set(((SpoutRegion) chunkLive.get().getRegion()).getEntityManager());
	}

	@Override
	public void copySnapshot() {
		chunk = chunkLive.get();
		entityManager = entityManagerLive.get();
		getTransform().copySnapshot();
		viewDistance = viewDistanceLive.get();
		justSpawned = false;
	}

	public Set<SpoutChunk> getObservedChunks() {
		return observingChunks;
	}

	@Override
	@DelayedWrite
	public void remove() {
		removeLive.getAndSet(true);
	}

	@Override
	@SnapshotRead
	public boolean isRemoved() {
		return removeLive.get();
	}

	@Override
	@DelayedWrite
	public void setSavable(boolean savable) {
		saveLive.getAndSet(savable);
	}

	@Override
	@SnapshotRead
	public boolean isSavable() {
		return saveLive.get();
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
