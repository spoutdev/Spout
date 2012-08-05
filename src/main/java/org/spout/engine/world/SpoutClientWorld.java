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
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.spout.api.Source;
import org.spout.api.datatable.DataMap;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.entity.Entity;
import org.spout.api.entity.component.Controller;
import org.spout.api.entity.component.controller.BlockController;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.generator.biome.Biome;
import org.spout.api.generator.biome.BiomeGenerator;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.map.DefaultedMap;
import org.spout.api.material.range.EffectRange;
import org.spout.api.player.Player;
import org.spout.api.plugin.Plugin;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.util.cuboid.CuboidBuffer;
import org.spout.api.util.hashing.IntPairHashed;
import org.spout.api.util.map.concurrent.TSyncLongObjectHashMap;
import org.spout.engine.SpoutEngine;
import org.spout.engine.entity.EntityManager;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.util.thread.ThreadAsyncExecutor;

/**
 * A dummy world used for the client
 */
public class SpoutClientWorld extends SpoutAbstractWorld {
	/**
	 * A map of the loaded columns
	 */
	private final TSyncLongObjectHashMap<SpoutColumn> columns = new TSyncLongObjectHashMap<SpoutColumn>();
	private final Set<SpoutColumn> columnSet = new LinkedHashSet<SpoutColumn>();

	/**
	 * Data map and Datatable associated with it
	 */
	private final DatatableMap datatableMap;
	private final DataMap dataMap;

	private final RegionSource regions;

	/**
	 * Holds all of the entities to be simulated
	 */
	private final EntityManager entityManager;

	public SpoutClientWorld(String name, UUID uid, SpoutEngine engine, byte[] datatable) {
		super(name, uid, engine, -1, new ThreadAsyncExecutor("SpoutClientWorld-" + name));

		this.datatableMap = new GenericDatatableMap();
		this.datatableMap.decompress(datatable);
		this.dataMap = new DataMap(datatableMap);
		regions = new RegionSource(this, null);
		entityManager = new EntityManager();
	}

	/**
	 * Removes a column corresponding to the given Column coordinates
	 *
	 * @param x the x coordinate
	 * @param z the z coordinate
	 */
	public void removeColumn(int x, int z, SpoutColumn column) {
		long key = IntPairHashed.key(x, z);
		if (columns.remove(key, column)) {
			synchronized(columnSet) {
				columnSet.remove(column);
			}
		}
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
		int colX = x >> SpoutColumn.BLOCKS.BITS;
		int colZ = z >> SpoutColumn.BLOCKS.BITS;
		long key = IntPairHashed.key(colX, colZ);
		SpoutColumn column = columns.get(key);
		if (create && column == null) {
			SpoutColumn newColumn = new SpoutColumn(this, colX, colZ);
			column = columns.putIfAbsent(key, newColumn);
			if (column == null) {
				column = newColumn;
				synchronized(columnSet) {
					columnSet.add(column);
				}
			}
		}
		return column;
	}

	public SpoutColumn getColumn(int x, int z) {
		return getColumn(x, z, false);
	}

	@Override
	public SpoutRegion getRegion(int x, int y, int z, LoadOption loadopt) {
		return regions.getRegion(x, y, z, LoadOption.NO_LOAD);
	}

	@Override
	public SpoutChunk getChunk(int x, int y, int z, LoadOption loadopt) {
		SpoutRegion region = getRegionFromChunk(x, y, z, loadopt);
		if (region != null) {
			return region.getChunk(x, y, z, loadopt);
		}
		return null;
	}

	public boolean containsChunk(int x, int y, int z) {
		return true;
	}

	public boolean hasChunk(int x, int y, int z) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean hasChunkAtBlock(int x, int y, int z) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void saveChunk(int x, int y, int z) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void unloadChunk(int x, int y, int z, boolean save) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public int getNumLoadedChunks() {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean setBlockLight(int x, int y, int z, byte light, Source source) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean setBlockSkyLight(int x, int y, int z, byte light, Source source) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void setBlockController(int x, int y, int z, BlockController controller) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean containsBlock(int x, int y, int z) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public BlockController getBlockController(int x, int y, int z) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public Biome getBiomeType(int x, int y, int z) {
		if (y < 0 || y > getHeight()) {
			return null;
		}
		final SpoutChunk chunk = getChunkFromBlock(x, y, z, LoadOption.LOAD_ONLY);
		if (chunk == null) {
			return null;
		}
		return chunk.getBiomeType(x, y, z);
	}

	public void queueBlockPhysics(int x, int y, int z, EffectRange range, Source source) {
	}

	public void updateBlockPhysics(int x, int y, int z, Source source) {
	}

	public void finalizeRun() throws InterruptedException {
		entityManager.finalizeRun();
	}

	public void preSnapshotRun() throws InterruptedException {
		entityManager.preSnapshotRun();
	}

	public void copySnapshotRun() throws InterruptedException {
	}

	public void startTickRun(int stage, long delta) throws InterruptedException {
	}

	public void runPhysics(int sequence) throws InterruptedException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void runDynamicUpdates(long time, int sequence) throws InterruptedException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void runLocalPhysics() throws InterruptedException {
	}

	public int runGlobalPhysics() throws InterruptedException {
		return 0;
	}

	public void runLocalDynamicUpdates(long time) throws InterruptedException {
	}

	public int runGlobalDynamicUpdates() throws InterruptedException {
		return 0;
	}

	public long getFirstDynamicUpdateTime() {
		return 0;
	}

	public void haltRun() throws InterruptedException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public long getAge() {
		return -1;
	}

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

	public Entity createEntity(Point point, Controller controller) {
		return new SpoutEntity(getEngine(), point, controller);
	}

	public void spawnEntity(Entity e) {
		if (e.isSpawned()) {
			throw new IllegalArgumentException("Cannot spawn an entity that is already spawned!");
		}
		((SpoutRegion) e.getRegion()).addEntity(e);
	}

	public Transform getSpawnPoint() {
		return new Transform();
	}

	public void setSpawnPoint(Transform transform) {
	}

	public long getSeed() {
		return 0;
	}

	public WorldGenerator getGenerator() {
		return null;
	}

	public int getHeight() {
		return 256;
	}

	public byte getSkyLight() {
		return 0;
	}

	public void setSkyLight(byte newLight) {
		//To change body of implemented methods use File | Settings | File Templates.
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

	public Set<Player> getPlayers() {
		Set<Player> players = new HashSet<Player>();
		for (Entity entity : getAll()) {
			if (entity instanceof Player) {
				players.add((Player) entity);
			}
		}
		return players;
	}

	public File getDirectory() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public DefaultedMap<String, Serializable> getDataMap() {
		return dataMap;
	}

	public Serializable get(Object key) {
		return dataMap.get(key);
	}

	public TaskManager getParallelTaskManager() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public TaskManager getTaskManager() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean setCuboid(CuboidBuffer buffer, Plugin plugin) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean addChunk(ChunkSnapshot c) {
		return addChunk(c.getX(), c.getY(), c.getZ(), c.getBlockIds(), c.getBlockData(), c.getBlockLight(), c.getSkyLight(), c.getBiomeManager() == null ? null : c.getBiomeManager().serialize());
	}

	public boolean addChunk(int x, int y, int z, short[] blockIds, short[] blockData, byte[] blockLight, byte[] skyLight, byte[] biomes) {
		// TODO: Implement
		return false;
	}
}
