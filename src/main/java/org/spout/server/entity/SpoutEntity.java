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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.server.entity;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spout.api.collision.model.CollisionModel;
import org.spout.api.datatable.DatatableTuple;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.entity.PlayerController;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Pointm;
import org.spout.api.geo.discrete.atomic.AtomicPoint;
import org.spout.api.geo.discrete.atomic.Transform;
import org.spout.api.inventory.Inventory;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector3m;
import org.spout.api.model.Model;
import org.spout.api.player.Player;
import org.spout.api.util.StringMap;
import org.spout.api.util.concurrent.AtomicFloat;
import org.spout.api.util.concurrent.OptimisticReadWriteLock;
import org.spout.server.SpoutChunk;
import org.spout.server.SpoutRegion;
import org.spout.server.SpoutServer;
import org.spout.server.datatable.SpoutDatatableMap;
import org.spout.server.datatable.value.SpoutDatatableBool;
import org.spout.server.datatable.value.SpoutDatatableFloat;
import org.spout.server.datatable.value.SpoutDatatableInt;
import org.spout.server.datatable.value.SpoutDatatableObject;
import org.spout.server.player.SpoutPlayer;

public class SpoutEntity implements Entity {
	private static final long serialVersionUID = 1L;

	public final static int NOTSPAWNEDID = -1;
	private final static Transform DEAD = new Transform(new Point(null, 0, 0, 0), new Quaternion(0F, 0F, 0F, 0F), new Vector3(0, 0, 0));
	// TODO - needs to have a world based version too?
	public static final StringMap entityStringMap = new StringMap(null, new MemoryStore<Integer>(), 0, Short.MAX_VALUE);

	private final OptimisticReadWriteLock lock = new OptimisticReadWriteLock();
	private final Transform transform = new Transform();
	private final Transform transformLive = new Transform();
	private EntityManager entityManager;
	private EntityManager entityManagerLive;
	private Controller controller = null;
	private Controller controllerLive = null;
	private Chunk chunk;
	private Chunk chunkLive;
	private boolean justSpawned = true;
	private final AtomicInteger viewDistanceLive = new AtomicInteger();
	private int viewDistance;

	public int id = NOTSPAWNEDID;

	Model model;
	CollisionModel collision;

	SpoutDatatableMap map;

	public SpoutEntity(SpoutServer server, Transform transform, Controller controller, int viewDistance) {
		this.transform.set(transform);
		setTransform(transform);
		if (controller != null) {
			this.controller = controller;
			setController(controller);
		}
		map = new SpoutDatatableMap();
		viewDistanceLive.set(viewDistance);
		this.viewDistance = viewDistance;
	}

	public SpoutEntity(SpoutServer server, Transform transform, Controller controller) {
		this(server, transform, controller, 64);
	}

	public SpoutEntity(SpoutServer server, Point point, Controller controller) {
		this(server, new Transform(point, Quaternion.identity, Vector3.ONE), controller);
	}

	public int getId() {
		while (true) {
			int seq = lock.readLock();
			int id = this.id;
			if (lock.readUnlock(seq)) {
				return id;
			}
		}
	}

	public void setId(int id) {
		int seq = lock.writeLock();
		try {
			this.id = id;
		} finally {
			lock.writeUnlock(seq);
		}
	}

	@Override
	public Controller getController() {
		while (true) {
			int seq = lock.readLock();
			Controller controller = controllerLive;
			if (lock.readUnlock(seq)) {
				return controller;
			}
		}
	}

	public Controller getPrevController() {
		return controller;
	}

	@Override
	public void setController(Controller controller) {
		controller.attachToEntity(this);
		int seq = lock.writeLock();
		try {
			controllerLive = controller;
		} finally {
			lock.writeUnlock(seq);
		}
		controller.onAttached();
	}

	public Transform getTransform() {
		return transform;
	}

	public Transform getLiveTransform() {
		return transformLive;
	}

	public void setTransform(Transform transform) {

		int seq = lock.writeLock();
		try {
			while (true) {
				int seqRead = transform.readLock();
				Point newPosition = transform.getPosition();

				World world = newPosition.getWorld();
				if (world == null) {
					chunkLive = null;
					transformLive.set(DEAD);
					entityManagerLive = null;
					return;
				}
				chunkLive = newPosition.getWorld().getChunk(newPosition);
				Region newRegion = chunkLive.getRegion();

				// TODO - entity moved into unloaded chunk - what happens for normal entities?
				if (newRegion == null && getController() instanceof PlayerController) {
					newRegion = newPosition.getWorld().getRegion(newPosition, true);
				}
				EntityManager newEntityManager = ((SpoutRegion) newRegion).getEntityManager();

				transformLive.set(transform);
				entityManagerLive = newEntityManager;
				if (transform.readUnlock(seqRead)) {
					return;
				}
			}

		} finally {
			lock.writeUnlock(seq);
		}

	}

