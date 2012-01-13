package org.spout.api.util.map.concurrent;

import org.spout.api.basic.blocks.BlockFullState;
import org.spout.api.datatable.DatatableSequenceNumber;

/**
 * This store stores block data for each chunk.  
 * Each block can either store a short id, or a short id, a short data 
 * value and a reference to a &lt;T&gt; object.
 */
public class AtomicBlockStore<T> {
	
	private final int side;
	private final int shift;
	private final int doubleShift;
	private final AtomicShortArray blockIds;
	private final AtomicIntReferenceArrayStore<T> auxStore;
	
	public AtomicBlockStore(int shift) {
		this.side = 1 << shift;
		this.shift = shift;
		this.doubleShift = shift << 1;
		blockIds = new AtomicShortArray(side * side * side);
		auxStore = new AtomicIntReferenceArrayStore<T>(side * side * side);
	}
	
	/**
	 * Gets the sequence number associated with a block location.<br>
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the sequence number, or DatatableSequenceNumber.ATOMIC for a single short record
	 */
	public final int getSequence(int x, int y, int z) {
		int index = getIndex(x, y, z);
		while (true) {
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
		while (true) {
			int seq = getSequence(x, y, z);
			short blockId = blockIds.get(index);
			if (auxStore.isReserved(blockId)) {
				blockId = auxStore.getId(blockId);
				int seq2 = getSequence(x, y, z);
				if (seq == seq2) {
					return blockId & 0x0000FFFF;
				}
			} else {
				return blockId & 0x0000FFFF;
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
		while (true) {
			int seq = getSequence(x, y, z);
			short blockId = blockIds.get(index);
			if (auxStore.isReserved(blockId)) {
				blockId = auxStore.getData(blockId);
				int seq2 = getSequence(x, y, z);
				if (seq == seq2) {
					return blockId & 0x0000FFFF;
				}
			} else {
				return 0;
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
		while (true) {
			int seq = getSequence(x, y, z);
			short blockId = blockIds.get(index);
			if (auxStore.isReserved(blockId)) {
				T auxData = auxStore.getAuxData(blockId);
				int seq2 = getSequence(x, y, z);
				if (seq == seq2) {
					return auxData;
				}
			} else {
				return null;
			}
		}
	}
	
	/**
	 * Atomically gets the full set of data associated with the block.<br>
	 * <br>
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param fullState a BlockFullState object to store the return value, or null to generate a new one
	 * @return the full state of the block
	 */
	public final BlockFullState<T> getFullData(int x, int y, int z) {
		return getFullData(x, y, z, null);
	}
	/**
	 * Atomically gets the full set of data associated with the block.<br>
	 * <br>
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param fullState a BlockFullState object to store the return value, or null to generate a new one
	 * @return the full state of the block
	 */
	public final BlockFullState<T> getFullData(int x, int y, int z, BlockFullState<T> fullData) {
		if (fullData == null) {
			fullData = new BlockFullState<T>();
		}
		int index = getIndex(x, y, z);
		while (true) {
			int seq = getSequence(x, y, z);
			short blockId = blockIds.get(index);
			if (auxStore.isReserved(blockId)) {
				fullData.setId(auxStore.getId(blockId));
				fullData.setData(auxStore.getData(blockId));
				fullData.setAuxData(auxStore.getAuxData(blockId));
				int seq2 = getSequence(x, y, z);
				if (seq == seq2) {
					return fullData;
				}
			} else {
				fullData.setId(blockId);
				fullData.setData((short)0);
				fullData.setAuxData(null);
				return fullData;
			}
		}
	}
	
	/**
	 * Sets the block id, data and auxData for the block at (x, y, z).<br>
	 * <br>
	 * If the data is 0 and the auxData is null, then the block will be stored as a single short.<br>
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param fullState the new state of the Block
	 */
	public final void setBlock(int x, int y, int z, BlockFullState<T> fullState) {
		setBlock(x, y, z, fullState.getId(), fullState.getData(), fullState.getAuxData());
	}
	
	/**
	 * Sets the block id, data and auxData for the block at (x, y, z).<br>
	 * <br>
	 * If the data is 0 and the auxData is null, then the block will be stored as a single short.<br>
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param id the block id
	 * @param data the block data
	 * @param auxData the block auxiliary data
	 */
	public final void setBlock(int x, int y, int z, short id, short data, T auxData) {
		int index = getIndex(x, y, z);
		while (true) {
			short oldBlockId = blockIds.get(index);
			boolean oldReserved = auxStore.isReserved(oldBlockId);
			if (data == 0 && auxData == null && !auxStore.isReserved(id)) {
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
				int newIndex = auxStore.add(id, data, auxData);
				if (!blockIds.compareAndSet(index, oldBlockId, (short)newIndex)) {
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
	}
	
	/**
	 * Sets the block id, data and auxData for the block at (x, y, z), if the current data matches the expected data.<br>
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
	public final boolean compareAndSetBlock(int x, int y, int z, short expectId, short expectData, T expectAuxData, short newId, short newData, T newAuxData) {
		int index = getIndex(x, y, z);
		
		while (true) {
			short oldBlockId = blockIds.get(index);
			boolean oldReserved = auxStore.isReserved(oldBlockId);
			
			if (!oldReserved) {
				if (blockIds.get(index) != expectId) {
					return false;
				}
			} else {
				int seq1 = auxStore.getSequence(index);
				short oldId = auxStore.getId(index);
				short oldData = auxStore.getData(index);
				T oldAuxData = auxStore.getAuxData(index);
				int seq2 = auxStore.getSequence(index);
				if (seq1 != seq2) {
					continue;
				}
				if (oldId != expectId || oldData != expectData || oldAuxData != expectAuxData) {
					return false;
				}
			}
			
			if (newData == 0 && newAuxData == null && !auxStore.isReserved(newId)) {
				if (!blockIds.compareAndSet(index, oldBlockId, newId)) {
					continue;
				}
				if (oldReserved) {
					if (!auxStore.remove(oldBlockId)) {
						throw new IllegalStateException("setBlock() tried to remove old record, but it had already been removed");
					}
				}
				return true;
			} else {
				int newIndex = auxStore.add(newId, newData, newAuxData);
				if (!blockIds.compareAndSet(index, oldBlockId, (short)newIndex)) {
					if (!auxStore.remove(newIndex)) {
						throw new IllegalStateException("setBlock() tried to remove old record, but it had already been removed");
					}
					continue;
				}
				if (oldReserved) {
					if (!auxStore.remove(oldBlockId)) {
						throw new IllegalStateException("setBlock() tried to remove old record, but it had already been removed");
					}				}
				return true;
			}
		}
	}
	
	/**
	 * Sets the block id, data and auxData for the block at (x, y, z), if the current data matches the expected data.<br>
	 * <br>
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param expect the expected block value
	 * @param newValue the new block value
	 * @return true if the block was set
	 */
	public final boolean compareAndSetBlock(int x, int y, int z, BlockFullState<T> expect, BlockFullState<T> newValue) {
		return compareAndSetBlock(x, y, z, expect.getId(), expect.getData(), expect.getAuxData(), newValue.getId(), newValue.getData(), newValue.getAuxData());
	}

	
	private final int getIndex(int x, int y, int z) {
		return (x << doubleShift) + (z << shift) + y;
	}

}
