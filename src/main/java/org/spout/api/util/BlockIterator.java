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
	private Point position;
	private Vector3 direction;
	private Vector3 stepDirection;
	private int blocksRead;
	private int maxDistance;

	/**
	 * Constructs the BlockIterator
	 *
	 * @param world The world to use for tracing
	 * @param pos of the starting transformation of the trace
	 * @param maxDistance This is the maximum distance in blocks for the trace.
	 *            Setting this value above 140 may lead to problems with
	 *            unloaded chunks. A value of 0 indicates no limit
	 *
	 */
	public BlockIterator(World world, Transform pos, int maxDistance) {
		this.position = new Point(pos.getPosition());
		this.direction = new Vector3(MathHelper.getDirectionVector(pos.getRotation()));

		float length = this.direction.length();

		if (length < 0.001f) {
			throw new IllegalArgumentException("Direction may not be a zero vector");
		}

		this.direction = this.direction.divide(length);
		this.stepDirection = this.direction.multiply(0.01f);

		blocksRead = 0;
		this.maxDistance = maxDistance;
	}

	public boolean hasNext() {
		return blocksRead < maxDistance;
	}

	public Block next() {
		Block current = this.position.getWorld().getBlock(this.position);
		blocksRead++;
		//translate position to precisely end up at a new block
		//TODO: Make this more efficient (it needs a calculation to get to the border of the current block)
		//This requires some sort of distance calculation using a 1x1x1 bounding box
		//Perhaps use the Collision utilities for this?

		Vector3 currentblock = this.position.floor();
		Vector3 newblock = currentblock;
		while (currentblock.equals(newblock)) {
			this.position = this.position.add(this.stepDirection);
			newblock = this.position.floor();
		}

		return current;
	}

	public void remove() {
		throw new UnsupportedOperationException("Block removal is not supported by this iterator");
	}
}
