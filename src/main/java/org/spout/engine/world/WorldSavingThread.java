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

import org.spout.api.geo.cuboid.ChunkSnapshot;
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
	public WorldSavingThread() {
		super("World Saving Thread");
		this.setDaemon(true);
	}
	
	public static void startThread() {
		if (instance == null) {
			instance = new WorldSavingThread();
			instance.start();
		}
	}
	
	public static void saveChunk(SpoutChunk chunk) {
		ChunkSaveTask task = instance.new ChunkSaveTask(chunk);
		instance.queue.add(task);
	}
	
	public static void finish() {
		instance.interrupt();
		instance.processRemaining();
	}
	
	@Override
	public void run() {
		while(!this.isInterrupted()) {
			Runnable task;
			try {
				task = queue.take();
				task.run();
			} catch (InterruptedException ignore) { }
		}
	}
	
	public void processRemaining() {
		Runnable task;
		do {
			task = queue.poll();
			if (task != null) {
				task.run();
			}
		} while(task != null);
	}
	
	private class ChunkSaveTask implements Runnable {
		SpoutRegion region;
		ChunkSnapshot snapshot;
		List<DynamicBlockUpdate> blockUpdates;
		ChunkSaveTask(SpoutChunk chunk) {
			this.region = chunk.getRegion();
			this.snapshot = chunk.getSnapshot(SnapshotType.BOTH, EntityType.ENTITIES, ExtraData.BOTH);
			this.blockUpdates = chunk.getRegion().getDynamicBlockUpdates(chunk);
		}
		
		@Override
		public void run() {
			WorldFiles.saveChunk(region.getWorld(), snapshot, blockUpdates, region.getChunkOutputStream(snapshot));
		}
	}

}
