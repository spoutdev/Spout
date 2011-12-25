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
}
