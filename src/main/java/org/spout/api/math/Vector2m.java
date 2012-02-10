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

public class Vector2m extends Vector2 {
	public Vector2m() {
	}

	public Vector2m(Vector2 o) {
		super(o);
	}

	public Vector2m(int x, int y) {
		super(x, y);
	}

	public Vector2m(double x, double y) {
		super(x, y);
	}

	public Vector2m(float x, float y) {
		super(x, y);
	}

	public void set(Vector2 vector) {
		x = vector.getX();
		y = vector.getY();
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

	@Override
	public Vector2 add(Vector2 that) {
		set(super.add(that));
		return this;
	}

	@Override
	public Vector2 subtract(Vector2 that) {
		set(super.subtract(that));
		return this;
	}

	@Override
	public Vector2 cross() {
		set(super.cross());
		return this;
	}

	/**
	 * Rounds the X and Y values of this Vector2 up to the nearest integer
	 * value.
	 *
	 * @return
	 */
	@Override
	public Vector2 ceil() {
		set(super.ceil());
		return this;
	}

	/**
	 * Rounds the X and Y values of this Vector2 down to the nearest integer
	 * value.
	 *
	 * @return
	 */
	@Override
	public Vector2 floor() {
		set(super.floor());
		return this;
	}

	/**
	 * Rounds the X and Y values of this Vector2 to the nearest integer value.
	 *
	 * @return
	 */
	@Override
	public Vector2 round() {
		set(super.round());
		return this;
	}

	/**
	 * Sets the X and Y values of this Vector2 to their absolute value.
	 *
	 * @return
	 */
	@Override
	public Vector2 abs() {
		set(super.abs());
		return this;
	}

	/**
	 * Returns this Vector2 where the length is equal to 1
	 *
	 * @return This Vector2 with length 1
	 */
	@Override
	public Vector2 normalize() {
		set(super.normalize());
		return this;
	}

	/**
	 * Divides the given Vector2 from this Vector2
	 *
	 * @param that The Vector2 to divide
	 * @return the new Vector2
	 */
	@Override
	public Vector2 divide(Vector2 that) {
		set(super.divide(that));
		return this;
	}

	/**
	 * Multiplies this Vector2 to the value of the Vector2 argument
	 *
	 * @param that The Vector2 to multiply
	 * @return the new Vector2
	 */
	@Override
	public Vector2 multiply(Vector2 that) {
		set(super.multiply(that));
		return this;
	}

	/**
	 * Raises the X and Y values of this Vector2 to the given power
	 *
	 * @param power
	 * @return
	 */
	@Override
	public Vector2 pow(double power) {
		set(super.pow(power));
		return this;
	}
}
