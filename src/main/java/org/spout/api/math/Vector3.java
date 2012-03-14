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

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spout.api.util.pool.PoolableObject;
import org.spout.api.util.pool.Vector3Pool;

/**
 * Represents a 3d vector.
 */
public class Vector3 extends PoolableObject implements Comparable<Vector3>, Cloneable {
	/**
	 * Vector with all elements set to 0. (0, 0, 0)
	 */
	public final static Vector3 ZERO = new Vector3(0, 0, 0);
	/**
	 * Unit Vector in the X direction. (1, 0, 0)
	 */
	public final static Vector3 UNIT_X = new Vector3(1, 0, 0);
	/**
	 * Unit Vector facing Forward. (1, 0, 0)
	 */
	public final static Vector3 Forward = UNIT_X;
	/**
	 * Unit Vector in the Y direction. (0, 1, 0)
	 */
	public final static Vector3 UNIT_Y = new Vector3(0, 1, 0);
	/**
	 * Unit Vector pointing Up. (0, 1, 0)
	 */
	public final static Vector3 Up = UNIT_Y;
	/**
	 * Unit Vector in the Z direction. (0, 0, 1)
	 */
	public final static Vector3 UNIT_Z = new Vector3(0, 0, 1);
	/**
	 * Unit Vector pointing Right. (0, 0, 1)
	 */
	public final static Vector3 Right = UNIT_Z;
	/**
	 * Unit Vector with all elements set to 1. (1, 1, 1)
	 */
	public final static Vector3 ONE = new Vector3(1, 1, 1);
	protected float x;
	protected float y;
	protected float z;

	/**
	 * Constructs and initializes a Vector3 from the given x, y, z
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 */
	protected Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Creates a new Vector3, grabbing it from the pool.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return pooled vector3
	 */
	public static Vector3 create(float x, float y, float z) {
		Vector3 v = Vector3Pool.checkout();
		v.setX(x);
		v.setY(y);
		v.setZ(z);
		return v;
	}

	/**
	 * Creates a new Vector3, grabbing it from the pool.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return pooled vector3
	 */
	public static Vector3 create(double x, double y, double z) {
		Vector3 v = Vector3Pool.checkout();
		v.setX((float) x);
		v.setY((float) y);
		v.setZ((float) z);
		return v;
	}

	/**
	 * Creates a new Vector3, grabbing it from the pool.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return pooled vector3
	 */
	public static Vector3 create(int x, int y, int z) {
		Vector3 v = Vector3Pool.checkout();
		v.setX(x);
		v.setY(y);
		v.setZ(z);
		return v;
	}

	/**
	 * Constructs and initializes a Vector3 to (0,0)
	 */
	protected Vector3() {
		this(0, 0, 0);
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}

	protected void setX(float x) {
		this.x = x;
	}

	protected void setY(float y) {
		this.y = y;
	}

	protected void setZ(float z) {
		this.z = z;
	}

	/**
	 * Adds this Vector3 to the value of the Vector3 argument
	 * 
	 * @param that The Vector3 to add
	 * @return the new Vector3
	 */
	public Vector3 add(Vector3 that) {
		return add(that.getX(), that.getY(), that.getZ());
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
		Vector3 v = Vector3Pool.checkout();
		v.setX(this.getX() + x);
		v.setY(this.getY() + y);
		v.setZ(this.getZ() + z);
		return v;
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
		return add((float) x, (float) y, (float) z);
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
		return add((float) x, (float) y, (float) z);
	}

	/**
	 * Subtracts the given Vector3 from this Vector3
	 * 
	 * @param that The Vector3 to subtract
	 * @return the new Vector3
	 */
	public Vector3 subtract(Vector3 that) {
		return subtract(that.getX(), that.getY(), that.getZ());
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
		Vector3 v = Vector3Pool.checkout();
		v.setX(this.getX() - x);
		v.setY(this.getY() - y);
		v.setZ(this.getZ() - z);
		return v;
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
		return subtract((float) x, (float) y, (float) z);
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
		return subtract((float) x, (float) y, (float) z);
	}

	public Vector3 scale(float s) {
		Vector3 v = Vector3Pool.checkout();
		v.setX(getX() * s);
		v.setY(getY() * s);
		v.setZ(getZ() * s);
		return v;
	}

