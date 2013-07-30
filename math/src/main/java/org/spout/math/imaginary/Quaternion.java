/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.math.imaginary;

import java.io.Serializable;

import org.spout.math.GenericMath;
import org.spout.math.TrigMath;
import org.spout.math.matrix.Matrix3;
import org.spout.math.vector.Vector3;

/**
 * Represent a quaternion of the form <code>xi + yj + zk + w</code>. The x, y, z and w components are stored as floats. This class is immutable.
 */
public class Quaternion implements Imaginary, Comparable<Quaternion>, Serializable, Cloneable {
	private static final long serialVersionUID = 1;
	/**
	 * An immutable identity (0, 0, 0, 1) quaternion.
	 */
	public static final Quaternion IDENTITY = new Quaternion();
	private final float x;
	private final float y;
	private final float z;
	private final float w;
	private transient volatile boolean hashed = false;
	private transient volatile int hashCode = 0;

	/**
	 * Constructs a new quaternion. The components are set to the identity (0, 0, 0, 1).
	 */
	public Quaternion() {
		this(0, 0, 0, 1);
	}

	/**
	 * Constructs a new quaternion from the double components.
	 *
	 * @param x The x (imaginary) component
	 * @param y The y (imaginary) component
	 * @param z The z (imaginary) component
	 * @param w The w (real) component
	 */
	public Quaternion(double x, double y, double z, double w) {
		this((float) x, (float) y, (float) z, (float) w);
	}

	/**
	 * Constructs a new quaternion from the float components.
	 *
	 * @param x The x (imaginary) component
	 * @param y The y (imaginary) component
	 * @param z The z (imaginary) component
	 * @param w The w (real) component
	 */
	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	/**
	 * Copy constructor.
	 *
	 * @param q The quaternion to copy
	 */
	public Quaternion(Quaternion q) {
		this(q.x, q.y, q.z, q.w);
	}

	/**
	 * Gets the x (imaginary) component of this quaternion.
	 *
	 * @return The x (imaginary) component
	 */
	public float getX() {
		return x;
	}

	/**
	 * Gets the y (imaginary) component of this quaternion.
	 *
	 * @return The y (imaginary) component
	 */
	public float getY() {
		return y;
	}

	/**
	 * Gets the z (imaginary) component of this quaternion.
	 *
	 * @return The z (imaginary) component
	 */
	public float getZ() {
		return z;
	}

	/**
	 * Gets the w (real) component of this quaternion.
	 *
	 * @return The w (real) component
	 */
	public float getW() {
		return w;
	}

	/**
	 * Gets the pitch component of this quaternion in degrees
	 *
	 * @return The pitch component in degrees
	 */
	public float getPitch() {
		return getAxesAngleDeg().getX();
	}

	/**
	 * Gets the yaw component of this quaternion in degrees
	 *
	 * @return The yaw component in degrees
	 */
	public float getYaw() {
		return getAxesAngleDeg().getY();
	}

	/**
	 * Gets the roll component of this quaternion in degrees
	 *
	 * @return The roll component in degrees
	 */
	public float getRoll() {
		return getAxesAngleDeg().getZ();
	}

	/**
	 * Adds another quaternion to this one.
	 *
	 * @param q The quaternion to add
	 * @return A new quaternion, which is the sum of both
	 */
	public Quaternion add(Quaternion q) {
		return add(q.x, q.y, q.z, q.w);
	}

	/**
	 * Adds the double components of another quaternion to this one.
	 *
	 * @param x The x (imaginary) component of the quaternion to add
	 * @param y The y (imaginary) component of the quaternion to add
	 * @param z The z (imaginary) component of the quaternion to add
	 * @param w The w (real) component of the quaternion to add
	 * @return A new quaternion, which is the sum of both
	 */
	public Quaternion add(double x, double y, double z, double w) {
		return add((float) x, (float) y, (float) z, (float) w);
	}

