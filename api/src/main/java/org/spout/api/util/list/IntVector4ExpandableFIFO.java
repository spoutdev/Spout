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
package org.spout.api.util.list;

import org.spout.api.math.IntVector3;
import org.spout.api.math.IntVector4;

public class IntVector4ExpandableFIFO extends IntVector3ExpandableFIFO {
	public IntVector4ExpandableFIFO(int size) {
		super(size);
	}

	@Override
	public boolean write(int x, int y, int z) {
		return write(0, x, y, z);
	}

	public boolean write(int w, int x, int y, int z) {
		if (write + 4 > read + array.length) {
			resize(array.length + (array.length >> 1) + 1);
		}
		int size = array.length;
		array[(write++) % size] = w;
		return super.write(x, y, z);
	}

	@Override
	public void write(IntVector3 v) {
		write(0, v.getX(), v.getY(), v.getZ());
	}

	public void write(IntVector4 v) {
		write(v.getW(), v.getX(), v.getY(), v.getZ());
	}

	/**
	 * Reads a triple integer from the FIFO
	 */
	public IntVector4 read() {
		if (write > read) {
			int size = array.length;
			int w = array[(read++) % size];
			int x = array[(read++) % size];
			int y = array[(read++) % size];
			int z = array[(read++) % size];
			return new IntVector4(w, x, y, z);
		} else {
			return null;
		}
	}
}
