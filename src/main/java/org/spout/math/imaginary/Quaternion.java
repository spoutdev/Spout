/*
 * This file is part of Math.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Math is licensed under the Spout License Version 1.
 *
 * Math is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Math is distributed in the hope that it will be useful, but WITHOUT ANY
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

public class Quaternion implements Imaginary, Comparable<Quaternion>, Serializable, Cloneable {
	private static final long serialVersionUID = 1;
	public static final Quaternion IDENTITY = new Quaternion();
	private final float x;
	private final float y;
	private final float z;
	private final float w;
	private transient volatile boolean hashed = false;
	private transient volatile int hashCode = 0;

	public Quaternion() {
		this(0, 0, 0, 1);
	}

	public Quaternion(Quaternion q) {
		this(q.x, q.y, q.z, q.w);
	}

	public Quaternion(double x, double y, double z, double w) {
		this((float) x, (float) y, (float) z, (float) w);
	}

	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
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

	public float getW() {
		return w;
	}

	public Quaternion add(Quaternion q) {
		return add(q.x, q.y, q.z, q.w);
	}

	public Quaternion add(double x, double y, double z, double w) {
		return add((float) x, (float) y, (float) z, (float) w);
	}

	public Quaternion add(float x, float y, float z, float w) {
		return new Quaternion(this.x + x, this.y + y, this.z + z, this.w + w);
	}

	public Quaternion sub(Quaternion q) {
		return sub(q.x, q.y, q.z, q.w);
	}

	public Quaternion sub(double x, double y, double z, double w) {
		return sub((float) x, (float) y, (float) z, (float) w);
	}

	public Quaternion sub(float x, float y, float z, float w) {
		return new Quaternion(this.x - x, this.y - y, this.z - z, this.w - w);
	}

	public Quaternion mul(double a) {
		return mul((float) a);
	}

	/**
	 * Multiplies the quaternion by the given scalar.
	 *
	 * @param a the scalar
	 * @return the multiplied Quaternion
	 */
	@Override
	public Quaternion mul(float a) {
		return new Quaternion(x * a, y * a, z * a, w * a);
	}

	public Quaternion mul(Quaternion q) {
		return mul(q.x, q.y, q.z, q.w);
	}

	public Quaternion mul(double x, double y, double z, double w) {
		return mul((float) x, (float) y, (float) z, (float) w);
	}

	public Quaternion mul(float x, float y, float z, float w) {
		return new Quaternion(
				this.w * x + this.x * w + this.y * z - this.z * y,
				this.w * y + this.y * w + this.z * x - this.x * z,
				this.w * z + this.z * w + this.x * y - this.y * x,
				this.w * w - this.x * x - this.y * y - this.z * z);
	}

	public Quaternion div(double a) {
		return div((float) a);
	}

	/**
	 * Divides the quaternion by the given scalar.
	 *
	 * @param a the scalar
	 * @return the divided Quaternion
	 */
	@Override
	public Quaternion div(float a) {
		return new Quaternion(x / a, y / a, z / a, w / a);
	}

	// TODO: quaternion division?

	public float dot(Quaternion q) {
		return dot(q.x, q.y, q.z, q.w);
	}

	public float dot(double x, double y, double z, double w) {
		return dot((float) x, (float) y, (float) z, (float) w);
	}

	public float dot(float x, float y, float z, float w) {
		return this.x * x + this.y * y + this.z * z + this.w * w;
	}

	public Vector3 getDirection() {
		return Matrix3.createRotation(this).transform(Vector3.FORWARD);
	}

	public Vector3 getAxesAngleDeg() {
		return getAxesAnglesRad().mul(TrigMath.RAD_TO_DEG);
	}

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
	 * Conjugate the quaternion <br> Conjugation of a quaternion <code>a</code> is an operation
	 * returning quaternion <code>a'</code> such that <code>a' * a = a * a' = |a|^2</code> where
	 * <code>|a|^2</code> is squared length of <code>a</code>.
	 *
	 * @return the conjugated quaternion
	 * @see org.spout.math.matrix.MatrixN#transpose()
	 */
	@Override
	public Quaternion conjugate() {
		return new Quaternion(-x, -y, -z, w);
	}

	/**
	 * Invert the quaternion <br> Inversion of a quaternion <code>a</code> returns quaternion
	 * <code>a^-1 = a' / |a|^2</code> where <code>a'</code> is {@link #conjugate() conjugation} of
	 * <code>a</code>, and <code>|a|^2</code> is squared length of <code>a</code>. <br> For any
	 * quaternions <code>a, b, c</code>, such that <code>a * b = c</code> equations <code>a^-1 * c =
	 * b</code> and <code>c * b^-1 = a</code> are true.
	 *
	 * @return the inverted quaternion
	 */
	@Override
	public Quaternion invert() {
		return conjugate().div(lengthSquared());
	}

	@Override
	public float lengthSquared() {
		return (float) GenericMath.lengthSquared(x, y, z, w);
	}

	@Override
	public float length() {
		return (float) GenericMath.length(x, y, z, w);
	}

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

	public static Quaternion fromAxesAnglesDeg(double pitch, double yaw, double roll) {
		return fromAxesAnglesDeg((float) pitch, (float) yaw, (float) roll);
	}

	public static Quaternion fromAxesAnglesRad(double pitch, double yaw, double roll) {
		return fromAxesAnglesRad((float) pitch, (float) yaw, (float) roll);
	}

	public static Quaternion fromAxesAnglesDeg(float pitch, float yaw, float roll) {
		return Quaternion.fromAngleDegAxis(yaw, Vector3.UNIT_Y).
				mul(Quaternion.fromAngleDegAxis(pitch, Vector3.UNIT_X)).
				mul(Quaternion.fromAngleDegAxis(roll, Vector3.UNIT_Z));
	}

	public static Quaternion fromAxesAnglesRad(float pitch, float yaw, float roll) {
		return Quaternion.fromAngleRadAxis(yaw, Vector3.UNIT_Y).
				mul(Quaternion.fromAngleRadAxis(pitch, Vector3.UNIT_X)).
				mul(Quaternion.fromAngleRadAxis(roll, Vector3.UNIT_Z));
	}

	public static Quaternion fromRotationTo(Vector3 from, Vector3 to) {
		return Quaternion.fromAngleRadAxis(TrigMath.acos(from.dot(to) / (from.length() * to.length())), from.cross(to));
	}

	public static Quaternion fromAngleDegAxis(double angle, Vector3 axis) {
		return fromAngleRadAxis(Math.toRadians(angle), axis);
	}

	public static Quaternion fromAngleRadAxis(double angle, Vector3 axis) {
		return fromAngleRadAxis((float) angle, axis);
	}

	public static Quaternion fromAngleDegAxis(float angle, Vector3 axis) {
		return fromAngleRadAxis((float) Math.toRadians(angle), axis);
	}

	public static Quaternion fromAngleRadAxis(float angle, Vector3 axis) {
		return fromAngleRadAxis(angle, axis.getX(), axis.getY(), axis.getZ());
	}

	public static Quaternion fromAngleDegAxis(double angle, double x, double y, double z) {
		return fromAngleRadAxis(Math.toRadians(angle), x, y, z);
	}

	public static Quaternion fromAngleRadAxis(double angle, double x, double y, double z) {
		return fromAngleRadAxis((float) angle, (float) x, (float) y, (float) z);
	}

	public static Quaternion fromAngleDegAxis(float angle, float x, float y, float z) {
		return fromAngleRadAxis((float) Math.toRadians(angle), x, y, z);
	}

	public static Quaternion fromAngleRadAxis(float angle, float x, float y, float z) {
		final float halfAngle = angle / 2;
		final double q = TrigMath.sin(halfAngle) / Math.sqrt(x * x + y * y + z * z);
		return new Quaternion(x * q, y * q, z * q, TrigMath.cos(halfAngle));
	}
}
