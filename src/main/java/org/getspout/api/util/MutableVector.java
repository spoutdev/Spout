/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.util;

import java.util.Random;

import org.getspout.api.World;

/**
 * Represents a mutable vector. Because the components of Vectors are mutable, storing Vectors long term may be dangerous if passing code modifies the Vector later. If you want to keep around a Vector, it may be wise to call <code>clone()</code> in order to get a copy.
 */
public class MutableVector implements Vector {
	private static Random random = new Random();

	/**
	 * Threshold for fuzzy equals().
	 */
	private static final double epsilon = 0.000001;

	protected double x;
	protected double y;
	protected double z;

	/**
	 * Construct the vector with all components as 0.
	 */
	public MutableVector() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	/**
	 * Construct the vector with provided integer components.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public MutableVector(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Construct the vector with provided double components.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public MutableVector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Construct the vector with provided float components.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public MutableVector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Adds the vector by another.
	 * 
	 * @param vec
	 * @return the same vector
	 */
	public Vector add(Vector vec) {
		x += vec.getX();
		y += vec.getY();
		z += vec.getZ();
		return this;
	}

	/**
	 * Subtracts the vector by another.
	 * 
	 * @param vec
	 * @return the same vector
	 */
	public Vector subtract(Vector vec) {
		x -= vec.getX();
		y -= vec.getY();
		z -= vec.getZ();
		return this;
	}

	/**
	 * Multiplies the vector by another.
	 * 
	 * @param vec
	 * @return the same vector
	 */
	public Vector multiply(Vector vec) {
		x *= vec.getX();
		y *= vec.getY();
		z *= vec.getZ();
		return this;
	}

	/**
	 * Divides the vector by another.
	 * 
	 * @param vec
	 * @return the same vector
	 */
	public Vector divide(Vector vec) {
		x /= vec.getX();
		y /= vec.getY();
		z /= vec.getZ();
		return this;
	}

	/**
	 * Copies another vector
	 * 
	 * @param vec
	 * @return the same vector
	 */
	public Vector copy(Vector vec) {
		x = vec.getX();
		y = vec.getY();
		z = vec.getZ();
		return this;
	}

	/**
	 * Gets the magnitude of the vector, defined as sqrt(x^2+y^2+z^2). The value of this method is not cached and uses a costly square-root function, so do not repeatedly call this method to get the vector's magnitude. NaN will be returned if the inner result of the sqrt() function overflows, which will be caused if the length is too long.
	 * 
	 * @return the magnitude
	 */
	public double length() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

