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
package org.spout.engine.world;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spout.api.Engine;
import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.collision.BoundingBox;
import org.spout.api.collision.CollisionModel;
import org.spout.api.collision.CollisionVolume;
import org.spout.api.datatable.DataMap;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.entity.BlockController;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.generator.biome.Biome;
import org.spout.api.geo.LoadGenerateOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.bytearrayarray.BAAWrapper;
import org.spout.api.map.DefaultedMap;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;
import org.spout.api.player.Player;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.util.StringMap;
import org.spout.api.util.hashing.IntPairHashed;
import org.spout.api.util.hashing.NibblePairHashed;
import org.spout.api.util.map.concurrent.TSyncIntPairObjectHashMap;
import org.spout.api.util.map.concurrent.TSyncLongObjectHashMap;
import org.spout.api.util.sanitation.StringSanitizer;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.Threadsafe;
import org.spout.engine.SpoutEngine;
import org.spout.engine.entity.EntityManager;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.filesystem.FileSystem;
import org.spout.engine.filesystem.WorldFiles;
import org.spout.engine.scheduler.SpoutParallelTaskManager;
import org.spout.engine.scheduler.SpoutTaskManager;
import org.spout.engine.util.thread.AsyncExecutor;
import org.spout.engine.util.thread.AsyncManager;
import org.spout.engine.util.thread.ThreadAsyncExecutor;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableLong;

public final class SpoutWorld extends AsyncManager implements World {
	private SnapshotManager snapshotManager = new SnapshotManager();
	/**
	 * The server of this world.
	 */
	private final Engine server;
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
	private final Transform spawnLocation = new Transform();
	/**
	 * The current world age.
	 */
	private SnapshotableLong age;
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
	/**
	 * A map of the loaded columns
	 */
	private final TSyncLongObjectHashMap<SpoutColumn> columns = new TSyncLongObjectHashMap<SpoutColumn>();
	/**
	 * A map of column height map files
	 */
	private final TSyncIntPairObjectHashMap<BAAWrapper> heightMapBAAs;
	/**
	 * The directory where the world data is stored
	 */
	private final File worldDirectory;
	/**
	 * The async thread which handles the calculation of block and sky lighting in the world
	 */
	private final SpoutWorldLighting lightingManager;

	/**
	 * The parallel task manager.  This is used for submitting tasks to all regions in the world.
	 */
	protected final SpoutParallelTaskManager parallelTaskManager;
	
	private final SpoutTaskManager taskManager;
	
	/**
	 * Hashcode cache
	 */
	private final int hashcode;
	
	/**
	 * Data map and Datatable associated with it
	 */
	private final DatatableMap datatableMap;
	private final DataMap dataMap;
	
	/**
	 * String item map, used to convert local id's to the server id
	 */
	private final StringMap itemMap;

	// TODO set up number of stages ?
	public SpoutWorld(String name, Engine server, long seed, long age, WorldGenerator generator, UUID uid, StringMap itemMap, DatatableMap extraData) {
		super(1, new ThreadAsyncExecutor(), server);
		this.uid = uid;
		this.server = server;
		this.seed = seed;
		if (!StringSanitizer.isAlphaNumericUnderscore(name)) {
			name = Long.toHexString(System.currentTimeMillis());
			Spout.getEngine().getLogger().severe("World name " + name + " is not valid, using " + name + " instead");
		}
		this.name = name;
		this.generator = generator;
		this.itemMap = itemMap;
		entityManager = new EntityManager();
		regions = new RegionSource(this, snapshotManager);

		worldDirectory = new File(FileSystem.WORLDS_DIRECTORY, name);
		worldDirectory.mkdirs();

		heightMapBAAs = new TSyncIntPairObjectHashMap<BAAWrapper>();
		
		if (extraData != null) {
			this.datatableMap = extraData;
		} else {
			this.datatableMap = new GenericDatatableMap();;
		}
		this.dataMap = new DataMap(this.datatableMap);

		this.hashcode = new HashCodeBuilder(27, 971).append(uid).toHashCode();

		this.lightingManager = new SpoutWorldLighting(this);
		this.lightingManager.start();

		parallelTaskManager = new SpoutParallelTaskManager(server.getScheduler(), this);
		
		AsyncExecutor e = getExecutor();
		Thread t = null;
		if (e instanceof Thread) {
			t = (Thread)e;
		} else {
			throw new IllegalStateException("AsyncExecutor should be instance of Thread");
		}

		this.age = new SnapshotableLong(snapshotManager, age);
		taskManager = new SpoutTaskManager(getEngine().getScheduler(), false, t, age);
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
	public SpoutBlock getBlock(int x, int y, int z) {
		return this.getBlock(x, y, z, null);
	}

	@Override
	public SpoutBlock getBlock(int x, int y, int z, Source source) {
		return new SpoutBlock(this, x, y, z, source);
	}

	@Override
	public SpoutBlock getBlock(float x, float y, float z) {
		return this.getBlock(x, y, z, this);
	}

	@Override
	public SpoutBlock getBlock(float x, float y, float z, Source source) {
		return this.getBlock(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z), source);
	}

