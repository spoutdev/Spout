/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.math.vector;

import java.io.Serializable;
import java.util.Random;

import org.spout.math.GenericMath;
import org.spout.math.TrigMath;

public class Vector3 implements Vector, Comparable<Vector3>, Serializable, Cloneable {
	private static final long serialVersionUID = 1;
	public static final Vector3 ZERO = new Vector3(0, 0, 0);
	public static final Vector3 UNIT_X = new Vector3(1, 0, 0);
	public static final Vector3 UNIT_Y = new Vector3(0, 1, 0);
	public static final Vector3 UNIT_Z = new Vector3(0, 0, 1);
	public static final Vector3 ONE = new Vector3(1, 1, 1);
	public static final Vector3 RIGHT = UNIT_X;
	public static final Vector3 UP = UNIT_Y;
	public static final Vector3 FORWARD = UNIT_Z;
	private final float x;
	private final float y;
	private final float z;
	private transient volatile boolean hashed = false;
	private transient volatile int hashCode = 0;

	public Vector3(Vector2 v) {
		this(v, 0);
	}

	// TODO: float overload

	public Vector3(Vector2 v, float z) {
		this(v.getX(), v.getY(), z);
	}

	public Vector3(Vector3 v) {
		this(v.x, v.y, v.z);
	}

	public Vector3(Vector4 v) {
		this(v.getX(), v.getY(), v.getZ());
	}

	public Vector3(VectorN v) {
		this(v.get(0), v.get(1), v.size() > 2 ? v.get(2) : 0);
	}

	public Vector3(double x, double y, double z) {
		this((float) x, (float) y, (float) z);
	}

	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public int getFloorX() {
		return GenericMath.floor(x);
	}

	public int getFloorY() {
		return GenericMath.floor(y);
	}

	public int getFloorZ() {
		return GenericMath.floor(z);
	}

	public Vector3 add(Vector3 v) {
		return add(v.x, v.y, v.z);
	}

	public Vector3 add(double x, double y, double z) {
		return add((float) x, (float) y, (float) z);
	}

	public Vector3 add(float x, float y, float z) {
		return new Vector3(this.x + x, this.y + y, this.z + z);
	}

	public Vector3 sub(Vector3 v) {
		return sub(v.x, v.y, v.z);
	}

	public Vector3 sub(double x, double y, double z) {
		return sub((float) x, (float) y, (float) z);
	}

	public Vector3 sub(float x, float y, float z) {
		return new Vector3(this.x - x, this.y - y, this.z - z);
	}

	public Vector3 mul(double a) {
		return mul((float) a);
	}

	@Override
	public Vector3 mul(float a) {
		return mul(a, a, a);
	}

	public Vector3 mul(Vector3 v) {
		return mul(v.x, v.y, v.z);
	}

	public Vector3 mul(double x, double y, double z) {
		return mul((float) x, (float) y, (float) z);
	}

	public Vector3 mul(float x, float y, float z) {
		return new Vector3(this.x * x, this.y * y, this.z * z);
	}

	public Vector3 div(double a) {
		return div((float) a);
	}

	@Override
	public Vector3 div(float a) {
		return div(a, a, a);
	}

	public Vector3 div(Vector3 v) {
		return div(v.x, v.y, v.z);
	}

	public Vector3 div(double x, double y, double z) {
		return div((float) x, (float) y, (float) z);
	}

	public Vector3 div(float x, float y, float z) {
		return new Vector3(this.x / x, this.y / y, this.z / z);
	}

	public float dot(Vector3 v) {
		return dot(v.x, v.y, v.z);
	}

	public float dot(double x, double y, double z) {
		return dot((float) x, (float) y, (float) z);
	}

	public float dot(float x, float y, float z) {
		return this.x * x + this.y * y + this.z * z;
	}

	public Vector3 cross(Vector3 v) {
		return cross(v.x, v.y, v.z);
	}

	public Vector3 cross(double x, double y, double z) {
		return cross((float) x, (float) y, (float) z);
	}

