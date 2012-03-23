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

import org.spout.api.Source;
import org.spout.api.basic.blocks.BlockFullState;
import org.spout.api.datatable.Datatable;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.material.BlockMaterial;
import org.spout.api.util.thread.LiveWrite;

public interface AreaBlockAccess extends AreaBlockSource {

	/**
	 * Sets the data for the block at (x, y, z) to the given data.<br>
	 * <br>
	 * This method will clear the block's auxiliary data.<br>
	 * <br>
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param data to set to
	 * @param updatePhysics whether this block change should update the physics of neighbor blocks afterword
	 * @param notify whether players nearby should be notified of the block change
	 * @param source of the change
	 * @throws NullPointerException
	 */
	@LiveWrite
	public boolean setBlockData(int x, int y, int z, short data, boolean updatePhysics, Source source);

	/**
	 * Sets the material and data for the block at (x, y, z) to the given material and data.<br>
	 * <br>
	 * This method will clear the block's auxiliary data.<br>
	 * <br>
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param data value to set to
	 * @param material to set to
	 * @param updatePhysics whether this block change should update the physics of neighbor blocks afterword
	 * @param notify whether players nearby should be notified of the block change
	 * @param source of the change
	 * @throws NullPointerException
	 */
	@LiveWrite
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, boolean updatePhysics, Source source);
	
	/**
	 * Forces a physics update for the block at the (x, y, z)
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 */
	public void updatePhysics(int x, int y, int z);

	/**
	 * Sets the snapshot data for the block at (x, y, z) to the given data, but
	 * only if the current block state matches the given state.
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
	 * Adds a key, value pair to the auxiliary data for the block, but only if
	 * the current block state matches the given state.
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
	 * Removes a key, value pair from the auxiliary data for the block, but only
	 * if the current block state matches the given state.
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
