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

import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spout.api.util.StringUtil;

/**
 * A 2-dimensional vector represented by float-precision x,y coordinates
 *
 * Note, this is the Immutable form of Vector2. All operations will construct a
 * new Vector2.
 */
public class Vector2 implements Comparable<Vector2>, Serializable{
	/**
	 * Represents the Zero vector (0,0)
	 */
	public final static Vector2 ZERO = new Vector2(0, 0);
	/**
	 * Represents a unit vector in the X direction (1,0)
	 */
	public final static Vector2 UNIT_X = new Vector2(1, 0);
	/**
	 * Represents a unit vector in the Y direction (0,1)
	 */
	public final static Vector2 UNIT_Y = new Vector2(0, 1);
	/**
	 * Represents a unit vector (1,1)
	 */
	public final static Vector2 ONE = new Vector2(1, 1);
	
	/**
	 * Hashcode caching
	 */
	private transient volatile boolean hashed = false;
	private transient volatile int hashcode = 0;
	
	
	protected final float x;
	protected final float y;

	/**
	 * Constructs and initializes a Vector2 from the given x, y
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructs and initializes a Vector2 from the given x, y
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Vector2(double x, double y) {
		this((float) x, (float) y);
	}

	/**
	 * Constructs and initializes a Vector2 from the given x, y
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Vector2(int x, int y) {
		this((float) x, (float) y);
	}

	/**
	 * Constructs and initializes a Vector2 from an old Vector2
	 *
	 * @param o
	 */
	public Vector2(Vector2 o) {
		this(o.x, o.y);
	}

	/**
	 * Constructs and initializes a Vector2 to (0,0)
	 */
	public Vector2() {
		this(0, 0);
	}

	/**
	 * Gets the X coordinate
	 *
	 * @return The X coordinate
	 */
	public float getX() {
		return x;
	}

	/**
	 * Gets the Y coordinate
	 *
	 * @return The Y coordinate
	 */
	public float getY() {
		return y;
	}

	/**
	 * Adds this Vector2 to the value of the Vector2 argument
	 *
	 * @param that The Vector2 to add
	 * @return the new Vector2
	 */
	public Vector2 add(Vector2 that) {
		return Vector2.add(this, that);
	}

	/**
	 * Adds a Vector2 comprised of the given x, y values
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2 add(float x, float y) {
		return add(new Vector2(x, y));
	}

	/**
	 * Adds a Vector2 comprised of the given x, y values
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2 add(double x, double y) {
		return add(new Vector2(x, y));
	}

	/**
	 * Adds a Vector2 comprised of the given x, y values
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2 add(int x, int y) {
		return add(new Vector2(x, y));
	}

	/**
	 * Subtracts this Vector2 to the value of the Vector2 argument
	 *
	 * @param that The Vector2 to subtract
	 * @return the new Vector2
	 */
	public Vector2 subtract(Vector2 that) {
		return Vector2.subtract(this, that);
	}

	/**
	 * Subtracts a Vector2 comprised of the given x, y values
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2 subtract(float x, float y) {
		return subtract(new Vector2(x, y));
	}

	/**
	 * Subtracts a Vector2 comprised of the given x, y values
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2 subtract(double x, double y) {
		return subtract(new Vector2(x, y));
	}

	/**
	 * Subtracts a Vector2 comprised of the given x, y values
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2 subtract(int x, int y) {
		return subtract(new Vector2(x, y));
	}

	/**
	 * Multiplies this Vector2 to the value of the Vector2 argument
	 *
	 * @param that The Vector2 to multiply
	 * @return the new Vector2
	 */
	public Vector2 multiply(Vector2 that) {
		return Vector2.multiply(this, that);
	}

	/**
	 * Multiplies a Vector2 comprised of the given x, y values
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2 multiply(float x, float y) {
		return multiply(new Vector2(x, y));
	}

	/**
	 * Multiplies a Vector2 comprised of the given x, y values
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2 multiply(double x, double y) {
		return multiply(new Vector2(x, y));
	}

	/**
	 * Multiplies a Vector2 comprised of the given x, y values
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2 multiply(int x, int y) {
		return multiply(new Vector2(x, y));
	}

	/**
	 * Multiplies a Vector2 by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector2 multiply(float val) {
		return multiply(new Vector2(val, val));
	}

	/**
	 * Multiplies a Vector2 by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector2 multiply(double val) {
		return multiply(new Vector2(val, val));
	}

	/**
	 * Multiplies a Vector2 by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector2 multiply(int val) {
		return multiply(new Vector2(val, val));
	}

	/**
	 * Divides the given Vector2 from this Vector2
	 *
	 * @param that The Vector2 to divide
	 * @return the new Vector2
	 */
	public Vector2 divide(Vector2 that) {
		return Vector2.divide(this, that);
	}

