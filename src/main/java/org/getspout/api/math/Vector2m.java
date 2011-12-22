package org.getspout.api.math;

public class Vector2m extends Vector2 {

	public Vector2m(float x, float y) {
		super(x, y);
	}

	/**
	 * Sets the X coordinate
	 *
	 * @param x The x coordinate
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Sets the Y coordinate
	 *
	 * @param y The Y coordinate
	 */
	public void setY(float y) {
		this.y = y;
	}

	@Override
	public Vector2 add(Vector2 that) {
		x += that.x;
		y += that.y;
		return this;
	}

	@Override
	public Vector2 subtract(Vector2 that) {
		x -= that.x;
		y -= that.y;
		return this;
	}

	@Override
	public Vector2 scale(float scale) {
		x *= scale;
		y *= scale;
		return this;
	}

	public Vector2 cross(Vector2 that) {
		float tmp = y;
		y = -x;
		x = tmp;
		return this;
	}

	@Override
	public Vector2 normalize() {
		float length = this.length();
		x *= 1 / length;
		y *= 1 / length;
		return this;
	}
}
