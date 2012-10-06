/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.util.map.concurrent.palette;

import gnu.trove.set.hash.TIntHashSet;

import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.material.Material;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.material.source.MaterialSource;
import org.spout.api.math.Vector3;
import org.spout.api.util.map.concurrent.AtomicBlockStore;

public class AtomicPaletteBlockStore implements AtomicBlockStore {
	
	private final int side;
	private final int shift;
	private final int doubleShift;
	private final int length;
	private final AtomicShortIntArray store;
	private final byte[] dirtyX;
	private final byte[] dirtyY;
	private final byte[] dirtyZ;
	private final AtomicInteger dirtyBlocks = new AtomicInteger(0);
	
	public AtomicPaletteBlockStore(int shift) {
		this(shift, 10);
	}
	
	public AtomicPaletteBlockStore(int shift, short[] initial) {
		this(shift, 10, initial);
	}
	
	public AtomicPaletteBlockStore(int shift, int dirtySize) {
		this(shift, dirtySize, null);
	}

	public AtomicPaletteBlockStore(int shift, int dirtySize, short[] initial) {
		this(shift, dirtySize, initial, null);
	}
	
	public AtomicPaletteBlockStore(int shift, int dirtySize, short[] blocks, short[] data) {
		this.side = 1 << shift;
		this.shift = shift;
		this.doubleShift = shift << 1;
		int size = side * side * side;
		store = new AtomicShortIntArray(size);
		this.length = size;
		dirtyX = new byte[dirtySize];
		dirtyY = new byte[dirtySize];
		dirtyZ = new byte[dirtySize];
		if (blocks != null && data != null) {
			int x = 0;
			int z = 0;
			int y = 0;
			int max = (1 << shift) - 1;

			for (int i = 0; i < Math.min(blocks.length, size); i++) {
				this.setBlock(x, y, z, blocks[i], data[i]);

				if (x < max) {
					x++;
				} else {
					x = 0;
					if (z < max) {
						z++;
					} else {
						z = 0;
						if (y < max) {
							y++;
						} else {
							y = 0;
						}
					}
				}
			}
		}
	}
	
	@Override
	public int getFullData(int x, int y, int z) {
		return store.get(getIndex(x, y, z));
	}
	
	@Override
	public int getAndSetBlock(int x, int y, int z, short id, short data) {
		return store.set(getIndex(x, y, z), BlockFullState.getPacked(id, data));
	}
	
	@Override
	public int getAndSetBlock(int x, int y, int z, MaterialSource material) {
		Material m = material.getMaterial();
		return getAndSetBlock(x, y, z, m.getId(), m.getData());
	}
	
	@Override
	public void setBlock(int x, int y, int z, short id, short data) {
		getAndSetBlock(x, y, z, id, data);
	}
	
	@Override
	public void setBlock(int x, int y, int z, MaterialSource material) {
		getAndSetBlock(x, y, z, material);
	}

	@Override
	public int getBlockId(int x, int y, int z) {
		return BlockFullState.getId(getFullData(x, y, z));
	}

	@Override
	public int getData(int x, int y, int z) {
		return BlockFullState.getData(getFullData(x, y, z));
	}

	@Override
	public boolean compareAndSetBlock(int x, int y, int z, short expectId, short expectData, short newId, short newData) {
		int exp = BlockFullState.getPacked(expectId, expectData);
		int update = BlockFullState.getPacked(newId, newData);
		return store.compareAndSet(getIndex(x, y, z), exp, update);
	}

	@Override
	public boolean needsCompression() {
		// TODO - needs removal or optimisation
		return true;
	}

	@Override
	public short[] getBlockIdArray() {
		return getBlockIdArray(new short[length]);
	}

	@Override
	public short[] getBlockIdArray(short[] array) {
		if (array.length != length) {
			array = new short[length];
		}
		for (int i = 0; i < length; i++) {
			array[i] = BlockFullState.getId(store.get(i));
		}
		return array;
	}

	@Override
	public short[] getDataArray() {
		return getDataArray(new short[length]);
	}

	@Override
	public short[] getDataArray(short[] array) {
		if (array.length != length) {
			array = new short[length];
		}
		for (int i = 0; i < length; i++) {
			array[i] = BlockFullState.getData(store.get(i));
		}
		return array;
	}

	@Override
	public void compress() {
		compress(new TIntHashSet());
	} 
	
	@Override
	public void compress(TIntHashSet inUseSet) {
		store.compress(inUseSet);
	}

	@Override
	public boolean isDirtyOverflow() {
		return dirtyBlocks.get() >= dirtyX.length;
	}

	@Override
	public boolean isDirty() {
		return dirtyBlocks.get() > 0;
	}

	@Override
	public boolean resetDirtyArrays() {
		return dirtyBlocks.getAndSet(0) > 0;
	}

	@Override
	public Vector3 getDirtyBlock(int i) {
		if (i >= dirtyBlocks.get()) {
			return null;
		}

		return new Vector3(dirtyX[i] & 0xFF, dirtyY[i] & 0xFF, dirtyZ[i] & 0xFF);
	}

	@Override
	public void markDirty(int x, int y, int z) {
		int index = incrementDirtyIndex();
		if (index < dirtyX.length) {
			dirtyX[index] = (byte) x;
			dirtyY[index] = (byte) y;
			dirtyZ[index] = (byte) z;
		}
	}
	
	public int incrementDirtyIndex() {
		boolean success = false;
		int index = -1;
		while (!success) {
			index = dirtyBlocks.get();
			if (index > dirtyX.length) {
				break;
			}
			int next = index + 1;
			success = dirtyBlocks.compareAndSet(index, next);
		}
		return index;
	}
	
	private final int getIndex(int x, int y, int z) {
		return (y << doubleShift) + (z << shift) + x;
	}

}
