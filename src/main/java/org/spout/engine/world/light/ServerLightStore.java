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
package org.spout.engine.world.light;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.event.Cause;
import org.spout.api.geo.AreaBlockSource;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot.EntityType;
import org.spout.api.geo.cuboid.ChunkSnapshot.ExtraData;
import org.spout.api.geo.cuboid.ChunkSnapshot.SnapshotType;
import org.spout.api.util.set.TNibbleQuadHashSet;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutColumn;
import org.spout.engine.world.SpoutWorldLighting;
import org.spout.engine.world.SpoutWorldLightingModel;

public final class ServerLightStore extends LightStore{
	/**
	 * If -1, there are no changes. If higher, there are changes and the number
	 * denotes how many ticks these have been there.<br> Every time a change is
	 * committed the value is set to 0. The region will increment it as well.
	 */
	public final AtomicInteger lightingCounter = new AtomicInteger(-1);

	public static final int BLOCK_UPDATES = 1 << 0;
	public static final int SKY_UPDATES = 1 << 1;
	public static final int BLOCK_OPERATIONS = 1 << 2;
	public static final int SKY_OPERATIONS = 1 << 3;
	/**
	 * If there are pending light operations for this chunk, marked with any combination of the 4 above flags
	 */
	protected final AtomicInteger lightOperationsPending = new AtomicInteger(0);
	/**
	 * Contains the pending block light operations of blocks in this chunk
	 */
	public final TNibbleQuadHashSet blockLightOperations = new TNibbleQuadHashSet();
	/**
	 * Contains the pending sky light operations of blocks in this chunk
	 */
	public final TNibbleQuadHashSet skyLightOperations = new TNibbleQuadHashSet();
	/**
	 * Contains the pending block light updates of blocks in this chunk
	 */
	public final TNibbleQuadHashSet blockLightUpdates = new TNibbleQuadHashSet();
	/**
	 * Contains the pending sky light updates of blocks in this chunk
	 */
	public final TNibbleQuadHashSet skyLightUpdates = new TNibbleQuadHashSet();

	/**
	 * This is always 'this', it is changed to a snapshot of the chunk in initLighting()
	 * Do NOT set this to something else or use it elsewhere but in initLighting()
	 */
	protected volatile AreaBlockSource lightBlockSource;

	private AtomicBoolean registeredWithLightingManager = new AtomicBoolean(false);

	/**
	 * True if this chunk is initializing lighting, False if not
	 */
	public final AtomicBoolean isInitializingLighting = new AtomicBoolean(false);
	public ServerLightStore(SpoutChunk chunk, SpoutColumn column, byte[] skyLight, byte[] blockLight) {
		super(chunk, column, skyLight, blockLight);
		this.lightBlockSource = chunk;
	}

	@Override
	public boolean isCalculatingLighting() {
		return lightOperationsPending.get() != 0;
	}

	public void submitPendingLightOperation(int bitflag) {
		int old;
		do {
			old = lightOperationsPending.get();
		} while(!lightOperationsPending.compareAndSet(old, old | bitflag));
	}

	public void clearPendingLightOperation(int bitflag) {
		int old;
		do {
			old = lightOperationsPending.get();
		} while(!lightOperationsPending.compareAndSet(old, old & ~bitflag));
	}

	public void addSkyLightOperation(int x, int y, int z, int operation) {
		SpoutWorldLightingModel model = getChunk().getWorld().getLightingManager().getSkyModel();
		if (operation == SpoutWorldLighting.REFRESH) {
			if (!model.canRefresh(this.lightBlockSource, x, y, z)) {
				return;
			}
		} else if (operation == SpoutWorldLighting.GREATER) {
			if (!model.canGreater(this.lightBlockSource, x, y, z)) {
				return;
			}
		}

		synchronized (this.skyLightOperations) {
			this.skyLightOperations.add(x & Chunk.BLOCKS.MASK, y & Chunk.BLOCKS.MASK, z & Chunk.BLOCKS.MASK, operation);
			submitPendingLightOperation(SKY_OPERATIONS);
		}
		registerWithLightingManager();
	}

	public void addBlockLightOperation(int x, int y, int z, int operation) {
		SpoutWorldLightingModel model = getChunk().getWorld().getLightingManager().getBlockModel();
		if (operation == SpoutWorldLighting.REFRESH) {
			if (!model.canRefresh(this.lightBlockSource, x, y, z)) {
				return;
			}
		} else if (operation == SpoutWorldLighting.GREATER) {
			if (!model.canGreater(this.lightBlockSource, x, y, z)) {
				return;
			}
		}

		synchronized (this.blockLightOperations) {
			this.blockLightOperations.add(x & Chunk.BLOCKS.MASK, y & Chunk.BLOCKS.MASK, z & Chunk.BLOCKS.MASK, operation);
			submitPendingLightOperation(BLOCK_OPERATIONS);
		}
		registerWithLightingManager();
	}

