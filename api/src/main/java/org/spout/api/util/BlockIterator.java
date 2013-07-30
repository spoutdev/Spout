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
package org.spout.api.util;

import java.util.Iterator;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.material.block.BlockFace;
import org.spout.math.vector.Vector3;

/**
 * This class performs ray tracing and iterates along blocks on a line
 */
public class BlockIterator implements Iterator<Block> {
	// World to iterate in
	private final World world;
	// Starting position
	private final Vector3 origin;
	// Direction of the ray
	private final Vector3 direction;
	// Max distance
	private final float range;
	// Current position
	private int X, Y, Z;
	// Step in blocks
	private int stepX, stepY, stepZ;
	// Step in distance
	private float tDeltaX, tDeltaY, tDeltaZ;
	// Current distance
	private float tMaxX, tMaxY, tMaxZ;
	// The face the ray enter by
	private BlockFace face;

	/**
	 * Constructs the BlockIterator
	 *
	 * @param world The world to use for tracing
	 * @param origin The starting position of the trace
	 * @param direction The direction of the trace
	 * @param maxDistance This is the maximum distance in blocks for the trace.
	 */
	public BlockIterator(World world, Vector3 origin, Vector3 direction, float maxDistance) {
		this.world = world;
		this.origin = origin;
		this.direction = direction;
		this.range = maxDistance;
		reset();
	}

	/**
	 * Constructs the BlockIterator
	 *
	 * @param world The world to use for tracing
	 * @param pos of the starting transformation of the trace
	 * @param maxDistance This is the maximum distance in blocks for the trace.
	 */
	public BlockIterator(World world, Transform pos, float maxDistance) {
		this.world = world;
		this.origin = pos.getPosition();
		this.direction = pos.forwardVector();
		this.range = maxDistance;
		reset();
	}

	/**
	 * Constructs the BlockIterator
	 *
	 * @param from The starting point
	 * @param to The end point
	 * @throws IllegalArgumentException If the worlds from both points differ.
	 */
	public BlockIterator(Point from, Point to) {
		if (!from.getWorld().equals(to.getWorld())) {
			throw new IllegalArgumentException("Cannot iterate between worlds.");
		}

		this.world = from.getWorld();
		this.origin = from;
		this.direction = to.sub(from).normalize();
		this.range = (float) from.distance(to);

		reset();
	}

	/**
	 * Reset the iterator
	 */
	public void reset() {
		X = origin.getFloorX();
		Y = origin.getFloorY();
		Z = origin.getFloorZ();

		float dx = direction.getX();
		float dy = direction.getY();
		float dz = direction.getZ();

		stepX = dx > 0 ? 1 : -1;
		stepY = dy > 0 ? 1 : -1;
		stepZ = dz > 0 ? 1 : -1;

		tDeltaX = (dx == 0f) ? Float.MAX_VALUE : Math.abs(1f / dx);
		tDeltaY = (dy == 0f) ? Float.MAX_VALUE : Math.abs(1f / dy);
		tDeltaZ = (dz == 0f) ? Float.MAX_VALUE : Math.abs(1f / dz);

		tMaxX = (dx == 0f) ? Float.MAX_VALUE : Math.abs((X + (dx > 0 ? 1 : 0) - origin.getX()) / dx);
		tMaxY = (dy == 0f) ? Float.MAX_VALUE : Math.abs((Y + (dy > 0 ? 1 : 0) - origin.getY()) / dy);
		tMaxZ = (dz == 0f) ? Float.MAX_VALUE : Math.abs((Z + (dz > 0 ? 1 : 0) - origin.getZ()) / dz);
	}

	@Override
	public boolean hasNext() {
		return (Math.min(Math.min(tMaxX, tMaxY), tMaxZ) <= range);
	}

	@Override
	public Block next() {
		if (tMaxX < tMaxY) {
			if (tMaxX < tMaxZ) {
				X += stepX;
				tMaxX += tDeltaX;
				face = stepX > 0 ? BlockFace.NORTH : BlockFace.SOUTH;
			} else {
				Z += stepZ;
				tMaxZ += tDeltaZ;
				face = stepZ > 0 ? BlockFace.EAST : BlockFace.WEST;
			}
		} else {
			if (tMaxY < tMaxZ) {
				Y += stepY;
				tMaxY += tDeltaY;
				face = stepY > 0 ? BlockFace.BOTTOM : BlockFace.TOP;
			} else {
				Z += stepZ;
				tMaxZ += tDeltaZ;
				face = stepZ > 0 ? BlockFace.EAST : BlockFace.WEST;
			}
		}
		return world.getBlock(X, Y, Z);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Block removal is not supported by this iterator");
	}

	/**
	 * Get the targeted block if there is one. Then reset the iterator (but not the block face)
	 *
	 * @return Block
	 */
	public Block getTarget(boolean invisible) {
		Block block = null;
		while (hasNext()) {
			block = next();
			if (invisible) {
				if (!block.getMaterial().isInvisible()) {
					break;
				}
			} else {
				if (block.getMaterial().isPlacementObstacle()) {
					break;
				}
			}
		}
		reset();
		return block;
	}

	public Block getTarget() {
		return getTarget(false);
	}

	/**
	 * Get the face hit by the ray.
	 *
	 * @return BlockFace
	 */
	public BlockFace getBlockFace() {
		return face;
	}
}
