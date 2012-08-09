/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.collision.BoundingBox;
import org.spout.api.collision.CollisionModel;
import org.spout.api.collision.CollisionVolume;
import org.spout.api.datatable.DataMap;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.entity.controller.BlockController;
import org.spout.api.event.block.CuboidChangeEvent;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.generator.biome.Biome;
import org.spout.api.generator.biome.BiomeGenerator;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.bytearrayarray.BAAWrapper;
import org.spout.api.map.DefaultedMap;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.plugin.Plugin;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.util.StringMap;
import org.spout.api.util.cuboid.CuboidBuffer;
import org.spout.api.util.hashing.IntPairHashed;
import org.spout.api.util.hashing.NibblePairHashed;
import org.spout.api.util.map.concurrent.TSyncIntPairObjectHashMap;
import org.spout.api.util.map.concurrent.TSyncLongObjectHashMap;

import org.spout.engine.SpoutEngine;
import org.spout.engine.entity.EntityManager;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.filesystem.SharedFileSystem;
import org.spout.engine.filesystem.WorldFiles;
import org.spout.engine.scheduler.SpoutParallelTaskManager;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.scheduler.SpoutTaskManager;
import org.spout.engine.util.thread.AsyncExecutor;
import org.spout.engine.util.thread.ThreadAsyncExecutor;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableLong;

public final class SpoutWorld extends SpoutAbstractWorld implements World {
	private SnapshotManager snapshotManager = new SnapshotManager();
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
	private final Set<SpoutColumn> columnSet = new LinkedHashSet<SpoutColumn>();
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
	 * The sky light level the sky emits
	 */
	private byte skyLightLevel = 15;
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
	/**
	 * A WeakReference to this world
	 */
	private final WeakReference<World> selfReference;
	public static final WeakReference<World> NULL_WEAK_REFERENCE = new WeakReference<World>(null);

	// TODO set up number of stages ?
	public SpoutWorld(String name, SpoutEngine engine, long seed, long age, WorldGenerator generator, UUID uid, StringMap itemMap, DatatableMap extraData) {
		super(name, uid, engine, 1, new ThreadAsyncExecutor(toString(name, uid, age)));
		this.seed = seed;

		this.generator = generator;
		this.itemMap = itemMap;
		entityManager = new EntityManager();
		regions = new RegionSource(this, snapshotManager);

		worldDirectory = new File(SharedFileSystem.WORLDS_DIRECTORY, name);
		worldDirectory.mkdirs();

		heightMapBAAs = new TSyncIntPairObjectHashMap<BAAWrapper>();

		if (extraData != null) {
			this.datatableMap = extraData;
		} else {
			this.datatableMap = new GenericDatatableMap();
		}
		this.dataMap = new DataMap(this.datatableMap);

		this.hashcode = new HashCodeBuilder(27, 971).append(uid).toHashCode();

		this.lightingManager = new SpoutWorldLighting(this);
		this.lightingManager.start();

		parallelTaskManager = new SpoutParallelTaskManager(engine.getScheduler(), this);

		AsyncExecutor e = getExecutor();
		Thread t;
		if (e instanceof Thread) {
			t = (Thread) e;
		} else {
			throw new IllegalStateException("AsyncExecutor should be instance of Thread");
		}

		this.age = new SnapshotableLong(snapshotManager, age);
		taskManager = new SpoutTaskManager(getEngine().getScheduler(), false, t, age);
		spawnLocation.set(new Transform(new Point(this, 1, 100, 1), Quaternion.IDENTITY, Vector3.ONE));
		selfReference = new WeakReference<World>(this);
	}

	@Override
	public long getAge() {
		return age.get();
	}

	@Override
	public SpoutRegion getRegion(int x, int y, int z, LoadOption loadopt) {
		return regions.getRegion(x, y, z, loadopt);
	}

	@Override
	public SpoutChunk getChunk(int x, int y, int z, LoadOption loadopt) {
		SpoutRegion region = getRegionFromChunk(x, y, z, loadopt);
		if (region != null) {
			return region.getChunk(x, y, z, loadopt);
		}
		return null;
	}

