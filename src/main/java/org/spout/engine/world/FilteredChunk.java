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
import org.spout.api.datatable.DataMap;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.Vector3;
import org.spout.api.util.map.concurrent.AtomicBlockStoreImpl;
import org.spout.engine.filesystem.WorldFiles;

public class FilteredChunk extends SpoutChunk{
	private final AtomicBoolean uniform;
	private final AtomicInteger uniformId = new AtomicInteger(0);
	private final AtomicReference<BlockMaterial> material = new AtomicReference<BlockMaterial>(null);

	protected final static byte[] DARK = new byte[BLOCKS.HALF_VOLUME];
	protected final static byte[] LIGHT = new byte[BLOCKS.HALF_VOLUME];

	static {
		Arrays.fill(LIGHT, (byte) 255);
	}

	public FilteredChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, short[] initial, BiomeManager manager, DataMap map) {
		this(world, region, x, y, z, false, initial, null, null, null, manager, map.getRawMap());
	}

	public FilteredChunk(SpoutWorld world, SpoutRegion region, float x, float y, float z, boolean populated, short[] blocks, short[] data, byte[] skyLight, byte[] blockLight, BiomeManager manager, DatatableMap extraData) {
		super(world, region, x, y, z, populated, blocks, data, skyLight, blockLight, manager, extraData);

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
			this.blockStore = new AtomicBlockStoreImpl(BLOCKS.BITS, 10, initial);
			
			this.skyLight = new byte[BLOCKS.HALF_VOLUME];
			System.arraycopy(this.isAboveGround() ? LIGHT : DARK, 0, this.skyLight, 0, this.skyLight.length);
			
			this.blockLight = new byte[BLOCKS.HALF_VOLUME];
			System.arraycopy(DARK, 0, this.blockLight, 0, this.blockLight.length);
			
			uniform.set(false);
		}
	}

	public boolean isUniform() {
		return uniform.get();
	}

	public boolean isAboveGround() {
		return this.getY() >= 4;
	}
	
	@Override
	public boolean setBlockData(int x, int y, int z, short data, Source source) {
		if (uniform.get()) {
			initialize();
		}
		return super.setBlockData(x, y, z, data, source);
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Source source) {
		if (uniform.get()) {
			initialize();
		}
		return super.setBlockMaterial(x, y, z, material, data, source);
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		if (uniform.get()) {
			return material.get();
		}
		return super.getBlockMaterial(x, y, z);
	}

	@Override
	public short getBlockData(int x, int y, int z) {
		if (uniform.get()) {
			return material.get().getData();
		}
		return super.getBlockData(x, y, z);
	}

	@Override
	public boolean compareAndSetData(int x, int y, int z, int expect, short data, Source source) {
		if (uniform.get()) {
			initialize();
		}
		return super.compareAndSetData(x, y, z, expect, data, source);
	}

	@Override
	public boolean setBlockLight(int x, int y, int z, byte light, Source source) {
		if (uniform.get()) {
			return false;
		}
		return super.setBlockLight(x, y, z, light, source);
	}

	@Override
	public boolean setBlockSkyLight(int x, int y, int z, byte light, Source source) {
		if (uniform.get()) {
			return false;
		}
		return super.setBlockSkyLight(x, y, z, light, source);
	}

	@Override
	public byte getBlockSkyLight(int x, int y, int z) {
		if (uniform.get()) {
			return this.isAboveGround() ? (byte) 0xF : (byte) 0x0;
		}
		return super.getBlockSkyLight(x, y, z);
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
		if (uniform.get()) {
			short[] initial = new short[BLOCKS.VOLUME];
			short id = (short)uniformId.get();
			for (int i = 0; i < initial.length; i++) {
				initial[i] = id;
			}
			WorldFiles.saveChunk(this, initial, new short[BLOCKS.VOLUME], this.getY() < 4 ? DARK : LIGHT, DARK, datatableMap, this.parentRegion.getChunkOutputStream(this));
		} else {
			super.syncSave();
		}
	}

	@Override
	public ChunkSnapshot getSnapshot(boolean entities) {
		if (uniform.get()) {
			short[] initial = new short[BLOCKS.VOLUME];
			short id = (short)uniformId.get();
			for (int i = 0; i < initial.length; i++) {
				initial[i] = id;
			}

			byte[] skyLight = new byte[BLOCKS.HALF_VOLUME];
			System.arraycopy(this.getY() < 4 ? DARK : LIGHT, 0, skyLight, 0, skyLight.length);

			byte[] blockLight = new byte[BLOCKS.HALF_VOLUME];
			System.arraycopy(DARK, 0, blockLight, 0, blockLight.length);
			return new SpoutChunkSnapshot(this, initial, new short[BLOCKS.VOLUME], blockLight, skyLight, entities);
		}
		return super.getSnapshot(entities);
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
}
