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

import org.spout.api.collision.model.CollisionModel;
import org.spout.api.datatable.DatatableTuple;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.entity.PlayerController;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.atomic.AtomicPoint;
import org.spout.api.geo.discrete.atomic.Transform;
import org.spout.api.inventory.Inventory;
import org.spout.api.io.store.MemoryStore;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.model.Model;
import org.spout.api.player.Player;
import org.spout.api.util.StringMap;
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
	private Controller controller;
	private Controller controllerLive;
	private final SpoutServer server;
	private Chunk chunk;
	private Chunk chunkLive;
	private boolean justSpawned = true;
	
	public int id = NOTSPAWNEDID;
	
	Model model;
	CollisionModel collision;
	
	SpoutDatatableMap map;
	
	public SpoutEntity(SpoutServer server, Transform transform, Controller controller) {
		this.server = server;
		this.transform.set(transform);
		setTransform(transform);
		this.controller = controller;
		this.controllerLive = controller;
		this.map = new SpoutDatatableMap();
	}

	public SpoutEntity(SpoutServer server, Point point, Controller controller) {
		this(server, new Transform(point, Quaternion.identity , Vector3.ONE), controller);
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
	public Controller getLiveController() {
		while (true) {
			int seq = lock.readLock();
			Controller controller = this.controllerLive;
			if (lock.readUnlock(seq)) {
				return controller;
			}
		}
	}
	
	@Override
	public Controller getController() {
		return controller;
	}

	
	@Override
	public void setController(Controller controller) {
		controller.attachToEntity(this);
		int seq = lock.writeLock();
		try {
			this.controllerLive = controller;
		} finally {
			lock.writeUnlock(seq);
		}
		controller.onAttached();
	}
	
	@Override
	public Transform getTransform() {
		return transform;
	}
	
	@Override
	public Transform getLiveTransform() {
		return transformLive;
	}

	@Override
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
				if (newRegion == null && this.getController() instanceof PlayerController) {
					newRegion = newPosition.getWorld().getRegion(newPosition, true);
				}
				EntityManager newEntityManager = ((SpoutRegion)newRegion).getEntityManager();

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
		this.collision = model;

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
		return (id != NOTSPAWNEDID);
	}
	
	/**
	 * Called when the tick is finished and collisions need to be resolved
	 * and move events fired
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
						Player p = ((PlayerController)controller).getPlayer();
						((SpoutPlayer)p).getNetworkSynchronizer().onDeath();
					}
				}
			}
		}
		if (entityManagerLive != null) {
			if(entityManager != entityManagerLive || controller != controllerLive) {
				entityManagerLive.allocate(this);
			}
		}
		if (chunkLive != chunk) {
			if (chunkLive != null) {
				((SpoutChunk)chunkLive).addEntity(this);
			}
			if (chunk != null) {
				((SpoutChunk)chunk).removeEntity(this);
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
	
	@Override
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

	@Override
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
		return clazz.isAssignableFrom(this.getController().getClass());
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
		if(inventorySize == newsize) return;
		inventorySize = newsize;
		if(getInventory().getSize() != inventorySize) {
			inventory = null;
			setData("inventory", null);
		}
	}


	@Override
	public Inventory getInventory() {
		if(getInventorySize() <= 0) {
			return null;
		}
		if(inventory == null) {
			SpoutDatatableObject obj = (SpoutDatatableObject)getData("inventory");
			if(obj == null) {
				inventory = new Inventory(getInventorySize());
				setData("inventory", inventory);
			} else {
				inventory = (Inventory)obj.get();
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
	
}
