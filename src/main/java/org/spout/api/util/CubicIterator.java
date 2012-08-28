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
public class CubicIterator extends IntVector3 implements Iterator<IntVector3> {
	
	private final IntVector3 bottom;
	private final IntVector3 top;
	private boolean hasNext;
	private boolean first = true;
	
	public CubicIterator() {
		this(0, 0, 0);
	}
	
	public CubicIterator(int w) {
		this(0, 0, 0, w);
	}
	
	public CubicIterator(int x, int y, int z) {
		this(x, y, z, Integer.MAX_VALUE);
	}
	
	public CubicIterator(int x, int y, int z, int w) {
		this(x - w, y - w, z - w, x + w, x + w, x + w);
	}
	

	
	public CubicIterator(int bx, int by, int bz, int tx, int ty, int tz) {
		super(bx, by, bz);
		if (bx > tx || by > ty || bz > tz) {
			throw new IllegalArgumentException("Bottom coordinates must be less than top coordinates");
		}
		bottom = new IntVector3(bx, by, bz);
		top = new IntVector3(tx, ty, tz);
		first = true;
		this.hasNext = true;
	}

	/**
	 * Resets the iterator to the cuboid from (bx, by, bz) to (tx, ty, tz)
	 * 
	 * @param bx
	 * @param by
	 * @param bz
	 * @param tx
	 * @param ty
	 * @param tz
	 */
	public void reset(int bx, int by, int bz, int tx, int ty, int tz) {
		if (bx > tx || by > ty || bz > tz) {
			throw new IllegalArgumentException("Bottom coordinates must be less than top coordinates");
		}
		bottom.set(bx, by, bz);
		super.set(bottom);
		top.set(tx, ty, tz);
		first = true;
		hasNext = true;
	}
	
	public void reset(int x, int y, int z, int w) {
		reset(x - w, y - w, z - w, x + w, y + w, z + w);
	}
	
	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public IntVector3 next() {
		// First block is always the central block
		int x = getX();
		int y = getY();
		int z = getZ();
		
		if (first) {
			first = false;
		} else {
			if (x >= top.getX()) {
				setX(x = bottom.getX());
				if (y >= top.getY()) {
					setY(y = bottom.getY());
					if (z >= top.getZ()) {
						throw new IllegalStateException("Iterator reached end");
					} else {
						setZ(++z);
					}
				} else {
					setY(++y);
				}
			} else {
				setX(++x);
			}
		}
		if (x >= top.getX() && z >= top.getZ() && y >= top.getY()) {
			hasNext = false;
		}
		return this;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("This operation is not supported");
	}

}
