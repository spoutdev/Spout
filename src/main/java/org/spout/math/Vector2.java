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
package org.spout.math;

import java.io.Serializable;
import java.util.Random;

public class Vector2 implements Comparable<Vector2>, Serializable, Cloneable {
	private static final long serialVersionUID = 1;
	public static final Vector2 ZERO = new Vector2(0, 0);
	public static final Vector2 ONE = new Vector2(1, 1);
	public static final Vector2 UNIT_X = new Vector2(1, 0);
	public static final Vector2 UNIT_Y = new Vector2(0, 1);
	private final float x;
	private final float y;
	private transient volatile boolean hashed = false;
	private transient volatile int hashCode = 0;

	public Vector2() {
		this(0, 0);
	}

	public Vector2(Vector2 v) {
		this(v.x, v.y);
	}

	public Vector2(Vector3 v) {
		this(v.getX(), v.getY());
	}

	public Vector2(Vector4 v) {
		this(v.getX(), v.getY());
	}

	public Vector2(double x, double y) {
		this((float) x, (float) y);
	}

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public int getFloorX() {
		return GenericMath.floor(x);
	}

	public int getFloorY() {
		return GenericMath.floor(y);
	}

	public Vector2 add(Vector2 v) {
		return add(v.x, v.y);
	}

	public Vector2 add(double x, double y) {
		return add((float) x, (float) y);
	}

	public Vector2 add(float x, float y) {
		return new Vector2(this.x + x, this.y + y);
	}

	public Vector2 sub(Vector2 v) {
		return sub(v.x, v.y);
	}

	public Vector2 sub(double x, double y) {
		return sub((float) x, (float) y);
	}

	public Vector2 sub(float x, float y) {
		return new Vector2(this.x - x, this.y - y);
	}

	public Vector2 mul(double a) {
		return mul((float) a);
	}

	public Vector2 mul(float a) {
		return mul(a, a);
	}

	public Vector2 mul(Vector2 v) {
		return mul(v.x, v.y);
	}

	public Vector2 mul(double x, double y) {
		return mul((float) x, (float) y);
	}

	public Vector2 mul(float x, float y) {
		return new Vector2(this.x * x, this.y * y);
	}

	public Vector2 div(Vector2 v) {
		return div(v.x, v.y);
	}

	public Vector2 div(double x, double y) {
		return div((float) x, (float) y);
	}

	public Vector2 div(float x, float y) {
		return new Vector2(this.x / x, this.y / y);
	}

	public float dot(Vector2 v) {
		return dot(v.x, v.y);
	}

	public float dot(double x, double y) {
		return dot((float) x, (float) y);
	}

	public float dot(float x, float y) {
		return this.x * x + this.y * y;
	}

	public Vector2 pow(double power) {
		return pow((float) power);
	}

	public Vector2 pow(float power) {
		return new Vector2(Math.pow(x, power), Math.pow(y, power));
	}

	public Vector2 ceil() {
		return new Vector2(Math.ceil(x), Math.ceil(y));
	}

	public Vector2 floor() {
		return new Vector2(GenericMath.floor(x), GenericMath.floor(y));
	}

	public Vector2 round() {
		return new Vector2(Math.round(x), Math.round(y));
	}

	public Vector2 abs() {
		return new Vector2(Math.abs(x), Math.abs(y));
	}

	public Vector2 negate() {
		return new Vector2(-x, -y);
	}

	public Vector2 min(Vector2 v) {
		return min(v.x, v.y);
	}

	public Vector2 min(double x, double y) {
		return min((float) x, (float) y);
	}

	public Vector2 min(float x, float y) {
		return new Vector2(Math.min(this.x, x), Math.min(this.y, y));
	}

	public Vector2 max(Vector2 v) {
		return max(v.x, v.y);
	}

	public Vector2 max(double x, double y) {
		return max((float) x, (float) y);
	}

	public Vector2 max(float x, float y) {
		return new Vector2(Math.max(this.x, x), Math.max(this.y, y));
	}

	public float distanceSquared(Vector2 v) {
		return distanceSquared(v.x, v.y);
	}

	public float distanceSquared(double x, double y) {
		return distanceSquared((float) x, (float) y);
	}

	public float distanceSquared(float x, float y) {
		return GenericMath.lengthSquaredF(this.x - x, this.y - y);
	}

	public float distance(Vector2 v) {
		return distance(v.x, v.y);
	}

	public float distance(double x, double y) {
		return distance((float) x, (float) y);
	}

	public float distance(float x, float y) {
		return GenericMath.lengthF(this.x - x, this.y - y);
	}

	public float lengthSquared() {
		return GenericMath.lengthSquaredF(x, y);
	}

	public float length() {
		return GenericMath.lengthF(x, y);
	}

	public Vector2 normalize() {
		final float length = length();
		return new Vector2(x / length, y / length);
	}

	public Vector3 toVector3() {
		return new Vector3(x, y, 0);
	}

	public Vector4 toVector4() {
		return new Vector4(x, y, 0, 0);
	}

	public Vector toVector() {
		return new Vector(x, y);
	}

	public float[] toArray() {
		return new float[]{x, y};
	}

	public Matrix toScalingMatrix(int size) {
		return Matrix.createScaling(size, this);
	}

	public Matrix toTranslationMatrix(int size) {
		return Matrix.createTranslation(size, this);
	}

	@Override
	public int compareTo(Vector2 v) {
		return (int) (lengthSquared() - v.lengthSquared());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Vector2)) {
			return false;
		}
		final Vector2 other = (Vector2) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) {
			return false;
		}
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		if (!hashed) {
			int hash = 5;
			hash = 29 * hash + Float.floatToIntBits(x);
			hash = 29 * hash + Float.floatToIntBits(y);
			hashCode = hash;
			hashed = true;
		}
		return hashCode;
	}

	@Override
	public Vector2 clone() {
		return new Vector2(this);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	/**
	 * Gets the direction vector of a random angle using the random specified.
	 *
	 * @param random to use
	 * @return the random direction vector
	 */
	public static Vector2 getRandomDirection(Random random) {
		return getDirection(random.nextFloat() * (float) TrigMath.TWO_PI);
	}

	/**
	 * Gets the direction vector of a certain angle.
	 *
	 * @param azimuth in radians
	 * @return the direction vector
	 */
	public static Vector2 getDirection(float azimuth) {
		return new Vector2(TrigMath.cos(azimuth), TrigMath.sin(azimuth));
	}
}
