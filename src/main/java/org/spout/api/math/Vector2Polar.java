/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.math;

/**
 * A 2-dimensional vector represented by float-precision r,theta coordinates
 *
 * Theta is in Radians!
 *
 * Note, this is the Immutable form of Vector2Polar. All operations will construct a
 * new Vector2Polar.
 */
public class Vector2Polar implements Comparable<Vector2Polar> {
	/**
	 * Represents the Zero vector (0 at 0 degrees)
	 */
	public final static Vector2Polar ZERO = new Vector2Polar(0, 0);
	/**
	 * Represents the unit vector (1 at 0 degrees)
	 */
	public final static Vector2Polar UNIT = new Vector2Polar(1, 0);
	private final static float twoPi = (float) (Math.PI * 2);
	private final static float ninetyDegrees = (float) (Math.PI / 4);
	protected float r;
	protected float theta;

	/**
	 * Constructs and initializes a Vector2Polar from the given r, theta
	 *
	 * @param r the r coordinate
	 * @param theta the theta coordinate
	 */
	public Vector2Polar(float r, float theta) {
		this.r = r;
		this.theta = Vector2Polar.getRealAngle(theta);
	}

	/**
	 * Constructs and initializes a Vector2Polar from the given r, theta
	 *
	 * @param r the r coordinate
	 * @param theta the theta coordinate
	 */
	public Vector2Polar(double r, double theta) {
		this((float) r, (float) theta);
	}

	/**
	 * Constructs and initializes a Vector2Polar from the given r, theta
	 *
	 * @param r the r coordinate
	 * @param theta the theta coordinate
	 */
	public Vector2Polar(int r, int theta) {
		this((float) r, (float) theta);
	}

	/**
	 * Constructs and initializes a Vector2Polar from an old Vector2Polar
	 *
	 * @param o
	 */
	public Vector2Polar(Vector2Polar o) {
		this(o.r, o.theta);
	}

	/**
	 * Constructs and initializes a Vector2Polar to (0,0)
	 */
	public Vector2Polar() {
		this(0, 0);
	}

	/**
	 * Gets the length of the vector from 0, 0
	 * @return
	 */
	public float getR() {
		return r;
	}

	/**
	 * Gets the angle of the vector from the positive X axis
	 * Vector2(1, 0) == Vector2Polar(1, 0)
	 * @return
	 */
	public float getTheta() {
		return theta;
	}

	/**
	 * Gets the X coordinate of this vector in the cartesian system.
	 * Uses Math.cos(), so it should be used as little as possible.
	 */
	public float getX() {
		return (float) (r * Math.cos(theta));
	}

	/**
	 * Gets the Y coordinate of this vector in the cartesian system.
	 * Uses Math.sin(), so it should be used as little as possible.
	 */
	public float getY() {
		return (float) (r * Math.sin(theta));
	}

	/**
	 * Adds this Vector2Polar to the value of the Vector2Polar argument
	 *
	 * @param that The Vector2Polar to add
	 * @return the new Vector2Polar
	 */
	public Vector2Polar add(Vector2Polar that) {
		return Vector2Polar.add(this, that);
	}

	/**
	 * Adds a Vector2Polar comprised of the given r, theta values
	 *
	 * @param r
	 * @param theta
	 * @return
	 */
	public Vector2Polar add(float r, float theta) {
		return add(new Vector2Polar(r, theta));
	}

	/**
	 * Adds a Vector2Polar comprised of the given r, theta values
	 *
	 * @param r
	 * @param theta
	 * @return
	 */
	public Vector2Polar add(double r, double theta) {
		return add(new Vector2Polar(r, theta));
	}

	/**
	 * Adds a Vector2Polar comprised of the given r, theta values
	 *
	 * @param r
	 * @param theta
	 * @return
	 */
	public Vector2Polar add(int r, int theta) {
		return add(new Vector2Polar(r, theta));
	}

	/**
	 * Subtracts this Vector2Polar to the value of the Vector2Polar argument
	 *
	 * @param that The Vector2Polar to subtract
	 * @return the new Vector2Polar
	 */
	public Vector2Polar subtract(Vector2Polar that) {
		return Vector2Polar.subtract(this, that);
	}

