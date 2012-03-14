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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.spout.api.math.TestUtils.doAssertDouble;
import static org.spout.api.math.TestUtils.eps;

import org.junit.Test;

public class Vector3Test {
	private void testValue(Vector3 v, float x, float y, float z) {
		if (Math.abs(v.getX() - x) >= eps || Math.abs(v.getY() - y) >= eps || Math.abs(v.getZ() - z) >= eps) {
			fail("Test Fail! Expected {" + x + "," + y + "," + z + "} but got " + v);
		}
	}

	private void testValue(Vector3 v, Vector3 v2) {
		testValue(v, v2.x, v2.y, v2.z);
	}

	@Test
	public void testUnits() {
		testValue(Vector3.ONE, 1, 1, 1);
		testValue(Vector3.ZERO, 0, 0, 0);
		testValue(Vector3.UNIT_X, 1, 0, 0);
		testValue(Vector3.Forward, Vector3.UNIT_X);
		testValue(Vector3.UNIT_Y, 0, 1, 0);
		testValue(Vector3.Up, Vector3.UNIT_Y);
		testValue(Vector3.UNIT_Z, 0, 0, 1);
		testValue(Vector3.Right, Vector3.UNIT_Z);
	}

	@Test
	public void testConstructors() {
		Vector3 x = Vector3.create(2f, 3f, 4f);
		doAssertDouble("x.X does not equal 2f", 2f, x.x);
		doAssertDouble("x.Y does not equal 3f", 3f, x.y);
		doAssertDouble("x.Z does not equal 4f", 4f, x.z);

		x = Vector3.create(4f, 5f, 6f);
		doAssertDouble("x.X does not equal 4f", 4f, x.x);
		doAssertDouble("x.Y does not equal 5f", 5f, x.y);
		doAssertDouble("x.Z does not equal 6f", 6f, x.z);

		x = Vector3.create(6d, 7d, 8d);
		doAssertDouble("x.X does not equal 6f", 6f, x.x);
		doAssertDouble("x.Y does not equal 7f", 7f, x.y);
		doAssertDouble("x.Z does not equal 8f", 8f, x.z);

		x = Vector3.create(8, 9, 10);
		doAssertDouble("x.X does not equal 8f", 8f, x.x);
		doAssertDouble("x.Y does not equal 9f", 9f, x.y);
		doAssertDouble("x.Z does not equal 10f", 10f, x.z);

		Vector3 y = Vector3.create(x);
		doAssertDouble("y.X does not equal 8f", 8f, y.x);
		doAssertDouble("y.Y does not equal 9f", 9f, y.y);
		doAssertDouble("x.Z does not equal 10f", 10f, x.z);

		x = Vector3.create();
		doAssertDouble("x.X does not equal 0", 0, x.x);
		doAssertDouble("x.Y does not equal 0", 0, x.y);
		doAssertDouble("x.Z does not equal 0", 0, x.z);
	}

	@Test
	public void testAddVector3() {
		Vector3 a = Vector3.create(1, -1, 3);
		Vector3 b = Vector3.create(2, 6, 4);
		Vector3 c = a.add(b);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
		doAssertDouble("x.Z does not equal 7", 7, c.z);
	}

	@Test
	public void testAddFloat() {
		Vector3 a = Vector3.create(1, -1, 4);
		Vector3 c = a.add(2.0F, 6.0F, 8.0F);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
		doAssertDouble("x.Z does not equal 12", 12, c.z);
	}

	@Test
	public void testAddDouble() {
		Vector3 a = Vector3.create(1, -1, -3);
		Vector3 c = a.add(2.0D, 6.0D, 3.0D);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
		doAssertDouble("x.Z does not equal 0", 0, c.z);
	}

	@Test
	public void testAddInt() {
		Vector3 a = Vector3.create(1, -1, 2);
		Vector3 c = a.add(2, 6, 3);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
		doAssertDouble("x.Z does not equal 5", 5, c.z);
	}

	@Test
	public void testSubtractVector3() {
		Vector3 a = Vector3.create(1, -1, 4);
		Vector3 b = Vector3.create(2, 6, 2);
		Vector3 c = a.subtract(b);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
		doAssertDouble("x.Z does not equal 2", 2, c.z);
	}

	@Test
	public void testSubtractFloat() {
		Vector3 a = Vector3.create(1, -1, 1);
		Vector3 c = a.subtract(2.0F, 6.0F, 1.0F);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
		doAssertDouble("x.Z does not equal 0", 0, c.z);
	}

	@Test
	public void testSubtractDouble() {
		Vector3 a = Vector3.create(1, -1, 19.5);
		Vector3 c = a.subtract(2.0D, 6.0D, 18.5D);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
		doAssertDouble("x.Z does not equal 1", 1, c.z);
	}

