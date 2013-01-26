/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.engine.world;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spout.api.Spout;
import org.spout.api.collision.BoundingBox;
import org.spout.api.collision.CollisionModel;
import org.spout.api.collision.CollisionVolume;
import org.spout.api.component.Component;
import org.spout.api.component.ComponentHolder;
import org.spout.api.component.WorldComponentHolder;
import org.spout.api.component.type.BlockComponent;
import org.spout.api.component.type.EntityComponent;
import org.spout.api.entity.Entity;
import org.spout.api.entity.EntityPrefab;
import org.spout.api.entity.Player;
import org.spout.api.entity.spawn.SpawnArrangement;
import org.spout.api.event.Cause;
import org.spout.api.event.block.CuboidChangeEvent;
import org.spout.api.event.entity.EntitySpawnEvent;
import org.spout.api.event.world.WorldSaveEvent;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.generator.biome.Biome;
import org.spout.api.generator.biome.BiomeGenerator;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.bytearrayarray.BAAWrapper;
import org.spout.api.lighting.LightingManager;
import org.spout.api.map.DefaultedMap;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.GenericMath;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.math.VectorMath;
import org.spout.api.model.Model;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.util.StringMap;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;
import org.spout.api.util.cuboid.CuboidLightBuffer;
import org.spout.api.util.hashing.IntPairHashed;
import org.spout.api.util.hashing.NibblePairHashed;
import org.spout.api.util.list.concurrent.ConcurrentList;
import org.spout.api.util.list.concurrent.UnprotectedCopyOnUpdateArray;
import org.spout.api.util.map.concurrent.TSyncIntPairObjectHashMap;
import org.spout.api.util.map.concurrent.TSyncLongObjectHashMap;
import org.spout.api.util.sanitation.StringSanitizer;
import org.spout.api.util.thread.annotation.LiveRead;
import org.spout.api.util.thread.annotation.Threadsafe;
import org.spout.engine.SpoutEngine;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.filesystem.versioned.WorldFiles;
import org.spout.engine.scheduler.SpoutParallelTaskManager;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.scheduler.SpoutTaskManager;
import org.spout.engine.util.thread.AsyncManager;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableLong;

public class SpoutWorld implements AsyncManager, World {
	private SnapshotManager snapshotManager = new SnapshotManager();
	/**
	 * The server of this world.
	 */
	private final SpoutEngine engine;
	/**
	 * The name of this world.
	 */
	private final String name;
	/**
	 * The world's UUID.
	 */
	private final UUID uid;
	/**
	 * String item map, used to convert local id's to the server id
	 */
	private final StringMap itemMap;
	/**
	 * String lighting map, used to covert local id's to the server id
	 */
	private final StringMap lightingMap;
	/**
	 * Lighting managers
	 */
	private final UnprotectedCopyOnUpdateArray<LightingManager<?>> lightingManagers;
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
	 * A set of all players currently connected to this world
	 */
	private final List<Player> players = new ConcurrentList<Player>();
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
	 * The execution thread for this world
	 */
	private Thread executionThread;
	/**
	 * Indicates if the snapshot queue for the renderer should be populated
	 */
	private final AtomicBoolean renderQueueEnabled = new AtomicBoolean(false);
	/**
	 * RegionFile manager for the world
	 */
	private final RegionFileManager regionFileManager;
	/*
	 * A WeakReference to this world
	 */
	private final WeakReference<World> selfReference;
	public static final WeakReference<World> NULL_WEAK_REFERENCE = new WeakReference<World>(null);
	Model skydome;
	/*
	 * Components
	 */
	private final WorldComponentHolder componentHolder;

