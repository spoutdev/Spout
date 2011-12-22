package org.getspout.unchecked.api.geo.discrete;

import org.getspout.unchecked.api.geo.World;
import org.getspout.unchecked.api.math.Vector3;

public class RayScalem extends RayScale {

	public RayScalem(Point position, Vector3 direction, Vector3 scale) {
		super(position, direction, scale);
	}

	public RayScalem(World world, double px, double py, double pz, double dx, double dy, double dz, double sx, double sy, double sz) {
		super(world, px, py, pz, dx, dy, dz, sx, sy, sz);
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
	 * Sets the forward scaling component
	 *
	 * @param up the forward scaling component
	 */
	public void setScaleForward(double z) {
		scale.setZ(z);
	}

	/**
	 * Sets the left scaling component
	 *
	 * @param up the left scaling component
	 */
	public void setScaleLeft(double y) {
		scale.setY(y);
	}

	/**
	 * Sets the vertical scaling component
	 *
	 * @param up the vertical scaling component
	 */
	public void setScaleUp(double up) {
		scale.setX(up);
	}

}
