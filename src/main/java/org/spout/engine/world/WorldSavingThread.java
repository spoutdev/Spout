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

import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
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
	private final LinkedBlockingQueue<Callable<SpoutRegion>> queue = new LinkedBlockingQueue<Callable<SpoutRegion>>();
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
		if (instance.isInterrupted()) {
			Spout.getLogger().warning("Attempt to queue chunks for saving after world thread shutdown");
			task.call();
		} else {
			instance.queue.add(task);
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
		while (!this.isInterrupted()) {
			Callable<SpoutRegion> task;
			try {
				task = queue.take();
				task.call();
			} catch (InterruptedException ignore) {
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		processRemaining();
	}

	private void processRemaining() {
		int toSave = queue.size();
		int saved = 0;
		int lastTenth = 0;
		HashSet<SpoutRegion> regions = new HashSet<SpoutRegion>();
		Callable<SpoutRegion> task;
		while ((task = queue.poll()) != null) {
			SpoutRegion r = null;
			try {
				r = task.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (r != null) {
				regions.add(r);
			}
			int tenth = ((++saved) * 10) / toSave;
			if (tenth != lastTenth) {
				lastTenth = tenth;
				Spout.getLogger().info("Saved " + tenth + "0% of queued chunks");
			}
		}
		for (SpoutRegion r : regions) {
			if (!r.attemptClose()) {
				Spout.getLogger().warning("Closing failed for region " + r.getWorld().getName() + ", " + r.getBase().toBlockString());
			}
		}
	}

	private static class ChunkSaveTask implements Callable<SpoutRegion> {
		final int rx, ry, rz;
		final SpoutChunkSnapshot snapshot;
		final List<DynamicBlockUpdate> blockUpdates;
		final SpoutChunk chunk;
		ChunkSaveTask(SpoutChunk chunk) {
			this.rx = chunk.getRegion().getX();
			this.ry = chunk.getRegion().getY();
			this.rz = chunk.getRegion().getZ();
			this.snapshot = (SpoutChunkSnapshot) chunk.getSnapshot(SnapshotType.BOTH, EntityType.BOTH, ExtraData.DATATABLE);
			this.blockUpdates = chunk.getRegion().getDynamicBlockUpdates(chunk);
			this.chunk = chunk;
		}

		@Override
		public SpoutRegion call() {
			SpoutRegion region = chunk.getWorld().getRegion(rx, ry, rz);
			OutputStream out = region.getChunkOutputStream(snapshot);
			if (out != null) {
				WorldFiles.saveChunk(region.getWorld(), snapshot, blockUpdates, out);
				chunk.saveComplete();
				return region;
			} else {
				Spout.getLogger().severe("World saving thread unable to open region " + region);
				return null;
			}
		}
	}
}
