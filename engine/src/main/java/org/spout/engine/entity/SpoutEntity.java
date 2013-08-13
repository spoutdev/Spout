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
package org.spout.engine.entity;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.spout.api.Engine;
import org.spout.api.component.BaseComponentOwner;
import org.spout.api.component.Component;
import org.spout.api.component.entity.EntityComponent;
import org.spout.api.component.entity.ModelComponent;
import org.spout.api.component.entity.NetworkComponent;
import org.spout.api.component.entity.PhysicsComponent;
import org.spout.api.datatable.SerializableMap;
import org.spout.api.entity.Entity;
import org.spout.api.entity.EntitySnapshot;
import org.spout.api.event.entity.EntityInteractEvent;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.thread.annotation.DelayedWrite;
import org.spout.api.util.thread.annotation.SnapshotRead;
import org.spout.engine.SpoutClient;
import org.spout.engine.component.entity.SpoutModelComponent;
import org.spout.engine.component.entity.SpoutPhysicsComponent;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.Snapshotable;
import org.spout.engine.util.thread.snapshotable.SnapshotableBoolean;
import org.spout.engine.util.thread.snapshotable.SnapshotableReference;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;

public class SpoutEntity extends BaseComponentOwner implements Entity, Snapshotable {
	public static final int NOTSPAWNEDID = Integer.MIN_VALUE;
	private final SnapshotManager snapshotManager = new SnapshotManager();
	//Snapshotable fields
	private final SnapshotableReference<EntityManager> entityManager = new SnapshotableReference<>(snapshotManager, null);
	private final SnapshotableBoolean save = new SnapshotableBoolean(snapshotManager, false);
	private final AtomicInteger id = new AtomicInteger(NOTSPAWNEDID);
	private volatile boolean remove = false;
	//Other
	private final Engine engine;
	private final UUID uid;
	protected boolean justSpawned = true;
	//For faster access
	private final SpoutPhysicsComponent physics;
	protected NetworkComponent network;
	private Class<? extends Component>[] initialComponents = null;

	public SpoutEntity(Engine engine, Transform transform) {
		this(engine, transform, null, (byte[]) null, (Class<? extends Component>[]) null);
	}

	public SpoutEntity(Engine engine, Point point) {
		this(engine, new Transform(point, Quaternion.IDENTITY, Vector3.ONE));
	}

	public SpoutEntity(Engine engine, SpoutEntitySnapshot snapshot) {
		this(engine, snapshot.getTransform(), snapshot.getUID(), snapshot.getDataMap().serialize(), snapshot.getComponents().toArray(new Class[0]));
	}

	public SpoutEntity(Engine engine, Point point, Class<? extends Component>... components) {
		this(engine, new Transform(point, Quaternion.IDENTITY, Vector3.ONE), null, (byte[]) null, components);
	}

	protected SpoutEntity(Engine engine, Transform transform, UUID uid, SerializableMap dataMap, Class<? extends Component>... components) {
		this(engine, transform, uid, dataMap.serialize(), components);
	}

	public SpoutEntity(Engine engine, Transform transform, UUID uid, byte[] dataMap, Class<? extends Component>... components) {
		if (transform == null) {
			throw new IllegalArgumentException("Entities must always have a valid transform");
		}

		id.set(NOTSPAWNEDID);
		this.engine = engine;

		physics = (SpoutPhysicsComponent) add(PhysicsComponent.class);

		if (!(this instanceof SpoutPlayer)) {
			network = add(NetworkComponent.class);
		}

		if (uid != null) {
			this.uid = uid;
		} else {
			this.uid = UUID.randomUUID();
		}

		physics.setTransform(transform, false);

		if (components != null && components.length > 0) {
			initialComponents = components;
		}

		if (dataMap != null) {
			try {
				this.getData().deserialize(dataMap);
			} catch (IOException e) {
				engine.getLogger().log(Level.SEVERE, "Unable to deserialize entity data", e);
			}
		}

		setupInitialChunk();
	}

	@Override
	public Engine getEngine() {
		return engine;
	}

