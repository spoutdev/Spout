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

import org.junit.Test;

import org.spout.math.imaginary.Quaternion;
import org.spout.math.matrix.MatrixN;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.spout.math.TestUtils.doAssertDouble;
import static org.spout.math.TestUtils.eps;

public class Vector3Test {
	private void testValue(Vector3 v, float x, float y, float z) {
		if (Math.abs(v.getX() - x) >= eps || Math.abs(v.getY() - y) >= eps || Math.abs(v.getZ() - z) >= eps) {
			fail("Test Fail! Expected {" + x + "," + y + "," + z + "} but got " + v);
		}
	}

	private void testValue(Vector3 v, Vector3 v2) {
		testValue(v, v2.getX(), v2.getY(), v2.getZ());
	}

	@Test
	public void testUnits() {
		testValue(Vector3.ZERO, 0, 0, 0);
		testValue(Vector3.UNIT_X, 1, 0, 0);
		testValue(Vector3.FORWARD, Vector3.UNIT_Z);
		testValue(Vector3.UNIT_Y, 0, 1, 0);
		testValue(Vector3.UP, Vector3.UNIT_Y);
		testValue(Vector3.UNIT_Z, 0, 0, 1);
		testValue(Vector3.RIGHT, Vector3.UNIT_X);
	}

	@Test
	public void testConstructors() {
		Vector3 x = new Vector3(2f, 3f, 4f);
		doAssertDouble("x.X does not equal 2f", 2f, x.getX());
		doAssertDouble("x.Y does not equal 3f", 3f, x.getY());
		doAssertDouble("x.Z does not equal 4f", 4f, x.getZ());

		x = new Vector3(4f, 5f, 6f);
		doAssertDouble("x.X does not equal 4f", 4f, x.getX());
		doAssertDouble("x.Y does not equal 5f", 5f, x.getY());
		doAssertDouble("x.Z does not equal 6f", 6f, x.getZ());

		x = new Vector3(6d, 7d, 8d);
		doAssertDouble("x.X does not equal 6f", 6f, x.getX());
		doAssertDouble("x.Y does not equal 7f", 7f, x.getY());
		doAssertDouble("x.Z does not equal 8f", 8f, x.getZ());

		x = new Vector3(8, 9, 10);
		doAssertDouble("x.X does not equal 8f", 8f, x.getX());
		doAssertDouble("x.Y does not equal 9f", 9f, x.getY());
		doAssertDouble("x.Z does not equal 10f", 10f, x.getZ());

		Vector3 y = new Vector3(x);
		doAssertDouble("y.X does not equal 8f", 8f, y.getX());
		doAssertDouble("y.Y does not equal 9f", 9f, y.getY());
		doAssertDouble("x.Z does not equal 10f", 10f, x.getZ());

		x = new Vector3();
		doAssertDouble("x.X does not equal 0", 0, x.getX());
		doAssertDouble("x.Y does not equal 0", 0, x.getY());
		doAssertDouble("x.Z does not equal 0", 0, x.getZ());
	}

	@Test
	public void testAddVector3() {
		Vector3 a = new Vector3(1, -1, 3);
		Vector3 b = new Vector3(2, 6, 4);
		Vector3 c = a.add(b);

		doAssertDouble("x.X does not equal 3", 3, c.getX());
		doAssertDouble("x.Y does not equal 5", 5, c.getY());
		doAssertDouble("x.Z does not equal 7", 7, c.getZ());
	}

	@Test
	public void testAddFloat() {
		Vector3 a = new Vector3(1, -1, 4);
		Vector3 c = a.add(2.0F, 6.0F, 8.0F);

		doAssertDouble("x.X does not equal 3", 3, c.getX());
		doAssertDouble("x.Y does not equal 5", 5, c.getY());
		doAssertDouble("x.Z does not equal 12", 12, c.getZ());
	}

	@Test
	public void testAddDouble() {
		Vector3 a = new Vector3(1, -1, -3);
		Vector3 c = a.add(2.0D, 6.0D, 3.0D);

		doAssertDouble("x.X does not equal 3", 3, c.getX());
		doAssertDouble("x.Y does not equal 5", 5, c.getY());
		doAssertDouble("x.Z does not equal 0", 0, c.getZ());
	}

	@Test
	public void testAddInt() {
		Vector3 a = new Vector3(1, -1, 2);
		Vector3 c = a.add(2, 6, 3);

		doAssertDouble("x.X does not equal 3", 3, c.getX());
		doAssertDouble("x.Y does not equal 5", 5, c.getY());
		doAssertDouble("x.Z does not equal 5", 5, c.getZ());
	}

