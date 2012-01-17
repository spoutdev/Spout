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
import org.junit.Test;

/**
 * @author yetanotherx
 */
public class Vector2Test {
	public static final double eps = 0.001;

	private void doAssertDouble(String message, double expect, double got) {
		assertEquals(message, expect, got, eps);
	}

	private void doAssertDouble(double expect, double got) {
		assertEquals(expect, got, eps);
	}

	@Test
	public void testUnits() {
		doAssertDouble("ONE.x does not equal 1", 1, Vector2.ONE.x);
		doAssertDouble("ONE.y does not equal 1", 1, Vector2.ONE.y);

		doAssertDouble("UNIT_X.x does not equal 1", 1, Vector2.UNIT_X.x);
		doAssertDouble("UNIT_X.y does not equal 0", 0, Vector2.UNIT_X.y);

		doAssertDouble("UNIT_X.x does not equal 0", 0, Vector2.UNIT_Y.x);
		doAssertDouble("UNIT_X.y does not equal 1", 1, Vector2.UNIT_Y.y);

		doAssertDouble("ZERO.x does not equal 0", 0, Vector2.ZERO.x);
		doAssertDouble("ZERO.y does not equal 0", 0, Vector2.ZERO.y);
	}

	@Test
	public void testConstructors() {
		Vector2 x = new Vector2(2f, 3f);
		doAssertDouble("x.X does not equal 2f", 2f, x.x);
		doAssertDouble("x.Y does not equal 3f", 3f, x.y);

		x = new Vector2(4f, 5f);
		doAssertDouble("x.X does not equal 4f", 4f, x.x);
		doAssertDouble("x.Y does not equal 5f", 5f, x.y);

		x = new Vector2(6d, 7d);
		doAssertDouble("x.X does not equal 6f", 6f, x.x);
		doAssertDouble("x.Y does not equal 7f", 7f, x.y);

		x = new Vector2(8, 9);
		doAssertDouble("x.X does not equal 8f", 8f, x.x);
		doAssertDouble("x.Y does not equal 9f", 9f, x.y);

		Vector2 y = new Vector2(x);
		doAssertDouble("y.X does not equal 8f", 8f, y.x);
		doAssertDouble("y.Y does not equal 9f", 9f, y.y);

		x = new Vector2();
		doAssertDouble("x.X does not equal 0", 0, x.x);
		doAssertDouble("x.Y does not equal 0", 0, x.y);
	}

	@Test
	public void testAddVector2() {
		Vector2 a = new Vector2(1, -1);
		Vector2 b = new Vector2(2, 6);
		Vector2 c = a.add(b);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
	}

	@Test
	public void testAddFloat() {
		Vector2 a = new Vector2(1, -1);
		Vector2 c = a.add(2.0F, 6.0F);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
	}

	@Test
	public void testAddDouble() {
		Vector2 a = new Vector2(1, -1);
		Vector2 c = a.add(2.0D, 6.0D);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
	}

	@Test
	public void testAddInt() {
		Vector2 a = new Vector2(1, -1);
		Vector2 c = a.add(2, 6);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
	}

	@Test
	public void testSubtractVector2() {
		Vector2 a = new Vector2(1, -1);
		Vector2 b = new Vector2(2, 6);
		Vector2 c = a.subtract(b);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
	}

	@Test
	public void testSubtractFloat() {
		Vector2 a = new Vector2(1, -1);
		Vector2 c = a.subtract(2.0F, 6.0F);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
	}

	@Test
	public void testSubtractDouble() {
		Vector2 a = new Vector2(1, -1);
		Vector2 c = a.subtract(2.0D, 6.0D);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
	}

	@Test
	public void testSubtractInt() {
		Vector2 a = new Vector2(1, -1);
		Vector2 c = a.subtract(2, 6);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
	}

	@Test
	public void testMultiplyVector2() {
		Vector2 a = new Vector2(1, -1);
		Vector2 b = new Vector2(2, 6);
		Vector2 c = a.multiply(b);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -6", -6, c.y);
	}

	@Test
	public void testMultiplyFloat() {
		Vector2 a = new Vector2(1, -1);
		Vector2 b = a.multiply(2.0F, 6.0F);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);

