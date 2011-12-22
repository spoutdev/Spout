package org.getspout.api.geo.discrete;

import org.getspout.api.geo.World;
import org.getspout.api.math.MathHelper;
import org.getspout.api.math.Vector3;
import org.getspout.api.math.Vector3m;

/**
 * Represents a position in a world and and direction
 */
public class Eyeline extends Point {

	protected final static Vector3 UNIT_SCALE = Vector3.ONE;

	protected final Vector3m direction;

	public Eyeline(Point position, Vector3 direction) {
		super(position);
		this.direction = new Vector3m(direction);
	}

	public Eyeline(World world, double px, double py, double pz, double dx, double dy, double dz) {
		this(new Pointm(world, px, py, pz), new Vector3m(dx, dy, dz));
	}

	public Eyeline(Point position, double pitch, double yaw) {
		this(position, MathHelper.getDirectionVector(pitch, yaw));
	}
	
	/**
	 * Gets the direction of the ray as a vector
	 * 
	 * @return the direction as a vector
	 */
	public Vector3 getDirection() {
		return direction;
	}

	/**
	 * Gets the X directional coordinate
	 *
	 * @return the x direction
	 */
	public double getDirX() {
		return direction.getX();
	}

	/**
	 * Gets the Y directional coordinate
	 *
	 * @return the y direction
	 */
	public double getDirY() {
		return direction.getY();
	}

	/**
	 * Gets the Z directional coordinate
	 *
	 * @return the z direction
	 */
	public double getDirZ() {
		return direction.getZ();
	}

}
