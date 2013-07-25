/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.block.BlockFace;
import org.spout.api.util.cuboid.ImmutableHeightMapBuffer;

public interface LocalAreaAccess {
	
	/**
	 * Gets a neighbouring region.  Only the 3x3x3 cube of regions centered on this region can be obtained by this method.
	 * 
	 * @param dx
	 * @param dy
	 * @param dz
	 * @param loadopt
	 * @return
	 */
	public Region getLocalRegion(BlockFace face, LoadOption loadopt);
	
	/**
	 * Gets a neighbouring region.  The coordinates provided range from 0 to 2, rather than -1 to +1.  
	 * If all 3 coordinates are 1, then this region is returned.
	 * 
	 * @param dx
	 * @param dy
	 * @param dz
	 * @param loadopt
	 * @return
	 */
	public Region getLocalRegion(int dx, int dy, int dz, LoadOption loadopt);
	
	/**
	 * Gets a chunk relative to a given chunk.  The given chunk must be in this region and the requested chunk 
	 * must be in the 3x3x3 cube of regions centred on this region.<br>
	 * 
	 * @param c
	 * @param face
	 * @param loadopt
	 * @return
	 */
	public Chunk getLocalChunk(Chunk c, BlockFace face, LoadOption loadopt);
	
	/**
	 * Gets a chunk relative to a given chunk.  The given chunk must be in this region and the requested chunk 
	 * must be in the 3x3x3 cube of regions centred on this region.<br>
	 * <br>
	 * (ox, oy, oz) is the offset to the desired chunk.  The coordinates of the offset can not have 
	 * a magnitude greater than 16.
	 * 
	 * @param c
	 * @param ox
	 * @param oy
	 * @param oz
	 * @param loadopt
	 * @return
	 */
	public Chunk getLocalChunk(Chunk c, int ox, int oy, int oz, LoadOption loadopt);
	
	/**
	 * Gets a chunk relative to given chunk coordinates.  The given chunk must be in this region and the requested chunk 
	 * must be in the 3x3x3 cube of regions centred on this region.<br>
	 * <br>
	 * (x, y, z) are the coordinates of a chunk in this region.<br>
	 * <br>
	 * (ox, oy, oz) is the offset to the desired chunk.  The coordinates of the offset can not have 
	 * a magnitude greater than 16.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param ox
	 * @param oy
	 * @param oz
	 * @param loadopt
	 * @return
	 */
	public Chunk getLocalChunk(int x, int y, int z, int ox, int oy, int oz, LoadOption loadopt);
	
	/**
	 * Gets a chunk in the 3x3x3 cube of regions centered on this region.<br>
	 * <br>
	 * The valid range for the (x, y, z) coordinates is -16 to 31.<br>
	 * <br>
	 * To request a chunk in this region, all three coordinates must be in the range of 0 to 15.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param loadopt
	 * @return
	 */
	public Chunk getLocalChunk(int x, int y, int z, LoadOption loadopt);
	
	/**
	 * Gets a heightmap in the 3x3 cube of regions centered on this region.<br>
	 * <br>
	 * The valid range for the (x, z) coordinates is -16 to 31.<br>
	 * <br>
	 * To request a heightmap in this region, both coordinates must be in the range of 0 to 15.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param loadopt
	 * @return
	 */
	public ImmutableHeightMapBuffer getLocalHeightMap(int x, int z, LoadOption loadopt);

}
