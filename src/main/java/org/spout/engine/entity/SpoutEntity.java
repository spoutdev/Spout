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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.Tickable;
import org.spout.api.collision.CollisionModel;
import org.spout.api.collision.CollisionStrategy;
import org.spout.api.collision.CollisionVolume;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.entity.PlayerController;
import org.spout.api.entity.component.EntityComponent;
import org.spout.api.event.entity.EntityControllerChangeEvent;
import org.spout.api.event.entity.EntityHealthChangeEvent;
import org.spout.api.geo.World;
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
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.SpoutEngine;
import org.spout.engine.protocol.SpoutSession;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;

public class SpoutEntity implements Entity, Tickable {
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
	private final Transform transform = new Transform();
	private boolean justSpawned = true;
	private boolean observer = false;
	private int viewDistance;
	private Chunk chunk;
	private CollisionModel collision;
	private Controller controller;
	private EntityManager entityManager;
	private Model model;
	private Thread owningThread;
	private Transform lastTransform = transform;
	private Point collisionPoint;
	private Set<Chunk> observedChunks = new HashSet<Chunk>(); //TODO: Different collection? Snapshotable?
	private Set<Chunk> oldObservedChunks = new HashSet<Chunk>(); //temporarily used to update chunks

	public SpoutEntity(SpoutEngine engine, Transform transform, Controller controller, int viewDistance, boolean load) {
		id.set(NOTSPAWNEDID);
		this.transform.set(transform);

		chunkLive = new AtomicReference<Chunk>();
		entityManagerLive = new AtomicReference<EntityManager>();
		controllerLive = new AtomicReference<Controller>();

		if (transform != null && load) {
			chunkLive.set(transform.getPosition().getWorld().getChunkFromBlock(transform.getPosition()));
			entityManagerLive.set(((SpoutRegion) chunkLive.get().getRegion()).getEntityManager());
		}

		viewDistanceLive.set(viewDistance);

		controllerLive.set(controller);
		if(controller != null)  {
			if (controller instanceof PlayerController) {
				setObserver(true);
			}
			controller.attachToEntity(this);
			controller.onAttached();
		}
	}
	
	public SpoutEntity(SpoutEngine engine, Transform transform, Controller controller, int viewDistance) {
		this(engine, transform, controller, viewDistance, true);
	}

	public SpoutEntity(SpoutEngine engine, Transform transform, Controller controller) {
		this(engine, transform, controller, SpoutConfiguration.VIEW_DISTANCE.getInt() * SpoutChunk.CHUNK_SIZE);
	}

	public SpoutEntity(SpoutEngine engine, Point point, Controller controller) {
		this(engine, new Transform(point, Quaternion.IDENTITY, Vector3.ONE), controller);
	}

	@Override
	public void onTick(float dt) {
		//Pulse all player messages here, so they can interact with the entities position safely
		if (controllerLive.get() instanceof PlayerController) {
			Player player = ((PlayerController) controllerLive.get()).getPlayer();
			if (player != null && player.getSession() != null) {
				((SpoutSession) player.getSession()).pulse();
			}
		}

		//Tick the controller
		if (controllerLive.get() != null && controllerLive.get().getParent() != null) {
			controllerLive.get().onTick(dt);
		}

		//Copy values last (position may change during controller or session pulses)
		if (!isDead() && this.transform.getPosition() != null && this.transform.getPosition().getWorld() != null) {
			//Note: if the chunk is null, this effectively kills the entity (since dead: {chunkLive.get() == null})
			chunkLive.set(transform.getPosition().getWorld().getChunkFromBlock(transform.getPosition(), false));
			
			entityManagerLive.set(((SpoutRegion)getRegion()).getEntityManager());
			lastTransform = transform.copy();
		}
	}

	/**
	 * Called right before resolving collisions. This is necessary to make sure all entities'
	 * get their collisions set.
	 * @return
	 */
	public boolean preResolve() {
		//Don't need to do collisions if we have no collision volume
		if (this.collision == null || this.getWorld() == null || controllerLive.get() == null) {
			return false;
		}

		//Set collision point at the current position of the entity.
		collisionPoint = this.transform.getPosition();

		//Move the collision volume to the new position
		this.collision.setPosition(collisionPoint);

		//This will let SpoutRegion know it should call resolve for this entity.
		return true;
	}

