/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
package org.spout.api.util.map.concurrent.palette;

import java.util.concurrent.atomic.AtomicInteger;

import gnu.trove.set.hash.TIntHashSet;

import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.math.IntVector3;
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
	private final int[] newState;
	private final int[] oldState;
	private final AtomicInteger maxX = new AtomicInteger();
	private final AtomicInteger maxY = new AtomicInteger();
	private final AtomicInteger maxZ = new AtomicInteger();
	private final AtomicInteger minX = new AtomicInteger();
	private final AtomicInteger minY = new AtomicInteger();
	private final AtomicInteger minZ = new AtomicInteger();
	private final AtomicInteger dirtyBlocks = new AtomicInteger(0);

	public AtomicPaletteBlockStore(int shift, boolean storeState, boolean compress) {
		this(shift, storeState, compress, 10);
	}

	public AtomicPaletteBlockStore(int shift, boolean storeState, boolean compress, short[] initial) {
		this(shift, storeState, compress, 10, initial);
	}

	public AtomicPaletteBlockStore(int shift, boolean storeState, boolean compress, int dirtySize) {
		this.side = 1 << shift;
		this.shift = shift;
		this.doubleShift = shift << 1;
		int size = side * side * side;
		store = new AtomicShortIntArray(size);
		this.length = size;
		dirtyX = new byte[dirtySize];
		dirtyY = new byte[dirtySize];
		dirtyZ = new byte[dirtySize];
		if (storeState) {
			oldState = new int[dirtySize];
			newState = new int[dirtySize];
		} else {
			oldState = null;
			newState = null;
		}
	}

	public AtomicPaletteBlockStore(int shift, boolean storeState, boolean compress, int dirtySize, short[] initial) {
		this(shift, storeState, compress, dirtySize, initial, null);
	}

	public AtomicPaletteBlockStore(int shift, boolean storeState, boolean compress, int dirtySize, short[] blocks, short[] data) {
		this(shift, storeState, compress, dirtySize);
		if (blocks != null) {
			int[] initial = new int[Math.min(blocks.length, this.length)];
			for (int i = 0; i < blocks.length; i++) {
				short d = data != null ? data[i] : 0;
				initial[i] = BlockFullState.getPacked(blocks[i], d);
			}
			if (compress) {
				store.set(initial);
			} else {
				store.uncompressedSet(initial);
			}
		}
	}

	public AtomicPaletteBlockStore(int shift, boolean storeState, boolean compress, int dirtySize, int[] palette, int blockArrayWidth, int[] variableWidthBlockArray) {
		this(shift, storeState, compress, dirtySize);
		if (!compress) {
			throw new IllegalArgumentException("Cannot disable compression when loading from palette");
		}
		store.set(palette, blockArrayWidth, variableWidthBlockArray);
	}

	@Override
	public int getFullData(int x, int y, int z) {
		return getFullData(getIndex(x, y, z));
	}

	@Override
	public int getFullData(int index) {
		return store.get(index);
	}

	@Override
	public int getAndSetBlock(int x, int y, int z, short id, short data) {
		int newState = BlockFullState.getPacked(id, data);
		int oldState = 0;
		try {
			return oldState = store.set(getIndex(x, y, z), newState);
		} finally {
			markDirty(x, y, z, oldState, newState);
		}
	}

	@Override
	public int getAndSetBlock(int x, int y, int z, BlockMaterial m) {
		return getAndSetBlock(x, y, z, m.getId(), m.getData());
	}

	@Override
	public int touchBlock(int x, int y, int z) {
		int state = getFullData(x, y, z);
		markDirty(x, y, z, state, state);
		return state;
	}

	@Override
	public void setBlock(int x, int y, int z, short id, short data) {
		getAndSetBlock(x, y, z, id, data);
	}

	@Override
	public void setBlock(int x, int y, int z, BlockMaterial material) {
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
		boolean success = store.compareAndSet(getIndex(x, y, z), exp, update);
		if (success && exp != update) {
			markDirty(x, y, z, exp, update);
		}
		return success;
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
		minX.set(Integer.MAX_VALUE);
		minY.set(Integer.MAX_VALUE);
		minZ.set(Integer.MAX_VALUE);
		maxX.set(Integer.MIN_VALUE);
		maxY.set(Integer.MIN_VALUE);
		maxZ.set(Integer.MIN_VALUE);
		return dirtyBlocks.getAndSet(0) > 0;
	}

	@Override
	public int getDirtyBlocks() {
		return dirtyBlocks.get();
	}

	@Override
	public IntVector3 getMaxDirty() {
		return new IntVector3(maxX.get(), maxY.get(), maxZ.get());
	}

	@Override
	public IntVector3 getMinDirty() {
		return new IntVector3(minX.get(), minY.get(), minZ.get());
	}

	@Override
	public Vector3 getDirtyBlock(int i) {
		if (i >= dirtyBlocks.get()) {
			return null;
		}

		return new Vector3(dirtyX[i] & 0xFF, dirtyY[i] & 0xFF, dirtyZ[i] & 0xFF);
	}

	@Override
	public int getDirtyOldState(int i) {
		if (oldState == null || i >= dirtyBlocks.get()) {
			return -1;
		}

		return oldState[i];
	}

	@Override
	public int getDirtyNewState(int i) {
		if (newState == null || i >= dirtyBlocks.get()) {
			return -1;
		}

		return newState[i];
	}

	public void markDirty(int x, int y, int z, int oldState, int newState) {
		setAsMax(maxX, x);
		setAsMin(minX, x);

		setAsMax(maxY, y);
		setAsMin(minY, y);

		setAsMax(maxZ, z);
		setAsMin(minZ, z);

		int index = incrementDirtyIndex();
		if (index < dirtyX.length) {
			dirtyX[index] = (byte) x;
			dirtyY[index] = (byte) y;
			dirtyZ[index] = (byte) z;
			if (this.oldState != null) {
				this.oldState[index] = oldState;
				this.newState[index] = newState;
			}
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

	private int getIndex(int x, int y, int z) {
		return (y << doubleShift) + (z << shift) + x;
	}

	@Override
	public int getPackedWidth() {
		return store.width();
	}

	@Override
	public int[] getPackedArray() {
		return store.getBackingArray();
	}

	@Override
	public int[] getPalette() {
		return store.getPalette();
	}

	@Override
	public void writeLock() {
		store.lock();
	}

	@Override
	public void writeUnlock() {
		store.unlock();
	}

	@Override
	public boolean tryWriteLock() {
		return store.tryLock();
	}

	@Override
	public boolean isBlockUniform() {
		return store.isUniform();
	}

	private void setAsMin(AtomicInteger i, int x) {
		int old;
		while ((old = i.get()) > x) {
			if (i.compareAndSet(old, x)) {
				return;
			}
		}
	}

	private void setAsMax(AtomicInteger i, int x) {
		int old;
		while ((old = i.get()) < x) {
			if (i.compareAndSet(old, x)) {
				return;
			}
		}
	}
}
