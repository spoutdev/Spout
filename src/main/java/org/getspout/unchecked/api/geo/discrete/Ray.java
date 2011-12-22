package org.getspout.unchecked.api.geo.discrete;

import org.getspout.unchecked.api.geo.World;
import org.getspout.unchecked.api.math.MathHelper;
import org.getspout.unchecked.api.math.Vector3;
import org.getspout.unchecked.api.math.Vector3m;

/**
 * Represents a position in a world and and direction
 */
public class Ray extends Point {

	protected final static Vector3 UNIT_SCALE = Vector3.ONE;

	protected final Vector3m direction;

	public Ray(Point position, Vector3 direction) {
		super(position);
		this.direction = new Vector3m(direction);
	}

	public Ray(World world, double px, double py, double pz, double dx, double dy, double dz) {
		this(new Pointm(world, px, py, pz), new Vector3m(dx, dy, dz));
	}

	public Ray(Point position, double pitch, double yaw) {
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
