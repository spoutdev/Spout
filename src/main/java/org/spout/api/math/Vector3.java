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
 * Represents a 3d vector.
 */
public class Vector3 implements Comparable<Vector3> {
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
		return add(new Vector3(x, y, z));
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
		return add(new Vector3(x, y, z));
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
		return add(new Vector3(x, y, z));
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
		return subtract(new Vector3(x, y, z));
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
		return subtract(new Vector3(x, y, z));
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
		return subtract(new Vector3(x, y, z));
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
		return xT.getX() == yT.getX() && xT.getY() == yT.getY() && xT.getZ() == yT.getZ();
	}

	// TODO - all the methods below need to use .getX(), .getY() etc, for all "other" vectors.

	/**
	 * Generates a unique hash code for this set of values
	 *
	 * @return
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Float.floatToIntBits(x);
		hash = 37 * hash + Float.floatToIntBits(y);
		hash = 37 * hash + Float.floatToIntBits(z);
		return hash;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
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
		return new Vector3(a.getX() + b.getX(), a.getY() + b.getY(), a.getZ() + b.getZ());
	}

	/**
	 * Creates a new vector that is A - B
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 subtract(Vector3 a, Vector3 b) {
		return new Vector3(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
	}

	/**
	 * Multiplies one Vector3 by the other Vector3
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 multiply(Vector3 a, Vector3 b) {
		return new Vector3(a.getX() * b.getX(), a.getY() * b.getY(), a.getZ() * b.getZ());
	}

	/**
	 * Divides one Vector3 by the other Vector3
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 divide(Vector3 a, Vector3 b) {
		return new Vector3(a.getX() / b.getX(), a.getY() / b.getY(), a.getZ() / b.getZ());
	}

	/**
	 * Returns the dot product of A and B
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static float dot(Vector3 a, Vector3 b) {
		return a.getX() * b.getX() + a.getY() * b.getY() + a.getZ() * b.getZ();
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
		return new Vector3(a.getY() * b.getZ() - a.getZ() * b.getY(), a.getZ() * b.getX() - a.getX() * b.getZ(), a.getX() * b.getY() - a.getY() * b.getX());
	}

	/**
	 * Rounds the X, Y, and Z values of the given Vector3 up to the nearest
	 * integer value.
	 *
	 * @param o Vector3 to use
	 * @return
	 */
	public static Vector3 ceil(Vector3 o) {
		return new Vector3(Math.ceil(o.getX()), Math.ceil(o.getY()), Math.ceil(o.getZ()));
	}

	/**
	 * Rounds the X, Y, and Z values of the given Vector3 down to the nearest
	 * integer value.
	 *
	 * @param o Vector3 to use
	 * @return
	 */
	public static Vector3 floor(Vector3 o) {
		return new Vector3(Math.floor(o.getX()), Math.floor(o.getY()), Math.floor(o.getZ()));
	}

	/**
	 * Rounds the X, Y, and Z values of the given Vector3 to the nearest integer
	 * value.
	 *
	 * @param o Vector3 to use
	 * @return
	 */
	public static Vector3 round(Vector3 o) {
		return new Vector3(Math.round(o.getX()), Math.round(o.getY()), Math.round(o.getZ()));
	}

	/**
	 * Sets the X, Y, and Z values of the given Vector3 to their absolute value.
	 *
	 * @param o Vector3 to use
	 * @return
	 */
	public static Vector3 abs(Vector3 o) {
		return new Vector3(Math.abs(o.getX()), Math.abs(o.getY()), Math.abs(o.getZ()));
	}

	/**
	 * Returns a Vector3 containing the smallest X, Y, and Z values.
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static Vector3 min(Vector3 o1, Vector3 o2) {
		return new Vector3(Math.min(o1.getX(), o2.getX()), Math.min(o1.getY(), o2.getY()), Math.min(o1.getZ(), o2.getZ()));
	}

	/**
	 * Returns a Vector3 containing the largest X, Y, and Z values.
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static Vector3 max(Vector3 o1, Vector3 o2) {
		return new Vector3(Math.max(o1.getX(), o2.getX()), Math.max(o1.getY(), o2.getY()), Math.max(o1.getZ(), o2.getZ()));
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
		return Math.sqrt(Math.pow(xzDist, 2) + Math.pow(Math.abs(Vector3.subtract(a, b).getY()), 2));
	}

	/**
	 * Raises the X, Y, and Z values of a Vector3 to the given power.
	 *
	 * @param o
	 * @param power
	 * @return
	 */
	public static Vector3 pow(Vector3 o, double power) {
		return new Vector3(Math.pow(o.getX(), power), Math.pow(o.getY(), power), Math.pow(o.getZ(), power));
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
		return new Vector2(o.getX(), o.getZ());
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
		return new Vector2m(o.getX(), o.getZ());
	}

	/**
	 * Returns a new float array that is {x, y, z}
	 *
	 * @param a
	 * @return
	 */
	public static float[] toArray(Vector3 a) {
		return new float[] {a.getX(), a.getY(), a.getZ()};
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
}
