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
package org.spout.math.vector;

import java.io.Serializable;

import org.spout.math.GenericMath;

public class Vector4 implements Vector, Comparable<Vector4>, Serializable, Cloneable {
	private static final long serialVersionUID = 1;
	public static final Vector4 ZERO = new Vector4(0, 0, 0, 0);
	public static final Vector4 UNIT_X = new Vector4(1, 0, 0, 0);
	public static final Vector4 UNIT_Y = new Vector4(0, 1, 0, 0);
	public static final Vector4 UNIT_Z = new Vector4(0, 0, 1, 0);
	public static final Vector4 UNIT_W = new Vector4(0, 0, 0, 1);
	private final float x;
	private final float y;
	private final float z;
	private final float w;
	private transient volatile boolean hashed = false;
	private transient volatile int hashCode = 0;

	public Vector4() {
		this(0, 0, 0, 0);
	}

	public Vector4(Vector2 v) {
		this(v, 0, 0);
	}

	public Vector4(Vector2 v, float z, float w) {
		this(v.getX(), v.getY(), z, w);
	}

	public Vector4(Vector3 v) {
		this(v, 0);
	}

	public Vector4(Vector3 v, float w) {
		this(v.getX(), v.getY(), v.getZ(), w);
	}

	public Vector4(Vector4 v) {
		this(v.x, v.y, v.z, v.w);
	}

	public Vector4(VectorN v) {
		this(v.get(0), v.get(1), v.size() > 2 ? v.get(2) : 0, v.size() > 3 ? v.get(3) : 0);
	}

	public Vector4(double x, double y, double z, double w) {
		this((float) x, (float) y, (float) z, (float) w);
	}

