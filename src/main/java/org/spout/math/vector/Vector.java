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
import java.util.Arrays;

import org.spout.math.GenericMath;
import org.spout.math.matrix.Matrix;

public class Vector implements Comparable<Vector>, Serializable, Cloneable {
	private static final long serialVersionUID = 1;
	private final float[] vec;

	public Vector(int size) {
		if (size < 2) {
			throw new IllegalArgumentException("Minimum vector size is 2");
		}
		vec = new float[size];
	}

	public Vector(Vector2 v) {
		this(v.getX(), v.getY());
	}

	public Vector(Vector3 v) {
		this(v.getX(), v.getY(), v.getZ());
	}

	public Vector(Vector4 v) {
		this(v.getX(), v.getY(), v.getZ(), v.getW());
	}

	public Vector(Vector v) {
		this(v.vec);
	}

	public Vector(float... v) {
		vec = v.clone();
	}

	public int size() {
		return vec.length;
	}

	public float get(int comp) {
		return vec[comp];
	}

	public int getFloored(int comp) {
		return GenericMath.floor(get(comp));
	}

	public void set(int comp, float val) {
		vec[comp] = val;
	}

	public void setZero() {
		Arrays.fill(vec, 0);
	}

	public Vector resize(int size) {
		final Vector d = new Vector(size);
		System.arraycopy(vec, 0, d.vec, 0, Math.min(size, size()));
		return d;
	}

	public Vector add(Vector v) {
		return add(v.vec);
	}

	public Vector add(float... v) {
		final int size = Math.min(size(), v.length);
		final Vector d = new Vector(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = vec[comp] + v[comp];
		}
		return d;
	}

	public Vector sub(Vector v) {
		return sub(v.vec);
	}

	public Vector sub(float... v) {
		final int size = Math.min(size(), v.length);
		final Vector d = new Vector(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = vec[comp] - v[comp];
		}
		return d;
	}

	public Vector mul(double a) {
		return mul((float) a);
	}

	public Vector mul(float a) {
		final int size = size();
		final Vector d = new Vector(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = vec[comp] * a;
		}
		return d;
	}

	public Vector mul(Vector v) {
		return mul(v.vec);
	}

	public Vector mul(float... v) {
		final int size = Math.min(size(), v.length);
		final Vector d = new Vector(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = vec[comp] * v[comp];
		}
		return d;
	}

	public Vector div(Vector v) {
		return div(v.vec);
	}

	public Vector div(float... v) {
		final int size = Math.min(size(), v.length);
		final Vector d = new Vector(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = vec[comp] / v[comp];
		}
		return d;
	}

	public float dot(Vector v) {
		return dot(v.vec);
	}

	public float dot(float... v) {
		final int size = Math.min(size(), v.length);
		float d = 0;
		for (int comp = 0; comp < size; comp++) {
			d += vec[comp] * v[comp];
		}
		return d;
	}

	public Vector pow(double power) {
		return mul((float) power);
	}

	public Vector pow(float power) {
		final int size = size();
		final Vector d = new Vector(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = (float) Math.pow(vec[comp], power);
		}
		return d;
	}

	public Vector ceil() {
		final int size = size();
		final Vector d = new Vector(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = (float) Math.ceil(vec[comp]);
		}
		return d;
	}

	public Vector floor() {
		final int size = size();
		final Vector d = new Vector(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = GenericMath.floor(vec[comp]);
		}
		return d;
	}

	public Vector round() {
		final int size = size();
		final Vector d = new Vector(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = Math.round(vec[comp]);
		}
		return d;
	}

	public Vector abs() {
		final int size = size();
		final Vector d = new Vector(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = Math.abs(vec[comp]);
		}
		return d;
	}

	public Vector negate() {
		final int size = size();
		final Vector d = new Vector(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = -vec[comp];
		}
		return d;
	}

	public Vector min(Vector v) {
		return min(v.vec);
	}

	public Vector min(float... v) {
		final int size = Math.min(size(), v.length);
		final Vector d = new Vector(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = Math.min(vec[comp], v[comp]);
		}
		return d;
	}

	public Vector max(Vector v) {
		return max(v.vec);
	}

	public Vector max(float... v) {
		final int size = Math.min(size(), v.length);
		final Vector d = new Vector(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = Math.max(vec[comp], v[comp]);
		}
		return d;
	}

	public float distanceSquared(Vector v) {
		return distanceSquared(v.vec);
	}

	public float distanceSquared(float... v) {
		final int size = Math.min(size(), v.length);
		final float[] d = new float[size];
		for (int comp = 0; comp < size; comp++) {
			d[comp] = vec[comp] - v[comp];
		}
		return GenericMath.lengthSquaredF(d);
	}

	public float distance(Vector v) {
		return distanceSquared(v.vec);
	}

	public float distance(float... v) {
		final int size = Math.min(size(), v.length);
		final float[] d = new float[size];
		for (int comp = 0; comp < size; comp++) {
			d[comp] = vec[comp] - v[comp];
		}
		return GenericMath.lengthF(d);
	}

	public float lengthSquared() {
		return GenericMath.lengthSquaredF(vec);
	}

	public float length() {
		return GenericMath.lengthF(vec);
	}

	public Vector normalize() {
		final int size = size();
		final float length = length();
		final Vector d = new Vector(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = vec[comp] / length;
		}
		return d;
	}

	public Vector2 toVector2() {
		return new Vector2(vec[0], vec[1]);
	}

	public Vector3 toVector3() {
		return new Vector3(vec[0], vec[1], size() > 2 ? vec[2] : 0);
	}

	public Vector4 toVector4() {
		final int size = size();
		return new Vector4(vec[0], vec[1], size > 2 ? vec[2] : 0, size > 3 ? vec[3] : 0);
	}

	public Matrix toScalingMatrix(int size) {
		return Matrix.createScaling(size, this);
	}

	public Matrix toTranslationMatrix(int size) {
		return Matrix.createTranslation(size, this);
	}

	@Override
	public int compareTo(Vector v) {
		return (int) (lengthSquared() - v.lengthSquared());
	}

	@Override
	public Vector clone() {
		return new Vector(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Vector)) {
			return false;
		}
		return Arrays.equals(vec, ((Vector) obj).vec);
	}

	@Override
	public int hashCode() {
		return 67 * 5 + Arrays.hashCode(vec);
	}

	public float[] toArray() {
		return vec.clone();
	}

	@Override
	public String toString() {
		return Arrays.toString(vec).replace('[', '(').replace(']', ')');
	}
}
