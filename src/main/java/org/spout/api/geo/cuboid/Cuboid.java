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
package org.spout.api.geo.cuboid;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spout.api.geo.World;
import org.spout.api.geo.WorldSource;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;

/**
 * Represents a Cuboid shaped volume that is located somewhere in a world.
 */
public class Cuboid implements WorldSource {
	protected final Point base;
	protected final Vector3 size;
	private final int x;
	private final int y;
	private final int z;

	/**
	 * Hashcode caching
	 */
	private volatile boolean hashed = false;
	private volatile int hashcode = 0;

	/**
	 * Vertex cache
	 */
	private Vector3[] vertices = null;

	/**
	 * Constructs a cubiod with the point as the base point, and
	 * 
	 * @param base
	 * @param size
	 */
	public Cuboid(Point base, Vector3 size) {
		this.base = base;
		this.size = size;
		this.x = (int) (base.getX() / size.getX());
		this.y = (int) (base.getY() / size.getY());
		this.z = (int) (base.getZ() / size.getZ());
	}

	public Point getBase() {
		return base;
	}

	public Vector3 getSize() {
		return size;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	@Override
	public World getWorld() {
		return base.getWorld();
	}

	/**
	 * Returns the vertices of this Cuboid.
	 * 
	 * @return The vertices
	 */
	public Vector3[] getVertices() {
		if (vertices == null) {
			vertices = new Vector3[8];

			// Front
			vertices[0] = new Vector3(base.getX(), base.getY(), base.getZ() + size.getZ());
			vertices[1] = new Vector3(base.getX() + size.getX(), base.getY(), base.getZ() + size.getZ());
			vertices[2] = new Vector3(base.getX() + size.getX(), base.getY() + size.getY(), base.getZ() + size.getZ());
			vertices[3] = new Vector3(base.getX(), base.getY() + size.getY(), base.getZ() + size.getZ());
			// Back
			vertices[4] = new Vector3(base.getX(), base.getY(), base.getZ());
			vertices[5] = new Vector3(base.getX() + size.getX(), base.getY(), base.getZ());
			vertices[6] = new Vector3(base.getX() + size.getX(), base.getY() + size.getY(), base.getZ());
			vertices[7] = new Vector3(base.getX(), base.getY() + size.getY(), base.getZ());
		}

		return vertices;
	}

	public boolean contains(Vector3 vec) {
		Vector3 max = base.add(size);
		return (base.getX() <= vec.getX() && vec.getX() < max.getX()) && (base.getY() <= vec.getY() && vec.getY() < max.getY()) && (base.getZ() <= vec.getZ() && vec.getZ() < max.getZ());
	}

	@Override
	public int hashCode() {
		if (!hashed) {
			hashcode = new HashCodeBuilder(563, 21).append(base).append(size).toHashCode();
			hashed = true;
		}
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		} else if (!(obj instanceof Cuboid)) {
			return false;
		} else {
			Cuboid cuboid = (Cuboid) obj;

			return cuboid.size.getX() == size.getX() && cuboid.size.getY() == size.getY() && cuboid.size.getZ() == size.getZ() && cuboid.getWorld().equals(getWorld()) && cuboid.getX() == getX() && cuboid.getY() == getY() && cuboid.getZ() == getZ();
		}

	}

	@Override
	public String toString() {
		return "Cuboid[" + size.getX() + ", " + size.getY() + ", " + size.getZ() + "]@[" + getX() + ", " + getY() + ", " + getZ() + "]";
	}
}