	public Vector3 scale(float x, float y, float z) {
		Vector3 v = Vector3Pool.checkout();
		v.setX(getX() * x);
		v.setY(getY() * y);
		v.setZ(getZ() * z);
		return v;
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
	 * Returns a Vector2m object using the X and Z values of this Vector3. The x
	 * of this Vector3 becomes the x of the Vector2, and the z of this Vector3
	 * becomes the y of the Vector2m.
	 * 
	 * @return
	 */
	public Vector2m toVector2m() {
		return Vector3.toVector2m(this);
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
		return new Vector3(Math.ceil(this.x), Math.ceil(this.y), Math.ceil(this.z));
	}

	/**
	 * Rounds the X, Y, and Z values of this Vector3 down to the nearest integer
	 * value.
	 * 
	 * @return
	 */
	public Vector3 floor() {
		return new Vector3(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
	}

	/**
	 * Rounds the X, Y, and Z values of this Vector3 to the nearest integer
	 * value.
	 * 
	 * @return
	 */
	public Vector3 round() {
		return new Vector3(Math.round(this.x), Math.round(this.y), Math.round(this.z));
	}

	/**
	 * Sets the X, Y, and Z values of this Vector3 to their absolute value.
	 * 
	 * @return
	 */
	public Vector3 abs() {
		return new Vector3(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
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
		return Vector3.transform(this, transformation);
	}

	/**
	 * Returns a new vector that is a transformation of this vector around the
	 * given transformation
	 * 
	 * @param transformation
	 * @return
	 */
	public Vector3 transform(Quaternion transformation) {
		return Vector3.transform(this, transformation);
	}

	/**
	 * Compares two Vector3s
	 */
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
		return new HashCodeBuilder(7, 37).append(this.x).append(this.y).append(this.z).toHashCode();
	}

	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

	@Override
	public Vector3 clone() {
		return new Vector3(this.x, this.y, this.z);
	}

	/**
	 * Returns the length of the given vector.
	 * 
	 * Note: Makes use of Math.sqrt and is not cached, so can be slow
	 * 
	 * Also known as norm. ||a||
	 * 
	 * @param a
	 * @return
	 */
	public static float length(Vector3 a) {
		return (float) Math.sqrt(lengthSquared(a));
	}

	/**
	 * Returns an approximate length of the given vector.
	 * 
	 * @param a
	 * @return
	 */
	public static float fastLength(Vector3 a) {
		return (float) Math.sqrt(lengthSquared(a));
	}

	/**
	 * returns the length squared to the given vector
	 * 
	 * @param a
	 * @return
	 */
	public static float lengthSquared(Vector3 a) {
		return Vector3.dot(a, a);
	}

	/**
	 * Returns a new vector that is the given vector but length 1
	 * 
	 * @param a
	 * @return
	 */
	public static Vector3 normalize(Vector3 a) {
		return a.multiply(1.f / a.length());
	}

	/**
	 * Creates a new Vector that is A + B
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 add(Vector3 a, Vector3 b) {
		return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
	}

	/**
	 * Creates a new vector that is A - B
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 subtract(Vector3 a, Vector3 b) {
		return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);
	}

	/**
	 * Multiplies one Vector3 by the other Vector3
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 multiply(Vector3 a, Vector3 b) {
		return new Vector3(a.x * b.x, a.y * b.y, a.z * b.z);
	}

	/**
	 * Divides one Vector3 by the other Vector3
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 divide(Vector3 a, Vector3 b) {
		return new Vector3(a.x / b.x, a.y / b.y, a.z / b.z);
	}

	/**
	 * Returns the dot product of A and B
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static float dot(Vector3 a, Vector3 b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}

	/**
	 * Creates a new Vector that is the A x B The Cross Product is the vector
	 * orthogonal to both A and B
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 cross(Vector3 a, Vector3 b) {
		return new Vector3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
	}

	/**
	 * Rounds the X, Y, and Z values of the given Vector3 up to the nearest
	 * integer value.
	 * 
	 * @param o Vector3 to use
	 * @return
	 */
	public static Vector3 ceil(Vector3 o) {
		return new Vector3(Math.ceil(o.x), Math.ceil(o.y), Math.ceil(o.z));
	}

	/**
	 * Rounds the X, Y, and Z values of the given Vector3 down to the nearest
	 * integer value.
	 * 
	 * @param o Vector3 to use
	 * @return
	 */
	public static Vector3 floor(Vector3 o) {
		return new Vector3(Math.floor(o.x), Math.floor(o.y), Math.floor(o.z));
	}

	/**
	 * Rounds the X, Y, and Z values of the given Vector3 to the nearest integer
	 * value.
	 * 
	 * @param o Vector3 to use
	 * @return
	 */
	public static Vector3 round(Vector3 o) {
		return new Vector3(Math.round(o.x), Math.round(o.y), Math.round(o.z));
	}

	/**
	 * Sets the X, Y, and Z values of the given Vector3 to their absolute value.
	 * 
	 * @param o Vector3 to use
	 * @return
	 */
	public static Vector3 abs(Vector3 o) {
		return new Vector3(Math.abs(o.x), Math.abs(o.y), Math.abs(o.z));
	}

	/**
	 * Returns a Vector3 containing the smallest X, Y, and Z values.
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static Vector3 min(Vector3 o1, Vector3 o2) {
		return new Vector3(Math.min(o1.x, o2.x), Math.min(o1.y, o2.y), Math.min(o1.z, o2.z));
	}

	/**
	 * Returns a Vector3 containing the largest X, Y, and Z values.
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static Vector3 max(Vector3 o1, Vector3 o2) {
		return new Vector3(Math.max(o1.x, o2.x), Math.max(o1.y, o2.y), Math.max(o1.z, o2.z));
	}

	/**
	 * Returns a Vector3 with random X, Y, and Z values (between 0 and 1)
	 * 
	 * @return
	 */
	public static Vector3 rand() {
		double[] rands = new double[3];
		for (int i = 0; i < 3; i++) {
			rands[i] = Math.random() * 2 - 1;
		}
		return new Vector3(rands[0], rands[1], rands[2]);
	}

	/**
	 * Gets the distance between two Vector3.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double distance(Vector3 a, Vector3 b) {
		double xzDist = Vector2.distance(a.toVector2(), b.toVector2());
		return Math.sqrt(Math.pow(xzDist, 2) + Math.pow(Math.abs(Vector3.subtract(a, b).y), 2));
	}

	/**
	 * Raises the X, Y, and Z values of a Vector3 to the given power.
	 * 
	 * @param o
	 * @param power
	 * @return
	 */
	public static Vector3 pow(Vector3 o, double power) {
		return new Vector3(Math.pow(o.x, power), Math.pow(o.y, power), Math.pow(o.z, power));
	}

	/**
	 * Returns a Vector2 object using the X and Z values of the given Vector3.
	 * The x of the Vector3 becomes the x of the Vector2, and the z of this
	 * Vector3 becomes the y of the Vector2m.
	 * 
	 * @param o Vector3 object to use
	 * @return
	 */
	public static Vector2 toVector2(Vector3 o) {
		return new Vector2(o.x, o.z);
	}

	/**
	 * Returns a Vector2m object using the X and Z values of the given Vector3.
	 * The x of the Vector3 becomes the x of the Vector2m, and the z of this
	 * Vector3 becomes the y of the Vector2m.
	 * 
	 * @param o Vector3 object to use
	 * @return
	 */
	public static Vector2m toVector2m(Vector3 o) {
		return new Vector2m(o.x, o.z);
	}

	/**
	 * Returns a new float array that is {x, y, z}
	 * 
	 * @param a
	 * @return
	 */
	public static float[] toArray(Vector3 a) {
		return new float[] {a.x, a.y, a.z};
	}

	/**
	 * Calculates and returns a new Vector3 transformed by the transformation
	 * matrix
	 * 
	 * @param vector the vector to transform
	 * @param transformation the transformation matrix
	 * @return
	 */
	public static Vector3 transform(Vector3 vector, Matrix transformation) {

		return Matrix.transform(vector, transformation);
	}

	/**
	 * Calculates and returns a new Vector3 transformed by the given quaternion
	 * 
	 * @param vector
	 * @param rot
	 * @return
	 */
	public static Vector3 transform(Vector3 vector, Quaternion rot) {
		return Vector3.transform(vector, Matrix.rotate(rot));
	}

	/**
	 * Compares two Vector3s
	 */
	public static int compareTo(Vector3 a, Vector3 b) {
		return (int) a.lengthSquared() - (int) b.lengthSquared();
	}

	/**
	 * Checks if two Vector3s are equal
	 */
	public static boolean equals(Vector3 a, Vector3 b) {
		return a.equals(b);
	}

	/**
	 * Creates a raw, unpooled immutable vector3 set to 0, 0, 0.
	 * 
	 * @return
	 */
	public static Vector3 createRaw() {
		return new Vector3();
	}
}
