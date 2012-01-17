/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.geo.cuboid;

import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;

/**
 * Represents a Cuboid that can be moved in discrete steps based on its size
 *
 * The size of the Cuboid may also be altered and all new movements will be made
 * relative to a grid based on that size.
 */
public interface MutableCuboid extends MovableCuboid {
	/**
	 * Sets the base of the Cuboid
	 *
	 * @param base the base of the Cuboid
	 */
	public void setBase(Point base);

	/**
	 * Sets the Size of the Cuboid
	 *
	 * @param size the size of the Cuboid
	 */
	public void setSize(Vector3 size);
}
