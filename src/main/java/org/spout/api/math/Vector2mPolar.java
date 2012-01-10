package org.spout.api.math;

public class Vector2mPolar extends Vector2Polar {

	public Vector2mPolar() {
	}

	public Vector2mPolar(Vector2Polar o) {
		super(o);
	}

	public Vector2mPolar(int r, int theta) {
		super(r, theta);
	}

	public Vector2mPolar(double r, double theta) {
		super(r, theta);
	}

	public Vector2mPolar(float r, float theta) {
		super(r, theta);
	}

	/**
	 * Sets the length of the vector
	 * 
	 * @param r 
	 */
	public void setR(float r) {
		this.r = r;
	}

	/**
	 * Sets the angle of the vector
	 * @param theta 
	 */
	public void setTheta(float theta) {
		this.theta = Vector2Polar.getRealAngle(theta);
	}

}
