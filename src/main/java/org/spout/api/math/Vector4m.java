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
		x += that.x;
		y += that.y;
		z += that.z;
		w += that.w;
		return this;
	}

	@Override
	public Vector4 subtract(Vector4 that) {
		x -= that.x;
		y -= that.y;
		z -= that.z;
		w -= that.w;
		return this;
	}

	/**
	 * Rounds the values of this Vector4 up to
	 * the nearest integer value.
	 *
	 * @return
	 */
	public Vector4 ceil() {
		x = (float) Math.ceil(x);
		y = (float) Math.ceil(y);
		z = (float) Math.ceil(z);
		w = (float) Math.ceil(w);
		return this;
	}

	/**
	 * Rounds the values of this Vector4 down to
	 * the nearest integer value.
	 *
	 * @return
	 */
	public Vector4 floor() {
		x = (float) Math.floor(x);
		y = (float) Math.floor(y);
		z = (float) Math.floor(z);
		w = (float) Math.floor(w);
		return this;
	}

	/**
	 * Rounds the values of this Vector4 to
	 * the nearest integer value.
	 *
	 * @return
	 */
	public Vector4 round() {
		x = Math.round(x);
		y = Math.round(y);
		z = Math.round(z);
		w = Math.round(w);
		return this;
	}

	/**
	 * Sets the values of this Vector4 to their
	 * absolute value.
	 *
	 * @return
	 */
	public Vector4 abs() {
		x = Math.abs(x);
		y = Math.abs(y);
		z = Math.abs(z);
		w = Math.abs(w);
		return this;
	}

	@Override
	public Vector4 normalize() {
		float length = this.length();
		x *= 1 / length;
		y *= 1 / length;
		z *= 1 / length;
		w *= 1 / length;
		return this;
	}
}
