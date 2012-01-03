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
