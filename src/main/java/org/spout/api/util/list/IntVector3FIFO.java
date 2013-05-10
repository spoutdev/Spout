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
package org.spout.api.util.list;

import org.spout.api.math.IntVector3;

public class IntVector3FIFO {
	
	protected int[] array;
	protected int write;
	protected int read;
	
	public IntVector3FIFO(int size) {
		this(size, false);
	}
	
	public IntVector3FIFO(int size, boolean fifo) {
		this.array = new int[size * 3];
		this.read = 0;
		this.write = 0;
	}
	
	/**
	 * Writes the 3 coordinates to the FIFO
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return true if the FIFO is full
	 */
	public boolean write(int x, int y, int z) {
		int size = array.length;
		array[(write++) % size] = x;
		array[(write++) % size] = y;
		array[(write++) % size] = z;
		int maxWrite = read + array.length;
		if (write > maxWrite) {
			throw new IllegalStateException("FIFO is full");
		}
		return write == maxWrite;
	}
	
	/**
	 * Writes the integer vector to the FIFO
	 * 
	 * @param v
	 */
	public void write(IntVector3 v) {
		write(v.getX(), v.getY(), v.getZ());
	}
	
	/**
	 * Reads a triple integer from the FIFO
	 * 
	 * @return
	 */
	public IntVector3 read() {
		if (write > read) {
			int size = array.length;
			int x = array[(read++) % size];
			int y = array[(read++) % size];
			int z = array[(read++) % size];
			return new IntVector3(x, y, z);
		} else {
			return null;
		}
	}
	
	/**
	 * Clears the FIFO
	 */
	public void clear() {
		read = 0;
		write = 0;
	}

}