	@Test
	public void testSubtractInt() {
		Vector3 a = Vector3.create(1, -1, 4);
		Vector3 c = a.subtract(2, 6, 3);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
		doAssertDouble("x.Z does not equal 1", 1, c.z);
	}

	@Test
	public void testMultiplyVector3() {
		Vector3 a = Vector3.create(1, -1, 3);
		Vector3 b = Vector3.create(2, 6, -3);
		Vector3 c = a.scale(b);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -6", -6, c.y);
		doAssertDouble("x.Z does not equal -9", -9, c.z);
	}

	@Test
	public void testMultiplyFloat() {
		Vector3 a = Vector3.create(1, -1, 3.5);
		Vector3 b = a.multiply(2.0F, 6.0F, 2.0F);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		doAssertDouble("x.Z does not equal 7", 7, b.z);

		Vector3 c = a.scale(2.0F);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -2", -2, c.y);
		doAssertDouble("x.Z does not equal 7", 7, c.z);
	}

	@Test
	public void testMultiplyDouble() {
		Vector3 a = Vector3.create(1, -1, 2);
		Vector3 b = a.multiply(2.0D, 6.0D, 5.1D);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		doAssertDouble("x.Z does not equal 10.2", 10.2, b.z);

		Vector3 c = a.scale(2.0D);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -2", -2, c.y);
		doAssertDouble("x.Z does not equal 4", 4, c.z);
	}

	@Test
	public void testMultiplyInt() {
		Vector3 a = Vector3.create(1, -1, 4);
		Vector3 b = a.multiply(2, 6, 5);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		doAssertDouble("x.Z does not equal 20", 20, b.z);

		Vector3 c = a.scale(2);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -2", -2, c.y);
		doAssertDouble("x.Z does not equal 8", 8, c.z);
	}

	

	@Test
	public void testDot() {
		Vector3 x = Vector3.create(2, 3, 4);
		doAssertDouble("x dot x should be 29", 29, x.dot(x));

		x = Vector3.create(3, 2, 1);
		Vector3 y = Vector3.create(4, -1, -2);
		doAssertDouble("x dot y should be 8", 8, x.dot(y));
	}

	@Test
	public void testToVector2() {
		Vector3 x = Vector3.create(3, 5, 6);
		Vector2 y = new Vector2(3, 6);

		assertTrue(x.toVector2().equals(y));
	}


	@Test
	public void testCross() {
		Vector3 x = Vector3.create(1, 0, 0);
		Vector3 y = Vector3.create(0, 1, 0);
		Vector3 z = x.cross(y);
		testValue(z, 0, 0, 1.f);

		Vector3 a = Vector3.create(2, 5, 3);
		Vector3 b = Vector3.create(3, -1, 8);
		Vector3 c = a.cross(b);
		testValue(c, 43.f, -7.f, -17.f);
	}

	@Test
	public void testCeil() {
		Vector3 x = Vector3.create(1.4, 0.2, 3.4);
		Vector3 y = x.ceil();
		doAssertDouble(2, y.x);
		doAssertDouble(1, y.y);
		doAssertDouble(4, y.z);

		x = Vector3.create(5.5, -3.3, 2.1);
		y = x.ceil();
		doAssertDouble(6, y.x);
		doAssertDouble(-3, y.y);
		doAssertDouble(3, y.z);
	}

	@Test
	public void testFloor() {
		Vector3 x = Vector3.create(1.4, 0.2, 3.4);
		Vector3 y = x.floor();
		doAssertDouble(1, y.x);
		doAssertDouble(0, y.y);
		doAssertDouble(3, y.z);

		x = Vector3.create(5.5, -3.3, 2.1);
		y = x.floor();
		doAssertDouble(5, y.x);
		doAssertDouble(-4, y.y);
		doAssertDouble(2, y.z);
	}

	@Test
	public void testRound() {
		Vector3 x = Vector3.create(1.4, 0.2, 3.4);
		Vector3 y = x.round();
		doAssertDouble(1, y.x);
		doAssertDouble(0, y.y);
		doAssertDouble(3, y.z);

		x = Vector3.create(5.5, -3.3, 2.1);
		y = x.round();
		doAssertDouble(6, y.x);
		doAssertDouble(-3, y.y);
		doAssertDouble(2, y.z);
	}

	@Test
	public void testAbs() {
		Vector3 x = Vector3.create(1.4, 0.2, 3.4);
		Vector3 y = x.abs();
		doAssertDouble(1.4, y.x);
		doAssertDouble(0.2, y.y);
		doAssertDouble(3.4, y.z);

		x = Vector3.create(5.5, -3.3, 2.1);
		y = x.abs();
		doAssertDouble(5.5, y.x);
		doAssertDouble(3.3, y.y);
		doAssertDouble(2.1, y.z);
	}

