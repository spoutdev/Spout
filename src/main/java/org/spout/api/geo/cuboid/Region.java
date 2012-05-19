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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.geo.AreaChunkAccess;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.player.Player;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;
import org.spout.api.util.thread.Threadsafe;

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
	/**
	 * The number of bits to shift to go from block to region or region to block coordinates.
	 */
	public static final int BLOCK_SHIFT = REGION_SIZE_BITS + Chunk.CHUNK_SIZE_BITS;
	/**
	 * Mask to convert a block integer coordinate into the region's base
	 */
	public final static int BASE_MASK = EDGE - 1;

	public Region(World world, float x, float y, float z) {
		super(new Point(world, x, y, z), EDGE, true);
	}

	/**
	 * Gets the x-coordinate of this region as a Block coordinate
	 * @return the x-coordinate of the first block in this region
	 */
	public int getBlockX() {
		return this.getX() << BLOCK_SHIFT;
	}

	/**
	 * Gets the y-coordinate of this region as a Block coordinate
	 * @return the y-coordinate of the first block in this region
	 */
	public int getBlockY() {
		return this.getY() << BLOCK_SHIFT;
	}

	/**
	 * Gets the z-coordinate of this region as a Block coordinate
	 * @return the z-coordinate of the first block in this region
	 */
	public int getBlockZ() {
		return this.getZ() << BLOCK_SHIFT;
	}

	/**
	 * Queues all chunks for saving at the next available opportunity.
	 */
	@DelayedWrite
	public abstract void save();

	/**
	 * Performs the nessecary tasks to unload this region from the world, and
	 * all associated chunks.
	 * @param save whether to save the region and associated data.
	 */
	@DelayedWrite
	public abstract void unload(boolean save);

	/**
	 * Gets all entities with the specified type.
	 * @param type The {@link Class} for the type.
	 * @param type The type of entity.
	 * @return A set of entities with the specified type.
	 */
	@SnapshotRead
	public abstract Set<Entity> getAll(Class<? extends Controller> type);

	/**
	 * Gets all entities.
	 * @return A collection of entities.
	 */
	@SnapshotRead
	public abstract Set<Entity> getAll();

	/**
	 * Gets an entity by its id.
	 * @param id The id.
	 * @return The entity, or {@code null} if it could not be found.
	 */
	@SnapshotRead
	public abstract Entity getEntity(int id);

	@LiveRead
	public abstract Set<Player> getPlayers();

	/**
	 * Gets a read-only copy of the nearest players in range of the entity specified. The list
	 * returned will NOT be in order of nearest to not as nearest player. If this matters to you,
	 * consider using getNearestPlayer instead.
	 * @param position of the center
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public Set<Player> getNearbyPlayers(Point position, int range) {
		return getNearbyPlayers(position, null, range);
	}

	/**
	 * Gets a read-only copy of the nearest players in range of the entity specified. The list
	 * returned will NOT be in order of nearest to not as nearest player. If this matters to you,
	 * consider using getNearestPlayer instead.
	 * @param entity marking the center and which is ignored
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public Set<Player> getNearbyPlayers(Entity entity, int range) {
		return getNearbyPlayers(entity.getPosition(), entity, range);
	}

	/**
	 * Gets a read-only copy of the nearest players in range of the entity specified. The list
	 * returned will NOT be in order of nearest to not as nearest player. If this matters to you,
	 * consider using getNearestPlayer instead.
	 * @param position of the center
	 * @param ignore Entity to ignore
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public Set<Player> getNearbyPlayers(Point position, Entity ignore, int range) {
		Set<Player> foundPlayers = new HashSet<Player>();
		final int RANGESQUARED = range * range;

		//Cut down on work by getting only players in the same world.
		for (Player plr : getWorld().getPlayers()) {
			//Do not count the entity passing in as a closest player if that entity is a player
			if (plr == null || plr.getEntity() == null || plr.getEntity() == ignore || plr.getEntity().getTransform() == new Transform(Point.invalid, Quaternion.IDENTITY, Vector3.ZERO)) {
				continue;
			}
			double distance = MathHelper.distanceSquared(position, plr.getEntity().getPosition());
			//Only add players that are within range.
			if (distance < RANGESQUARED) {
				foundPlayers.add(plr);
			}
		}

		return Collections.unmodifiableSet(foundPlayers);
	}

	/**
	 * Gets the absolute closest player from the specified entity within a specified range.
	 * @param entity
	 * @param range
	 * @return
	 */
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Entity entity, int range) {
		Player best = null;
		Vector3 position = entity.getPosition();
		int bestDistance = Integer.MAX_VALUE;
		final int RANGESQUARED = range * range;

		//Cut down on work by getting only players in the same world.
		for (Player plr : getWorld().getPlayers()) {
			//Do not count the entity passing in as a closest player if that entity is a player
			if (plr.getEntity() == entity) {
				continue;
			}
			int distance = (int) MathHelper.distanceSquared(position, plr.getEntity().getPosition());
			if (distance < RANGESQUARED && distance < bestDistance) {
				best = plr;
				bestDistance = distance;
			}
		}

		return best;
	}
}