	public Vector3 cross(float x, float y, float z) {
		return new Vector3(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
	}

	public Vector3 pow(double pow) {
		return pow((float) pow);
	}

	@Override
	public Vector3 pow(float power) {
		return new Vector3(Math.pow(x, power), Math.pow(y, power), Math.pow(z, power));
	}

	@Override
	public Vector3 ceil() {
		return new Vector3(Math.ceil(x), Math.ceil(y), Math.ceil(z));
	}

	@Override
	public Vector3 floor() {
		return new Vector3(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
	}

	@Override
	public Vector3 round() {
		return new Vector3(Math.round(x), Math.round(y), Math.round(z));
	}

	@Override
	public Vector3 abs() {
		return new Vector3(Math.abs(x), Math.abs(y), Math.abs(z));
	}

	@Override
	public Vector3 negate() {
		return new Vector3(-x, -y, -z);
	}

	public Vector3 min(Vector3 v) {
		return min(v.x, v.y, v.z);
	}

	public Vector3 min(double x, double y, double z) {
		return min((float) x, (float) y, (float) z);
	}

	public Vector3 min(float x, float y, float z) {
		return new Vector3(Math.min(this.x, x), Math.min(this.y, y), Math.min(this.z, z));
	}

	public Vector3 max(Vector3 v) {
		return max(v.x, v.y, v.z);
	}

	public Vector3 max(double x, double y, double z) {
		return max((float) x, (float) y, (float) z);
	}

	public Vector3 max(float x, float y, float z) {
		return new Vector3(Math.max(this.x, x), Math.max(this.y, y), Math.max(this.z, z));
	}

	public float distanceSquared(Vector3 v) {
		return distanceSquared(v.x, v.y, v.z);
	}

	public float distanceSquared(double x, double y, double z) {
		return distanceSquared((float) x, (float) y, (float) z);
	}

	public float distanceSquared(float x, float y, float z) {
		return (float) GenericMath.lengthSquared(this.x - x, this.y - y, this.z - z);
	}

	public float distance(Vector3 v) {
		return distance(v.x, v.y, v.z);
	}

	public float distance(double x, double y, double z) {
		return distance((float) x, (float) y, (float) z);
	}

	public float distance(float x, float y, float z) {
		return (float) GenericMath.length(this.x - x, this.y - y, this.z - z);
	}

	@Override
	public float lengthSquared() {
		return (float) GenericMath.lengthSquared(x, y, z);
	}

	@Override
	public float length() {
		return (float) GenericMath.length(x, y, z);
	}

	@Override
	public Vector3 normalize() {
		final float length = length();
		return new Vector3(x / length, y / length, z / length);
	}

	public Vector2 toVector2() {
		return new Vector2(this);
	}

	public Vector2 toVector2(boolean useZ) {
		return new Vector2(x, useZ ? z : y);
	}

	public Vector4 toVector4() {
		return toVector4(0);
	}

	public Vector4 toVector4(double w) {
		return toVector4((float) w);
	}

	public Vector4 toVector4(float w) {
		return new Vector4(this, w);
	}

	public VectorN toVectorN() {
		return new VectorN(this);
	}

	@Override
	public float[] toArray() {
		return new float[] {x, y, z};
	}

	@Override
	public int compareTo(Vector3 v) {
		return (int) (lengthSquared() - v.lengthSquared());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Vector3)) {
			return false;
		}
		final Vector3 vector3 = (Vector3) o;
		if (Float.compare(vector3.x, x) != 0) {
			return false;
		}
		if (Float.compare(vector3.y, y) != 0) {
			return false;
		}
		if (Float.compare(vector3.z, z) != 0) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		if (!hashed) {
			int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
			result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
			hashCode = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
			hashed = true;
		}
		return hashCode;
	}

	@Override
	public Vector3 clone() {
		return new Vector3(this);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	/**
	 * Gets the direction vector of a random pitch and yaw using the random specified.
	 *
	 * @param random to use
	 * @return the random direction vector
	 */
	public static Vector3 createRandomDirection(Random random) {
		return createDirection(random.nextFloat() * (float) TrigMath.TWO_PI,
				random.nextFloat() * (float) TrigMath.TWO_PI);
	}

	// TODO: add overloads for doubles and degree angles

	/**
	 * Gets the direction vector of a certain yaw and pitch.
	 *
	 * @param azimuth in radians
	 * @param inclination in radians
	 * @return the random direction vector
	 */
	public static Vector3 createDirection(float azimuth, float inclination) {
		final float yFact = TrigMath.cos(inclination);
		return new Vector3(yFact * TrigMath.cos(azimuth), TrigMath.sin(inclination), yFact * TrigMath.sin(azimuth));
	}
}
