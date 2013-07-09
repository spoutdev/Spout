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

	public static void assertEquals(float[] a, float... f) {
		Assert.assertArrayEquals(f, a, EPSILON);
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

	public static void assertEquals(Matrix2 m, float... f) {
		Assert.assertArrayEquals(f, m.toArray(), EPSILON);
	}

	public static void assertEquals(Matrix3 m, float... f) {
		Assert.assertArrayEquals(f, m.toArray(), EPSILON);
	}

	public static void assertEquals(Matrix4 m, float... f) {
		Assert.assertArrayEquals(f, m.toArray(), EPSILON);
	}

	public static void assertEquals(MatrixN m, float... f) {
		Assert.assertArrayEquals(f, m.toArray(), EPSILON);
	}
}
