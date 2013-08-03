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
import java.util.NoSuchElementException;

import org.spout.api.math.IntVector3;

/**
 * An Iterator that iterates outwards from a given central 3d integer coordinate.<br> <br> The Manhattan distance from the given center to the coordinates in the sequence increases monotonically and
 * the iterator passes through all integer coordinates.
 */
public class FlatIterator extends IntVector3 implements Iterator<IntVector3> {
	private static final long serialVersionUID = 1L;
	private final IntVector3 center;
	private final IntVector3 step;
	private int distance;
	private int endDistance;
	private int top;
	private boolean hasNext;
	private boolean first = true;

	public FlatIterator() {
		this(0, 0, 0, 1);
	}

	public FlatIterator(int x, int base, int z, int height) {
		this(x, z, base, height, Integer.MAX_VALUE);
	}

	public FlatIterator(int x, int base, int z, int height, int maxDistance) {
		super(x, base, z);
		center = new IntVector3(x, base, z);
		top = base + height - 1;
		step = new IntVector3(0, 0, 0);
		first = true;
		distance = 0;
		this.endDistance = maxDistance;
		this.hasNext = true;
	}

	/**
	 * Resets the iterator and positions it at (x, y, z)
	 */
	public void reset(int x, int y, int z, int height) {
		super.setX(x);
		super.setY(y);
		super.setZ(z);
		center.setX(x);
		center.setY(y);
		center.setZ(z);
		top = y + height - 1;
		first = true;
		hasNext = true;
		distance = 0;
	}

	public void reset(int x, int y, int z, int height, int endDistance) {
		this.endDistance = endDistance;
		reset(x, y, z, height);
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public IntVector3 next() {
		if (!this.hasNext) {
			throw new NoSuchElementException("The Outward Iterator ran out of elements");
		}
		// First block is always the central block
		if (first) {
			step.setX(0);
			step.setY(0);
			step.setZ(0);
			first = false;
			if (this.endDistance <= 0) {
				this.hasNext = false;
			}
		} else {
			int dx = getX() - center.getX();
			int dy = getY() - center.getY();
			int dz = getZ() - center.getZ();

			if (getY() == top) {
				step.setY(-dy);

				if (dz > 0) {
					if (dx > 0) {
						step.setX(1); //
						step.setZ(-1); //
					} else if (dx == 0) {
						step.setX(1); //
						step.setZ(0); //
						distance++;
					} else {
						step.setX(1); //
						step.setZ(1); //
					}
				} else if (dz == 0) {
					if (dx > 0) {
						step.setX(-1); //
						step.setZ(-1); //
					} else if (dx == 0) {
						step.setX(1); //
						step.setZ(0); //
						distance++;
					} else {
						step.setX(1); //
						step.setZ(1); //
					}
				} else {
					if (dx > 0) {
						step.setX(-1); //
						step.setZ(-1); //
					} else if (dx == 0) {
						step.setX(-1); //
						step.setZ(1); //
					} else {
						step.setX(-1); //
						step.setZ(1); //
					}
				}
			} else if (getY() == center.getY()) {
				step.setY(1);
				step.setX(0);
				step.setZ(0);
			}
			add(step);
			if (getY() == top && dx == 0 && dz >= endDistance) {
				hasNext = false;
			}
		}
		return this;
	}

	public int getDistance() {
		return distance;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("This operation is not supported");
	}
}
