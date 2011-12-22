package org.getspout.api.geo.discrete;

import org.getspout.api.geo.World;
import org.getspout.api.math.Vector3;
import org.getspout.api.math.Vector3m;

/**
 * Represents a position in a world and and direction
 */
public class Eyelinem extends Eyeline {

	public Eyelinem(Point position, Vector3 direction) {
		super(position, direction);
	}

	public Eyelinem(World world, double px, double py, double pz, double dx, double dy, double dz) {
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
	
	/**
	 * Sets the position equal to a point
	 */
	public void setPoint(Point point) {
		this.world = point.world;
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}
	
	/**
	 * Sets this Eyeline equal to another Eyeline
	 */
	public void setEyeline(Eyeline eyeline) {
		setPoint(eyeline);
		direction.setX(eyeline.getDirX());
		direction.setY(eyeline.getDirY());
		direction.setZ(eyeline.getDirZ());
	}
}