	@Test
	public void testSubtractVector3() {
		Vector3 a = new Vector3(1, -1, 4);
		Vector3 b = new Vector3(2, 6, 2);
		Vector3 c = a.sub(b);

		doAssertDouble("x.X does not equal -1", -1, c.getX());
		doAssertDouble("x.Y does not equal -7", -7, c.getY());
		doAssertDouble("x.Z does not equal 2", 2, c.getZ());
	}

	@Test
	public void testSubtractFloat() {
		Vector3 a = new Vector3(1, -1, 1);
		Vector3 c = a.sub(2.0F, 6.0F, 1.0F);

		doAssertDouble("x.X does not equal -1", -1, c.getX());
		doAssertDouble("x.Y does not equal -7", -7, c.getY());
		doAssertDouble("x.Z does not equal 0", 0, c.getZ());
	}

	@Test
	public void testSubtractDouble() {
		Vector3 a = new Vector3(1, -1, 19.5);
		Vector3 c = a.sub(2.0D, 6.0D, 18.5D);

		doAssertDouble("x.X does not equal -1", -1, c.getX());
		doAssertDouble("x.Y does not equal -7", -7, c.getY());
		doAssertDouble("x.Z does not equal 1", 1, c.getZ());
	}

	@Test
	public void testSubtractInt() {
		Vector3 a = new Vector3(1, -1, 4);
		Vector3 c = a.sub(2, 6, 3);

		doAssertDouble("x.X does not equal -1", -1, c.getX());
		doAssertDouble("x.Y does not equal -7", -7, c.getY());
		doAssertDouble("x.Z does not equal 1", 1, c.getZ());
	}

	@Test
	public void testMultiplyVector3() {
		Vector3 a = new Vector3(1, -1, 3);
		Vector3 b = new Vector3(2, 6, -3);
		Vector3 c = a.mul(b);

		doAssertDouble("x.X does not equal 2", 2, c.getX());
		doAssertDouble("x.Y does not equal -6", -6, c.getY());
		doAssertDouble("x.Z does not equal -9", -9, c.getZ());
	}

	@Test
	public void testMultiplyFloat() {
		Vector3 a = new Vector3(1, -1, 3.5);
		Vector3 b = a.mul(2.0F, 6.0F, 2.0F);

		doAssertDouble("x.X does not equal 2", 2, b.getX());
		doAssertDouble("x.Y does not equal -6", -6, b.getY());
		doAssertDouble("x.Z does not equal 7", 7, b.getZ());

		Vector3 c = a.mul(2.0F);

		doAssertDouble("x.X does not equal 2", 2, c.getX());
		doAssertDouble("x.Y does not equal -2", -2, c.getY());
		doAssertDouble("x.Z does not equal 7", 7, c.getZ());
	}

	@Test
	public void testDot() {
		Vector3 x = new Vector3(2, 3, 4);
		doAssertDouble("x dot x should be 29", 29, x.dot(x));

		x = new Vector3(3, 2, 1);
		Vector3 y = new Vector3(4, -1, -2);
		doAssertDouble("x dot y should be 8", 8, x.dot(y));
	}

	@Test
	public void testToVector2() {
		Vector3 x = new Vector3(3, 5, 6);
		Vector2 y = new Vector2(3, 5);

		assertTrue(x.toVector2().equals(y));
	}

	@Test
	public void testCross() {
		Vector3 x = new Vector3(1, 0, 0);
		Vector3 y = new Vector3(0, 1, 0);
		Vector3 z = x.cross(y);
		testValue(z, 0, 0, 1.f);

		Vector3 a = new Vector3(2, 5, 3);
		Vector3 b = new Vector3(3, -1, 8);
		Vector3 c = a.cross(b);
		testValue(c, 43.f, -7.f, -17.f);
	}

	@Test
	public void testCeil() {
		Vector3 x = new Vector3(1.4, 0.2, 3.4);
		Vector3 y = x.ceil();
		doAssertDouble(2, y.getX());
		doAssertDouble(1, y.getY());
		doAssertDouble(4, y.getZ());

		x = new Vector3(5.5, -3.3, 2.1);
		y = x.ceil();
		doAssertDouble(6, y.getX());
		doAssertDouble(-3, y.getY());
		doAssertDouble(3, y.getZ());
	}

	@Test
	public void testFloor() {
		Vector3 x = new Vector3(1.4, 0.2, 3.4);
		Vector3 y = x.floor();
		doAssertDouble(1, y.getX());
		doAssertDouble(0, y.getY());
		doAssertDouble(3, y.getZ());

		x = new Vector3(5.5, -3.3, 2.1);
		y = x.floor();
		doAssertDouble(5, y.getX());
		doAssertDouble(-4, y.getY());
		doAssertDouble(2, y.getZ());
	}

