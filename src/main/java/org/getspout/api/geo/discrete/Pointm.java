package org.getspout.api.geo.discrete;

import org.getspout.api.geo.World;

/**
 * Represents a mutable position in a World
 */

public class Pointm extends Point {

	public Pointm(World world, float x, float y, float z) {
		super(world, x, y, z);
	}

	public Pointm(Point point) {
		super(point.getWorld(), point.getX(), point.getY(), point.getZ());
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
	public void setPoint(Point point) {
		this.world = point.world;
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}
}
