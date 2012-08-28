/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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

import java.util.UUID;

import org.spout.api.Source;
import org.spout.api.collision.CollisionModel;
import org.spout.api.datatable.Datatable;
import org.spout.api.geo.World;
import org.spout.api.geo.WorldSource;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.model.Model;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.SendMode;
import org.spout.api.tickable.Tickable;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.LiveWrite;
import org.spout.api.util.thread.SnapshotRead;

/**
 * Represents an entity, which may or may not be spawned into the world.
 */
public interface Entity extends Source, Tickable, WorldSource, ComponentHolder {
	public int getId();

	// TODO - add thread timing annotations
	public void setModel(Model model);

	public Model getModel();

	public void setCollision(CollisionModel model);

	public CollisionModel getCollision();

	/**
	 * Gets the entity's persistent unique id.
	 * <p/>
	 * Can be used to look up the entity, and persists between starts.
	 * @return persistent uid
	 */
	public UUID getUID();

	/**
	 * Called when the entity is set to be sent to clients
	 */
	public void onSync();

	/**
	 * Returns true if this entity is spawned and being Simulated in the world
	 * @return spawned
	 */
	public boolean isSpawned();

	/**
	 * Gets the {@link Chunk} this entity resides in, or null if unspawned.
	 * @return chunk the entity is in, or null if unspawned.
	 */
	@SnapshotRead
	public Chunk getChunk();

	/**
	 * Gets the region the entity is associated and managed with, or null if unspawned.
	 * @return region the entity is in.
	 */
	@SnapshotRead
	public Region getRegion();

	/**
	 * Gets the world the entity is associated with, or null if unspawned.
	 * @return world
	 */
	@SnapshotRead
	@Override
	public World getWorld();

	/**
	 * Called just before a snapshot update.
	 */
	public void finalizeRun();

	/**
	 * Removes the entity. This takes effect at the next snapshot.
	 */
	@DelayedWrite
	@LiveRead
	public void remove();

	/**
	 * True if the entity is removed.
	 * @return removed
	 */
	@SnapshotRead
	public boolean isRemoved();

	/**
	 * Sets the maximum distance at which the entity can be seen.<br/>
	 * <br/>
	 * The actual view distance used by the server may not be exactly the value that is set.<br/>
	 * @param distance in blocks at which the entity can be seen
	 */
	@LiveWrite
	public void setViewDistance(int distance);

	/**
	 * Gets the maximum distance at which the entity can be seen.<br/>
	 * @return the distance in blocks at which the entity can be seen
	 */
	@LiveRead
	public int getViewDistance();

	/**
	 * Sets whether or not the entity is an observer.<br/>
	 * An observer is any entity that is allowed to keep chunks from being unloaded.</br>
	 * @param obs True if the entity should be an observer, false if not
	 */
	@DelayedWrite
	public void setObserver(boolean obs);

	/**
	 * Checks whether or not the entity is currently observing the region it is in.<br/>
	 * If this value does not match {@link #isObserverLive()} the entity will be changing its observer status on the next update.<br/>
	 * An observer is any entity that is allowed to keep chunks from being unloaded.<br/>
	 * @return true if the entity is currently an observer, false if not
	 */
	@SnapshotRead
	public boolean isObserver();

	/**
	 * Checks whether or not the entity is observing. This is used to update the status of the entity.
	 * If isObserverLive() not equal to isObserver(), then the entity will be changing its observer status on the next update.
	 * @return true if the entity will be an observer, false if not
	 */
	@LiveRead
	public boolean isObserverLive();

	/**
	 * Gets a {@link Transform} {@link Component} representing the current position, scale and
	 * rotation of the entity.
	 * @return
	 */
	public Transform getTransform();

	/**
	 * Gets a {@link Transform} {@link Component} representing the last tick's position, scale and rotation of the entity.
	 * @return transform of the entity.
	 */
	public Transform getLastTransform();
	
	/**
	 * Returns the {@link Datatable} {@link Component} attached to the entity.
	 * @return The datatable component
	 */
	public Datatable getDatatable();
	
	/**
	 * Gets the current position of the entity
	 * @return position of the entity in the world.
	 */
	public Point getPosition();

	/**
	 * Gets the current rotation of the entity
	 * @return rotation of the entity in the world.
	 */
	public Quaternion getRotation();

	/**
	 * Gets the current Scale of the entity
	 * @return scale of the entity in the world.
	 */
	public Vector3 getScale();

	/**
	 * Sets the position of the entity. <br/>
	 * This must be called in the same thread as the entity lives.<br/>
	 * @param position
	 */
	public void setPosition(Point position);

	/**
	 * Sets the rotation of the entity. <br/>
	 * This must be called in the same thread as the entity lives.<br/>
	 * @param rotation
	 */
	public void setRotation(Quaternion rotation);

	/**
	 * Sets the scale of the entity. <br/>
	 * This must be called in the same thread asthe entity lives.<br/>
	 * @param scale
	 */
	public void setScale(Vector3 scale);

	/**
	 * Moves the entity by the provided vector<br/>
	 * @param amount to move the entity
	 */
	public void translate(Vector3 amount);

	/**
	 * Moves the entity by the provided vector
	 * @param x offset
	 * @param y offset
	 * @param z offset
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

	/**
	 * Gets the entities current pitch, or vertical angle.
	 * @return pitch of the entity
	 */
	public float getPitch();

	/**
	 * Gets the entities current yaw, or horizontal angle.
	 * @return yaw of the entity.
	 */
	public float getYaw();

	/**
	 * Gets the entities current roll as a float.
	 * @return roll of the entity
	 */
	public float getRoll();

	/**
	 * Sets the pitch of the entity.
	 * @param ang
	 */
	public void setPitch(float ang);

	/**
	 * Sets the roll of the entity.
	 * @param ang
	 */
	public void setRoll(float ang);

	/**
	 * sets the yaw of the entity.
	 * @param ang
	 */
	public void setYaw(float ang);

	/**
	 * Sends all the Messages to all observing players of this Entity that have the specified protocol
	 * 
	 * @param sendMode to use
	 * @param protocol the messages are meant for
	 * @param messages to send
	 */
	public void sendMessage(SendMode sendMode, Protocol protocol, Message... messages);
}
