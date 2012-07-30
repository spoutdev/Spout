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

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.spout.api.Spout;
import org.spout.api.geo.cuboid.ChunkSnapshot.EntityType;
import org.spout.api.geo.cuboid.ChunkSnapshot.ExtraData;
import org.spout.api.geo.cuboid.ChunkSnapshot.SnapshotType;
import org.spout.engine.filesystem.WorldFiles;
import org.spout.engine.world.dynamic.DynamicBlockUpdate;

/**
 * Dedicated thread to IO write operations for world chunks
 */
public class WorldSavingThread extends Thread{
	private static WorldSavingThread instance = null;
	private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	private final AtomicBoolean shutdown = new AtomicBoolean(false);
	public WorldSavingThread() {
		super("World Saving Thread");
	}

	public static void startThread() {
		if (instance == null) {
			instance = new WorldSavingThread();
			instance.start();
		}
	}

	public static void saveChunk(SpoutChunk chunk) {
		ChunkSaveTask task = new ChunkSaveTask(chunk);
		if (instance.shutdown.get()) {
			Spout.getLogger().warning("Attempt to queue chunks for saving after world thread shutdown");
			task.run();
		} else {
			instance.queue.add(task);
		}
	}

	public static void finish() {
		WorldSavingThread localInstance = instance;
		localInstance.shutdown.set(true);
		if (localInstance != null) {
			localInstance.interrupt();
			localInstance.processRemaining();
		}
	}

	@Override
	public void run() {
		while (!this.isInterrupted()) {
			Runnable task;
			try {
				task = queue.take();
				task.run();
			} catch (InterruptedException ignore) {
				break;
			}
		}
	}

	public void processRemaining() {
		int stepMax = queue.size() / 10;
		int step = stepMax;
		int i = 0;
		Runnable task;
		do {
			step--;
			if (step <= 0) {
				i += 10;
				Spout.getLogger().info("Saved " + i + "% of queued chunks");
				step = stepMax;
			}
			task = queue.poll();
			if (task != null) {
				task.run();
			}
		} while (task != null);
	}

	private static class ChunkSaveTask implements Runnable {
		SpoutRegion region;
		SpoutChunkSnapshot snapshot;
		List<DynamicBlockUpdate> blockUpdates;
		ChunkSaveTask(SpoutChunk chunk) {
			this.region = chunk.getRegion();
			this.snapshot = (SpoutChunkSnapshot) chunk.getSnapshot(SnapshotType.BOTH, EntityType.ENTITIES, ExtraData.BOTH);
			this.blockUpdates = chunk.getRegion().getDynamicBlockUpdates(chunk);
		}

		@Override
		public void run() {
			WorldFiles.saveChunk(region.getWorld(), snapshot, blockUpdates, region.getChunkOutputStream(snapshot));
		}
	}

}
