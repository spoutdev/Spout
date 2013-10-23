/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import org.spout.api.Server;
import org.spout.api.component.Component;
import org.spout.api.entity.Player;
import org.spout.api.event.world.EntityEnterWorldEvent;
import org.spout.api.event.world.EntityExitWorldEvent;
import org.spout.api.event.world.WorldSaveEvent;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.bytearrayarray.BAAWrapper;
import org.spout.api.util.StringToUniqueIntegerMap;
import org.spout.api.util.hashing.NibblePairHashed;
import org.spout.api.util.list.concurrent.ConcurrentList;
import org.spout.api.util.list.concurrent.setqueue.SetQueue;
import org.spout.api.util.map.WeakValueHashMap;

import org.spout.engine.SpoutEngine;
import org.spout.engine.filesystem.versioned.WorldFiles;
import org.spout.math.imaginary.Quaternion;
import org.spout.math.vector.Vector3;

public class SpoutServerWorld extends SpoutWorld {
	/**
	 * The spawn position.
	 */
	private final Transform spawnLocation = new Transform();
	/**
	 * String item map, used to convert local id's to the server id
	 */
	private final StringToUniqueIntegerMap itemMap;
	/**
	 * String lighting map, used to covert local id's to the server id
	 */
	private final StringToUniqueIntegerMap lightingMap;
	/**
	 * A set of all players currently connected to this world
	 */
	private final List<Player> players = new ConcurrentList<>();
	/**
	 * The directory where the world data is stored
	 */
	private final File worldDirectory;
	/**
	 * RegionFile manager for the world
	 */
	private final RegionFileManager regionFileManager;
	/*
	 * A WeakReference to this world
	 */
	private final WeakReference<SpoutServerWorld> selfReference;
	private final WeakValueHashMap<Long, SetQueue<SpoutColumn>> regionColumnDirtyQueueMap = new WeakValueHashMap<>();

