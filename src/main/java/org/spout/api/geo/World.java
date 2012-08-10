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
package org.spout.api.geo;

import java.io.File;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import org.spout.api.Engine;
import org.spout.api.Source;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.entity.controller.type.ControllerType;
import org.spout.api.entity.spawn.SpawnArrangement;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.map.DefaultedMap;
import org.spout.api.material.BlockMaterial;
import org.spout.api.plugin.Plugin;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.util.Named;
import org.spout.api.util.cuboid.CuboidBuffer;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.LiveWrite;
import org.spout.api.util.thread.SnapshotRead;
import org.spout.api.util.thread.Threadsafe;

/**
 * Represents a World.
 */
public interface World extends Source, AreaRegionAccess, AreaPhysicsAccess, Named {
	/**
	 * Gets the name of the world
	 * @return the name of the world
	 */
	@SnapshotRead
	@Override
	public String getName();

	/**
	 * Gets the age of the world in ms. This count cannot be modified, and
	 * increments on every tick
	 * @return the world's age in ms
	 */
	@SnapshotRead
	public long getAge();

	/**
	 * Gets the UID representing the world. With extremely high probability the
	 * UID is unique to each world.
	 * @return the name of the world
	 */
	@SnapshotRead
	public UUID getUID();

	/**
	 * Gets the height of the highest block in the given (x, z) column.<br>
	 * <br>
	 * Blocks which are completely transparent are ignored.
	 * @param x the block x coordinate of the column
	 * @param z the block z coordinate of the column
	 * @return the highest of the highest block
	 */
	@LiveRead
	public int getSurfaceHeight(int x, int z);

	/**
	 * Gets the height of the highest block in the given (x, z) column.<br>
	 * <br>
	 * Blocks which are completely transparent are ignored.
	 * @param x the block x coordinate of the column
	 * @param z the block z coordinate of the column
	 * @param load height map is loaded if necessary
	 * @return the highest of the highest block
	 */
	@LiveRead
	public int getSurfaceHeight(int x, int z, boolean load);

	/**
	 * Gets the BlockMaterial of the highest block in the given (x, z) column.<br>
	 * @param x the block x coordinate of the column
	 * @param z the block z coordinate of the column
	 * @return the BlockMaterial
	 */
	@LiveRead
	public BlockMaterial getTopmostBlock(int x, int z);

	/**
	 * Gets the BlockMaterial of the highest block in the given (x, z) column.<br>
	 * @param x the block x coordinate of the column
	 * @param z the block z coordinate of the column
	 * @param load height map is loaded if necessary
	 * @return the BlockMaterial
	 */
	@LiveRead
	public BlockMaterial getTopmostBlock(int x, int z, boolean load);

	/**
	 * Gets the entity with the matching unique id
	 * <br/> <br/>
	 * Performs a search on each region for the entity, stopping when it
	 * is found, or after all the worlds have been searched upon failure.
	 * @param uid to search and match
	 * @return entity that matched the uid, or null if none was found
	 */
	@SnapshotRead
	public Entity getEntity(UUID uid);

	/**
	 * Create a new Entity for initialization
	 * <p/>
	 * This does not add the Entity to the server. You must call
	 * {@link #spawnEntity(Entity)} to simulate the Entity in the world
	 * @param point The point to spawn the Entity
	 * @param controller The entity that will be attached to the Entity
	 * @return The created entity
	 */
	public Entity createEntity(Point point, Controller controller);

	/**
	 * Add a created entity to the world for simulation and syncing to clients
	 * @param e The entity to spawn
	 */
	public void spawnEntity(Entity e);

	/**
	 * Creates and Spawns an entity at the given point and with the given
	 * Controller This is the same as {@link #createEntity(Point, org.spout.api.entity.Controller)} and
	 * {@link #spawnEntity(Entity)} together.
	 * @param point The point to spawn the Entity
	 * @param controller The entity that will be attached to the Entity
	 * @return The Entity that has been created and spawned
	 */
	public Entity createAndSpawnEntity(Point point, Controller controller);

	/**
	 * Creates and Spawns entities at the given points.  This is the same as calling
	 * {@link #createAndSpawnEntity(point, controller)} for each element in the array.
	 * @param points The points to use for spawning the entities
	 * @param type The type of entity that will be attached to the Entity
	 * @return The Entities that has been created and spawned
	 */
	public Entity[] createAndSpawnEntity(Point[] points, ControllerType type);

	/**
	 * Creates and Spawns entities at the given points.  This is the same as calling
	 * {@link #createAndSpawnEntity(point, controller)} for each point with the
	 * corresponding element from the entity array. The two arrays must be the same length.
	 * @param points The points to use for spawning the entities
	 * @param controllers The controllers that will be attached to the Entity
	 * @return The Entities that has been created and spawned
	 */
	public Entity[] createAndSpawnEntity(Point[] points, Controller[] controllers);