	/**
	 * Adds the float components of another quaternion to this one.
	 *
	 * @param x The x (imaginary) component of the quaternion to add
	 * @param y The y (imaginary) component of the quaternion to add
	 * @param z The z (imaginary) component of the quaternion to add
	 * @param w The w (real) component of the quaternion to add
	 * @return A new quaternion, which is the sum of both
	 */
	public Quaternion add(float x, float y, float z, float w) {
		return new Quaternion(this.x + x, this.y + y, this.z + z, this.w + w);
	}

	/**
	 * Subtracts another quaternion from this one.
	 *
	 * @param q The quaternion to subtract
	 * @return A new quaternion, which is the difference of both
	 */
	public Quaternion sub(Quaternion q) {
		return sub(q.x, q.y, q.z, q.w);
	}

	/**
	 * Subtracts the double components of another quaternion from this one.
	 *
	 * @param x The x (imaginary) component of the quaternion to subtract
	 * @param y The y (imaginary) component of the quaternion to subtract
	 * @param z The z (imaginary) component of the quaternion to subtract
	 * @param w The w (real) component of the quaternion to subtract
	 * @return A new quaternion, which is the difference of both
	 */
	public Quaternion sub(double x, double y, double z, double w) {
		return sub((float) x, (float) y, (float) z, (float) w);
	}

	/**
	 * Subtracts the float components of another quaternion from this one.
	 *
	 * @param x The x (imaginary) component of the quaternion to subtract
	 * @param y The y (imaginary) component of the quaternion to subtract
	 * @param z The z (imaginary) component of the quaternion to subtract
	 * @param w The w (real) component of the quaternion to subtract
	 * @return A new quaternion, which is the difference of both
	 */
	public Quaternion sub(float x, float y, float z, float w) {
		return new Quaternion(this.x - x, this.y - y, this.z - z, this.w - w);
	}

	/**
	 * Multiplies the components of this quaternion by a double scalar.
	 *
	 * @param a The multiplication scalar
	 * @return A new quaternion, which has each component multiplied by the scalar
	 */
	public Quaternion mul(double a) {
		return mul((float) a);
	}

	/**
	 * Multiplies the components of this quaternion by a float scalar.
	 *
	 * @param a The multiplication scalar
	 * @return A new quaternion, which has each component multiplied by the scalar
	 */
	@Override
	public Quaternion mul(float a) {
		return new Quaternion(x * a, y * a, z * a, w * a);
	}

	/**
	 * Multiplies another quaternion with this one.
	 *
	 * @param q The quaternion to multiply with
	 * @return A new quaternion, which is the product of both
	 */
	public Quaternion mul(Quaternion q) {
		return mul(q.x, q.y, q.z, q.w);
	}

	/**
	 * Multiplies the double components of another quaternion with this one.
	 *
	 * @param x The x (imaginary) component of the quaternion to multiply with
	 * @param y The y (imaginary) component of the quaternion to multiply with
	 * @param z The z (imaginary) component of the quaternion to multiply with
	 * @param w The w (real) component of the quaternion to multiply with
	 * @return A new quaternion, which is the product of both
	 */
	public Quaternion mul(double x, double y, double z, double w) {
		return mul((float) x, (float) y, (float) z, (float) w);
	}

	/**
	 * Multiplies the float components of another quaternion with this one.
	 *
	 * @param x The x (imaginary) component of the quaternion to multiply with
	 * @param y The y (imaginary) component of the quaternion to multiply with
	 * @param z The z (imaginary) component of the quaternion to multiply with
	 * @param w The w (real) component of the quaternion to multiply with
	 * @return A new quaternion, which is the product of both
	 */
	public Quaternion mul(float x, float y, float z, float w) {
		return new Quaternion(
				this.w * x + this.x * w + this.y * z - this.z * y,
				this.w * y + this.y * w + this.z * x - this.x * z,
				this.w * z + this.z * w + this.x * y - this.y * x,
				this.w * w - this.x * x - this.y * y - this.z * z);
	}

