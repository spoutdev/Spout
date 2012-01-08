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
		x += that.x;
		y += that.y;
		return this;
	}

	@Override
	public Vector2 subtract(Vector2 that) {
		x -= that.x;
		y -= that.y;
		return this;
	}

	@Deprecated
	@Override
	public Vector2 scale(float scale) {
		x *= scale;
		y *= scale;
		return this;
	}

	@Override
	public Vector2 cross() {
		float tmp = y;
		y = -x;
		x = tmp;
		return this;
	}
	
	/**
	 * Rounds the X and Y values of this Vector2 up to 
	 * the nearest integer value. 
	 * 
	 * @return 
	 */
	public Vector2 ceil() {
		x = (float) Math.ceil(x);
		y = (float) Math.ceil(y);
		return this;
	}
	
	/**
	 * Rounds the X and Y values of this Vector2 down to 
	 * the nearest integer value. 
	 * 
	 * @return 
	 */
	public Vector2 floor() {
		x = (float) Math.floor(x);
		y = (float) Math.floor(y);
		return this;
	}
	
	/**
	 * Rounds the X and Y values of this Vector2 to 
	 * the nearest integer value. 
	 * 
	 * @return 
	 */
	public Vector2 round() {
		x = Math.round(x);
		y = Math.round(y);
		return this;
	}
	
	/**
	 * Sets the X and Y values of this Vector2 to their
	 * absolute value.
	 * 
	 * @return 
	 */
	public Vector2 abs() {
		x = Math.abs(x);
		y = Math.abs(y);
		return this;
	}

	@Override
	public Vector2 normalize() {
		float length = this.length();
		x *= 1 / length;
		y *= 1 / length;
		return this;
	}
}
