package org.getspout.api.geo;

import java.util.UUID;

import org.getspout.api.Server;
import org.getspout.api.entity.Controller;
import org.getspout.api.entity.Entity;
import org.getspout.api.event.EventSource;
import org.getspout.api.geo.cuboid.Block;
import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;
import org.getspout.api.util.thread.Threadsafe;

/**
 * Represents a World.
 */
public interface World extends EventSource, BlockAccess {

	/**
	 * Gets the name of the world
	 *
	 * @return the name of the world
	 */
	@SnapshotRead
	public String getName();

	/**
	 * Gets the age of the world in ms. This count cannot be modified, and increments on every tick
	 *
	 * @return the world's age in ms
	 */
	@SnapshotRead
	public long getAge();

	/**
	 * Gets a Block representing a particular location in the world
	 *
	 * @return the Block
	 */
	@Threadsafe
	public Block getBlock(int x, int y, int z);
	
	/**
	 * Gets a Block representing a particular point in the world
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
	 * Gets the region at region coordinates (x, y, z)
	 * 
	 * @param x the region x coordinate
	 * @param y the region y coordinate
	 * @param z the region z coordinate
	 * @return the region
	 */
	@SnapshotRead
	public Region getRegion(int x, int y, int z);
	
	/**
	 * Gets the region at region coordinates (x, y, z)
	 * 
	 * @param x the region x coordinate
	 * @param y the region y coordinate
	 * @param z the region z coordinate
	 * @param load true if the region should be loaded/generated
	 * @return the region
	 */
	@LiveRead
	public Region getRegionLive(int x, int y, int z, boolean load);
	
	/**
	 * Gets the region at block position
	 * 
	 * @param point in the world
	 * @return the region
	 */
	@SnapshotRead
	public Region getRegion(Point point);
	
	/**
	 * Gets the region at block position
	 * 
	 * @param point in the world
	 * @param load true if the region should be loaded/generated
	 * @return the region
	 */
	@LiveRead
	public Region getRegionLive(Point point, boolean load);
	
	/**
	 * Gets the chunk at chunk coordinates (x, y, z)
	 * 
	 * @param x the chunk x coordinate
	 * @param y the chunk y coordinate
	 * @param z the chunk z coordinate
	 * @return the chunk
	 */
	@SnapshotRead
	public Chunk getChunk(int x, int y, int z);
	
	/**
	 * Gets the chunk at block position
	 * 
	 * @param point in the world
	 * @return the chunk
	 */
	@SnapshotRead
	public Chunk getChunk(Point point);
	
	/**
	 * Gets the chunk at chunk coordinates (x, y, z)
	 * 
	 * @param x the chunk x coordinate
	 * @param y the chunk y coordinate
	 * @param z the chunk z coordinate
	 * @param load true if the Chunk should be loaded/generated
	 * @return the chunk
	 */
	@LiveRead
	public Chunk getChunkLive(int x, int y, int z, boolean load);
	
	/**
	 * Gets the chunk at block position
	 * 
	 * @param point in the world
	 * @param load true if the Chunk should be loaded/generated
	 * @return the chunk
	 */
	@LiveRead
	public Chunk getChunkLive(Point point, boolean load);
	
	/**
	 * Create a new Entity for initialization
	 * 
	 * This does not add the Entity to the server.  You must call {@link #spawnEntity(Entity)} to simulate the Entity in the world
	 * @param point The point to spawn the Entity
	 * @param controller The controller that will be attached to the Entity
	 * @return
	 */
	public Entity createEntity(Point point, Controller controller);
	/**
	 * Add a created entity to the world for simulation and syncing to clients
	 * @param e
	 */
	public void spawnEntity(Entity e);
	
	/**
	 * Creates and Spawns an entity at the given point and with the given Controller
	 * This is the same as {@link #createEntity()} and {@link #spawnEntity(Entity)} together.
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
	 * Gets the world's seed.  This value is immutable and set at world creation
	 * 
	 * @return the seed
	 */
	@Threadsafe
	public long getSeed();
	
	/**
	 * Gets the server associated with this world
	 * 
	 * @return the server
	 */
	public Server getServer();
}
