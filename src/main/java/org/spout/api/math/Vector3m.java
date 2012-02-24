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

public class Vector3m extends Vector3 implements Cloneable{
	public Vector3m() {
	}

	public Vector3m(Vector3 o) {
		super(o);
	}

	public Vector3m(int x, int y, int z) {
		super(x, y, z);
	}

	public Vector3m(double x, double y, double z) {
		super(x, y, z);
	}

	public Vector3m(float x, float y, float z) {
		super(x, y, z);
	}

	public void setZ(float z) {
		this.z = z;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void set(Vector3 vector) {
		x = vector.getX();
		y = vector.getY();
		z = vector.getZ();
	}

	/**
	 * Adds two vectors
	 *
	 * @param that
	 * @return
	 */

	@Override
	public Vector3 add(Vector3 that) {
		set(super.add(that));
		return this;
	}

	/**
	 * Subtracts two vectors
	 *
	 * @param that
	 * @return
	 */

	@Override
	public Vector3 subtract(Vector3 that) {
		set(super.subtract(that));
		return this;
	}

	/**
	 * Multiplies this Vector3 by the value of the Vector3 argument
	 *
	 * @param that The Vector3 to multiply
	 * @return the new Vector3
	 */
	@Override
	public Vector3 multiply(Vector3 that) {
		set(super.multiply(that));
		return this;
	}

	/**
	 * Divides the given Vector3 from this Vector3
	 *
	 * @param that The Vector3 to divide
	 * @return the new Vector3
	 */
	@Override
	public Vector3 divide(Vector3 that) {
		set(super.divide(that));
		return this;
	}

	/**
	 * Calculates and sets a new Vector3 transformed by the transformation
	 * matrix
	 *
	 * @param transformation
	 * @return
	 */
	@Override
	public Vector3 transform(Matrix transformation) {
		set(super.transform(transformation));
		return this;
	}

	/**
	 * Calculates and sets a new Vector3 transformed by the given quaternion
	 *
	 * @param vector
	 * @param rot
	 * @return
	 */
	@Override
	public Vector3 transform(Quaternion transformation) {
		set(super.transform(transformation));
		return this;
	}

	/**
	 * Raises the X, Y and Z values to the given power
	 *
	 * @param power
	 * @return
	 */
	@Override
	public Vector3 pow(double power) {
		set(super.pow(power));
		return this;
	}

	/**
	 * Takes the cross product of two vectors
	 *
	 * @param that
	 * @return
	 */
	@Override
	public Vector3 cross(Vector3 that) {
		set(super.cross(that));
		return this;
	}

	/**
	 * Rounds the X, Y, and Z values of this Vector3 up to the nearest integer
	 * value.
	 *
	 * @return
	 */
	@Override
	public Vector3 ceil() {
		set(super.ceil());
		return this;
	}

	/**
	 * Rounds the X, Y, and Z values of this Vector3 down to the nearest integer
	 * value.
	 *
	 * @return
	 */
	@Override
	public Vector3 floor() {
		set(super.floor());
		return this;
	}

	/**
	 * Rounds the X, Y, and Z values of this Vector3 to the nearest integer
	 * value.
	 *
	 * @return
	 */
	@Override
	public Vector3 round() {
		set(super.round());
		return this;
	}

	/**
	 * Sets the X, Y, and Z values of this Vector3 to their absolute value.
	 *
	 * @return
	 */
	@Override
	public Vector3 abs() {
		set(super.abs());
		return this;
	}

	/**
	 * returns the vector with a length of 1
	 *
	 * @return
	 */
	@Override
	public Vector3 normalize() {
		set(super.normalize());
		return this;
	}

	@Override
	public Vector3m clone() {
		return new Vector3m(x, y, z);
	}
}