		Vector2 c = a.multiply(2.0F);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -2", -2, c.y);
	}

	@Test
	public void testMultiplyDouble() {
		Vector2 a = new Vector2(1, -1);
		Vector2 b = a.multiply(2.0D, 6.0D);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);

		Vector2 c = a.multiply(2.0D);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -2", -2, c.y);
	}

	@Test
	public void testMultiplyInt() {
		Vector2 a = new Vector2(1, -1);
		Vector2 b = a.multiply(2, 6);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);

		Vector2 c = a.multiply(2);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -2", -2, c.y);
	}

	@Test
	public void testDivideVector2() {
		Vector2 a = new Vector2(4, -36);
		Vector2 b = new Vector2(2, 6);
		Vector2 c = a.divide(b);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -6", -6, c.y);
	}

	@Test
	public void testDivideFloat() {
		Vector2 a = new Vector2(4, -36);
		Vector2 b = a.divide(2.0F, 6.0F);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);

		Vector2 c = a.divide(2.0F);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -18", -18, c.y);
	}

	@Test
	public void testDivideDouble() {
		Vector2 a = new Vector2(4, -36);
		Vector2 b = a.divide(2.0D, 6.0D);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);

		Vector2 c = a.divide(2.0D);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -18", -18, c.y);
	}

	@Test
	public void testDivideInt() {
		Vector2 a = new Vector2(4, -36);
		Vector2 b = a.divide(2, 6);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);

		Vector2 c = a.divide(2);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -18", -18, c.y);
	}

	@Test
	public void testDot() {
		Vector2 x = new Vector2(2, 3);
		doAssertDouble("x dot x should be 13", 13, x.dot(x));

		x = new Vector2(3, 2);
		Vector2 y = new Vector2(4, -1);
		doAssertDouble("x dot y should be 10", 10, x.dot(y));
	}

	@Test
	public void testToVector3() {
		Vector2 x = new Vector2(3, 5);
		Vector3 y = new Vector3(3, 0, 5);
		Vector3 y2 = new Vector3(3, 6, 5);

		assertTrue(x.toVector3().equals(y));
		assertTrue(x.toVector3(6).equals(y2));
	}

	@Test
	public void testToVector3m() {
		Vector2 x = new Vector2(3, 5);
		Vector3 y = new Vector3m(3, 0, 5);
		Vector3 y2 = new Vector3m(3, 6, 5);

		assertTrue(x.toVector3m().equals(y));
		assertTrue(x.toVector3m(6).equals(y2));
	}

	@Test
	public void testCross() {
		Vector2 x = new Vector2(1, 0);
		Vector2 y = x.cross();
		doAssertDouble(0, y.x);
		doAssertDouble(-1, y.y);

		x = new Vector2(5, -3);
		y = x.cross();
		doAssertDouble(-3, y.x);
		doAssertDouble(-5, y.y);
	}

	@Test
	public void testCeil() {
		Vector2 x = new Vector2(1.4, 0.2);
		Vector2 y = x.ceil();
		doAssertDouble(2, y.x);
		doAssertDouble(1, y.y);

		x = new Vector2(5.5, -3.3);
		y = x.ceil();
		doAssertDouble(6, y.x);
		doAssertDouble(-3, y.y);
	}

	@Test
	public void testFloor() {
		Vector2 x = new Vector2(1.4, 0.2);
		Vector2 y = x.floor();
		doAssertDouble(1, y.x);
		doAssertDouble(0, y.y);

		x = new Vector2(5.5, -3.3);
		y = x.floor();
		doAssertDouble(5, y.x);
		doAssertDouble(-4, y.y);
	}

	@Test
	public void testRound() {
		Vector2 x = new Vector2(1.4, 0.2);
		Vector2 y = x.round();
		doAssertDouble(1, y.x);
		doAssertDouble(0, y.y);

		x = new Vector2(5.5, -3.3);
		y = x.round();
		doAssertDouble(6, y.x);
		doAssertDouble(-3, y.y);
	}

	@Test
	public void testAbs() {
		Vector2 x = new Vector2(1.4, 0.2);
		Vector2 y = x.abs();
		doAssertDouble(1.4, y.x);
		doAssertDouble(0.2, y.y);

		x = new Vector2(5.5, -3.3);
		y = x.abs();
		doAssertDouble(5.5, y.x);
		doAssertDouble(3.3, y.y);
	}

	@Test
	public void testDistance() {
		Vector2 x = new Vector2(1.4, 0.2);
		doAssertDouble(Math.sqrt(2), x.distance(new Vector2()));

		x = new Vector2(5.5, -3.3);
		doAssertDouble(Math.sqrt(41.14), x.distance(new Vector2()));
	}

	@Test
	public void testPow() {
		Vector2 x = new Vector2(1.4, 0.2);
		Vector2 y = x.pow(3);
		doAssertDouble(2.744, y.x);
		doAssertDouble(0.008, y.y);

		x = new Vector2(5.5, -3.3);
		y = x.pow(2);
		doAssertDouble(30.25, y.x);
		doAssertDouble(10.89, y.y);

		x = new Vector2(25, 16);
		y = x.pow(0.5);
		doAssertDouble(5, y.x);
		doAssertDouble(4, y.y);
	}

	@Test
	public void testLengthSquared() {
		Vector2 x = new Vector2(3, 4);
		doAssertDouble(25, x.lengthSquared());

		x = new Vector2(5, 12);
		doAssertDouble(169, x.lengthSquared());
	}

	@Test
	public void testLength() {
		Vector2 x = new Vector2(3, 4);
		doAssertDouble(5, x.length());

		x = new Vector2(5, 12);
		doAssertDouble(13, x.length());
	}

	@Test
	public void testNormalize() {
		Vector2 x = new Vector2(3, 4);
		Vector2 y = x.normalize();
		doAssertDouble(3.0d / 5.0d, y.x);
		doAssertDouble(4.0d / 5.0d, y.y);
		doAssertDouble(1, y.length());

		x = new Vector2(5, 12);
		y = x.normalize();
		doAssertDouble(5.0d / 13.0d, y.x);
		doAssertDouble(12.0d / 13.0d, y.y);
		doAssertDouble(1, y.length());
	}

	@Test
	public void testToArray() {
		Vector2 x = new Vector2(5, 3);
		float[] r = x.toArray();
		assertArrayEquals(new float[]{5, 3}, r, (float) eps);
		doAssertDouble(5, r[0]);
		doAssertDouble(3, r[1]);
	}

	@Test
	public void testCompareTo() {
		Vector2 x = new Vector2(0, 0);
		Vector2 y = new Vector2(1, 1);
		assertTrue(x.compareTo(y) < 0);
		x = new Vector2(5, 3);
		y = new Vector2(-2, 5);
		assertTrue(x.compareTo(y) >= 0);
	}

	@Test
	public void testEquals() {
		Vector2 x = new Vector2(1, 1);
		Vector2 y = new Vector2(1, 1);
		Vector2 z = new Vector2(1, 2);
		assertTrue(x.equals(y));
		assertFalse(x.equals(z));
	}

	@Test
	public void testHashCode() {
		Vector2 x = new Vector2(5, 27);
		Vector2 y = new Vector2(5, -3);
		doAssertDouble(649610237, x.hashCode());
		doAssertDouble(-1524612099, y.hashCode());
	}

	@Test
	public void testToString() {
		Vector2 x = new Vector2(3, 5);
		assertEquals("(3.0, 5.0)", x.toString());
	}

	@Test
	public void testMin() {
		Vector2 x = new Vector2(5, -15);
		Vector2 y = new Vector2(3, 2);
		assertEquals(new Vector2(3, -15), Vector2.min(x, y));
	}

	@Test
	public void testMax() {
		Vector2 x = new Vector2(5, -15);
		Vector2 y = new Vector2(3, 2);
		assertEquals(new Vector2(5, 2), Vector2.max(x, y));
	}

	@Test
	public void testRand() {
		for (int i = 0; i < 100; ++i) {
			Vector2 x = Vector2.rand();
			assertTrue(x.x >= -1);
			assertTrue(x.x <= 1);
			assertTrue(x.y >= -1);
			assertTrue(x.y <= 1);
		}
	}
}
