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
package org.spout.engine;

import gnu.trove.map.hash.TObjectByteHashMap;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.spout.api.Spout;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.engine.util.thread.lock.SpoutSnapshotLock;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutChunk.SaveState;
import org.spout.engine.world.SpoutRegion;

/**
 * Monitors references to memory intensive objects and warns if objects appear
 * to be leaking memory
 */
public class MemoryLeakThread extends Thread {
	private static String taskName = "Memory Leak Thread";
	
	private final TObjectByteHashMap<WeakReference<Chunk>> chunkPasses = new TObjectByteHashMap<WeakReference<Chunk>>();
	private final List<WeakReference<Chunk>> chunkQueue = new LinkedList<WeakReference<Chunk>>();
	private final List<WeakReference<Chunk>> chunkArrivals = new LinkedList<WeakReference<Chunk>>();
	//each pass is one minute, so 5 = 5 minutes
	private static final int WARNING_PASSES = 5;
	private static final int LEAK_PASSES = 30;

	public MemoryLeakThread() {
		super("Memory Leak Detection Thread");
		this.setDaemon(true);
	}

	public void monitor(Chunk chunk) {
		if (Spout.debugMode()) {
			synchronized (chunkArrivals) {
				chunkArrivals.add(new WeakReference<Chunk>(chunk));
			}
		}
	}

	@Override
	public void run() {
		SpoutSnapshotLock lock = (SpoutSnapshotLock)Spout.getEngine().getScheduler().getSnapshotLock();
		while (!this.isInterrupted()) {
			boolean locked = lock.coreReadTryLock(taskName);
			if (locked) {
				try {
					System.gc();
					System.gc();
					System.gc();
					System.gc();
					System.gc();
					System.gc();
					System.gc();
					System.gc();
					System.gc();
					TObjectByteHashMap<WeakReference<Chunk>> recentChunkPasses = new TObjectByteHashMap<WeakReference<Chunk>>();

					int analyzed = 0;
					int unobserved = 0;
					int queuedForUnload = 0;
					int unloaded = 0;
					Iterator<WeakReference<Chunk>> i = chunkQueue.iterator();
					while (i.hasNext()) {
						WeakReference<Chunk> ref = i.next();
						Chunk chunk = ref.get();
						if (chunk != null) {
							if (!chunk.isLoaded() || chunk.getNumObservers() == 0) {
								//Reference should be gc'd soon
								byte passes;
								if (chunkPasses.containsKey(ref)) {
									passes = (byte) (chunkPasses.get(ref) + 1);
									analyzed++;
									if (((SpoutChunk)chunk).getNumObservers() == 0) {
										unobserved++;
									}
									SaveState saveState = ((SpoutChunk)chunk).getSaveState();
									if (saveState == SaveState.UNLOAD || saveState == SaveState.UNLOAD_SAVE) {
										queuedForUnload++;
									}
									if (saveState == SaveState.UNLOADED) {
										unloaded++;
									}
								} else {
									passes = 1;
								}
								recentChunkPasses.put(ref, passes);

								if (passes > LEAK_PASSES) {
									Spout.getLogger().severe("Chunk is leaking memory! Chunk is " + chunk.toString() + ":" + ((SpoutChunk)chunk).getSaveState());
									if (chunk.getRegion() != null && chunk.getRegion().getChunk(chunk.getX(), chunk.getY(), chunk.getZ()) == chunk) {
										Spout.getLogger().severe("Chunk is still referenced by it's region! Chunk is " + chunk.toString());
									}
									int rx = chunk.getX() >> Region.CHUNKS.BITS;
									int ry = chunk.getY() >> Region.CHUNKS.BITS;
									int rz = chunk.getZ() >> Region.CHUNKS.BITS;
									Region r = chunk.getWorld().getRegion(rx, ry,rz, LoadOption.NO_LOAD);
									if (r != chunk.getRegion()) {
										Spout.getLogger().severe("Chunk's region is not referenced by the world! Chunk is " + chunk.toString() + 
												" Chunk's region is " + chunk.getRegion() + " world's region is " + r);
									}
									Thread t = ((SpoutRegion)chunk.getRegion()).getExceutionThread();
									if (r == null && t.isAlive()) {
										Spout.getLogger().severe("Region's thread is still alive, but world returns null for get region");
									}
								} else if (passes > WARNING_PASSES) {
									Spout.getLogger().warning("Chunk may be leaking memory, " + chunk.toString());
									if (chunk.getRegion() != null && chunk.getRegion().getChunk(chunk.getX(), chunk.getY(), chunk.getZ()) == chunk) {
										Spout.getLogger().severe("Chunk is still referenced by it's region! Chunk is " + chunk.toString());
									}
									int rx = chunk.getX() >> Region.CHUNKS.BITS;
									int ry = chunk.getY() >> Region.CHUNKS.BITS;
									int rz = chunk.getZ() >> Region.CHUNKS.BITS;
									Region r = chunk.getWorld().getRegion(rx, ry,rz, LoadOption.NO_LOAD);
									if (r != chunk.getRegion()) {
										Spout.getLogger().severe("Chunk's region is not referenced by the world! Chunk is " + chunk.toString() + 
												" Chunk's region is " + chunk.getRegion() + " world's region is " + r);
									}
									Thread t = ((SpoutRegion)chunk.getRegion()).getExceutionThread();
									if (r == null && t.isAlive()) {
										Spout.getLogger().severe("Region's thread is still alive, but world returns null for get region");
									}
								}
							}
						} else {
							i.remove();
						}
					}

					//Ensures that all the key/values are recent
					chunkPasses.clear();
					chunkPasses.putAll(recentChunkPasses);

					synchronized (chunkArrivals) {
						chunkQueue.addAll(chunkArrivals);
						chunkArrivals.clear();
					}

					Spout.getLogger().info("Memory Leak Detection Analyzed " + analyzed + " potential leaks, " + unobserved + " Unobserved, " + queuedForUnload + " queued for unloading, " + unloaded + " unloaded");
				} finally {
					lock.coreReadUnlock(taskName);
				}
			}
			try {
				sleep(60000);
			} catch (InterruptedException ignore) { }
		}
	}
}