	/**
	 * Divides a Vector2 comprised of the given x, y values
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2 divide(float x, float y) {
		return divide(new Vector2(x, y));
	}

	/**
	 * Divides a Vector2 comprised of the given x, y values
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2 divide(double x, double y) {
		return divide(new Vector2(x, y));
	}

	/**
	 * Divides a Vector2 comprised of the given x, y values
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2 divide(int x, int y) {
		return divide(new Vector2(x, y));
	}

	/**
	 * Divides a Vector2 by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector2 divide(float val) {
		return divide(new Vector2(val, val));
	}

	/**
	 * Divides a Vector2 by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector2 divide(double val) {
		return divide(new Vector2(val, val));
	}

	/**
	 * Divides a Vector2 by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector2 divide(int val) {
		return divide(new Vector2(val, val));
	}

	/**
	 * Returns this Vector2 dot the Vector2 argument. Dot Product is defined as
	 * a.x*b.x + a.y*b.y
	 *
	 * @param that The Vector2 to dot with this.
	 * @return The dot product
	 */
	public float dot(Vector2 that) {
		return Vector2.dot(this, that);
	}

	/**
	 * Returns a Vector3 object with a y-value of 0. The x of this Vector2
	 * becomes the x of the Vector3, the y of this Vector2 becomes the z of the
	 * Vector3.
	 *
	 * @return
	 */
	public Vector3 toVector3() {
		return Vector2.toVector3(this);
	}

	/**
	 * Returns a Vector2Polar object with the same value as this Vector2
	 *
	 * @return
	 */
	public Vector2Polar toVector2Polar() {
		return new Vector2Polar(length(), Math.atan2(y, x));
	}

	/**
	 * Returns a Vector3 object with the given y value. The x of this Vector2
	 * becomes the x of the Vector3, the y of this Vector2 becomes the z of the
	 * Vector3.
	 *
	 * @param y Y value to use in the new Vector3.
	 * @return
	 */
	public Vector3 toVector3(float y) {
		return Vector2.toVector3(this, y);
	}

	/**
	 * Returns the Cross Product of this Vector2 Note: Cross Product is
	 * undefined in 2d space. This returns the orthogonal vector to this vector
	 *
	 * @return The orthogonal vector to this vector.
	 */
	public Vector2 cross() {
		return Vector2.cross(this);
	}

	/**
	 * Rounds the X and Y values of this Vector2 up to the nearest integer
	 * value.
	 *
	 * @return
	 */
	public Vector2 ceil() {
		return new Vector2(Math.ceil(x), Math.ceil(y));
	}

	/**
	 * Rounds the X and Y values of this Vector2 down to the nearest integer
	 * value.
	 *
	 * @return
	 */
	public Vector2 floor() {
		return new Vector2(Math.floor(x), Math.floor(y));
	}

	/**
	 * Rounds the X and Y values of this Vector2 to the nearest integer value.
	 *
	 * @return
	 */
	public Vector2 round() {
		return new Vector2(Math.round(x), Math.round(y));
	}

	/**
	 * Sets the X and Y values of this Vector2 to their absolute value.
	 *
	 * @return
	 */
	public Vector2 abs() {
		return new Vector2(Math.abs(x), Math.abs(y));
	}

	/**
	 * Gets the distance between this Vector2 and a given Vector2.
	 *
	 * @param a
	 * @return
	 */
	public double distance(Vector2 a) {
		return Vector2.distance(a, this);
	}

	/**
	 * Gets the squared distance between this Vector2 and a given Vector2.
	 *
	 * @param a
	 * @return
	 */
	public double distanceSquared(Vector2 a) {
		return Vector2.distanceSquared(a, this);
	}

	/**
	 * Raises the X and Y values of this Vector2 to the given power.
	 *
	 * @param power
	 * @return
	 */
	public Vector2 pow(double power) {
		return Vector2.pow(this, power);
	}

