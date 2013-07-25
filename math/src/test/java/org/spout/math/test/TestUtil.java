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
package org.spout.math.test;

import org.junit.Assert;

import org.spout.math.imaginary.Complex;
import org.spout.math.imaginary.Quaternion;
import org.spout.math.matrix.Matrix2;
import org.spout.math.matrix.Matrix3;
import org.spout.math.matrix.Matrix4;
import org.spout.math.matrix.MatrixN;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;
import org.spout.math.vector.Vector4;
import org.spout.math.vector.VectorN;

public class TestUtil {
	private static final float EPSILON = 0.00001f;

	public static void assertEquals(float value, float expected) {
		Assert.assertEquals(expected, value, EPSILON);
	}

	public static void assertEquals(Vector2 v, float x, float y) {
		assertEquals(v.getX(), x);
		assertEquals(v.getY(), y);
	}

	public static void assertEquals(Vector3 v, float x, float y, float z) {
		assertEquals(v.getX(), x);
		assertEquals(v.getY(), y);
		assertEquals(v.getZ(), z);
	}

	public static void assertEquals(Vector4 v, float x, float y, float z, float w) {
		assertEquals(v.getX(), x);
		assertEquals(v.getY(), y);
		assertEquals(v.getZ(), z);
		assertEquals(v.getW(), w);
	}

	public static void assertEquals(VectorN v, float... f) {
		Assert.assertArrayEquals(f, v.toArray(), EPSILON);
	}

	public static void assertEquals(Complex c, float x, float y) {
		assertEquals(c.getX(), x);
		assertEquals(c.getY(), y);
	}

	public static void assertEquals(Quaternion q, float x, float y, float z, float w) {
		assertEquals(q.getX(), x);
		assertEquals(q.getY(), y);
		assertEquals(q.getZ(), z);
		assertEquals(q.getW(), w);
	}

	public static void assertEquals(Matrix2 m, float m00, float m01, float m10, float m11) {
		assertEquals(m.get(0, 0), m00);
		assertEquals(m.get(0, 1), m01);
		assertEquals(m.get(1, 0), m10);
		assertEquals(m.get(1, 1), m11);
	}

	public static void assertEquals(Matrix3 m, float m00, float m01, float m02, float m10, float m11,
									float m12, float m20, float m21, float m22) {
		assertEquals(m.get(0, 0), m00);
		assertEquals(m.get(0, 1), m01);
		assertEquals(m.get(0, 2), m02);
		assertEquals(m.get(1, 0), m10);
		assertEquals(m.get(1, 1), m11);
		assertEquals(m.get(1, 2), m12);
		assertEquals(m.get(2, 0), m20);
		assertEquals(m.get(2, 1), m21);
		assertEquals(m.get(2, 2), m22);
	}

	public static void assertEquals(Matrix4 m, float m00, float m01, float m02, float m03, float m10,
									float m11, float m12, float m13, float m20, float m21, float m22,
									float m23, float m30, float m31, float m32, float m33) {
		assertEquals(m.get(0, 0), m00);
		assertEquals(m.get(0, 1), m01);
		assertEquals(m.get(0, 2), m02);
		assertEquals(m.get(0, 3), m03);
		assertEquals(m.get(1, 0), m10);
		assertEquals(m.get(1, 1), m11);
		assertEquals(m.get(1, 2), m12);
		assertEquals(m.get(1, 3), m13);
		assertEquals(m.get(2, 0), m20);
		assertEquals(m.get(2, 1), m21);
		assertEquals(m.get(2, 2), m22);
		assertEquals(m.get(2, 3), m23);
		assertEquals(m.get(3, 0), m30);
		assertEquals(m.get(3, 1), m31);
		assertEquals(m.get(3, 2), m32);
		assertEquals(m.get(3, 3), m33);
	}

	public static void assertEquals(MatrixN m, float... f) {
		Assert.assertArrayEquals(f, m.toArray(), EPSILON);
	}

	public static void assertEquals(float[] a, float... f) {
		Assert.assertArrayEquals(f, a, EPSILON);
	}
}
