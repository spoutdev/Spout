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

import org.spout.api.geo.World;
import org.spout.api.geo.discrete.atomic.AtomicPoint;
import org.spout.api.math.Vector3;

/**
 * Represents a mutable position in a World
 */
public class Pointm extends Point {
	public Pointm() {
		super(null, 0, 0, 0);
	}

	public Pointm(World world, float x, float y, float z) {
		super(world, x, y, z);
	}

	public Pointm(Point point) {
		super(point.getWorld(), point.getX(), point.getY(), point.getZ());
	}

	public Pointm(Pointm point) {
		super(point.world, point.x, point.y, point.z);
	}

	@Override
	public Point add(Point other) {
		if (this.world != other.world) {
			throw new IllegalArgumentException("Cannot add two points in seperate worlds");
		}
		return add((Vector3) other);
	}

	public Point add(Pointm other) {
		if (this.world != other.world) {
			throw new IllegalArgumentException("Cannot add two points in seperate worlds");
		}
		this.x += other.x;
		this.y += other.y;
		this.z += other.z;
		return this;
	}

	@Override
	public Point add(Vector3 other) {
		this.x += other.getX();
		this.y += other.getY();
		this.z += other.getZ();
		return this;
	}

	/**
	 * Sets the world that this point is contained it
	 */
	public void setWorld(World world) {
		this.world = world;
	}

	/**
	 * Sets the x coordinate of this point
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Sets the y coordinate of this point
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Sets the z coordinate of this point
	 */
	public void setZ(float z) {
		this.z = z;
	}

	/**
	 * Sets this point equal to another point
	 */
	public void set(Point point) {
		this.world = point.world;
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}

	/**
	 * Sets this point equal to another pointm
	 */
	protected void set(Pointm point) {
		this.world = point.world;
		this.x = point.x;
		this.y = point.y;
		this.z = point.y;
	}

	/**
	 * Sets this point equal to another pointm
	 */
	protected void set(AtomicPoint point) {
		this.world = point.world;
		this.x = point.x;
		this.y = point.y;
		this.z = point.y;
	}
}
