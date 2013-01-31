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

import javax.vecmath.Vector3f;
import java.util.Random;

/**
 * Class containing vector mathematical functions.
 */
public class VectorMath {
	private VectorMath() {
	}

	/**
	 * Returns the length of the given vector.
	 * <p/>
	 * Note: Makes use of Math.sqrt and is not cached, so can be slow
	 * <p/>
	 * Also known as norm. ||a||
	 * @param a The vector
	 * @return The length of the vector
	 */
	public static float length(Vector3 a) {
		return (float) Math.sqrt(lengthSquared(a));
	}

	/**
	 * Returns an approximate length of the given vector.
	 * @param a The vector
	 * @return The fast approximate length of the vector
	 */
	@Deprecated
	public static float fastLength(Vector3 a) {
		return length(a);
	}

	/**
	 * returns the length squared to the given vector
	 * @param a The vector
	 * @return The squared length of the vector
	 */
	public static float lengthSquared(Vector3 a) {
		return dot(a, a);
	}

	/**
	 * Returns a new vector that is the given vector but length 1
	 * @param a The vector
	 * @return The vector with the same direction but length one
	 */
	public static Vector3 normalize(Vector3 a) {
		return a.multiply(1.f / a.length());
	}

	/**
	 * Creates a new Vector that is A + B
	 * @param a The left vector
	 * @param b The right vector
	 * @return The sum vector of left plus right
	 */
	public static Vector3 add(Vector3 a, Vector3 b) {
		return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
	}

