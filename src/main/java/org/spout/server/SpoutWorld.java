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
package org.spout.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.spout.api.Game;
import org.spout.api.Server;
import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.basic.blocks.BlockFullState;
import org.spout.api.collision.BoundingBox;
import org.spout.api.collision.CollisionModel;
import org.spout.api.collision.CollisionVolume;
import org.spout.api.datatable.Datatable;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.event.entity.EntitySpawnEvent;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.atomic.Transform;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.MathHelper;
import org.spout.api.player.Player;
import org.spout.server.entity.EntityManager;
import org.spout.server.entity.SpoutEntity;
import org.spout.server.util.thread.AsyncManager;
import org.spout.server.util.thread.ThreadAsyncExecutor;
import org.spout.server.util.thread.snapshotable.SnapshotManager;
import org.spout.server.util.thread.snapshotable.SnapshotableLong;
import org.spout.server.util.thread.snapshotable.SnapshotableReference;

public class SpoutWorld extends AsyncManager implements World {

	private SnapshotManager snapshotManager = new SnapshotManager();

	/**
	 * The server of this world.
	 */
	private final Server server;

	/**
	 * The name of this world.
	 */
	private final String name;

	/**
	 * The region source
	 */
	private final RegionSource regions;

	/**
	 * The world seed.
	 */
	private final long seed;

	/**
	 * The spawn position.
	 */
	private SnapshotableReference<Transform> spawnLocation = new SnapshotableReference<Transform>(snapshotManager, null);

	/**
	 * The current world age.
	 */
	private SnapshotableLong age = new SnapshotableLong(snapshotManager, 0L);

	/**
	 * The world's UUID.
	 */
	private final UUID uid;

	/**
	 * The generator responsible for generating chunks in this world.
	 */
	private final WorldGenerator generator;

	/**
	 * Holds all of the entities to be simulated
	 */
	private final EntityManager entityManager;

	/**
	 * A set of all players currently connected to this world
	 */
	private final Set<Player> players = Collections.newSetFromMap(new ConcurrentHashMap<Player, Boolean>());

