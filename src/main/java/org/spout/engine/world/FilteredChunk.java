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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Source;
import org.spout.api.datatable.ManagedHashMap;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.ChunkSnapshot.EntityType;
import org.spout.api.geo.cuboid.ChunkSnapshot.ExtraData;
import org.spout.api.geo.cuboid.ChunkSnapshot.SnapshotType;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.math.Vector3;
import org.spout.api.util.map.concurrent.palette.AtomicPaletteBlockStore;
import org.spout.engine.entity.SpoutEntity;

public class FilteredChunk extends SpoutChunk{
	private final AtomicBoolean uniform;
	private final AtomicInteger uniformId = new AtomicInteger(0);
	private final AtomicReference<BlockMaterial> material = new AtomicReference<BlockMaterial>(null);
	/**
	 * Keeps track if the chunk has been modified since it's last save
	 */
	private final AtomicBoolean chunkModified = new AtomicBoolean(false);

	private final AtomicBoolean entitiesModified = new AtomicBoolean(false);

	protected final static byte[] DARK = new byte[BLOCKS.HALF_VOLUME];
	protected final static byte[] LIGHT = new byte[BLOCKS.HALF_VOLUME];

	static {
		Arrays.fill(LIGHT, (byte) 255);
	}

	public FilteredChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, short[] initial, ManagedHashMap map) {
		this(world, region, x, y, z, PopulationState.UNTOUCHED, initial, null, null, null, map);
		chunkModified.set(true);
	}

	public FilteredChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, PopulationState popState, short[] blocks, short[] data, byte[] skyLight, byte[] blockLight, ManagedHashMap extraData) {
		super(world, region, x, y, z, popState, blocks, data, skyLight, blockLight, extraData);

		uniform = new AtomicBoolean(true);
		short id = blocks[0];
		for (int i = 1; i < blocks.length; i++) {
			if (id != blocks[i]) {
				uniform.set(false);
				break;
			}
		}

		if (uniform.get()) {
			uniformId.set(id);
			material.set(BlockMaterial.get(id));
			this.blockStore = null;
			this.skyLight = null;
			this.blockLight = null;
		}
	}

	private synchronized void initialize() {
		if (uniform.get()) {
			short[] initial = new short[BLOCKS.VOLUME];
			short id = (short)uniformId.get();
			for (int i = 0; i < initial.length; i++) {
				initial[i] = id;
			}
			this.blockStore = new AtomicPaletteBlockStore(BLOCKS.BITS, 10, initial);
			
			this.skyLight = new byte[BLOCKS.HALF_VOLUME];
			System.arraycopy(this.isAboveGround() ? LIGHT : DARK, 0, this.skyLight, 0, this.skyLight.length);
			
			this.blockLight = new byte[BLOCKS.HALF_VOLUME];
			System.arraycopy(DARK, 0, this.blockLight, 0, this.blockLight.length);
			
			uniform.set(false);
		}
	}

	private void setModified() {
		if (chunkModified.compareAndSet(false, true)) {
			setAutosaveTicks(20 * 60 * 5);
		}
	}

	public boolean isUniform() {
		return uniform.get();
	}

	public boolean isAboveGround() {
		return this.getY() >= 4;
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Source source) {
		if (uniform.get()) {
			initialize();
		}
		boolean changed = super.setBlockMaterial(x, y, z, material, data, source);
		if (changed) {
			setModified();
		}
		return changed;
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		if (uniform.get()) {
			return material.get();
		}
		return super.getBlockMaterial(x, y, z);
	}
	
	@Override
	public int getBlockFullState(int x, int y, int z) {
		if (uniform.get()) {
			Material m = material.get();
			return BlockFullState.getPacked(m.getId(), m.getData());
		}
		return super.getBlockFullState(x, y, z);
	}

	@Override
	public short getBlockData(int x, int y, int z) {
		if (uniform.get()) {
			return material.get().getData();
		}
		return super.getBlockData(x, y, z);
	}
	
	@Override
	public int getBlockDataField(int bx, int by, int bz, int bits) {
		if (uniform.get()) {
			int shift = shiftCache[bits];
			short data = material.get().getData();
			return (data & bits) >> (shift);
		}
		return super.getBlockDataField(bx, by, bz, bits);
	}

	@Override
	protected int setBlockDataFieldRaw(int bx, int by, int bz, int bits, int value, Source source) {
		if (uniform.get()) {
			initialize();
		}
		setModified();
		return super.setBlockDataFieldRaw(bx, by, bz, bits, value, source);
	}

	@Override
	protected int addBlockDataFieldRaw(int bx, int by, int bz, int bits, int value, Source source) {
		if (uniform.get()) {
			initialize();
		}
		setModified();
		return super.addBlockDataFieldRaw(bx, by, bz, bits, value, source);
	}

	@Override
	public boolean compareAndSetData(int x, int y, int z, int expect, short data, Source source) {
		if (uniform.get()) {
			Material m = material.get();
			if (m.getId() == BlockFullState.getId(expect) && m.getData() == BlockFullState.getData(expect)) {
				initialize();
			} else {
				return false;
			}
		}
		setModified();
		return super.compareAndSetData(x, y, z, expect, data, source);
	}

	@Override
	public boolean setBlockLight(int x, int y, int z, byte light, Source source) {
		if (uniform.get()) {
			return false;
		}
		boolean changed = super.setBlockLight(x, y, z, light, source);
		if (changed) {
			setModified();
		}
		return changed;
	}

	@Override
	public boolean setBlockSkyLight(int x, int y, int z, byte light, Source source) {
		if (uniform.get()) {
			return false;
		}
		boolean changed = super.setBlockSkyLight(x, y, z, light, source);
		if (changed) {
			setModified();
		}
		return changed;
	}

	@Override
	public byte getBlockSkyLight(int x, int y, int z) {
		if (uniform.get()) {
			return this.isAboveGround() ? getWorld().getSkyLight() : (byte) 0x0;
		}
		return super.getBlockSkyLight(x, y, z);
	}

	@Override
	public byte getBlockSkyLightRaw(int x, int y, int z) {
		if (uniform.get()) {
			return this.isAboveGround() ? (byte) 0xF : (byte) 0x0;
		}
		return super.getBlockSkyLightRaw(x, y, z);
	}

	@Override
	public byte getBlockLight(int x, int y, int z) {
		if (uniform.get()) {
			return material.get().getLightLevel((short) 0);
		}
		return super.getBlockLight(x, y, z);
	}

	@Override
	public void initLighting() {
		if (!uniform.get()) {
			super.initLighting();
		}
	}

	@Override
	public void syncSave() {
		if (this.chunkModified.get() || entitiesModified.get() || this.hasEntities()) {
			chunkModified.set(false);
			entitiesModified.set(false);
			super.syncSave();
		} else {
			super.saveComplete();
		}
	}

	@Override
	public ChunkSnapshot getSnapshot(SnapshotType type, EntityType entities, ExtraData data) {
		if (uniform.get()) {
			byte[] blockLightCopy = null, skyLightCopy = null;
			short[] blockIds = null, blockData = null;
			switch(type) {
				case NO_BLOCK_DATA: break;
				case BLOCK_IDS_ONLY: 
					blockIds = uniformBlockIds();
					break;
				case BLOCKS_ONLY: 
					blockIds = uniformBlockIds();
					blockData = new short[BLOCKS.VOLUME];
					break;
				case LIGHT_ONLY: 
					blockLightCopy = new byte[BLOCKS.VOLUME / 2];
					System.arraycopy(DARK, 0, blockLightCopy, 0, blockLightCopy.length);
					skyLightCopy = new byte[BLOCKS.VOLUME / 2];
					System.arraycopy(this.getY() < 4 ? DARK : LIGHT, 0, skyLightCopy, 0, skyLightCopy.length);
					break;
				case BOTH: 
					blockIds = uniformBlockIds();
					blockData = new short[BLOCKS.VOLUME];
					
					blockLightCopy = new byte[BLOCKS.VOLUME / 2];
					System.arraycopy(DARK, 0, blockLightCopy, 0, blockLightCopy.length);
					skyLightCopy = new byte[BLOCKS.VOLUME / 2];
					System.arraycopy(this.getY() < 4 ? DARK : LIGHT, 0, skyLightCopy, 0, skyLightCopy.length);
					break;
			}
			
			return new SpoutChunkSnapshot(this, blockIds, blockData, blockLightCopy, skyLightCopy, entities, data);
		}
		return super.getSnapshot(type, entities, data);
	}
	
	private short[] uniformBlockIds() {
		short[] initial = new short[BLOCKS.VOLUME];
		short id = (short)uniformId.get();
		for (int i = 0; i < initial.length; i++) {
			initial[i] = id;
		}
		return initial;
	}

	@Override
	public boolean compressIfRequired() {
		if (uniform.get()) {
			return false;
		}
		return super.compressIfRequired();
	}
	
	@Override
	public boolean isDirty() {
		if (uniform.get()) {
			return false;
		}
		return super.isDirty();
	}

	@Override
	public boolean isDirtyOverflow() {
		if (uniform.get()) {
			return false;
		}
		return super.isDirtyOverflow();
	}

	@Override
	protected Vector3 getDirtyBlock(int i) {
		if (uniform.get()) {
			return null;
		}
		return super.getDirtyBlock(i);
	}

	@Override
	public void resetDirtyArrays() {
		if (!uniform.get()) {
			super.resetDirtyArrays();
		}
	}

	@Override
	public void onEntityEnter(SpoutEntity e) {
		entitiesModified.compareAndSet(false, true);
	}

	@Override
	public void onEntityLeave(SpoutEntity e) {
		entitiesModified.compareAndSet(false, true);
	}
}
