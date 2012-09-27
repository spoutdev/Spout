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
package org.spout.api.inventory.util;

import org.spout.api.inventory.shape.Cube;

/**
 * Represents an {@link java.util.Iterator} of a grid that iterates through the
 * indexes in the correct order of an {@link org.spout.api.inventory.Inventory}.
 */
public class CubeIterator extends GridIterator {
	/**
	 * The z coordinate of the index
	 */
	private int z = 0;
	
	/**
	 * Constructs a new CubeIterator 
	 * 
	 * @param cube to iterate through 
	 */
	public CubeIterator(Cube cube) {
		super(cube);
	}
	
	/**
	 * Returns the 'z' coordinate of the iterator.
	 * 
	 * @return z coordinate
	 */
	public int getZ() {
		return z;
	}
	
	@Override
	public Integer next() {	
		if (x < grid.getLength() - 1) {
			x++;
		} else if (y < grid.getHeight() - 1) {
			x = 0;
			y++;
		} else if (z < ((Cube) grid).getWidth() - 1) {
			x = 0;
			y = 0;
			z++;
		} else {
			throw new IndexOutOfBoundsException("Cannot increment cursor beyond it's final index.");
		}
		return ++index;
	}
	
	@Override
	public void remove() {
		if (x > 0) {
			x--;
		} else if (y > 0) {
			x = grid.getLength() - 1;
			y--;
		} else if (z > 0) {
			x = grid.getLength() - 1;
			y = grid.getHeight() - 1;
			z--;
		} else if (x == 0 && y == 0 && z == 0) {
			x--;
		} else {
			throw new IndexOutOfBoundsException("Cannot decrement cursor beyond 0.");
		}
		index--;
	}
}