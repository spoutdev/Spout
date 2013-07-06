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
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.spout.api.Server;
import org.spout.api.component.Component;
import org.spout.api.entity.Player;
import org.spout.api.event.world.EntityEnterWorldEvent;
import org.spout.api.event.world.EntityExitWorldEvent;
import org.spout.api.event.world.WorldSaveEvent;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.bytearrayarray.BAAWrapper;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.util.StringMap;
import org.spout.api.util.list.concurrent.ConcurrentList;
import org.spout.api.util.list.concurrent.setqueue.SetQueue;
import org.spout.api.util.map.WeakValueHashMap;
import org.spout.engine.SpoutEngine;
import org.spout.engine.filesystem.versioned.WorldFiles;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.scheduler.SpoutTaskManager;
import org.spout.engine.util.thread.AsyncManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableLong;

public class SpoutServerWorld extends SpoutWorld implements AsyncManager {
	/**
	 * The spawn position.
	 */
	private final Transform spawnLocation = new Transform();
	/**
	 * The current world age.
	 */
	private SnapshotableLong age;
	/**
	 * String item map, used to convert local id's to the server id
	 */
	private final StringMap itemMap;
	/**
	 * String lighting map, used to covert local id's to the server id
	 */
	private final StringMap lightingMap;
	/**
	 * A set of all players currently connected to this world
	 */
	private final List<Player> players = new ConcurrentList<Player>();
	/**
	 * The directory where the world data is stored
	 */
	private final File worldDirectory;
	/**
	 * RegionFile manager for the world
	 */
	private final RegionFileManager regionFileManager;
	protected final SpoutTaskManager taskManager;
	/**
	 * The execution thread for this world
	 */
	private Thread executionThread;
	/*
	 * A WeakReference to this world
	 */
	private final WeakReference<SpoutServerWorld> selfReference;
	public static final WeakReference<SpoutServerWorld> NULL_WEAK_REFERENCE = new WeakReference<SpoutServerWorld>(null);
	
	private final WeakValueHashMap<Long, SetQueue<SpoutColumn>> regionColumnDirtyQueueMap = new WeakValueHashMap<Long, SetQueue<SpoutColumn>>();

	// TODO set up number of stages ?
	public SpoutServerWorld(String name, SpoutEngine engine, long seed, long age, WorldGenerator generator, UUID uid, StringMap itemMap, StringMap lightingMap) {
		super(name, engine, seed, age, generator, uid);

		this.itemMap = itemMap;
		this.lightingMap = lightingMap;

		worldDirectory = new File(((Server) engine).getWorldFolder(), name);
		worldDirectory.mkdirs();

		regionFileManager = new RegionFileManager(worldDirectory);

		this.age = new SnapshotableLong(snapshotManager, age);
		spawnLocation.set(new Transform(new Point(this, 1, 20, 1), Quaternion.IDENTITY, Vector3.ONE));
		selfReference = new WeakReference<SpoutServerWorld>(this);
		
		taskManager = new SpoutTaskManager(getEngine().getScheduler(), null, this, age);

		getEngine().getScheduler().addAsyncManager(this);
	}

	@Override
	public long getAge() {
		return age.get();
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
	protected BAAWrapper getColumnHeightMapBAA(int x, int z) {
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

	public StringMap getItemMap() {
		return itemMap;
	}

	public StringMap getLightingMap() {
		return lightingMap;
	}
	
	@Override
	public TaskManager getTaskManager() {
		return taskManager;
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
	public void startTickRun(int stage, long delta) {
		switch (stage) {
			case 0: {
				age.set(age.get() + delta);
				parallelTaskManager.heartbeat(delta);
				taskManager.heartbeat(delta);
				for (Component component : values()) {
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

	@Override
	public int getSequence() {
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