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
package org.spout.api.geo.discrete;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spout.api.geo.World;
import org.spout.api.math.Vector3;

/**
 * Represents a position in a World
 */
public class Point extends Vector3 {
	protected World world;

	public Point(Point point) {
		super(point);
		world = point.getWorld();
	}

	public Point(Vector3 vector, World w) {
		super(vector);
		world = w;
	}

	public Point(World world, float x, float y, float z) {
		super(x, y, z);
		this.world = world;
	}

	public Point add(Point other) {
		if (world != other.world) {
			throw new IllegalArgumentException("Cannot add two points in seperate worlds");
		}
		return new Point(Vector3.add(this, other), world);
	}

	@Override
	public Point add(Vector3 other) {
		return new Point(Vector3.add(this, other), world);
	}

	/**
	 * Gets the square of the distance between two points.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either
	 * world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the Manhattan distance.
	 *
	 */
	public double getSquaredDistance(Point other) {
		if (other == null || world == null || other.world == null || !world.equals(other.world)) {
			return Double.MAX_VALUE;
		}
		double dx = x - other.x;
		double dy = y - other.y;
		double dz = z - other.z;
		return dx * dx + dy * dy + dz * dz;
	}

	/**
	 * Gets the distance between two points.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either
	 * world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the Manhattan distance.
	 *
	 */
	public double getDistance(Point other) {
		return Math.sqrt(getSquaredDistance(other));
	}

	/**
	 * Gets the Manhattan distance between two points.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either
	 * world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the Manhattan distance.
	 *
	 */
	public double getManhattanDistance(Point other) {
		if (other == null || world == null || other.world == null || !world.equals(other.world)) {
			return Double.MAX_VALUE;
		}
		return Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z);
	}

	/**
	 * Gets the largest distance between two points, when projected onto one of
	 * the axes.
	 *
	 * This will return Double.MAX_VALUE if the other Point is null, either
	 * world is null, or the two points are in different worlds.
	 *
	 * Otherwise, it returns the max distance.
	 */
	public double getMaxDistance(Point other) {
		if (other == null || world == null || other.world == null || !world.equals(other.world)) {
			return Double.MAX_VALUE;
		}
		return Math.max(Math.abs(x - other.x), Math.max(Math.abs(y - other.y), Math.abs(z - other.z)));
	}

	/**
	 * Gets the world this point is locate in
	 *
	 * @return the world
	 */
	public World getWorld() {
		return world;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(5033, 61).appendSuper(super.hashCode()).append(world).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		} else if (!(obj instanceof Point)) {
			return false;
		} else {
			Point point = (Point) obj;

			return point.world.equals(world) && point.x == x && point.y == y && point.z == z;
		}

	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + world + ", " + super.toString() + "}";
	}
}