	/**
	 * Called when the tick is finished and collisions need to be resolved and
	 * move events fired
	 */
	public void resolve() {
		if (Spout.debugMode()) {
		//	System.out.println("COLLISION DEBUGGING");
		//	System.out.println("Current Collision: " + this.collision.toString());
		}

		List<CollisionVolume> colliding = ((SpoutWorld) collisionPoint.getWorld()).getCollidingObject(this.collision);

		Vector3 offset = this.lastTransform.getPosition().subtract(collisionPoint);
		for (CollisionVolume box : colliding) {
			if (Spout.debugMode()) {
			//	System.out.println("Colliding box: " + box.toString());
			}
			Vector3 collision = this.collision.resolve(box);
			if (Spout.debugMode()) {
			//	System.out.println("Collision vector: " + collision.toString());
			}
			if (collision != null) {
				collision = collision.subtract(collisionPoint);
				if (Spout.debugMode()) {
				//	System.out.println("Collision point: " + collision.toString() + " Collision vector: " + collision);
				}

				if (collision.getX() != 0F) {
					offset = new Vector3(collision.getX(), offset.getY(), offset.getZ());
				}
				if (collision.getY() != 0F) {
					offset = new Vector3(offset.getX(), collision.getY(), offset.getZ());
				}
				if (collision.getZ() != 0F) {
					offset = new Vector3(offset.getX(), offset.getY(), collision.getZ());
				}

				if (Spout.debugMode()) {
				//	System.out.println("Collision offset: " + offset.toString());
				}
				if (this.getCollision().getStrategy() == CollisionStrategy.SOLID && box.getStrategy() == CollisionStrategy.SOLID) {
					this.setPosition(collisionPoint.add(offset));
					if (Spout.debugMode()) {
					//	System.out.println("New Position: " + this.getPosition());
					}
				}

				controllerLive.get().onCollide(getWorld().getBlock(box.getPosition()));
			}
		}

		//Check to see if we should fire off a Move event
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
		if (activeThreadIsValid("set transform")) {
			this.transform.set(transform);
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
		if (activeThreadIsValid("set position")) {
			transform.setPosition(position);
		}
	}

	@Override
	public void setRotation(Quaternion rotation) {
		if (activeThreadIsValid("set rotation")) {
			transform.setRotation(rotation);
		}
	}

	@Override
	public void setScale(Vector3 scale) {
		if (activeThreadIsValid("set scale")) {
			transform.setScale(scale);
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
		setAxisAngles(pitch, getYaw(), getRoll(), "set pitch");
	}

	@Override
	public void setRoll(float roll) {
		setAxisAngles(getPitch(), getYaw(), roll, "set roll");
	}

	@Override
	public void setYaw(float yaw) {
		setAxisAngles(getPitch(), yaw, getRoll(), "set yaw");
	}

	private void setAxisAngles(float pitch, float yaw, float roll, String errorMessage) {
		if (activeThreadIsValid(errorMessage)) {
			setRotation(Quaternion.rotation(pitch, yaw, roll));
		}
	}

	private boolean activeThreadIsValid(String attemptedAction) {
		Thread current = Thread.currentThread();
		boolean invalidAccess = !(this.owningThread == current || Spout.getEngine().getMainThread() == current);

		if (invalidAccess && Spout.getEngine().debugMode()) {
			if (attemptedAction == null) {
				attemptedAction = "Unknown Action";
			}

			throw new IllegalAccessError("Tried to " + attemptedAction + " from another thread {current: " + Thread.currentThread().getPriority() + " owner: " + owningThread.getName() + "}!");
		}
		return !invalidAccess;
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
		//    2.) Entity is swapping controllers (ControllerLive != Controller, niether is null)
		//    3.) Entity has just spawned and has never executed copy snapshot, Controller == null, ControllerLive != null
		if (controller != controllerLive.get()) {
			//1.) Entity is dead
			if (controller != null && controllerLive.get() == null) {
				//Sanity check
				if (!isDead()) throw new IllegalStateException("ControllerLive is null, but entity is not dead!");
				
				//Kill old controller
				controller.onDeath();
				if (controller instanceof PlayerController) {
					Player p = ((PlayerController) controller).getPlayer();
					if (p != null) {
						p.getNetworkSynchronizer().onDeath();
					}
				}
			}
			//2.) Entity is changing controllers
			else if (controller != null && controllerLive.get() != null) {
				//Kill old controller
				controller.onDeath();
				if (controller instanceof PlayerController) {
					Player p = ((PlayerController) controller).getPlayer();
					if (p != null) {
						p.getNetworkSynchronizer().onDeath();
					}
				}
				
				//Allocate new controller
				if (entityManagerLive.get() != null) {
					entityManagerLive.get().allocate(this);
				}
			}
			//3.) Entity was just spawned, has not copied snapshots yet
			else if (controller == null && controllerLive.get() != null) {
				//Sanity check
				if (!this.justSpawned()) throw new IllegalStateException("Controller is null, ControllerLive is not-null, and the entity did not just spawn.");
			}
		}

		if (chunkLive.get() != chunk) {
			//add entity to new chunk
			if (chunkLive.get() != null) {
				((SpoutChunk) chunkLive.get()).addEntity(this);
			}
			//remove entity from old chunk
			if (chunk != null && chunk.isLoaded()) {
				((SpoutChunk) chunk).removeEntity(this);
			}

			//if this entity has no chunk - remove
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
			} else if (this.observer) {
				this.oldObservedChunks.clear();
				this.oldObservedChunks.addAll(this.observedChunks);
				this.updateObservedChunks(this.getPosition());
				//remove observers
				for (Chunk chunk : this.oldObservedChunks) {
					if (!chunk.isLoaded()) continue;
					if (!this.observedChunks.contains(chunk)) {
						chunk.removeObserver(this);
					}
				}
				//add observers
				for (Chunk chunk : this.observedChunks) {
					if (!this.oldObservedChunks.contains(chunk)) {
						chunk.refreshObserver(this);
					}
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
		chunk = chunkLive.get();
		entityManager = entityManagerLive.get();
		controller = controllerLive.get();
		viewDistance = viewDistanceLive.get();
		justSpawned = false;
	}

	//TODO: Bring to SpoutAPI
	/**
	 * Gets the chunk view distance this entity has as an observer
	 * @return The chunk view distance
	 */
	public int getChunkViewDistance() {
		return 10;
	}

	//TODO: Bring to SpoutAPI
	/**
	 * Updates the nearby chunks this entity observes
	 * @param currentPosition of this entity
	 */
	public void updateObservedChunks(Point currentPosition) {
		World world = currentPosition.getWorld();
		int bx = (int) currentPosition.getX();
		int by = (int) currentPosition.getY();
		int bz = (int) currentPosition.getZ();

		observedChunks.clear();

		Point playerChunkBase = Chunk.pointToBase(currentPosition);

		int cx = bx >> Chunk.CHUNK_SIZE_BITS;
		int cy = by >> Chunk.CHUNK_SIZE_BITS;
		int cz = bz >> Chunk.CHUNK_SIZE_BITS;

		int viewDistance = this.getChunkViewDistance();
		int blockViewDistance = viewDistance * Chunk.CHUNK_SIZE;

		for (int x = cx - viewDistance; x < cx + viewDistance; x++) {
			for (int y = cy - viewDistance; y < cy + viewDistance; y++) {
				for (int z = cz - viewDistance; z < cz + viewDistance; z++) {
					Point base = new Point(this.getWorld(), x << Chunk.CHUNK_SIZE_BITS, y << Chunk.CHUNK_SIZE_BITS, z << Chunk.CHUNK_SIZE_BITS);
					double distance = base.getManhattanDistance(playerChunkBase);
					if (distance <= blockViewDistance) {
						this.observedChunks.add(world.getChunkFromBlock(base));
					}
				}
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
		//Check here to avoid the lookup
		if (entityManager != null && entityManager.getRegion() != null) {
			return entityManager.getRegion();
		}
		//Lookup
		World world = getWorld();
		if (world == null) {
			return null;
		} else {
			return world.getRegionFromBlock(MathHelper.floor(getPosition().getX()), MathHelper.floor(getPosition().getY()), MathHelper.floor(getPosition().getZ()));
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

	private int inventorySize;
	private Inventory inventory;

	@Override
	public int getInventorySize() {
		return inventorySize;
	}

	@Override
	public void setInventorySize(int newsize) {
		if (inventorySize == newsize || controllerLive.get() == null) {
			return;
		}
		inventorySize = newsize;
		if (inventory != null && getInventory().getSize() != inventorySize) {
			inventory = null;
			controllerLive.get().data().put("inventory", null);
		}
	}

	@Override
	public Inventory getInventory() {
		if (getInventorySize() <= 0 || controllerLive.get() == null) {
			return null;
		}
		if (inventory == null) {

			if (!controllerLive.get().data().containsKey("inventory")) {
				inventory = controllerLive == null ? new Inventory(getInventorySize()) : controllerLive.get().createInventory(getInventorySize());
				controllerLive.get().data().put("inventory", inventory);
			} else {
				inventory = (Inventory) controllerLive.get().data().get("inventory");
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
}
