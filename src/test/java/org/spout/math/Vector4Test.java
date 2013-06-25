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

import org.spout.math.vector.Vector3;
import org.spout.math.vector.Vector4;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.spout.math.TestUtils.doAssertDouble;
import static org.spout.math.TestUtils.eps;

public class Vector4Test {
	@Test
	public void testUnits() {
		doAssertDouble("ZERO.getX() does not equal 0", 0, Vector4.ZERO.getX());
		doAssertDouble("ZERO.getY() does not equal 0", 0, Vector4.ZERO.getY());
		doAssertDouble("ZERO.getZ() does not equal 0", 0, Vector4.ZERO.getZ());
		doAssertDouble("ZERO.getW() does not equal 0", 0, Vector4.ZERO.getW());

		doAssertDouble("UNIT_X.getX() does not equal 1", 1, Vector4.UNIT_X.getX());
		doAssertDouble("UNIT_X.getY() does not equal 0", 0, Vector4.UNIT_X.getY());
		doAssertDouble("UNIT_X.getZ() does not equal 0", 0, Vector4.UNIT_X.getZ());
		doAssertDouble("UNIT_X.getW() does not equal 0", 0, Vector4.UNIT_X.getW());

		doAssertDouble("UNIT_Y.getX() does not equal 0", 0, Vector4.UNIT_Y.getX());
		doAssertDouble("UNIT_Y.getY() does not equal 1", 1, Vector4.UNIT_Y.getY());
		doAssertDouble("UNIT_Y.getZ() does not equal 0", 0, Vector4.UNIT_Y.getZ());
		doAssertDouble("UNIT_Y.getW() does not equal 0", 0, Vector4.UNIT_Y.getW());

		doAssertDouble("UNIT_Z.getX() does not equal 0", 0, Vector4.UNIT_Z.getX());
		doAssertDouble("UNIT_Z.getY() does not equal 0", 0, Vector4.UNIT_Z.getY());
		doAssertDouble("UNIT_Z.getZ() does not equal 1", 1, Vector4.UNIT_Z.getZ());
		doAssertDouble("UNIT_Z.getW() does not equal 0", 0, Vector4.UNIT_Z.getW());

		doAssertDouble("UNIT_W.getX() does not equal 0", 0, Vector4.UNIT_W.getX());
		doAssertDouble("UNIT_W.getY() does not equal 0", 0, Vector4.UNIT_W.getY());
		doAssertDouble("UNIT_W.getZ() does not equal 0", 0, Vector4.UNIT_W.getZ());
		doAssertDouble("UNIT_W.getW() does not equal 1", 1, Vector4.UNIT_W.getW());
	}

	@Test
	public void testConstructors() {
		Vector4 x = new Vector4(2f, 3f, 4f, 5f);
		doAssertDouble("x.X does not equal 2f", 2f, x.getX());
		doAssertDouble("x.Y does not equal 3f", 3f, x.getY());
		doAssertDouble("x.Z does not equal 2f", 4f, x.getZ());
		doAssertDouble("x.W does not equal 3f", 5f, x.getW());

		x = new Vector4(6d, 7d, 8d, 9d);
		doAssertDouble("x.X does not equal 6f", 6f, x.getX());
		doAssertDouble("x.Y does not equal 7f", 7f, x.getY());
		doAssertDouble("x.Z does not equal 8f", 8f, x.getZ());
		doAssertDouble("x.W does not equal 9f", 9f, x.getW());

		x = new Vector4(8, 9, 10, 11);
		doAssertDouble("x.X does not equal 8f", 8f, x.getX());
		doAssertDouble("x.Y does not equal 9f", 9f, x.getY());
		doAssertDouble("x.X does not equal 10f", 10f, x.getZ());
		doAssertDouble("x.Y does not equal 11f", 11f, x.getW());

		Vector4 y = new Vector4(x);
		doAssertDouble("y.X does not equal 8f", 8f, y.getX());
		doAssertDouble("y.Y does not equal 9f", 9f, y.getY());
		doAssertDouble("x.X does not equal 10f", 10f, x.getZ());
		doAssertDouble("x.Y does not equal 11f", 11f, x.getW());

		x = new Vector4();
		assertEquals(x, Vector4.ZERO);
	}

	@Test
	public void testAddVector4() {
		Vector4 a = new Vector4(1, -1, 3, 5);
		Vector4 b = new Vector4(2, 6, -2, 5);
		Vector4 c = a.add(b);

		doAssertDouble(3, c.getX());
		doAssertDouble(5, c.getY());
		doAssertDouble(1, c.getZ());
		doAssertDouble(10, c.getW());
	}

	@Test
	public void testAddFloat() {
		Vector4 a = new Vector4(1, -1, 3, 5);
		Vector4 c = a.add(2f, 6f, -2f, 5f);

		doAssertDouble(3, c.getX());
		doAssertDouble(5, c.getY());
		doAssertDouble(1, c.getZ());
		doAssertDouble(10, c.getW());
	}

