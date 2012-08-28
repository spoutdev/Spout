/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.api.util;

import java.util.Iterator;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;

/**
 * This class performs ray tracing and iterates along blocks on a line
 */
public class BlockIterator implements Iterator<Block> {
	// how many steps on a block line for each unit of length
	private static final byte STEPS_PER_UNIT = 10;
	// world to iterate in
	private final World world;
	// current position
	private Vector3 position;
	// position we're aiming for
	private final Vector3 end;
	// vector added to the position at each step
	private final Vector3 stepDirection;
	// deltas used for finding out if we're closing in on the end point
	private float lastDeltaX = Integer.MAX_VALUE;
	private float lastDeltaZ = Integer.MAX_VALUE;
	private float lastDeltaY = Integer.MAX_VALUE;

	/**
	 * Constructs the BlockIterator
	 *
	 * @param world The world to use for tracing
	 * @param pos of the starting transformation of the trace
	 * @param maxDistance This is the maximum distance in blocks for the trace.
	 *
	 */
	public BlockIterator(World world, Transform pos, int maxDistance) {
		this.world = world;
		position = pos.getPosition().floor();
		final Vector3 direction = MathHelper.getDirectionVector(pos.getRotation());
		final Vector3 normalizedDirection = direction.normalize();
		stepDirection = normalizedDirection.divide(STEPS_PER_UNIT);
		end = normalizedDirection.multiply(maxDistance).add(position).floor();
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
		world = from.getWorld();
		position = from.floor();
		end = to.floor();
		stepDirection = to.subtract(from).normalize().divide(STEPS_PER_UNIT);
	}

	@Override
	public boolean hasNext() {
		// if the delta values are decreasing, we're nearing 0
		// and we're still approaching the end position
		// else, we've passed it
		final float currentDeltaX = Math.abs(end.getX() - position.getX());
		final float currentDeltaY = Math.abs(end.getY() - position.getY());
		final float currentDeltaZ = Math.abs(end.getZ() - position.getZ());
		boolean hasNext = false;
		if (currentDeltaX < lastDeltaX
				|| currentDeltaY < lastDeltaY
				|| currentDeltaZ < lastDeltaZ) {
			hasNext = true;
		}
		lastDeltaX = currentDeltaX;
		lastDeltaY = currentDeltaY;
		lastDeltaZ = currentDeltaZ;
		return hasNext;
	}

	@Override
	public Block next() {
		final Block requested = world.getBlock(position.round(), world);
		//translate position to precisely end up at a new block
		//TODO: Make this more efficient (it needs a calculation to get to the border of the current block)
		//This requires some sort of distance calculation using a 1x1x1 bounding box
		//Perhaps use the Collision utilities for this?
		Vector3 current = position.floor();
		Vector3 next = current;
		while (next.floor().equals(current)) {
			position = position.add(stepDirection);
			next = position;
		}
		return requested;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Block removal is not supported by this iterator");
	}
}
