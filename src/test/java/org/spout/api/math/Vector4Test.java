/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.math;

import static org.junit.Assert.*;
import static org.spout.api.math.TestUtils.*;

import org.junit.Test;

/**
 * @author yetanotherx
 */
public class Vector4Test {
	@Test
	public void testUnits() {
		doAssertDouble("ONE.x does not equal 1", 1, Vector4.ONE.x);
		doAssertDouble("ONE.y does not equal 1", 1, Vector4.ONE.y);
		doAssertDouble("ONE.z does not equal 1", 1, Vector4.ONE.z);
		doAssertDouble("ONE.w does not equal 1", 1, Vector4.ONE.w);

		doAssertDouble("ZERO.x does not equal 0", 0, Vector4.ZERO.x);
		doAssertDouble("ZERO.y does not equal 0", 0, Vector4.ZERO.y);
		doAssertDouble("ZERO.z does not equal 0", 0, Vector4.ZERO.z);
		doAssertDouble("ZERO.w does not equal 0", 0, Vector4.ZERO.w);

		doAssertDouble("UNIT_X.x does not equal 1", 1, Vector4.UNIT_X.x);
		doAssertDouble("UNIT_X.y does not equal 0", 0, Vector4.UNIT_X.y);
		doAssertDouble("UNIT_X.z does not equal 0", 0, Vector4.UNIT_X.z);
		doAssertDouble("UNIT_X.w does not equal 0", 0, Vector4.UNIT_X.w);

		doAssertDouble("UNIT_Y.x does not equal 0", 0, Vector4.UNIT_Y.x);
		doAssertDouble("UNIT_Y.y does not equal 1", 1, Vector4.UNIT_Y.y);
		doAssertDouble("UNIT_Y.z does not equal 0", 0, Vector4.UNIT_Y.z);
		doAssertDouble("UNIT_Y.w does not equal 0", 0, Vector4.UNIT_Y.w);

		doAssertDouble("UNIT_Z.x does not equal 0", 0, Vector4.UNIT_Z.x);
		doAssertDouble("UNIT_Z.y does not equal 0", 0, Vector4.UNIT_Z.y);
		doAssertDouble("UNIT_Z.z does not equal 1", 1, Vector4.UNIT_Z.z);
		doAssertDouble("UNIT_Z.w does not equal 0", 0, Vector4.UNIT_Z.w);

		doAssertDouble("UNIT_W.x does not equal 0", 0, Vector4.UNIT_W.x);
		doAssertDouble("UNIT_W.y does not equal 0", 0, Vector4.UNIT_W.y);
		doAssertDouble("UNIT_W.z does not equal 0", 0, Vector4.UNIT_W.z);
		doAssertDouble("UNIT_W.w does not equal 1", 1, Vector4.UNIT_W.w);
	}

	@Test
	public void testConstructors() {
		Vector4 x = new Vector4(2f, 3f, 4f, 5f);
		doAssertDouble("x.X does not equal 2f", 2f, x.x);
		doAssertDouble("x.Y does not equal 3f", 3f, x.y);
		doAssertDouble("x.Z does not equal 2f", 4f, x.z);
		doAssertDouble("x.W does not equal 3f", 5f, x.w);

		x = new Vector4(6d, 7d, 8d, 9d);
		doAssertDouble("x.X does not equal 6f", 6f, x.x);
		doAssertDouble("x.Y does not equal 7f", 7f, x.y);
		doAssertDouble("x.Z does not equal 8f", 8f, x.z);
		doAssertDouble("x.W does not equal 9f", 9f, x.w);

		x = new Vector4(8, 9, 10, 11);
		doAssertDouble("x.X does not equal 8f", 8f, x.x);
		doAssertDouble("x.Y does not equal 9f", 9f, x.y);
		doAssertDouble("x.X does not equal 10f", 10f, x.z);
		doAssertDouble("x.Y does not equal 11f", 11f, x.w);

		Vector4 y = new Vector4(x);
		doAssertDouble("y.X does not equal 8f", 8f, y.x);
		doAssertDouble("y.Y does not equal 9f", 9f, y.y);
		doAssertDouble("x.X does not equal 10f", 10f, x.z);
		doAssertDouble("x.Y does not equal 11f", 11f, x.w);

		x = new Vector4();
		assertEquals(x, Vector4.ZERO);
	}

