/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.geo;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.spout.api.Engine;
import org.spout.api.component.Component;
import org.spout.api.component.ComponentOwner;
import org.spout.api.component.DatatableComponent;
import org.spout.api.data.DataSubject;
import org.spout.api.entity.Entity;
import org.spout.api.entity.EntityPrefab;
import org.spout.api.entity.Player;
import org.spout.api.entity.spawn.SpawnArrangement;
import org.spout.api.event.Cause;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.lighting.LightingManager;
import org.spout.api.material.BlockMaterial;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.util.Named;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;
import org.spout.api.util.thread.annotation.LiveRead;
import org.spout.api.util.thread.annotation.SnapshotRead;
import org.spout.api.util.thread.annotation.Threadsafe;

/**
 * Represents a World.
 */
public interface World extends AreaRegionAccess, AreaPhysicsAccess, Named, ComponentOwner, DataSubject {
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
	 * @param loadopt load option
	 * @return the highest of the highest block
	 */
	@LiveRead
	public int getSurfaceHeight(int x, int z, LoadOption loadopt);
	
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
	 * @param loadopt load option
	 * @return the BlockMaterial
	 */
	@LiveRead
	public BlockMaterial getTopmostBlock(int x, int z, LoadOption loadopt);

	/**
	 * Gets the biome manager in the given (x, z) column.<br>
	 * @param x the block x coordinate of the column
	 * @param z the block z coordinate of the column
	 * @param loadopt load option
	 * @return the biome manager
	 */
	@LiveRead
	public BiomeManager getBiomeManager(int x, int z, LoadOption loadopt);

	/**
	 * Gets the entity with the matching unique id
	 * <p/>
	 * Performs a search on each region for the entity, stopping when it
	 * is found, or after all the worlds have been searched upon failure.
	 * @param uid to search and match
	 * @return entity that matched the uid, or null if none was found
	 */
	@SnapshotRead
	public Entity getEntity(UUID uid);

	/**
	 * Creates a new {@link Entity} at the {@link Point} with the {@link Component} classes attached.
	 * @param point The area in space where spawn will occur
	 * @param classes The classes to attach
	 * @return The entity set to spawn at the point provided with components attached
	 */
	public Entity createEntity(Point point, Class<? extends Component>... classes);

	/**
	 * Creates a new {@link Entity} at the {@link Point} blueprinted with the {@link EntityPrefab} provided.
	 * @param point The area in space where spawn will occur
	 * @param prefab The blueprint
	 * @return The entity set to spawn at the point provided with the prefab applied
	 */
	public Entity createEntity(Point point, EntityPrefab prefab);

	/**
	 * Spawns the {@link Entity}.
	 * @param e Entity to spawn
	 */
	public void spawnEntity(Entity e);

	/**
	 * Creates and spawns an {@link Entity} at the {@link Point} blueprinted with the {@link EntityPrefab} provided.
	 * <p/>
	 * The {@link LoadOption} parameter is used to tell Spout if it should load, create and load, or not load the chunk
	 * for the point provided. Great caution should be used; only load (and more so create) if absolutely necessary.
	 * @param point The area in space to spawn
	 * @param option Whether to not load, load, or load and create the chunk
	 * @param prefab The blueprint
	 * @return The spawned entity at the point with the prefab applied
	 */
	public Entity createAndSpawnEntity(Point point, LoadOption option, EntityPrefab prefab);

	/**
	 * Creates and spawns an {@link Entity} at the {@link Point} with the {@link Component} classes attached.
	 * <p/>
	 * The {@link LoadOption} parameter is used to tell Spout if it should load, create and load, or not load the chunk
	 * for the point provided. Great caution should be used; only load (and more so create) if absolutely necessary.
	 * @param point The area in space to spawn
	 * @param option Whether to not load, load, or load and create the chunk
	 * @param classes The classes to attach
	 * @return The spawned entity at the point with the components attached
	 */
	public Entity createAndSpawnEntity(Point point, LoadOption option, Class<? extends Component>... classes);

	/**
	 * Creates and spawns multiple {@link Entity} at the {@link Point}s with the {@link Component} classes attached.
	 * <p/>
	 * The {@link LoadOption} parameter is used to tell Spout if it should load, create and load, or not load the chunk
	 * for the points provided. Great caution should be used; only load (and more so create) if absolutely necessary.
	 * @param points The areas in space to spawn
	 * @param option Whether to not load, load, or load and create the chunk
	 * @param classes The classes to attach
	 * @return The spawned entities at the points with the components attached
	 */
	public Entity[] createAndSpawnEntity(Point[] points, LoadOption option, Class<? extends Component>... classes);

