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
package org.spout.api.geo.cuboid;

import java.util.Set;

import org.spout.api.entity.Entity;
import org.spout.api.geo.BlockData;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.util.thread.SnapshotRead;

public abstract class ChunkSnapshot extends Cube implements BlockData {
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

	public ChunkSnapshot(World world, float x, float y, float z) {
		super(new Point(world, x, y, z), CHUNK_SIZE);
	}

	/**
	 * Gets the raw block ids
	 * 
	 * @return raw block ids
	 */
	public abstract short[] getBlockIds();

	/**
	 * Gets the raw block data.
	 * 
	 * @return block data
	 */
	public abstract short[] getBlockData();

	/**
	 * Gets the raw block light data. <br/><br/> 
	 * 
	 * Light is stored in nibbles, with the first index even, the second odd.
	 * @return raw block light data
	 */
	public abstract byte[] getBlockLight();

	/**
	 * Gets the raw sky light data. <br/><br/> 
	 * 
	 * Light is stored in nibbles, with the first index even, the second odd.
	 * @return raw skylight data
	 */
	public abstract byte[] getSkyLight();

	/**
	 * Gets the region that this chunk is located in
	 *
	 * @return
	 */
	public abstract Region getRegion();

	/**
	 * Gets the entities in the chunk at the last snapshot
	 *
	 * @return the entities
	 */
	@SnapshotRead
	public abstract Set<Entity> getEntities();
}
