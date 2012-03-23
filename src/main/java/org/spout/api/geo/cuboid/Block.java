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
package org.spout.api.geo.cuboid;

import org.spout.api.Source;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.source.DataSource;
import org.spout.api.material.source.MaterialContainer;
import org.spout.api.material.source.MaterialSource;
import org.spout.api.math.Vector3;

public interface Block extends MaterialContainer, Cloneable {

	public Point getPosition();

	public Chunk getChunk();
	
	public World getWorld();

	public int getX();

	public int getY();

	public int getZ();

	public Block setX(int x);
	
	public Block setY(int y);
	
	public Block setZ(int z);
	
	public Block move(BlockFace offset);

	public Block move(Vector3 offset);

	public Block move(int dx, int dy, int dz);
	
	/**
	 * Gets the source this block represents
	 * 
	 * @return the source
	 */
	public Source getSource();
	
	/**
	 * Sets the source this block represents
	 * 
	 * @param source
	 */
	public void setSource(Source source);
	
	/**
	 * Sets the material
	 * @param material to set to
	 * @param update whether players nearby should be notified of the block change
	 */
	public void setMaterial(MaterialSource material, boolean update);
	
	/**
	 * Gets the block material
	 * 
	 * @return the block material
	 */
	@Override
	public BlockMaterial getMaterial();
	
	/**
	 * Sets the material and data to the one of the source block
	 * @param blocksource to set to
	 */
	public void setBlock(MaterialSource block);
	
	/**
	 * Sets the material and data to the one of the source block
	 * @param blocksource to set to
	 * @param update whether players nearby should be notified of the block change
	 */
	public void setBlock(MaterialSource blocksource, boolean update);
	
	/**
	 * Sets the material and data
	 * @param material to set to
	 * @param datasource of the data to set to
	 * @param update whether players nearby should be notified of the block change
	 */
	public void setMaterial(MaterialSource material, DataSource datasource, boolean update);

	/**
	 * Sets the material and data
	 * @param material to set to
	 * @param data value to set to
	 * @param update whether players nearby should be notified of the block change
	 */
	public void setMaterial(MaterialSource material, short data, boolean update);

	/**
	 * Sets the data
	 * @param datasource of the data to set to
	 * @param update whether players nearby should be notified of the block change
	 */
	public void setData(DataSource datasource, boolean update);

	/**
	 * Sets the data
	 * @param data value to set to
	 * @param update whether players nearby should be notified of the block change
	 */
	public void setData(short data, boolean update);
	
	/**
	 * Clones this block
	 * 
	 * @return a new instance of this block
	 */
	public Block clone();
}
