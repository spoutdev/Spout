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
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;

/**
 * Represents a Cuboid shaped volume that is located somewhere in a world.
 */
public class Cuboid {
	protected Point base;
	protected Vector3 size;
	protected final boolean immutable;
	private final int x;
	private final int y;
	private final int z;
	
	/**
	 * Hashcode caching
	 */
	private volatile boolean hashed = false;
	private volatile int hashcode = 0;
	
	public Cuboid(Point base, Vector3 size) {
		this(base, size, false);
	}

	public Cuboid(Point base, Vector3 size, boolean immutable) {
		this.base = new Point(base);
		this.size = new Vector3(size);
		this.immutable = immutable;
		this.x = getXRaw();
		this.y = getYRaw();
		this.z = getZRaw();
	}

	public Point getBase() {
		return base;
	}

	public Vector3 getSize() {
		return size;
	}

	public int getX() {
		if (immutable) {
			return x;
		} else {
			return getXRaw();
		}
	}
	
	private int getXRaw() {
		return (int) (base.getX() / size.getX());
	}

	public int getY() {
		if (immutable) {
			return y;
		} else {
			return getYRaw();
		}
	}
	
	private int getYRaw() {
		return (int) (base.getY() / size.getY());
	}

	public int getZ() {
		if (immutable) {
			return z;
		} else {
			return getZRaw();
		}
	}
	
	private int getZRaw() {
		return (int) (base.getZ() / size.getZ());
	}

	public World getWorld() {
		return base.getWorld();
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