	@Override
	public SpoutBlock getBlock(Vector3 position) {
		return this.getBlock(position, this);
	}

	@Override
	public SpoutBlock getBlock(Vector3 position, Source source) {
		return this.getBlock(position.getX(), position.getY(), position.getZ(), source);
	}

	@Override
	public UUID getUID() {
		return uid;
	}

	@Override
	public SpoutRegion getRegion(int x, int y, int z) {
		return regions.getRegion(x, y, z);
	}

	@Override
	public SpoutRegion getRegion(int x, int y, int z, boolean load) {
		return regions.getRegion(x, y, z, load ? LoadGenerateOption.LOAD_OR_GENERATE_IF_NEEDED : LoadGenerateOption.NO_LOAD);
	}

	@Override
	public SpoutRegion getRegion(int x, int y, int z, LoadGenerateOption loadopt) {
		return regions.getRegion(x, y, z, loadopt);
	}

	@Override
	public SpoutRegion getRegionFromBlock(int x, int y, int z) {
		return this.regions.getRegionFromBlock(x, y, z);
	}

	@Override
	public SpoutRegion getRegionFromBlock(int x, int y, int z, boolean load) {
		return this.regions.getRegionFromBlock(x, y, z, load ? LoadGenerateOption.LOAD_OR_GENERATE_IF_NEEDED : LoadGenerateOption.NO_LOAD);
	}

	@Override
	public SpoutRegion getRegionFromBlock(int x, int y, int z, LoadGenerateOption loadopt) {
		return this.regions.getRegionFromBlock(x, y, z, loadopt);
	}

	@Override
	public SpoutRegion getRegionFromBlock(Vector3 position) {
		int x = MathHelper.floor(position.getX());
		int y = MathHelper.floor(position.getY());
		int z = MathHelper.floor(position.getZ());
		return regions.getRegionFromBlock(x, y, z);
	}

	@Override
	public SpoutRegion getRegionFromBlock(Vector3 position, boolean load) {
		int x = MathHelper.floor(position.getX());
		int y = MathHelper.floor(position.getY());
		int z = MathHelper.floor(position.getZ());
		return regions.getRegionFromBlock(x, y, z, load ? LoadGenerateOption.LOAD_OR_GENERATE_IF_NEEDED : LoadGenerateOption.NO_LOAD);
	}

	@Override
	public SpoutRegion getRegionFromBlock(Vector3 position, LoadGenerateOption loadopt) {
		int x = MathHelper.floor(position.getX());
		int y = MathHelper.floor(position.getY());
		int z = MathHelper.floor(position.getZ());
		return regions.getRegionFromBlock(x, y, z, loadopt);
	}

	@Override
	public SpoutChunk getChunk(int x, int y, int z) {
		return this.getChunk(x, y, z, true);
	}

	@Override
	public SpoutChunk getChunk(int x, int y, int z, boolean load) {
		return this.getChunk(x, y, z, load ? LoadGenerateOption.LOAD_OR_GENERATE_IF_NEEDED : LoadGenerateOption.NO_LOAD);
	}

