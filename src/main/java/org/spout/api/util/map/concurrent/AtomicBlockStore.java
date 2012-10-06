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
package org.spout.api.util.map.concurrent;

import gnu.trove.set.hash.TIntHashSet;

import org.spout.api.material.source.MaterialSource;
import org.spout.api.math.Vector3;

/**
 * This store stores block data for each chunk. Each block can either store a
 * short id, or a short id, a short data value and a reference to a &lt;T&gt;
 * object.
 */
public interface AtomicBlockStore {

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
	public int getBlockId(int x, int y, int z);

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
	public int getData(int x, int y, int z);

	/**
	 * Atomically gets the full set of data associated with the block.<br>
	 * <br>
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the full state of the block
	 */
	public int getFullData(int x, int y, int z);

	/**
	 * Sets the block id and data for the block at (x, y, z).<br>
	 * <br>
	 * If the data is 0 and the auxData is null, then the block will be stored
	 * as a single short.<br>
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param fullState the new state of the Block
	 */
	public void setBlock(int x, int y, int z, MaterialSource material);
	
	/**
	 * Sets the block id and data for the block at (x, y, z).<br>
	 * <br>
	 * If the data is 0 and the auxData is null, then the block will be stored
	 * as a single short.<br>
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param material the new material
	 * @return the old full state of the block
	 */
	public int getAndSetBlock(int x, int y, int z, MaterialSource material);

	/**
	 * Sets the block id and data for the block at (x, y, z).<br>
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
	public void setBlock(int x, int y, int z, short id, short data);
	
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
	 * @return the old full state of the block
	 */
	public int getAndSetBlock(int x, int y, int z, short id, short data);

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
	public boolean compareAndSetBlock(int x, int y, int z, short expectId, short expectData, short newId, short newData);

	/**
	 * Gets if the store would benefit from compression.<br>
	 * <br>
	 * If this method is called when the store is being accessed by another
	 * thread, it may give spurious results.
	 *
	 * @return true if compression would reduce the store size
	 */
	public boolean needsCompression();
	
	/**
	 * Gets a short array containing the block ids in the store.<br>
	 * <br>
	 * If the store is updated while this snapshot is being taken, data tearing
	 * could occur.
	 *
	 * @return the array
	 */
	public short[] getBlockIdArray();

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
	public short[] getBlockIdArray(short[] array);
	/**
	 * Gets a short array containing the block data for the blocks in the store.<br>
	 * <br>
	 * If the store is updated while this snapshot is being taken, data tearing
	 * could occur.
	 *
	 * @return the array
	 */
	public short[] getDataArray();

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
	public short[] getDataArray(short[] array);

	/**
	 * Compresses the store.<br>
	 */
	public void compress();
	
	/**
	 * Compresses the store.<br>
	 * @param set to use to store used ids
	 */
	public void compress(TIntHashSet inUseSet);

	/**
	 * Gets if the dirty array has overflowed since the last reset.<br>
	 * <br>
	 *
	 * @return true if there was an overflow
	 */
	public boolean isDirtyOverflow();
	/**
	 * Gets if the store has been modified since the last reset of the dirty
	 * arrays
	 *
	 * @return true if the store is dirty
	 */
	public boolean isDirty();

	/**
	 * Resets the dirty arrays
	 * 
	 * @return true if there were dirty blocks
	 */
	public boolean resetDirtyArrays();

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
	public Vector3 getDirtyBlock(int i);	
}
