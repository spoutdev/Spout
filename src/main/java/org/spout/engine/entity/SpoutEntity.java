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

import java.awt.List;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.component.BaseComponentHolder;
import org.spout.api.component.Component;
import org.spout.api.component.components.NetworkComponent;
import org.spout.api.component.components.TransformComponent;
import org.spout.api.entity.Entity;
import org.spout.api.entity.EntityType;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.SnapshotRead;
import org.spout.engine.util.thread.snapshotable.Snapshotable;
import org.spout.engine.world.SpoutRegion;

public class SpoutEntity extends BaseComponentHolder implements Entity, Snapshotable {
	public static final int NOTSPAWNEDID = -1;
	//Live
	private final AtomicReference<EntityManager> entityManagerLive;
	private final AtomicReference<Chunk> chunkLive;	
	private final AtomicBoolean removeLive = new AtomicBoolean(false);
	private final AtomicBoolean saveLive = new AtomicBoolean(true);	
	private final AtomicInteger id = new AtomicInteger();
	private final TransformComponent transformComponent = new TransformComponent();
	private final NetworkComponent networkComponent = new NetworkComponent();
	//Snapshot
	private Chunk chunk;
	private EntityManager entityManager;
	//Other
	private final UUID uid;
	protected boolean justSpawned = false;

	public SpoutEntity(Transform transform, UUID uid, boolean load) {
		id.set(NOTSPAWNEDID);
		
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
		
		addComponent(transformComponent);
		addComponent(networkComponent);
	}

	public SpoutEntity(Transform transform) {
		this(transform, null, true);
	}

	public SpoutEntity(Point point) {
		this(new Transform(point, Quaternion.IDENTITY, Vector3.ONE));
	}

	@Override
	public void onTick(float dt) {
		for (Component component : getComponents()) {
			component.tick(dt);
		}
		//If position is dirty, set chunk/manager live values
		if (transformComponent.isDirty()) {
			chunkLive.set(getWorld().getChunkFromBlock(getTransform().getPosition(), LoadOption.NO_LOAD));
			entityManagerLive.set(((SpoutRegion) getRegion()).getEntityManager());
		}
	}

	@Override
	public boolean canTick() {
		return !isRemoved();
	}

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
		//Entity was removed so automatically remove components
		if (isRemoved()) {
			//Call onRemoved for Components and remove them
			for (Component component : getComponents()) {
				component.onRemoved();
				removeComponent(component.getClass());
			}
		}
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
		return transformComponent.getPosition().getWorld();
	}

	public boolean justSpawned() {
		return justSpawned;
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
		transformComponent.copySnapshot();
		justSpawned = false;
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
	public NetworkComponent getNetworkComponent() {
		return networkComponent;
	}
	
	@Override
	public TransformComponent getTransform() {
		return transformComponent;
	}

	@Override
	public void applyType(EntityType type) {
		if (type == null) {
			return;
		}
		for (Component component : type.getComponents()) {
			addComponent(component);
		}
		type.init(this);
	}

	@Override
	public void removeType(EntityType type) {
		if (type == null) {
			return;
		}
		for (Component component : type.getComponents()) {
			if (hasComponent(component.getClass())) {
				removeComponent(component.getClass());
			}
		}
	}	
}