	/**
	 * Calculates the length of this Vector2 squared.
	 *
	 * @return the squared length
	 */
	public float lengthSquared() {
		return Vector2.lengthSquared(this);
	}

	/**
	 * Calculates the length of this Vector2 Note: This makes use of the sqrt
	 * function, and is not cached. That could affect performance
	 *
	 * @return the length of this vector2
	 */
	public float length() {
		return Vector2.length(this);
	}

	/**
	 * Returns this Vector2 where the length is equal to 1
	 *
	 * @return This Vector2 with length 1
	 */
	public Vector2 normalize() {
		return Vector2.normalize(this);
	}

	/**
	 * Returns this Vector2 in an array. Element 0 contains x Element 1 contains
	 * y
	 *
	 * @return The array containing this Vector2
	 */
	public float[] toArray() {
		return Vector2.toArray(this);
	}

	/**
	 * Compares two Vector3s
	 */
	public int compareTo(Vector2 o) {
		return Vector2.compareTo(this, o);
	}

	/**
	 * Checks if two Vector2s are equal
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Vector2)) {
			return false;
		}
		return this == o || compareTo(this, (Vector2) o) == 0;
	}

	/**
	 * Generates a hashCode for these two values
	 *
	 * @return
	 */
	@Override
	public int hashCode() {
		if (!hashed) {
			hashcode = new HashCodeBuilder(5, 59).append(x).append(y).toHashCode();
			hashed = true;
		}
		return hashcode;
	}

	@Override
	public String toString() {
		return StringUtil.toString(this.x, this.y);
	}

	/**
	 * Returns the length of the provided Vector2 Note: This makes use of the
	 * sqrt function, and is not cached. This could affect performance.
	 *
	 * @param a The Vector2 to calculate the length of
	 * @return The length of the Vector2
	 */
	public static float length(Vector2 a) {
		return (float) Math.sqrt(lengthSquared(a));
	}

	/**
	 * Returns the length squared of the provided Vector2
	 *
	 * @param a the Vector2 to calculate the length squared
	 * @return the length squared of the Vector2
	 */
	public static float lengthSquared(Vector2 a) {
		return Vector2.dot(a, a);
	}

	/**
	 * Returns a Vector2 that is the unit form of the provided Vector2
	 *
	 * @param a
	 * @return
	 */
	public static Vector2 normalize(Vector2 a) {
		return a.multiply(1.f / a.length());
	}

	/**
	 * Adds one Vector2 to the other Vector2
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2 add(Vector2 a, Vector2 b) {
		return new Vector2(a.getX() + b.getX(), a.getY() + b.getY());
	}

	/**
	 * Subtracts one Vector2 from the other Vector2
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2 subtract(Vector2 a, Vector2 b) {
		return new Vector2(a.getX() - b.getX(), a.getY() - b.getY());
	}

	/**
	 * Multiplies one Vector2 by the other Vector2
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2 multiply(Vector2 a, Vector2 b) {
		return new Vector2(a.getX() * b.getX(), a.getY() * b.getY());
	}

	/**
	 * Divides one Vector2 by the other Vector2
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2 divide(Vector2 a, Vector2 b) {
		return new Vector2(a.getX() / b.getX(), a.getY() / b.getY());
	}

	/**
	 * Scales the Vector2 by the ammount
	 *
	 * @param a
	 * @param b
	 * @return
	 * @deprecated Use {@link Vector2#multiply} instead
	 */
	@Deprecated
	public static Vector2 scale(Vector2 a, float b) {
		return Vector2.multiply(a, new Vector2(b, b));
	}

	/**
	 * Calculates the Dot Product of two Vector2s Dot Product is defined as
	 * a.x*b.x + a.y*b.y
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static float dot(Vector2 a, Vector2 b) {
		return a.getX() * b.getX() + a.getY() * b.getY();
	}

	/**
	 * Returns a Vector3 object with a y-value of 0. The x of the Vector2
	 * becomes the x of the Vector3, the y of the Vector2 becomes the z of the
	 * Vector3.
	 *
	 * @param o Vector2 to use as the x/z values
	 * @return
	 */
	public static Vector3 toVector3(Vector2 o) {
		return new Vector3(o.getX(), 0, o.getY());
	}

	/**
	 * Returns a Vector2Polar object with the same value as the given Vector2
	 *
	 * @param o Vector2 to use
	 * @return
	 */
	public static Vector2Polar toVector2Polar(Vector2 o) {
		return new Vector2Polar(o.length(), Math.atan2(o.getY(), o.getX()));
	}