	@Override
	public SpoutChunk getChunk(int x, int y, int z, LoadGenerateOption loadopt) {
		SpoutRegion region = getRegion(x >> Region.REGION_SIZE_BITS, y >> Region.REGION_SIZE_BITS, z >> Region.REGION_SIZE_BITS, loadopt);
		if (region != null) {
			return region.getChunk(x & Region.REGION_SIZE - 1, y & Region.REGION_SIZE - 1, z & Region.REGION_SIZE - 1, loadopt);
		}
		return null;
	}

	@Override
	public SpoutChunk getChunkFromBlock(int x, int y, int z) {
		return this.getChunkFromBlock(x, y, z, true);
	}

	@Override
	public SpoutChunk getChunkFromBlock(int x, int y, int z, boolean load) {
		return this.getChunk(x >> Chunk.CHUNK_SIZE_BITS, y >> Chunk.CHUNK_SIZE_BITS, z >> Chunk.CHUNK_SIZE_BITS, load);
	}

	@Override
	public SpoutChunk getChunkFromBlock(int x, int y, int z, LoadGenerateOption loadopt) {
		return this.getChunk(x >> Chunk.CHUNK_SIZE_BITS, y >> Chunk.CHUNK_SIZE_BITS, z >> Chunk.CHUNK_SIZE_BITS, loadopt);
	}

	@Override
	public SpoutChunk getChunkFromBlock(Vector3 position) {
		return this.getChunkFromBlock(position, true);
	}

	@Override
	public SpoutChunk getChunkFromBlock(Vector3 position, boolean load) {
		int x = MathHelper.floor(position.getX());
		int y = MathHelper.floor(position.getY());
		int z = MathHelper.floor(position.getZ());
		return this.getChunkFromBlock(x, y, z, load);
	}

	@Override
	public SpoutChunk getChunkFromBlock(Vector3 position, LoadGenerateOption loadopt) {
		int x = MathHelper.floor(position.getX());
		int y = MathHelper.floor(position.getY());
		int z = MathHelper.floor(position.getZ());
		return this.getChunkFromBlock(x, y, z, loadopt);
	}
	
	@Override
	public Biome getBiomeType(int x, int y, int z) {
		if (y < 0 || y > getHeight()) {
			return null;
		}
		return getChunkFromBlock(x, y, z).getBiomeType(x, y, z);
	}

	@Override
	public int hashCode() {
		return hashcode;
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
		return new SpoutEntity((SpoutEngine) server, point, controller);
	}

	/**
	 * Spawns an entity into the world. Fires off a cancellable EntitySpawnEvent
	 */
	@Override
	public void spawnEntity(Entity e) {
		if (e.isSpawned()) {
			throw new IllegalArgumentException("Cannot spawn an entity that is already spawned!");
		}
		((SpoutRegion) e.getRegion()).addEntity(e);
	}

