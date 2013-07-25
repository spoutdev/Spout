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
package org.spout.api.inventory.shape;

import org.spout.api.inventory.util.CubeIterator;

/**
 * Represents a grid that can be iterated through in the correct order of an {@link org.spout.api.inventory.Inventory}.
 */
public class Cube extends Grid {
	private static final long serialVersionUID = 1L;
	/**
	 * The width of the cube
	 */
	private final int width;

	/**
	 * Constructs a new Cube
	 *
	 * @param length of the faces
	 * @param width of the faces
	 * @param height of the faces
	 */
	public Cube(int length, int height, int width) {
		super(length, width);
		this.width = width;
	}

	/**
	 * Returns the width of the cube
	 *
	 * @return width of the cube
	 */
	public int getWidth() {
		return width;
	}

	@Override
	public int getSize() {
		return super.getSize() * width;
	}

	@Override
	public CubeIterator iterator() {
		return new CubeIterator(this);
	}
}
