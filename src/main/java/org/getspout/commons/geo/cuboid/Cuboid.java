package org.getspout.commons.geo.cuboid;

import org.getspout.commons.geo.point.Point;
import org.getspout.commons.math.Vector3;

/**
 * Represents a Cuboid shaped volume that is located somewhere in a world.
 */

public class Cuboid {

	private Point base;
	private Vector3 size;
	
	public Cuboid(Point base, Vector3 size) {
		this.base = base;
		this.size = size;
	}
	
	public Point getBase() {
		return base;
	}
	
	public void setBase(Point base) {
		this.base = base;
	}
	
	public Vector3 getSize() {
		return size;
	}
	
	public void setSize(Vector3 size) {
		this.size = size;
	}
	
	public int getX() {
		return (int)(base.getX() / size.getX());
	}

	public void setX(int x) {
		base.setX(x * size.getX());
	}

	public int getY() {
		return (int)(base.getY() / size.getY());
	}

	public void setY(int y) {
		base.setY(y * size.getY());
	}
	
	public int getZ() {
		return (int)(base.getZ() / size.getZ());
	}

	public void setZ(int z) {
		base.setZ(z * size.getZ());
	}
	
}