	@Test
	public void testAddDouble() {
		Vector4 a = new Vector4(1, -1, 3, 5);
		Vector4 c = a.add(2d, 6d, -2d, 5d);

		doAssertDouble(3, c.getX());
		doAssertDouble(5, c.getY());
		doAssertDouble(1, c.getZ());
		doAssertDouble(10, c.getW());
	}

	@Test
	public void testAddInt() {
		Vector4 a = new Vector4(1, -1, 3, 5);
		Vector4 c = a.add(2, 6, -2, 5);

		doAssertDouble(3, c.getX());
		doAssertDouble(5, c.getY());
		doAssertDouble(1, c.getZ());
		doAssertDouble(10, c.getW());
	}

	@Test
	public void testSubtractVector4() {
		Vector4 a = new Vector4(1, -1, 4, -2);
		Vector4 b = new Vector4(2, 6, 4, 1);
		Vector4 c = a.sub(b);

		doAssertDouble(-1, c.getX());
		doAssertDouble(-7, c.getY());
		doAssertDouble(0, c.getZ());
		doAssertDouble(-3, c.getW());
	}

	@Test
	public void testSubtractFloat() {
		Vector4 a = new Vector4(1, -1, 4, -2);
		Vector4 c = a.sub(2f, 6f, 4f, 1f);

		doAssertDouble(-1, c.getX());
		doAssertDouble(-7, c.getY());
		doAssertDouble(0, c.getZ());
		doAssertDouble(-3, c.getW());
	}

	@Test
	public void testSubtractDouble() {
		Vector4 a = new Vector4(1, -1, 4, -2);
		Vector4 c = a.sub(2d, 6d, 4d, 1d);

		doAssertDouble(-1, c.getX());
		doAssertDouble(-7, c.getY());
		doAssertDouble(0, c.getZ());
		doAssertDouble(-3, c.getW());
	}

	@Test
	public void testSubtractInt() {
		Vector4 a = new Vector4(1, -1, 4, -2);
		Vector4 c = a.sub(2, 6, 4, 1);

		doAssertDouble(-1, c.getX());
		doAssertDouble(-7, c.getY());
		doAssertDouble(0, c.getZ());
		doAssertDouble(-3, c.getW());
	}

	@Test
	public void testMultiplyVector4() {
		Vector4 a = new Vector4(1, -1, 4, 2);
		Vector4 b = new Vector4(2, 6, 4, 1);
		Vector4 c = a.mul(b);

		doAssertDouble(2, c.getX());
		doAssertDouble(-6, c.getY());
		doAssertDouble(16, c.getZ());
		doAssertDouble(2, c.getW());
	}

	@Test
	public void testMultiplyFloat() {
		Vector4 a = new Vector4(1, -1, 4, 2);
		Vector4 b = a.mul(2f, 6f, 4f, 1f);

		doAssertDouble(2, b.getX());
		doAssertDouble(-6, b.getY());
		doAssertDouble(16, b.getZ());
		doAssertDouble(2, b.getW());

		Vector4 c = a.mul(2.0F);

		doAssertDouble(2, c.getX());
		doAssertDouble(-2, c.getY());
		doAssertDouble(8, c.getZ());
		doAssertDouble(4, c.getW());
	}

	@Test
	public void testMultiplyDouble() {
		Vector4 a = new Vector4(1, -1, 4, 2);
		Vector4 b = a.mul(2d, 6d, 4d, 1d);

		doAssertDouble(2, b.getX());
		doAssertDouble(-6, b.getY());
		doAssertDouble(16, b.getZ());
		doAssertDouble(2, b.getW());

		Vector4 c = a.mul(2.0d);

		doAssertDouble(2, c.getX());
		doAssertDouble(-2, c.getY());
		doAssertDouble(8, c.getZ());
		doAssertDouble(4, c.getW());
	}

	@Test
	public void testMultiplyInt() {
		Vector4 a = new Vector4(1, -1, 4, 2);
		Vector4 b = a.mul(2, 6, 4, 1);

		doAssertDouble(2, b.getX());
		doAssertDouble(-6, b.getY());
		doAssertDouble(16, b.getZ());
		doAssertDouble(2, b.getW());

		Vector4 c = a.mul(2.0);

		doAssertDouble(2, c.getX());
		doAssertDouble(-2, c.getY());
		doAssertDouble(8, c.getZ());
		doAssertDouble(4, c.getW());
	}

	@Test
	public void testDot() {
		Vector4 x = new Vector4(2, 3, 4, 5);
		doAssertDouble("x dot x should be 54", 54, x.dot(x));

		x = new Vector4(3, 2, 4, 5);
		Vector4 y = new Vector4(4, -1, 4, 2);
		doAssertDouble("x dot y should be 36", 36, x.dot(y));
	}

	@Test
	public void testToVector3() {
		Vector4 x = new Vector4(3, 5, 6, 7);
		Vector3 y = new Vector3(3, 5, 6);

		assertTrue(x.toVector3().equals(y));
	}

