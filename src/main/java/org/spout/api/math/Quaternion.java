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
 * Represents a rotation around a unit 4d circle.
 */
public class Quaternion implements Serializable {
	private static final long serialVersionUID = 1L;
	protected final float x, y, z, w;
	protected transient volatile Vector3 cachedAngle = null;
	/**
	 * Represents no rotation
	 */
	public static final Quaternion IDENTITY = new Quaternion(0, 0, 0, 1, true);
	/**
	 * Represents 90 degrees rotation around the x axis
	 */
	public static final Quaternion UNIT_X = new Quaternion(1, 0, 0, 0, true);
	/**
	 * Represents 90 degrees rotation around the < axis
	 */
	public static final Quaternion UNIT_Y = new Quaternion(0, 1, 0, 0, true);
	/**
	 * Represents 90 degrees rotation around the z axis
	 */
	public static final Quaternion UNIT_Z = new Quaternion(0, 0, 1, 0, true);

	/**
	 * Constructs a new Quaternion with the given xyzw NOTE: This represents a
	 * Unit Vector in 4d space. Do not use unless you know what you are doing.
	 * If you want to create a normal rotation, use the angle/axis override.
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @param ignore Ignored. This is because float float float float should be
	 * for angle/x,y,z
	 */
	public Quaternion(float x, float y, float z, float w, boolean ignore) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	/**
	 * Constructs a new Quaternion that represents a given rotation around an
	 * arbitrary axis
	 * @param angle Angle, in Degrees, to rotate the axis about by
	 * @param x-axis
	 * @param y-axis
	 * @param z-axis
	 */
	public Quaternion(float angle, float x, float y, float z) {
		double halfAngle = Math.toRadians(angle) / 2;
		double q = Math.sin(halfAngle) / Math.sqrt(x * x + y * y + z * z);
		this.x = (float) (x * q);
		this.y = (float) (y * q);
		this.z = (float) (z * q);
		this.w = (float) Math.cos(halfAngle);
	}

	/**
	 * Constructs a new Quaternion that represents a given rotation around an
	 * arbitrary axis
	 * @param angle Angle, in Degrees, to rotate the axis about by
	 * @param axis
	 */
	public Quaternion(float angle, Vector3 axis) {
		this(angle, axis.getX(), axis.getY(), axis.getZ());
	}

	/**
	 * Copy Constructor
	 */
	public Quaternion(Quaternion rotation) {
		this(rotation.x, rotation.y, rotation.z, rotation.w, false);
	}

	/**
	 * Returns the X component of the quaternion
	 * @return
	 */
	public float getX() {
		return x;
	}

	/**
	 * Returns the Y component of the quaternion
	 * @return
	 */
	public float getY() {
		return y;
	}

	/**
	 * Returns the Z component of the quaternion
	 * @return
	 */
	public float getZ() {
		return z;
	}

	/**
	 * Returns the W component of the quaternion
	 * @return
	 */
	public float getW() {
		return w;
	}

	public float getPitch() {
		return getAxisAngles().getX();
	}

	public float getYaw() {
		return getAxisAngles().getY();
	}

	public float getRoll() {
		return getAxisAngles().getZ();
	}

	/**
	 * Returns the direction this Quaternion is pointed at.
	 * @return direction of Quaternion
	 */
	public Vector3 getDirection() {
		return VectorMath.getDirection(this);
	}

	/**
	 * Returns the length squared of the quaternion
	 * @return
	 */
	public float lengthSquared() {
		return QuaternionMath.lengthSquared(this);
	}

	/**
	 * Returns the length of the quaternion. Note: This uses square root, so is
	 * slowish
	 * @return
	 */
	public float length() {
		return QuaternionMath.length(this);
	}

	/**
	 * Returns this quaternion but length() == 1
	 * @return
	 */
	public Quaternion normalize() {
		return QuaternionMath.normalize(this);
	}

	/**
	 * Multiplies this Quaternion by the other Quaternion
	 * @param o
	 * @return
	 */
	public Quaternion multiply(Quaternion o) {
		return QuaternionMath.multiply(this, o);
	}

	/**
	 * Creates and returns a new Quaternion that represnets this quaternion
	 * rotated by the given Axis and Angle
	 * @param angle
	 * @param axis
	 * @return rotated Quaternion
	 */
	public Quaternion rotate(float angle, Vector3 axis) {
		return QuaternionMath.rotate(this, angle, axis);
	}

	/**
	 * Creates and returns a new Quaternion that represnets this quaternion
	 * rotated by the given Axis and Angle
	 * @param angle
	 * @param x axis
	 * @param y axis
	 * @param z axis
	 * @return rotated Quaternion
	 */
	public Quaternion rotate(float angle, float x, float y, float z) {
		return QuaternionMath.rotate(this, angle, x, y, z);
	}

	/**
	 * Returns the angles about each axis of this quaternion stored in a Vector3
	 * <p/>
	 * vect.X = Rotation about the X axis (Roll) vect.Y = Rotation about the Y
	 * axis (Yaw) vect.Z = Rotation about the Z axis (Pitch)
	 * @return
	 */
	public Vector3 getAxisAngles() {
		if (cachedAngle == null) {
			cachedAngle = QuaternionMath.getAxisAngles(this);
		}

		return cachedAngle;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + StringUtil.toString(this.x, this.y, this.z, this.w);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Quaternion)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		Quaternion q = (Quaternion) obj;
		return this.w == q.w && this.x == q.x && this.y == q.y && this.z == q.z;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(41, 63).append(w).append(x).append(y).append(z).toHashCode();
	}
}
