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

public class IntVector3Stack {
	
	protected int[] array;
	protected int stackPointer;
	
	public IntVector3Stack(int size) {
		this(size, false);
	}
	
	public IntVector3Stack(int size, boolean fifo) {
		this.array = new int[size * 3];
		this.stackPointer = 0;
	}
	
	/**
	 * Pushes the 3 coordinates onto the stack
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return true if the stack is full
	 */
	public boolean push(int x, int y, int z) {
		array[stackPointer++] = z;
		array[stackPointer++] = y;
		array[stackPointer++] = x;
		return stackPointer == array.length;
	}
	
	/**
	 * Pushes the integer vector onto the stack
	 * 
	 * @param v
	 */
	public void push(IntVector3 v) {
		push(v.getX(), v.getY(), v.getZ());
	}
	
	/**
	 * Pops a triple integer from the stack
	 * 
	 * @return
	 */
	public IntVector3 pop() {
		if (stackPointer > 0) {
			return new IntVector3(array[--stackPointer], array[--stackPointer], array[--stackPointer]);
		} else {
			return null;
		}
	}
	
	/**
	 * Pops a single integer from the stack.  The order that the coords are pushed onto
	 * the stack is z, y and then x.
	 * 
	 * @return
	 */
	public int popSingle() {
		return array[--stackPointer];
	}
	
	/**
	 * Clears the stack
	 */
	public void clear() {
		stackPointer = 0;
	}

}