	@Test
	public void testAddVector4() {
		Vector4 a = new Vector4(1, -1, 3, 5);
		Vector4 b = new Vector4(2, 6, -2, 5);
		Vector4 c = a.add(b);

		doAssertDouble(3, c.x);
		doAssertDouble(5, c.y);
		doAssertDouble(1, c.z);
		doAssertDouble(10, c.w);
	}

	@Test
	public void testAddFloat() {
		Vector4 a = new Vector4(1, -1, 3, 5);
		Vector4 c = a.add(2f, 6f, -2f, 5f);

		doAssertDouble(3, c.x);
		doAssertDouble(5, c.y);
		doAssertDouble(1, c.z);
		doAssertDouble(10, c.w);
	}

	@Test
	public void testAddDouble() {
		Vector4 a = new Vector4(1, -1, 3, 5);
		Vector4 c = a.add(2d, 6d, -2d, 5d);

		doAssertDouble(3, c.x);
		doAssertDouble(5, c.y);
		doAssertDouble(1, c.z);
		doAssertDouble(10, c.w);
	}

	@Test
	public void testAddInt() {
		Vector4 a = new Vector4(1, -1, 3, 5);
		Vector4 c = a.add(2, 6, -2, 5);

		doAssertDouble(3, c.x);
		doAssertDouble(5, c.y);
		doAssertDouble(1, c.z);
		doAssertDouble(10, c.w);
	}

	@Test
	public void testSubtractVector4() {
		Vector4 a = new Vector4(1, -1, 4, -2);
		Vector4 b = new Vector4(2, 6, 4, 1);
		Vector4 c = a.subtract(b);

		doAssertDouble(-1, c.x);
		doAssertDouble(-7, c.y);
		doAssertDouble(0, c.z);
		doAssertDouble(-3, c.w);
	}

	@Test
	public void testSubtractFloat() {
		Vector4 a = new Vector4(1, -1, 4, -2);
		Vector4 c = a.subtract(2f, 6f, 4f, 1f);

		doAssertDouble(-1, c.x);
		doAssertDouble(-7, c.y);
		doAssertDouble(0, c.z);
		doAssertDouble(-3, c.w);
	}

	@Test
	public void testSubtractDouble() {
		Vector4 a = new Vector4(1, -1, 4, -2);
		Vector4 c = a.subtract(2d, 6d, 4d, 1d);

		doAssertDouble(-1, c.x);
		doAssertDouble(-7, c.y);
		doAssertDouble(0, c.z);
		doAssertDouble(-3, c.w);
	}

	@Test
	public void testSubtractInt() {
		Vector4 a = new Vector4(1, -1, 4, -2);
		Vector4 c = a.subtract(2, 6, 4, 1);

		doAssertDouble(-1, c.x);
		doAssertDouble(-7, c.y);
		doAssertDouble(0, c.z);
		doAssertDouble(-3, c.w);
	}

	@Test
	public void testMultiplyVector4() {
		Vector4 a = new Vector4(1, -1, 4, 2);
		Vector4 b = new Vector4(2, 6, 4, 1);
		Vector4 c = a.multiply(b);

		doAssertDouble(2, c.x);
		doAssertDouble(-6, c.y);
		doAssertDouble(16, c.z);
		doAssertDouble(2, c.w);
	}

	@Test
	public void testMultiplyFloat() {
		Vector4 a = new Vector4(1, -1, 4, 2);
		Vector4 b = a.multiply(2f, 6f, 4f, 1f);

		doAssertDouble(2, b.x);
		doAssertDouble(-6, b.y);
		doAssertDouble(16, b.z);
		doAssertDouble(2, b.w);

		Vector4 c = a.multiply(2.0F);

		doAssertDouble(2, c.x);
		doAssertDouble(-2, c.y);
		doAssertDouble(8, c.z);
		doAssertDouble(4, c.w);
	}

