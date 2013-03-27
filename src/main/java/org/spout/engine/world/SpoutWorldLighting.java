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

import gnu.trove.iterator.TLongIterator;

import org.spout.api.Spout;
import org.spout.api.geo.LoadOption;
import org.spout.api.scheduler.Scheduler;
import org.spout.api.util.hashing.Int21TripleHashed;
import org.spout.api.util.set.TInt21TripleHashSet;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.util.ChunkModel;
import org.spout.engine.util.thread.lock.SpoutSnapshotLock;
import org.spout.engine.world.light.ServerLightStore;

public class SpoutWorldLighting extends Thread {
	private static String taskName = "Lighting Thread";
	/*
	 * Some constants used in the chunk set storage
	 */
	public static final int GREATER = 0;
	public static final int LESSER = 1;
	public static final int REFRESH = 2;

	private SpoutWorldLightingModel skyLight;
	private SpoutWorldLightingModel blockLight;
	private final ChunkModel tmpChunks;
	private final SpoutWorld world;
	private boolean running = false;

	private final TInt21TripleHashSet dirtyChunks = new TInt21TripleHashSet();

	public void addChunk(int x, int y, int z) {
		synchronized (dirtyChunks) {
			dirtyChunks.add(x, y, z);
		}
	}

	/**
	 * Gets the model to calculate sky light
	 * 
	 * @return sky light model
	 */
	public SpoutWorldLightingModel getSkyModel() {
		return this.skyLight;
	}

	/**
	 * Gets the model to calculate block light
	 * 
	 * @return block light model
	 */
	public SpoutWorldLightingModel getBlockModel() {
		return this.blockLight;
	}

	/**
	 * Sets the model to calculate sky light
	 * 
	 * @param model to set to
	 */
	public void setSkyModel(SpoutWorldLightingModel model) {
		this.skyLight = model;
	}

	/**
	 * Sets the model to calculate block light
	 * 
	 * @param model to set to
	 */
	public void setBlockModel(SpoutWorldLightingModel model) {
		this.blockLight = model;
	}

	public boolean isRunning() {
		return this.running;
	}

	public SpoutWorldLighting(SpoutWorld world) {
		super("Lighting thread for world " + world.getName());
		setDaemon(true);
		this.world = world;
		this.skyLight = new SpoutWorldLightingModel(this, true);
		this.blockLight = new SpoutWorldLightingModel(this, false);
		this.tmpChunks = new ChunkModel(world);
	}

	public void abort() {
		this.running = false;
	}

	public SpoutWorld getWorld() {
		return this.world;
	}

	/**
	 * Only to be used by the SpoutWorldLightingModel of this thread (is not thread safe!)
	 */
	protected SpoutChunk getChunkFromBlock(int bx, int by, int bz) {
		return this.tmpChunks.getChunkFromBlock(bx, by, bz);
	}

	@Override
	public void run() {
		long[] chunkBuffer = new long[10];
		int chunkBufferSize = 0;
		TLongIterator iter;
		int i, cx, cy, cz;
		int idleCounter = 0;
		this.running = SpoutConfiguration.LIGHTING_ENABLED.getBoolean();
		Scheduler scheduler = Spout.getEngine().getScheduler();
		SpoutSnapshotLock lock = (SpoutSnapshotLock) scheduler.getSnapshotLock();
		while (this.running) {
			boolean updated = false;
			// Obtain the chunks to work with
			synchronized (this.dirtyChunks) {
				if ((chunkBufferSize = this.dirtyChunks.size()) > 0) {
					if (chunkBufferSize > chunkBuffer.length) {
						chunkBuffer = new long[chunkBufferSize + 100];
					}
					iter = this.dirtyChunks.iterator();
					for (i = 0; i < chunkBufferSize && iter.hasNext(); i++) {
						chunkBuffer[i] = iter.next();
					}
					this.dirtyChunks.clear();
				}
			}
			if (updated = chunkBufferSize > 0) {
				for (i = 0; i < chunkBufferSize; i++) {
					lock.coreReadLock(taskName);
					try {
						cx = Int21TripleHashed.key1(chunkBuffer[i]);
						cy = Int21TripleHashed.key2(chunkBuffer[i]);
						cz = Int21TripleHashed.key3(chunkBuffer[i]);
						if (this.tmpChunks.load(cx, cy, cz, LoadOption.LOAD_ONLY).isLoaded()) {
							SpoutChunk center = this.tmpChunks.getCenter();
							((ServerLightStore)center.getLightStore()).clearRegisteredWithLightingManager();
							if (((ServerLightStore)center.getLightStore()).isInitializingLighting.get()) {
								// Schedule the chunk for a later check-up
								this.addChunk(cx, cy, cz);
							} else {
								while (this.skyLight.resolve(center)) {
								}
								while (this.blockLight.resolve(center)) {
								}
							}
						}
					} finally {
						lock.coreReadUnlock(taskName);
					}
				}
				idleCounter = 0;
			}
			if (!updated) {
				if (idleCounter++ == 20) {
					this.skyLight.cleanUp();
					this.skyLight.reportChanges();
					this.blockLight.cleanUp();
					this.blockLight.reportChanges();
					this.tmpChunks.cleanUp();
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException ex) {}
			}
		}
	}
}
