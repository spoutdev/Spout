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

public class VectorN implements Vector, Comparable<VectorN>, Serializable, Cloneable {
	public static VectorN ZERO_2 = new ImmutableZeroVectorN(0, 0);
	public static VectorN ZERO_3 = new ImmutableZeroVectorN(0, 0, 0);
	public static VectorN ZERO_4 = new ImmutableZeroVectorN(0, 0, 0, 0);
	private static final long serialVersionUID = 1;
	private final float[] vec;

	public VectorN(int size) {
		if (size < 2) {
			throw new IllegalArgumentException("Minimum vector size is 2");
		}
		vec = new float[size];
	}

	public VectorN(Vector2 v) {
		this(v.getX(), v.getY());
	}

	public VectorN(Vector3 v) {
		this(v.getX(), v.getY(), v.getZ());
	}

	public VectorN(Vector4 v) {
		this(v.getX(), v.getY(), v.getZ(), v.getW());
	}

	public VectorN(VectorN v) {
		this(v.vec);
	}

	public VectorN(double... v) {
		vec = new float[v.length];
		
		for(int i = 0; i < v.length; i++){
			vec[i] = (float)v[i];
		}
	}

	public VectorN(float... v) {
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

	// TODO: add double overload

	public void set(int comp, float val) {
		vec[comp] = val;
	}

	public void setZero() {
		Arrays.fill(vec, 0);
	}

	public VectorN resize(int size) {
		final VectorN d = new VectorN(size);
		System.arraycopy(vec, 0, d.vec, 0, Math.min(size, size()));
		return d;
	}

	public VectorN add(VectorN v) {
		return add(v.vec);
	}

	// TODO: add double overload

	public VectorN add(float... v) {
		final int size = Math.min(size(), v.length);
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = vec[comp] + v[comp];
		}
		return d;
	}

	public VectorN sub(VectorN v) {
		return sub(v.vec);
	}

	// TODO: add double overload

	public VectorN sub(float... v) {
		final int size = Math.min(size(), v.length);
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = vec[comp] - v[comp];
		}
		return d;
	}

	public VectorN mul(double a) {
		return mul((float) a);
	}

	@Override
	public VectorN mul(float a) {
		final int size = size();
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = vec[comp] * a;
		}
		return d;
	}

	public VectorN mul(VectorN v) {
		return mul(v.vec);
	}

	// TODO: add double overload

	public VectorN mul(float... v) {
		final int size = Math.min(size(), v.length);
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = vec[comp] * v[comp];
		}
		return d;
	}

	public VectorN div(double a) {
		return div((float) a);
	}

	@Override
	public VectorN div(float a) {
		final int size = size();
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = vec[comp] / a;
		}
		return d;
	}

	public VectorN div(VectorN v) {
		return div(v.vec);
	}

	// TODO: add double overload

	public VectorN div(float... v) {
		final int size = Math.min(size(), v.length);
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = vec[comp] / v[comp];
		}
		return d;
	}

	public float dot(VectorN v) {
		return dot(v.vec);
	}

	// TODO: add double overload

	public float dot(float... v) {
		final int size = Math.min(size(), v.length);
		float d = 0;
		for (int comp = 0; comp < size; comp++) {
			d += vec[comp] * v[comp];
		}
		return d;
	}

	public VectorN pow(double pow) {
		return pow((float) pow);
	}

	@Override
	public VectorN pow(float power) {
		final int size = size();
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = (float) Math.pow(vec[comp], power);
		}
		return d;
	}

	@Override
	public VectorN ceil() {
		final int size = size();
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = (float) Math.ceil(vec[comp]);
		}
		return d;
	}

	@Override
	public VectorN floor() {
		final int size = size();
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = GenericMath.floor(vec[comp]);
		}
		return d;
	}

	@Override
	public VectorN round() {
		final int size = size();
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = Math.round(vec[comp]);
		}
		return d;
	}

	@Override
	public VectorN abs() {
		final int size = size();
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = Math.abs(vec[comp]);
		}
		return d;
	}

	@Override
	public VectorN negate() {
		final int size = size();
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = -vec[comp];
		}
		return d;
	}

	public VectorN min(VectorN v) {
		return min(v.vec);
	}

	// TODO: add double overload

	public VectorN min(float... v) {
		final int size = Math.min(size(), v.length);
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = Math.min(vec[comp], v[comp]);
		}
		return d;
	}

	public VectorN max(VectorN v) {
		return max(v.vec);
	}

	// TODO: add double overload

	public VectorN max(float... v) {
		final int size = Math.min(size(), v.length);
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = Math.max(vec[comp], v[comp]);
		}
		return d;
	}

	public float distanceSquared(VectorN v) {
		return distanceSquared(v.vec);
	}

	// TODO: add double overload

	public float distanceSquared(float... v) {
		final int size = Math.min(size(), v.length);
		final float[] d = new float[size];
		for (int comp = 0; comp < size; comp++) {
			d[comp] = vec[comp] - v[comp];
		}
		return lengthSquared(d);
	}

	public float distance(VectorN v) {
		return distanceSquared(v.vec);
	}

	// TODO: add double overload

	public float distance(float... v) {
		final int size = Math.min(size(), v.length);
		final float[] d = new float[size];
		for (int comp = 0; comp < size; comp++) {
			d[comp] = vec[comp] - v[comp];
		}
		return length(d);
	}

	@Override
	public float lengthSquared() {
		return lengthSquared(vec);
	}

	@Override
	public float length() {
		return length(vec);
	}

	@Override
	public VectorN normalize() {
		final int size = size();
		final float length = length();
		final VectorN d = new VectorN(size);
		for (int comp = 0; comp < size; comp++) {
			d.vec[comp] = vec[comp] / length;
		}
		return d;
	}

	public Vector2 toVector2() {
		return new Vector2(this);
	}

	public Vector3 toVector3() {
		return new Vector3(this);
	}

	public Vector4 toVector4() {
		return new Vector4(this);
	}

	@Override
	public float[] toArray() {
		return vec.clone();
	}

	@Override
	public int compareTo(VectorN v) {
		return (int) (lengthSquared() - v.lengthSquared());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof VectorN)) {
			return false;
		}
		return Arrays.equals(vec, ((VectorN) obj).vec);
	}

	@Override
	public int hashCode() {
		return 67 * 5 + Arrays.hashCode(vec);
	}

	@Override
	public VectorN clone() {
		return new VectorN(this);
	}

	@Override
	public String toString() {
		return Arrays.toString(vec).replace('[', '(').replace(']', ')');
	}

	private static float length(float... vec) {
		return (float) Math.sqrt(lengthSquared(vec));
	}

	private static float lengthSquared(float... vec) {
		float lengthSquared = 0;
		for (float comp : vec) {
			lengthSquared += comp * comp;
		}
		return lengthSquared;
	}

	private static class ImmutableZeroVectorN extends VectorN {
		public ImmutableZeroVectorN(float... v) {
			super(v);
		}

		@Override
		public void set(int comp, float val) {
			throw new UnsupportedOperationException("You may not alter this vector");
		}
	}
}
