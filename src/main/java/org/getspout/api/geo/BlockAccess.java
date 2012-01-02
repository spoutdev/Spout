/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.geo;

import org.getspout.api.material.BlockMaterial;
import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

public interface BlockAccess {
	
	/**
	 * Sets the block at (x, y, z) to the given material type and returns the snapshot value
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param material
	 * @return the block's material from the snapshot
	 */
	@SnapshotRead
	@DelayedWrite
	public BlockMaterial setBlockMaterial(int x, int y, int z, BlockMaterial material);
	
	/**
	 * Sets the id for the block at (x, y, z) to the given id and returns the snapshot value
	 * 
	 * For ids greater than 255, the id must represent a value custom id
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param id
	 * @return the block's id from the snapshot
	 */
	@SnapshotRead
	@DelayedWrite
	public short setBlockId(int x, int y, int z, short id);
	
	/**
	 * Gets the snapshot material for the block at (x, y, z)
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the block's material from the snapshot
	 */
	@SnapshotRead
	public BlockMaterial getBlockMaterial(int x, int y, int z);
	
	/**
	 * Gets the material for the block at (x, y, z)<br/>
	 * 
	 * Note: using live material may have a negative performance impact, relative to reading the snapshot value
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param live whether to use the live id, or snapshot value
	 * @return the block's material
	 */
	@SnapshotRead
	@LiveRead
	public BlockMaterial getBlockMaterial(int x, int y, int z, boolean live);
	
	/**
	 * Gets the snapshot id for the block at (x, y, z)
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the block's material from the snapshot
	 */
	@SnapshotRead
	public short getBlockId(int x, int y, int z);
	
	/**
	 * Gets the id for the block at (x, y, z)<br/>
	 * 
	 * Note: using live id may have a negative performance impact, relative to reading the snapshot value
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param live whether to use the live id, or snapshot value
	 * @return the block's material
	 */
	@SnapshotRead
	@LiveRead
	public short getBlockId(int x, int y, int z, boolean live);
	
	/**
	 * Gets the snapshot data for the block at (x, y, z)
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the block's data from the snapshot
	 */
	@SnapshotRead
	public byte getBlockData(int x, int y, int z);
	
	/**
	 * Gets the data for the block at (x, y, z)<br/>
	 * 
	 * Note: using live data may have a negative performance impact, relative to reading the snapshot value
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param live whether to use the live data, or snapshot value
	 * @return the block's data
	 */
	@SnapshotRead
	@LiveRead
	public byte getBlockData(int x, int y, int z, boolean live);
	
	/**
	 * Sets the snapshot data for the block at (x, y, z) to the given data and returns the snapshot value.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param data to set at the block
	 * @return the block's data from the snapshot
	 */
	@SnapshotRead
	@DelayedWrite
	public byte setBlockData(int x, int y, int z, byte data);

}
