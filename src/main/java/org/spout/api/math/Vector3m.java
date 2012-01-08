/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.math;

public class Vector3m extends Vector3 {

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

	/**
	 * Adds two vectors
	 *
	 * @param that
	 * @return
	 */

	public Vector3 add(Vector3 that) {
		x += that.x;
		y += that.y;
		z += that.z;
		return this;
	}

	/**
	 * Subtracts two vectors
	 *
	 * @param that
	 * @return
	 */

	public Vector3 subtract(Vector3 that) {
		x -= that.x;
		y -= that.y;
		z -= that.z;
		return this;
	}

	/**
	 * Scales by the scalar value
	 *
	 * @param scale
	 * @return
	 */

	public Vector3 multiply(float scale) {
		x *= scale;
		y *= scale;
		z *= scale;
		return this;
	}

	/**
	 * Takes the cross product of two vectors
	 *
	 * @param that
	 * @return
	 */

	public Vector3 cross(Vector3 that) {
		x = getY() * that.getZ() - getZ() * that.getY();
		y = getZ() * that.getX() - getX() * that.getZ();
		z = getX() * that.getY() - getY() * that.getX();

		return this;
	}
	
	/**
	 * Rounds the X, Y, and Z values of this Vector3 up to 
	 * the nearest integer value. 
	 * 
	 * @return 
	 */
	public Vector3 ceil() {
		x = (float) Math.ceil(x);
		y = (float) Math.ceil(y);
		z = (float) Math.ceil(z);
		return this;
	}
	
	/**
	 * Rounds the X, Y, and Z values of this Vector3 down to 
	 * the nearest integer value. 
	 * 
	 * @return 
	 */
	public Vector3 floor() {
		x = (float) Math.floor(x);
		y = (float) Math.floor(y);
		z = (float) Math.floor(z);
		return this;
	}
	
	/**
	 * Rounds the X, Y, and Z values of this Vector3 to 
	 * the nearest integer value. 
	 * 
	 * @return 
	 */
	public Vector3 round() {
		x = Math.round(x);
		y = Math.round(y);
		z = Math.round(z);
		return this;
	}
	
	/**
	 * Sets the X, Y, and Z values of this Vector3 to their
	 * absolute value.
	 * 
	 * @return 
	 */
	public Vector3 abs() {
		x = Math.abs(x);
		y = Math.abs(y);
		z = Math.abs(z);
		return this;
	}

	/**
	 * returns the vector with a length of 1
	 *
	 * @return
	 */

	public Vector3 normalize() {
		float length = this.length();
		x *= 1 / length;
		y *= 1 / length;
		z *= 1 / length;
		return this;
	}
}