	/**
	 * Subtracts a Vector2Polar comprised of the given r, theta values
	 *
	 * @param r
	 * @param theta
	 * @return
	 */
	public Vector2Polar subtract(float r, float theta) {
		return subtract(new Vector2Polar(r, theta));
	}

	/**
	 * Subtracts a Vector2Polar comprised of the given r, theta values
	 *
	 * @param r
	 * @param theta
	 * @return
	 */
	public Vector2Polar subtract(double r, double theta) {
		return subtract(new Vector2Polar(r, theta));
	}

	/**
	 * Subtracts a Vector2Polar comprised of the given r, theta values
	 *
	 * @param r
	 * @param theta
	 * @return
	 */
	public Vector2Polar subtract(int r, int theta) {
		return subtract(new Vector2Polar(r, theta));
	}

	/**
	 * Multiplies this Vector2Polar to the value of the Vector2Polar argument
	 *
	 * @param that The Vector2Polar to multiply
	 * @return the new Vector2Polar
	 */
	public Vector2Polar multiply(Vector2Polar that) {
		return Vector2Polar.multiply(this, that);
	}

	/**
	 * Multiplies a Vector2Polar comprised of the given r, theta values
	 *
	 * @param r
	 * @param theta
	 * @return
	 */
	public Vector2Polar multiply(float r, float theta) {
		return multiply(new Vector2Polar(r, theta));
	}

	/**
	 * Multiplies a Vector2Polar comprised of the given r, theta values
	 *
	 * @param r
	 * @param theta
	 * @return
	 */
	public Vector2Polar multiply(double r, double theta) {
		return multiply(new Vector2Polar(r, theta));
	}

	/**
	 * Multiplies a Vector2Polar comprised of the given r, theta values
	 *
	 * @param r
	 * @param theta
	 * @return
	 */
	public Vector2Polar multiply(int r, int theta) {
		return multiply(new Vector2Polar(r, theta));
	}

	/**
	 * Multiplies a Vector2Polar by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector2Polar multiply(float val) {
		return multiply(new Vector2Polar(val, 0));
	}

	/**
	 * Multiplies a Vector2Polar by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector2Polar multiply(double val) {
		return multiply(new Vector2Polar(val, 0));
	}

	/**
	 * Multiplies a Vector2Polar by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector2Polar multiply(int val) {
		return multiply(new Vector2Polar(val, 0));
	}

	/**
	 * Divides the given Vector2Polar from this Vector2Polar
	 *
	 * @param that The Vector2Polar to divide
	 * @return the new Vector2Polar
	 */
	public Vector2Polar divide(Vector2Polar that) {
		return Vector2Polar.divide(this, that);
	}

	/**
	 * Divides a Vector2Polar comprised of the given r, theta values
	 *
	 * @param x
	 * @param theta
	 * @return
	 */
	public Vector2Polar divide(float r, float theta) {
		return divide(new Vector2Polar(r, theta));
	}

	/**
	 * Divides a Vector2Polar comprised of the given r, theta values
	 *
	 * @param r
	 * @param theta
	 * @return
	 */
	public Vector2Polar divide(double r, double theta) {
		return divide(new Vector2Polar(r, theta));
	}

	/**
	 * Divides a Vector2Polar comprised of the given r, theta values
	 *
	 * @param r
	 * @param theta
	 * @return
	 */
	public Vector2Polar divide(int r, int theta) {
		return divide(new Vector2Polar(r, theta));
	}

	/**
	 * Divides a Vector2Polar by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector2Polar divide(float val) {
		return divide(new Vector2Polar(val, 0));
	}

	/**
	 * Divides a Vector2Polar by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector2Polar divide(double val) {
		return divide(new Vector2Polar(val, 0));
	}

	/**
	 * Divides a Vector2Polar by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector2Polar divide(int val) {
		return divide(new Vector2Polar(val, 0));
	}

	/**
	 * Returns this Vector2Polar dot the Vector2Polar argument.
	 *
	 * @param that The Vector2Polar to dot with this.
	 * @return The dot product
	 */
	public float dot(Vector2Polar that) {
		return Vector2Polar.dot(this, that);
	}