	@Override
	public Entity createAndSpawnEntity(Point point, Controller controller) {
		Entity e = createEntity(point, controller);
		//initialize region if needed
		this.getRegionFromBlock(point, true);
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
		if (stage == 0) {
			age.set(age.get() + delta);
		}
		switch (stage) {
			case 0: {
				parallelTaskManager.heartbeat(delta);
				taskManager.heartbeat(delta);
				float dt = delta / 1000.f;
				//Update all entities
				for (SpoutEntity ent : entityManager) {
					try {
						ent.onTick(dt);
					} catch (Exception e) {
						Spout.getEngine().getLogger().severe("Unhandled exception during tick for " + ent.toString());
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
	public Engine getGame() {
		return server;
	}

	/**
	 * Gets the lighting manager that calculates the light for this world
	 * @return world lighting manager
	 */
	public SpoutWorldLighting getLightingManager() {
		return this.lightingManager;
	}

	@Override
	public int getHeight() {
		// TODO: Variable world height
		return 256;
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Source source) {
		return this.getChunkFromBlock(x, y, z).setBlockMaterial(x, y, z, material, data, source);
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
	public short getBlockData(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockData(x, y, z);
	}

	@Override
	public byte getBlockSkyLight(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockSkyLight(x, y, z);
	}

	@Override
	public byte getBlockLight(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockLight(x, y, z);
	}

	@Override
	public boolean compareAndSetData(int x, int y, int z, BlockFullState expect, short data) {
		return getChunkFromBlock(x, y, z).compareAndSetData(x, y, z, expect, data);
	}

	@Override
	public void updateBlockPhysics(int x, int y, int z, Source source) {
		regions.getRegionFromBlock(x, y, z).updateBlockPhysics(x, y, z, source);
	}

	@Override
	public Transform getSpawnPoint() {
		return spawnLocation.copy();
	}

	@Override
	public void setSpawnPoint(Transform transform) {
		spawnLocation.set(transform);
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
		HashSet<Entity> entities = new HashSet<Entity>(entityManager.getAll());
		for (Region region : regions) {
			entities.addAll(region.getAll());
		}
		return entities;
	}

	@Override
	public HashSet<Entity> getAll(Class<? extends Controller> type) {
		HashSet<Entity> entities = new HashSet<Entity>(entityManager.getAll(type));
		for (Region region : regions) {
			entities.addAll(region.getAll(type));
		}
		return entities;
	}

	@Override
	public Entity getEntity(int id) {
		Entity entity = entityManager.getEntity(id);
		if (entity == null) {
			for (Region region : regions) {
				if ((entity = region.getEntity(id)) != null) {
					break;
				}
			}
		}
		return entity;
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

	public List<CollisionVolume> getCollidingObject(CollisionModel model) {
		//TODO Make this more general
		final int minX = MathHelper.floor(model.getPosition().getX());
		final int minY = MathHelper.floor(model.getPosition().getY());
		final int minZ = MathHelper.floor(model.getPosition().getZ());
		final int maxX = minX + 1;
		final int maxY = minY + 1;
		final int maxZ = minZ + 1;

		final LinkedList<CollisionVolume> colliding = new LinkedList<CollisionVolume>();

		final BoundingBox mutable = new BoundingBox(0, 0, 0, 0, 0, 0);

		for (int dx = minX; dx < maxX; dx++) {
			for (int dy = minY - 1; dy < maxY; dy++) {
				for (int dz = minZ; dz < maxZ; dz++) {
					BlockMaterial material = this.getBlockMaterial(dx, dy, dz);
					mutable.set((BoundingBox) material.getBoundingArea());
					BoundingBox box = mutable.offset(dx, dy, dz);
					if (box.intersects(model.getVolume())) {
						colliding.add(mutable.clone());
					}
				}
			}
		}

		//TODO: colliding entities
		return colliding;
	}
	
	@Override
	public int getSurfaceHeight(int x, int z, boolean load) {
		SpoutColumn column = getColumn(x, z, load);
		if (column == null) {
			return Integer.MIN_VALUE;
		} else {
			return column.getSurfaceHeight(x, z);
		}
	}

	@Override
	public int getSurfaceHeight(int x, int z) {
		return getSurfaceHeight(x, z, false);
	}

	/**
	 * Removes a column corresponding to the given Column coordinates
	 * 
	 * @param x the x coordinate
	 * @param z the z coordinate
	 */
	public void removeColumn(int x, int z, SpoutColumn column) {
		long key = IntPairHashed.key(x, z);
		columns.remove(key, column);
	}

	/**
	 * Gets the column corresponding to the given Block coordinates
	 * 
	 * @param x the x block coordinate
	 * @param z the z block coordinate
	 * @param create true to create the column if it doesn't exist
	 * @return the column or null if it doesn't exist
	 */
	public SpoutColumn getColumn(int x, int z, boolean create) {
		int colX = x >> SpoutColumn.COLUMN_SIZE_BITS;
		int colZ = z >> SpoutColumn.COLUMN_SIZE_BITS;
		long key = IntPairHashed.key(colX, colZ);
		SpoutColumn column = columns.get(key);
		if (create && column == null) {
			SpoutColumn newColumn = new SpoutColumn(this, colX, colZ);
			column = columns.putIfAbsent(key, newColumn);
			if (column == null) {
				column = newColumn;
			}
		}
		return column;
	}

	public SpoutColumn getColumn(int x, int z) {
		return getColumn(x, z, false);
	}

	private BAAWrapper getColumnHeightMapBAA(int x, int z) {
		int cx = x >> Region.REGION_SIZE_BITS;
		int cz = z >> Region.REGION_SIZE_BITS;

		BAAWrapper baa = null;

		baa = heightMapBAAs.get(cx, cz);

		if (baa == null) {
			File columnDirectory = new File(worldDirectory, "col");
			columnDirectory.mkdirs();
			File file = new File(columnDirectory, "col" + cx + "_" + cz + ".scl");
			baa = new BAAWrapper(file, 1024, 256, SpoutRegion.TIMEOUT);
			BAAWrapper oldBAA = heightMapBAAs.putIfAbsent(cx, cz, baa);
			if (oldBAA != null) {
				baa = oldBAA;
			}
		}

		return baa;
	}

	public InputStream getHeightMapInputStream(int x, int z) {

		BAAWrapper baa = getColumnHeightMapBAA(x, z);

		int key = NibblePairHashed.key(x, z) & 0xFF;

		return baa.getBlockInputStream(key);
	}

	public OutputStream getHeightMapOutputStream(int x, int z) {

		BAAWrapper baa = getColumnHeightMapBAA(x, z);

		int key = NibblePairHashed.key(x, z) & 0xFF;

		return baa.getBlockOutputStream(key);
	}
	
	@Override
	public Entity getEntity(UUID uid) {
		for (Region region : regions) {
			for (Entity e :region.getAll()) {
				if (e.getUID().equals(uid)) {
					return e;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "SpoutWorld{ " + getName() + " UUID: " + this.uid + " Age: " + this.getAge() + "}";
	}

	@Override
	public File getDirectory() {
		return worldDirectory;
	}

	public void unload(boolean save) {
		this.getLightingManager().abort();
		if (save) {
			WorldFiles.saveWorldData(this);
		}
		Collection<Region> regions = this.regions.getRegions();
		final int total = Math.max(1, regions.size());
		int progress = 0;
		for (Region r : regions) {
			r.unload(save);
			progress++;
			if (save && progress % 4 == 0) {
				Spout.getLogger().info("Saving world [" + getName() + "], " + (int)(progress * 100F / total) + "% Complete");
			}
		}
	}

	public Collection<Region> getRegions() {
		return this.regions.getRegions();
	}

	@Override
	public boolean hasChunk(int x, int y, int z) {
		return this.getChunk(x, y, z, false) != null;
	}

	@Override
	public boolean hasChunkAtBlock(int x, int y, int z) {
		return this.getChunkFromBlock(x, y, z, false) != null;
	}

	@Override
	public void saveChunk(int x, int y, int z) {
		SpoutRegion r = this.getRegion(x >> Region.REGION_SIZE_BITS, y >> Region.REGION_SIZE_BITS, z >> Region.REGION_SIZE_BITS, false);
		if (r != null) {
			r.saveChunk(x & Region.REGION_SIZE - 1, y & Region.REGION_SIZE - 1, z & Region.REGION_SIZE - 1);
		}
	}

	@Override
	public void unloadChunk(int x, int y, int z, boolean save) {
		SpoutRegion r = this.getRegion(x >> Region.REGION_SIZE_BITS, y >> Region.REGION_SIZE_BITS, z >> Region.REGION_SIZE_BITS, false);
		if (r != null) {
			r.unloadChunk(x & Region.REGION_SIZE - 1, y & Region.REGION_SIZE - 1, z & Region.REGION_SIZE - 1, save);
		}
	}

	@Override
	public int getNumLoadedChunks() {
		int total = 0;
		for (Region region : this.regions) {
			total += region.getNumLoadedChunks();
		}
		return total;
	}

	@Override
	public boolean setBlockLight(int x, int y, int z, byte light, Source source) {
		return this.getChunkFromBlock(x, y, z).setBlockLight(x, y, z, light, source);
	}

	@Override
	public boolean setBlockSkyLight(int x, int y, int z, byte light, Source source) {
		return this.getChunkFromBlock(x, y, z).setBlockSkyLight(x, y, z, light, source);
	}

	@Override
	public void setBlockController(int x, int y, int z, BlockController controller) {
		getRegionFromBlock(x, y, z).setBlockController(x, y, z, controller);
	}

	@Override
	public BlockController getBlockController(int x, int y, int z) {
		return this.getRegionFromBlock(x, y, z).getBlockController(x, y, z);
	}

	@Override
	public DefaultedMap<String, Serializable> getDataMap() {
		return dataMap;
	}
	
	public StringMap getItemMap() {
		return itemMap;
	}

	@Override
	public TaskManager getParallelTaskManager() {
		return parallelTaskManager;
	}

	@Override
	public TaskManager getTaskManager() {
		return taskManager;
	}

	/**
	 * Gets a set of nearby players to the point, inside of the range
	 * 
	 * @param position of the center
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public Set<Player> getNearbyPlayers(Point position, int range) {
		return getNearbyPlayers(position, null, range);
	}

	/**
	 * Gets a set of nearby players to the entity, inside of the range
	 * 
	 * @param entity marking the center and which is ignored
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public Set<Player> getNearbyPlayers(Entity entity, int range) {
		return getNearbyPlayers(entity.getPosition(), entity, range);
	}

	/**
	 * Gets a set of nearby players to the point, inside of the range.
	 * The search will ignore the specified entity.
	 * 
	 * @param position of the center
	 * @param ignore Entity to ignore
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public Set<Player> getNearbyPlayers(Point position, Entity ignore, int range) {
		Set<Player> foundPlayers = new HashSet<Player>();
		final int RANGE_SQUARED = range * range;
		
		for (Player plr : getPlayersNearRegion(position, range)) {
			if (plr.getEntity() != ignore && plr.getEntity() != null) {
				double distance = MathHelper.distanceSquared(position, plr.getEntity().getPosition());
				if (distance < RANGE_SQUARED) {
					foundPlayers.add(plr);
				}
			}
		}

		return foundPlayers;
	}

	/**
	 * Finds all the players inside of the regions inside the range area
	 * 
	 * @param position to search from
	 * @param range to search for regions
	 * @return nearby region's players
	 */
	private Set<Player> getPlayersNearRegion(Point position, int range) {
		final int REGION_SIZE = Region.REGION_SIZE * Chunk.CHUNK_SIZE;
		Region center = this.getRegionFromBlock(position);

		if (center != null) {
			HashSet<Player> players = new HashSet<Player>();
			final int regions = (range + REGION_SIZE - 1) / REGION_SIZE; //round up 1 region size
			for (int dx = -regions; dx < regions; dx++) {
				for (int dy = -regions; dy < regions; dy++) {
					for (int dz = -regions; dz < regions; dz++) {
						Region region = this.getRegion(center.getX() + dx, center.getY() + dy, center.getZ() + dz, false);
						if (region != null) {
							players.addAll(region.getPlayers());
						}
					}
				}
			}
		}
		return players;
	}

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * @param position to search from
	 * @param entity to ignore while searching
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Point position, Entity ignore, int range) {
		Player best = null;
		double bestDistance = range * range;

		for (Player plr : getPlayersNearRegion(position, range)) {
			if (plr.getEntity() != ignore && plr.getEntity() != null) {
				double distance = MathHelper.distanceSquared(position, plr.getEntity().getPosition());
				if (distance < bestDistance) {
					bestDistance = distance;
					best = plr;
				}
			}
		}
		return best;
	}

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * 
	 * @param entity to search from
	 * @param entity to ignore while searching
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Point position, int range) {
		return getNearestPlayer(position, null, range);
	}

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * 
	 * @param entity to search from
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Entity entity, int range) {
		return getNearestPlayer(entity.getPosition(), entity, range);
	}
}
