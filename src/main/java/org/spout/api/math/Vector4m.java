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

public class Vector4m extends Vector4 {
	public Vector4m() {
	}

	public Vector4m(Vector4 o) {
		super(o);
	}

	public Vector4m(int x, int y, int z, int w) {
		super(x, y, z, w);
	}

	public Vector4m(double x, double y, double z, double w) {
		super(x, y, z, w);
	}

	public Vector4m(float x, float y, float z, float w) {
		super(x, y, z, w);
	}

	public void set(Vector4 vector) {
		this.x = vector.getX();
		this.y = vector.getY();
		this.z = vector.getZ();
		this.w = vector.getW();
	}

	/**
	 * Sets the X coordinate
	 *
	 * @param x The x coordinate
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Sets the Y coordinate
	 *
	 * @param y The Y coordinate
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Sets the Z coordinate
	 *
	 * @param z The z coordinate
	 */
	public void setZ(float z) {
		this.z = z;
	}

	/**
	 * Sets the W coordinate
	 *
	 * @param w The W coordinate
	 */
	public void setW(float w) {
		this.w = w;
	}

	@Override
	public Vector4 add(Vector4 that) {
		set(super.add(that));
		return this;
	}

	@Override
	public Vector4 subtract(Vector4 that) {
		set(super.subtract(that));
		return this;
	}

	/**
	 * Rounds the values of this Vector4 up to
	 * the nearest integer value.
	 *
	 * @return
	 */
	@Override
	public Vector4 ceil() {
		set(super.ceil());
		return this;
	}

	/**
	 * Rounds the values of this Vector4 down to
	 * the nearest integer value.
	 *
	 * @return
	 */
	@Override
	public Vector4 floor() {
		set(super.floor());
		return this;
	}

	/**
	 * Rounds the values of this Vector4 to
	 * the nearest integer value.
	 *
	 * @return
	 */
	@Override
	public Vector4 round() {
		set(super.round());
		return this;
	}

	/**
	 * Sets the values of this Vector4 to their
	 * absolute value.
	 *
	 * @return
	 */
	@Override
	public Vector4 abs() {
		set(super.abs());
		return this;
	}

	/**
	 * Divides the given Vector4 from this Vector4
	 *
	 * @param that The Vector4 to divide
	 * @return the new Vector4
	 */
	@Override
	public Vector4 divide(Vector4 that) {
		set(super.divide(that));
		return this;
	}

	/**
	 * Multiplies this Vector4 to the value of the Vector4 argument
	 *
	 * @param that The Vector4 to multiply
	 * @return the new Vector4
	 */
	@Override
	public Vector4 multiply(Vector4 that) {
		set(super.multiply(that));
		return this;
	}

	/**
	 * Raises the values of this Vector4 to the given power.
	 *
	 * @param power
	 * @return
	 */
	@Override
	public Vector4 pow(double power) {
		set(super.pow(power));
		return this;
	}

	/**
	 * Returns this Vector4 where the length is equal to 1
	 *
	 * @return This Vector4 with length 1
	 */
	@Override
	public Vector4 normalize() {
		set(super.normalize());
		return this;
	}
}
