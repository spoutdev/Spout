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

import gnu.trove.iterator.TLongIterator;

import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.geo.LoadOption;
import org.spout.api.util.hashing.Int21TripleHashed;
import org.spout.api.util.set.TInt21TripleHashSet;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.util.thread.lock.SpoutSnapshotLock;

public class SpoutWorldLighting extends Thread implements Source {
	
	private static String taskName = "Lighting Thread";

	/*
	 * Some constants used in the chunk set storage
	 */
	public static final int GREATER = 0;
	public static final int LESSER = 1;
	public static final int REFRESH = 2;

	public final SpoutWorldLightingModel skyLight;
	public final SpoutWorldLightingModel blockLight;
	private final SpoutWorld world;
	private boolean running = false;

	private final TInt21TripleHashSet dirtyChunks = new TInt21TripleHashSet();

	public void addChunk(int x, int y, int z) {
		synchronized (dirtyChunks) {
			dirtyChunks.add(x, y, z);
		}
	}

	public boolean isRunning() {
		return this.running;
	}

	public SpoutWorldLighting(SpoutWorld world) {
		super("Lighting thread for world " + world.getName());
		this.world = world;
		this.skyLight = new SpoutWorldLightingModel(this, true);
		this.blockLight = new SpoutWorldLightingModel(this, false);
	}

	public void abort() {
		this.running = false;
	}

	public SpoutWorld getWorld() {
		return this.world;
	}

	@Override
	public void run() {
		long[] chunkBuffer = new long[10];
		int chunkBufferSize = 0;
		TLongIterator iter;
		int i, cx, cy, cz;
		int idleCounter = 0;
		SpoutChunk chunk;
		this.running = SpoutConfiguration.LIGHTING_ENABLED.getBoolean();
		SpoutSnapshotLock lock = (SpoutSnapshotLock)Spout.getEngine().getScheduler().getSnapshotLock();
		while (this.running) {
			boolean updated = false;
			// Bergerkiller, ideally, these 2 methods would have a max time of 5-10ms
			// Better to do 10 calls of 2ms each, and release the lock between them, than 1 call of 20ms.
			lock.coreReadLock(taskName);
			try {
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
					// Resolve all chunks
					for (i = 0; i < chunkBufferSize; i++) {
						cx = Int21TripleHashed.key1(chunkBuffer[i]);
						cy = Int21TripleHashed.key2(chunkBuffer[i]);
						cz = Int21TripleHashed.key3(chunkBuffer[i]);
						chunk = this.world.getChunk(cx, cy, cz, LoadOption.LOAD_ONLY);
						if (chunk != null && chunk.isLoaded() && chunk.isPopulated()) {
							// Resolve all the operations in this chunk
							while (this.blockLight.resolve(chunk));
							while (this.skyLight.resolve(chunk));
						}
					}
					idleCounter = 0;
				}
			} finally {
				lock.coreReadUnlock(taskName);
			}
			if (!updated) {
				if (idleCounter++ == 20) {
					this.skyLight.cleanUp();
					this.skyLight.reportChanges();
					this.blockLight.cleanUp();
					this.blockLight.reportChanges();
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException ex) {}
			}
		}
	}
}
