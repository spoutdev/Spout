package org.getspout.api.geo.point;

import org.getspout.api.geo.World;

/**
 * Represents a mutable point located in a World
 */

public class Pointm extends Point {
	
	public Pointm(World world, double x, double y, double z) {
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
	public void setX(double x) {
		this.x = x;
	}
	
	/**
	 * Sets the y coordinate of this point
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * Sets the z coordinate of this point
	 */
	public void setZ(double z) {
		this.z = z;
	}

}