	public Vector4(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
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

	public float getW() {
		return w;
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

	public int getFloorW() {
		return GenericMath.floor(w);
	}

	public Vector4 add(Vector4 v) {
		return add(v.x, v.y, v.z, v.w);
	}

	public Vector4 add(double x, double y, double z, double w) {
		return add((float) x, (float) y, (float) z, (float) w);
	}

	public Vector4 add(float x, float y, float z, float w) {
		return new Vector4(this.x + x, this.y + y, this.z + z, this.w + w);
	}

	public Vector4 sub(Vector4 v) {
		return sub(v.x, v.y, v.z, v.w);
	}

	public Vector4 sub(double x, double y, double z, double w) {
		return sub((float) x, (float) y, (float) z, (float) w);
	}

	public Vector4 sub(float x, float y, float z, float w) {
		return new Vector4(this.x - x, this.y - y, this.z - z, this.w - w);
	}

	public Vector4 mul(double a) {
		return mul((float) a);
	}

	@Override
	public Vector4 mul(float a) {
		return mul(a, a, a, a);
	}

	public Vector4 mul(Vector4 v) {
		return mul(v.x, v.y, v.z, v.w);
	}

	public Vector4 mul(double x, double y, double z, double w) {
		return mul((float) x, (float) y, (float) z, (float) w);
	}

	public Vector4 mul(float x, float y, float z, float w) {
		return new Vector4(this.x * x, this.y * y, this.z * z, this.w * w);
	}

	public Vector4 div(double a) {
		return div((float) a);
	}

	@Override
	public Vector4 div(float a) {
		return div(a, a, a, a);
	}

	public Vector4 div(Vector4 v) {
		return div(v.x, v.y, v.z, v.w);
	}

	public Vector4 div(double x, double y, double z, double w) {
		return div((float) x, (float) y, (float) z, (float) w);
	}

	public Vector4 div(float x, float y, float z, float w) {
		return new Vector4(this.x / x, this.y / y, this.z / z, this.w / w);
	}

	public float dot(Vector4 v) {
		return dot(v.x, v.y, v.z, v.w);
	}

	public float dot(double x, double y, double z, double w) {
		return dot((float) x, (float) y, (float) z, (float) w);
	}

	public float dot(float x, float y, float z, float w) {
		return this.x * x + this.y * y + this.z * z + this.w * w;
	}

	public Vector4 pow(double pow) {
		return pow((float) pow);
	}

	@Override
	public Vector4 pow(float power) {
		return new Vector4(Math.pow(x, power), Math.pow(y, power), Math.pow(z, power), Math.pow(w, power));
	}

	@Override
	public Vector4 ceil() {
		return new Vector4(Math.ceil(x), Math.ceil(y), Math.ceil(z), Math.ceil(w));
	}

	@Override
	public Vector4 floor() {
		return new Vector4(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z), GenericMath.floor(w));
	}

	@Override
	public Vector4 round() {
		return new Vector4(Math.round(x), Math.round(y), Math.round(z), Math.round(w));
	}

	@Override
	public Vector4 abs() {
		return new Vector4(Math.abs(x), Math.abs(y), Math.abs(z), Math.abs(w));
	}

	@Override
	public Vector4 negate() {
		return new Vector4(-x, -y, -z, -w);
	}

	public Vector4 min(Vector4 v) {
		return min(v.x, v.y, v.z, v.w);
	}

	public Vector4 min(double x, double y, double z, double w) {
		return min((float) x, (float) y, (float) z, (float) w);
	}

	public Vector4 min(float x, float y, float z, float w) {
		return new Vector4(Math.min(this.x, x), Math.min(this.y, y), Math.min(this.z, z), Math.min(this.w, w));
	}

	public Vector4 max(Vector4 v) {
		return max(v.x, v.y, v.z, v.w);
	}

	public Vector4 max(double x, double y, double z, double w) {
		return max((float) x, (float) y, (float) z, (float) w);
	}

	public Vector4 max(float x, float y, float z, float w) {
		return new Vector4(Math.max(this.x, x), Math.max(this.y, y), Math.max(this.z, z), Math.max(this.w, w));
	}

	public float distanceSquared(Vector4 v) {
		return distanceSquared(v.x, v.y, v.z, v.w);
	}

	public float distanceSquared(double x, double y, double z, double w) {
		return distanceSquared((float) x, (float) y, (float) z, (float) w);
	}

	public float distanceSquared(float x, float y, float z, float w) {
		return (float) GenericMath.lengthSquared(this.x - x, this.y - y, this.z - z, this.w - w);
	}

	public float distance(Vector4 v) {
		return distance(v.x, v.y, v.z, v.w);
	}

	public float distance(double x, double y, double z, double w) {
		return distance((float) x, (float) y, (float) z, (float) w);
	}

	public float distance(float x, float y, float z, float w) {
		return (float) GenericMath.length(this.x - x, this.y - y, this.z - z, this.w - w);
	}

	@Override
	public float lengthSquared() {
		return (float) GenericMath.lengthSquared(x, y, z, w);
	}

	@Override
	public float length() {
		return (float) GenericMath.length(x, y, z, w);
	}

	@Override
	public Vector4 normalize() {
		final float length = length();
		return new Vector4(x / length, y / length, z / length, w / length);
	}

	public Vector2 toVector2() {
		return new Vector2(this);
	}

	public Vector3 toVector3() {
		return new Vector3(this);
	}

	public VectorN toVectorN() {
		return new VectorN(this);
	}

	@Override
	public float[] toArray() {
		return new float[]{x, y, z, w};
	}

	@Override
	public int compareTo(Vector4 v) {
		return (int) (lengthSquared() - v.lengthSquared());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Vector4)) {
			return false;
		}
		final Vector4 vector4 = (Vector4) o;
		if (Float.compare(vector4.w, w) != 0) {
			return false;
		}
		if (Float.compare(vector4.x, x) != 0) {
			return false;
		}
		if (Float.compare(vector4.y, y) != 0) {
			return false;
		}
		if (Float.compare(vector4.z, z) != 0) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		if (!hashed) {
			int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
			result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
			result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
			hashCode = 31 * result + (w != +0.0f ? Float.floatToIntBits(w) : 0);
			hashed = true;
		}
		return hashCode;
	}

	@Override
	public Vector4 clone() {
		return new Vector4(this);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + w + ")";
	}
}