	/**
	 * Creates a new vector that is A - B
	 * @param a The left vector
	 * @param b The right vector
	 * @return The sum vector of left minus right
	 */
	public static Vector3 subtract(Vector3 a, Vector3 b) {
		return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);
	}

	/**
	 * Multiplies one Vector3 by the other Vector3
	 * @param a The left vector
	 * @param b The right vector
	 * @return The product vector of left times right
	 */
	public static Vector3 multiply(Vector3 a, Vector3 b) {
		return new Vector3(a.x * b.x, a.y * b.y, a.z * b.z);
	}

	/**
	 * Divides one Vector3 by the other Vector3
	 * @param a The left vector
	 * @param b The right vector
	 * @return The quotient vector of left divided by right
	 */
	public static Vector3 divide(Vector3 a, Vector3 b) {
		return new Vector3(a.x / b.x, a.y / b.y, a.z / b.z);
	}

	/**
	 * Returns the dot product of A and B
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
	 * @param o Vector3 to use
	 * @return The ceiling vector
	 */
	public static Vector3 ceil(Vector3 o) {
		return new Vector3(Math.ceil(o.x), Math.ceil(o.y), Math.ceil(o.z));
	}

	/**
	 * Rounds the X, Y, and Z values of the given Vector3 down to the nearest
	 * integer value.
	 * @param o Vector3 to use
	 * @return The floor vector
	 */
	public static Vector3 floor(Vector3 o) {
		return new Vector3(Math.floor(o.x), Math.floor(o.y), Math.floor(o.z));
	}

	/**
	 * Rounds the X, Y, and Z values of the given Vector3 to the nearest integer
	 * value.
	 * @param o Vector3 to use
	 * @return The rounded vector
	 */
	public static Vector3 round(Vector3 o) {
		return new Vector3(Math.round(o.x), Math.round(o.y), Math.round(o.z));
	}

	/**
	 * Sets the X, Y, and Z values of the given Vector3 to their absolute value.
	 * @param o Vector3 to use
	 * @return The absolute vector
	 */
	public static Vector3 abs(Vector3 o) {
		return new Vector3(Math.abs(o.x), Math.abs(o.y), Math.abs(o.z));
	}

	/**
	 * Returns a Vector3 containing the smallest X, Y, and Z values.
	 * @param o1 The first vector
	 * @param o2 The second vector
	 * @return The minimum of both vectors combined
	 */
	public static Vector3 min(Vector3 o1, Vector3 o2) {
		return new Vector3(Math.min(o1.x, o2.x), Math.min(o1.y, o2.y), Math.min(o1.z, o2.z));
	}

	/**
	 * Returns a Vector3 containing the largest X, Y, and Z values.
	 * @param o1 The first vector
	 * @param o2 The second vector
	 * @return The maximum of both vectors combined
	 */
	public static Vector3 max(Vector3 o1, Vector3 o2) {
		return new Vector3(Math.max(o1.x, o2.x), Math.max(o1.y, o2.y), Math.max(o1.z, o2.z));
	}

	/**
	 * Returns a Vector3 with random X, Y, and Z values (between 0 and 1)
	 * @return a random vector
	 */
	public static Vector3 random() {
		return new Vector3(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1);
	}

	/**
	 * Gets the distance between two Vector3.
	 * @param a The first vector
	 * @param b The second vector
	 * @return The distance between the two
	 */
	public static double distance(Vector3 a, Vector3 b) {
		return GenericMath.length(a.x - b.x, a.y - b.y, a.z - b.z);
	}

	/**
	 * Gets the squared distance between two Vector3.
	 * @param a The first vector
	 * @param b The second vector
	 * @return The square of the distance between the two
	 */
	public static double distanceSquared(Vector3 a, Vector3 b) {
		return GenericMath.lengthSquared(a.x - b.x, a.y - b.y, a.z - b.z);
	}

	/**
	 * Raises the X, Y, and Z values of a Vector3 to the given power.
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
	 * @param o Vector3 object to use
	 * @return The vector2 form
	 */
	public static Vector2 toVector2(Vector3 o) {
		return new Vector2(o.x, o.z);
	}

	/**
	 * Returns a new float array that is {x, y, z}
	 * @param a The vector
	 * @return An array of length 3 with x, y and z
	 */
	public static float[] toArray(Vector3 a) {
		return new float[]{a.x, a.y, a.z};
	}

	/**
	 * Gets the direction vector of a random pitch and yaw using the random
	 * specified.
	 * @param random to use
	 * @return the random direction vector
	 */
	public static Vector3 getRandomDirection3D(Random random) {
		return getDirection3D(random.nextFloat() * (float) TrigMath.TWO_PI,
				random.nextFloat() * (float) TrigMath.TWO_PI);
	}

	/**
	 * Gets the direction vector of a certain yaw and pitch.
	 * @param azimuth in radians
	 * @param inclination in radians
	 * @return the random direction vector
	 */
	public static Vector3 getDirection3D(float azimuth, float inclination) {
		final float yFact = TrigMath.cos(inclination);
		return new Vector3(yFact * TrigMath.cos(azimuth), TrigMath.sin(inclination), yFact * TrigMath.sin(azimuth));
	}

	/**
	 * Gets the direction vector of a random angle using the random specified.
	 * @param random to use
	 * @return the random direction vector
	 */
	public static Vector2 getRandomDirection2D(Random random) {
		return getDirection2D(random.nextFloat() * (float) TrigMath.TWO_PI);
	}

	/**
	 * Gets the direction vector of a certain angle.
	 * @param azimuth in radians
	 * @return the direction vector
	 */
	public static Vector2 getDirection2D(float azimuth) {
		return new Vector2(TrigMath.cos(azimuth), TrigMath.sin(azimuth));
	}

	/**
	 * Returns the forward vector transformed by the provided quaternion
	 * @param rot The rotation
	 * @return The forward vector for the rotation
	 */
	public static Vector3 getDirection(Quaternion rot) {
		return transform(Vector3.FORWARD, rot);
	}

	/**
	 * Calculates and returns a new Vector3 transformed by the given quaternion
	 * @param vector The vector to transform
	 * @param rot The rotation to apply
	 * @return The rotated vector
	 */
	public static Vector3 transform(Vector3 vector, Quaternion rot) {
		return transform(vector, MatrixMath.rotate(rot));
	}

	/**
	 * Calculates and returns a new Vector3 transformed by the transformation
	 * matrix.
	 * @param v the vector to transform
	 * @param m the transformation matrix
	 * @return The transformed vector
	 */
	public static Vector3 transform(Vector3 v, Matrix m) {
		float[] vector = {v.getX(), v.getY(), v.getZ(), 1};
		float[] vres = new float[4];
		final int dimension = m.getDimension();
		for (int i = 0; i < dimension; i++) {
			vres[i] = 0;
			for (int k = 0; k < dimension; k++) {
				float n = m.get(i, k) * vector[k];
				vres[i] += n;
			}
		}

		return new Vector3(vres[0], vres[1], vres[2]);
	}

	/**
	 * Compares two vectors.
	 * @param a the first vector
	 * @param b the second vector
	 * @return the result of the comparison
	 * @see {@link java.lang.Comparable}.
	 */
	public static int compareTo(Vector3 a, Vector3 b) {
		return (int) a.lengthSquared() - (int) b.lengthSquared();
	}

	/**
	 * Transforms a vecmath 3D vector to a Spout 3D vector.
	 * @param vector The vecmath 3D vector
	 * @return The vector as a Spout 3D vector
	 */
	public static Vector3 toVector3(Vector3f vector) {
		return new Vector3(vector.x, vector.y, vector.z);
	}

	/**
	 * Transforms a Spout 3D vector to a vecmath 3D vector.
	 * @param vector The Spout 3D vector
	 * @return The vector as a vecmath 3D vector
	 */
	public static Vector3f toVector3f(Vector3 vector) {
		return new Vector3f(vector.x, vector.y, vector.z);
	}
}
