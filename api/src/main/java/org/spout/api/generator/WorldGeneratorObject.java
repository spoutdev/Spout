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
package org.spout.api.generator;

import org.spout.api.geo.World;

/**
 * Represents an Object for a WorldGenerator
 */
public abstract class WorldGeneratorObject {
	/**
	 * Verify if the object can be placed at the given coordinates.
	 *
	 * @param w The world w.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 * @return true if the object can be placed, false if it can't.
	 */
	public abstract boolean canPlaceObject(World w, int x, int y, int z);

	/**
	 * Place this object into the world at the given coordinates.
	 *
	 * @param w The world w.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 */
	public abstract void placeObject(World w, int x, int y, int z);

	/**
	 * Attempts placement of this object into the world at the given
	 * coordinates. The attempt will fail if
	 * {@link #canPlaceObject(org.spout.api.geo.World, int, int, int)} returns
	 * false.
	 *
	 * @param w The world w.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 * @return True if the object was placed, false if otherwise.
	 */
	public boolean tryPlaceObject(World w, int x, int y, int z) {
		if (canPlaceObject(w, x, y, z)) {
			placeObject(w, x, y, z);
			return true;
		}
		return false;
	}
}
