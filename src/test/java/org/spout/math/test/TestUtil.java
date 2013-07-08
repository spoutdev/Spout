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

import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;
import org.spout.math.vector.Vector4;
import org.spout.math.vector.VectorN;

public class TestUtil {
	private static final float DEFAULT_EPSILON_FLOAT = 0.001f;
	private static final double DEFAULT_EPSILON_DOUBLE = 0.001;

	public static void assertEquals(float value, float expected) {
		Assert.assertEquals(expected, value, DEFAULT_EPSILON_FLOAT);
	}

	public static void assertEquals(double value, double expected) {
		Assert.assertEquals(expected, value, DEFAULT_EPSILON_DOUBLE);
	}

	public static void assertEquals(Vector2 v, float x, float y) {
		assertEquals(v.getX(), x);
		assertEquals(v.getY(), y);
	}

	public static void assertEquals(Vector2 v, double x, double y) {
		assertEquals(v.getX(), x);
		assertEquals(v.getY(), y);
	}

	public static void assertEquals(Vector3 v, float x, float y, float z) {
		assertEquals(v.getX(), x);
		assertEquals(v.getY(), y);
		assertEquals(v.getZ(), z);
	}

	public static void assertEquals(Vector3 v, double x, double y, double z) {
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

	public static void assertEquals(Vector4 v, double x, double y, double z, double w) {
		assertEquals(v.getX(), x);
		assertEquals(v.getY(), y);
		assertEquals(v.getZ(), z);
		assertEquals(v.getW(), w);
	}

	public static void assertEquals(VectorN v, float... f) {
		for (int i = 0; i < v.size(); i++) {
			assertEquals(v.get(i), f[i]);
		}
	}

	public static void assertEquals(VectorN v, double... d) {
		for (int i = 0; i < v.size(); i++) {
			assertEquals(v.get(i), d[i]);
		}
	}

	public static void assertEquals(float[] v, float... f) {
		for (int i = 0; i < v.length; i++) {
			assertEquals(v[i], f[i]);
		}
	}

	public static void assertEquals(double[] v, double... d) {
		for (int i = 0; i < v.length; i++) {
			assertEquals(v[i], d[i]);
		}
	}
}
