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
package org.spout.api.geo;

import java.util.Set;
import java.util.UUID;

import org.spout.api.Game;
import org.spout.api.Source;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.atomic.Transform;
import org.spout.api.player.Player;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;
import org.spout.api.util.thread.Threadsafe;

/**
 * Represents a World.
 */
public interface World extends Source, BlockAccess {
	/**
	 * Gets the name of the world
	 *
	 * @return the name of the world
	 */
	@SnapshotRead
	public String getName();

	/**
	 * Gets the age of the world in ms. This count cannot be modified, and
	 * increments on every tick
	 *
	 * @return the world's age in ms
	 */
	@SnapshotRead
	public long getAge();

	/**
	 * Gets a {@link Block} representing a particular location in the world
	 *
	 * @return the Block
	 */
	@Threadsafe
	public Block getBlock(int x, int y, int z);

	/**
	 * Gets a {@link Block} representing a particular point in the world
	 *
	 * @param point The point
	 * @return the Block
	 */
	@Threadsafe
	public Block getBlock(Point point);

	/**
	 * Gets the UID representing the world. With extremely high probability the
	 * UID is unique to each world.
	 *
	 * @return the name of the world
	 */
	@SnapshotRead
	public UUID getUID();

	/**
	 * Gets the {@link Region} at region coordinates (x, y, z)
	 *
	 * @param x the region x coordinate
	 * @param y the region y coordinate
	 * @param z the region z coordinate
	 * @return the region
	 */
	@LiveRead
	public Region getRegion(int x, int y, int z);

	/**
	 * Gets the {@link Region} at region coordinates (x, y, z)
	 *
	 * @param x the region x coordinate
	 * @param y the region y coordinate
	 * @param z the region z coordinate
	 * @param load true if the region should be loaded/generated
	 * @return the region
	 */
	@LiveRead
	public Region getRegion(int x, int y, int z, boolean load);

	/**
	 * Gets the {@link Region} at block position
	 *
	 * @param point in the world
	 * @return the region
	 */
	@LiveRead
	public Region getRegion(Point point);

	/**
	 * Gets the {@link Region} at block position
	 *
	 * @param point in the world
	 * @param load true if the region should be loaded/generated
	 * @return the region
	 */
	@LiveRead
	public Region getRegion(Point point, boolean load);

	/**
	 * Gets the {@link Region} at block coordinates (x, y, z)
	 *
	 * @param x the block x coordinate
	 * @param y the block y coordinate
	 * @param z the block z coordinate
	 * @return the region
	 */
	@LiveRead
	public Region getRegionFromBlock(int x, int y, int z);

	/**
	 * Gets the {@link Chunk} at chunk coordinates (x, y, z)
	 *
	 * @param x the chunk x coordinate
	 * @param y the chunk y coordinate
	 * @param z the chunk z coordinate
	 * @return the chunk
	 */
	@LiveRead
	public Chunk getChunk(int x, int y, int z);

	/**
	 * Gets the {@link Chunk} at block position
	 *
	 * @param point in the world
	 * @return the chunk
	 */
	@LiveRead
	public Chunk getChunk(Point point);

	/**
	 * Gets the {@link Chunk} at block position
	 *
	 * @param point in the world
	 * @param load true if the region should be loaded/generated
	 * @return the chunk
	 */
	@LiveRead
	public Chunk getChunk(Point point, boolean load);

	/**
	 * Gets the {@link Chunk} at chunk coordinates (x, y, z)
	 *
	 * @param x the chunk x coordinate
	 * @param y the chunk y coordinate
	 * @param z the chunk z coordinate
	 * @param load true if the Chunk should be loaded/generated
	 * @return the chunk
	 */
	@LiveRead
	public Chunk getChunk(int x, int y, int z, boolean load);

	/**
	 * Gets the {@link Chunk} at block coordinates (x, y, z)
	 *
	 * @param x the block x coordinate
	 * @param y the block y coordinate
	 * @param z the block z coordinate
	 * @return the chunk
	 */
	@LiveRead
	public Chunk getChunkFromBlock(int x, int y, int z);

	/**
	 * Create a new Entity for initialization
	 *
	 * This does not add the Entity to the server. You must call
	 * {@link #spawnEntity(Entity)} to simulate the Entity in the world
	 *
	 * @param point The point to spawn the Entity
	 * @param controller The controller that will be attached to the Entity
	 * @return The created entity
	 */
	public Entity createEntity(Point point, Controller controller);

	/**
	 * Add a created entity to the world for simulation and syncing to clients
	 *
	 * @param e The entity to spawn
	 */
	public void spawnEntity(Entity e);

	/**
	 * Creates and Spawns an entity at the given point and with the given
	 * Controller This is the same as {@link #createEntity(Point, Controller)} and
	 * {@link #spawnEntity(Entity)} together.
	 *
	 * @param point The point to spawn the Entity
	 * @param controller The controller that will be attached to the Entity
	 * @return The Entity that has been created and spawned
	 */
	public Entity createAndSpawnEntity(Point point, Controller controller);

	/**
	 * Gets the world's spawn point
	 *
	 * @return the spawn point
	 */
	public Transform getSpawnPoint();

	/**
	 * Sets the world's spawn point
	 *
	 * @param transform the Transform of the spawn point
	 */
	public void setSpawnPoint(Transform transform);

	/**
	 * Gets the world's seed. This value is immutable and set at world creation
	 *
	 * @return the seed
	 */
	@Threadsafe
	public long getSeed();

	/**
	 * Gets the {@link WorldGenerator} responsible for generating new chunks for
	 * this world
	 *
	 * @return generator
	 */
	public WorldGenerator getGenerator();

	/**
	 * Gets the game associated with this world
	 *
	 * @return the game
	 */
	public Game getGame();

	/**
	 * Gets the height of this world in blocks.
	 *
	 * @return The height of this world in blocks
	 */
	public int getHeight();

	/**
	 * Gets all entities with the specified type.
	 *
	 * @param type The {@link Class} for the type.
	 * @return A collection of entities with the specified type.
	 */
	@SnapshotRead
	public Set<Entity> getAll(Class<? extends Controller> type);

	/**
	 * Gets all entities.
	 *
	 * @return A collection of entities.
	 */
	@SnapshotRead
	public Set<Entity> getAll();

	/**
	 * Gets a set of all players on active on this world
	 *
	 * @return all players on this world
	 */
	public Set<Player> getPlayers();


}
