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

public class IntVector3ExpandableFIFO extends IntVector3FIFO {
	public IntVector3ExpandableFIFO(int size) {
		super(size);
	}

	/**
	 * Writes the 3 coordinates to the FIFO
	 *
	 * @return true if the FIFO is full
	 */
	@Override
	public boolean write(int x, int y, int z) {
		if (write + 3 > read + array.length) {
			resize(array.length + (array.length >> 1) + 1);
		}
		super.write(x, y, z);
		return false;
	}

	/**
	 * Gets if the FIFO is full
	 *
	 * @return true if the fifo is full
	 */
	@Override
	public boolean isFull() {
		return false;
	}

	protected void resize(int newSize) {
		int[] newArray = new int[newSize];

		if (read != write) {
			int size = array.length;
			int start = read % size;
			int end = write % size;

			if (start < end) {
				System.arraycopy(array, start, newArray, 0, end - start);
			} else {
				System.arraycopy(array, start, newArray, 0, size - start);
				System.arraycopy(array, 0, newArray, size - start, end);
			}
		}
		write -= read;
		read = 0;
		array = newArray;
	}
}