	/**
	 * Creates and Spawns entities at the given points.  This is the same as calling
	 * {@link #createAndSpawnEntity(point, controller)} using type.createController()
	 * as the entity for each point. The two arrays must be the same length.
	 * @param points The points to use for spawning the entities
	 * @param types The entity types that will be attached to the Entity
	 * @return The Entities that has been created and spawned
	 */
	public Entity[] createAndSpawnEntity(Point[] points, ControllerType[] types);

	/**
	 * Creates and Spawns entities for the given arrangement.  This is the same as calling
	 * {@link #createAndSpawnEntity(point, controller)} for each Point, entity pair in
	 * the arrangement
	 * @param points The points to use for spawning the entities
	 * @param controller The entity that will be attached to the Entity
	 * @return The Entities that has been created and spawned
	 */
	public Entity[] createAndSpawnEntity(SpawnArrangement arrangement);

	/**
	 * Gets the world's spawn point
	 * @return the spawn point
	 */
	public Transform getSpawnPoint();

	/**
	 * Sets the world's spawn point
	 * @param transform the Transform of the spawn point
	 */
	public void setSpawnPoint(Transform transform);

	/**
	 * Gets the world's seed. This value is immutable and set at world creation
	 * @return the seed
	 */
	@Threadsafe
	public long getSeed();

	/**
	 * Gets the {@link WorldGenerator} responsible for generating new chunks for
	 * this world
	 * @return generator
	 */
	public WorldGenerator getGenerator();

	/**
	 * Gets the engine associated with this world
	 * @return the engine
	 */
	public Engine getEngine();

	/**
	 * Gets the height of this world in blocks.
	 * @return The height of this world in blocks
	 */
	public int getHeight();

	/**
	 * Gets the light level the sky emits<br>
	 * Block sky light levels are affected by this
	 * @return the sky light, a level from 0 to 15
	 */
	public byte getSkyLight();

	/**
	 * Sets the light level the sky emits<br>
	 * Block sky light levels are affected by this
	 * @param newLight level from 0 to 15 for the sky light
	 */
	public void setSkyLight(byte newLight);

	/**
	 * Gets all entities with the specified type.
	 * @param type The {@link Class} for the type.
	 * @return A collection of entities with the specified type.
	 */
	@SnapshotRead
	public Set<Entity> getAll(Class<? extends Controller> type);

	/**
	 * Gets all entities.
	 * @return A collection of entities.
	 */
	@SnapshotRead
	public Set<Entity> getAll();

	/**
	 * Gets an entity by its id.
	 * @param id The id.
	 * @return The entity, or {@code null} if it could not be found.
	 */
	@SnapshotRead
	public Entity getEntity(int id);

	/**
	 * Gets a set of all players on active on this world
	 * @return all players on this world
	 */
	public Set<Player> getPlayers();

	/**
	 * Gets the directory where world data is stored
	 */
	public File getDirectory();

	/**
	 * Gets a map of data attached to this world. Data will persist across restarts.
	 * @return data map
	 */
	public DefaultedMap<String, Serializable> getDataMap();

	/**
	 * Gets a value from the data map by providing a key. Data will persist across restarts.
	 * @param key The key to lookup a value from the map
	 * @return the data stored for this key or null if no data found.
	 */
	public Serializable get(Object key);

	/**
	 * Gets the task manager responsible for parallel region tasks.<br>
	 * <br>
	 * All tasks are submitted to all loaded regions at the start of the next tick.<br>
	 * <br>
	 * Repeating tasks are also submitted to all new regions when they are created.<br>
	 * Repeated tasks are NOT guaranteed to happen in the same tick for all regions,
	 * as each task is submitted individually to each region.<br>
	 * <br>
	 * This task manager does not support async tasks.
	 * <br>
	 * If the Runnable for the task is a ParallelRunnable, then a new instance of the Runnable will be created for each region.
	 * @return the parallel task manager for the engine
	 */
	public TaskManager getParallelTaskManager();

	/**
	 * Gets the TaskManager associated with this world
	 */
	public abstract TaskManager getTaskManager();

	/**
	 * Gets a set of nearby players to the point, inside of the range
	 * @param position of the center
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public Set<Player> getNearbyPlayers(Point position, int range);

	/**
	 * Gets a set of nearby players to the entity, inside of the range
	 * @param entity marking the center and which is ignored
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public Set<Player> getNearbyPlayers(Entity entity, int range);

	/**
	 * Gets a set of nearby players to the point, inside of the range.
	 * The search will ignore the specified entity.
	 * @param position of the center
	 * @param ignore Entity to ignore
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public Set<Player> getNearbyPlayers(Point position, Entity ignore, int range);

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * @param position to search from
	 * @param ignore to ignore while searching
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Point position, Entity ignore, int range);

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * @param position center of search
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Point position, int range);

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * @param entity to search from
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Entity entity, int range);

	/**
	 * Sets the cuboid area to the values inside of the cuboid buffer.
	 * @param buffer
	 * @param plugin that is setting the cuboid area
	 * @return false if the set fails
	 */
	@LiveWrite
	public boolean setCuboid(CuboidBuffer buffer, Plugin plugin);
}