	// TODO - make actually atomic, rather than just threadsafe
	public boolean kill() {
		int seq = lock.writeLock();
		boolean alive = true;
		try {
			AtomicPoint p = transformLive.getPosition();
			alive = p.getWorld() != null;
			transformLive.set(DEAD);
		} finally {
			lock.writeUnlock(seq);
		}
		setTransform(DEAD);
		return alive;
	}

	@Override
	public boolean isDeadLive() {
		while (true) {
			int seq = transformLive.readLock();
			boolean dead = id != NOTSPAWNEDID && transformLive.getPosition().getWorld() == null;
			if (transformLive.readUnlock(seq)) {
				return dead;
			}
		}
	}

	@Override
	public boolean isDead() {
		boolean dead = id != NOTSPAWNEDID && transformLive.getPosition().getWorld() == null;
		return dead;
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

	}

	// TODO - needs to be made thread safe
	@Override
	public CollisionModel getCollision() {
		return collision;
	}

	/**
	 * @param dt milliseconds since the last tick
	 */
	public void onTick(float dt) {
		if (controller != null) {
			controller.onTick(dt);
		}
	}

	@Override
	public boolean isSpawned() {
		return id != NOTSPAWNEDID;
	}

	/**
	 * Called when the tick is finished and collisions need to be resolved and
	 * move events fired
	 */
	public void resolve() {
		//Resolve Collisions Here

		//Check to see if we should fire off a Move event
	}

	public void finalizeRun() {
		if (entityManager != null) {
			if (entityManager != entityManagerLive || controller != controllerLive) {
				entityManager.deallocate(this);
				if (entityManagerLive == null) {
					controller.onDeath();
					if (controller instanceof PlayerController) {
						Player p = ((PlayerController) controller).getPlayer();
						((SpoutPlayer) p).getNetworkSynchronizer().onDeath();
					}
				}
			}
		}
		if (entityManagerLive != null) {
			if (entityManager != entityManagerLive || controller != controllerLive) {
				entityManagerLive.allocate(this);
			}
		}
		if (chunkLive != chunk) {
			if (chunkLive != null) {
				((SpoutChunk) chunkLive).addEntity(this);
			}
			if (chunk != null) {
				((SpoutChunk) chunk).removeEntity(this);
			}
		}
	}

	public void copyToSnapshot() {
		transform.set(transformLive);
		chunk = chunkLive;
		if (entityManager != entityManagerLive) {
			entityManager = entityManagerLive;
		}
		controller = controllerLive;
		justSpawned = false;
		viewDistance = viewDistanceLive.get();
	}

