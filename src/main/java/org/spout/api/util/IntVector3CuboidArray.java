/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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

import org.spout.api.math.IntVector3;

public class IntVector3CuboidArray extends IntVector3 implements Iterable<IntVector3>, Iterator<IntVector3>{
	
	private final int[] bx;
	private final int[] by;
	private final int[] bz;
	private final int[] tx;
	private final int[] ty;
	private final int[] tz;
	private final int length;
	private int pos;
	private int x;
	private int y;
	private int z;
	
	public IntVector3CuboidArray(int[] bx, int[] by, int[] bz, int[] tx, int[] ty, int[] tz, int length) {
		super(0, 0, 0);
		this.bx = bx;
		this.by = by;
		this.bz = bz;
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
		this.length = length;
		this.pos = 0;
		iterator();
	}

	@Override
	public boolean hasNext() {
		return pos < length;
	}

	@Override
	public IntVector3 next() {
		super.setX(x);
		super.setY(y);
		super.setZ(z);
		x++;
		if (x >= tx[pos]) {
			x = bx[pos];
			y++;
			if (y >= ty[pos]) {
				y = by[pos];
				z++;
				if (z >= tz[pos]) {
					pos++;
					if (pos < length) {
						x = bx[pos];
						y = by[pos];
						z = bz[pos]; 
					}
				}
			}
		}
		
		return this;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Removal is not supported");
	}

	@Override
	public Iterator<IntVector3> iterator() {
		pos = 0;
		if (length > 0) {
			x = bx[0];
			y = by[0];
			z = bz[0];
		}
		return this;
	}

}
