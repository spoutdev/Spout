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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.spout.api.collision.CollisionModel;
import org.spout.api.datatable.DatatableTuple;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.entity.PlayerController;
import org.spout.api.entity.Position;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Pointm;
import org.spout.api.geo.discrete.atomic.AtomicPoint;
import org.spout.api.geo.discrete.atomic.Transform;
import org.spout.api.inventory.Inventory;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.math.*;
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

		map = new SpoutDatatableMap();
		viewDistanceLive.set(viewDistance);
		this.viewDistance = viewDistance;

		//Sets the cached x, y, z, yaw, pitch, roll, scale values
		scale = new Vector3m(transform.getScale());
		updatePosition();
		updateRotation();

		if (controller != null) {
			this.controller = controller;
			setController(controller);
		}
	}

	public SpoutEntity(SpoutServer server, Transform transform, Controller controller) {
		this(server, transform, controller, 64);
	}

	public SpoutEntity(SpoutServer server, Point point, Controller controller) {
		this(server, new Transform(point, Quaternion.identity, Vector3.ONE), controller);
	}

	@Override
	public int getId() {
		while (true) {
			int seq = lock.readLock();
			int lid = this.id;
			if (lock.readUnlock(seq)) {
				return lid;
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
			Controller lcontroller = controllerLive;
			if (lock.readUnlock(seq)) {
				return lcontroller;
			}
		}
	}

	public Controller getPrevController() {
		return controller;
	}

	@Override
	public void setController(Controller controller) {
		if (controller != null);
		controller.attachToEntity(this);
		int seq = lock.writeLock();
		try {
			controllerLive = controller;
		} finally {
			lock.writeUnlock(seq);
		}
		if (controller != null) {
			controller.onAttached();
		}
	}

	public Transform getTransform() {
		return transform;
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
					transform.set(DEAD);
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

				this.transform.set(transform);
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
	@Override
	public boolean kill() {
		int seq = lock.writeLock();
		boolean alive = true;
		try {
			AtomicPoint p = transform.getPosition();
			alive = p.getWorld() != null;
			transform.set(DEAD);
			chunkLive = null;
			entityManagerLive = null;
		} finally {
			lock.writeUnlock(seq);
		}
		return alive;
	}

	@Override
	public boolean isDead() {
		while (true) {
			int seq = transform.readLock();
			boolean dead = id != NOTSPAWNEDID && transform.getPosition().getWorld() == null;
			if (transform.readUnlock(seq)) {
				return dead;
			}
		}
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

	@Override
	public void finalizeRun() {
		if (entityManager != null) {
			if (entityManager != entityManagerLive || controller != controllerLive) {
				SpoutRegion r = (SpoutRegion)chunk.getRegion();
				r.removeEntity(this);
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
			if (chunk != null && chunk.isLoaded()) {
				((SpoutChunk) chunk).removeEntity(this);
			}
			if (chunkLive == null) {
				((SpoutChunk) chunk).removeEntity(this);
				entityManagerLive.deallocate(this);
			}
		}
	}

	public void copyToSnapshot() {
		chunk = chunkLive;
		if (entityManager != entityManagerLive) {
			entityManager = entityManagerLive;
		}
		controller = controllerLive;
		justSpawned = false;
		viewDistance = viewDistanceLive.get();
		updatePosition();
		updateRotation();
		updateScale();
	}

	@Override
	public Chunk getChunk() {
		World world = getWorld();
		if (world == null) {
			return null;
		} else {
			return world.getChunkFromBlock(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
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
			return world.getRegionFromBlock(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
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

	@Override
	public int getViewDistance() {
		return viewDistanceLive.get();
	}

	public int getPrevViewDistance() {
		return viewDistance;
	}
	float x, y, z, yaw, pitch, roll;
	final Vector3m scale;
	boolean posModified = false, yawModified = false, pitchModified = false,
			rollModified = false, scaleModified = false;
	//Locking is done to prevent tearing, not to provide access to live values
	final ReentrantReadWriteLock stateLock = new ReentrantReadWriteLock();

	@Override
	public float getX() {
		stateLock.readLock().lock();
		try {
			return x;
		} finally {
			stateLock.readLock().unlock();
		}
	}

	@Override
	public float getY() {
		stateLock.readLock().lock();
		try {
			return y;
		} finally {
			stateLock.readLock().unlock();
		}
	}

	@Override
	public float getZ() {
		stateLock.readLock().lock();
		try {
			return z;
		} finally {
			stateLock.readLock().unlock();
		}
	}

	@Override
	public Pointm getPoint() {
		stateLock.readLock().lock();
		try {
			return new Pointm(getWorld(), x, y, z);
		} finally {
			stateLock.readLock().unlock();
		}
	}

	@Override
	public void setPoint(Point p) {
		setPoint(p.getX(), p.getY(), p.getZ());
	}

	@Override
	public void setPoint(float x, float y, float z) {
		stateLock.writeLock().lock();
		try {
			this.x = x;
			this.y = y;
			this.z = z;
			posModified = true;
		} finally {
			stateLock.writeLock().unlock();
		}
	}

	@Override
	public void setPosition(Point p, float pitch, float yaw, float roll) {
		stateLock.writeLock().lock();
		try {
			x = p.getX();
			y = p.getY();
			z = p.getZ();
			this.pitch = pitch;
			pitchModified = true;
			this.yaw = yaw;
			yawModified = true;
			this.roll = roll;
			rollModified = true;
			posModified = true;
		} finally {
			stateLock.writeLock().unlock();
		}
	}

	@Override
	public void setPosition(Entity other) {
		setPosition(other.getPoint(), other.getPitch(), other.getYaw(), other.getRoll());
	}

	@Override
	public void setPosition(Position pos) {
		setPosition(pos.getPosition(), pos.getPitch(), pos.getYaw(), pos.getRoll());
	}

	@Override
	public Position getPosition() {
		stateLock.readLock().lock();
		try {
			return new Position(new Point(getWorld(), x, y, z), pitch, yaw, roll);
		} finally {
			stateLock.readLock().unlock();
		}
	}

	/**
	 * Called when the game finalizes the position from any movement or
	 * collision calculations, and updates the cache.
	 *
	 * If the API has modified the position, it will use the modified value
	 * instead of the calculated value.
	 */
	public void updatePosition() {
		stateLock.writeLock().lock();
		try {
			Pointm position = transform.getPosition();
			if (!posModified) {
				x = position.getX();
				y = position.getY();
				z = position.getZ();
			} else {
				posModified = false;
				position.setX(x);
				position.setY(y);
				position.setZ(z);
			}
		} finally {
			stateLock.writeLock().unlock();
		}
	}

	@Override
	public float getYaw() {
		stateLock.readLock().lock();
		try {
			return yaw;
		} finally {
			stateLock.readLock().unlock();
		}
	}

	@Override
	public void setYaw(float yaw) {
		stateLock.writeLock().lock();
		try {
			this.yaw = yaw;
			yawModified = true;
		} finally {
			stateLock.writeLock().unlock();
		}
	}

	/**
	 * Called when the game finalizes the rotation from any movement or
	 * collision calculations, and updates the cache. <br/> <br/> If the API has
	 * modified the yaw, pitch, or scale, it will use the modified value instead
	 * of the calculated value.
	 */
	public void updateRotation() {
		stateLock.writeLock().lock();
		try {
			Quaternionm rotation = transform.getRotation();
			Vector3 axisAngles = rotation.getAxisAngles();
			if (!yawModified) {
				yaw = axisAngles.getY();
			}
			if (!pitchModified) {
				pitch = axisAngles.getZ();
			}
			if (!rollModified) {
				roll = axisAngles.getX();
			}
			yawModified = pitchModified = rollModified = false;
			rotation.set(Quaternion.identity);
			rotation.rotate(roll, 1, 0, 0);
			rotation.rotate(yaw, 0, 1, 0);
			rotation.rotate(pitch, 0, 0, 1);
		} finally {
			stateLock.writeLock().unlock();
		}
	}

	@Override
	public float getPitch() {
		stateLock.readLock().lock();
		try {
			return pitch;
		} finally {
			stateLock.readLock().unlock();
		}
	}

	@Override
	public void setPitch(float pitch) {
		stateLock.writeLock().lock();
		try {
			this.pitch = pitch;
			pitchModified = true;
		} finally {
			stateLock.writeLock().unlock();
		}
	}

	@Override
	public float getRoll() {
		stateLock.readLock().lock();
		try {
			return roll;
		} finally {
			stateLock.readLock().unlock();
		}
	}

	@Override
	public void setRoll(float roll) {
		stateLock.writeLock().lock();
		try {
			this.roll = roll;
			rollModified = true;
		} finally {
			stateLock.writeLock().unlock();
		}
	}

	public void updateScale() {
		stateLock.writeLock().lock();
		try {
			Vector3m lscale = transform.getScale();
			if (!scaleModified) {
				this.scale.set(lscale);
			} else {
				scaleModified = false;
				lscale.set(this.scale);
			}
		} finally {
			stateLock.writeLock().unlock();
		}
	}

	@Override
	public Vector3 getScale() {
		stateLock.readLock().lock();
		try {
			return scale;
		} finally {
			stateLock.readLock().unlock();
		}
	}

	@Override
	public void setScale(Vector3 scale) {
		stateLock.writeLock().lock();
		try {
			this.scale.set(scale);
			scaleModified = true;
		} finally {
			stateLock.writeLock().unlock();
		}
	}
}
