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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.collision.BoundingBox;
import org.spout.api.collision.CollisionModel;
import org.spout.api.collision.CollisionVolume;
import org.spout.api.component.Component;
import org.spout.api.component.components.BlockComponent;
import org.spout.api.component.components.DatatableComponent;
import org.spout.api.component.components.WorldComponent;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.entity.spawn.SpawnArrangement;
import org.spout.api.event.block.CuboidChangeEvent;
import org.spout.api.event.entity.EntitySpawnEvent;
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
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicUpdateEntry;
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
import org.spout.api.util.list.concurrent.ConcurrentList;
import org.spout.api.util.map.concurrent.TSyncIntPairObjectHashMap;
import org.spout.api.util.map.concurrent.TSyncLongObjectHashMap;

import org.spout.api.util.sanitation.StringSanitizer;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.Threadsafe;
import org.spout.engine.SpoutEngine;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.filesystem.SharedFileSystem;
import org.spout.engine.filesystem.WorldFiles;
import org.spout.engine.scheduler.SpoutParallelTaskManager;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.scheduler.SpoutTaskManager;
import org.spout.engine.util.thread.AsyncExecutor;
import org.spout.engine.util.thread.AsyncManager;
import org.spout.engine.util.thread.ThreadAsyncExecutor;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableLong;

public class SpoutWorld extends AsyncManager implements World {
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
	
	/*
	 * A WeakReference to this world
	 */
	private final WeakReference<World> selfReference;
	public static final WeakReference<World> NULL_WEAK_REFERENCE = new WeakReference<World>(null);
	
	/*
	 * Components
	 */
	private final HashMap<Class<? extends Component>, Component> components = new HashMap<Class<? extends Component>, Component>();
	private final DatatableComponent datatable;
	
	// TODO set up number of stages ?
	public SpoutWorld(String name, SpoutEngine engine, long seed, long age, WorldGenerator generator, UUID uid, StringMap itemMap, DatatableMap extraData) {
		super(1, new ThreadAsyncExecutor(toString(name, uid, age)), engine);
		this.engine = engine;
		if (!StringSanitizer.isAlphaNumericUnderscore(name)) {
			name = Long.toHexString(System.currentTimeMillis());
			Spout.getEngine().getLogger().severe("World name " + name + " is not valid, using " + name + " instead");
		}
		this.name = name;
		this.uid = uid;
		this.itemMap = itemMap;
		this.seed = seed;

		this.generator = generator;
		regions = new RegionSource(this, snapshotManager);

		worldDirectory = new File(SharedFileSystem.WORLDS_DIRECTORY, name);
		worldDirectory.mkdirs();

		heightMapBAAs = new TSyncIntPairObjectHashMap<BAAWrapper>();
		
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
		//Datatables
		if (extraData != null) {
			datatable = new DatatableComponent(extraData);
		} else {
			datatable = new DatatableComponent();
		}

		addComponent(datatable);		
	}

	public String getName() {
		return name;
	}

	public UUID getUID() {
		return uid;
	}

	@Override
	public long getAge() {
		return age.get();
	}

	@Override
	public SpoutBlock getBlock(int x, int y, int z, Source source) {
		return new SpoutBlock(this, x, y, z, source);
	}

