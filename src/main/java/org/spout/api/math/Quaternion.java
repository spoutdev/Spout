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
 * Represents a rotation around a unit 4d circle.
 *
 *
 */
public class Quaternion implements Cloneable{
	protected final float x, y, z, w;
	protected Vector3 cachedAngle = null;

	/**
	 * Represents no rotation
	 */
	public static Quaternion identity = new Quaternion(0, 0, 0, 1);

	/**
	 * Constructs a new Quaternion with the given xyzw NOTE: This represents a
	 * Unit Vector in 4d space. Do not use unless you know what you are doing.
	 * If you want to create a normal rotation, use the angle/axis override.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	/**
	 * Constructs a new Quaternion that represents a given rotation around an
	 * arbatrary axis
	 *
	 * @param angle Angle, in Degrees, to rotate the axis about by
	 * @param x-axis
	 * @param y-axis
	 * @param z-axis
	 * @param ignore
	 */
	public Quaternion(float angle, float x, float y, float z, boolean ignore) {
		double rads = Math.toRadians(angle);
		double halfAngle = Math.sin(rads / 2);
		this.x = (float) (x * halfAngle);
		this.y = (float) (y * halfAngle);
		this.z = (float) (z * halfAngle);
		this.w = (float) Math.cos(rads / 2);
	}

	/**
	 * Constructs a new Quaternion that represents a given rotation around an
	 * arbatrary axis
	 *
	 * @param angle Angle, in Degrees, to rotate the axis about by
	 * @param axis
	 */
	public Quaternion(float angle, Vector3 axis) {
		this(angle, axis.getX(), axis.getY(), axis.getZ(), true);
	}

	/**
	 * Copy Constructor
	 */
	public Quaternion(Quaternion rotation) {
		this(rotation.x, rotation.y, rotation.z, rotation.w);
	}

	/**
	 * Returns the X component of the quaternion
	 *
	 * @return
	 */
	public float getX() {
		return x;
	}

	/**
	 * Returns the Y component of the quaternion
	 *
	 * @return
	 */
	public float getY() {
		return y;
	}

	/**
	 * Returns the Z component of the quaternion
	 *
	 * @return
	 */
	public float getZ() {
		return z;
	}

	/**
	 * Returns the W component of the quaternion
	 *
	 * @return
	 */
	public float getW() {
		return w;
	}

	/**
	 * Returns the length squared of the quaternion
	 *
	 * @return
	 */
	public float lengthSquared() {
		return Quaternion.lengthSquared(this);
	}

	/**
	 * Returns the length of the quaternion. Note: This uses square root, so is
	 * slowish
	 *
	 * @return
	 */
	public float length() {
		return Quaternion.length(this);
	}

	/**
	 * Returns this quaternion but length() == 1
	 *
	 * @return
	 */
	public Quaternion normalize() {
		return Quaternion.normalize(this);
	}

	/**
	 * Multiplies this Quaternion by the other Quaternion
	 *
	 * @param o
	 * @return
	 */
	public Quaternion multiply(Quaternion o) {
		return Quaternion.multiply(this, o);
	}

	/**
	 * Creates and returns a new Quaternion that represnets this quaternion
	 * rotated by the given Axis and Angle
	 *
	 * @param angle
	 * @param axis
	 * @return rotated Quaternion
	 */
	public Quaternion rotate(float angle, Vector3 axis) {
		return Quaternion.rotate(this, angle, axis);
	}
	
	/**
	 * Creates and returns a new Quaternion that represnets this quaternion
	 * rotated by the given Axis and Angle
	 *
	 * @param angle
	 * @param x axis
	 * @param y axis
	 * @param z axis
	 * @return rotated Quaternion
	 */
	public Quaternion rotate(float angle, float x, float y, float z) {
		return Quaternion.rotate(this, angle, x, y, z);
	}
	
	@Override
	public Quaternion clone() {
		return new Quaternion(x, y, z, w);
	}

