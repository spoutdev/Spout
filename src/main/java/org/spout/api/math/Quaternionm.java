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
public class Quaternionm extends Quaternion implements Cloneable {
	/**
	 * Constructs a new Quaternion and sets the components equal to the identity
	 */
	public Quaternionm() {
		super(identity);
	}

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
	public Quaternionm(float x, float y, float z, float w) {
		super(x, y, z, w);
	}

	/**
	 * Constructs a new Quaternion that represents a given rotation around an
	 * arbatrary axis
	 *
	 * @param angle Angle, in Degrees, to rotate the axis about by
	 * @param axis
	 */
	public Quaternionm(float angle, Vector3 axis) {
		super(angle, axis);
	}

	/**
	 * Copy Constructor
	 */
	public Quaternionm(Quaternionm rotation) {
		super(rotation);
	}

	/**
	 * Sets the X component of the quaternion
	 *
	 * @param x
	 */
	public void setX(float x) {
		this.cachedAngle = null;
		this.x = x;
	}

	/**
	 * Sets the Y component of the quaternion
	 *
	 * @param y
	 */
	public void setY(float y) {
		this.cachedAngle = null;
		this.y = y;
	}

	/**
	 * Sets the Z component of the quaternion
	 *
	 * @param z
	 */
	public void setZ(float z) {
		this.cachedAngle = null;
		this.z = z;
	}

	/**
	 * Sets the W component of the quaternion
	 *
	 * @param w
	 */
	public void setW(float w) {
		this.cachedAngle = null;
		this.w = w;
	}

	/**
	 * Sets the value of the Quaternion
	 *
	 * @param quaternion
	 */
	public void set(Quaternion quaternion) {
		this.cachedAngle = null;
		x = quaternion.getX();
		y = quaternion.getY();
		z = quaternion.getZ();
		w = quaternion.getW();
	}

	@Override
	public Quaternion multiply(Quaternion o) {
		set(super.multiply(o));
		return this;
	}

	@Override
	public Quaternion normalize() {
		set(super.normalize());
		return this;
	}

	@Override
	public Quaternion rotate(float angle, Vector3 axis) {
		set(super.rotate(angle, axis));
		return this;
	}
	
	@Override
	public Quaternion rotate(float angle, float x, float y, float z) {
		set(super.rotate(angle, x, y, z));
		return this;
	}

	@Override
	public Quaternionm clone() {
		return new Quaternionm(x, y, z, w);
	}
}