	// TODO need world that loads from disk
	// TODO set up number of stages ?
	public SpoutWorld(String name, Server server, long seed, WorldGenerator generator) {
		super(1, new ThreadAsyncExecutor(), server);
		uid = UUID.randomUUID();
		this.server = server;
		this.seed = seed;
		this.name = name;
		this.generator = generator;
		entityManager = new EntityManager();
		regions = new RegionSource(this, snapshotManager);

		//load spawn regions
		for (int dx = -1; dx < 1; dx++) {
			for (int dy = -1; dy < 1; dy++) {
				for (int dz = -1; dz < 1; dz++) {
					regions.getRegion(dx, dy, dz, true, true);
				}
			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getAge() {
		return age.get();
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return new SpoutBlock(this, x, y, z, getChunkFromBlock(x, y, z).getBlockMaterial(x, y, z));
	}

	@Override
	public Block getBlock(Point point) {
		int x = MathHelper.floor(point.getX());
		int y = MathHelper.floor(point.getY());
		int z = MathHelper.floor(point.getZ());
		return getBlock(x, y, z);
	}

	@Override
	public UUID getUID() {
		// TODO non-null
		return uid;
	}

	@Override
	public Region getRegion(int x, int y, int z) {
		return regions.getRegion(x, y, z);
	}

	@Override
	public Region getRegion(Point point) {
		int x = MathHelper.floor(point.getX());
		int y = MathHelper.floor(point.getY());
		int z = MathHelper.floor(point.getZ());
		return regions.getRegionFromBlock(x, y, z);
	}

	@Override
	public Region getRegion(int x, int y, int z, boolean load) {
		return regions.getRegion(x, y, z, load);
	}

	@Override
	public Region getRegion(Point point, boolean load) {
		int x = MathHelper.floor(point.getX());
		int y = MathHelper.floor(point.getY());
		int z = MathHelper.floor(point.getZ());
		return regions.getRegionFromBlock(x, y, z, load);
	}

	@Override
	public Region getRegionFromBlock(int x, int y, int z) {
		return regions.getRegionFromBlock(x, y, z);
	}

	@Override
	public Chunk getChunk(int x, int y, int z) {
		return getChunk(x, y, z, true);
	}

	@Override
	public Chunk getChunk(int x, int y, int z, boolean load) {
		Region region = getRegion(x >> Region.REGION_SIZE_BITS, y >> Region.REGION_SIZE_BITS, z >> Region.REGION_SIZE_BITS, load);
		if (region != null) {
			return region.getChunk(x & Region.REGION_SIZE - 1, y & Region.REGION_SIZE - 1, z & Region.REGION_SIZE - 1, load);
		}
		return null;
	}

	@Override
	public Chunk getChunk(Point point) {
		return getChunk(point, true);
	}

	@Override
	public Chunk getChunk(Point point, boolean load) {
		int x = MathHelper.floor(point.getX());
		int y = MathHelper.floor(point.getY());
		int z = MathHelper.floor(point.getZ());
		return getChunk(x >> Chunk.CHUNK_SIZE_BITS, y >> Chunk.CHUNK_SIZE_BITS, z >> Chunk.CHUNK_SIZE_BITS, load);
	}
	
	@Override
	public Chunk getChunkFromBlock(int x, int y, int z) {
		return getChunk(x >> Chunk.CHUNK_SIZE_BITS, y >> Chunk.CHUNK_SIZE_BITS, z >> Chunk.CHUNK_SIZE_BITS);
	}

	@Override
	public int hashCode() {
		UUID uid = getUID();
		long hash = uid.getMostSignificantBits();
		hash += (hash << 5) + uid.getLeastSignificantBits();

		return (int) (hash ^ hash >> 32);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (!(obj instanceof SpoutWorld)) {
			return false;
		} else {
			SpoutWorld world = (SpoutWorld) obj;

			return world.getUID().equals(getUID());
		}

	}

	@Override
	public Entity createEntity(Point point, Controller controller) {
		return new SpoutEntity((SpoutServer) server, point, controller);
	}

	/**
	 * Spawns an entity into the world.  Fires off a cancellable EntitySpawnEvent
	 */
	@Override
	public void spawnEntity(Entity e) {
		if (e.isSpawned()) {
			throw new IllegalArgumentException("Cannot spawn an entity that is already spawned!");
		}
		SpoutRegion region = (SpoutRegion) e.getRegion();
		region.addEntity(e);

	}

	@Override
	public Entity createAndSpawnEntity(Point point, Controller controller) {
		Entity e = createEntity(point, controller);
		//initialize region if needed
		getRegion(point, true);
		spawnEntity(e);
		return e;
	}

	@Override
	public void copySnapshotRun() throws InterruptedException {
		entityManager.copyAllSnapshots();
		snapshotManager.copyAllSnapshots();
	}

	@Override
	public void startTickRun(int stage, long delta) throws InterruptedException {
		switch (stage) {
			case 0: {
				float dt = delta / 1000.f;
				//Update all entities
				for (SpoutEntity ent : entityManager) {
					try {
						ent.onTick(dt);
					} catch (Exception e) {
						Spout.getGame().getLogger().severe("Unhandled exception during tick for " + ent.toString());
						e.printStackTrace();
					}
				}
				break;
			}
			case 1: {
				//Resolve and collisions and prepare for a snapshot.
				for (SpoutEntity ent : entityManager) {
					try {
						ent.resolve();
					} catch (Exception e) {
						Spout.getGame().getLogger().severe("Unhandled exception during tick resolution for " + ent.toString());
						e.printStackTrace();
					}
				}
				break;
			}
			default: {
				throw new IllegalStateException("Number of states exceeded limit for SpoutRegion");
			}
		}
	}

	@Override
	public void haltRun() throws InterruptedException {
		// TODO - save on halt ?
	}

	@Override
	public long getSeed() {
		return seed;
	}

	@Override
	public Game getGame() {
		return server;
	}

	@Override
	public int getHeight() {
		// TODO: Variable world height
		return 128;
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, Source source) {
		return getChunkFromBlock(x, y, z).setBlockMaterial(x, y, z, material, true, source);
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, boolean updatePhysics, Source source) {
		return getChunkFromBlock(x, y, z).setBlockMaterial(x, y, z, material, updatePhysics, source);
	}

	@Override
	public boolean setBlockId(int x, int y, int z, short id, Source source) {
		return getChunkFromBlock(x, y, z).setBlockId(x, y, z, id, true, source);
	}

	@Override
	public boolean setBlockId(int x, int y, int z, short id, boolean updatePhysics, Source source) {
		return getChunkFromBlock(x, y, z).setBlockId(x, y, z, id, updatePhysics, source);
	}

	@Override
	public boolean setBlockData(int x, int y, int z, short data, boolean updatePhysics, Source source) {
		return getChunkFromBlock(x, y, z).setBlockData(x, y, z, data, updatePhysics, source);
	}

	@Override
	public boolean setBlockData(int x, int y, int z, short data, Source source) {
		return getChunkFromBlock(x, y, z).setBlockData(x, y, z, data, source);
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockMaterial(x, y, z);
	}

	@Override
	public short getBlockId(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockId(x, y, z);
	}

	@Override
	public short getBlockData(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockData(x, y, z);
	}

	@Override
	public boolean compareAndPut(int x, int y, int z, BlockFullState<DatatableMap> expect, String key, Datatable auxData) {
		return getChunkFromBlock(x, y, z).compareAndPut(x, y, z, expect, key, auxData);
	}

	@Override
	public boolean compareAndRemove(int x, int y, int z, BlockFullState<DatatableMap> expect, String key, Datatable auxData) {
		return getChunkFromBlock(x, y, z).compareAndRemove(x, y, z, expect, key, auxData);
	}

	@Override
	public boolean compareAndSetData(int x, int y, int z, BlockFullState<DatatableMap> expect, short data) {
		return getChunkFromBlock(x, y, z).compareAndSetData(x, y, z, expect, data);
	}

	@Override
	public boolean setBlockIdAndData(int x, int y, int z, short id, short data, Source source) {
		return getChunkFromBlock(x, y, z).setBlockIdAndData(x, y, z, id, data, true, source);
	}

	@Override
	public boolean setBlockIdAndData(int x, int y, int z, short id, short data, boolean updatePhysics, Source source) {
		return getChunkFromBlock(x, y, z).setBlockIdAndData(x, y, z, id, data, updatePhysics, source);
	}

	@Override
	public void updatePhysics(int x, int y, int z) {
		regions.getRegionFromBlock(x, y, z).queuePhysicsUpdate(x, y, z);
	}

	@Override
	public Transform getSpawnPoint() {
		return spawnLocation.get();
	}

	@Override
	public void setSpawnPoint(Transform transform) {
		spawnLocation.set(transform.copy());
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public void finalizeRun() throws InterruptedException {
		entityManager.finalizeRun();
	}

	@Override
	public void preSnapshotRun() throws InterruptedException {
		entityManager.preSnapshotRun();
	}

	@Override
	public WorldGenerator getGenerator() {
		return generator;
	}

	@Override
	public HashSet<Entity> getAll() {
		HashSet<Entity> entities = new HashSet<Entity>();
		for (Region region : regions) {
			entities.addAll(region.getAll());
		}
		return entities;
	}

	@Override
	public HashSet<Entity> getAll(Class<? extends Controller> type) {
		HashSet<Entity> entities = new HashSet<Entity>();
		for (Region region : regions) {
			entities.addAll(region.getAll(type));
		}
		return entities;
	}

	public void addPlayer(Player player) {
		players.add(player);
	}

	public void removePlayer(Player player) {
		players.remove(player);
	}

	@Override
	public Set<Player> getPlayers() {
		return Collections.unmodifiableSet(players);
	}
	
	public List<CollisionVolume> getCollidingObject(CollisionModel model){
		//TODO Make this more general
		final int minX = MathHelper.floor(model.getPosition().getX());
		final int minY = MathHelper.floor(model.getPosition().getY());
		final int minZ = MathHelper.floor(model.getPosition().getZ());
		final int maxX = minX + 1;
		final int maxY = minY + 1;
		final int maxZ = minZ + 1;
		
		final LinkedList<CollisionVolume> colliding = new LinkedList<CollisionVolume>();
		
		final BoundingBox mutable = new BoundingBox(0, 0, 0, 0, 0, 0);
		final BoundingBox position = new BoundingBox((BoundingBox)model.getVolume());
		position.offset(minX, minY, minZ);
		
		for (int dx = minX; dx < maxX; dx++) {
			for (int dy = minY - 1; dy < maxY; dy++) {
				for (int dz = minZ; dz < maxZ; dz++) {
					BlockMaterial material = this.getBlockMaterial(dx, dy, dz);
					mutable.set((BoundingBox)material.getBoundingArea());
					mutable.offset(dx, dy, dz);
					if (mutable.intersects(position)) {
						colliding.add(mutable.clone());
					}
				}
			}
		}
		
		//TODO: colliding entities
		return colliding;
		
	}
	
	
	@Override
	public String toString() {
		return "SpoutWorld{ " + getName() + " UUID: " + this.uid + " Age: " + this.getAge() + "}";
	}
}