	@Test
	public void testDistance() {
		Vector3 x = Vector3.create(1.4, Math.sqrt(2), 0.2);
		doAssertDouble(2, x.distance(Vector3.create()));

		x = Vector3.create(5.5, 6.414, -3.3);
		doAssertDouble(9.07, x.distance(Vector3.create()));
	}

	@Test
	public void testPow() {
		Vector3 x = Vector3.create(1.4, 0.2, 3.4);
		Vector3 y = x.pow(3);
		doAssertDouble(2.744, y.x);
		doAssertDouble(0.008, y.y);
		doAssertDouble(39.304, y.z);

		x = Vector3.create(5.5, -3.3, 2.1);
		y = x.pow(2);
		doAssertDouble(30.25, y.x);
		doAssertDouble(10.89, y.y);
		doAssertDouble(4.41, y.z);
	}

	@Test
	public void testLengthSquared() {
		Vector3 x = Vector3.create(3, 5, 4);
		doAssertDouble(50, x.lengthSquared());

		x = Vector3.create(5, 13, 12);
		doAssertDouble(338, x.lengthSquared());
	}

	@Test
	public void testLength() {
		Vector3 x = Vector3.create(3, 5, 4);
		doAssertDouble(7.071, x.length());

		x = Vector3.create(5, 13, 12);
		doAssertDouble(18.385, x.length());
	}

	@Test
	public void testNormalize() {
		Vector3 x = Vector3.create(1, 0, 0);
		doAssertDouble(1, Math.abs(x.normalize().length()));

		Vector3 y = Vector3.create(2, 4, 0);
		doAssertDouble(1, Math.abs(y.normalize().length()));
	}

	@Test
	public void testToArray() {
		Vector3 x = Vector3.create(5, 3, 7);
		float[] r = x.toArray();
		assertArrayEquals(new float[] {5, 3, 7}, r, (float) eps);
		doAssertDouble(5, r[0]);
		doAssertDouble(3, r[1]);
		doAssertDouble(7, r[2]);
	}

	@Test
	public void testCompareTo() {
		Vector3 x = Vector3.create(1, 0, 0);
		Vector3 y = Vector3.create(2, 0, 0);
		assertTrue(x.compareTo(y) < 0);
	}

	@Test
	public void testEquals() {
		Vector3 x = Vector3.create(1, 1, 1);
		Vector3 y = Vector3.create(1, 1, 1);
		Vector3 z = Vector3.create(1, 2, 1);
		assertTrue(x.equals(y));
		assertFalse(x.equals(z));
	}

	@Test
	public void testHashCode() {
		Vector3 x = Vector3.create(5, 27, 2);
		Vector3 y = Vector3.create(5, -3, 3);
		doAssertDouble(1541237003, x.hashCode());
		doAssertDouble(-1591383797, y.hashCode());
	}

	@Test
	public void testToString() {
		Vector3 x = Vector3.create(3, 5, 7.3);
		assertEquals("(3.0, 5.0, 7.3)", x.toString());
	}

	@Test
	public void testMin() {
		Vector3 x = Vector3.create(5, -15, 4);
		Vector3 y = Vector3.create(3, 2, 6);
		assertEquals(Vector3.create(3, -15, 4), Vector3.min(x, y));
	}

	@Test
	public void testMax() {
		Vector3 x = Vector3.create(5, -15, 4);
		Vector3 y = Vector3.create(3, 2, 6);
		assertEquals(Vector3.create(5, 2, 6), Vector3.max(x, y));
	}

	@Test
	public void testRand() {
		for (int i = 0; i < 100; ++i) {
			Vector3 x = Vector3.rand();
			assertTrue(x.x >= -1);
			assertTrue(x.x <= 1);
			assertTrue(x.y >= -1);
			assertTrue(x.y <= 1);
			assertTrue(x.z >= -1);
			assertTrue(x.z <= 1);
		}
	}

	@Test
	public void testTransformVector3Matrix() {
		Vector3 x = Vector3.create(1, 0, 0);
		Vector3 u = x.transform(Matrix.rotateY(90));
		testValue(u, 0, 0, -1);

		Vector3 y = Vector3.create(2, 4, 5);
		Vector3 v = y.transform(Matrix.rotateX(30));
		testValue(v, 2, .9666f, 6.333f);
	}

	@Test
	public void testTransformVector3Quaternion() {
		Vector3 x = Vector3.create(1, 0, 0);
		Vector3 u = x.transform(Quaternion.create(90, Vector3.create(0, 1, 0)));
		testValue(u, 0, 0, -1);

		Vector3 y = Vector3.create(2, 4, 5);
		Vector3 v = y.transform(Quaternion.create(30, Vector3.create(1, 0, 0)));
		testValue(v, 2, .964f, 6.328f);
	}
}
