/*
 * This file is part of Math.
 *
 * Copyright (c) 2011-2013, Spout LLC <http://www.spout.org/>
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

/**
 * Class containing quaternion mathematical functions.
 */
public class QuaternionMath {
	private QuaternionMath() {
	}

	/**
	 * Returns the length squared of the given Quaternion
	 * @param a The quaternion
	 * @return The square of the length
	 */
	public static float lengthSquared(Quaternion a) {
		return a.x * a.x + a.y * a.y + a.z * a.z + a.w * a.w;
	}

	/**
	 * Returns the length of the given Quaternion <br/> <br/> Note: Uses
	 * Math.sqrt.
	 * @param a The quaternion
	 * @return length of the quaternion
	 */
	public static float length(Quaternion a) {
		return (float) Math.sqrt(lengthSquared(a));
	}

	/**
	 * Constructs and returns a new Quaternion that is the given Quaternion but
	 * with a length of 1
	 * @param a The quaternion
	 * @return normalized Quaternion
	 */
	public static Quaternion normalize(Quaternion a) {
		float length = length(a);
		return new Quaternion(a.x / length, a.y / length, a.z / length, a.w / length, true);
	}

	/**
	 * Constructs and returns a new Quaternion that is A * B
	 * @param a The left quaternion
	 * @param b The right quaternion
	 * @return The product quaternion of left times b
	 */
	public static Quaternion multiply(Quaternion a, Quaternion b) {
		float x = a.w * b.x + a.x * b.w + a.y * b.z - a.z * b.y;

		float y = a.w * b.y + a.y * b.w + a.z * b.x - a.x * b.z;

		float z = a.w * b.z + a.z * b.w + a.x * b.y - a.y * b.x;

		float w = a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z;

		return new Quaternion(x, y, z, w, true);
	}

	/**
	 * Creates a quaternion of the axis angles.
	 * @param pitch The pitch; the rotation around x
	 * @param yaw The yaw; the rotation around y
	 * @param roll The roll; the rotation around z
	 * @return The quaternion representation or the pitch, yaw and roll
	 */
	public static Quaternion rotation(float pitch, float yaw, float roll) {
		final Quaternion qpitch = new Quaternion(pitch, Vector3.UNIT_X);
		final Quaternion qyaw = new Quaternion(yaw, Vector3.UNIT_Y);
		final Quaternion qroll = new Quaternion(roll, Vector3.UNIT_Z);
		return qyaw.multiply(qpitch).multiply(qroll);
	}

	/**
	 * Constructs and returns a new Quaternion that is rotated about the axis
	 * and angle
	 * @param a The quaternion
	 * @param angle The angle around the axis
	 * @param axis The axis
	 * @return The rotated Quaternion
	 */
	public static Quaternion rotate(Quaternion a, float angle, Vector3 axis) {
		return multiply(new Quaternion(angle, axis), a);
	}

	/**
	 * Constructs and returns a new Quaternion that is rotated about the axis
	 * and angle
	 * @param a The quaternion
	 * @param angle The angle
	 * @param x The x dimension of the axis
	 * @param y The y dimension of the axis
	 * @param z The z dimension of the axis
	 * @return The rotated Quaternion
	 */
	public static Quaternion rotate(Quaternion a, float angle, float x, float y, float z) {
		return multiply(new Quaternion(angle, x, y, z), a);
	}

	/**
	 * Returns the rotation between two vectors.
	 * @param from The first vector
	 * @param to The second vector
	 * @return the rotation between both vectors
	 */
	public static Quaternion rotationTo(Vector3 from, Vector3 to) {
		if (from == to || from.equals(to)) {
			return Quaternion.IDENTITY;
		}
		final Vector3 axis = from.cross(to).normalize();
		final float x = axis.getX();
		final float y = axis.getY();
		final float z = axis.getZ();
		final double halfAngle = TrigMath.acos(from.dot(to) / (from.length() * to.length())) / 2;
		final double q = Math.sin(halfAngle);
		// This doesn't use the axis-angle constructor because of loss of precision in a rad -> deg -> rad conversion.
		return new Quaternion((float) (x * q), (float) (y * q), (float) (z * q), (float) Math.cos(halfAngle), false);
	}

	/**
	 * Returns the angles, in degrees, about each axis of this quaternion stored
	 * in a Vector3 <br/> <br/>
	 * <p/>
	 * vect.X = Rotation about the X axis (Pitch) <br/> vect.Y = Rotation about
	 * the Y axis (Yaw) <br/> vect.Z = Rotation about the Z axis (Roll) <br/>
	 * @param a The quaternion
	 * @return The axis angles
	 */
	public static Vector3 getAxisAngles(Quaternion a) {
		// Map to Euler angles
		final float q0 = a.w;
		final float q1 = a.z; // roll
		final float q2 = a.x; // pitch
		final float q3 = a.y; // yaw

		final double r1, r2, r3, test;
		test = q0 * q2 - q3 * q1;

		if (Math.abs(test) < 0.4999) {
			r1 = Math.atan2(2 * (q0 * q1 + q2 * q3), 1 - 2 * (q1 * q1 + q2 * q2));
			r2 = Math.asin(2 * test);
			r3 = Math.atan2(2 * (q0 * q3 + q1 * q2), 1 - 2 * (q2 * q2 + q3 * q3));
		} else { // pitch is at north or south pole
			int sign = (test < 0) ? -1 : 1;
			r1 = 0;
			r2 = sign * TrigMath.HALF_PI;
			r3 = -sign * 2 * Math.atan2(q1, q0);
		}

		// ...and back to Tait-Bryan
		final float roll = (float) Math.toDegrees(r1);
		final float pitch = (float) Math.toDegrees(r2);
		float yaw = (float) Math.toDegrees(r3);
		if (yaw > 180) // keep -180 < yaw < 180
		{
			yaw -= 360;
		} else if (yaw < -180) {
			yaw += 360;
		}
		return new Vector3(pitch, yaw, roll);
	}
}
