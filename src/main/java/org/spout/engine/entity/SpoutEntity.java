/*
 * This file is part of Spout (http://www.spout.org/).
 *
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.collision.CollisionModel;
import org.spout.api.collision.CollisionStrategy;
import org.spout.api.collision.CollisionVolume;
import org.spout.api.datatable.DatatableTuple;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.datatable.value.DatatableBool;
import org.spout.api.datatable.value.DatatableFloat;
import org.spout.api.datatable.value.DatatableInt;
import org.spout.api.datatable.value.DatatableSerializable;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.entity.PlayerController;
import org.spout.api.entity.component.EntityComponent;
import org.spout.api.event.entity.EntityControllerChangeEvent;
import org.spout.api.event.entity.EntityHealthChangeEvent;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.inventory.Inventory;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.model.Model;
import org.spout.api.player.Player;
import org.spout.api.util.concurrent.OptimisticReadWriteLock;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.SpoutEngine;
import org.spout.engine.net.SpoutSession;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;

public class SpoutEntity implements Entity {
	public static final int NOTSPAWNEDID = -1;

	//Thread-safe
	private final AtomicReference<EntityManager> entityManagerLive;
	private final AtomicReference<Controller> controllerLive;
	private final AtomicReference<Chunk> chunkLive;
	private final ArrayList<AtomicReference<EntityComponent>> components = new ArrayList<AtomicReference<EntityComponent>>();
	private final AtomicBoolean observerLive = new AtomicBoolean(false);
	private final AtomicInteger health = new AtomicInteger(1), maxHealth = new AtomicInteger(1);
	private final AtomicInteger id = new AtomicInteger();
	private final AtomicInteger viewDistanceLive = new AtomicInteger();

	private static final long serialVersionUID = 1L;
	private static final Transform DEAD = new Transform(Point.invalid, Quaternion.IDENTITY, Vector3.ZERO);
	private final OptimisticReadWriteLock lock = new OptimisticReadWriteLock();
	private final Transform transform = new Transform();
	private boolean justSpawned = true;
	private boolean observer = false;
	private float pitch, yaw, roll;
	private int viewDistance;
	private Chunk chunk;
	private CollisionModel collision;
	private Controller controller;
	private EntityManager entityManager;
	private GenericDatatableMap map;
	private Model model;
	private Thread owningThread;
	private Transform lastTransform = transform;

	public SpoutEntity(SpoutEngine engine, Transform transform, Controller controller, int viewDistance) {
		id.set(NOTSPAWNEDID);
		this.transform.set(transform);

		chunkLive = new AtomicReference<Chunk>();
		entityManagerLive = new AtomicReference<EntityManager>();
		controllerLive = new AtomicReference<Controller>();

		if (transform != null) {
			chunkLive.set(transform.getPosition().getWorld().getChunk(transform.getPosition()));
			entityManagerLive.set(((SpoutRegion) chunkLive.get().getRegion()).getEntityManager());
		}

		map = new GenericDatatableMap();
		this.viewDistance = viewDistance;
		viewDistanceLive.set(viewDistance);

		//Only call setController if the controller was null (indicates this entity was just created)
		if (controller != null) {
			this.controller = controller;
			setController(controller);
		}
	}

	public SpoutEntity(SpoutEngine engine, Transform transform, Controller controller) {
		this(engine, transform, controller, SpoutConfiguration.VIEW_DISTANCE.getInt() * SpoutChunk.CHUNK_SIZE);
	}

	public SpoutEntity(SpoutEngine engine, Point point, Controller controller) {
		this(engine, new Transform(point, Quaternion.IDENTITY, Vector3.ONE), controller);
	}

	public void onTick(float dt) {
		lastTransform = transform.copy();
		Vector3 ang = this.transform.getRotation().getAxisAngles();
		pitch = ang.getZ();
		yaw = ang.getY();
		roll = ang.getX();

		if (controller != null && controller.getParent() != null && !isDead()) {
			controller.onTick(dt);
		}
		updateTransformAxisAngles();

		if (controllerLive.get() instanceof PlayerController) {
			Player player = ((PlayerController) controllerLive.get()).getPlayer();
			if (player != null && player.getSession() != null) {
				((SpoutSession) player.getSession()).pulse();
			}
		}
	}

	/**
	 * Called when the tick is finished and collisions need to be resolved and
	 * move events fired
	 */
	public void resolve() {
		//Don't need to do collisions if we have no collision volume
		if (this.collision == null || this.getWorld() == null) {
			return;
		}

		//Resolve Collisions Here
		final Point location = this.transform.getPosition();

		//Move the collision volume to the new position
		this.collision.setPosition(location);

		List<CollisionVolume> colliding = ((SpoutWorld) location.getWorld()).getCollidingObject(this.collision);

		Vector3 offset = this.lastTransform.getPosition().subtract(location);
		for (CollisionVolume box : colliding) {
			Vector3 collision = this.collision.resolve(box);
			if (collision != null) {
				collision = collision.subtract(location);

				if (collision.getX() != 0F) {
					offset = new Vector3(collision.getX(), offset.getY(), offset.getZ());
				}
				if (collision.getY() != 0F) {
					offset = new Vector3(offset.getX(), collision.getY(), offset.getZ());
				}
				if (collision.getZ() != 0F) {
					offset = new Vector3(offset.getX(), offset.getY(), collision.getZ());
				}

				if (this.getCollision().getStrategy() == CollisionStrategy.SOLID && box.getStrategy() == CollisionStrategy.SOLID) {
					this.setPosition(location.add(offset));
				}
				if (controllerLive.get() != null) {
					Block b = this.transform.getPosition().getWorld().getBlock((int) box.getPosition().getX(), (int) box.getPosition().getY(), (int) box.getPosition().getZ());
					controllerLive.get().onCollide(b.clone());
				}
			}
		}

		//Check to see if we should fire off a Move event
	}

	private final boolean isValidAccess() {
		Thread current = Thread.currentThread();
		return this.owningThread == current || Spout.getEngine().getMainThread() == current;
	}

	@Override
	public Transform getTransform() {
		return transform.copy();
	}

	@Override
	public void setTransform(Transform transform) {
		this.transform.set(transform);
	}

	@Override
	public void translate(Vector3 amount) {
		if (!isValidAccess()) {
			if (Spout.getEngine().debugMode()) {
				throw new IllegalAccessError("Tried to translate from another thread {current: " + Thread.currentThread().getPriority() + " owner: " + owningThread.getName() + "}!");
			}
			return;
		}
		setPosition(getPosition().add(amount));
	}

	@Override
	public void translate(float x, float y, float z) {
		translate(new Vector3(x, y, z));
	}

	@Override
	public void rotate(float ang, float x, float y, float z) {
		if (!isValidAccess()) {
			if (Spout.getEngine().debugMode()) {
				throw new IllegalAccessError("Tried to rotate from another thread {current: " + Thread.currentThread().getPriority() + " owner: " + owningThread.getName() + "}!");
			}
			return;
		}
		setRotation(getRotation().rotate(ang, x, y, z));
	}

	@Override
	public void rotate(Quaternion rot) {
		if (!isValidAccess()) {
			if (Spout.getEngine().debugMode()) {
				throw new IllegalAccessError("Tried to rotation from another thread {current: " + Thread.currentThread().getPriority() + " owner: " + owningThread.getName() + "}!");
			}
			return;
		}
		setRotation(getRotation().multiply(rot));
	}

	@Override
	public void scale(Vector3 amount) {
		if (!isValidAccess()) {
			if (Spout.getEngine().debugMode()) {
				throw new IllegalAccessError("Tried to scale from another thread {current: " + Thread.currentThread().getPriority() + " owner: " + owningThread.getName() + "}!");
			}
			return;
		}
		setScale(getScale().multiply(amount));
	}

	@Override
	public void scale(float x, float y, float z) {
		scale(new Vector3(x, y, z));
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
		return this.transform.getScale();
	}

	@Override
	public void setPosition(Point position) {
		if (!isValidAccess()) {
			if (Spout.getEngine().debugMode()) {
				throw new IllegalAccessError("Tried to set position from another thread {current: " + Thread.currentThread().getPriority() + " owner: " + owningThread.getName() + "}!");
			}
			return;
		}
		this.transform.setPosition(position);
	}

	@Override
	public void setRotation(Quaternion rotation) {
		if (!isValidAccess()) {
			if (Spout.getEngine().debugMode()) {
				throw new IllegalAccessError("Tried to set rotation from another thread {current: " + Thread.currentThread().getPriority() + " owner: " + owningThread.getName() + "}!");
			}
			return;
		}
		this.transform.setRotation(rotation);
	}

	@Override
	public void setScale(Vector3 scale) {
		if (!isValidAccess()) {
			if (Spout.getEngine().debugMode()) {
				throw new IllegalAccessError("Tried to set scale from another thread {current: " + Thread.currentThread().getPriority() + " owner: " + owningThread.getName() + "}!");
			}
			return;
		}
		this.transform.setScale(scale);
	}

	@Override
	public void roll(float ang) {
		if (!isValidAccess()) {
			if (Spout.getEngine().debugMode()) {
				throw new IllegalAccessError("Tried to roll from another thread {current: " + Thread.currentThread().getPriority() + " owner: " + owningThread.getName() + "}!");
			}
			return;
		}
		setRoll(getRoll()+ang);
	}

	@Override
	public void pitch(float ang) {
		if (!isValidAccess()) {
			if (Spout.getEngine().debugMode()) {
				throw new IllegalAccessError("Tried to pitch from another thread {current: " + Thread.currentThread().getPriority() + " owner: " + owningThread.getName() + "}!");
			}
			return;
		}
		setPitch(getPitch()+ang);
	}

	@Override
	public void yaw(float ang) {
		if (!isValidAccess()) {
			if (Spout.getEngine().debugMode()) {
				throw new IllegalAccessError("Tried to yaw from another thread {current: " + Thread.currentThread().getPriority() + " owner: " + owningThread.getName() + "}!");
			}
			return;
		}
		setYaw(getYaw()+yaw);
	}

	@Override
	public float getPitch() {
		return pitch;
	}

	@Override
	public float getYaw() {
		return yaw;
	}

	@Override
	public float getRoll() {
		return roll;
	}

	@Override
	public void setPitch(float ang) {
		if (!isValidAccess()) {
			if (Spout.getEngine().debugMode()) {
				throw new IllegalAccessError("Tried to set pitch from another thread {current: " + Thread.currentThread().getPriority() + " owner: " + owningThread.getName() + "}!");
			}
			return;
		}
		pitch = ang;
		updateTransformAxisAngles();
	}

	@Override
	public void setRoll(float ang) {
		if (!isValidAccess()) {
			if (Spout.getEngine().debugMode()) {
				throw new IllegalAccessError("Tried to set scale from another thread {current: " + Thread.currentThread().getPriority() + " owner: " + owningThread.getName() + "}!");
			}
			return;
		}
		roll = ang;
		updateTransformAxisAngles();
	}

	@Override
	public void setYaw(float ang) {
		if (!isValidAccess()) {
			if (Spout.getEngine().debugMode()) {
				throw new IllegalAccessError("Tried to set scale from another thread {current: " + Thread.currentThread().getPriority() + " owner: " + owningThread.getName() + "}!");
			}
			return;
		}
		yaw = ang;
		updateTransformAxisAngles();
	}

	@Override
	public int getHealth() {
		return health.get();
	}

	@Override
	public void setHealth(int health, Source source) {

		// Event handling
		int oldHealth = this.health.get();
		int change = health - oldHealth;
		EntityHealthChangeEvent event = Spout.getEventManager().callEvent(new EntityHealthChangeEvent(this, source, change));
		int newHealth = oldHealth + event.getChange();

		//Enforce max health
		if (newHealth >= maxHealth.get()) {
			this.health.getAndSet(maxHealth.get());
		} else {
			this.health.getAndSet(newHealth);
		}
	}

	@Override
	public void setMaxHealth(int maxHealth) {
		this.maxHealth.getAndSet(maxHealth);
	}

	@Override
	public int getMaxHealth() {
		return maxHealth.get();
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
		EntityControllerChangeEvent event = Spout.getEventManager().callEvent(new EntityControllerChangeEvent(this, source, controller));
		Controller newController = event.getNewController();
		controllerLive.set(controller);
		if (newController != null) {
			controller.attachToEntity(this);
			if (controller instanceof PlayerController) {
				setObserver(true);
			}
			controller.onAttached();
		}
	}

	@Override
	public void setController(Controller controller) {
		setController(controller, null);
	}

	@Override
	public boolean kill() {
		Point p = transform.getPosition();
		boolean alive = p.getWorld() != null;
		transform.set(DEAD);
		chunkLive.set(null);
		entityManagerLive.set(null);
		return alive;
	}

	@Override
	public boolean isDead() {
		return id.get() != NOTSPAWNEDID && transform.getPosition().getWorld() == null;
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
		if (entityManager != null) {
			if (entityManager != entityManagerLive.get() || controller != controllerLive.get()) {
				SpoutRegion r = (SpoutRegion) chunk.getRegion();
				r.removeEntity(this);
				if (entityManagerLive.get() == null) {
					controller.onDeath();
					if (controller instanceof PlayerController) {
						Player p = ((PlayerController) controller).getPlayer();
						p.getNetworkSynchronizer().onDeath();
					}
				}
			}
		}
		if (entityManagerLive.get() != null) {
			if (entityManager != entityManagerLive.get() || controller != controllerLive.get()) {
				entityManagerLive.get().allocate(this);
			}
		}

		if (chunkLive.get() != chunk) {
			if (chunkLive.get() != null) {
				((SpoutChunk) chunkLive.get()).addEntity(this);
				if (observer) {
					chunkLive.get().refreshObserver(this);
				}
			}
			if (chunk != null && chunk.isLoaded()) {
				((SpoutChunk) chunk).removeEntity(this);
				if (observer) {
					chunk.removeObserver(this);
				}
			}
			if (chunkLive.get() == null) {
				if (chunk != null && chunk.isLoaded()) {
					((SpoutChunk) chunk).removeEntity(this);
					if (observer) {
						chunk.removeObserver(this);
					}
				}
				if (entityManagerLive.get() != null) {
					entityManagerLive.get().deallocate(this);
				}
			}
		}

		if (observerLive.get() != observer) {
			observer = !observer;
			if (observer) {
				chunkLive.get().refreshObserver(this);
			} else {
				chunkLive.get().removeObserver(this);
			}
		}
	}

	public void copyToSnapshot() {
		if (chunk != chunkLive.get()) {
			chunk = chunkLive.get();
		}
		if (entityManager != entityManagerLive.get()) {
			entityManager = entityManagerLive.get();
		}
		if (controller != controllerLive.get()) {
			controller = controllerLive.get();
		}
		if (viewDistance != viewDistanceLive.get()) {
			viewDistance = viewDistanceLive.get();
		}
		justSpawned = false;
	}

	@Override
	public Chunk getChunk() {
		World world = getWorld();
		if (world == null) {
			return null;
		} else {
			return world.getChunkFromBlock(MathHelper.floor(getPosition().getX()), MathHelper.floor(getPosition().getY()), MathHelper.floor(getPosition().getZ()));
		}
	}

	public Chunk getChunkLive() {
		while (true) {
			int seq = lock.readLock();
			Point position = transform.getPosition();
			World w = position.getWorld();
			if (w == null) {
				if (lock.readUnlock(seq)) {
					return null;
				}
			} else {
				Chunk c = w.getChunk(position, true);
				if (lock.readUnlock(seq)) {
					return c;
				}
			}
		}
	}

	@Override
	public Region getRegion() {
		World world = getWorld();
		if (world == null) {
			return null;
		} else {
			return world.getRegionFromBlock(MathHelper.floor(getPosition().getX()), MathHelper.floor(getPosition().getY()), MathHelper.floor(getPosition().getZ()));
		}
	}

	public Region getRegionLive() {
		while (true) {
			int seq = lock.readLock();
			Point position = transform.getPosition();
			World world = position.getWorld();
			if (world == null && lock.readUnlock(seq)) {
				return null;
			} else {
				Region r = world.getRegion(position, true);
				if (lock.readUnlock(seq)) {
					return r;
				}
			}
		}
	}

	@Override
	public World getWorld() {
		return transform.getPosition().getWorld();
	}

	@Override
	public boolean is(Class<? extends Controller> clazz) {
		return clazz.isAssignableFrom(controllerLive.get().getClass());
	}

	// TODO - datatable and atomics
	@Override
	public void setData(String key, int value) {
		int ikey = map.getKey(key);
		map.set(ikey, new DatatableInt(ikey, value));
	}

	@Override
	public void setData(String key, float value) {
		int ikey = map.getKey(key);
		map.set(ikey, new DatatableFloat(ikey, value));
	}

	@Override
	public void setData(String key, boolean value) {
		int ikey = map.getKey(key);
		map.set(ikey, new DatatableBool(ikey, value));
	}

	@Override
	public void setData(String key, Serializable value) {
		int ikey = map.getKey(key);
		map.set(ikey, new DatatableSerializable(ikey, value));
	}

	@Override
	public DatatableTuple getData(String key) {
		return map.get(key);
	}

	@Override
	public boolean hasData(String key) {
		return map.contains(key);
	}

	private int inventorySize;
	private Inventory inventory;

	@Override
	public int getInventorySize() {
		return inventorySize;
	}

	@Override
	public void setInventorySize(int newsize) {
		if (inventorySize == newsize) {
			return;
		}
		inventorySize = newsize;
		if (inventory != null && getInventory().getSize() != inventorySize) {
			inventory = null;
			setData("inventory", null);
		}
	}

	@Override
	public Inventory getInventory() {
		if (getInventorySize() <= 0) {
			return null;
		}
		if (inventory == null) {

			if (!hasData("inventory")) {
				inventory = controllerLive == null ? new Inventory(getInventorySize()) : controllerLive.get().createInventory(getInventorySize());
				setData("inventory", inventory);
			} else {
				inventory = (Inventory) getData("inventory").get();
			}
		}
		return inventory;
	}

	@Override
	public void onSync() {
		//Forward to controller for now, but we may want to do some sync logic here for the entity.
		controller.onSync();
		//TODO - this might not be needed, if it is, it needs to send to the network synchronizer for players
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
	public void attachComponent(EntityComponent component) {
		component.attachToEntity(this);
		component.onAttached();
		components.add(new AtomicReference<EntityComponent>(component));
	}

	@Override
	public void removeComponent(EntityComponent component) {
		if (components.remove(component)) {
			component.onDetached();
		}
	}

	@Override
	public boolean hasComponent(EntityComponent component) {
		return components.contains(component);
	}
	
	private void updateTransformAxisAngles() {
		setRotation(Quaternion.IDENTITY);
		rotate(pitch, 0,0,1);
		rotate(yaw, 0,1,0);
		rotate(roll, 1,0,0);
	}
}
