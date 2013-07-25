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
package org.spout.api.inventory.util;

import org.spout.api.inventory.shape.Grid;
import java.util.Iterator;

/**
 * Represents an {@link Iterator} of a grid that iterates through the indexes in 
 * the correct order of an {@link org.spout.api.inventory.Inventory}
 */
public class GridIterator implements Iterator<Integer> {
	/**
	 * The {@link Grid} to iterate through
	 */
	protected final Grid grid;
	/**
	 * The current index of the grid
	 */
	protected int index = -1;
	/**
	 * The current position on the x axis of the grid
	 */
	protected int x = -1;
	/**
	 * The current position on the y axis of the grid
	 */
	protected int y = 0;

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
		return index < grid.getSize() - 1;
	}

	@Override
	public Integer next() {
		if (x < grid.getLength() - 1) {
			x++;
		} else if (y < grid.getHeight() - 1) {
			x = 0;
			y++;
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
		} else if (x == 0 && y == 0) {
			x--;
		} else {
			throw new IndexOutOfBoundsException("Cannot decrement cursor beyond 0.");
		}
		index--;
	}
}