	/**
	 * Creates and spawns multiple {@link Entity} with the {@link Component} classes attached. The {@link SpawnArrangement}
	 * is a template for how to spawn (i.e. spawn entities around a point in a circle).
	 * <p/>
	 * The {@link LoadOption} parameter is used to tell Spout if it should load, create and load, or not load the chunk
	 * for the points provided. Great caution should be used; only load (and more so create) if absolutely necessary.
	 * @param arrangement The template for the spawn
	 * @param option Whether to not load, load, or load and create the chunk
	 * @param classes The classes to attach
	 * @return The spawned entities at the points with the components attached
	 */
	public Entity[] createAndSpawnEntity(SpawnArrangement arrangement, LoadOption option, Class<? extends Component>... classes);

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
	 * @return A collection of entities with the specified type.
	 */
	@SnapshotRead
	public List<Entity> getAll();

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
	public List<Player> getPlayers();

	/**
	 * Gets the directory where world data is stored
	 */
	public File getDirectory();

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
	 * Gets a list of nearby entities of the point, inside of the range
	 * @param position of the center
	 * @param ignore Entity to ignore
	 * @param range to look for
	 * @return the list of nearby entities (or empty if none)
	 */
	public List<Entity> getNearbyEntities(Point position, Entity ignore, int range);

	/**
	 * Gets a set of nearby players to the point, inside of the range
	 * @param position of the center
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public List<Entity> getNearbyEntities(Point position, int range);

	/**
	 * Gets a set of nearby players to the entity, inside of the range
	 * @param entity marking the center and which is ignored
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public List<Entity> getNearbyEntities(Entity entity, int range);

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * @param position to search from
	 * @param ignore to ignore while searching
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Entity getNearestEntity(Point position, Entity ignore, int range);

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * @param position center of search
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Entity getNearestEntity(Point position, int range);

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * @param entity to search from
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Entity getNearestEntity(Entity entity, int range);

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
	public List<Player> getNearbyPlayers(Point position, Player ignore, int range);

	/**
	 * Gets a set of nearby players to the point, inside of the range
	 * @param position of the center
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public List<Player> getNearbyPlayers(Point position, int range);

	/**
	 * Gets a set of nearby players to the entity, inside of the range
	 * @param entity marking the center and which is ignored
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public List<Player> getNearbyPlayers(Entity entity, int range);

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * @param position to search from
	 * @param ignore to ignore while searching
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Point position, Player ignore, int range);

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
	 * Atomically sets the cuboid volume to the values inside of the cuboid buffer.
	 * @param buffer
	 * @param cause that is setting the cuboid volume
	 */
	@Threadsafe
	public void setCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause);

	/**
	 * Atomically sets the cuboid volume to the values inside of the cuboid buffer with the base located at the given coords
	 * @param x
	 * @param y
	 * @param z
	 * @param buffer
	 * @param cause that is setting the cuboid volume
	 */
	@Threadsafe
	public void setCuboid(int x, int y, int z, CuboidBlockMaterialBuffer buffer, Cause<?> cause);

	/**
	 * Atomically gets the cuboid volume with the base located at the given coords of the given size.<br>
	 * <br>
	 * Note: The block at the base coordinate is inside the
	 * @param bx base x-coordinate
	 * @param by base y-coordinate
	 * @param bz base z-coordinate
	 * @param sx size x-coordinate
	 * @param sy size y-coordinate
	 * @param sz size z-coordinate
	 */
	@Threadsafe
	public CuboidBlockMaterialBuffer getCuboid(int bx, int by, int bz, int sx, int sy, int sz);

	/**
	 * Atomically gets the cuboid volume with the base located at the given coords and the size of the given buffer.<br>
	 * <br>
	 * Note: The block at the base coordinate is inside the
	 * @param bx base x-coordinate
	 * @param by base y-coordinate
	 * @param bz base z-coordinate
	 */
	@Threadsafe
	public void getCuboid(int bx, int by, int bz, CuboidBlockMaterialBuffer buffer);

	/**
	 * Atomically gets the cuboid volume contained within the given buffer
	 * @param buffer the buffer
	 */
	@Threadsafe
	public void getCuboid(CuboidBlockMaterialBuffer buffer);

	/**
	 * Unloads the world from the server. Undefined behavior will occur
	 * if any players are currently alive on the world while it is being
	 * unloaded.
	 * @param save
	 */
	public void unload(boolean save);

	/**
	 * Adds a lighting manager to the world
	 * @param manager the lighting manager
	 * @return true, if the lighting manager was added
	 */
	public boolean addLightingManager(LightingManager<?> manager);

	/**
	 * Saves all world data to world data file.
	 * <p>
	 * Note: World data does not include chunks, regions, or other data.
	 * World data pertains to world age, world name, and world data maps.
	 * </p>
	 */
	public void save();

	/**
	 * Gets the {@link DatatableComponent} which is always attached to each world.
	 * <p/>
	 * This is merely a convenience method.
	 * @return datatable component
	 */
	public DatatableComponent getDatatable();
}
