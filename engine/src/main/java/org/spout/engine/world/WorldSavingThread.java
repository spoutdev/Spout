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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.ChunkSnapshot.EntityType;
import org.spout.api.geo.cuboid.ChunkSnapshot.ExtraData;
import org.spout.api.geo.cuboid.ChunkSnapshot.SnapshotType;
import org.spout.engine.filesystem.versioned.ChunkFiles;
import org.spout.engine.world.dynamic.DynamicBlockUpdate;

/**
 * Dedicated thread to IO write operations for world chunks
 */
public class WorldSavingThread extends Thread {
	private static final WorldSavingThread instance = new WorldSavingThread();
	private final AtomicBoolean queueRunning = new AtomicBoolean(true);
	private final LinkedBlockingQueue<Callable<SpoutServerWorld>> queue = new LinkedBlockingQueue<Callable<SpoutServerWorld>>();

	public WorldSavingThread() {
		super("World Saving Thread");
	}

	public static void startThread() {
		if (Spout.getEngine() instanceof Client) {
			throw new IllegalStateException("Client mode is not allowed to save the world");
		}
		instance.start();
	}

	public static void saveChunk(SpoutChunk chunk) {
		if (Spout.getEngine() instanceof Client) {
			throw new IllegalStateException("Client mode is not allowed to save chunks");
		}
		instance.addChunk(chunk);
	}

	public void addChunk(SpoutChunk chunk) {
		if (Spout.getEngine() instanceof Client) {
			throw new IllegalStateException("Client mode is not allowed to add chunks for saving");
		}
		ChunkSaveTask task = new ChunkSaveTask(chunk);
		instance.queue.add(task);
		pingBackup();
	}

	private void pingBackup() {
		if (Spout.getEngine() instanceof Client) {
			throw new IllegalStateException("Client mode is not allowed to poll backup of chunks");
		}
		if (instance.queueRunning.compareAndSet(false, true)) {
			new BackupThread().start();
		}
	}

	public static void finish() {
		instance.interrupt();
	}

	public static void staticJoin() {
		try {
			instance.join();
		} catch (InterruptedException ie) {
			Spout.getLogger().info("Main thread interruped while waiting for world save thread to end");
		}
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			Callable<SpoutServerWorld> task;
			try {
				task = queue.take();
				task.call();
			} catch (InterruptedException ignore) {
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		processRemaining("main");
	}

	private void processRemaining(String threadType) {
		int toSave = queue.size();
		int saved = 0;
		int lastTenth = 0;
		Callable<SpoutServerWorld> task;
		while ((task = queue.poll()) != null) {
			try {
				task.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
			int tenth = ((++saved) * 10) / toSave;
			if (tenth != lastTenth) {
				lastTenth = tenth;
				Spout.getLogger().info("Saved " + tenth + "0% of queued chunks");
			}
		}
		Collection<? extends World> worlds = Spout.getEngine().getWorlds();
		for (World w : worlds) {
			SpoutColumn[] columns = ((SpoutWorld) w).getColumns();
			for (SpoutColumn c : columns) {
				c.syncSave();
			}
		}
		for (World w : worlds) {
			((SpoutServerWorld) w).getRegionFileManager().stopTimeoutThread();
		}
		for (World w : worlds) {
			((SpoutServerWorld) w).getRegionFileManager().closeAll();
		}

		if (!queueRunning.compareAndSet(true, false)) {
			Spout.getLogger().severe("queueRunning was already false when " + threadType + " world saving thread finished");
		}

		if (!queue.isEmpty()) {
			pingBackup();
		}
	}

	private static class ChunkSaveTask implements Callable<SpoutServerWorld> {
		final SpoutChunkSnapshot snapshot;
		final List<DynamicBlockUpdate> blockUpdates;
		final SpoutChunk chunk;

		ChunkSaveTask(SpoutChunk chunk) {
			this.snapshot = chunk.getSnapshot(SnapshotType.LIGHT_ONLY, EntityType.BOTH, ExtraData.DATATABLE, true);
			this.blockUpdates = chunk.getRegion().getDynamicBlockUpdates(chunk);
			this.chunk = chunk;
		}

		@Override
		public SpoutServerWorld call() {
			SpoutServerWorld world = (SpoutServerWorld) chunk.getWorld();
			OutputStream out = world.getChunkOutputStream(snapshot);
			if (out != null) {
				try {
					ChunkFiles.saveChunk((SpoutServerWorld) chunk.getWorld(), snapshot, blockUpdates, out);
				} finally {
					try {
						out.close();
					} catch (IOException ioe) {
						Spout.getLogger().info("Failed to commit chunk " + chunk);
						ioe.printStackTrace();
					}
				}
				chunk.saveComplete();
			} else {
				Spout.getLogger().severe("World saving thread unable to open file for chunk " + chunk);
			}
			return world;
		}
	}

	private class BackupThread extends Thread {
		public BackupThread() {
			Spout.getLogger().info("Backup world save thread started due to late submission of chunk for saving");
			Thread.dumpStack();
		}

		@Override
		public void run() {
			processRemaining("backup");
		}
	}
}
