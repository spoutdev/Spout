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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.component.Component;
import org.spout.api.component.components.DatatableComponent;
import org.spout.api.component.components.TransformComponent;
import org.spout.api.entity.Entity;
import org.spout.api.entity.EntityComponent;
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
import org.spout.api.util.OutwardIterator;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.SnapshotRead;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.util.thread.snapshotable.Snapshotable;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;

public class SpoutEntity implements Entity, Snapshotable {
	public static final int NOTSPAWNEDID = -1;
	//Live
	private final AtomicReference<EntityManager> entityManagerLive;
	private final AtomicReference<Chunk> chunkLive;
	private final AtomicBoolean deadLive = new AtomicBoolean(false);
	private final AtomicBoolean observerLive = new AtomicBoolean(false);
	private final AtomicInteger id = new AtomicInteger();
	private final AtomicInteger viewDistanceLive = new AtomicInteger();
	private final TransformComponent transform = new TransformComponent();
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

		
		if (uid != null) {
			this.uid = uid;
		} else {
			this.uid = UUID.randomUUID();
		}

		chunkLive = new AtomicReference<Chunk>();
		entityManagerLive = new AtomicReference<EntityManager>();

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
	}

	@Override
	public boolean canTick() {
		return true;
	}

	public void tick(float dt) {
		if (canTick()) {
			onTick(dt);
		}
	}

	@Override
	public TransformComponent getTransform() {
		return transform;
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

	@Override
	public void finalizeRun() {

		//Moving from one region to another
		if (entityManager != null) {
			if (entityManager != entityManagerLive.get()) {
				//Deallocate entity
				if (entityManager.getRegion() != null) {
					entityManager.getRegion().removeEntity(this);
				} else {
					entityManager.deallocate(this);
				}
			}
		}
		if (entityManagerLive.get() != null) {
			if (entityManager != entityManagerLive.get()) {
				//Allocate entity
				entityManagerLive.get().allocate(this);
			}
		}

		if (chunkLive.get() != chunk) {
			if (observer) {
				if (!isRemoved()) {
					updateObserver();
				} else {
					removeObserver();
				}
			}
		}

		if (observerLive.get() != observer) {
			observer = !observer;
			if (observer) {
				updateObserver();
			} else {
				removeObserver();
			}
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
		//Check here to avoid the lookup
		if (entityManager != null && entityManager.getRegion() != null) {
			return entityManager.getRegion();
		}
		//Lookup
		World world = getWorld();
		if (world == null) {
			return null;
		}

		return world.getRegionFromBlock(MathHelper.floor(getTransform().getPosition().getX()), MathHelper.floor(getTransform().getPosition().getY()), MathHelper.floor(getTransform().getPosition().getZ()));
	}

	@Override
	public World getWorld() {
		return transform.getPosition().getWorld();
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

	public Matrix getModelMatrix()
	{
		Matrix trans = MathHelper.translate(transform.getPosition());
		Matrix rot = MathHelper.rotate(transform.getRotation());

		return rot.multiply(trans);
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
		viewDistance = viewDistanceLive.get();
		justSpawned = false;
	}

	public Set<SpoutChunk> getObservedChunks() {
		return observingChunks;
	}

	@Override
	public EntityComponent addComponent(Component component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeComponent(Class<? extends Component> component) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EntityComponent getComponent(Class<? extends Component> component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasComponent(Class<? extends Component> component) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DatatableComponent getDatatable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@DelayedWrite
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	@SnapshotRead
	public boolean isRemoved() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@DelayedWrite
	public void setSavable(boolean savable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@SnapshotRead
	public boolean isSavable() {
		// TODO Auto-generated method stub
		return false;
	}
}