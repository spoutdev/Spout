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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.component.BaseComponentHolder;
import org.spout.api.component.Component;
import org.spout.api.component.components.EntityComponent;
import org.spout.api.component.components.NetworkComponent;
import org.spout.api.component.components.PhysicsComponent;
import org.spout.api.component.components.PredictableTransformComponent;
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
import org.spout.api.plugin.Platform;
import org.spout.api.util.OutwardIterator;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.SnapshotRead;

import org.spout.engine.SpoutConfiguration;
import org.spout.engine.entity.component.SpoutPhysicsComponent;
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
	private final AtomicInteger id = new AtomicInteger(NOTSPAWNEDID);
	private final SnapshotableInt viewDistance = new SnapshotableInt(snapshotManager, 10);
	private volatile boolean remove = false;
	//Other
	private final Set<SpoutChunk> observingChunks = new HashSet<SpoutChunk>();
	private final UUID uid;
	protected boolean justSpawned = true;

	public SpoutEntity(Transform transform, int viewDistance, UUID uid, boolean load, byte[] dataMap, Class<? extends Component>... components) {
		id.set(NOTSPAWNEDID);

		if (Spout.getPlatform() == Platform.CLIENT) {
			add(PredictableTransformComponent.class);
		} else {
			add(TransformComponent.class);
		}

		add(NetworkComponent.class);

		if (uid != null) {
			this.uid = uid;
		} else {
			this.uid = UUID.randomUUID();
		}

		if (transform != null) {
			getTransform().setTransform(transform);
			getTransform().copySnapshot();
		}

		if (transform != null && load) {
			setupInitialChunk(transform);
		}

		int maxViewDistance = SpoutConfiguration.VIEW_DISTANCE.getInt() * Chunk.BLOCKS.SIZE;

		if (viewDistance < 0) {
			viewDistance = maxViewDistance;
		} else if (viewDistance > maxViewDistance) {
			viewDistance = maxViewDistance;
		}

		setViewDistance(viewDistance);

		if (dataMap != null) {
			try {
				this.getData().deserialize(dataMap);
			} catch (IOException e) {
				Spout.getLogger().log(Level.SEVERE, "Unable to deserialize entity data", e);
			}
		}

		if (components != null && components.length > 0) {
			this.add(components);
		}

		//Set all the initial snapshot values
		//Ensures there are no null/wrong snapshot values for the first tick
		snapshotManager.copyAllSnapshots();
	}

	@Override
	protected <T extends Component> T add(Class<T> type, boolean attach) {
		if (type.equals(PhysicsComponent.class)) {
			return super.add(type, SpoutPhysicsComponent.class, attach);
		}
		return super.add(type, attach);
	}

	public SpoutEntity(Transform transform, int viewDistance) {
		this(transform, viewDistance, null, true, null, (Class<? extends Component>[]) null);
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
		SpoutChunk chunkLive = (SpoutChunk) getChunkLive();
		SpoutChunk chunk = (SpoutChunk) getChunk();

		//Entity was removed so automatically remove observer/components
		if (isRemoved()) {
			removeObserver();
			//Call onRemoved for Components and remove them
			for (Component component : values()) {
				detach(component.getClass());
			}
			//Track entities w/their chunks
			if (chunk != null) {
				chunk.onEntityLeave(this);
			}
			return;
		}

		//Track entities w/their chunks, for saving purposes
		if (!(this instanceof SpoutPlayer)) {
			if (chunk != chunkLive) {
				if (chunk != null) {
					chunk.onEntityLeave(this);
				}
				if (chunkLive != null) {
					chunkLive.onEntityEnter(this);
				}
			}
		}

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
		Transform t = getTransform().getTransform();
		Chunk c = w.getChunkFromBlock(t.getPosition(), LoadOption.LOAD_GEN);
		int cx = c.getX();
		int cy = c.getY();
		int cz = c.getZ();
		HashSet<SpoutChunk> observing = new HashSet<SpoutChunk>((viewDistance * viewDistance * viewDistance * 3) / 2);
		OutwardIterator oi = new OutwardIterator(cx, cy, cz, viewDistance);
		while (oi.hasNext()) {
			IntVector3 v = oi.next();
			Chunk chunk = w.getChunk(v.getX(), v.getY(), v.getZ(), LoadOption.LOAD_GEN);
			chunk.refreshObserver(this);
			observing.add((SpoutChunk) chunk);
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

	public Set<SpoutChunk> getObservingChunks(){
		return observingChunks;
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
		if (isObserver()) {
			updateObserver();
		}
		SpoutRegion region = (SpoutRegion) getTransform().getTransformLive().getPosition().getChunk(LoadOption.LOAD_GEN).getRegion();
		entityManager.set(region.getEntityManager());
	}

	@Override
	public void copySnapshot() {
		getTransform().copySnapshot();
		snapshotManager.copyAllSnapshots();

		PhysicsComponent physics = get(PhysicsComponent.class);
		if (physics != null) {
			((SpoutPhysicsComponent) physics).copySnapshot();
		}

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
	public void interact(Action action, Entity source) {
		for (Component component : this.values()) {
			if (component instanceof EntityComponent) {
				((EntityComponent) component).onInteract(action, source);
			}
		}
	}
}
