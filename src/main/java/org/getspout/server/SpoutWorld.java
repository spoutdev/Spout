/*
 * This file is part of Spout (http://www.getspout.org/).
 *
 * Spout is licensed under the SpoutDev license version 1.
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
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */

package org.getspout.server;

import java.util.UUID;

import org.getspout.api.Game;
import org.getspout.api.Server;
import org.getspout.api.Spout;
import org.getspout.api.entity.Controller;
import org.getspout.api.entity.Entity;
import org.getspout.api.generator.WorldGenerator;
import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Block;
import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.material.BlockMaterial;
import org.getspout.server.entity.EntityManager;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.server.util.thread.AsyncManager;
import org.getspout.server.util.thread.ThreadAsyncExecutor;
import org.getspout.server.util.thread.snapshotable.SnapshotManager;
import org.getspout.server.util.thread.snapshotable.SnapshotableBoolean;
import org.getspout.server.util.thread.snapshotable.SnapshotableInt;
import org.getspout.server.util.thread.snapshotable.SnapshotableLong;
import org.getspout.server.util.thread.snapshotable.SnapshotableReference;

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
	 * Whether to keep the spawn chunks in memory (prevent them from being
	 * unloaded)
	 */
	private SnapshotableBoolean keepSpawnLoaded = new SnapshotableBoolean(snapshotManager, true);

	/**
	 * Whether PvP is allowed in this world.
	 */
	private SnapshotableBoolean pvpAllowed = new SnapshotableBoolean(snapshotManager, true);

	/**
	 * Whether animals can spawn in this world.
	 */
	private SnapshotableBoolean spawnAnimals = new SnapshotableBoolean(snapshotManager, true);

	/**
	 * Whether monsters can spawn in this world.
	 */
	private SnapshotableBoolean spawnMonsters = new SnapshotableBoolean(snapshotManager, true);

	/**
	 * Whether it is currently raining/snowing on this world.
	 */
	private SnapshotableBoolean currentlyRaining = new SnapshotableBoolean(snapshotManager, false);

	/**
	 * How many ticks until the rain/snow status is expected to change.
	 */
	private SnapshotableInt rainingTicks = new SnapshotableInt(snapshotManager, 0);

	/**
	 * Whether it is currently thundering on this world.
	 */
	private SnapshotableBoolean currentlyThundering = new SnapshotableBoolean(snapshotManager, false);

	/**
	 * How many ticks until the thundering status is expected to change.
	 */
	private SnapshotableInt thunderingTicks = new SnapshotableInt(snapshotManager, 0);

	/**
	 * The current world age.
	 */
	private SnapshotableLong age = new SnapshotableLong(snapshotManager, 0L);

	/**
	 * The current world time.
	 */
	private SnapshotableInt time = new SnapshotableInt(snapshotManager, 0);

	/**
	 * The current world time.
	 */
	private SnapshotableInt dayLength = new SnapshotableInt(snapshotManager, 0);

	/**
	 * The time until the next full-save.
	 */
	private SnapshotableInt saveTimer = new SnapshotableInt(snapshotManager, 0);

	/**
	 * The check to autosave.
	 */
	private SnapshotableBoolean autosave = new SnapshotableBoolean(snapshotManager, true);

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

	// TODO need world that loads from disk
	// TODO set up number of stages ?
	public SpoutWorld(String name, Server server, long seed, WorldGenerator generator) {
		super(1, new ThreadAsyncExecutor(), server);
		this.uid = UUID.randomUUID();
		this.server = server;
		this.seed = seed;
		this.name = name;
		this.generator = generator;
		this.entityManager = new EntityManager();
		this.regions = new RegionSource(this, snapshotManager);
		
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Block getBlock(Point point) {
		// TODO Auto-generated method stub
		return null;
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
		int x = (int) Math.floor(point.getX());
		int y = (int) Math.floor(point.getY());
		int z = (int) Math.floor(point.getZ());
		return regions.getRegionFromBlock(x, y, z);
	}

	@Override
	public Region getRegion(int x, int y, int z, boolean load) {
		return regions.getRegion(x, y, z, load);
	}

	@Override
	public Region getRegion(Point point, boolean load) {
		int x = (int) Math.floor(point.getX());
		int y = (int) Math.floor(point.getY());
		int z = (int) Math.floor(point.getZ());
		return regions.getRegionFromBlock(x, y, z, load);
	}

	@Override
	public Chunk getChunk(int x, int y, int z) {
		return getChunk(x, y, z, true);
	}

	@Override
	public Chunk getChunk(int x, int y, int z, boolean load) {
		Region region = getRegion(x >> Region.REGION_SIZE_BITS, y >> Region.REGION_SIZE_BITS, z >> Region.REGION_SIZE_BITS, load);
		if (region != null) {
			return region.getChunk(x & (Region.REGION_SIZE - 1), y & (Region.REGION_SIZE - 1), z & (Region.REGION_SIZE - 1), load);
		}
		return null;
	}
	
	@Override
	public Chunk getChunk(Point point) {
		return getChunk(point, true);
	}

	@Override
	public Chunk getChunk(Point point, boolean load) {
		int x = (int) Math.floor(point.getX());
		int y = (int) Math.floor(point.getY());
		int z = (int) Math.floor(point.getZ());
		return getChunk(x >> Chunk.CHUNK_SIZE_BITS, y >> Chunk.CHUNK_SIZE_BITS, z >> Chunk.CHUNK_SIZE_BITS, load);
	}

	@Override
	public int hashCode() {
		UUID uid = getUID();
		long hash = uid.getMostSignificantBits();
		hash += (hash << 5) + uid.getLeastSignificantBits();

		return (int) (hash ^ (hash >> 32));
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

	@Override
	public void spawnEntity(Entity e) {
		if (e.isSpawned()) throw new IllegalArgumentException("Cannot spawn an entity that is already spawned!");
		SpoutRegion region = (SpoutRegion) e.getRegion();
		region.allocate((SpoutEntity) e);
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
					}
					catch (Exception e){
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
					}
					catch (Exception e){
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
	
	public Chunk getChunkFromBlock(int x, int y, int z) {
		return getChunk(x >> Chunk.CHUNK_SIZE_BITS, y >> Chunk.CHUNK_SIZE_BITS, z >> Chunk.CHUNK_SIZE_BITS);
	}

	@Override
	public BlockMaterial setBlockMaterial(int x, int y, int z, BlockMaterial material) {
		return getChunkFromBlock(x, y, z).setBlockMaterial(x & 0xF, y & 0xF, z & 0xF, material);
	}

	@Override
	public short setBlockId(int x, int y, int z, short id) {
		return getChunkFromBlock(x, y, z).setBlockId(x & 0xF, y & 0xF, z & 0xF, id);
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockMaterial(x & 0xF, y & 0xF, z & 0xF);
	}

	@Override
	public short getBlockId(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockId(x & 0xF, y & 0xF, z & 0xF);
	}

	@Override
	public short getBlockData(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockData(x & 0xF, y & 0xF, z & 0xF);
	}

	@Override
	public short setBlockData(int x, int y, int z, short data) {
		return getChunkFromBlock(x, y, z).setBlockData(x & 0xF, y & 0xF, z & 0xF, data);
	}

	@Override
	public Transform getSpawnPoint() {
		return this.spawnLocation.get();
	}

	@Override
	public void setSpawnPoint(Transform transform) {
		this.spawnLocation.set(transform.copy());
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public void preSnapshotRun() throws InterruptedException {
		entityManager.preSnapshot();
	}

	@Override
	public WorldGenerator getGenerator() {
		return generator;
	}

}
