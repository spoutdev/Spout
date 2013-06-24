/*
 * This file is part of Math.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Math is licensed under the Spout License Version 1.
 *
 * Math is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Math is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.math.imaginary;

import java.io.Serializable;

import org.spout.math.GenericMath;
import org.spout.math.matrix.Matrix;
import org.spout.math.TrigMath;
import org.spout.math.vector.Vector2;

public class Complex implements Comparable<Complex>, Serializable, Cloneable {
	private static final long serialVersionUID = 1;
	public static final Complex IDENTITY = new Complex();
	private final float x;
	private final float y;
	private transient volatile boolean hashed = false;
	private transient volatile int hashCode = 0;

	public Complex() {
		this(1, 0);
	}

	public Complex(double x, double y) {
		this((float) x, (float) y);
	}

	public Complex(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Complex(Complex c) {
		this.x = c.x;
		this.y = c.y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public Complex mul(float a) {
		return new Complex(x * a, y * a);
	}

	public Complex mul(Complex c) {
		return mul(c.x, c.y);
	}

	public Complex mul(double x, double y) {
		return mul((float) x, (float) y);
	}

	public Complex mul(float x, float y) {
		return new Complex(
				this.x * x - this.y * y,
				this.x * y + this.y * x);
	}

	public Complex div(float a) {
		return new Complex(x / a, y / a);
	}

	public Vector2 getDirection() {
		return new Vector2(x, y);
	}

	public float getAngleRad() {
		return (float) TrigMath.atan2(x, y);
	}

	public float getAngleDeg() {
		return (float) Math.toDegrees(getAngleRad());
	}

	public Complex conjugate() {
		return new Complex(x, -y);
	}

	public Complex invert() {
		return conjugate().div(lengthSquared());
	}

	public float lengthSquared() {
		return GenericMath.lengthSquaredF(x, y);
	}

	public float length() {
		return GenericMath.lengthF(x, y);
	}

	public Complex normalize() {
		final float length = length();
		return new Complex(x / length, y / length);
	}

	public Matrix toRotationMatrix(int size) {
		return Matrix.createRotation(size, this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Complex)) {
			return false;
		}
		final Complex complex = (Complex) o;
		if (Float.compare(complex.x, x) != 0) {
			return false;
		}
		if (Float.compare(complex.y, y) != 0) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		if (!hashed) {
			final int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
			hashCode = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
			hashed = true;
		}
		return hashCode;
	}

	@Override
	public int compareTo(Complex c) {
		return (int) (lengthSquared() - c.lengthSquared());
	}

	@Override
	public Complex clone() {
		return new Complex(this);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public static Complex fromRotationTo(Vector2 from, Vector2 to) {
		return fromAngleRad(TrigMath.acos(from.dot(to) / (from.length() * to.length())));
	}

	public static Complex fromAngleDeg(double angle) {
		return fromAngleRad(Math.toRadians(angle));
	}

	public static Complex fromAngleRad(double angle) {
		return fromAngleRad((float) angle);
	}

	public static Complex fromAngleDeg(float angle) {
		return fromAngleRad((float) Math.toRadians(angle));
	}

	public static Complex fromAngleRad(float angle) {
		return new Complex(TrigMath.cos(angle), TrigMath.sin(angle));
	}
}
