/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.math;

import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.spout.api.util.StringUtil;

/**
 * Represents a 3d vector.
 */
public class Vector3 implements Comparable<Vector3>, Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * Vector with all elements set to 0. (0, 0, 0)
	 */
	public final static Vector3 ZERO = new Vector3(0, 0, 0);
	/**
	 * Unit Vector in the X direction. (1, 0, 0)
	 */
	public final static Vector3 UNIT_X = new Vector3(1, 0, 0);
	/**
	 * Unit Vector pointing Right. (1, 0, 0)
	 */
	public final static Vector3 RIGHT = UNIT_X;
	/**
	 * Unit Vector in the Y direction. (0, 1, 0)
	 */
	public final static Vector3 UNIT_Y = new Vector3(0, 1, 0);
	/**
	 * Unit Vector pointing Up. (0, 1, 0)
	 */
	public final static Vector3 UP = UNIT_Y;
	/**
	 * Unit Vector in the Z direction. (0, 0, 1)
	 */
	public final static Vector3 UNIT_Z = new Vector3(0, 0, 1);
	/**
	 * Unit Vector facing Forward. (0, 0, 1)
	 */
	public final static Vector3 FORWARD = UNIT_Z;
	/**
	 * Unit Vector with all elements set to 1. (1, 1, 1)
	 */
	public final static Vector3 ONE = new Vector3(1, 1, 1);
	/**
	 * Hashcode caching
	 */
	private transient volatile boolean hashed = false;
	private transient volatile int hashcode = 0;
	protected final float x;
	protected final float y;
	protected final float z;

	/**
	 * Constructs and initializes a Vector3 from the given x, y, z
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 */
	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Constructs and initializes a Vector3 from the given x, y, z
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 */
	public Vector3(double x, double y, double z) {
		this((float) x, (float) y, (float) z);
	}

	/**
	 * Constructs and initializes a Vector3 from the given x, y, z
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 */
	public Vector3(int x, int y, int z) {
		this((float) x, (float) y, (float) z);
	}

	/**
	 * Constructs and initializes a Vector3 from an old Vector3
	 *
	 * @param o
	 */
	public Vector3(Vector3 o) {
		this(o.x, o.y, o.z);
	}

	/**
	 * Constructs and initializes a Vector3 to (0,0)
	 */
	public Vector3() {
		this(0, 0, 0);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public int getFloorX() {
		return GenericMath.floor(x);
	}

	public int getFloorY() {
		return GenericMath.floor(y);
	}

	public int getFloorZ() {
		return GenericMath.floor(z);
	}

	public final float getRight() {
		return getX();
	}

	public final float getUp() {
		return getY();
	}

	public final float getForward() {
		return getZ();
	}

	public final float getSouth() {
		return getX();
	}

	public final float getWest() {
		return getZ();
	}

	/**
	 * Adds this Vector3 to the value of the Vector3 argument
	 *
	 * @param that The Vector3 to add
	 * @return the new Vector3
	 */
	public Vector3 add(Vector3 that) {
		return Vector3.add(this, that);
	}

	/**
	 * Adds a Vector3 comprised of the given x, y, z values
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3 add(float x, float y, float z) {
		return new Vector3(this.x + x, this.y + y, this.z + z);
	}

	/**
	 * Adds a Vector3 comprised of the given x, y, z values
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3 add(double x, double y, double z) {
		return new Vector3(this.x + x, this.y + y, this.z + z);
	}

	/**
	 * Adds a Vector3 comprised of the given x, y, z values
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3 add(int x, int y, int z) {
		return new Vector3(this.x + x, this.y + y, this.z + z);
	}

	/**
	 * Subtracts the given Vector3 from this Vector3
	 *
	 * @param that The Vector3 to subtract
	 * @return the new Vector3
	 */
	public Vector3 subtract(Vector3 that) {
		return Vector3.subtract(this, that);
	}

	/**
	 * Subtracts a Vector3 comprised of the given x, y, z values
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3 subtract(float x, float y, float z) {
		return add(-x, -y, -z);
	}

	/**
	 * Subtracts a Vector3 comprised of the given x, y, z values
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3 subtract(double x, double y, double z) {
		return add(-x, -y, -z);
	}

	/**
	 * Subtracts a Vector3 comprised of the given x, y, z values
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3 subtract(int x, int y, int z) {
		return add(-x, -y, -z);
	}

	/**
	 * Multiplies this Vector3 by the value of the Vector3 argument
	 *
	 * @param that The Vector3 to multiply
	 * @return the new Vector3
	 */
	public Vector3 multiply(Vector3 that) {
		return Vector3.multiply(this, that);
	}

	/**
	 * Multiplies a Vector3 comprised of the given x, y, z values
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3 multiply(float x, float y, float z) {
		return multiply(new Vector3(x, y, z));
	}

	/**
	 * Multiplies a Vector3 comprised of the given x, y, z values
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3 multiply(double x, double y, double z) {
		return multiply(new Vector3(x, y, z));
	}

	/**
	 * Multiplies a Vector3 comprised of the given x, y, z values
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3 multiply(int x, int y, int z) {
		return multiply(new Vector3(x, y, z));
	}

	/**
	 * Multiplies a Vector3 by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector3 multiply(float val) {
		return multiply(new Vector3(val, val, val));
	}

	/**
	 * Multiplies a Vector3 by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector3 multiply(double val) {
		return multiply(new Vector3(val, val, val));
	}

	/**
	 * Multiplies a Vector3 by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector3 multiply(int val) {
		return multiply(new Vector3(val, val, val));
	}

	/**
	 * Divides the given Vector3 from this Vector3
	 *
	 * @param that The Vector3 to divide
	 * @return the new Vector3
	 */
	public Vector3 divide(Vector3 that) {
		return Vector3.divide(this, that);
	}

	/**
	 * Divides a Vector3 comprised of the given x, y, z values
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3 divide(float x, float y, float z) {
		return divide(new Vector3(x, y, z));
	}

	/**
	 * Divides a Vector3 comprised of the given x, y, z values
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3 divide(double x, double y, double z) {
		return divide(new Vector3(x, y, z));
	}

	/**
	 * Divides a Vector3 comprised of the given x, y, z values
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3 divide(int x, int y, int z) {
		return divide(new Vector3(x, y, z));
	}

	/**
	 * Divides a Vector3 by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector3 divide(float val) {
		return divide(new Vector3(val, val, val));
	}

	/**
	 * Divides a Vector3 by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector3 divide(double val) {
		return divide(new Vector3(val, val, val));
	}

	/**
	 * Divides a Vector3 by the given value
	 *
	 * @param val
	 * @return
	 */
	public Vector3 divide(int val) {
		return divide(new Vector3(val, val, val));
	}

	/**
	 * Takes the dot product of two vectors
	 *
	 * @param that
	 * @return
	 */
	public float dot(Vector3 that) {
		return Vector3.dot(this, that);
	}

	/**
	 * Returns a Vector2 object using the X and Z values of this Vector3. The x
	 * of this Vector3 becomes the x of the Vector2, and the z of this Vector3
	 * becomes the y of the Vector2.
	 *
	 * @return
	 */
	public Vector2 toVector2() {
		return Vector3.toVector2(this);
	}

	/**
	 * Takes the cross product of two vectors
	 *
	 * @param that
	 * @return
	 */
	public Vector3 cross(Vector3 that) {
		return Vector3.cross(this, that);
	}

	/**
	 * Rounds the X, Y, and Z values of this Vector3 up to the nearest integer
	 * value.
	 *
	 * @return
	 */
	public Vector3 ceil() {
		return new Vector3(Math.ceil(x), Math.ceil(y), Math.ceil(z));
	}

	/**
	 * Rounds the X, Y, and Z values of this Vector3 down to the nearest integer
	 * value.
	 *
	 * @return
	 */
	public Vector3 floor() {
		return new Vector3(Math.floor(x), Math.floor(y), Math.floor(z));
	}

	/**
	 * Rounds the X, Y, and Z values of this Vector3 to the nearest integer
	 * value.
	 *
	 * @return
	 */
	public Vector3 round() {
		return new Vector3(Math.round(x), Math.round(y), Math.round(z));
	}

	/**
	 * Sets the X, Y, and Z values of this Vector3 to their absolute value.
	 *
	 * @return
	 */
	public Vector3 abs() {
		return new Vector3(Math.abs(x), Math.abs(y), Math.abs(z));
	}

	/**
	 * Gets the distance between this Vector3 and a given Vector3.
	 *
	 * @param a
	 * @return
	 */
	public double distance(Vector3 a) {
		return Vector3.distance(a, this);
	}

	/**
	 * Gets the squared distance between this Vector3 and a given Vector3.
	 *
	 * @param a
	 * @return
	 */
	public double distanceSquared(Vector3 a) {
		return Vector3.distanceSquared(a, this);
	}

	/**
	 * Raises the X, Y, and Z values of this Vector3 to the given power.
	 *
	 * @param power
	 * @return
	 */
	public Vector3 pow(double power) {
		return Vector3.pow(this, power);
	}

	/**
	 * returns the squared length of the vector
	 *
	 * @return
	 */
	public float lengthSquared() {
		return Vector3.lengthSquared(this);
	}

	/**
	 * returns the length of this vector. Note: makes use of Math.sqrt and is
	 * not cached.
	 *
	 * @return
	 */
	public float length() {
		return Vector3.length(this);
	}

	/**
	 * Returns a fast approximation of this vector's length.
	 *
	 * @return
	 */
	public float fastLength() {
		return Vector3.fastLength(this);
	}

	/**
	 * returns the vector with a length of 1
	 *
	 * @return
	 */
	public Vector3 normalize() {
		return Vector3.normalize(this);
	}

	/**
	 * returns the vector as [x,y,z]
	 *
	 * @return
	 */
	public float[] toArray() {
		return Vector3.toArray(this);
	}

	/**
	 * Returns a new vector that is a transformation of this vector around the
	 * given transformation
	 *
	 * @param transformation
	 * @return
	 */
	public Vector3 transform(Matrix transformation) {
		return VectorMath.transform(this, transformation);
	}

	/**
	 * Returns a new vector that is a transformation of this vector around the
	 * given transformation
	 *
	 * @param transformation
	 * @return
	 */
	public Vector3 transform(Quaternion transformation) {
		return VectorMath.transform(this, transformation);
	}

	/**
	 * Gets the Vector3 composed of the smallest components of the two vectors.
	 *
	 * @param other The other Vector3 to compare this Vector3 with.
	 * @return
	 */
	public Vector3 min(Vector3 other) {
		return Vector3.min(this, other);
	}

	/**
	 * Gets the Vector3 composed of the largest components of the two vectors.
	 *
	 * @param other The other Vector3 to compare this Vector3 with.
	 * @return
	 */
	public Vector3 max(Vector3 other) {
		return Vector3.max(this, other);
	}

	/**
	 * Compares two Vector3s
	 */
	@Override
	public int compareTo(Vector3 o) {
		return Vector3.compareTo(this, o);
	}

	/**
	 * Checks if two Vector3s are equal
	 */
	@Override
	public boolean equals(Object b) {
		if (!(b instanceof Vector3)) {
			return false;
		}
		if (this == b) {
			return true;
		}
		Vector3 xT = this;
		Vector3 yT = (Vector3) b;
		return xT.x == yT.x && xT.y == yT.y && xT.z == yT.z;
	}

	// All of the below methods use .x, .y, .z instead of .getX() on purpose. Changing them will break AtomicPoint!
	/**
	 * Generates a unique hash code for this set of values
	 *
	 * @return
	 */
	@Override
	public int hashCode() {
		if (!hashed) {
			hashcode = new HashCodeBuilder(7, 37).append(x).append(y).append(z).toHashCode();
			hashed = true;
		}
		return hashcode;
	}

	@Override
	public String toString() {
		return StringUtil.toString(this.x, this.y, this.z);
	}

	/**
	 * Returns the length of the given vector.
	 * <p/>
	 * Note: Makes use of Math.sqrt and is not cached, so can be slow
	 * <p/>
	 * Also known as norm. ||a||
	 *
	 * @param a The vector
	 * @return The length of the vector
	 */
	public static float length(Vector3 a) {
		return (float) Math.sqrt(lengthSquared(a));
	}

	/**
	 * Returns an approximate length of the given vector.
	 *
	 * @param a The vector
	 * @return The fast approximate length of the vector
	 */
	@Deprecated
	public static float fastLength(Vector3 a) {
		return length(a);
	}

	/**
	 * returns the length squared to the given vector
	 *
	 * @param a The vector
	 * @return The squared length of the vector
	 */
	public static float lengthSquared(Vector3 a) {
		return dot(a, a);
	}

	/**
	 * Returns a new vector that is the given vector but length 1
	 *
	 * @param a The vector
	 * @return The vector with the same direction but length one
	 */
	public static Vector3 normalize(Vector3 a) {
		return a.multiply(1.f / a.length());
	}

	/**
	 * Creates a new Vector that is A + B
	 *
	 * @param a The left vector
	 * @param b The right vector
	 * @return The sum vector of left plus right
	 */
	public static Vector3 add(Vector3 a, Vector3 b) {
		return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
	}

	/**
	 * Creates a new vector that is A - B
	 *
	 * @param a The left vector
	 * @param b The right vector
	 * @return The sum vector of left minus right
	 */
	public static Vector3 subtract(Vector3 a, Vector3 b) {
		return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);
	}

	/**
	 * Multiplies one Vector3 by the other Vector3
	 *
	 * @param a The left vector
	 * @param b The right vector
	 * @return The product vector of left times right
	 */
	public static Vector3 multiply(Vector3 a, Vector3 b) {
		return new Vector3(a.x * b.x, a.y * b.y, a.z * b.z);
	}

	/**
	 * Divides one Vector3 by the other Vector3
	 *
	 * @param a The left vector
	 * @param b The right vector
	 * @return The quotient vector of left divided by right
	 */
	public static Vector3 divide(Vector3 a, Vector3 b) {
		return new Vector3(a.x / b.x, a.y / b.y, a.z / b.z);
	}

	/**
	 * Returns the dot product of A and B
	 *
	 * @param a The left vector
	 * @param b The right vector
	 * @return The dot product vector of left dot right
	 */
	public static float dot(Vector3 a, Vector3 b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}

	/**
	 * Creates a new Vector that is the A x B The Cross Product is the vector
	 * orthogonal to both A and B
	 *
	 * @param a The left vector
	 * @param b The right vector
	 * @return The cross product vector of left cross right
	 */
	public static Vector3 cross(Vector3 a, Vector3 b) {
		return new Vector3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
	}

	/**
	 * Rounds the X, Y, and Z values of the given Vector3 up to the nearest
	 * integer value.
	 *
	 * @param o Vector3 to use
	 * @return The ceiling vector
	 */
	public static Vector3 ceil(Vector3 o) {
		return new Vector3(Math.ceil(o.x), Math.ceil(o.y), Math.ceil(o.z));
	}

	/**
	 * Rounds the X, Y, and Z values of the given Vector3 down to the nearest
	 * integer value.
	 *
	 * @param o Vector3 to use
	 * @return The floor vector
	 */
	public static Vector3 floor(Vector3 o) {
		return new Vector3(Math.floor(o.x), Math.floor(o.y), Math.floor(o.z));
	}

	/**
	 * Rounds the X, Y, and Z values of the given Vector3 to the nearest integer
	 * value.
	 *
	 * @param o Vector3 to use
	 * @return The rounded vector
	 */
	public static Vector3 round(Vector3 o) {
		return new Vector3(Math.round(o.x), Math.round(o.y), Math.round(o.z));
	}

	/**
	 * Sets the X, Y, and Z values of the given Vector3 to their absolute value.
	 *
	 * @param o Vector3 to use
	 * @return The absolute vector
	 */
	public static Vector3 abs(Vector3 o) {
		return new Vector3(Math.abs(o.x), Math.abs(o.y), Math.abs(o.z));
	}

	/**
	 * Returns a Vector3 containing the smallest X, Y, and Z values.
	 *
	 * @param o1 The first vector
	 * @param o2 The second vector
	 * @return The minimum of both vectors combined
	 */
	public static Vector3 min(Vector3 o1, Vector3 o2) {
		return new Vector3(Math.min(o1.x, o2.x), Math.min(o1.y, o2.y), Math.min(o1.z, o2.z));
	}

	/**
	 * Returns a Vector3 containing the largest X, Y, and Z values.
	 *
	 * @param o1 The first vector
	 * @param o2 The second vector
	 * @return The maximum of both vectors combined
	 */
	public static Vector3 max(Vector3 o1, Vector3 o2) {
		return new Vector3(Math.max(o1.x, o2.x), Math.max(o1.y, o2.y), Math.max(o1.z, o2.z));
	}

	/**
	 * Returns a Vector3 with random X, Y, and Z values (between 0 and 1)
	 *
	 * @return a random vector
	 */
	public static Vector3 rand() {
		return new Vector3(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1);
	}

	/**
	 * Gets the distance between two Vector3.
	 *
	 * @param a The first vector
	 * @param b The second vector
	 * @return The distance between the two
	 */
	public static double distance(Vector3 a, Vector3 b) {
		return GenericMath.length(a.x - b.x, a.y - b.y, a.z - b.z);
	}

	/**
	 * Gets the squared distance between two Vector3.
	 *
	 * @param a The first vector
	 * @param b The second vector
	 * @return The square of the distance between the two
	 */
	public static double distanceSquared(Vector3 a, Vector3 b) {
		return GenericMath.lengthSquared(a.x - b.x, a.y - b.y, a.z - b.z);
	}

	/**
	 * Raises the X, Y, and Z values of a Vector3 to the given power.
	 *
	 * @param o The vector
	 * @param power The power
	 * @return The vector to the specified power
	 */
	public static Vector3 pow(Vector3 o, double power) {
		return new Vector3(Math.pow(o.x, power), Math.pow(o.y, power), Math.pow(o.z, power));
	}

	/**
	 * Returns a Vector2 object using the X and Z values of the given Vector3.
	 * The x of the Vector3 becomes the x of the Vector2, and the z of this
	 * Vector3 becomes the y of the Vector2.
	 *
	 * @param o Vector3 object to use
	 * @return The vector2 form
	 */
	public static Vector2 toVector2(Vector3 o) {
		return new Vector2(o.x, o.z);
	}

	/**
	 * Returns a new float array that is {x, y, z}
	 *
	 * @param a The vector
	 * @return An array of length 3 with x, y and z
	 */
	public static float[] toArray(Vector3 a) {
		return new float[]{a.x, a.y, a.z};
	}

	/**
	 * Compares two vectors.
	 *
	 * @param a the first vector
	 * @param b the second vector
	 * @return the result of the comparison
	 * @see {@link java.lang.Comparable}.
	 */
	public static int compareTo(Vector3 a, Vector3 b) {
		return (int) a.lengthSquared() - (int) b.lengthSquared();
	}
}