	@Override
	public Chunk getChunk() {
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

	public Chunk getChunkLive() {
		while (true) {
			int seq = lock.readLock();
			Point position = transformLive.getPosition();
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
		Point position = transform.getPosition();
		World world = position.getWorld();
		if (world == null) {
			return null;
		} else {
			return world.getRegion(position, true);
		}
	}

	public Region getRegionLive() {
		while (true) {
			int seq = lock.readLock();
			Point position = transformLive.getPosition();
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
		return clazz.isAssignableFrom(getController().getClass());
	}

	// TODO - datatable and atomics
	@Override
	public void setData(String key, int value) {
		int ikey = map.getKey(key);
		map.set(ikey, new SpoutDatatableInt(ikey, value));
	}

	@Override
	public void setData(String key, float value) {
		int ikey = map.getKey(key);
		map.set(ikey, new SpoutDatatableFloat(ikey, value));
	}

	@Override
	public void setData(String key, boolean value) {
		int ikey = map.getKey(key);
		map.set(ikey, new SpoutDatatableBool(ikey, value));

	}

	@Override
	public void setData(String key, Serializable value) {
		int ikey = map.getKey(key);
		map.set(ikey, new SpoutDatatableObject(ikey, value));
	}

	@Override
	public DatatableTuple getData(String key) {
		return map.get(key);
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
		if (getInventory().getSize() != inventorySize) {
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
			SpoutDatatableObject obj = (SpoutDatatableObject) getData("inventory");
			if (obj == null) {
				inventory = new Inventory(getInventorySize());
				setData("inventory", inventory);
			} else {
				inventory = (Inventory) obj.get();
			}
		}
		return inventory;
	}

	@Override
	public void onSync() {
		//Forward to controller for now, but we may want to do some sync logic here for the entitiy.
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

	public int getViewDistance() {
		return viewDistanceLive.get();
	}

	public int getPrevViewDistance() {
		return viewDistance;
	}
	
	float x, y, z, yaw, pitch, velX, velY, velZ;
	boolean posModified = false, velModified = false, yawModified = false, pitchModified = false;
	//Locking is done to prevent tearing, not to provide access to live values
	ReentrantReadWriteLock posLock = new ReentrantReadWriteLock(), velLock = new ReentrantReadWriteLock();
	ReentrantReadWriteLock pitchLock = new ReentrantReadWriteLock(), yawLock = new ReentrantReadWriteLock();

	@Override
	public float getX() {
		posLock.readLock().lock();
		try {
			return x;
		}
		finally {
			posLock.readLock().unlock();
		}
	}

	@Override
	public float getY() {
		posLock.readLock().lock();
		try {
			return y;
		}
		finally {
			posLock.readLock().unlock();
		}
	}

	@Override
	public float getZ() {
		posLock.readLock().lock();
		try {
			return z;
		}
		finally {
			posLock.readLock().unlock();
		}
	}

	@Override
	public Pointm getPosition() {
		posLock.readLock().lock();
		try {
			return new Pointm(getWorld(), x, y, z);
		}
		finally {
			posLock.readLock().unlock();
		}
	}
	
	@Override
	public void setPosition(Point p) {
		posLock.writeLock().lock();
		try {
			x = p.getX();
			y = p.getY();
			z = p.getZ();
			posModified = true;
		}
		finally {
			posLock.writeLock().unlock();
		}
	}
	
	/**
	 * Called when the game finalizes the position from any movement or collision calculations, and updates the cache.
	 * 
	 * If the API has modified the position, it will use the modified value instead of the calculated value.
	 * @param newPosition the calculated new position value
	 * @return the new value, or the modified value if one was set
	 */
	public Point updatePosition(Point newPosition) {
		posLock.writeLock().lock();
		try {
			if (!posModified) {
				x = newPosition.getX();
				y = newPosition.getY();
				z = newPosition.getZ();
				return newPosition;
			}
			else {
				posModified = false;
				return new Point(newPosition.getWorld(), x, y, z);
			}
		}
		finally {
			posLock.writeLock().unlock();
		}
	}

	@Override
	public Vector3m getVelocity() {
		velLock.readLock().lock();
		try {
			return new Vector3m(velX, velY, velZ);
		}
		finally {
			velLock.readLock().unlock();
		}
	}

	@Override
	public void setVelocity(Vector3 v) {
		velLock.writeLock().lock();
		try {
			velX = v.getX();
			velY = v.getY();
			velZ = v.getZ();
			velModified = true;
		}
		finally {
			velLock.writeLock().unlock();
		}
	}
	
	/**
	 * Called when the game finalizes the velocity from any movement or collision calculations, and updates the cache.
	 * 
	 * If the API has modified the velocity, it will use the modified value instead of the calculated value.
	 * @param newVelocity the calculated new velocity value
	 * @return the new value, or the modified value if one was set
	 */
	public Vector3 updateVelocity(Vector3 newVelocity) {
		velLock.writeLock().lock();
		try {
			if (!velModified) {
				x = newVelocity.getX();
				y = newVelocity.getY();
				z = newVelocity.getZ();
				return newVelocity;
			}
			else {
				velModified = false;
				return new Vector3(x, y, z);
			}
		}
		finally {
			velLock.writeLock().unlock();
		}
	}

	@Override
	public float getYaw() {
		yawLock.readLock().lock();
		try {
			return yaw;
		}
		finally {
			yawLock.readLock().unlock();
		}
	}
	
	@Override
	public void setYaw(float yaw) {
		yawLock.writeLock().lock();
		try {
			this.yaw = yaw;
			yawModified = true;
		}
		finally {
			yawLock.writeLock().unlock();
		}
	}
	
	/**
	 * Called when the game finalizes the yaw from any movement or collision calculations, and updates the cache.
	 * 
	 * If the API has modified the yaw, it will use the modified value instead of the calculated value.
	 * @param newYaw the calculated new yaw value
	 * @return the new value, or the modified value if one was set
	 */
	public float updateYaw(float newYaw) {
		yawLock.writeLock().lock();
		try {
			if (!yawModified) {
				yaw = newYaw;
				return newYaw;
			}
			else {
				yawModified = false;
				return yaw;
			}
		}
		finally {
			yawLock.writeLock().unlock();
		}
	}

	@Override
	public float getPitch() {
		pitchLock.readLock().lock();
		try {
			return pitch;
		}
		finally {
			pitchLock.readLock().unlock();
		}
	}

	@Override
	public void setPitch(float pitch) {
		pitchLock.writeLock().lock();
		try {
			this.pitch = pitch;
			pitchModified = true;
		}
		finally {
			pitchLock.writeLock().unlock();
		}
	}
	
	/**
	 * Called when the game finalizes the pitch from any movement or collision calculations, and updates the cache.
	 * 
	 * If the API has modified the pitch, it will use the modified value instead of the calculated value.
	 * @param newPitch the calculated new pitch value
	 * @return the new value, or the modified value if one was set
	 */
	public float updatePitch(float newPitch) {
		pitchLock.writeLock().lock();
		try {
			if (!pitchModified) {
				yaw = newPitch;
				return newPitch;
			}
			else {
				pitchModified = false;
				return yaw;
			}
		}
		finally {
			pitchLock.writeLock().unlock();
		}
	}
}