	/**
	 * Returns a Vector2 with the same x, y coordinates as this vector
	 * @return
	 */
	public Vector2 toVector2() {
		return Vector2Polar.toVector2(this);
	}

	/**
	 * Returns a Vector2m with the same x, y coordinates as this vector
	 * @return
	 */
	public Vector2m toVector2m() {
		return Vector2Polar.toVector2m(this);
	}

	/**
	 * Returns the Cross Product of this Vector2Polar Note: Cross Product is
	 * undefined in 2d space. This returns the orthogonal vector to this vector
	 *
	 * @return The orthogonal vector to this vector.
	 */
	public Vector2Polar cross() {
		return Vector2Polar.cross(this);
	}

	/**
	 * Rounds the values of this Vector2Polar up to
	 * the nearest integer value.
	 *
	 * @return
	 */
	public Vector2Polar ceil() {
		return Vector2Polar.ceil(this);
	}

	/**
	 * Rounds the values of this Vector2Polar down to
	 * the nearest integer value.
	 *
	 * @return
	 */
	public Vector2Polar floor() {
		return Vector2Polar.floor(this);
	}

	/**
	 * Rounds the values of this Vector2Polar to
	 * the nearest integer value.
	 *
	 * @return
	 */
	public Vector2Polar round() {
		return Vector2Polar.round(this);
	}

	/**
	 * Gets the distance between this Vector2Polar and a given Vector2Polar.
	 *
	 * @param a
	 * @return
	 */
	public double distance(Vector2Polar a) {
		return Vector2Polar.distance(a, this);
	}

	/**
	 * Returns this Vector2Polar in an array. Element 0 contains r Element 1 contains
	 * theta
	 *
	 * @return The array containing this Vector2Polar
	 */
	public float[] toArray() {
		return Vector2Polar.toArray(this);
	}

	/**
	 * Compares two Vector2Polars
	 */
	public int compareTo(Vector2Polar o) {
		return Vector2Polar.compareTo(this, o);
	}

	/**
	 * Checks if two Vector2Polars are equal
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Vector2Polar)) {
			return false;
		}
		if (this == o) {
			return true;
		}
		Vector2Polar polar = (Vector2Polar) o;
		return polar.r == this.r && polar.theta == this.theta;
	}

	/**
	 * Generates a hashcode for this Vector
	 */
	@Override
	public int hashCode() {
		int hash = 5;
		hash = 59 * hash + Float.floatToIntBits(this.r);
		hash = 59 * hash + Float.floatToIntBits(this.theta);
		return hash;
	}

	@Override
	public String toString() {
		return "(" + r + ", " + theta + " radians)";
	}

	/**
	 * Adds one Vector2Polar to the other Vector2Polar
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2Polar add(Vector2Polar a, Vector2Polar b) {
		return new Vector2(a.getX() + b.getX(), a.getY() + b.getY()).toVector2Polar();
	}

	/**
	 * Subtracts one Vector2Polar from the other Vector2Polar
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2Polar subtract(Vector2Polar a, Vector2Polar b) {
		return new Vector2(a.getX() - b.getX(), a.getY() - b.getY()).toVector2Polar();
	}

	/**
	 * Multiplies one Vector2Polar by the other Vector2Polar
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2Polar multiply(Vector2Polar a, Vector2Polar b) {
		return new Vector2Polar(a.r * b.r, a.theta + b.theta);
	}

	/**
	 * Divides one Vector2Polar by the other Vector2Polar
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2Polar divide(Vector2Polar a, Vector2Polar b) {
		return new Vector2Polar(a.r / b.r, a.theta - b.theta);
	}

	/**
	 * Calculates the Dot Product of two Vector2Polars
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static float dot(Vector2Polar a, Vector2Polar b) {
		return (float) (a.r * b.r * Math.cos(a.theta - b.theta));
	}

	/**
	 * Returns a Vector2m object at the same coordinate.
	 *
	 * @param o Vector2Polar to use as the x/z values
	 * @return
	 */
	public static Vector2 toVector2(Vector2Polar o) {
		return new Vector2(o.getX(), o.getY());
	}