	/**
	 * Gets the magnitude of the vector squared.
	 * 
	 * @return the magnitude
	 */
	public double lengthSquared() {
		return Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2);
	}

	/**
	 * Get the distance between this vector and another. The value of this method is not cached and uses a costly square-root function, so do not repeatedly call this method to get the vector's magnitude. NaN will be returned if the inner result of the sqrt() function overflows, which will be caused if the distance is too long.
	 * 
	 * @return the distance
	 */
	public double distance(Vector o) {
		return Math.sqrt(Math.pow(x - o.getX(), 2) + Math.pow(y - o.getY(), 2) + Math.pow(z - o.getZ(), 2));
	}

	/**
	 * Get the squared distance between this vector and another.
	 * 
	 * @return the distance
	 */
	public double distanceSquared(Vector o) {
		return Math.pow(x - o.getX(), 2) + Math.pow(y - o.getY(), 2) + Math.pow(z - o.getZ(), 2);
	}

	/**
	 * Gets the angle between this vector and another in radians.
	 * 
	 * @param other
	 * @return angle in radians
	 */
	public float angle(Vector other) {
		double dot = dot(other) / (length() * other.length());

		return (float) Math.acos(dot);
	}

	/**
	 * Sets this vector to the midpoint between this vector and another.
	 * 
	 * @param other
	 * @return this same vector (now a midpoint)
	 */
	public Vector midpoint(Vector other) {
		x = (x + other.getX()) / 2;
		y = (y + other.getY()) / 2;
		z = (z + other.getZ()) / 2;
		return this;
	}

	/**
	 * Gets a new midpoint vector between this vector and another.
	 * 
	 * @param other
	 * @return a new midpoint vector
	 */
	public Vector getMidpoint(Vector other) {
		x = (x + other.getX()) / 2;
		y = (y + other.getY()) / 2;
		z = (z + other.getZ()) / 2;
		return new MutableVector(x, y, z);
	}

	/**
	 * Performs scalar multiplication, multiplying all components with a scalar.
	 * 
	 * @param m
	 * @return the same vector
	 */
	public Vector multiply(int m) {
		x *= m;
		y *= m;
		z *= m;
		return this;
	}

	/**
	 * Performs scalar multiplication, multiplying all components with a scalar.
	 * 
	 * @param m
	 * @return the same vector
	 */
	public Vector multiply(double m) {
		x *= m;
		y *= m;
		z *= m;
		return this;
	}

	/**
	 * Performs scalar multiplication, multiplying all components with a scalar.
	 * 
	 * @param m
	 * @return the same vector
	 */
	public Vector multiply(float m) {
		x *= m;
		y *= m;
		z *= m;
		return this;
	}

	/**
	 * Calculates the dot product of this vector with another. The dot product is defined as x1*x2+y1*y2+z1*z2. The returned value is a scalar.
	 * 
	 * @param other
	 * @return dot product
	 */
	public double dot(Vector other) {
		return x * other.getX() + y * other.getY() + z * other.getZ();
	}

	/**
	 * Calculates the cross product of this vector with another. The cross product is defined as:
	 * 
	 * x = y1 * z2 - y2 * z1<br/>
	 * y = z1 * x2 - z2 * x1<br/>
	 * z = x1 * y2 - x2 * y1
	 * 
	 * @param o
	 * @return the same vector
	 */
	public Vector crossProduct(Vector o) {
		double newX = y * o.getZ() - o.getY() * z;
		double newY = z * o.getX() - o.getZ() * x;
		double newZ = x * o.getY() - o.getX() * y;

		x = newX;
		y = newY;
		z = newZ;
		return this;
	}

	/**
	 * Converts this vector to a unit vector (a vector with length of 1).
	 * 
	 * @return the same vector
	 */
	public Vector normalize() {
		double length = length();

		x /= length;
		y /= length;
		z /= length;

		return this;
	}

	/**
	 * Zero this vector's components.
	 * 
	 * @return the same vector
	 */
	public Vector zero() {
		x = 0;
		y = 0;
		z = 0;
		return this;
	}

	/**
	 * Returns whether this vector is in an axis-aligned bounding box. The minimum and maximum vectors given must be truly the minimum and maximum X, Y and Z components.
	 * 
	 * @param min
	 * @param max
	 * @return whether this vector is in the AABB
	 */
	public boolean isInAABB(Vector min, Vector max) {
		return x >= min.getX() && x <= max.getX() && y >= min.getY() && y <= max.getY() && z >= min.getZ() && z <= max.getZ();
	}

	/**
	 * Returns whether this vector is within a sphere.
	 * 
	 * @param origin
	 * @param radius
	 * @return whether this vector is in the sphere
	 */
	public boolean isInSphere(Vector origin, double radius) {
		return (Math.pow(origin.getX() - x, 2) + Math.pow(origin.getY() - y, 2) + Math.pow(origin.getZ() - z, 2)) <= Math.pow(radius, 2);
	}

	/**
	 * Gets the X component.
	 * 
	 * @return
	 */
	public double getX() {
		return x;
	}

	/**
	 * Gets the floored value of the X component, indicating the block that this vector is contained with.
	 * 
	 * @return block X
	 */
	public int getBlockX() {
		return (int) Math.floor(x);
	}

	/**
	 * Gets the Y component.
	 * 
	 * @return
	 */
	public double getY() {
		return y;
	}

	/**
	 * Gets the floored value of the Y component, indicating the block that this vector is contained with.
	 * 
	 * @return block y
	 */
	public int getBlockY() {
		return (int) Math.floor(y);
	}

	/**
	 * Gets the Z component.
	 * 
	 * @return
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Gets the floored value of the Z component, indicating the block that this vector is contained with.
	 * 
	 * @return block z
	 */
	public int getBlockZ() {
		return (int) Math.floor(z);
	}

	/**
	 * Set the X component.
	 * 
	 * @param x
	 * @return x
	 */
	public Vector setX(int x) {
		this.x = x;
		return this;
	}

	/**
	 * Set the X component.
	 * 
	 * @param x
	 * @return x
	 */
	public Vector setX(double x) {
		this.x = x;
		return this;
	}

	/**
	 * Set the X component.
	 * 
	 * @param x
	 * @return x
	 */
	public Vector setX(float x) {
		this.x = x;
		return this;
	}

	/**
	 * Set the Y component.
	 * 
	 * @param y
	 * @return y
	 */
	public Vector setY(int y) {
		this.y = y;
		return this;
	}

	/**
	 * Set the Y component.
	 * 
	 * @param y
	 * @return y
	 */
	public Vector setY(double y) {
		this.y = y;
		return this;
	}

	/**
	 * Set the Y component.
	 * 
	 * @param y
	 * @return y
	 */
	public Vector setY(float y) {
		this.y = y;
		return this;
	}

	/**
	 * Set the Z component.
	 * 
	 * @param z
	 * @return z
	 */
	public Vector setZ(int z) {
		this.z = z;
		return this;
	}

	/**
	 * Set the Z component.
	 * 
	 * @param z
	 * @return z
	 */
	public Vector setZ(double z) {
		this.z = z;
		return this;
	}

	/**
	 * Set the Z component.
	 * 
	 * @param z
	 * @return z
	 */
	public Vector setZ(float z) {
		this.z = z;
		return this;
	}

	/**
	 * Checks to see if two objects are equal.
	 * 
	 * Only two Vectors can ever return true. This method uses a fuzzy match to account for floating point errors. The epsilon can be retrieved with epsilon.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector)) {
			return false;
		}

		Vector other = (Vector) obj;

		return Math.abs(x - other.getX()) < epsilon && Math.abs(y - other.getY()) < epsilon && Math.abs(z - other.getZ()) < epsilon && (this.getClass().equals(obj.getClass()));
	}

	/**
	 * Returns a hash code for this vector
	 * 
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		int hash = 7;

		hash = 79 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
		hash = 79 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
		hash = 79 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
		return hash;
	}

	/**
	 * Get a new vector.
	 * 
	 * @return vector
	 */
	@Override
	public Vector clone() {
		Vector v;
		try {
			v = (Vector) super.clone();
			v.setX(x);
			v.setY(y);
			v.setZ(z);
			return v;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns this vector's components as x,y,z.
	 * 
	 */
	@Override
	public String toString() {
		return x + "," + y + "," + z;
	}

	/**
	 * Gets a Location version of this vector with yaw and pitch being 0.
	 * 
	 * @param world
	 * @return the location
	 */
	public Location toLocation(World world) {
		return new MutableLocation(world, x, y, z);
	}

	/**
	 * Gets a Location version of this vector.
	 * 
	 * @param world
	 * @return the location
	 */
	public Location toLocation(World world, float yaw, float pitch) {
		return new MutableLocation(world, x, y, z, yaw, pitch);
	}

	/**
	 * Get the threshold used for equals().
	 * 
	 * @return
	 */
	public static double getEpsilon() {
		return epsilon;
	}

	/**
	 * Gets the minimum components of two vectors.
	 * 
	 * @param v1
	 * @param v2
	 * @return minimum
	 */
	public static Vector getMinimum(Vector v1, Vector v2) {
		return new MutableVector(Math.min(v1.getX(), v2.getX()), Math.min(v1.getY(), v2.getY()), Math.min(v1.getZ(), v2.getZ()));
	}

	/**
	 * Gets the maximum components of two vectors.
	 * 
	 * @param v1
	 * @param v2
	 * @return maximum
	 */
	public static Vector getMaximum(Vector v1, Vector v2) {
		return new MutableVector(Math.max(v1.getX(), v2.getZ()), Math.max(v1.getY(), v2.getY()), Math.max(v1.getZ(), v2.getZ()));
	}

	/**
	 * Gets a random vector with components having a random value between 0 and 1.
	 * 
	 * @return
	 */
	public static Vector getRandom() {
		return new MutableVector(random.nextDouble(), random.nextDouble(), random.nextDouble());
	}

}
