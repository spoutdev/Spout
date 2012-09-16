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

import java.util.Iterator;

/**
 * Represents an {@link Iterator} of a grid that iterates through the indexes in the correct order of an {@link org.spout.api.inventory.Inventory}
 */
public class GridIterator implements Iterator<Integer> {
	/**
	 * The {@link Grid} to iterate through
	 */
	private final Grid grid;
	/**
	 * The current index of the grid
	 */
	private int index = -1;
	/**
	 * The current position on the x axis of the grid
	 */
	private int x = -1;
	/**
	 * The current position on the y axis of the grid
	 */
	private int y = 0;

	/**
	 * Constructs a new grid iterator
	 * @param grid to iterate through
	 */
	public GridIterator(Grid grid) {
		this.grid = grid;
	}

	/**
	 * Gets the current index of the grid
	 * @return index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Gets the current position on the x axis
	 * @return x axis position
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the current position on the y axis
	 * @return y axis position
	 */
	public int getY() {
		return y;
	}

	@Override
	public boolean hasNext() {
		return index != grid.getSize() - 1;
	}

	@Override
	public Integer next() {
		if (x != grid.getLength() - 1) {
			x++;
		} else {
			x = 0;
			y++;
		}
		return ++index;
	}

	@Override
	public void remove() {
		index--;
	}
}
