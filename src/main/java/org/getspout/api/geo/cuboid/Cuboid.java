/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.geo.discrete.Pointm;
import org.getspout.api.math.Vector3;
import org.getspout.api.math.Vector3m;

/**
 * Represents a Cuboid shaped volume that is located somewhere in a world.
 */
public class Cuboid {

	protected Pointm base;
	protected Vector3m size;

	public Cuboid(Point base, Vector3 size) {
		this.base = new Pointm(base);
		this.size = new Vector3m(size);
	}

	public Point getBase() {
		return base;
	}

	public Vector3 getSize() {
		return size;
	}

	public int getX() {
		return (int) (base.getX() / size.getX());
	}

	public int getY() {
		return (int) (base.getY() / size.getY());
	}

	public int getZ() {
		return (int) (base.getZ() / size.getZ());
	}
	
	public World getWorld() {
		return base.getWorld();
	}
	
	public int hashCode() {
		int hash = getX();
		hash += (hash << 5) + getY();
		hash += (hash << 5) + getZ();
		hash += (hash << 5) + getWorld().getUID().getLeastSignificantBits();
		hash += (hash << 5) + getWorld().getUID().getMostSignificantBits();
		return hash;
	}
	
	public boolean equals(Object obj) {
		
		if (obj == null) {
			return false;
		} else if (!(obj instanceof Cuboid)) {
			return false;
		} else {
			Cuboid cuboid = (Cuboid)obj;
			
			return cuboid.size.getX() == size.getX() && cuboid.size.getY() == size.getY() && cuboid.size.getZ() == size.getZ() && cuboid.getWorld().equals(getWorld()) && cuboid.getX() == getX() && cuboid.getY() == getY() && cuboid.getZ() == getZ();
		}
		
	}
	
	public String toString() {
		return "Cuboid[" + size.getX() + ", " + size.getY() + ", " + size.getZ() + "]@[" + getX() + ", " + getY() + ", " + getZ() + "]";
	}
}