	/**
	 * Returns a Vector3 object with the given y-value. The x of the Vector2
	 * becomes the x of the Vector3, the y of the Vector2 becomes the z of the
	 * Vector3.
	 *
	 * @param o Vector2 to use as the x/z values
	 * @param y Y value of the new Vector3
	 * @return
	 */
	public static Vector3 toVector3(Vector2 o, float y) {
		return new Vector3(o.getX(), y, o.getY());
	}

	/**
	 * Returns the Cross Product of this Vector2 Note: Cross Product is
	 * undefined in 2d space. This returns the orthogonal vector to this vector
	 *
	 * @return The orthogonal vector to this vector.
	 */
	public static Vector2 cross(Vector2 o) {
		return new Vector2(o.getY(), -o.getX());
	}

	/**
	 * Rounds the X and Y values of the given Vector2 up to the nearest integer
	 * value.
	 *
	 * @param o Vector2 to use
	 * @return
	 */
	public static Vector2 ceil(Vector2 o) {
		return new Vector2(Math.ceil(o.getX()), Math.ceil(o.getY()));
	}

	/**
	 * Rounds the X and Y values of the given Vector2 down to the nearest
	 * integer value.
	 *
	 * @param o Vector2 to use
	 * @return
	 */
	public static Vector2 floor(Vector2 o) {
		return new Vector2(Math.floor(o.getX()), Math.floor(o.getY()));
	}

	/**
	 * Rounds the X and Y values of the given Vector2 to the nearest integer
	 * value.
	 *
	 * @param o Vector2 to use
	 * @return
	 */
	public static Vector2 round(Vector2 o) {
		return new Vector2(Math.round(o.getX()), Math.round(o.getY()));
	}

	/**
	 * Sets the X and Y values of the given Vector2 to their absolute value.
	 *
	 * @param o Vector2 to use
	 * @return
	 */
	public static Vector2 abs(Vector2 o) {
		return new Vector2(Math.abs(o.getX()), Math.abs(o.getY()));
	}

	/**
	 * Returns a Vector2 containing the smallest X and Y values.
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static Vector2 min(Vector2 o1, Vector2 o2) {
		return new Vector2(Math.min(o1.getX(), o2.getX()), Math.min(o1.getY(), o2.getY()));
	}

	/**
	 * Returns a Vector2 containing the largest X and Y values.
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static Vector2 max(Vector2 o1, Vector2 o2) {
		return new Vector2(Math.max(o1.getX(), o2.getX()), Math.max(o1.getY(), o2.getY()));
	}

	/**
	 * Returns a Vector2 with random X and Y values (between 0 and 1)
	 *
	 * @param o
	 * @return
	 */
	public static Vector2 rand() {
		double[] rands = new double[2];
		for (int i = 0; i < 2; i++) {
			rands[i] = Math.random() * 2 - 1;
		}
		return new Vector2(rands[0], rands[1]);
	}

	/**
	 * Returns the provided Vector2 in an array. Element 0 contains x Element 1
	 * contains y
	 *
	 * @return The array containing the Vector2
	 */
	public static float[] toArray(Vector2 a) {
		return new float[] {a.getX(), a.getY()};
	}

	/**
	 * Compares two Vector3s
	 */
	public static int compareTo(Vector2 a, Vector2 b) {
		return (int) a.lengthSquared() - (int) b.lengthSquared();
	}

	/**
	 * Gets the distance between two Vector2.
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static double distance(Vector2 a, Vector2 b) {
		return MathHelper.length(a.x - b.x, a.y - b.y);
	}

	/**
	 * Gets the squared distance between two Vector2.
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static double distanceSquared(Vector2 a, Vector2 b) {
		return MathHelper.lengthSquared(a.x - b.x, a.y - b.y);
	}

	/**
	 * Raises the X and Y values of a Vector2 to the given power.
	 *
	 * @param o
	 * @param power
	 * @return
	 */
	public static Vector2 pow(Vector2 o, double power) {
		return new Vector2(Math.pow(o.getX(), power), Math.pow(o.getY(), power));
	}

	/**
	 * Checks if two Vector2s are equal
	 */
	public static boolean equals(Object a, Object b) {
		return a.equals(b);
	}
}
