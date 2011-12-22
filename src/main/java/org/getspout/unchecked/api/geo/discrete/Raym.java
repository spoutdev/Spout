package org.getspout.unchecked.api.geo.discrete;

import org.getspout.unchecked.api.geo.World;
import org.getspout.unchecked.api.math.Vector3;

/**
 * Represents a position in a world and and direction
 */
public class Raym extends Ray {

	public Raym(Point position, Vector3 direction) {
		super(position, direction);
	}

	public Raym(World world, double px, double py, double pz, double dx, double dy, double dz) {
		super(world, px, py, pz, dx, dy, dz);
	}

	/**
	 * Sets the world that this point is contained it
	 */
	public void setWorld(World world) {
		this.world = world;
	}

	/**
	 * Sets the x coordinate of this point
	 *
	 * @param x the x coordinate
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Sets the y coordinate of this point
	 *
	 * @param y the y coordinate
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Sets the z coordinate of this point
	 *
	 * @param z the z coordinate
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * Sets the X directional coordinate
	 *
	 * @param x the x component
	 */
	public void setDirX(double x) {
		direction.setX(x);
	}

	/**
	 * Sets the Y directional coordinate
	 *
	 * @param y the y component
	 */
	public void setDirY(double y) {
		direction.setY(y);
	}

	/**
	 * Sets the Z directional coordinate
	 *
	 * @param z the z component
	 */
	public void setDirZ(double z) {
		direction.setZ(z);
	}

}
