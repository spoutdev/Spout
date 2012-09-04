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
import org.spout.api.geo.WorldSource;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.tickable.Tickable;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.LiveWrite;
import org.spout.api.util.thread.SnapshotRead;

/**
 * Represents an entity, which may or may not be spawned into the world.
 */
public interface Entity extends Source, Tickable, WorldSource, EntityComponentHolder {
	/**
	 * Gets the current ID of this entity within the current game session
	 * @return The entities' id.
	 */
	public int getId();

	/**
	 * Gets the entity's persistent unique id.
	 * <p/>
	 * Can be used to look up the entity, and persists between starts.
	 * @return persistent uuid
	 */
	public UUID getUID();

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
	 * Returns true if this entity is spawned.
	 * @return spawned
	 */
	public boolean isSpawned();

	/**
	 * Sets whether or not the entity should be saved.<br/>
	 * @param savable True if the entity should be saved, false if not
	 */
	@DelayedWrite
	public void setSavable(boolean savable);
	
	/**
	 * Returns true if this entity should be saved.
	 * @return savable
	 */
	@SnapshotRead
	public boolean isSavable();

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
	 * An observer is any entity that is allowed to keep chunks from being unloaded.<br/>
	 * @return true if the entity is currently an observer, false if not
	 */
	@SnapshotRead
	public boolean isObserver();

	/**
	 * Gets the {@link Chunk} this entity resides in, or null if removed.
	 * @return chunk the entity is in, or null if removed.
	 */
	@SnapshotRead
	public Chunk getChunk();

	/**
	 * Gets the region the entity is associated and managed with, or null if removed.
	 * @return region the entity is in.
	 */
	@SnapshotRead
	public Region getRegion();	
}