	@Override
	public SpoutBlock getBlock(float x, float y, float z, Source source) {
		return this.getBlock(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z), source);
	}

	@Override
	public SpoutBlock getBlock(Vector3 position, Source source) {
		return this.getBlock(position.getX(), position.getY(), position.getZ(), source);
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
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Source source) {
		return this.getChunkFromBlock(x, y, z).setBlockMaterial(x, y, z, material, data, source);
	}

	@Override
	public boolean setBlockData(int x, int y, int z, short data, Source source) {
		return getChunkFromBlock(x, y, z).setBlockData(x, y, z, data, source);
	}

	@Override
	public boolean addBlockData(int x, int y, int z, short data, Source source) {
		return getChunkFromBlock(x, y, z).addBlockData(x, y, z, data, source);
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
	public boolean compareAndSetData(int x, int y, int z, int expect, short data, Source source) {
		return getChunkFromBlock(x, y, z).compareAndSetData(x, y, z, expect, data, source);
	}

	@Override
	public short setBlockDataBits(int x, int y, int z, int bits, boolean set, Source source) {
		return getChunkFromBlock(x, y, z).setBlockDataBits(x, y, z, bits, set, source);
	}

	@Override
	public short setBlockDataBits(int x, int y, int z, int bits, Source source) {
		return getChunkFromBlock(x, y, z).setBlockDataBits(x, y, z, bits, source);
	}

	@Override
	public short clearBlockDataBits(int x, int y, int z, int bits, Source source) {
		return getChunkFromBlock(x, y, z).clearBlockDataBits(x, y, z, bits, source);
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
	public int setBlockDataField(int x, int y, int z, int bits, int value, Source source) {
		return getChunkFromBlock(x, y, z).setBlockDataField(x, y, z, bits, value, source);
	}

	@Override
	public int addBlockDataField(int x, int y, int z, int bits, int value, Source source) {
		return getChunkFromBlock(x, y, z).addBlockDataField(x, y, z, bits, value, source);
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
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, int data) {
		return this.getRegionFromBlock(x, y, z).queueDynamicUpdate(x, y, z, nextUpdate, data);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate) {
		return this.getRegionFromBlock(x, y, z).queueDynamicUpdate(x, y, z, nextUpdate);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z) {
		return this.getRegionFromBlock(x, y, z).queueDynamicUpdate(x, y, z);
	}


	public StringMap getItemMap() {
		return itemMap;
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
	public Entity createEntity(Point point) {
		return new SpoutEntity(point);
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
			EntitySpawnEvent event = Spout.getEventManager().callEvent(new EntitySpawnEvent(e, e.getTransform().getPosition()));
			if (event.isCancelled()) {
				return;
			}
			region.addEntity(e);
		} else {
			throw new IllegalStateException("Cannot spawn an entity that already has an id!");
		}
	}

	@Override
	public Entity createAndSpawnEntity(Point point, LoadOption option) {
		getRegionFromBlock(point, option);
		Entity e = createEntity(point);
		spawnEntity(e);
		return e;
	}

	@Override
	public Entity[] createAndSpawnEntity(Point[] points, LoadOption option) {
		Entity[] entities = new Entity[points.length];
		for (int i = 0; i < points.length; i++) {
			entities[i] = createAndSpawnEntity(points[i], option);
		}
		return entities;
	}

	@Override
	public Entity[] createAndSpawnEntity(SpawnArrangement arrangement, LoadOption option) {
		return createAndSpawnEntity(arrangement.getArrangement(), option);
	}

	@Override
	public void copySnapshotRun() throws InterruptedException {
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
				break;
			}
			default: {
				throw new IllegalStateException("Number of states exceeded limit for SpoutWorld");
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

	@Override
	public void finalizeRun() throws InterruptedException {
		synchronized (columnSet) {
			for (SpoutColumn c : columnSet) {
				c.onFinalize();
			}
		}
	}

	@Override
	public void preSnapshotRun() throws InterruptedException {

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

	/**
	 * Gets a set of nearby players to the point, inside of the range
	 * @param position of the center
	 * @param range to look for
	 * @return A set of nearby Players
	 */
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
	@LiveRead
	@Threadsafe
	public List<Player> getNearbyPlayers(Entity entity, int range) {
		return getNearbyPlayers(entity.getTransform().getPosition(), entity, range);
	}

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
	public List<Player> getNearbyPlayers(Point position, Entity ignore, int range) {
		ArrayList<Player> foundPlayers = new ArrayList<Player>();
		final int RANGE_SQUARED = range * range;

		for (Player plr : getPlayersNearRegion(position, range)) {
			if (plr != ignore && plr != null) {
				double distance = MathHelper.distanceSquared(position, plr.getTransform().getPosition());
				if (distance < RANGE_SQUARED) {
					foundPlayers.add(plr);
				}
			}
		}

		return foundPlayers;
	}

	/**
	 * Finds all the players inside of the regions inside the range area
	 * @param position to search from
	 * @param range to search for regions
	 * @return nearby region's players
	 */
	private List<Player> getPlayersNearRegion(Point position, int range) {
		Region center = this.getRegionFromBlock(position, LoadOption.NO_LOAD);

		ArrayList<Player> players = new ArrayList<Player>();
		if (center != null) {
			final int regions = (range + Region.BLOCKS.SIZE - 1) / Region.BLOCKS.SIZE; //round up 1 region size
			for (int dx = -regions; dx < regions; dx++) {
				for (int dy = -regions; dy < regions; dy++) {
					for (int dz = -regions; dz < regions; dz++) {
						Region region = this.getRegion(center.getX() + dx, center.getY() + dy, center.getZ() + dz, LoadOption.NO_LOAD);
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
	 * @param ignore to ignore while searching
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Point position, Entity ignore, int range) {
		Player best = null;
		double bestDistance = range * range;

		for (Player plr : getPlayersNearRegion(position, range)) {
			if (plr != ignore && plr != null) {
				double distance = MathHelper.distanceSquared(position, plr.getTransform().getPosition());
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
	 * @param entity to search from
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Entity entity, int range) {
		return getNearestPlayer(entity.getTransform().getPosition(), entity, range);
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
	public void setBlockComponent(int x, int y, int z, BlockComponent component) {
		getRegionFromBlock(x, y, z).setBlockComponent(x, y, z, component);
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
	public void runLighting(int sequence) throws InterruptedException {
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
	
	@Override
	public WorldComponent addComponent(Component component) {
		Class<? extends Component> clazz = component.getClass();
		if (hasComponent(clazz)) {
			return (WorldComponent) getComponent(clazz);
		}
		components.put(clazz, component);
		component.attachTo(this);
		component.onAttached();
		return (WorldComponent) component;
	}

	@Override
	public boolean removeComponent(Class<? extends Component> aClass) {
		if (!hasComponent(aClass)) {
			return false;
		}
		getComponent(aClass).onDetached();
		components.remove(aClass);
		return true;
	}

	@Override
	public WorldComponent getComponent(Class<? extends Component> aClass) {
		for(Class<? extends Component> c : components.keySet()){
			if(aClass.isAssignableFrom(c)) return (WorldComponent) components.get(c);
		}
		return null;
	}

	@Override
	public boolean hasComponent(Class<? extends Component> aClass) {
		for(Class<? extends Component> c : components.keySet()){
			if(aClass.isAssignableFrom(c)) return true;
		}
		return false;
	}

	@Override
	public Collection<Component> getComponents() {
		return components.values();
	}
	
	@Override
	public DatatableComponent getDatatable() {
		return datatable;
	}
}
