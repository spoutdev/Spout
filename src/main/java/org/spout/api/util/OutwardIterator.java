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

import org.spout.api.math.IntVector3;

/**
 * An Iterator that iterates outwards from a given central 3d integer coordinate.<br>
 * <br>
 * The Manhattan distance from the given center to the coordinates in the sequence increases monotonically and the iterator passes through all integer coordinates.
 */
public class OutwardIterator extends IntVector3 implements Iterator<IntVector3> {
	
	private final IntVector3 center;
	private final IntVector3 step;
	private boolean first = true;
	
	public OutwardIterator() {
		this(0, 0, 0);
	}
	
	public OutwardIterator(int x, int y, int z) {
		super(x, y, z);
		center = new IntVector3(x, y, z);
		step = new IntVector3(0, 0, 0);
		first = true;
	}

	/**
	 * Resets the iterator and positions it at (x, y, z)
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void reset(int x, int y, int z) {
		super.setX(x);
		super.setY(y);
		super.setZ(z);
		center.setX(x);
		center.setY(y);
		center.setZ(z);
		first = true;
	}
	
	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public IntVector3 next() {
		// First block is always the central block
		if (first) {
			step.setX(0);
			step.setZ(0);
			first = false;
		} else {
			int dx = getX() - center.getX();
			int dy = getY() - center.getY();
			int dz = getZ() - center.getZ();
			
			// Last block was top of layer, move to start of next layer
			if (dx == 0 && dz == 0 && dy >= 0) {
				setY((center.getY() << 1) - getY() - 1);
				step.setX(0);
				step.setZ(0);
			} else if (dx == 0) {
				// Reached end of horizontal slice
				// Move up to next slice
				if (dz >= 0) {
					step.setX(1);
					step.setZ(-1);

					setY(getY() + 1);

					// Bottom half of layer
					if (dy < 0) {
						setZ(getZ() + 1);
					// Top half of layer
					} else {
						setZ(getZ() - 1);
					// Reached top of layer
						if (getZ() == center.getZ()) {
							step.setX(0);
							step.setZ(0);
						}
					}
				// Change direction (50% of horizontal slice complete)
				} else {
					step.setX(-1);
					step.setZ(1);
				}
			} else if (dz == 0) {
				// Change direction (25% of horizontal slice complete)
				if (dx > 0) {
					step.setX(-1);
					step.setZ(-1);
				// Change direction (75% of horizontal slice compete)
				} else {
					step.setX(1);
					step.setZ(1);
				}
			}
		}
		add(step);
		return this;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("This operation is not supported");
	}

}
