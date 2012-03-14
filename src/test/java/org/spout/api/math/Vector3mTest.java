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

import org.junit.Test;
import static org.junit.Assert.*;
import static org.spout.api.math.TestUtils.*;

/**
 *
 * @author yetanotherx
 */
public class Vector3mTest {
	private void testValue(Vector3 v, float x, float y, float z) {
		if (Math.abs(v.getX() - x) >= eps || Math.abs(v.getY() - y) >= eps || Math.abs(v.getZ() - z) >= eps) {
			fail("Test Fail! Expected {" + x + "," + y + "," + z + "} but got " + v);
		}
	}

	@Test
	public void testSetValues() {
		Vector3m x = new Vector3m(0, 4, 5);
		doAssertDouble(x.x, 0);
		doAssertDouble(x.y, 4);
		doAssertDouble(x.z, 5);

		x.setX(5);
		x.setY(7);
		x.setZ(6);
		doAssertDouble(x.x, 5);
		doAssertDouble(x.y, 7);
		doAssertDouble(x.z, 6);
	}

	@Test
	public void testAddVector3m() {
		Vector3 a = new Vector3m(1, -1, 3);
		Vector3 b = new Vector3m(2, 6, 4);
		Vector3 c = a.add(b);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
		doAssertDouble("x.Z does not equal 7", 7, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testAddFloat() {
		Vector3 a = new Vector3m(1, -1, 4);
		Vector3 c = a.add(2.0F, 6.0F, 8.0F);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
		doAssertDouble("x.Z does not equal 12", 12, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testAddDouble() {
		Vector3 a = new Vector3m(1, -1, -3);
		Vector3 c = a.add(2.0D, 6.0D, 3.0D);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
		doAssertDouble("x.Z does not equal 0", 0, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testAddInt() {
		Vector3 a = new Vector3m(1, -1, 2);
		Vector3 c = a.add(2, 6, 3);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
		doAssertDouble("x.Z does not equal 5", 5, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractVector3m() {
		Vector3 a = new Vector3m(1, -1, 4);
		Vector3 b = new Vector3m(2, 6, 2);
		Vector3 c = a.subtract(b);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
		doAssertDouble("x.Z does not equal 2", 2, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractFloat() {
		Vector3 a = new Vector3m(1, -1, 1);
		Vector3 c = a.subtract(2.0F, 6.0F, 1.0F);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
		doAssertDouble("x.Z does not equal 0", 0, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractDouble() {
		Vector3 a = new Vector3m(1, -1, 19.5);
		Vector3 c = a.subtract(2.0D, 6.0D, 18.5D);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
		doAssertDouble("x.Z does not equal 1", 1, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractInt() {
		Vector3 a = new Vector3m(1, -1, 4);
		Vector3 c = a.subtract(2, 6, 3);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
		doAssertDouble("x.Z does not equal 1", 1, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyVector3m() {
		Vector3 a = new Vector3m(1, -1, 3);
		Vector3 b = new Vector3m(2, 6, -3);
		Vector3 c = a.scale(b);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -6", -6, c.y);
		doAssertDouble("x.Z does not equal -9", -9, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyFloat() {
		Vector3 a = new Vector3m(1, -1, 3.5);
		Vector3 b = a.multiply(2.0F, 6.0F, 2.0F);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		doAssertDouble("x.Z does not equal 7", 7, b.z);
		assertTrue("b is not identical to a", b == a);

		Vector3 c = a.scale(2.0F);

		doAssertDouble("x.X does not equal 4", 4, c.x);
		doAssertDouble("x.Y does not equal -12", -12, c.y);
		doAssertDouble("x.Z does not equal 14", 14, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyDouble() {
		Vector3 a = new Vector3m(1, -1, 2);
		Vector3 b = a.multiply(2.0D, 6.0D, 5.1D);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		doAssertDouble("x.Z does not equal 10.2", 10.2, b.z);
		assertTrue("b is not identical to a", b == a);

		Vector3 c = a.scale(2.0D);

		doAssertDouble("x.X does not equal 4", 4, c.x);
		doAssertDouble("x.Y does not equal -12", -12, c.y);
		doAssertDouble("x.Z does not equal 20.4", 20.4, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyInt() {
		Vector3 a = new Vector3m(1, -1, 4);
		Vector3 b = a.multiply(2, 6, 5);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		doAssertDouble("x.Z does not equal 20", 20, b.z);
		assertTrue("b is not identical to a", b == a);

		Vector3 c = a.scale(2);

		doAssertDouble("x.X does not equal 4", 4, c.x);
		doAssertDouble("x.Y does not equal -12", -12, c.y);
		doAssertDouble("x.Z does not equal 40", 40, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideVector3m() {
		Vector3 a = new Vector3m(4, -36, 8);
		Vector3 b = new Vector3m(2, 6, 4);
		Vector3 c = a.divide(b);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -6", -6, c.y);
		doAssertDouble("x.Z does not equal 2", 2, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideFloat() {
		Vector3 a = new Vector3m(4, -36, 8);
		Vector3 b = a.divide(2.0F, 6.0F, 2.5F);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		doAssertDouble("x.Z does not equal 3.2", 3.2, b.z);
		assertTrue("b is not identical to a", b == a);

		Vector3 c = a.divide(2.0F);

		doAssertDouble("x.X does not equal 1", 1, c.x);
		doAssertDouble("x.Y does not equal -3", -3, c.y);
		doAssertDouble("x.Z does not equal 1.6", 1.6, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideDouble() {
		Vector3 a = new Vector3m(4, -36, 3.4);
		Vector3 b = a.divide(2.0D, 6.0D, 2.3D);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		doAssertDouble("x.Z does not equal 1.478", 1.478, b.z);
		assertTrue("b is not identical to a", b == a);

		Vector3 c = a.divide(2.0D);

		doAssertDouble("x.X does not equal 1", 1, c.x);
		doAssertDouble("x.Y does not equal -3", -3, c.y);
		doAssertDouble("x.Z does not equal 0.739", 0.739, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideInt() {
		Vector3 a = new Vector3m(4, -36, 4);
		Vector3 b = a.divide(2, 6, 1);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		doAssertDouble("x.Z does not equal 4", 4, b.z);
		assertTrue("b is not identical to a", b == a);

		Vector3 c = a.divide(2);

		doAssertDouble("x.X does not equal 1", 1, c.x);
		doAssertDouble("x.Y does not equal -3", -3, c.y);
		doAssertDouble("x.Z does not equal 2", 2, c.z);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testCross() {
		Vector3 x = new Vector3m(1, 0, 0);
		Vector3 y = new Vector3m(0, 1, 0);
		Vector3 z = x.cross(y);
		testValue(z, 0, 0, 1.f);
		assertTrue("z is not identical to x", z == x);

		Vector3 a = new Vector3m(2, 5, 3);
		Vector3 b = new Vector3m(3, -1, 8);
		Vector3 c = a.cross(b);
		testValue(c, 43.f, -7.f, -17.f);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testCeil() {
		Vector3 x = new Vector3m(1.4, 0.2, 3.4);
		Vector3 y = x.ceil();
		doAssertDouble(2, y.x);
		doAssertDouble(1, y.y);
		doAssertDouble(4, y.z);
		assertTrue("y is not identical to x", y == x);

		x = new Vector3m(5.5, -3.3, 2.1);
		y = x.ceil();
		doAssertDouble(6, y.x);
		doAssertDouble(-3, y.y);
		doAssertDouble(3, y.z);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testFloor() {
		Vector3 x = new Vector3m(1.4, 0.2, 3.4);
		Vector3 y = x.floor();
		doAssertDouble(1, y.x);
		doAssertDouble(0, y.y);
		doAssertDouble(3, y.z);
		assertTrue("y is not identical to x", y == x);

		x = new Vector3m(5.5, -3.3, 2.1);
		y = x.floor();
		doAssertDouble(5, y.x);
		doAssertDouble(-4, y.y);
		doAssertDouble(2, y.z);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testRound() {
		Vector3 x = new Vector3m(1.4, 0.2, 3.4);
		Vector3 y = x.round();
		doAssertDouble(1, y.x);
		doAssertDouble(0, y.y);
		doAssertDouble(3, y.z);
		assertTrue("y is not identical to x", y == x);

		x = new Vector3m(5.5, -3.3, 2.1);
		y = x.round();
		doAssertDouble(6, y.x);
		doAssertDouble(-3, y.y);
		doAssertDouble(2, y.z);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testAbs() {
		Vector3 x = new Vector3m(1.4, 0.2, 3.4);
		Vector3 y = x.abs();
		doAssertDouble(1.4, y.x);
		doAssertDouble(0.2, y.y);
		doAssertDouble(3.4, y.z);
		assertTrue("y is not identical to x", y == x);

		x = new Vector3m(5.5, -3.3, 2.1);
		y = x.abs();
		doAssertDouble(5.5, y.x);
		doAssertDouble(3.3, y.y);
		doAssertDouble(2.1, y.z);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testPow() {
		Vector3 x = new Vector3m(1.4, 0.2, 3.4);
		Vector3 y = x.pow(3);
		doAssertDouble(2.744, y.x);
		doAssertDouble(0.008, y.y);
		doAssertDouble(39.304, y.z);
		assertTrue("y is not identical to x", y == x);

		x = new Vector3m(5.5, -3.3, 2.1);
		y = x.pow(2);
		doAssertDouble(30.25, y.x);
		doAssertDouble(10.89, y.y);
		doAssertDouble(4.41, y.z);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testTransformVector3Matrix() {
		Vector3 x = new Vector3m(1, 0, 0);
		Vector3 u = x.transform(Matrix.rotateY(90));
		testValue(u, 0, 0, -1);
		assertTrue("u is not identical to x", u == x);

		Vector3 y = new Vector3m(2, 4, 5);
		Vector3 v = y.transform(Matrix.rotateX(30));
		testValue(v, 2, .9666f, 6.333f);
		assertTrue("v is not identical to y", v == y);
	}

	@Test
	public void testTransformVector3Quaternion() {
		Vector3 x = new Vector3m(1, 0, 0);
		Vector3 u = x.transform(new Quaternion(90, new Vector3m(0, 1, 0)));
		testValue(u, 0, 0, -1);
		assertTrue("u is not identical to x", u == x);

		Vector3 y = new Vector3m(2, 4, 5);
		Vector3 v = y.transform(new Quaternion(30, new Vector3m(1, 0, 0)));
		testValue(v, 2, .964f, 6.328f);
		assertTrue("v is not identical to y", v == y);
	}

	@Test
	public void testNormalize() {
		Vector3 x = new Vector3m(1, 0, 0);
		Vector3 y = x.normalize();
		doAssertDouble(1, Math.abs(y.length()));
		assertTrue("y is not identical to x", y == x);

		x = new Vector3m(2, 4, 0);
		y = x.normalize();
		doAssertDouble(1, Math.abs(y.length()));
		assertTrue("y is not identical to x", y == x);
	}
}
