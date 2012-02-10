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
import org.spout.api.geo.discrete.Pointm;
import org.spout.api.geo.discrete.atomic.Transform;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3m;

/**
 * This class performs ray tracing and iterates along blocks on a line
 */
public class BlockIterator implements Iterator<Block> {
	// TODO -- need to actually code this :)

	@SuppressWarnings("unused")
	private final Pointm position;
	private final Vector3m direction;
	private final Block[] blockBuffer = new Block[3];
	private int bufferSize = 0;
	private int blocksRead;
	private int maxDistance;
	private boolean done = false;

	/**
	 * Constructs the BlockIterator
	 *
	 * @param world The world to use for tracing
	 * @param eye the eyeline to trace
	 * @param yOffset The trace begins vertically offset from the start vector
	 *            by this value
	 * @param maxDistance This is the maximum distance in blocks for the trace.
	 *            Setting this value above 140 may lead to problems with
	 *            unloaded chunks. A value of 0 indicates no limit
	 *
	 */
	public BlockIterator(World world, Transform pos, int maxDistance) {
		position = new Pointm(pos.getPosition());
		direction = new Vector3m(MathHelper.getDirectionVector(pos.getRotation()));

		float max = Math.abs(direction.getX());
		max = Math.abs(direction.getY()) > max ? Math.abs(direction.getY()) : max;
		max = Math.abs(direction.getZ()) > max ? Math.abs(direction.getY()) : max;

		if (max == 0) {
			throw new IllegalArgumentException("Direction may not be a zero vector");
		}

		direction.multiply(1 / max);

		blocksRead = 0;
		this.maxDistance = maxDistance;
	}

	public boolean hasNext() {
		return !done && blocksRead < maxDistance;
	}

	public Block next() {
		if (done) {
			throw new IllegalStateException("Iterator has already completed");
		}
		if (bufferSize == 0) {
			//updateBuffer();
		}
		Block block = blockBuffer[--bufferSize];
		if (block == null) {
			done = true;
		}
		blocksRead++;
		return block;
	}

	public void remove() {
		throw new UnsupportedOperationException("Block removal is not supported by this iterator");
	}
}