	@Test
	public void testMultiplyDouble() {
		Vector4 a = new Vector4(1, -1, 4, 2);
		Vector4 b = a.multiply(2d, 6d, 4d, 1d);

		doAssertDouble(2, b.x);
		doAssertDouble(-6, b.y);
		doAssertDouble(16, b.z);
		doAssertDouble(2, b.w);

		Vector4 c = a.multiply(2.0d);

		doAssertDouble(2, c.x);
		doAssertDouble(-2, c.y);
		doAssertDouble(8, c.z);
		doAssertDouble(4, c.w);
	}

	@Test
	public void testMultiplyInt() {
		Vector4 a = new Vector4(1, -1, 4, 2);
		Vector4 b = a.multiply(2, 6, 4, 1);

		doAssertDouble(2, b.x);
		doAssertDouble(-6, b.y);
		doAssertDouble(16, b.z);
		doAssertDouble(2, b.w);

		Vector4 c = a.multiply(2.0);

		doAssertDouble(2, c.x);
		doAssertDouble(-2, c.y);
		doAssertDouble(8, c.z);
		doAssertDouble(4, c.w);
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
		doAssertDouble(2, y.x);
		doAssertDouble(0, y.y);
		doAssertDouble(-2, y.z);
		doAssertDouble(4, y.w);
	}

	@Test
	public void testFloor() {
		Vector4 x = new Vector4(1.4, -0.2, -2.4, 3.4);
		Vector4 y = x.floor();
		doAssertDouble(1, y.x);
		doAssertDouble(-1, y.y);
		doAssertDouble(-3, y.z);
		doAssertDouble(3, y.w);
	}

	@Test
	public void testRound() {
		Vector4 x = new Vector4(1.4, -0.2, -2.4, 3.4);
		Vector4 y = x.round();
		doAssertDouble(1, y.x);
		doAssertDouble(0, y.y);
		doAssertDouble(-2, y.z);
		doAssertDouble(3, y.w);
	}

	@Test
	public void testAbs() {
		Vector4 x = new Vector4(1.4, -0.2, -2.4, 3.4);
		Vector4 y = x.abs();
		doAssertDouble(1.4, y.x);
		doAssertDouble(0.2, y.y);
		doAssertDouble(2.4, y.z);
		doAssertDouble(3.4, y.w);
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
		doAssertDouble(1, y.x);
		doAssertDouble(8, y.y);
		doAssertDouble(27, y.z);
		doAssertDouble(64, y.w);

		x = new Vector4(1, 2, 3, 4);
		y = x.pow(2);
		doAssertDouble(1, y.x);
		doAssertDouble(4, y.y);
		doAssertDouble(9, y.z);
		doAssertDouble(16, y.w);

		x = new Vector4(25, 16, 9, 4);
		y = x.pow(0.5);
		doAssertDouble(5, y.x);
		doAssertDouble(4, y.y);
		doAssertDouble(3, y.z);
		doAssertDouble(2, y.w);
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
		doAssertDouble(0.323, y.x);
		doAssertDouble(0.431, y.y);
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
		assertTrue(Vector4.ZERO.compareTo(Vector4.ONE) < 0);

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
	public void testHashCode() {
		Vector4 x = new Vector4(5, 27, 1, 2);
		Vector4 y = new Vector4(5, -3, 0, 1);
		doAssertDouble(-1677538473, x.hashCode());
		doAssertDouble(2032847703, y.hashCode());
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
		assertEquals(new Vector4(3, -15, 3, -1), Vector4.min(x, y));
	}

	@Test
	public void testMax() {
		Vector4 x = new Vector4(5, -15, 3, 1);
		Vector4 y = new Vector4(3, 2, 5, -1);
		assertEquals(new Vector4(5, 2, 5, 1), Vector4.max(x, y));
	}

	@Test
	public void testRand() {
		for (int i = 0; i < 100; ++i) {
			Vector4 x = Vector4.rand();
			assertTrue(x.x >= -1);
			assertTrue(x.x <= 1);
			assertTrue(x.y >= -1);
			assertTrue(x.y <= 1);
			assertTrue(x.z >= -1);
			assertTrue(x.z <= 1);
			assertTrue(x.w >= -1);
			assertTrue(x.w <= 1);
		}
	}
}