	/**
	 * Returns a Vector2m object at the same coordinate.
	 *
	 * @param o Vector2Polar to use as the x/z values
	 * @return
	 */
	public static Vector2m toVector2m(Vector2Polar o) {
		return new Vector2m(o.getX(), o.getY());
	}

	/**
	 * Returns the Cross Product of this Vector2Polar Note: Cross Product is
	 * undefined in 2d space. This returns the orthogonal vector to this vector
	 *
	 * @return The orthogonal vector to this vector.
	 */
	public static Vector2Polar cross(Vector2Polar o) {
		return new Vector2Polar(o.r, o.theta + ninetyDegrees);
	}

	/**
	 * Rounds the values of the given Vector2Polar up to
	 * the nearest integer value.
	 *
	 * @param o Vector2Polar to use
	 * @return
	 */
	public static Vector2Polar ceil(Vector2Polar o) {
		return new Vector2Polar(Math.ceil(o.r), Math.ceil(o.theta));
	}

	/**
	 * Rounds the values of the given Vector2Polar down to
	 * the nearest integer value.
	 *
	 * @param o Vector2Polar to use
	 * @return
	 */
	public static Vector2Polar floor(Vector2Polar o) {
		return new Vector2Polar(Math.floor(o.r), Math.floor(o.theta));
	}

	/**
	 * Rounds the values of the given Vector2Polar to
	 * the nearest integer value.
	 *
	 * @param o Vector2Polar to use
	 * @return
	 */
	public static Vector2Polar round(Vector2Polar o) {
		return new Vector2Polar(Math.round(o.r), Math.round(o.theta));
	}

	/**
	 * Returns a Vector2Polar containing the smallest X and Y values.
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static Vector2Polar min(Vector2Polar o1, Vector2Polar o2) {
		return new Vector2Polar(Math.min(o1.r, o2.r), Math.min(o1.theta, o2.theta));
	}

	/**
	 * Returns a Vector2Polar containing the largest X and Y values.
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static Vector2Polar max(Vector2Polar o1, Vector2Polar o2) {
		return new Vector2Polar(Math.max(o1.r, o2.r), Math.max(o1.theta, o2.theta));
	}

	/**
	 * Returns a Vector2Polar with random values (between 0 and twoPi)
	 *
	 * @param o
	 * @return
	 */
	public static Vector2Polar rand() {
		return new Vector2Polar(Math.random(), Math.random() * twoPi);
	}

	/**
	 * Returns the provided Vector2Polar in an array. Element 0 contains r Element 1
	 * contains theta
	 *
	 * @return The array containing the Vector2Polar
	 */
	public static float[] toArray(Vector2Polar a) {
		return new float[]{a.r, a.theta};
	}

	/**
	 * Compares two Vector3s
	 */
	public static int compareTo(Vector2Polar a, Vector2Polar b) {
		return (int) a.r - (int) b.r;
	}

	/**
	 * Gets the distance between two Vector2Polar.
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static double distance(Vector2Polar a, Vector2Polar b) {
		double aR2 = a.r * a.r;
		double bR2 = b.r * b.r;
		double cosMagic = 2 * Vector2Polar.dot(a, b);
		return Math.sqrt(aR2 + bR2 - cosMagic);
	}

	/**
	 * Checks if two Vector2Polars are equal
	 */
	public static boolean equals(Object a, Object b) {
		return a.equals(b);
	}

	/**
	 * Gets the smallest angle possible from 0. Converts stuff like 7pi/4, etc
	 * to the minimum angle with the same position.
	 *
	 * @param theta
	 * @return
	 */
	public static float getRealAngle(float theta) {
		float out = 0;
		if (theta < 0) {
			for (float i = theta; i <= (twoPi); i = i + twoPi) {
				out = i;
			}
		} else if (theta < twoPi) {
			return theta;
		} else {
			for (float i = theta; i > 0; i = i - twoPi) {
				out = i;
			}
		}
		return out;
	}
}
