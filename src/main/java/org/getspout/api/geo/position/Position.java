package org.getspout.api.geo.position;

import org.getspout.api.geo.World;
import org.getspout.api.geo.point.Point;
import org.getspout.api.geo.point.Pointm;
import org.getspout.api.math.Vector3;
import org.getspout.api.math.Vector3m;

/**
 * Represents a position in a world.  
 * 
 * TODO need to add mutable position
 */
public class Position {

	protected final static Vector3 UNIT_SCALE = new Vector3(1, 1, 1);

	protected final Pointm location;
	protected final Vector3m direction;
	protected final Vector3m scale;
	
	public Position(Point location, Vector3 direction, Vector3 scale) {
		this.location = new Pointm(location);
		this.direction = new Vector3m(direction);
		this.scale = new Vector3m(scale);
	}

	public Position(World world, double lx, double ly, double lz, double dx, double dy, double dz) {
		this (world, lx, ly, lz, dx, dy, dz, 1.0D, 1.0D, 1.0D);
	}

	public Position(World world, double lx, double ly, double lz, double dx, double dy, double dz, double sx, double sy, double sz) {
		this(new Pointm(world, lx, ly, lz), new Vector3m(dx, dy, dz), new Vector3m(sx, sy, sz));
	}

	/**
	 * Gets the X positional coordinate
	 * 
	 * @return the world
	 */
	public double getX() {
		return location.getX();
	}
	
	/**
	 * Gets the Y positional coordinate
	 * 
	 * @return the world
	 */
	public double getY() {
		return location.getY();
	}
	
	/**
	 * Gets the Z positional coordinate
	 * 
	 * @return the world
	 */
	public double getZ() {
		return location.getZ();
	}
	
	/**
	 * Gets the X directional coordinate
	 * 
	 * @return the world
	 */
	public double getDirX() {
		return location.getY();
	}
	
	/**
	 * Gets the Y directional coordinate
	 * 
	 * @return the world
	 */
	public double getDirY() {
		return location.getY();
	}
	
	/**
	 * Gets the Z directional coordinate
	 * 
	 * @return the world
	 */
	public double getDirZ() {
		return location.getY();
	}
	
	/**
	 * Gets the forward scaling component
	 * 
	 * @return the world
	 */
	public double getScaleForward() {
		return location.getZ();
	}
	
	/**
	 * TODO should this be right?
	 * 
	 * Gets the left scaling component 
	 * 
	 * @return the world
	 */
	public double getScaleLeft() {
		return location.getY();
	}
	
	/**
	 * Gets the vertical scaling component
	 * 
	 * @return the world
	 */
	public double getScaleUp() {
		return location.getY();
	}
	

}


