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
package org.spout.math;

import java.io.Serializable;

public class Quaternion implements Comparable<Quaternion>, Serializable, Cloneable {
	private static final long serialVersionUID = 1;
	public static final Quaternion IDENTITY = new Quaternion();
	private final float x;
	private final float y;
	private final float z;
	private final float w;

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

	public Vector3 getDirection() {
		return toRotationMatrix(3).transform(Vector3.FORWARD);
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
			roll = Math.atan2(2 * (w * z + x * y), 1 - 2 * (x * x + z * z));
			pitch = Math.asin(2 * test);
			yaw = Math.atan2(2 * (w * y + z * x), 1 - 2 * (x * x + y * y));
		} else {
			final int sign = (test < 0) ? -1 : 1;
			roll = 0;
			pitch = sign * Math.PI / 2;
			yaw = -sign * 2 * Math.atan2(z, w);
		}
		if (yaw > 180) {
			yaw -= 360;
		} else if (yaw < -180) {
			yaw += 360;
		}
		return new Vector3(pitch, yaw, roll);
	}

	public float lengthSquared() {
		return GenericMath.lengthSquared(x, y, z, w);
	}

	public float length() {
		return GenericMath.length(x, y, z, w);
	}

	public Quaternion normalize() {
		final float length = length();
		return new Quaternion(x / length, y / length, z / length, w / length);
	}

	public Matrix toRotationMatrix(int size) {
		return Matrix.createRotation(size, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Quaternion)) {
			return false;
		}
		final Quaternion other = (Quaternion) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) {
			return false;
		}
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) {
			return false;
		}
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z)) {
			return false;
		}
		if (Float.floatToIntBits(w) != Float.floatToIntBits(other.w)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 59 * hash + Float.floatToIntBits(x);
		hash = 59 * hash + Float.floatToIntBits(y);
		hash = 59 * hash + Float.floatToIntBits(z);
		hash = 59 * hash + Float.floatToIntBits(w);
		return hash;
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

	public static Quaternion fromRotationTo(Vector3 from) {
		return Quaternion.fromRotationTo(from, Vector3.UNIT_Z);
	}

	public static Quaternion fromRotationTo(Vector3 from, Vector3 to) {
		return Quaternion.fromAngleRadAxis(Math.acos(from.dot(to) / (from.length() * to.length())), from.cross(to));
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
		return fromAngleRadAxis(Math.toDegrees(angle), x, y, z);
	}

	public static Quaternion fromAngleRadAxis(double angle, double x, double y, double z) {
		return fromAngleRadAxis((float) angle, (float) x, (float) y, (float) z);
	}

	public static Quaternion fromAngleDegAxis(float angle, float x, float y, float z) {
		return fromAngleRadAxis((float) Math.toRadians(angle), x, y, z);
	}

	public static Quaternion fromAngleRadAxis(float angle, float x, float y, float z) {
		final double halfAngle = angle / 2;
		final double q = Math.sin(halfAngle) / Math.sqrt(x * x + y * y + z * z);
		return new Quaternion(x * q, y * q, z * q, Math.cos(halfAngle));
	}
}