	@Override
	public Biome getBiomeType(int x, int y, int z) {
		if (y < 0 || y > getHeight()) {
			return null;
		}
		if (!(generator instanceof BiomeGenerator)) {
			return null;
		}
		final SpoutChunk chunk = getChunkFromBlock(x, y, z, LoadOption.LOAD_ONLY);
		if (chunk == null) {
			return ((BiomeGenerator) generator).getBiome(x, y, z, seed);
		}
		return chunk.getBiomeType(x, y, z);
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
		return new SpoutEntity(getEngine(), point, controller);
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
						ent.tick(dt);
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
	public boolean containsBlock(int x, int y, int z) {
		return true;
	}

	@Override
	public boolean containsChunk(int x, int y, int z) {
		return true;
	}

	@Override
	public long getSeed() {
		return seed;
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
	public byte getSkyLight() {
		return this.skyLightLevel;
	}

	@Override
	public void setSkyLight(byte newLight) {
		this.skyLightLevel = newLight;
	}

	@Override
	public void updateBlockPhysics(int x, int y, int z, Source source) {
		this.getRegionFromBlock(x, y, z).updateBlockPhysics(x, y, z, source);
	}

	@Override
	public void queueBlockPhysics(int x, int y, int z, EffectRange range, Source source) {
		queueBlockPhysics(x, y, z, range, null, source);
	}

	public void queueBlockPhysics(int x, int y, int z, EffectRange range, BlockMaterial oldMaterial, Source source) {
		this.getRegionFromBlock(x, y, z).queueBlockPhysics(x, y, z, range, oldMaterial, source);
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
		synchronized (columnSet) {
			for (SpoutColumn c : columnSet) {
				c.onFinalize();
			}
		}
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
		final int minX = MathHelper.floor(model.getVolume().getPosition().getX());
		final int minY = MathHelper.floor(model.getVolume().getPosition().getY());
		final int minZ = MathHelper.floor(model.getVolume().getPosition().getZ());
		final int maxX = minX + 1;
		final int maxY = minY + 1;
		final int maxZ = minZ + 1;

		final LinkedList<CollisionVolume> colliding = new LinkedList<CollisionVolume>();

		final BoundingBox mutable = new BoundingBox(0, 0, 0, 0, 0, 0);

		for (int dx = minX; dx < maxX; dx++) {
			for (int dy = minY; dy < maxY; dy++) {
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

	/**
	 * Removes a column corresponding to the given Column coordinates
	 * @param x the x coordinate
	 * @param z the z coordinate
	 */
	public void removeColumn(int x, int z, SpoutColumn column) {
		long key = IntPairHashed.key(x, z);
		if (columns.remove(key, column)) {
			synchronized (columnSet) {
				columnSet.remove(column);
			}
		}
	}

	/**
	 * Gets the column corresponding to the given Block coordinates
	 * @param x the x block coordinate
	 * @param z the z block coordinate
	 * @param create true to create the column if it doesn't exist
	 * @return the column or null if it doesn't exist
	 */
	public SpoutColumn getColumn(int x, int z, boolean create) {
		int colX = x >> SpoutColumn.BLOCKS.BITS;
		int colZ = z >> SpoutColumn.BLOCKS.BITS;
		long key = IntPairHashed.key(colX, colZ);
		SpoutColumn column = columns.get(key);
		if (create && column == null) {
			SpoutColumn newColumn = new SpoutColumn(this, colX, colZ);
			column = columns.putIfAbsent(key, newColumn);
			if (column == null) {
				column = newColumn;
				synchronized (columnSet) {
					columnSet.add(column);
				}
			}
		}
		return column;
	}

	public SpoutColumn getColumn(int x, int z) {
		return getColumn(x, z, false);
	}

	private BAAWrapper getColumnHeightMapBAA(int x, int z) {
		int cx = x >> Region.CHUNKS.BITS;
		int cz = z >> Region.CHUNKS.BITS;

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
			for (Entity e : region.getAll()) {
				if (e.getUID().equals(uid)) {
					return e;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return toString(this.getName(), this.getUID(), this.getAge());
	}

	private static String toString(String name, UUID uid, long age) {
		return "SpoutWorld{ " + name + " UUID: " + uid + " Age: " + age + "}";
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
			((SpoutRegion) r).unload(save);
			progress++;
			if (save && progress % 4 == 0) {
				Spout.getLogger().info("Saving world [" + getName() + "], " + (int) (progress * 100F / total) + "% Complete");
			}
		}
	}

	public Collection<Region> getRegions() {
		return this.regions.getRegions();
	}

	@Override
	public boolean hasChunk(int x, int y, int z) {
		return this.getChunk(x, y, z, LoadOption.NO_LOAD) != null;
	}

	@Override
	public boolean hasChunkAtBlock(int x, int y, int z) {
		return this.getChunkFromBlock(x, y, z, LoadOption.NO_LOAD) != null;
	}

	@Override
	public void saveChunk(int x, int y, int z) {
		SpoutRegion r = this.getRegionFromChunk(x, y, z, LoadOption.NO_LOAD);
		if (r != null) {
			r.saveChunk(x, y, z);
		}
	}

	@Override
	public void unloadChunk(int x, int y, int z, boolean save) {
		SpoutRegion r = this.getRegionFromChunk(x, y, z, LoadOption.NO_LOAD);
		if (r != null) {
			r.unloadChunk(x, y, z, save);
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

	@Override
	public Serializable get(Object key) {
		return dataMap.get(key);
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

	@Override
	public boolean setCuboid(CuboidBuffer buffer, Plugin plugin) {
		//TODO: this seems to not work correctly
		if (plugin == null) {
			throw new NullPointerException("Plugin can not be null");
		}
		CuboidChangeEvent event = new CuboidChangeEvent(buffer, plugin);
		Spout.getEngine().getEventManager().callEvent(event);
		if (event.isCancelled()) {
			return false;
		}

		Chunk start = getChunkFromBlock(buffer.getBase());
		Chunk end = getChunkFromBlock(buffer.getBase().add(buffer.getSize()));
		for (int dx = start.getX(); dx < end.getX(); dx++) {
			for (int dy = start.getY(); dy < end.getY(); dy++) {
				for (int dz = start.getZ(); dz < end.getZ(); dz++) {
					Chunk chunk = getChunk(dx, dy, dz);
					((SpoutChunk) chunk).setCuboid(buffer);
				}
			}
		}
		return true;
	}

	// Worlds don't do any of these

	@Override
	public void runPhysics(int sequence) throws InterruptedException {
	}

	@Override
	public long getFirstDynamicUpdateTime() {
		return SpoutScheduler.END_OF_THE_WORLD;
	}

	@Override
	public void runDynamicUpdates(long time, int sequence) throws InterruptedException {
	}

	public WeakReference<World> getWeakReference() {
		return selfReference;
	}
}