	/**
	 * Divides the components of this quaternion by a double scalar.
	 *
	 * @param a The division scalar
	 * @return A new quaternion, which has each component divided by the scalar
	 */
	public Quaternion div(double a) {
		return div((float) a);
	}

	/**
	 * Divides the components of this quaternion by a float scalar.
	 *
	 * @param a The division scalar
	 * @return A new quaternion, which has each component divided by the scalar
	 */
	@Override
	public Quaternion div(float a) {
		return new Quaternion(x / a, y / a, z / a, w / a);
	}

	// TODO: quaternion division?

	/**
	 * Returns the dot product of this quaternion with another one.
	 *
	 * @param q The quaternion to calculate the dot product with
	 * @return The dot product of the two quaternions
	 */
	public float dot(Quaternion q) {
		return dot(q.x, q.y, q.z, q.w);
	}

	/**
	 * Returns the dot product of this quaternion with the double components of another one.
	 *
	 * @param x The x (imaginary) component of the quaternion to calculate the dot product with
	 * @param y The y (imaginary) component of the quaternion to calculate the dot product with
	 * @param z The z (imaginary) component of the quaternion to calculate the dot product with
	 * @param w The w (real) component of the quaternion to calculate the dot product with
	 * @return The dot product of the two quaternions
	 */
	public float dot(double x, double y, double z, double w) {
		return dot((float) x, (float) y, (float) z, (float) w);
	}

	/**
	 * Returns the dot product of this quaternion with the floats components of another one.
	 *
	 * @param x The x (imaginary) component of the quaternion to calculate the dot product with
	 * @param y The y (imaginary) component of the quaternion to calculate the dot product with
	 * @param z The z (imaginary) component of the quaternion to calculate the dot product with
	 * @param w The w (real) component of the quaternion to calculate the dot product with
	 * @return The dot product of the two quaternions
	 */
	public float dot(float x, float y, float z, float w) {
		return this.x * x + this.y * y + this.z * z + this.w * w;
	}

	/**
	 * Returns a unit vector representing the direction of this quaternion, which is {@link Vector3#FORWARD} rotated by this quaternion.
	 *
	 * @return The vector representing the direction this quaternion is pointing to
	 */
	public Vector3 getDirection() {
		return Matrix3.createRotation(this).transform(Vector3.FORWARD);
	}

	/**
	 * Returns the angles in degrees around the x, y and z axes that correspond to the rotation represented by this quaternion.
	 *
	 * @return The angle in degrees for each axis, stored in a vector, in the corresponding component
	 */
	public Vector3 getAxesAngleDeg() {
		return getAxesAnglesRad().mul(TrigMath.RAD_TO_DEG);
	}

	/**
	 * Returns the angles in radians around the x, y and z axes that correspond to the rotation represented by this quaternion.
	 *
	 * @return The angle in radians for each axis, stored in a vector, in the corresponding component
	 */
	public Vector3 getAxesAnglesRad() {
		final double roll;
		final double pitch;
		double yaw;
		final double test = w * x - y * z;
		if (Math.abs(test) < 0.4999) {
			roll = TrigMath.atan2(2 * (w * z + x * y), 1 - 2 * (x * x + z * z));
			pitch = TrigMath.asin(2 * test);
			yaw = TrigMath.atan2(2 * (w * y + z * x), 1 - 2 * (x * x + y * y));
		} else {
			final int sign = (test < 0) ? -1 : 1;
			roll = 0;
			pitch = sign * Math.PI / 2;
			yaw = -sign * 2 * TrigMath.atan2(z, w);
		}
		if (yaw > 180) {
			yaw -= 360;
		} else if (yaw < -180) {
			yaw += 360;
		}
		return new Vector3(pitch, yaw, roll);
	}

