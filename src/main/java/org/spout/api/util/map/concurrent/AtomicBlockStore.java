/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.util.map.concurrent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.datatable.DatatableSequenceNumber;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.material.source.MaterialSource;

/**
 * This store stores block data for each chunk. Each block can either store a
 * short id, or a short id, a short data value and a reference to a &lt;T&gt;
 * object.
 */
public class AtomicBlockStore<T> {
	private final int side;
	private final int shift;
	private final int doubleShift;
	private final AtomicShortArray blockIds;
	private AtomicIntReferenceArrayStore<T> auxStore; //TODO: Replace with AtomicShortArray!
	private final AtomicBoolean compressing = new AtomicBoolean(false);
	private final byte[] dirtyX;
	private final byte[] dirtyY;
	private final byte[] dirtyZ;
	private final AtomicInteger dirtyBlocks = new AtomicInteger(0);
	private final AtomicInteger waiting = new AtomicInteger(0);
	private final int SPINS = 10;

	public AtomicBlockStore(int shift) {
		this(shift, 10);
	}
	
	public AtomicBlockStore(int shift, short[] initial) {
		this(shift, 10, initial);
	}
	
	public AtomicBlockStore(int shift, int dirtySize) {
		this(shift, dirtySize, null);
	}

	public AtomicBlockStore(int shift, int dirtySize, short[] initial) {
		this(shift, dirtySize, initial, null);
	}
	
	public AtomicBlockStore(int shift, int dirtySize, short[] blocks, short[] data) {
		this(shift, dirtySize, blocks, data, null);
	}

