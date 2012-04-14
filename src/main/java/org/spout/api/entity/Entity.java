/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.entity;

import org.spout.api.Source;
import org.spout.api.collision.CollisionModel;
import org.spout.api.datatable.Datatable;
import org.spout.api.entity.component.EntityComponent;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.inventory.Inventory;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.model.Model;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;

/**
 * Represents an entity, which may or may not be spawned into the world.
 */
public interface Entity extends Datatable, Source {

	public int getId();

	/**
	 * Gets the controller for the entity
	 *
	 * @return the controller
	 */
	@SnapshotRead
	public Controller getController();

	/**
	 * Sets the controller for the entity
	 *
	 * @param controller
	 */
	@DelayedWrite
	public void setController(Controller controller, Source source);

	/**
	 * Sets the controller for the entity
	 *
	 * @param controller
	 */
	@DelayedWrite
	public void setController(Controller controller);

	// TODO - add thread timing annotations
	public void setModel(Model model);

	public Model getModel();

	public void setCollision(CollisionModel model);

	public CollisionModel getCollision();

	
	/**
	 * Called when the entity is set to be sent to clients
	 */
	public void onSync();

	/**
	 * Returns true if this entity's controller is the provided controller
	 *
	 * @param clazz
	 * @return true if this entity's controller is the provided controller
	 */
	public boolean is(Class<? extends Controller> clazz);

	/**
	 * Returns true if this entity is spawned and being Simulated in the world
	 *
	 * @return spawned
	 */
	public boolean isSpawned();

	/**
	 * Gets the chunk the entity resides in, or null if unspawned.
	 *
	 * @return chunk
	 */
	@SnapshotRead
	public Chunk getChunk();

	/**
	 * Gets the region the entity is associated and managed with, or null if
	 * unspawned.
	 *
	 * @return region
	 */
	@SnapshotRead
	public Region getRegion();

	/**
	 * Gets the world the entity is associated with, or null if unspawned.
	 *
	 * @return world
	 */
	@SnapshotRead
	public World getWorld();

	/**
	 * Called just before a snapshot update.
	 */
	public void finalizeRun();

	/**
	 * Kills the entity. This takes effect at the next snapshot.
	 *
	 * If the entity's position is set before the next snapshot, the entity
	 * won't be removed.
	 *
	 * @return true if the entity was alive
	 */
	@DelayedWrite
	@LiveRead
	public boolean kill();

	/**
	 * True if the entity is dead.
	 *
	 * @return dead
	 */
	@SnapshotRead
	public boolean isDead();

	/**
	 * Returns the inventory of the entity
	 *
	 * @return inventory
	 */
	@SnapshotRead
	public Inventory getInventory();

	/**
	 * Returns the size of an entity's inventory
	 *
	 * @return inventorysize
	 */
	@SnapshotRead
	public int getInventorySize();

	/**
	 * Sets the size of an entity's inventory
	 *
	 * @return
	 */
	@DelayedWrite
	public void setInventorySize(int size);

	/**
	 * Sets the maximum distance at which the entity can be seen.<br>
	 * <br>
	 * The actual view distance used by the server may not be exactly the value
	 * that is set.
	 *
	 * @param distance the distance in blocks at which the entity can be seen
	 */
	@DelayedWrite
	public void setViewDistance(int distance);

	/**
	 * Gets the maximum distance at which the entity can be seen.<br>
	 *
	 * @return the distance in blocks at which the entity can be seen
	 */
	@SnapshotRead
	public int getViewDistance();

	
	/**
	 * Sets whether or not the entity is an observer
	 * 
	 * An entity that is an observer is an entity that keeps chunks loaded in memory
	 * 
	 * @param obs True if the entity should be an observer, false if not
	 */
	@DelayedWrite
	public void setObserver(boolean obs);
	
	/**
	 * Tells whether or not the entity is an Observer.
	 * 
	 * an entity that is an observer will keep chunks loaded in memory.
	 * 
	 * @return true if the entity is an observer, false if not
	 */
	@SnapshotRead
	public boolean isObserver();
	
	/**
	 * Tells whether or not the entity is an Observer.
	 * 
	 * an entity that is an observer will keep chunks loaded in memory.
	 * 
	 * @return true if the entity is an observer, false if not
	 */
	@LiveRead
	public boolean isObserverLive();


	/**
	 * Gets the current position of the entity
	 * @return
	 */
	public Point getPosition();
	/**
	 * Gets the current rotation of the entity
	 * @return
	 */
	public Quaternion getRotation();
	/**
	 * Gets the current Scale of the entity
	 * @return
	 */
	public Vector3 getScale();


	/**
	 * Sets the position of the entity.
	 * This must be called in the same thread as the entity lives.
	 * @param position
	 */
	public void setPosition(Point position);
	/**
	 * Sets the rotation of the entity.
	 * This must be called in the same thread as the entity lives.
	 * @param rotation
	 */
	public void setRotation(Quaternion rotation);
	/**
	 * Sets the scale of the entity.
	 * This must be called in the same thread as the entity lives.
	 * @param scale
	 */
	public void setScale(Vector3 scale);


	/**
	 * Moves the entity by the provided vector
	 * @param amount
	 */
	public void translate(Vector3 amount);

	/**
	 * Moves the entity by the provided vector
	 * @param x
	 * @param y
	 * @param z
	 */
	public void translate(float x, float y, float z);

	/**
	 * Rotates the entity about the provided axis by the provided angle
	 * @param ang
	 * @param x
	 * @param y
	 * @param z
	 */
	public void rotate(float ang, float x, float y, float z);

	/**
	 * Rotates the entity by the provided rotation
	 * @param rot
	 */
	public void rotate(Quaternion rot);

	/**
	 * Scales the entity by the provided amount
	 * @param amount
	 */
	public void scale(Vector3 amount);

	/**
	 * Scales the entity by the provided amount
	 * @param x
	 * @param y
	 * @param z
	 */
	public void scale(float x, float y, float z);

	/**
	 * Rolls the entity by the provided amount
	 * @param ang
	 */
	public void roll(float ang);
	/**
	 * pitches the entity by the provided amount
	 * @param ang
	 */
	public void pitch(float ang);
	/**
	 * yaws the entity by the provided amount
	 * @param ang
	 */
	public void yaw(float ang);

	public float getPitch();

	public float getYaw();

	public float getRoll();

	public void setPitch(float ang);

	public void setRoll(float ang);

	public void setYaw(float ang);

	/**
	 * Gets the health of the entity.
	 * @return the health of the entity.
	 */
	public int getHealth();

	/**
	 * Sets the health of the entity.
	 */
	public void setHealth(int health, Source source);

	/**
	 * Sets the max health of the entity
	 */
	public void setMaxHealth(int maxHealth);

	/**
	 * Gets the max health of the entity
	 */
	public int getMaxHealth();
	
	
	/**
	 * Attaches a component to this entity.  If it's already attached, it will fail silently
	 * @param component
	 */
	public void attachComponent(EntityComponent component);
	
	/**
	 * removes a component from an entity.  Fails silently if component doesnt exist
	 * @param component
	 */
	public void removeComponent(EntityComponent component);
	/**
	 * True if component is attached.  False if not
	 * @param component
	 */
	public boolean hasComponent(EntityComponent component);
	
}
