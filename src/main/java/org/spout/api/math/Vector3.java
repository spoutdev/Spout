/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
		return MathHelper.floor(x);
	}

	public int getFloorY() {
		return MathHelper.floor(y);
	}

	public int getFloorZ() {
		return MathHelper.floor(z);
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
		return MathHelper.add(this, that);
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
		return MathHelper.subtract(this, that);
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
		return MathHelper.multiply(this, that);
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
		return MathHelper.divide(this, that);
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
		return MathHelper.dot(this, that);
	}

	/**
	 * Returns a Vector2 object using the X and Z values of this Vector3. The x
	 * of this Vector3 becomes the x of the Vector2, and the z of this Vector3
	 * becomes the y of the Vector2.
	 *
	 * @return
	 */
	public Vector2 toVector2() {
		return MathHelper.toVector2(this);
	}

	/**
	 * Takes the cross product of two vectors
	 *
	 * @param that
	 * @return
	 */
	public Vector3 cross(Vector3 that) {
		return MathHelper.cross(this, that);
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
		return MathHelper.distance(a, this);
	}

	/**
	 * Gets the squared distance between this Vector3 and a given Vector3.
	 *
	 * @param a
	 * @return
	 */
	public double distanceSquared(Vector3 a) {
		return MathHelper.distanceSquared(a, this);
	}

	/**
	 * Raises the X, Y, and Z values of this Vector3 to the given power.
	 *
	 * @param power
	 * @return
	 */
	public Vector3 pow(double power) {
		return MathHelper.pow(this, power);
	}

	/**
	 * returns the squared length of the vector
	 *
	 * @return
	 */
	public float lengthSquared() {
		return MathHelper.lengthSquared(this);
	}

	/**
	 * returns the length of this vector. Note: makes use of Math.sqrt and is
	 * not cached.
	 *
	 * @return
	 */
	public float length() {
		return MathHelper.length(this);
	}

	/**
	 * Returns a fast approximation of this vector's length.
	 *
	 * @return
	 */
	public float fastLength() {
		return MathHelper.fastLength(this);
	}

	/**
	 * returns the vector with a length of 1
	 *
	 * @return
	 */
	public Vector3 normalize() {
		return MathHelper.normalize(this);
	}

	/**
	 * returns the vector as [x,y,z]
	 *
	 * @return
	 */
	public float[] toArray() {
		return MathHelper.toArray(this);
	}

	/**
	 * Returns a new vector that is a transformation of this vector around the
	 * given transformation
	 *
	 * @param transformation
	 * @return
	 */
	public Vector3 transform(Matrix transformation) {
		return MathHelper.transform(this, transformation);
	}

	/**
	 * Returns a new vector that is a transformation of this vector around the
	 * given transformation
	 *
	 * @param transformation
	 * @return
	 */
	public Vector3 transform(Quaternion transformation) {
		return MathHelper.transform(this, transformation);
	}	

	/**
	 * Gets the Vector3 composed of the smallest components of the two vectors.
	 * 
	 * @param other The other Vector3 to compare this Vector3 with.
	 * @return
	 */
	public Vector3 min(Vector3 other) {
		return MathHelper.min(this, other);
	}

	/**
	 * Gets the Vector3 composed of the largest components of the two vectors.
	 * 
	 * @param other The other Vector3 to compare this Vector3 with.
	 * @return
	 */
	public Vector3 max(Vector3 other) {
		return MathHelper.max(this, other);
	}

	/**
	 * Compares two Vector3s
	 */
	public int compareTo(Vector3 o) {
		return MathHelper.compareTo(this, o);
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


	public Quaternion rotationTo(Vector3 other) {
		return MathHelper.rotationTo(this, other);
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
}