	/**
	 * Returns the angles about each axis of this quaternion stored in a Vector3
	 *
	 * vect.X = Rotation about the X axis (Roll) vect.Y = Rotation about the Y
	 * axis (Yaw) vect.Z = Rotation about the Z axis (Pitch)
	 *
	 * @param a
	 * @return
	 */
	public Vector3 getAxisAngles() {
		if (cachedAngle != null) {
			return cachedAngle.clone();
		}
		else {
			cachedAngle = Quaternion.getAxisAngles(this);
			return cachedAngle.clone();
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + x + "," + y + "," + z + "," + w + "}";
	}

	/**
	 * Returns the length squared of the given Quaternion
	 *
	 * @param a
	 * @return
	 */
	public static float lengthSquared(Quaternion a) {
		return a.x * a.x + a.y * a.y + a.z * a.z + a.w * a.w;
	}

	/**
	 * Returns the length of the given Quaternion <br/>
	 * <br/>
	 * Note: Uses Math.sqrt.
	 *
	 * @param a
	 * @return length of Quaternion
	 */
	public static float length(Quaternion a) {
		return (float) Math.sqrt(lengthSquared(a));
	}

	/**
	 * Constructs and returns a new Quaternion that is the given Quaternion but
	 * length() == 1
	 *
	 * @param a
	 * @return normalized Quaternion
	 */
	public static Quaternion normalize(Quaternion a) {
		float length = length(a);
		return new Quaternion(a.x / length, a.y / length, a.z / length, a.w / length);
	}

	/**
	 * Constructs and returns a new Quaternion that is A * B
	 *
	 * @param a
	 * @param b
	 * @return multiplied Quaternion
	 */
	public static Quaternion multiply(Quaternion a, Quaternion b) {
		float x = a.w * b.x + a.x * b.w + a.y * b.z - a.z * b.y;

		float y = a.w * b.y + a.y * b.w + a.z * b.x - a.x * b.z;

		float z = a.w * b.z + a.z * b.w + a.x * b.y - a.y * b.x;

		float w = a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z;

		return new Quaternion(x, y, z, w);
	}

	/**
	 * Constructs and returns a new Quaternion that is rotated about the axis
	 * and angle
	 *
	 * @param a
	 * @param angle
	 * @param axis
	 * @return rotated Quaternion
	 */
	public static Quaternion rotate(Quaternion a, float angle, Vector3 axis) {
		return multiply(new Quaternion(angle, axis), a);
	}
	
	/**
	 * Constructs and returns a new Quaternion that is rotated about the axis
	 * and angle
	 *
	 * @param a
	 * @param angle
	 * @param x axis
	 * @param y axis
	 * @param z axis
	 * @return rotated Quaternion
	 */
	public static Quaternion rotate(Quaternion a, float angle, float x, float y, float z) {
		return multiply(new Quaternion(angle, x, y, z, true), a);
	}

	/**
	 * Returns the angles, in degrees, about each axis of this quaternion stored
	 * in a Vector3 <br/> <br/>
	 *
	 * vect.X = Rotation about the X axis (Roll) <br/>
	 * vect.Y = Rotation about the Y axis (Yaw) <br/>
	 * vect.Z = Rotation about the Z axis (Pitch) <br/>
	 *
	 * @param a
	 * @return axis angles
	 */
	public static Vector3 getAxisAngles(Quaternion a) {
		//Forward is 1,0,0
		float yaw = (float) Math.toDegrees(Math.atan2(2 * (a.getX() * a.getY() + a.getZ() * a.getW()), 1 - 2 * (a.getY() * a.getY() + a.getZ() * a.getZ())));
		//According to this calculation, {0, 1, 0} is down, so we need to multiply by -1
		float pitch = -1 * (float) Math.toDegrees(Math.asin(2 * (a.getX() * a.getZ() - a.getW() * a.getY())));
		//Our left and right are swapped from this calculation, so we need to subtract the angle from 180.
		float roll = 180 - (float) Math.toDegrees(Math.atan2(2 * (a.getX() * a.getW() + a.getY() * a.getZ()), 1 - 2 * (a.getZ() * a.getZ() + a.getW() * a.getW())));

		return new Vector3(roll, pitch, yaw);
	}
}
