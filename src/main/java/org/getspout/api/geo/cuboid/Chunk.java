/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.BlockAccess;
import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.player.Player;
import org.getspout.api.util.cuboid.CuboidShortBuffer;
import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;

/**
 * Represents a cube containing 16x16x16 Blocks
 */
public abstract class Chunk extends Cube implements BlockAccess {

	/**
	 * Internal size of a side of a chunk
	 */
	public final static int CHUNK_SIZE = 16;
	
	/**
	 * Number of bits on the side of a chunk
	 */
	public final static int CHUNK_SIZE_BITS = 4;
	
	/**
	 * Mask to convert a block integer coordinate into the chunk's base
	 */
	public final static int BASE_MASK = -CHUNK_SIZE;

	public Chunk(World world, float x, float y, float z) {
		super(new Point(world, x, y, z), CHUNK_SIZE);
	}
	
	/**
	 * Performs the necessary tasks to unload this chunk from the world.
	 * 
	 * @param save whether the chunk data should be saved.
	 */
	public abstract void unload(boolean save);
	
	/**
	 * Performs the necessary tasks to save this chunk.
	 * 
	 * @param save whether the chunk data should be saved.
	 */
	public abstract void save();
	
	/**
	 * Gets a snapshot of the live block id data for the chunk.
	 * 
	 * This process may result in tearing if called during potential updates
	 * 
	 * @return the snapshot
	 */
	@LiveRead
	public abstract CuboidShortBuffer getBlockCuboidBufferLive();
	
	/**
	 * Register a player as observing the chunk.  
	 * 
	 * @param player the player
	 * @return false if the player was already observing the chunk
	 */
	@DelayedWrite
	public abstract boolean addObserver(Player player);
	
	/**
	 * De-register a player as observing the chunk.  
	 * 
	 * @param player the player
	 * @return true if the player was observing the chunk
	 */
	@DelayedWrite
	public abstract boolean removeObserver(Player player);
	
	/**
	 * Gets the region that this chunk is located in
	 * 
	 * @return
	 */
	public abstract Region getRegion();
	
	public static Point pointToBase(Point p) {
		return new Point(p.getWorld(), ((int)p.getX()) & BASE_MASK, ((int)p.getY()) & BASE_MASK, ((int)p.getZ()) & BASE_MASK);
	}
	
	/**
	 * Tests if the chunk has been unloaded.
	 * 
	 * Chunks may be unloaded at the end of each tick
	 */
	public abstract boolean isUnloaded();
	
}