	public AtomicBlockStore(int shift, int dirtySize, short[] blocks, short[] data, T[] auxData) {
		this.side = 1 << shift;
		this.shift = shift;
		this.doubleShift = shift << 1;
		int size = side * side * side;
		blockIds = new AtomicShortArray(size);
		auxStore = new AtomicIntReferenceArrayStore<T>(size);
		dirtyX = new byte[dirtySize];
		dirtyY = new byte[dirtySize];
		dirtyZ = new byte[dirtySize];
		if (blocks != null) {
			int x = 0;
			int z = 0;
			int y = 0;
			int max = (1 << shift) - 1;

			for (int i = 0; i < Math.min(blocks.length, size); i++) {
				short d = 0;
				if(data != null) {
					d = data[i];
				}
				
				this.setBlock(x, y, z, blocks[i], d);
				
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

	/**
	 * Gets the sequence number associated with a block location.<br>
	 * <br>
	 * If soft is true, this method counts as a volatile read. Otherwise, it is
	 * both a volatile read and a volatile write.<br>
	 * <br>
	 * Soft reads should only be used for the first of the 2 step process for
	 * confirming that data hasn't changed.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the sequence number, or DatatableSequenceNumber.ATOMIC for a
	 *         single short record
	 */
	public final int getSequence(int x, int y, int z) {
		checkCompressing();
		int index = getIndex(x, y, z);
		int spins = 0;
		boolean interrupted = false;
		try {
			while (true) {
				if (spins++ > SPINS) {
					interrupted |= atomicWait();
				}
				checkCompressing();

				int blockId = blockIds.get(index);
				if (!auxStore.isReserved(blockId)) {
					return DatatableSequenceNumber.ATOMIC;
				} else {
					int sequence = auxStore.getSequence(blockId);
					if (sequence != DatatableSequenceNumber.UNSTABLE) {
						return sequence;
					}
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Tests if a the sequence number associated with a particular block
	 * location has not changed.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param expected the expected sequence number
	 * @return true if the sequence number has not changed and expected is not
	 *         DatatableSequenceNumber.ATOMIC
	 */
	public final boolean testSequence(int x, int y, int z, int expected) {

		if (expected == DatatableSequenceNumber.ATOMIC) {
			return false;
		}

		checkCompressing();
		int index = getIndex(x, y, z);
		int spins = 0;
		boolean interrupted = false;
		try {
			if (spins++ > SPINS) {
				interrupted |= atomicWait();
			}
			checkCompressing();

			int blockId = blockIds.get(index);
			return auxStore.isReserved(blockId) && auxStore.testSequence(blockId, expected);
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Gets the block id for a block at a particular location.<br>
	 * <br>
	 * Block ids range from 0 to 65535.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the block id
	 */
	public final int getBlockId(int x, int y, int z) {
		int index = getIndex(x, y, z);
		int spins = 0;
		boolean interrupted = false;
		try {
			while (true) {
				if (spins++ > SPINS) {
					interrupted |= atomicWait();
				}
				checkCompressing();

				int seq = getSequence(x, y, z);
				short blockId = blockIds.get(index);
				if (auxStore.isReserved(blockId)) {
					blockId = auxStore.getId(blockId);
					if (testSequence(x, y, z, seq)) {
						return blockId & 0x0000FFFF;
					}
				} else {
					return blockId & 0x0000FFFF;
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Gets the block data for a block at a particular location.<br>
	 * <br>
	 * Block data ranges from 0 to 65535.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the block data
	 */
	public final int getData(int x, int y, int z) {
		int index = getIndex(x, y, z);
		int spins = 0;
		boolean interrupted = false;
		try {
			while (true) {
				if (spins++ > SPINS) {
					interrupted |= atomicWait();
				}
				checkCompressing();

				int seq = getSequence(x, y, z);
				short blockId = blockIds.get(index);
				if (auxStore.isReserved(blockId)) {
					blockId = auxStore.getData(blockId);
					if (testSequence(x, y, z, seq)) {
						return blockId & 0x0000FFFF;
					}
				} else {
					return 0;
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Gets the block auxiliary data for a block at a particular location.<br>
	 * <br>
	 * Block data ranges from 0 to 65535.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the block auxiliary data
	 */
	public final T getAuxData(int x, int y, int z) {
		int index = getIndex(x, y, z);
		int spins = 0;
		boolean interrupted = false;
		try {
			while (true) {
				if (spins++ > SPINS) {
					interrupted |= atomicWait();
				}
				checkCompressing();

				int seq = getSequence(x, y, z);
				short blockId = blockIds.get(index);
				if (auxStore.isReserved(blockId)) {
					T auxData = auxStore.getAuxData(blockId);
					if (testSequence(x, y, z, seq)) {
						return auxData;
					}
				} else {
					return null;
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Atomically gets the full set of data associated with the block.<br>
	 * <br>
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param fullState a BlockFullState object to store the return value, or
	 *            null to generate a new one
	 * @return the full state of the block
	 */
	public final BlockFullState getFullData(int x, int y, int z) {
		return getFullData(x, y, z, null);
	}

	/**
	 * Atomically gets the full set of data associated with the block.<br>
	 * <br>
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param input is a BlockFullState object to store the return value, or
	 *            null to generate a new one
	 * @return the full state of the block
	 */
	public final BlockFullState getFullData(int x, int y, int z, BlockFullState input) {
		if (input == null) {
			input = new BlockFullState();
		}
		int index = getIndex(x, y, z);
		int spins = 0;
		boolean interrupted = false;
		try {
			while (true) {
				if (spins++ > SPINS) {
					interrupted |= atomicWait();
				}
				checkCompressing();

				int seq = getSequence(x, y, z);
				short blockId = blockIds.get(index);
				if (auxStore.isReserved(blockId)) {
					input.setId(auxStore.getId(blockId));
					input.setData(auxStore.getData(blockId));
					if (testSequence(x, y, z, seq)) {
						return input;
					}
				} else {
					input.setId(blockId);
					input.setData((short) 0);
					return input;
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Sets the block id, data and auxData for the block at (x, y, z).<br>
	 * <br>
	 * If the data is 0 and the auxData is null, then the block will be stored
	 * as a single short.<br>
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param fullState the new state of the Block
	 */
	public final void setBlock(int x, int y, int z, MaterialSource material) {
		setBlock(x, y, z, material.getMaterial().getId(), material.getData());
	}

	/**
	 * Sets the block id, data and auxData for the block at (x, y, z).<br>
	 * <br>
	 * If the data is 0 and the auxData is null, then the block will be stored
	 * as a single short.<br>
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param id the block id
	 * @param data the block data
	 * @param auxData the block auxiliary data
	 */
	public final void setBlock(int x, int y, int z, short id, short data) {
		int index = getIndex(x, y, z);
		int spins = 0;
		boolean interrupted = false;
		try {
			while (true) {
				if (spins++ > SPINS) {
					interrupted |= atomicWait();
				}
				checkCompressing();

				short oldBlockId = blockIds.get(index);
				boolean oldReserved = auxStore.isReserved(oldBlockId);
				if (data == 0 && !auxStore.isReserved(id)) {
					if (!blockIds.compareAndSet(index, oldBlockId, id)) {
						continue;
					}
					if (oldReserved) {
						if (!auxStore.remove(oldBlockId)) {
							throw new IllegalStateException("setBlock() tried to remove old record, but it had already been removed");
						}
					}
					return;
				} else {
					int newIndex = auxStore.add(id, data, null);
					if (!blockIds.compareAndSet(index, oldBlockId, (short) newIndex)) {
						if (auxStore.remove(newIndex)) {
							throw new IllegalStateException("setBlock() tried to remove old record, but it had already been removed");
						}
						continue;
					}
					if (oldReserved) {
						if (!auxStore.remove(oldBlockId)) {
							throw new IllegalStateException("setBlock() tried to remove old record, but it had already been removed");
						}
					}
					return;
				}

			}
		} finally {
			markDirty(x, y, z);
			atomicNotify();
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Sets the block id, data and auxData for the block at (x, y, z), if the
	 * current data matches the expected data.<br>
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param expectId the expected block id
	 * @param expectData the expected block data
	 * @param expectAuxData the expected block auxiliary data
	 * @param newId the new block id
	 * @param newData the new block data
	 * @param newAuxData the new block auxiliary data
	 * @return true if the block was set
	 */
	public final boolean compareAndSetBlock(int x, int y, int z, short expectId, short expectData, short newId, short newData) {
		int index = getIndex(x, y, z);
		int spins = 0;
		boolean interrupted = false;
		try {
			while (true) {
				if (spins++ > SPINS) {
					interrupted |= atomicWait();
				}
				checkCompressing();

				short oldBlockId = blockIds.get(index);
				boolean oldReserved = auxStore.isReserved(oldBlockId);

				if (!oldReserved) {
					if (blockIds.get(index) != expectId || expectData != 0) {
						return false;
					}
				} else {
					int seq = auxStore.getSequence(oldBlockId);
					short oldId = auxStore.getId(oldBlockId);
					short oldData = auxStore.getData(oldBlockId);
					if (!testSequence(x, y, z, seq)) {
						continue;
					}
					if (oldId != expectId || oldData != expectData) {
						return false;
					}
				}

				if (newData == 0 && !auxStore.isReserved(newId)) {
					if (!blockIds.compareAndSet(index, oldBlockId, newId)) {
						continue;
					}
					if (oldReserved) {
						if (!auxStore.remove(oldBlockId)) {
							throw new IllegalStateException("setBlock() tried to remove old record, but it had already been removed");
						}
					}
					markDirty(x, y, z);
					return true;
				} else {
					int newIndex = auxStore.add(newId, newData, null);
					if (!blockIds.compareAndSet(index, oldBlockId, (short) newIndex)) {
						if (!auxStore.remove(newIndex)) {
							throw new IllegalStateException("setBlock() tried to remove old record, but it had already been removed");
						}
						continue;
					}
					if (oldReserved) {
						if (!auxStore.remove(oldBlockId)) {
							throw new IllegalStateException("setBlock() tried to remove old record, but it had already been removed");
						}
					}
					markDirty(x, y, z);
					return true;
				}
			}
		} finally {
			atomicNotify();
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Gets if the store would benefit from compression.<br>
	 * <br>
	 * If this method is called when the store is being accessed by another
	 * thread, it may give spurious results.
	 *
	 * @return true if compression would reduce the store size
	 */
	public boolean needsCompression() {
		return (auxStore.getEntries() << 3) / 3 < auxStore.getSize();
	}

	/**
	 * Gets a short array containing the block ids in the store.<br>
	 * <br>
	 * If the store is updated while this snapshot is being taken, data tearing
	 * could occur.
	 *
	 * @return the array
	 */
	public short[] getBlockIdArray() {
		return getBlockIdArray(null);
	}

	/**
	 * Copies the block ids in the store into an array.<br>
	 * <br>
	 * If the store is updated while this snapshot is being taken, data tearing
	 * could occur.<br>
	 * <br>
	 * If the array is the wrong length or null, a new array is created.
	 *
	 * @param the array to place the data
	 * @return the array
	 */
	public short[] getBlockIdArray(short[] array) {
		int length = blockIds.length();
		if (array == null || array.length != length) {
			array = new short[length];
		}
		for (int i = 0; i < length; i++) {
			short blockId = blockIds.get(i);
			if (auxStore.isReserved(blockId)) {
				blockId = auxStore.getId(blockId);
			} else {
				blockId &= 0x0000FFFF;
			}
			array[i] = blockId;
		}
		return array;
	}

	/**
	 * Gets a short array containing the block data for the blocks in the store.<br>
	 * <br>
	 * If the store is updated while this snapshot is being taken, data tearing
	 * could occur.
	 *
	 * @return the array
	 */
	public short[] getDataArray() {
		return getDataArray(null);
	}

	/**
	 * Copies the block data in the store into an array.<br>
	 * <br>
	 * If the store is updated while this snapshot is being taken, data tearing
	 * could occur.<br>
	 * <br>
	 * If the array is the wrong length or null, a new array is created.
	 *
	 * @param the array to place the data
	 * @return the array
	 */
	public short[] getDataArray(short[] array) {
		int length = blockIds.length();
		if (array == null || array.length != length) {
			array = new short[length];
		}
		for (int i = 0; i < length; i++) {
			short blockId = blockIds.get(i);
			if (auxStore.isReserved(blockId)) {
				array[i] = auxStore.getData(blockId);
			} else {
				array[i] = 0;
			}
		}
		return array;
	}

	/**
	 * Compresses the auxiliary store.<br>
	 * <br>
	 * This method should only be called when the store is guaranteed not to be
	 * accessed from any other thread.<br>
	 */
	public final void compress() {
		if (!compressing.compareAndSet(false, true)) {
			throw new IllegalStateException("Compression started while compression was in progress");
		}
		int length = side * side * side;
		AtomicIntReferenceArrayStore<T> newAuxStore = new AtomicIntReferenceArrayStore<T>(side * side * side);
		for (int i = 0; i < length; i++) {
			short blockId = blockIds.get(i);
			if (auxStore.isReserved(blockId)) {
				short storedId = auxStore.getId(blockId);
				short storedData = auxStore.getData(blockId);
				T storedAuxData = auxStore.getAuxData(blockId);
				int newIndex = newAuxStore.add(storedId, storedData, storedAuxData);
				if (!blockIds.compareAndSet(i, blockId, (short) newIndex)) {
					throw new IllegalStateException("Unstable block id data during compression step");
				}
			}
		}
		auxStore = newAuxStore;
		compressing.set(false);
	}

	/**
	 * Gets the size of the internal arrays
	 *
	 * @return the size of the arrays
	 */
	public final int getSize() {
		checkCompressing();
		return auxStore.getSize();
	}

	/**
	 * Gets the number of entries in the store
	 *
	 * @return the size of the arrays
	 */
	public final int getEntries() {
		checkCompressing();
		return auxStore.getEntries();
	}

	/**
	 * Gets if the dirty array has overflowed since the last reset.<br>
	 * <br>
	 *
	 * @return true if there was an overflow
	 */
	public boolean isDirtyOverflow() {
		return dirtyBlocks.get() >= dirtyX.length;
	}

	/**
	 * Gets if the store has been modified since the last reset of the dirty
	 * arrays
	 *
	 * @return true if the store is dirty
	 */
	public boolean isDirty() {
		return dirtyBlocks.get() > 0;
	}

	/**
	 * Resets the dirty arrays
	 */
	public void resetDirtyArrays() {
		dirtyBlocks.set(0);
	}

	/**
	 * Gets the position of the dirty block at a given index.<br>
	 * <br>
	 * If there is no block at that index, then the method return null.<br>
	 * <br>
	 * Note: the x, y and z values returned are the chunk coordinates, not the
	 * world coordinates and the method has no effect on the world field of the
	 * block.<br>
	 *
	 * @param i
	 * @param block
	 * @return
	 */
	public Block getDirtyBlock(int i, Block block) {
		if (i >= dirtyBlocks.get()) {
			return null;
		}
		block.setX(dirtyX[i] & 0xFF);
		block.setY(dirtyY[i] & 0xFF);
		block.setZ(dirtyZ[i] & 0xFF);
		return block;
	}

	private final int getIndex(int x, int y, int z) {
		return (y << doubleShift) + (z << shift) + x;
	}
	
	/**
	 * Marks a block as dirty.<br>
	 * <br>
	 * Updates for dirty blocks will be sent at the end of the tick.<br>
	 *
	 * @param x the x coordinate of the dirty block
	 * @param y the y coordinate of the dirty block
	 * @param z the z coordinate of the dirty block
	 */
	public void markDirty(int x, int y, int z) {
		int index = dirtyBlocks.getAndIncrement();
		if (index < dirtyX.length) {
			dirtyX[index] = (byte) x;
			dirtyY[index] = (byte) y;
			dirtyZ[index] = (byte) z;
		}
	}

	private final void checkCompressing() {
		if (compressing.get()) {
			throw new IllegalStateException("Attempting to access block store during compression phase");
		}
	}

	/**
	 * Waits until a notify
	 *
	 * @return true if interrupted during the wait
	 */
	private final boolean atomicWait() {
		waiting.incrementAndGet();
		try {
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					return true;
				}
			}
		} finally {
			waiting.decrementAndGet();
		}
		return true;
	}

	/**
	 * Notifies all waiting threads
	 */
	private final void atomicNotify() {
		if (waiting.getAndAdd(0) > 0) {
			synchronized (this) {
				notifyAll();
			}
		}
	}
}
