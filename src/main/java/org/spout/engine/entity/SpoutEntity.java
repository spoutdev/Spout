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

import org.spout.api.Source;
import org.spout.api.collision.CollisionModel;
import org.spout.api.entity.BasicController;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.event.entity.EntityControllerChangeEvent;
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
import org.spout.api.model.Model;
import org.spout.api.util.OutwardIterator;
import org.spout.api.util.Profiler;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.SpoutEngine;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;

public class SpoutEntity implements Entity {
	public static final int NOTSPAWNEDID = -1;
	//Live
	private final AtomicReference<EntityManager> entityManagerLive;
	private final AtomicReference<Controller> controllerLive;
	private final AtomicReference<Chunk> chunkLive;
	private final AtomicReference<Transform> transformLive;
	private final AtomicBoolean observerLive = new AtomicBoolean(false);
	private final AtomicInteger id = new AtomicInteger();
	private final AtomicInteger viewDistanceLive = new AtomicInteger();
	private final Transform transform = new Transform();
	//Snapshot
	private final Transform lastTransform = new Transform();
	private Chunk chunk;
	private Controller controller;
	private EntityManager entityManager;
	private boolean observer = false;
	private int viewDistance;
	//Other
	private final Set<SpoutChunk> observingChunks = new HashSet<SpoutChunk>();
	private final SpoutEngine engine;
	private final UUID uid;
	private CollisionModel collision;
	private Model model;
	private Thread owningThread;
	protected boolean justSpawned = true;

	public SpoutEntity(SpoutEngine engine, Transform transform, Controller controller, int viewDistance, UUID uid, boolean load) {
		id.set(NOTSPAWNEDID);
		this.transform.set(transform);
		this.engine = engine;

		if (uid != null) {
			this.uid = uid;
		} else {
			this.uid = UUID.randomUUID();
		}

		chunkLive = new AtomicReference<Chunk>();
		entityManagerLive = new AtomicReference<EntityManager>();
		controllerLive = new AtomicReference<Controller>();
		transformLive = new AtomicReference<Transform>();

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
		setController(controller);
	}

	public SpoutEntity(SpoutEngine engine, Transform transform, Controller controller, int viewDistance) {
		this(engine, transform, controller, viewDistance, null, true);
	}

	public SpoutEntity(SpoutEngine engine, Transform transform, Controller controller) {
		this(engine, transform, controller, -1);
	}

	public SpoutEntity(SpoutEngine engine, Point point, Controller controller) {
		this(engine, new Transform(point, Quaternion.IDENTITY, Vector3.ONE), controller);
	}