	@Test
	public void testCeil() {
		Vector4 x = new Vector4(1.4, -0.2, -2.4, 3.4);
		Vector4 y = x.ceil();
		doAssertDouble(2, y.getX());
		doAssertDouble(0, y.getY());
		doAssertDouble(-2, y.getZ());
		doAssertDouble(4, y.getW());
	}

	@Test
	public void testFloor() {
		Vector4 x = new Vector4(1.4, -0.2, -2.4, 3.4);
		Vector4 y = x.floor();
		doAssertDouble(1, y.getX());
		doAssertDouble(-1, y.getY());
		doAssertDouble(-3, y.getZ());
		doAssertDouble(3, y.getW());
	}

	@Test
	public void testRound() {
		Vector4 x = new Vector4(1.4, -0.2, -2.4, 3.4);
		Vector4 y = x.round();
		doAssertDouble(1, y.getX());
		doAssertDouble(0, y.getY());
		doAssertDouble(-2, y.getZ());
		doAssertDouble(3, y.getW());
	}

	@Test
	public void testAbs() {
		Vector4 x = new Vector4(1.4, -0.2, -2.4, 3.4);
		Vector4 y = x.abs();
		doAssertDouble(1.4, y.getX());
		doAssertDouble(0.2, y.getY());
		doAssertDouble(2.4, y.getZ());
		doAssertDouble(3.4, y.getW());
	}

	@Test
	public void testDistance() {
		Vector4 x = new Vector4(2, 3, 4, 5);
		Vector4 y = new Vector4(1, 2, 3, 4);
		doAssertDouble(2, x.distance(y));

		x = new Vector4(10, 3, 6, -5);
		y = new Vector4(7, 3, -2, 1);
		doAssertDouble(Math.sqrt(109), x.distance(y));
	}

	@Test
	public void testPow() {
		Vector4 x = new Vector4(1, 2, 3, 4);
		Vector4 y = x.pow(3);
		doAssertDouble(1, y.getX());
		doAssertDouble(8, y.getY());
		doAssertDouble(27, y.getZ());
		doAssertDouble(64, y.getW());

		x = new Vector4(1, 2, 3, 4);
		y = x.pow(2);
		doAssertDouble(1, y.getX());
		doAssertDouble(4, y.getY());
		doAssertDouble(9, y.getZ());
		doAssertDouble(16, y.getW());

		x = new Vector4(25, 16, 9, 4);
		y = x.pow(0.5);
		doAssertDouble(5, y.getX());
		doAssertDouble(4, y.getY());
		doAssertDouble(3, y.getZ());
		doAssertDouble(2, y.getW());
	}

	@Test
	public void testLengthSquared() {
		Vector4 x = new Vector4(2, 3, 4, 5);
		doAssertDouble(54, x.lengthSquared());
	}

	@Test
	public void testLength() {
		Vector4 x = new Vector4(2, 3, 4, 5);
		doAssertDouble(Math.sqrt(54), x.length());
	}

	@Test
	public void testNormalize() {
		Vector4 x = new Vector4(3, 4, 5, 6);
		Vector4 y = x.normalize();
		doAssertDouble(0.323, y.getX());
		doAssertDouble(0.431, y.getY());
		doAssertDouble(1, y.length());
	}

	@Test
	public void testToArray() {
		Vector4 x = new Vector4(5, 3, 6, 7);
		float[] r = x.toArray();
		assertArrayEquals(new float[]{5, 3, 6, 7}, r, (float) eps);
		doAssertDouble(5, r[0]);
		doAssertDouble(3, r[1]);
		doAssertDouble(6, r[2]);
		doAssertDouble(7, r[3]);
	}

	@Test
	public void testCompareTo() {
		Vector4 x = new Vector4(5, 3, 4, 6);
		Vector4 y = new Vector4(-2, 5, -2, 4);
		assertTrue(x.compareTo(y) >= 0);
	}

	@Test
	public void testEquals() {
		Vector4 x = new Vector4(1, 1, 1, 1);
		Vector4 y = new Vector4(1, 1, 1, 1);
		Vector4 z = new Vector4(1, 2, 1, 1);
		assertTrue(x.equals(y));
		assertFalse(x.equals(z));
	}

	@Test
	public void testToString() {
		Vector4 x = new Vector4(3, 5, 0, 1);
		assertEquals("(3.0, 5.0, 0.0, 1.0)", x.toString());
	}

	@Test
	public void testMin() {
		Vector4 x = new Vector4(5, -15, 3, 1);
		Vector4 y = new Vector4(3, 2, 5, -1);
		assertEquals(new Vector4(3, -15, 3, -1), x.min(y));
	}

	@Test
	public void testMax() {
		Vector4 x = new Vector4(5, -15, 3, 1);
		Vector4 y = new Vector4(3, 2, 5, -1);
		assertEquals(new Vector4(5, 2, 5, 1), x.max(y));
	}
}