	@Override
	protected <T extends Component> T add(Class<T> type, boolean attach) {
		if (type.equals(PhysicsComponent.class)) {
			return super.add(type, SpoutPhysicsComponent.class, attach);
		} else if (type.equals(ModelComponent.class)) {
			T component = super.add(type, SpoutModelComponent.class, attach);
			if (getEngine() instanceof SpoutClient) {
				((SpoutClient) getEngine()).getRenderer().getEntityRenderer().add((SpoutModelComponent) component);
			}
			return component;
		} else if (NetworkComponent.class.isAssignableFrom(type) && get(type) == null) {
			//Detach old NetworkComponent
			super.detach(NetworkComponent.class);
			//Attach new one
			this.network = (NetworkComponent) super.add(type, attach);
			return (T) network;
		}
		return super.add(type, attach);
	}

	@Override
	protected <T extends Component> T detach(Class<? extends Component> type, boolean force) {
		if (ModelComponent.class.equals(type)) {
			T component = super.detach(type);
			if (getEngine() instanceof SpoutClient) {
				((SpoutClient) getEngine()).getRenderer().getEntityRenderer().remove((SpoutModelComponent) component);
			}
			return component;
		} else if (NetworkComponent.class.isAssignableFrom(type) && !force) {
			return (T) network;
		}
		return super.detach(type);
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

	public void finalizeRun() {
		SpoutChunk chunkLive = (SpoutChunk) getChunkLive();
		SpoutChunk chunk = (SpoutChunk) getChunk();

		//Entity was removed so automatically remove components
		if (isRemoved()) {
			//Get rid of physics
			physics.deactivate();
			//Call onRemoved for Components and remove them
			for (Component component : values()) {
				detach(component.getClass(), true);
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
			boolean activated = physics.isActivated();
			physics.deactivate();
			entityManager.get().removeEntity(this);
			//Set the new EntityManager for the new region
			entityManager.set(chunkLive.getRegion().getEntityManager());
			//Add entity to Region B
			entityManager.getLive().addEntity(this);
			if (activated) {
				physics.activate(chunkLive.getRegion());
			}
		}

		this.getNetwork().finalizeRun(((SpoutPhysicsComponent) getPhysics()).getTransformLive().copy());
	}

	public void preSnapshotRun() {
		this.getNetwork().preSnapshotRun(((SpoutPhysicsComponent) getPhysics()).getTransformLive().copy());
	}

	@Override
	public Chunk getChunk() {
		return physics.getPosition().getChunk(LoadOption.NO_LOAD);
	}

	public Chunk getChunkLive() {
		return physics.getTransformLive().getPosition().getChunk(LoadOption.NO_LOAD);
	}

	@Override
	public Region getRegion() {
		// TODO: we should have an entityManager
		//return entityManager.get().getRegion();
		// TODO: are we going to make this a thing...or?
		if (TickStage.testStage(~TickStage.SNAPSHOT)) {
			return physics.getPosition().getRegion(LoadOption.LOAD_GEN);
		} else {
			return physics.getPosition().getRegion(LoadOption.NO_LOAD);
		}
	}

	@Override
	public World getWorld() {
		return physics.getPosition().getWorld();
	}

	@Override
	public String toString() {
		return "SpoutEntity {id= " + this.getId() + ", " + physics + "}";
	}

	@Override
	public UUID getUID() {
		return uid;
	}

	/**
	 * Prevents stack overflow when creating an entity during chunk loading due to circle of calls
	 */
	public void setupInitialChunk() {
		physics.copySnapshot();
		SpoutRegion region = (SpoutRegion) physics.getTransformLive().getPosition().getRegion(LoadOption.LOAD_GEN);

		entityManager.set(region.getEntityManager());

		snapshotManager.copyAllSnapshots();

		if (initialComponents != null) {
			this.add(initialComponents);
			initialComponents = null;
		}
	}

	@Override
	public void copySnapshot() {
		physics.copySnapshot();
		snapshotManager.copyAllSnapshots();

		justSpawned = false;

		network.copySnapshot();
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
	public PhysicsComponent getPhysics() {
		return physics;
	}

	@Override
	public NetworkComponent getNetwork() {
		return network;
	}

	@Override
	public EntitySnapshot snapshot() {
		return new SpoutEntitySnapshot(this);
	}

	@Override
	@Deprecated
	public void interact(final EntityInteractEvent<?> event) {
		for (final Component component : values()) {
			if (component instanceof EntityComponent) {
				((EntityComponent) component).onInteract(event);
			}
		}
	}
}
