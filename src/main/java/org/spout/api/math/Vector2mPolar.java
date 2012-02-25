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

public class Vector2mPolar extends Vector2Polar {
	public Vector2mPolar() {
	}

	public Vector2mPolar(Vector2Polar o) {
		super(o);
	}

	public Vector2mPolar(int r, int theta) {
		super(r, theta);
	}

	public Vector2mPolar(double r, double theta) {
		super(r, theta);
	}

	public Vector2mPolar(float r, float theta) {
		super(r, theta);
	}

	public void set(Vector2Polar vector) {
		r = vector.getR();
		theta = vector.getTheta();
	}

	/**
	 * Sets the length of the vector
	 *
	 * @param r
	 */
	public void setR(float r) {
		this.r = r;
	}

	/**
	 * Sets the angle of the vector
	 *
	 * @param theta
	 */
	public void setTheta(float theta) {
		this.theta = Vector2Polar.getRealAngle(theta);
	}

	@Override
	public Vector2Polar add(Vector2Polar that) {
		set(super.add(that));
		return this;
	}

	@Override
	public Vector2Polar ceil() {
		set(super.ceil());
		return this;
	}

	@Override
	public Vector2Polar cross() {
		set(super.cross());
		return this;
	}

	@Override
	public Vector2Polar divide(Vector2Polar that) {
		set(super.divide(that));
		return this;
	}

	@Override
	public Vector2Polar floor() {
		set(super.floor());
		return this;
	}

	@Override
	public Vector2Polar multiply(Vector2Polar that) {
		set(super.multiply(that));
		return this;
	}

	@Override
	public Vector2Polar round() {
		set(super.round());
		return this;
	}

	@Override
	public Vector2Polar subtract(Vector2Polar that) {
		set(super.subtract(that));
		return this;
	}

}