	// TODO set up number of stages ?
	public SpoutWorld(String name, SpoutEngine engine, long seed, long age, WorldGenerator generator, UUID uid, StringMap itemMap, StringMap lightingMap) {
		this.engine = engine;
		if (!StringSanitizer.isAlphaNumericUnderscore(name)) {
			name = Long.toHexString(System.currentTimeMillis());
			Spout.getEngine().getLogger().severe("World name " + name + " is not valid, using " + name + " instead");
		}
		this.name = name;
		this.uid = uid;
		this.itemMap = itemMap;
		this.lightingMap = lightingMap;
		this.seed = seed;

		this.generator = generator;
		regions = new RegionSource(this, snapshotManager);

		worldDirectory = new File(engine.getWorldFolder(), name);
		worldDirectory.mkdirs();

		regionFileManager = new RegionFileManager(worldDirectory);

		heightMapBAAs = new TSyncIntPairObjectHashMap<BAAWrapper>();

		this.hashcode = new HashCodeBuilder(27, 971).append(uid).toHashCode();

		this.lightingManager = new SpoutWorldLighting(this);
		this.lightingManager.start();

		parallelTaskManager = new SpoutParallelTaskManager(engine.getScheduler(), this);

		lightingManagers = new UnprotectedCopyOnUpdateArray<LightingManager<?>>(LightingManager.class, true);

		this.age = new SnapshotableLong(snapshotManager, age);
		taskManager = new SpoutTaskManager(getEngine().getScheduler(), null, this, age);
		spawnLocation.set(new Transform(new Point(this, 1, 100, 1), Quaternion.IDENTITY, Vector3.ONE));
		selfReference = new WeakReference<World>(this);
		componentHolder = new WorldComponentHolder(this);
		
		((SpoutScheduler) Spout.getEngine().getScheduler()).addAsyncManager(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public UUID getUID() {
		return uid;
	}

	@Override
	public long getAge() {
		return age.get();
	}

	@Override
	public SpoutBlock getBlock(int x, int y, int z) {
		return new SpoutBlock(this, x, y, z);
	}

	@Override
	public SpoutBlock getBlock(float x, float y, float z) {
		return this.getBlock(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
	}

	@Override
	public SpoutBlock getBlock(Vector3 position) {
		return this.getBlock(position.getX(), position.getY(), position.getZ());
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
		} else if (loadopt.loadIfNeeded() && loadopt.generateIfNeeded()) {
			Spout.getLogger().info("Warning unable to load region: " + x + ", " + y + ", " + z + ":" + loadopt);
		}
		return null;
	}

	@Override
	public Biome getBiome(int x, int y, int z) {
		if (y < 0 || y > getHeight()) {
			return null;
		}
		if (!(generator instanceof BiomeGenerator)) {
			return null;
		}
		final SpoutColumn column = getColumn(x, z, true);
		final BiomeManager manager = column.getBiomeManager();
		if (manager != null) {
			final Biome biome = column.getBiomeManager().getBiome(x & SpoutColumn.BLOCKS.MASK, y & SpoutColumn.BLOCKS.MASK, z & SpoutColumn.BLOCKS.MASK);
			if (biome != null) {
				return biome;
			}
		}
		return ((BiomeGenerator) generator).getBiome(x, y, z, seed);
	}

	@Override
	public BiomeManager getBiomeManager(int x, int z) {
		return getBiomeManager(x, z, false);
	}

	@Override
	public BiomeManager getBiomeManager(int x, int z, boolean load) {
		final SpoutColumn column = getColumn(x, z, load);
		if (column != null) {
			return column.getBiomeManager();
		}
		return null;
	}

	@Override
	public SpoutRegion getRegion(int x, int y, int z) {
		return getRegion(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutRegion getRegionFromChunk(int x, int y, int z) {
		return getRegionFromChunk(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutRegion getRegionFromChunk(int x, int y, int z, LoadOption loadopt) {
		return getRegion(x >> Region.CHUNKS.BITS, y >> Region.CHUNKS.BITS, z >> Region.CHUNKS.BITS, loadopt);
	}

	@Override
	public SpoutRegion getRegionFromBlock(Vector3 position) {
		return getRegionFromBlock(position, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutRegion getRegionFromBlock(Vector3 position, LoadOption loadopt) {
		return this.getRegionFromBlock(position.getFloorX(), position.getFloorY(), position.getFloorZ(), loadopt);
	}

	@Override
	public SpoutRegion getRegionFromBlock(int x, int y, int z) {
		return getRegionFromBlock(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutRegion getRegionFromBlock(int x, int y, int z, LoadOption loadopt) {
		return getRegion(x >> Region.BLOCKS.BITS, y >> Region.BLOCKS.BITS, z >> Region.BLOCKS.BITS, loadopt);
	}

	@Override
	public SpoutChunk getChunk(int x, int y, int z) {
		return this.getChunk(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutChunk getChunkFromBlock(int x, int y, int z) {
		return this.getChunkFromBlock(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutChunk getChunkFromBlock(int x, int y, int z, LoadOption loadopt) {
		return this.getChunk(x >> Chunk.BLOCKS.BITS, y >> Chunk.BLOCKS.BITS, z >> Chunk.BLOCKS.BITS, loadopt);
	}

	@Override
	public SpoutChunk getChunkFromBlock(Vector3 position) {
		return this.getChunkFromBlock(position, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutChunk getChunkFromBlock(Vector3 position, LoadOption loadopt) {
		return this.getChunkFromBlock(position.getFloorX(), position.getFloorY(), position.getFloorZ(), loadopt);
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Cause<?> cause) {
		return this.getChunkFromBlock(x, y, z).setBlockMaterial(x, y, z, material, data, cause);
	}

	@Override
	public boolean setBlockData(int x, int y, int z, short data, Cause<?> cause) {
		return getChunkFromBlock(x, y, z).setBlockData(x, y, z, data, cause);
	}

	@Override
	public boolean addBlockData(int x, int y, int z, short data, Cause<?> cause) {
		return getChunkFromBlock(x, y, z).addBlockData(x, y, z, data, cause);
	}

	@Override
	public int getBlockFullState(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockFullState(x, y, z);
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
	public byte getBlockSkyLightRaw(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockSkyLightRaw(x, y, z);
	}

	@Override
	public byte getBlockLight(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockLight(x, y, z);
	}

	@Override
	public boolean compareAndSetData(int x, int y, int z, int expect, short data, Cause<?> cause) {
		return getChunkFromBlock(x, y, z).compareAndSetData(x, y, z, expect, data, cause);
	}

	@Override
	public short setBlockDataBits(int x, int y, int z, int bits, boolean set, Cause<?> cause) {
		return getChunkFromBlock(x, y, z).setBlockDataBits(x, y, z, bits, set, cause);
	}

	@Override
	public short setBlockDataBits(int x, int y, int z, int bits, Cause<?> cause) {
		return getChunkFromBlock(x, y, z).setBlockDataBits(x, y, z, bits, cause);
	}

	@Override
	public short clearBlockDataBits(int x, int y, int z, int bits, Cause<?> cause) {
		return getChunkFromBlock(x, y, z).clearBlockDataBits(x, y, z, bits, cause);
	}

	@Override
	public int getBlockDataField(int x, int y, int z, int bits) {
		return getChunkFromBlock(x, y, z).getBlockDataField(x, y, z, bits);
	}

	@Override
	public boolean isBlockDataBitSet(int x, int y, int z, int bits) {
		return getChunkFromBlock(x, y, z).isBlockDataBitSet(x, y, z, bits);
	}

	@Override
	public int setBlockDataField(int x, int y, int z, int bits, int value, Cause<?> cause) {
		return getChunkFromBlock(x, y, z).setBlockDataField(x, y, z, bits, value, cause);
	}

	@Override
	public int addBlockDataField(int x, int y, int z, int bits, int value, Cause<?> cause) {
		return getChunkFromBlock(x, y, z).addBlockDataField(x, y, z, bits, value, cause);
	}

	@Override
	public void resetDynamicBlock(int x, int y, int z) {
		this.getRegionFromBlock(x, y, z).resetDynamicBlock(x, y, z);
	}

	@Override
	public void syncResetDynamicBlock(int x, int y, int z) {
		this.getRegionFromBlock(x, y, z).syncResetDynamicBlock(x, y, z);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, int data, boolean exclusive) {
		return this.getRegionFromBlock(x, y, z).queueDynamicUpdate(x, y, z, nextUpdate, data, exclusive);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, boolean exclusive) {
		return this.getRegionFromBlock(x, y, z).queueDynamicUpdate(x, y, z, nextUpdate, exclusive);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, boolean exclusive) {
		return this.getRegionFromBlock(x, y, z).queueDynamicUpdate(x, y, z, exclusive);
	}

	public StringMap getItemMap() {
		return itemMap;
	}
	
	public StringMap getLightingMap() {
		return lightingMap;
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
	public int getSurfaceHeight(int x, int z, boolean load) {
		SpoutColumn column = getColumn(x, z, load);
		if (column == null) {
			return Integer.MIN_VALUE;
		}

		return column.getSurfaceHeight(x, z);
	}

	@Override
	public int getSurfaceHeight(int x, int z) {
		return getSurfaceHeight(x, z, false);
	}

	@Override
	public BlockMaterial getTopmostBlock(int x, int z, boolean load) {
		SpoutColumn column = getColumn(x, z, load);
		if (column == null) {
			return null;
		}

		return column.getTopmostBlock(x, z);
	}

	@Override
	public BlockMaterial getTopmostBlock(int x, int z) {
		return getTopmostBlock(x, z, false);
	}

	@Override
	public Entity createEntity(Point point, Class<? extends Component> type) {
		SpoutEntity entity = new SpoutEntity(point);
		entity.add(type);
		return entity;
	}

	@Override
	public Entity createEntity(Point point, EntityPrefab prefab) {
		SpoutEntity entity = new SpoutEntity(point);
		for (Class<? extends EntityComponent> c : prefab.getComponents()) {
			entity.add(c);
		}
		return entity;
	}

	/**
	 * Spawns an entity into the world. Fires off a cancellable EntitySpawnEvent
	 */
	@Override
	public void spawnEntity(Entity e) {
		if (e.isSpawned()) {
			throw new IllegalArgumentException("Cannot spawn an entity that is already spawned!");
		}
		SpoutRegion region = (SpoutRegion) e.getRegion();
		if (region == null) {
			throw new IllegalStateException("Cannot spawn an entity that has a null region!");
		}
		if (region.getEntityManager().isSpawnable((SpoutEntity) e)) {
			EntitySpawnEvent event = Spout.getEventManager().callEvent(new EntitySpawnEvent(e, e.getScene().getPosition()));
			if (event.isCancelled()) {
				return;
			}
			region.getEntityManager().addEntity((SpoutEntity) e);
			for (Component component : e.values()) {
				if (component instanceof EntityComponent) {
					((EntityComponent) component).onSpawned();
				}
			}
		} else {
			throw new IllegalStateException("Cannot spawn an entity that already has an id!");
		}
	}

	@Override
	public Entity createAndSpawnEntity(Point point, EntityPrefab prefab, LoadOption option) {
		getRegionFromBlock(point, option);
		Entity e = createEntity(point, prefab);
		return e;
	}

	@Override
	public Entity createAndSpawnEntity(Point point, Class<? extends Component> type, LoadOption option) {
		getRegionFromBlock(point, option);
		Entity e = createEntity(point, type);
		spawnEntity(e);
		return e;
	}

	@Override
	public Entity[] createAndSpawnEntity(Point[] points, Class<? extends Component> type, LoadOption option) {
		Entity[] entities = new Entity[points.length];
		for (int i = 0; i < points.length; i++) {
			entities[i] = createAndSpawnEntity(points[i], type, option);
		}
		return entities;
	}

	@Override
	public Entity[] createAndSpawnEntity(SpawnArrangement arrangement, Class<? extends Component> type, LoadOption option) {
		return createAndSpawnEntity(arrangement.getArrangement(), type, option);
	}

	@Override
	public void copySnapshotRun() {
		snapshotManager.copyAllSnapshots();
	}

	@Override
	public void startTickRun(int stage, long delta) {
		switch (stage) {
			case 0: {
				age.set(age.get() + delta);
				parallelTaskManager.heartbeat(delta);
				taskManager.heartbeat(delta);
				for (Component component : componentHolder.values()) {
					component.tick(delta);
				}
				break;
			}
			default: {
				throw new IllegalStateException("Number of states exceeded limit for SpoutWorld");
			}
		}
	}
	
	@Override
	public int getMaxStage() {
		return 0;
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

	@Override
	public SpoutEngine getEngine() {
		return engine;
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
	public void updateBlockPhysics(int x, int y, int z) {
		this.getRegionFromBlock(x, y, z).updateBlockPhysics(x, y, z);
	}

	@Override
	public void queueBlockPhysics(int x, int y, int z, EffectRange range) {
		queueBlockPhysics(x, y, z, range, null);
	}

	public void queueBlockPhysics(int x, int y, int z, EffectRange range, BlockMaterial oldMaterial) {
		this.getRegionFromBlock(x, y, z).queueBlockPhysics(x, y, z, range, oldMaterial);
	}

	@Override
	public Transform getSpawnPoint() {
		return spawnLocation.copy();
	}

	@Override
	public void setSpawnPoint(Transform transform) {
		spawnLocation.set(transform);
	}

	@Override
	public void finalizeRun() {
		synchronized (columnSet) {
			for (SpoutColumn c : columnSet) {
				c.onFinalize();
			}
		}
	}

	@Override
	public void preSnapshotRun() {

	}

	@Override
	public WorldGenerator getGenerator() {
		return generator;
	}

	@Override
	public List<Entity> getAll() {
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for (Region region : regions) {
			entities.addAll(region.getAll());
		}
		return entities;
	}

	@Override
	public Entity getEntity(int id) {
		for (Region region : regions) {
			Entity entity = region.getEntity(id);
			if (entity != null) {
				return entity;
			}
		}
		return null;
	}

	public void addPlayer(Player player) {
		players.add(player);
	}

	public void removePlayer(Player player) {
		players.remove(player);
	}

	@Override
	public List<Player> getPlayers() {
		return Collections.unmodifiableList(players);
	}

	@Override
	public List<Entity> getNearbyEntities(Point position, Entity ignore, int range) {
		ArrayList<Entity> foundEntities = new ArrayList<Entity>();
		final int RANGE_SQUARED = range * range;

		for (Entity entity : getEntitiesNearRegion(position, range)) {
			if (entity != null && entity != ignore) {
				double distance = VectorMath.distanceSquared(position, entity.getScene().getPosition());
				if (distance < RANGE_SQUARED) {
					foundEntities.add(entity);
				}
			}
		}

		return Collections.unmodifiableList(foundEntities);
	}

	@Override
	public List<Entity> getNearbyEntities(Point position, int range) {
		return getNearbyEntities(position, null, range);
	}

	@Override
	public List<Entity> getNearbyEntities(Entity entity, int range) {
		return getNearbyEntities(entity.getScene().getPosition(), range);
	}

	@Override
	public Entity getNearestEntity(Point position, Entity ignore, int range) {
		Entity best = null;
		double bestDistance = range * range;

		for (Entity entity : getEntitiesNearRegion(position, range)) {
			if (entity != null && entity != ignore) {
				double distance = VectorMath.distanceSquared(position, entity.getScene().getPosition());
				if (distance < bestDistance) {
					bestDistance = distance;
					best = entity;
				}
			}
		}
		return best;
	}

	@Override
	public Entity getNearestEntity(Point position, int range) {
		return getNearestEntity(position, null, range);
	}

	@Override
	public Entity getNearestEntity(Entity entity, int range) {
		return getNearestEntity(entity.getScene().getPosition(), range);
	}

	/**
	 * Gets a set of nearby players to the point, inside of the range.
	 * The search will ignore the specified entity.
	 * @param position of the center
	 * @param ignore Entity to ignore
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@Override
	@LiveRead
	@Threadsafe
	public List<Player> getNearbyPlayers(Point position, Player ignore, int range) {
		ArrayList<Player> foundPlayers = new ArrayList<Player>();
		for (Entity entity : getNearbyEntities(position, ignore, range)) {
			if (entity instanceof Player) {
				foundPlayers.add((Player) entity);
			}
		}
		return Collections.unmodifiableList(foundPlayers);
	}

	/**
	 * Gets a set of nearby players to the point, inside of the range
	 * @param position of the center
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@Override
	@LiveRead
	@Threadsafe
	public List<Player> getNearbyPlayers(Point position, int range) {
		return getNearbyPlayers(position, null, range);
	}

	/**
	 * Gets a set of nearby players to the entity, inside of the range
	 * @param entity marking the center and which is ignored
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@Override
	@LiveRead
	@Threadsafe
	public List<Player> getNearbyPlayers(Entity entity, int range) {
		return getNearbyPlayers(entity.getScene().getPosition(), range);
	}

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * @param position to search from
	 * @param ignore to ignore while searching
	 * @param range to search
	 * @return nearest player
	 */
	@Override
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Point position, Player ignore, int range) {
		Entity best = null;
		double bestDistance = range * range;

		for (Entity entity : getEntitiesNearRegion(position, range)) {
			if (entity != null && entity instanceof Player && entity != ignore) {
				double distance = VectorMath.distanceSquared(position, entity.getScene().getPosition());
				if (distance < bestDistance) {
					bestDistance = distance;
					best = entity;
				}
			}
		}
		return (Player) best;
	}

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * @param range to search
	 * @return nearest player
	 */
	@Override
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Point position, int range) {
		return getNearestPlayer(position, null, range);
	}

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * @param entity to search from
	 * @param range to search
	 * @return nearest player
	 */
	@Override
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Entity entity, int range) {
		return getNearestPlayer(entity.getScene().getPosition(), range);
	}

	/**
	 * Finds all the players inside of the regions inside the range area
	 * @param position to search from
	 * @param range to search for regions
	 * @return nearby region's players
	 */
	private List<Entity> getEntitiesNearRegion(Point position, int range) {
		Region center = this.getRegionFromBlock(position, LoadOption.NO_LOAD);

		ArrayList<Entity> entities = new ArrayList<Entity>();
		if (center != null) {
			final int regions = (range + Region.BLOCKS.SIZE - 1) / Region.BLOCKS.SIZE; //round up 1 region size
			for (int dx = -regions; dx < regions; dx++) {
				for (int dy = -regions; dy < regions; dy++) {
					for (int dz = -regions; dz < regions; dz++) {
						Region region = this.getRegion(center.getX() + dx, center.getY() + dy, center.getZ() + dz, LoadOption.NO_LOAD);
						if (region != null) {
							entities.addAll(region.getAll());
						}
					}
				}
			}
		}
		return entities;
	}

	public List<CollisionVolume> getCollidingObject(CollisionModel model) {
		//TODO Make this more general
		final int minX = GenericMath.floor(model.getVolume().getPosition().getX());
		final int minY = GenericMath.floor(model.getVolume().getPosition().getY());
		final int minZ = GenericMath.floor(model.getVolume().getPosition().getZ());
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

	public SpoutColumn[] getColumns() {
		return columns.values(new SpoutColumn[0]);
	}

	private BAAWrapper getColumnHeightMapBAA(int x, int z) {
		int cx = x >> Region.CHUNKS.BITS;
		int cz = z >> Region.CHUNKS.BITS;

		BAAWrapper baa = null;

		baa = heightMapBAAs.get(cx, cz);

		if (baa == null) {
			File columnDirectory = new File(worldDirectory, "col");
			columnDirectory.mkdirs();
			File file = new File(columnDirectory, "col" + cx + "_" + cz + ".sco");
			baa = new BAAWrapper(file, 1024, 256, RegionFileManager.TIMEOUT);
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

	@Override
	public void unload(boolean save) {
		for (Component component : componentHolder.values()) {
			component.onDetached();
		}
		this.getLightingManager().abort();
		if (save) {
			save();
		}
		Collection<Region> regions = this.regions.getRegions();
		final int total = Math.max(1, regions.size());
		int progress = 0;
		for (Region r : regions) {
			r.unload(save);
			progress++;
			if (save && progress % 4 == 0) {
				Spout.getLogger().info("Saving world [" + getName() + "], " + (int) (progress * 100F / total) + "% Complete");
			}
		}
	}

	@Override
	public void save() {
		WorldFiles.saveWorld(this);
		Spout.getEventManager().callDelayedEvent(new WorldSaveEvent(this));
	}

	@Override
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
	public boolean setBlockLight(int x, int y, int z, byte light, Cause<?> cause) {
		return this.getChunkFromBlock(x, y, z).setBlockLight(x, y, z, light, cause);
	}

	@Override
	public boolean setBlockSkyLight(int x, int y, int z, byte light, Cause<?> cause) {
		return this.getChunkFromBlock(x, y, z).setBlockSkyLight(x, y, z, light, cause);
	}

	@Override
	public BlockComponent getBlockComponent(int x, int y, int z) {
		return this.getRegionFromBlock(x, y, z).getBlockComponent(x, y, z);
	}

	@Override
	public TaskManager getParallelTaskManager() {
		return parallelTaskManager;
	}

	@Override
	public TaskManager getTaskManager() {
		return taskManager;
	}

	private SpoutChunk[][][] getChunks(int x, int y, int z, CuboidBlockMaterialBuffer buffer) {
		Vector3 size = buffer.getSize();

		int startX = x;
		int startY = y;
		int startZ = z;

		int endX = x + size.getFloorX();
		int endY = y + size.getFloorY();
		int endZ = z + size.getFloorZ();

		Chunk start = getChunkFromBlock(startX, startY, startZ);
		Chunk end = getChunkFromBlock(endX - 1, endY - 1, endZ - 1);

		int chunkStartX = start.getX();
		int chunkStartY = start.getY();
		int chunkStartZ = start.getZ();

		int chunkEndX = end.getX();
		int chunkEndY = end.getY();
		int chunkEndZ = end.getZ();

		int chunkSizeX = chunkEndX - chunkStartX + 1;
		int chunkSizeY = chunkEndY - chunkStartY + 1;
		int chunkSizeZ = chunkEndZ - chunkStartZ + 1;

		SpoutChunk[][][] chunks = new SpoutChunk[chunkSizeX][chunkSizeY][chunkSizeZ];
		for (int dx = chunkStartX; dx <= chunkEndX; dx++) {
			for (int dy = chunkStartY; dy <= chunkEndY; dy++) {
				for (int dz = chunkStartZ; dz <= chunkEndZ; dz++) {
					SpoutChunk chunk = getChunk(dx, dy, dz, LoadOption.LOAD_GEN);
					if (chunk == null) {
						throw new IllegalStateException("Null chunk loaded with LoadOption.LOAD_GEN");
					}
					chunks[dx - chunkStartX][dy - chunkStartY][dz - chunkStartZ] = chunk;
				}
			}
		}
		return chunks;
	}

	protected void lockChunks(SpoutChunk[][][] chunks) {
		for (int dx = 0; dx < chunks.length; dx++) {
			SpoutChunk[][] subArray1 = chunks[dx];
			for (int dy = 0; dy < subArray1.length; dy++) {
				SpoutChunk[] subArray2 = subArray1[dy];
				for (int dz = 0; dz < subArray2.length; dz++) {
					subArray2[dz].lockStore();
				}
			}
		}
	}

	protected void unlockChunks(SpoutChunk[][][] chunks) {
		for (int dx = 0; dx < chunks.length; dx++) {
			SpoutChunk[][] subArray1 = chunks[dx];
			for (int dy = 0; dy < subArray1.length; dy++) {
				SpoutChunk[] subArray2 = subArray1[dy];
				for (int dz = 0; dz < subArray2.length; dz++) {
					subArray2[dz].unlockStore();
				}
			}
		}
	}

	@Override
	public void setCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		Vector3 base = buffer.getBase();
		setCuboid(base.getFloorX(), base.getFloorY(), base.getFloorZ(), buffer, cause);
	}

	@Override
	public void setCuboid(int x, int y, int z, CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		if (cause == null) {
			throw new NullPointerException("Cause can not be null");
		}
		CuboidChangeEvent event = new CuboidChangeEvent(buffer, cause);
		Spout.getEngine().getEventManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		SpoutChunk[][][] chunks = getChunks(x, y, z, buffer);

		setCuboid(chunks, x, y, z, buffer, cause);
	}

	public boolean commitCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
		Vector3 base = buffer.getBase();
		int x = base.getFloorX();
		int y = base.getFloorY();
		int z = base.getFloorZ();
		SpoutChunk[][][] chunks = getChunks(x, y, z, buffer);

		return commitCuboid(chunks, buffer, cause);
	}

	protected boolean commitCuboid(SpoutChunk[][][] chunks, CuboidBlockMaterialBuffer buffer, Cause<?> cause) {

		Vector3 base = buffer.getBase();
		int x = base.getFloorX();
		int y = base.getFloorY();
		int z = base.getFloorZ();

		lockChunks(chunks);

		try {
			for (int dx = 0; dx < chunks.length; dx++) {
				SpoutChunk[][] subArray1 = chunks[dx];
				for (int dy = 0; dy < subArray1.length; dy++) {
					SpoutChunk[] subArray2 = subArray1[dy];
					for (int dz = 0; dz < subArray2.length; dz++) {
						if (!subArray2[dz].testCuboid(x, y, z, buffer)) {
							return false;
						}
					}
				}
			}

			// set
			for (int dx = 0; dx < chunks.length; dx++) {
				SpoutChunk[][] subArray1 = chunks[dx];
				for (int dy = 0; dy < subArray1.length; dy++) {
					SpoutChunk[] subArray2 = subArray1[dy];
					for (int dz = 0; dz < subArray2.length; dz++) {
						subArray2[dz].setCuboid(x, y, z, buffer, cause);
					}
				}
			}

			return true;
		} finally {
			unlockChunks(chunks);
		}

	}

	protected void setCuboid(SpoutChunk[][][] chunks, int x, int y, int z, CuboidBlockMaterialBuffer buffer, Cause<?> cause) {

		lockChunks(chunks);

		try {
			for (int dx = 0; dx < chunks.length; dx++) {
				SpoutChunk[][] subArray1 = chunks[dx];
				for (int dy = 0; dy < subArray1.length; dy++) {
					SpoutChunk[] subArray2 = subArray1[dy];
					for (int dz = 0; dz < subArray2.length; dz++) {
						subArray2[dz].setCuboid(x, y, z, buffer, cause);
					}
				}
			}
		} finally {
			unlockChunks(chunks);
		}
	}
	
	@Override
	public CuboidLightBuffer getLightBuffer(short id) {
		throw new UnsupportedOperationException("Unable to get a light buffer corresponding to an entire world");
	}

	@Override
	public CuboidBlockMaterialBuffer getCuboid(boolean backBuffer) {
		throw new UnsupportedOperationException("Unable to create a cuboid corresponding to an entire world");
	}
	
	@Override
	public CuboidBlockMaterialBuffer getCuboid(int x, int y, int z, int sx, int sy, int sz) {
		return getCuboid(x, y, z, sx, sy, sz, true);
	}

	@Override
	public CuboidBlockMaterialBuffer getCuboid(int x, int y, int z, int sx, int sy, int sz, boolean backBuffer) {
		CuboidBlockMaterialBuffer buffer = new CuboidBlockMaterialBuffer(x, y, z, sx, sy, sz, backBuffer);
		getCuboid(x, y, z, buffer);
		return buffer;
	}

	@Override
	public void getCuboid(CuboidBlockMaterialBuffer buffer) {
		Vector3 base = buffer.getBase();
		getCuboid(base.getFloorX(), base.getFloorY(), base.getFloorZ(), buffer);
	}

	@Override
	public void getCuboid(int x, int y, int z, CuboidBlockMaterialBuffer buffer) {
		SpoutChunk[][][] chunks = getChunks(x, y, z, buffer);

		getCuboid(chunks, x, y, z, buffer);
	}

	protected void getCuboid(SpoutChunk[][][] chunks, int x, int y, int z, CuboidBlockMaterialBuffer buffer) {

		lockChunks(chunks);

		try {
			for (int dx = 0; dx < chunks.length; dx++) {
				SpoutChunk[][] subArray1 = chunks[dx];
				for (int dy = 0; dy < subArray1.length; dy++) {
					SpoutChunk[] subArray2 = subArray1[dy];
					for (int dz = 0; dz < subArray2.length; dz++) {
						subArray2[dz].getCuboid(x, y, z, buffer);
					}
				}
			}
		} finally {
			unlockChunks(chunks);
		}
	}

	public void enableRenderQueue() {
		this.renderQueueEnabled.set(true);
	}

	public void disableRenderQueue() {
		this.renderQueueEnabled.set(false);
	}

	public boolean isRenderQueueEnabled() {
		return renderQueueEnabled.get();
	}

	// Worlds don't do any of these

	@Override
	public void runPhysics(int sequence) {
	}

	@Override
	public void runLighting(int sequence) {
	}

	@Override
	public long getFirstDynamicUpdateTime() {
		return SpoutScheduler.END_OF_THE_WORLD;
	}

	@Override
	public void runDynamicUpdates(long time, int sequence) {
	}

	public WeakReference<World> getWeakReference() {
		return selfReference;
	}

	@Override
	public ComponentHolder getComponentHolder() {
		return componentHolder;
	}

	@Override
	public DefaultedMap<Serializable> getDataMap() {
		return componentHolder.getData();
	}

	public RegionFileManager getRegionFileManager() {
		return regionFileManager;
	}

	public BAAWrapper getRegionFile(int rx, int ry, int rz) {
		return regionFileManager.getBAAWrapper(rx, ry, rz);
	}

	public OutputStream getChunkOutputStream(ChunkSnapshot c) {
		return regionFileManager.getChunkOutputStream(c);
	}

	public Model getSkydomeModel() {
		return skydome;
	}

	public void setSkydomeModel(Model model) {
		this.skydome = model;
	}

	@Override
	public boolean addLightingManager(LightingManager<?> manager) {
		return this.lightingManagers.add(manager);
	}
	
	protected LightingManager<?>[] getLightingManagers() {
		return this.lightingManagers.toArray();
	}

	@Override
	public int getSequence() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Thread getExecutionThread() {
		return executionThread;
	}
	
	@Override
	public void setExecutionThread(Thread t) {
		this.executionThread = t;
	}
}