	/**
	 * Conjugates the quaternion. <br> Conjugation of a quaternion <code>a</code> is an operation returning quaternion <code>a'</code> such that <code>a' * a = a * a' = |a|<sup>2</sup></code> where
	 * <code>|a|<sup>2<sup/></code> is squared length of <code>a</code>.
	 *
	 * @return the conjugated quaternion
	 */
	@Override
	public Quaternion conjugate() {
		return new Quaternion(-x, -y, -z, w);
	}

	/**
	 * Inverts the quaternion. <br> Inversion of a quaternion <code>a</code> returns quaternion <code>a<sup>-1</sup> = a' / |a|<sup>2</sup></code> where <code>a'</code> is {@link #conjugate()
	 * conjugation} of <code>a</code>, and <code>|a|<sup>2</sup></code> is squared length of <code>a</code>. <br> For any quaternions <code>a, b, c</code>, such that <code>a * b = c</code> equations
	 * <code>a<sup>-1</sup> * c = b</code> and <code>c * b<sup>-1</sup> = a</code> are true.
	 *
	 * @return the inverted quaternion
	 */
	@Override
	public Quaternion invert() {
		return conjugate().div(lengthSquared());
	}

	/**
	 * Returns the square of the length of this quaternion.
	 *
	 * @return The square of the length
	 */
	@Override
	public float lengthSquared() {
		return (float) GenericMath.lengthSquared(x, y, z, w);
	}

	/**
	 * Returns the length of this quaternion.
	 *
	 * @return The length
	 */
	@Override
	public float length() {
		return (float) GenericMath.length(x, y, z, w);
	}

