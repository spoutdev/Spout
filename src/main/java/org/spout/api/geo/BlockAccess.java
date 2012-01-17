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
package org.spout.api.geo;

import org.spout.api.basic.blocks.BlockFullState;
import org.spout.api.datatable.Datatable;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.material.BlockMaterial;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.LiveWrite;

public interface BlockAccess {
	/**
	 * Sets the block at (x, y, z) to the given material type.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param material
	 */
	@LiveWrite
	public void setBlockMaterial(int x, int y, int z, BlockMaterial material);

	/**
	 * Sets the id for the block at (x, y, z) to the given id.<br>
	 * <br>
	 * This method will clear the block's data and any auxiliary data.<br>
	 * <br>
	 * For ids greater than 255, the id must represent a valid custom id.<br>
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param id
	 */
	@LiveWrite
	public void setBlockId(int x, int y, int z, short id);


	/**
	 * Sets the id for the block at (x, y, z) to the given id.<br>
	 * <br>
	 * This method will clear the block's auxiliary data.<br>
	 * <br>
	 * For ids greater than 255, the id must represent a valid custom id.<br>
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param id
	 */
	@LiveWrite
	public void setBlockIdAndData(int x, int y, int z, short id, short data);

	/**
	 * Gets the snapshot material for the block at (x, y, z)
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the block's material from the snapshot
	 */
	@LiveRead
	public BlockMaterial getBlockMaterial(int x, int y, int z);

	/**
	 * Gets the snapshot id for the block at (x, y, z)
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the block's material from the snapshot
	 */
	@LiveRead
	public short getBlockId(int x, int y, int z);

	/**
	 * Gets the data for the block at (x, y, z)
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the block's data from the snapshot
	 */
	@LiveRead
	public short getBlockData(int x, int y, int z);

	/**
	 * Sets the snapshot data for the block at (x, y, z) to the given data, but only if the current block state matches the given state.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param data to set at the block
	 * @return true on success
	 */
	@LiveWrite
	public boolean compareAndSetData(int x, int y, int z, BlockFullState<DatatableMap> expect, short data);

	/**
	 * Adds a key, value pair to the auxiliary data for the block, but only if the current block state matches the given state.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param data to set at the block
	 * @return true on success
	 */
	@LiveWrite
	public boolean compareAndPut(int x, int y, int z, BlockFullState<DatatableMap> expect, String key, Datatable auxData);

	/**
	 * Removes a key, value pair from the auxiliary data for the block, but only if the current block state matches the given state.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param data to set at the block
	 * @return true on success
	 */
	@LiveWrite
	public boolean compareAndRemove(int x, int y, int z, BlockFullState<DatatableMap> expect, String key, Datatable auxData);
}
