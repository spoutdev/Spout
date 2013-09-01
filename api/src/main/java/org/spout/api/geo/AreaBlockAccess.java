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
package org.spout.api.geo;

import org.spout.api.event.Cause;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.BlockMaterial;
import org.spout.math.vector.Vector3;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;
import org.spout.api.util.cuboid.CuboidLightBuffer;
import org.spout.api.util.thread.annotation.LiveWrite;
import org.spout.api.util.thread.annotation.Threadsafe;

public interface AreaBlockAccess extends AreaBlockSource {
	/**
	 * Sets the data for the block at (x, y, z) to the given data.
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @param data to set to
	 * @param cause of the change, or null if non-specific cause
	 */
	@LiveWrite
	public boolean setBlockData(int x, int y, int z, short data, Cause<?> source);

	/**
	 * Adds a value to the data for the block at (x, y, z)
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @param data to add
	 * @param cause of the change, or null if non-specific cause
	 */
	@LiveWrite
	public boolean addBlockData(int x, int y, int z, short data, Cause<?> source);

	/**
	 * Sets the material and data for the block at (x, y, z) to the given material and data.
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @param data value to set to
	 * @param material to set to
	 * @param cause of the change, or null if non-specific cause
	 */
	@LiveWrite
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Cause<?> source);

	/**
	 * Sets the data of the block at (x, y, z) if the expected state matches
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @param expect is the state of the block it expects
	 * @param data to set to if it matches
	 * @param cause of the change, or null if non-specific cause
	 * @return whether setting was successful
	 */
	@LiveWrite
	public boolean compareAndSetData(int x, int y, int z, int expect, short data, Cause<?> source);

	/**
	 * Sets the given bits in the data for the block at (x, y, z)<br> <br> newData = oldData | (bits)
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @param bits the bits to set
	 * @return the old data for the block
	 */
	@LiveWrite
	public short setBlockDataBits(int x, int y, int z, int bits, Cause<?> source);

	/**
	 * Sets the given bits in the data for the block at (x, y, z)<br> <br> newData = oldData | (bits) <br>or<br> newData = oldData & ~(bits)
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @param bits the bits to set or clear
	 * @param set true to set, false to clear
	 * @return the old data for the block
	 */
	@LiveWrite
	public short setBlockDataBits(int x, int y, int z, int bits, boolean set, Cause<?> source);

	/**
	 * Clears the given bits in the data for the block at (x, y, z)<br> <br> newData = oldData & (~bits)
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @param bits the bits to clear
	 * @param cause of the change, or null if non-specific cause
	 * @return the old data for the block
	 */
	@LiveWrite
	public short clearBlockDataBits(int x, int y, int z, int bits, Cause<?> source);

	/**
	 * Gets the data field from the block at (x, y, z)<br> <br> field = (data & bits) >> (shift)<br> <br> The shift value used shifts the least significant non-zero bit of bits to the LSB position
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @param bits the bits of the field
	 * @return the field value
	 */
	@Threadsafe
	public int getBlockDataField(int x, int y, int z, int bits);

	/**
	 * Gets if any of the indicated bits are set.
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @param bits the bits of the field
	 * @return true if any of the given bits are set
	 */
	@Threadsafe
	public boolean isBlockDataBitSet(int x, int y, int z, int bits);

	/**
	 * Sets the data field for the block at (x, y, z).  This is the reverse operation to the getBlockDataField method.<br> <br> newData = ((value << shift) & bits) | (oldData & (~bits))<br> <br> The
	 * shift value used shifts the least significant non-zero bit of bits to the LSB position
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @param bits the bits of the field
	 * @param value the new value of the field
	 * @param cause of the change, or null if non-specific cause
	 * @return the old value of the field
	 */
	@LiveWrite
	@Threadsafe
	public int setBlockDataField(int x, int y, int z, int bits, int value, Cause<?> source);

	/**
	 * Adds a value to the data field for the block at (x, y, z).  This is the reverse operation to the getBlockDataField method.<br> <br> newData = (((oldData + (value << shift)) & bits) | (oldData &
	 * ~bits))<br> <br> The shift value used shifts the least significant non-zero bit of bits to the LSB position
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @param bits the bits of the field
	 * @param value to add to the value of the field
	 * @return the old value of the field
	 */
	@LiveWrite
	@Threadsafe
	public int addBlockDataField(int x, int y, int z, int bits, int value, Cause<?> source);

	/**
	 * Gets if a block is contained in this area
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return true if it is contained, false if not
	 */
	public boolean containsBlock(int x, int y, int z);

	/**
	 * Gets a {@link Block} representing the block at (x, y, z)
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return the Block
	 */
	@Threadsafe
	public Block getBlock(int x, int y, int z);

	/**
	 * Gets a {@link Block} representing the block at (x, y, z)
	 *
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @return the Block
	 */
	@Threadsafe
	public Block getBlock(float x, float y, float z);

	/**
	 * Gets a {@link Block} representing the block at the position given
	 *
	 * @param position of the block
	 * @return the Block
	 */
	@Threadsafe
	public Block getBlock(Vector3 position);

	/**
	 * Atomically sets the cuboid volume to the values inside of the cuboid buffer, if the contents of the buffer's backbuffer matches the world.
	 *
	 * @param cause that is setting the cuboid volume
	 */
	@Threadsafe
	public boolean commitCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause);

	/**
	 * Atomically sets the cuboid volume to the values inside of the cuboid buffer.
	 *
	 * @param cause that is setting the cuboid volume
	 */
	@Threadsafe
	public void setCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause);

	/**
	 * Atomically sets the cuboid volume to the values inside of the cuboid buffer with the base located at the given coords
	 *
	 * @param cause that is setting the cuboid volume
	 */
	@Threadsafe
	public void setCuboid(int x, int y, int z, CuboidBlockMaterialBuffer buffer, Cause<?> cause);

	/**
	 * Gets the CuboidLightBuffer for a given id.
	 *
	 * @param id the id for the buffer
	 */
	@Threadsafe
	public CuboidLightBuffer getLightBuffer(short id);

	/**
	 * Atomically gets the cuboid volume
	 *
	 * @param backBuffer true for a buffer with a back buffer
	 */
	@Threadsafe
	public CuboidBlockMaterialBuffer getCuboid(boolean backBuffer);

	/**
	 * Atomically gets the cuboid volume with the base located at the given coords of the given size.<br> The buffer returned contains a back buffer <br> Note: The block at the base coordinate is inside
	 * the buffer
	 *
	 * @param bx base x-coordinate
	 * @param by base y-coordinate
	 * @param bz base z-coordinate
	 * @param sx size x-coordinate
	 * @param sy size y-coordinate
	 * @param sz size z-coordinate
	 */
	@Threadsafe
	public CuboidBlockMaterialBuffer getCuboid(int bx, int by, int bz, int sx, int sy, int sz);

	/**
	 * Atomically gets the cuboid volume with the base located at the given coords of the given size.<br> <br> Note: The block at the base coordinate is inside the buffer
	 *
	 * @param bx base x-coordinate
	 * @param by base y-coordinate
	 * @param bz base z-coordinate
	 * @param sx size x-coordinate
	 * @param sy size y-coordinate
	 * @param sz size z-coordinate
	 * @param backBuffer true for a buffer with a back buffer
	 */
	@Threadsafe
	public CuboidBlockMaterialBuffer getCuboid(int bx, int by, int bz, int sx, int sy, int sz, boolean backBuffer);

	/**
	 * Atomically gets the cuboid volume with the base located at the given coords and the size of the given buffer.<br> <br> Note: The block at the base coordinate is inside the
	 *
	 * @param bx base x-coordinate
	 * @param by base y-coordinate
	 * @param bz base z-coordinate
	 */
	@Threadsafe
	public void getCuboid(int bx, int by, int bz, CuboidBlockMaterialBuffer buffer);

	/**
	 * Atomically gets the cuboid volume contained within the given buffer
	 *
	 * @param buffer the buffer
	 */
	@Threadsafe
	public void getCuboid(CuboidBlockMaterialBuffer buffer);
}
