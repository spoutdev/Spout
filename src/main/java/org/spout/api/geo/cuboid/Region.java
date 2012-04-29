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

import java.util.Set;

import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.geo.AreaChunkAccess;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.SnapshotRead;

/**
 * Represents a cube containing 16x16x16 Chunks (256x256x256 Blocks)
 */
public abstract class Region extends Cube implements AreaChunkAccess {
	/**
	 * Number of chunks on a side of a region
	 */
	public static final int REGION_SIZE = 16;

	/**
	 * Number of bits in {@link #REGION_SIZE}
	 */
	public static final int REGION_SIZE_BITS = 4;

	/**
	 * Number of blocks on a side of a region
	 */
	public final static int EDGE = 256;

	public Region(World world, float x, float y, float z) {
		super(new Point(world, x, y, z), EDGE, true);
	}

	/**
	 * Queues all chunks for saving at the next available opportunity.
	 */
	@DelayedWrite
	public abstract void save();

	/**
	 * Performs the nessecary tasks to unload this region from the world, and
	 * all associated chunks.
	 *
	 * @param save whether to save the region and associated data.
	 */
	@DelayedWrite
	public abstract void unload(boolean save);

	/**
	 * Gets all entities with the specified type.
	 *
	 * @param type The {@link Class} for the type.
	 * @param type The type of entity.
	 * @return A set of entities with the specified type.
	 */
	@SnapshotRead
	public abstract Set<Entity> getAll(Class<? extends Controller> type);

	/**
	 * Gets all entities.
	 *
	 * @return A collection of entities.
	 */
	@SnapshotRead
	public abstract Set<Entity> getAll();

	/**
	 * Gets an entity by its id.
	 *
	 * @param id The id.
	 * @return The entity, or {@code null} if it could not be found.
	 */
	@SnapshotRead
	public abstract Entity getEntity(int id);
}