	public void addSkyLightUpdates(int x, int y, int z, int level) {
		synchronized (this.skyLightUpdates) {
			this.skyLightUpdates.add(x & Chunk.BLOCKS.MASK, y & Chunk.BLOCKS.MASK, z & Chunk.BLOCKS.MASK, level);
			submitPendingLightOperation(SKY_UPDATES);
		}
		registerWithLightingManager();
	}

	public void addBlockLightUpdates(int x, int y, int z, int level) {
		synchronized (this.blockLightUpdates) {
			this.blockLightUpdates.add(x & Chunk.BLOCKS.MASK, y & Chunk.BLOCKS.MASK, z & Chunk.BLOCKS.MASK, level);
			submitPendingLightOperation(BLOCK_UPDATES);
		}
		registerWithLightingManager();
	}

	public void initLighting() {
		this.isInitializingLighting.set(true);
		this.notifyLightChange();
		int x, y, z, minY, maxY, columnY;
		// Lock operations to prevent premature handling
		Arrays.fill(this.blockLight, (byte) 0);
		Arrays.fill(this.skyLight, (byte) 0);

		// Initialize block lighting
		this.lightBlockSource = getChunk().getSnapshot(SnapshotType.BLOCKS_ONLY, EntityType.NO_ENTITIES, ExtraData.NO_EXTRA_DATA);
		for (x = 0; x < Chunk.BLOCKS.SIZE; x++) {
			for (y = 0; y < Chunk.BLOCKS.SIZE; y++) {
				for (z = 0; z < Chunk.BLOCKS.SIZE; z++) {
					this.setBlockLight(x, y, z, this.lightBlockSource.getBlockMaterial(x, y, z).getLightLevel(this.lightBlockSource.getBlockData(x, y, z)), null);
				}
			}
		}

		// Report the columns that require a sky-light update
		minY = getChunk().getBlockY();
		maxY = minY + Chunk.BLOCKS.SIZE;
		for (x = 0; x < Chunk.BLOCKS.SIZE; x++) {
			for (z = 0; z < Chunk.BLOCKS.SIZE; z++) {
				columnY = getColumn().getSurfaceHeight(x, z) + 1;
				if (columnY < minY) {
					// everything is air - ignore refresh checks
					for (y = 0; y < Chunk.BLOCKS.SIZE; y++) {
						this.addSkyLightUpdates(x, y, z, 15);
					}
				} else {
					// fill area above height with light
					for (y = columnY; y < maxY; y++) {
						this.addSkyLightUpdates(x, y, z, 15);
					}

					if (x == 0 || x == 15 || z == 0 || z == 15) {
						// refresh area below height at the edges
						for (y = columnY; y >= minY; y--) {
							this.addSkyLightOperation(x, y, z, SpoutWorldLighting.REFRESH);
						}
					} else {
						// Refresh top and bottom blocks
						this.addSkyLightOperation(x, 0, z, SpoutWorldLighting.REFRESH);
						if (columnY >= maxY) {
							this.addSkyLightOperation(x, 15, z, SpoutWorldLighting.REFRESH);
						}
					}
				}
			}
		}
		this.isInitializingLighting.set(false);
		this.lightBlockSource = getChunk(); // stop using the snapshot from now on
		this.registerWithLightingManager();
	}
	
	public void registerWithLightingManager() {
		if (registeredWithLightingManager.compareAndSet(false, true)) {
			getChunk().getWorld().getLightingManager().addChunk(getChunk().getX(), getChunk().getY(), getChunk().getZ());
		}
	}

	public void clearRegisteredWithLightingManager() {
		registeredWithLightingManager.set(false);
	}

	private void notifyLightChange() {
		if (this.lightingCounter.getAndSet(0) == -1) {
			getChunk().getRegion().reportChunkLightDirty(getChunk().getX(), getChunk().getY(), getChunk().getZ());
		}
	}

	@Override
	public boolean setBlockLight(int x, int y, int z, byte light, Cause<?> cause) {
		addBlockLightUpdates(x, y, z, light);
		return true;
	}

	@Override
	public byte setBlockLightSync(int x, int y, int z, byte light, Cause<?> cause) {
		byte oldLight = super.setBlockLightSync(x, y, z, light, cause);
		if (light > oldLight) {
			// light increased
			this.addBlockLightOperation(x, y, z, SpoutWorldLighting.GREATER);
		} else if (light < oldLight) {
			// light decreased
			this.addBlockLightOperation(x, y, z, SpoutWorldLighting.LESSER);
		}
		this.notifyLightChange();
		return oldLight;
	}

	@Override
	public boolean setSkyLight(int x, int y, int z, byte light, Cause<?> cause) {
		addSkyLightUpdates(x, y, z, light);
		return true;
	}

	@Override
	public byte setSkyLightSync(int x, int y, int z, byte light, Cause<?> cause) {
		byte oldLight = super.setSkyLightSync(x, y, z, light, cause);
		if (light > oldLight) {
			// light increased
			this.addSkyLightOperation(x, y, z, SpoutWorldLighting.GREATER);
		} else if (light < oldLight) {
			// light decreased
			this.addSkyLightOperation(x, y, z, SpoutWorldLighting.LESSER);
		}
		this.notifyLightChange();
		return oldLight;
	}
}