	@Override
	public void onTick(float dt) {
		Profiler.start("tick entity session");

		if (controller != null) {
			if (!isDead() && getPosition() != null && getWorld() != null) {
				Profiler.startAndStop("tick entity controller");
				controller.onTick(dt);
				Profiler.startAndStop("tick entity chunk");
				//TODO Fix, this isn't right
				chunkLive.set(getWorld().getChunkFromBlock(transform.getPosition(), LoadOption.NO_LOAD));
				entityManagerLive.set(((SpoutRegion)getRegion()).getEntityManager());
			}
		}
		Profiler.stop();
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
	public Transform getTransform() {
		return transform.copy();
	}

	@Override
	public Transform getLastTransform() {
		return lastTransform.copy();
	}

	@Override
	public void setTransform(Transform transform) {
		if (activeThreadIsValid()) {
			this.transform.set(transform);
		} else {
			this.transformLive.set(transform.copy());
		}
	}

	@Override
	public void translate(float x, float y, float z) {
		translate(new Vector3(x, y, z));
	}

	@Override
	public void translate(Vector3 amount) {
		setPosition(getPosition().add(amount));
	}

	@Override
	public void rotate(float ang, float x, float y, float z) {
		setRotation(getRotation().rotate(ang, x, y, z));
	}

	@Override
	public void rotate(Quaternion rot) {
		setRotation(getRotation().multiply(rot));
	}

	@Override
	public void scale(float x, float y, float z) {
		scale(new Vector3(x, y, z));
	}

	@Override
	public void scale(Vector3 amount) {
		setScale(getScale().multiply(amount));
	}

	@Override
	public Point getPosition() {
		return transform.getPosition();
	}

	@Override
	public Quaternion getRotation() {
		return transform.getRotation();
	}

	@Override
	public Vector3 getScale() {
		return transform.getScale();
	}

	@Override
	public void setPosition(Point position) {
		if (activeThreadIsValid()) {
			transform.setPosition(position);
		} else {
			boolean success = false;
			while (!success) {
				Transform current = transformLive.get();
				Transform next = (current == null ? lastTransform : current).copy();
				next.setPosition(position);
				success = transformLive.compareAndSet(current, next);
			}
		}
	}

	@Override
	public void setRotation(Quaternion rotation) {
		if (activeThreadIsValid()) {
			transform.setRotation(rotation);
		} else {
			boolean success = false;
			while (!success) {
				Transform current = transformLive.get();
				Transform next = (current == null ? lastTransform : current).copy();
				next.setRotation(rotation);
				success = transformLive.compareAndSet(current, next);
			}
		}
	}

	@Override
	public void setScale(Vector3 scale) {
		if (activeThreadIsValid()) {
			transform.setScale(scale);
		} else {
			boolean success = false;
			while (!success) {
				Transform current = transformLive.get();
				Transform next = (current == null ? lastTransform : current).copy();
				next.setScale(scale);
				success = transformLive.compareAndSet(current, next);
			}
		}
	}

	@Override
	public void roll(float ang) {
		setRoll(getRoll() + ang);
	}

	@Override
	public void pitch(float ang) {
		setPitch(getPitch() + ang);
	}

	@Override
	public void yaw(float ang) {
		setYaw(getYaw() + ang);
	}

	@Override
	public float getPitch() {
		return transform.getRotation().getPitch();
	}

	@Override
	public float getYaw() {
		return transform.getRotation().getYaw();
	}

	@Override
	public float getRoll() {
		return transform.getRotation().getRoll();
	}

	@Override
	public void setPitch(float pitch) {
		setAxisAngles(pitch, getYaw(), getRoll());
	}

	@Override
	public void setRoll(float roll) {
		setAxisAngles(getPitch(), getYaw(), roll);
	}

	@Override
	public void setYaw(float yaw) {
		setAxisAngles(getPitch(), yaw, getRoll());
	}

	private void setAxisAngles(float pitch, float yaw, float roll) {
		setRotation(MathHelper.rotation(pitch, yaw, roll));
	}

	private boolean activeThreadIsValid() {
		Thread current = Thread.currentThread();

		return this.owningThread == current || engine.getMainThread() == current;
	}

	@Override
	public int getId() {
		return id.get();
	}

	public void setId(int id) {
		this.id.set(id);
	}

	@Override
	public Controller getController() {
		return controllerLive.get();
	}

	public Controller getPrevController() {
		return controller;
	}

	@Override
	public void setController(Controller controller, Source source) {
		EntityControllerChangeEvent event = engine.getEventManager().callEvent(new EntityControllerChangeEvent(this, source, controller));
		Controller newController = event.getNewController();
		if (newController != null) {
			controllerLive.set(newController);
			controller.attachToEntity(this);
			controller.onAttached();
		}
	}

	@Override
	public void setController(Controller controller) {
		setController(controller, null);
	}

	@Override
	public boolean kill() {
		chunkLive.set(null);
		return true;
	}

	@Override
	public boolean isDead() {
		return id.get() != NOTSPAWNEDID && (chunkLive.get() == null || entityManagerLive.get() == null);
	}

	// TODO - needs to be made thread safe
	@Override
	public void setModel(Model model) {
		this.model = model;
	}

	// TODO - needs to be made thread safe
	@Override
	public Model getModel() {
		return model;
	}

	// TODO - needs to be made thread safe
	@Override
	public void setCollision(CollisionModel model) {
		collision = model;
		if (collision != null) {
			collision.setPosition(this.transform.getPosition());
		}
	}

	// TODO - needs to be made thread safe
	@Override
	public CollisionModel getCollision() {
		return collision;
	}

	@Override
	public boolean isSpawned() {
		return id.get() != NOTSPAWNEDID;
	}

	@Override
	public void finalizeRun() {
		
		Transform t = transformLive.getAndSet(null);
		if(t != null) {
			transform.set(t);
		}
		
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

		//Could be 1 of 3 scenarios:
		//    1.) Entity is dead (ControllerLive == null)
		//    2.) Entity is swapping controllers (ControllerLive != Controller, neither is null)
		//    3.) Entity has just spawned and has never executed copy snapshot, Controller == null, ControllerLive != null
		if (controller != controllerLive.get()) {
			//1.) Entity is dead
			if (controller != null && controllerLive.get() == null) {
				//Sanity check
				if (!isDead()) {
					throw new IllegalStateException("ControllerLive is null, but entity is not dead!");
				}

				//Kill old entity
				controller.onDeath();
			}
			//2.) Entity is changing controllers
			else if (controller != null && controllerLive.get() != null) {
				//Kill old entity
				controller.onDeath();

				//Allocate new entity
				if (entityManagerLive.get() != null) {
					entityManagerLive.get().allocate(this);
				}
			}
			//3.) Entity was just spawned, has not copied snapshots yet
			else if (controller == null && controllerLive.get() != null) {
				//Sanity check
				if (!this.justSpawned()) {
					throw new IllegalStateException("Controller is null, ControllerLive is not-null, and the entity did not just spawn.");
				}
			}
		}

		if (chunkLive.get() != chunk) {
			if (observer) {
				if (!isDead()) {
					updateObserver();
				} else {
					removeObserver();
				}
			}
			if (chunkLive.get() != null) {
				((SpoutChunk) chunkLive.get()).addEntity(this);

			}
			if (chunk != null && chunk.isLoaded()) {
				((SpoutChunk) chunk).removeEntity(this);
			}
			if (chunkLive.get() == null) {
				if (chunk != null && chunk.isLoaded()) {
					((SpoutChunk) chunk).removeEntity(this);
				}
				if (entityManagerLive.get() != null) {
					entityManagerLive.get().deallocate(this);
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

	public void copyToSnapshot() {
		chunk = chunkLive.get();
		entityManager = entityManagerLive.get();
		controller = controllerLive.get();
		viewDistance = viewDistanceLive.get();
		lastTransform.set(transform);
		justSpawned = false;
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

		return world.getRegionFromBlock(MathHelper.floor(getPosition().getX()), MathHelper.floor(getPosition().getY()), MathHelper.floor(getPosition().getZ()));
	}

	@Override
	public World getWorld() {
		return transform.getPosition().getWorld();
	}

	@Override
	public boolean is(Class<? extends Controller> clazz) {
		return clazz.isAssignableFrom(controllerLive.get().getClass());
	}

	@Override
	public void onSync() {
		//TODO Needed?
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
	public boolean isObserverLive() {
		return observerLive.get();
	}

	public void setOwningThread(Thread thread) {
		this.owningThread = thread;
	}

	@Override
	public String toString() {
		return "SpoutEntity - ID: " + this.getId() + " Controller: " + getController() + " Position: " + getPosition();
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
}