	@Test
	public void testRound() {
		Vector3 x = new Vector3(1.4, 0.2, 3.4);
		Vector3 y = x.round();
		doAssertDouble(1, y.getX());
		doAssertDouble(0, y.getY());
		doAssertDouble(3, y.getZ());

		x = new Vector3(5.5, -3.3, 2.1);
		y = x.round();
		doAssertDouble(6, y.getX());
		doAssertDouble(-3, y.getY());
		doAssertDouble(2, y.getZ());
	}

	@Test
	public void testAbs() {
		Vector3 x = new Vector3(1.4, 0.2, 3.4);
		Vector3 y = x.abs();
		doAssertDouble(1.4, y.getX());
		doAssertDouble(0.2, y.getY());
		doAssertDouble(3.4, y.getZ());

		x = new Vector3(5.5, -3.3, 2.1);
		y = x.abs();
		doAssertDouble(5.5, y.getX());
		doAssertDouble(3.3, y.getY());
		doAssertDouble(2.1, y.getZ());
	}

	@Test
	public void testDistance() {
		Vector3 x = new Vector3(1.4, Math.sqrt(2), 0.2);
		doAssertDouble(2, x.distance(new Vector3()));

		x = new Vector3(5.5, 6.414, -3.3);
		doAssertDouble(9.07, x.distance(new Vector3()));
	}

	@Test
	public void testPow() {
		Vector3 x = new Vector3(1.4, 0.2, 3.4);
		Vector3 y = x.pow(3);
		doAssertDouble(2.744, y.getX());
		doAssertDouble(0.008, y.getY());
		doAssertDouble(39.304, y.getZ());

		x = new Vector3(5.5, -3.3, 2.1);
		y = x.pow(2);
		doAssertDouble(30.25, y.getX());
		doAssertDouble(10.89, y.getY());
		doAssertDouble(4.41, y.getZ());
	}

	@Test
	public void testLengthSquared() {
		Vector3 x = new Vector3(3, 5, 4);
		doAssertDouble(50, x.lengthSquared());

		x = new Vector3(5, 13, 12);
		doAssertDouble(338, x.lengthSquared());
	}

	@Test
	public void testLength() {
		Vector3 x = new Vector3(3, 5, 4);
		doAssertDouble(7.071, x.length());

		x = new Vector3(5, 13, 12);
		doAssertDouble(18.385, x.length());
	}

	@Test
	public void testNormalize() {
		Vector3 x = new Vector3(1, 0, 0);
		doAssertDouble(1, Math.abs(x.normalize().length()));

		Vector3 y = new Vector3(2, 4, 0);
		doAssertDouble(1, Math.abs(y.normalize().length()));
	}

	@Test
	public void testToArray() {
		Vector3 x = new Vector3(5, 3, 7);
		float[] r = x.toArray();
		assertArrayEquals(new float[]{5, 3, 7}, r, (float) eps);
		doAssertDouble(5, r[0]);
		doAssertDouble(3, r[1]);
		doAssertDouble(7, r[2]);
	}

	@Test
	public void testCompareTo() {
		Vector3 x = new Vector3(1, 0, 0);
		Vector3 y = new Vector3(2, 0, 0);
		assertTrue(x.compareTo(y) < 0);
	}

	@Test
	public void testEquals() {
		Vector3 x = new Vector3(1, 1, 1);
		Vector3 y = new Vector3(1, 1, 1);
		Vector3 z = new Vector3(1, 2, 1);
		assertTrue(x.equals(y));
		assertFalse(x.equals(z));
	}

	@Test
	public void testToString() {
		Vector3 x = new Vector3(3, 5, 7.3);
		assertEquals("(3.0, 5.0, 7.3)", x.toString());
	}

	@Test
	public void testMin() {
		Vector3 x = new Vector3(5, -15, 4);
		Vector3 y = new Vector3(3, 2, 6);
		assertEquals(new Vector3(3, -15, 4), x.min(y));
	}

	@Test
	public void testMax() {
		Vector3 x = new Vector3(5, -15, 4);
		Vector3 y = new Vector3(3, 2, 6);
		assertEquals(new Vector3(5, 2, 6), x.max(y));
	}

	@Test
	public void testTransformVector3() {
		Vector3 x = new Vector3(1, 0, 0);
		Vector3 u = MatrixN.createRotation(3, Quaternion.fromAngleDegAxis(90, Vector3.UNIT_Y)).transform(x);
		testValue(u, 0, 0, -1);

		Vector3 y = new Vector3(2, 4, 5);
		Vector3 v = MatrixN.createRotation(3, Quaternion.fromAngleDegAxis(30, Vector3.UNIT_X)).transform(y);
		testValue(v, 2, .9666f, 6.333f);
	}
}