	// TODO set up number of stages ?
	public SpoutServerWorld(String name, SpoutEngine engine, long seed, long age, WorldGenerator generator, UUID uid, StringToUniqueIntegerMap itemMap, StringToUniqueIntegerMap lightingMap) {
		super(name, engine, seed, age, generator, uid);

		this.itemMap = itemMap;
		this.lightingMap = lightingMap;

		worldDirectory = new File(((Server) engine).getWorldFolder(), name);
		worldDirectory.mkdirs();

		regionFileManager = new RegionFileManager(worldDirectory);

		spawnLocation.set(new Transform(new Point(this, 1, 20, 1), Quaternion.IDENTITY, Vector3.ONE));
		selfReference = new WeakReference<>(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SpoutServerWorld)) {
			return false;
		}
		SpoutServerWorld world = (SpoutServerWorld) obj;
		return world.getUID().equals(getUID());
	}

	@Override
	public Transform getSpawnPoint() {
		return spawnLocation.copy();
	}

	@Override
	public void setSpawnPoint(Transform transform) {
		spawnLocation.set(transform);
	}

	public void addPlayer(Player player) {
		players.add(player);
		engine.getEventManager().callDelayedEvent(new EntityEnterWorldEvent(this, player));
	}

	public void removePlayer(Player player) {
		players.remove(player);
		engine.getEventManager().callDelayedEvent(new EntityExitWorldEvent(this, player));
	}

	@Override
	public List<Player> getPlayers() {
		return Collections.unmodifiableList(players);
	}

	@Override
	protected SpoutColumn getColumn(int x, int z, LoadOption loadopt) {
		SpoutColumn column = super.getColumn(x, z, loadopt);

		if (column != null || !loadopt.loadIfNeeded()) {
			return column;
		}

		column = loadColumn(x, z);
		if (column != null || !loadopt.generateIfNeeded()) {
			return column;
		}

		/*
		int[][] height = this.getGenerator().getSurfaceHeight(this, x, z);

		int h = (height[7][7] >> Chunk.BLOCKS.BITS);

		SpoutRegion r = getRegionFromChunk(x, h, z, loadopt);

		if (r == null) {
			throw new IllegalStateException("Unable to generate region for new column and load option " + loadopt);
		}

		RegionGenerator generator = r.getRegionGenerator();
		if (generator != null) {
			generator.generateColumn(x, z, false, true);
		} else {
			setIfNotGenerated(x, z, new int[SpoutColumn.BLOCKS.SIZE][SpoutColumn.BLOCKS.SIZE]);
		}
		*/

		setIfNotGenerated(x, z, new int[SpoutColumn.BLOCKS.SIZE][SpoutColumn.BLOCKS.SIZE]);

		column = super.getColumn(x, z, LoadOption.NO_LOAD);

		if (column == null) {
			throw new IllegalStateException("Unable to generate column " + x + ", " + z);
		}

		return column;
	}

	public SpoutColumn setIfNotGenerated(int x, int z, int[][] heightMap) {
		long key = (((long) x) << 32) | (z & 0xFFFFFFFFL);
		key = (key % 7919);
		key &= columnLockMap.length - 1;

		Lock lock = columnLockMap[(int) key];
		lock.lock();
		try {
			SpoutColumn col = getColumn(x, z, LoadOption.NO_LOAD);
			if (col != null) {
				return col;
			}
			col = loadColumn(x, z);
			if (col != null) {
				return col;
			}
			return setColumn(x, z, new SpoutColumn(heightMap, this, x, z));
		} finally {
			lock.unlock();
		}
	}

	public SpoutColumn loadColumn(int x, int z) {
		InputStream in = getHeightMapInputStream(x, z);
		if (in == null) {
			return null;
		}
		return setColumn(x, z, new SpoutColumn(in, this, x, z));
	}

	protected BAAWrapper getColumnHeightMapBAA(int x, int z) {
		int cx = x >> Region.CHUNKS.BITS;
		int cz = z >> Region.CHUNKS.BITS;

		BAAWrapper baa;

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
		for (Component component : values()) {
			component.onDetached();
		}
		if (save) {
			save();
		}
		Collection<Region> regions = getRegions();
		final int total = Math.max(1, regions.size());
		int progress = 0;
		for (Region r : regions) {
			r.unload(save);
			progress++;
			if (save && progress % 4 == 0) {
				getEngine().getLogger().info("Saving world [" + getName() + "], " + (int) (progress * 100F / total) + "% Complete");
			}
		}
	}

	@Override
	public void save() {
		WorldFiles.saveWorld(this);
		getEngine().getEventManager().callDelayedEvent(new WorldSaveEvent(this));
	}

	@Override
	public void saveChunk(int x, int y, int z) {
		SpoutRegion r = this.getRegionFromChunk(x, y, z, LoadOption.NO_LOAD);
		if (r != null) {
			r.saveChunk(x, y, z);
		}
	}

	@Override
	public WeakReference<SpoutServerWorld> getWeakReference() {
		return selfReference;
	}

	public RegionFileManager getRegionFileManager() {
		if (regionFileManager == null) {
			throw new IllegalStateException("Client does not have file manager");
		}
		return regionFileManager;
	}

	public BAAWrapper getRegionFile(int rx, int ry, int rz) {
		if (regionFileManager == null) {
			throw new IllegalStateException("Client does not have file manager");
		}
		return regionFileManager.getBAAWrapper(rx, ry, rz);
	}

	public OutputStream getChunkOutputStream(ChunkSnapshot c) {
		if (regionFileManager == null) {
			throw new IllegalStateException("Client does not have file manager");
		}
		return regionFileManager.getChunkOutputStream(c);
	}

	public StringToUniqueIntegerMap getItemMap() {
		return itemMap;
	}

	public StringToUniqueIntegerMap getLightingMap() {
		return lightingMap;
	}

	@Override
	public void copySnapshotRun() {
		synchronized (regionColumnDirtyQueueMap) {
			// This performs copy snapshot and also clears the column dirty queues
			Set<Long> keys = regionColumnDirtyQueueMap.keySet();
			for (Long key : keys) {
				SetQueue<SpoutColumn> queue = regionColumnDirtyQueueMap.safeGet(key);
				if (queue != null) {
					SpoutColumn col;
					while ((col = queue.poll()) != null) {
						col.copySnapshot();
					}
				}
			}
			regionColumnDirtyQueueMap.flushKeys();
		}
		snapshotManager.copyAllSnapshots();
	}

	@Override
	public void finalizeRun() {
		synchronized (columnSet) {
			for (SpoutColumn c : columnSet) {
				c.onFinalize();
			}
		}
	}
}
