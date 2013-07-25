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

import org.spout.api.math.IntVector3;

public class IntVector3Array extends IntVector3 implements Iterable<IntVector3>, Iterator<IntVector3> {
	private final int[] x;
	private final int[] y;
	private final int[] z;
	private final int length;
	private int pos;

	public IntVector3Array(int[] x, int[] y, int[] z, int length) {
		super(0, 0, 0);
		this.x = x;
		this.y = y;
		this.z = z;
		this.length = length;
		this.pos = 0;
	}

	@Override
	public boolean hasNext() {
		return pos < length;
	}

	@Override
	public IntVector3 next() {
		super.setX(x[pos]);
		super.setY(y[pos]);
		super.setZ(z[pos]);
		pos++;
		return this;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Removal is not supported");
	}

	@Override
	public Iterator<IntVector3> iterator() {
		pos = 0;
		return this;
	}
}