	/**
	 * Normalizes this quaternion.
	 *
	 * @return A new quaternion of unit length
	 */
	@Override
	public Quaternion normalize() {
		final float length = length();
		return new Quaternion(x / length, y / length, z / length, w / length);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Quaternion)) {
			return false;
		}
		final Quaternion that = (Quaternion) o;
		if (Float.compare(that.w, w) != 0) {
			return false;
		}
		if (Float.compare(that.x, x) != 0) {
			return false;
		}
		if (Float.compare(that.y, y) != 0) {
			return false;
		}
		if (Float.compare(that.z, z) != 0) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		if (!hashed) {
			int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
			result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
			result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
			hashCode = 31 * result + (w != +0.0f ? Float.floatToIntBits(w) : 0);
			hashed = true;
		}
		return hashCode;
	}

	@Override
	public int compareTo(Quaternion q) {
		return (int) (lengthSquared() - q.lengthSquared());
	}

	@Override
	public Quaternion clone() {
		return new Quaternion(this);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + w + ")";
	}

	/**
	 * Creates a new quaternion from the double angles in degrees around the x, y and z axes.
	 *
	 * @param pitch The rotation around x
	 * @param yaw The rotation around y
	 * @param roll The rotation around z
	 * @return The quaternion defined by the rotations around the axes
	 */
	public static Quaternion fromAxesAnglesDeg(double pitch, double yaw, double roll) {
		return fromAxesAnglesDeg((float) pitch, (float) yaw, (float) roll);
	}

	/**
	 * Creates a new quaternion from the double angles in radians around the x, y and z axes.
	 *
	 * @param pitch The rotation around x
	 * @param yaw The rotation around y
	 * @param roll The rotation around z
	 * @return The quaternion defined by the rotations around the axes
	 */
	public static Quaternion fromAxesAnglesRad(double pitch, double yaw, double roll) {
		return fromAxesAnglesRad((float) pitch, (float) yaw, (float) roll);
	}

	/**
	 * Creates a new quaternion from the float angles in degrees around the x, y and z axes.
	 *
	 * @param pitch The rotation around x
	 * @param yaw The rotation around y
	 * @param roll The rotation around z
	 * @return The quaternion defined by the rotations around the axes
	 */
	public static Quaternion fromAxesAnglesDeg(float pitch, float yaw, float roll) {
		return Quaternion.fromAngleDegAxis(yaw, Vector3.UNIT_Y).
				mul(Quaternion.fromAngleDegAxis(pitch, Vector3.UNIT_X)).
				mul(Quaternion.fromAngleDegAxis(roll, Vector3.UNIT_Z));
	}

	/**
	 * Creates a new quaternion from the float angles in radians around the x, y and z axes.
	 *
	 * @param pitch The rotation around x
	 * @param yaw The rotation around y
	 * @param roll The rotation around z
	 * @return The quaternion defined by the rotations around the axes
	 */
	public static Quaternion fromAxesAnglesRad(float pitch, float yaw, float roll) {
		return Quaternion.fromAngleRadAxis(yaw, Vector3.UNIT_Y).
				mul(Quaternion.fromAngleRadAxis(pitch, Vector3.UNIT_X)).
				mul(Quaternion.fromAngleRadAxis(roll, Vector3.UNIT_Z));
	}

	/**
	 * Creates a new quaternion from the angle-axis rotation defined from the first to the second vector.
	 *
	 * @param from The first vector
	 * @param to The second vector
	 * @return The quaternion defined by the angle-axis rotation between the vectors
	 */
	public static Quaternion fromRotationTo(Vector3 from, Vector3 to) {
		return Quaternion.fromAngleRadAxis(TrigMath.acos(from.dot(to) / (from.length() * to.length())), from.cross(to));
	}

	/**
	 * Creates a new quaternion from the rotation double angle in degrees around the axis vector.
	 *
	 * @param angle The rotation angle in degrees
	 * @param axis The axis of rotation
	 * @return The quaternion defined by the rotation around the axis
	 */
	public static Quaternion fromAngleDegAxis(double angle, Vector3 axis) {
		return fromAngleRadAxis(Math.toRadians(angle), axis);
	}

	/**
	 * Creates a new quaternion from the rotation double angle in radians around the axis vector.
	 *
	 * @param angle The rotation angle in radians
	 * @param axis The axis of rotation
	 * @return The quaternion defined by the rotation around the axis
	 */
	public static Quaternion fromAngleRadAxis(double angle, Vector3 axis) {
		return fromAngleRadAxis((float) angle, axis);
	}

	/**
	 * Creates a new quaternion from the rotation float angle in degrees around the axis vector.
	 *
	 * @param angle The rotation angle in degrees
	 * @param axis The axis of rotation
	 * @return The quaternion defined by the rotation around the axis
	 */
	public static Quaternion fromAngleDegAxis(float angle, Vector3 axis) {
		return fromAngleRadAxis((float) Math.toRadians(angle), axis);
	}

	/**
	 * Creates a new quaternion from the rotation float angle in radians around the axis vector.
	 *
	 * @param angle The rotation angle in radians
	 * @param axis The axis of rotation
	 * @return The quaternion defined by the rotation around the axis
	 */
	public static Quaternion fromAngleRadAxis(float angle, Vector3 axis) {
		return fromAngleRadAxis(angle, axis.getX(), axis.getY(), axis.getZ());
	}

	/**
	 * Creates a new quaternion from the rotation double angle in degrees around the axis vector double components.
	 *
	 * @param angle The rotation angle in degrees
	 * @param x The x component of the axis vector
	 * @param y The y component of the axis vector
	 * @param z The z component of the axis vector
	 * @return The quaternion defined by the rotation around the axis
	 */
	public static Quaternion fromAngleDegAxis(double angle, double x, double y, double z) {
		return fromAngleRadAxis(Math.toRadians(angle), x, y, z);
	}

	/**
	 * Creates a new quaternion from the rotation double angle in radians around the axis vector double components.
	 *
	 * @param angle The rotation angle in radians
	 * @param x The x component of the axis vector
	 * @param y The y component of the axis vector
	 * @param z The z component of the axis vector
	 * @return The quaternion defined by the rotation around the axis
	 */
	public static Quaternion fromAngleRadAxis(double angle, double x, double y, double z) {
		return fromAngleRadAxis((float) angle, (float) x, (float) y, (float) z);
	}

	/**
	 * Creates a new quaternion from the rotation float angle in degrees around the axis vector float components.
	 *
	 * @param angle The rotation angle in degrees
	 * @param x The x component of the axis vector
	 * @param y The y component of the axis vector
	 * @param z The z component of the axis vector
	 * @return The quaternion defined by the rotation around the axis
	 */
	public static Quaternion fromAngleDegAxis(float angle, float x, float y, float z) {
		return fromAngleRadAxis((float) Math.toRadians(angle), x, y, z);
	}

	/**
	 * Creates a new quaternion from the rotation float angle in radians around the axis vector float components.
	 *
	 * @param angle The rotation angle in radians
	 * @param x The x component of the axis vector
	 * @param y The y component of the axis vector
	 * @param z The z component of the axis vector
	 * @return The quaternion defined by the rotation around the axis
	 */
	public static Quaternion fromAngleRadAxis(float angle, float x, float y, float z) {
		final float halfAngle = angle / 2;
		final double q = TrigMath.sin(halfAngle) / Math.sqrt(x * x + y * y + z * z);
		return new Quaternion(x * q, y * q, z * q, TrigMath.cos(halfAngle));
	}

	/**
	 * Creates a new quaternion from the rotation matrix. The matrix will be interpreted as a rotation matrix even if it is not.
	 *
	 * @param matrix The rotation matrix
	 * @return The quaternion defined by the rotation matrix
	 */
	public static Quaternion fromRotationMatrix(Matrix3 matrix) {
		final float trace = matrix.trace();
		if (trace < 0) {
			if (matrix.get(1, 1) > matrix.get(0, 0)) {
				if (matrix.get(2, 2) > matrix.get(1, 1)) {
					final float r = (float) Math.sqrt(matrix.get(2, 2) - matrix.get(0, 0) - matrix.get(1, 1) + 1);
					final float s = 0.5f / r;
					return new Quaternion(
							(matrix.get(2, 0) + matrix.get(0, 2)) * s,
							(matrix.get(1, 2) + matrix.get(2, 1)) * s,
							0.5f * r,
							(matrix.get(1, 0) - matrix.get(0, 1)) * s);
				} else {
					final float r = (float) Math.sqrt(matrix.get(1, 1) - matrix.get(2, 2) - matrix.get(0, 0) + 1);
					final float s = 0.5f / r;
					return new Quaternion(
							(matrix.get(0, 1) + matrix.get(1, 0)) * s,
							0.5f * r,
							(matrix.get(1, 2) + matrix.get(2, 1)) * s,
							(matrix.get(0, 2) - matrix.get(2, 0)) * s);
				}
			} else if (matrix.get(2, 2) > matrix.get(0, 0)) {
				final float r = (float) Math.sqrt(matrix.get(2, 2) - matrix.get(0, 0) - matrix.get(1, 1) + 1);
				final float s = 0.5f / r;
				return new Quaternion(
						(matrix.get(2, 0) + matrix.get(0, 2)) * s,
						(matrix.get(1, 2) + matrix.get(2, 1)) * s,
						0.5f * r,
						(matrix.get(1, 0) - matrix.get(0, 1)) * s);
			} else {
				final float r = (float) Math.sqrt(matrix.get(0, 0) - matrix.get(1, 1) - matrix.get(2, 2) + 1);
				final float s = 0.5f / r;
				return new Quaternion(
						0.5f * r,
						(matrix.get(0, 1) + matrix.get(1, 0)) * s,
						(matrix.get(2, 0) - matrix.get(0, 2)) * s,
						(matrix.get(2, 1) - matrix.get(1, 2)) * s);
			}
		} else {
			final float r = (float) Math.sqrt(trace + 1);
			final float s = 0.5f / r;
			return new Quaternion(
					(matrix.get(2, 1) - matrix.get(1, 2)) * s,
					(matrix.get(0, 2) - matrix.get(2, 0)) * s,
					(matrix.get(1, 0) - matrix.get(0, 1)) * s,
					0.5f * r);
		}
	}
}
