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
public class Vector4mTest {
	@Test
	public void testSetValues() {
		Vector4m x = new Vector4m(0, 4, 3, 4);
		doAssertDouble(x.x, 0);
		doAssertDouble(x.y, 4);
		doAssertDouble(x.z, 3);
		doAssertDouble(x.w, 4);

		x.setX(5);
		x.setY(7);
		x.setZ(6);
		x.setW(0);
		doAssertDouble(x.x, 5);
		doAssertDouble(x.y, 7);
		doAssertDouble(x.z, 6);
		doAssertDouble(x.w, 0);
	}

	@Test
	public void testAddVector4m() {
		Vector4 a = new Vector4m(1, -1, 3, 5);
		Vector4 b = new Vector4m(2, 6, -2, 5);
		Vector4 c = a.add(b);
		System.out.println(c);
		doAssertDouble(3, c.x);
		doAssertDouble(5, c.y);
		doAssertDouble(1, c.z);
		doAssertDouble(10, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testAddFloat() {
		Vector4 a = new Vector4m(1, -1, 3, 5);
		Vector4 c = a.add(2f, 6f, -2f, 5f);

		doAssertDouble(3, c.x);
		doAssertDouble(5, c.y);
		doAssertDouble(1, c.z);
		doAssertDouble(10, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testAddDouble() {
		Vector4 a = new Vector4m(1, -1, 3, 5);
		Vector4 c = a.add(2d, 6d, -2d, 5d);

		doAssertDouble(3, c.x);
		doAssertDouble(5, c.y);
		doAssertDouble(1, c.z);
		doAssertDouble(10, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testAddInt() {
		Vector4 a = new Vector4m(1, -1, 3, 5);
		Vector4 c = a.add(2, 6, -2, 5);

		doAssertDouble(3, c.x);
		doAssertDouble(5, c.y);
		doAssertDouble(1, c.z);
		doAssertDouble(10, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractVector4m() {
		Vector4 a = new Vector4m(1, -1, 4, -2);
		Vector4 b = new Vector4m(2, 6, 4, 1);
		Vector4 c = a.subtract(b);

		doAssertDouble(-1, c.x);
		doAssertDouble(-7, c.y);
		doAssertDouble(0, c.z);
		doAssertDouble(-3, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractFloat() {
		Vector4 a = new Vector4m(1, -1, 4, -2);
		Vector4 c = a.subtract(2f, 6f, 4f, 1f);

		doAssertDouble(-1, c.x);
		doAssertDouble(-7, c.y);
		doAssertDouble(0, c.z);
		doAssertDouble(-3, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractDouble() {
		Vector4 a = new Vector4m(1, -1, 4, -2);
		Vector4 c = a.subtract(2d, 6d, 4d, 1d);

		doAssertDouble(-1, c.x);
		doAssertDouble(-7, c.y);
		doAssertDouble(0, c.z);
		doAssertDouble(-3, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractInt() {
		Vector4 a = new Vector4m(1, -1, 4, -2);
		Vector4 c = a.subtract(2, 6, 4, 1);

		doAssertDouble(-1, c.x);
		doAssertDouble(-7, c.y);
		doAssertDouble(0, c.z);
		doAssertDouble(-3, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyVector4m() {
		Vector4 a = new Vector4m(1, -1, 4, 2);
		Vector4 b = new Vector4m(2, 6, 4, 1);
		Vector4 c = a.multiply(b);

		doAssertDouble(2, c.x);
		doAssertDouble(-6, c.y);
		doAssertDouble(16, c.z);
		doAssertDouble(2, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyFloat() {
		Vector4 a = new Vector4m(1, -1, 4, 2);
		Vector4 b = a.multiply(2f, 6f, 4f, 1f);

		doAssertDouble(2, b.x);
		doAssertDouble(-6, b.y);
		doAssertDouble(16, b.z);
		doAssertDouble(2, b.w);
		assertTrue("b is not identical to a", b == a);

		Vector4 c = a.multiply(2.0F);

		doAssertDouble(4, c.x);
		doAssertDouble(-12, c.y);
		doAssertDouble(32, c.z);
		doAssertDouble(4, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyDouble() {
		Vector4 a = new Vector4m(1, -1, 4, 2);
		Vector4 b = a.multiply(2d, 6d, 4d, 1d);

		doAssertDouble(2, b.x);
		doAssertDouble(-6, b.y);
		doAssertDouble(16, b.z);
		doAssertDouble(2, b.w);
		assertTrue("b is not identical to a", b == a);

		Vector4 c = a.multiply(2.0d);

		doAssertDouble(4, c.x);
		doAssertDouble(-12, c.y);
		doAssertDouble(32, c.z);
		doAssertDouble(4, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyInt() {
		Vector4 a = new Vector4m(1, -1, 4, 2);
		Vector4 b = a.multiply(2, 6, 4, 1);

		doAssertDouble(2, b.x);
		doAssertDouble(-6, b.y);
		doAssertDouble(16, b.z);
		doAssertDouble(2, b.w);
		assertTrue("b is not identical to a", b == a);

		Vector4 c = a.multiply(2.0);

		doAssertDouble(4, c.x);
		doAssertDouble(-12, c.y);
		doAssertDouble(32, c.z);
		doAssertDouble(4, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideVector4m() {
		Vector4 a = new Vector4m(4, -36, 5, 2);
		Vector4 b = new Vector4m(2, 6, 5, -1);
		Vector4 c = a.divide(b);

		doAssertDouble(2, c.x);
		doAssertDouble(-6, c.y);
		doAssertDouble(1, c.z);
		doAssertDouble(-2, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideFloat() {
		Vector4 a = new Vector4m(4, -36, 5, 2);
		Vector4 b = a.divide(2f, 6f, 5f, -1f);

		doAssertDouble(2, b.x);
		doAssertDouble(-6, b.y);
		doAssertDouble(1, b.z);
		doAssertDouble(-2, b.w);
		assertTrue("b is not identical to a", b == a);

		Vector4 c = a.divide(2f);

		doAssertDouble(1, c.x);
		doAssertDouble(-3, c.y);
		doAssertDouble(0.5, c.z);
		doAssertDouble(-1, c.w);
	}

	@Test
	public void testDivideDouble() {
		Vector4 a = new Vector4m(4, -36, 5, 2);
		Vector4 b = a.divide(2d, 6d, 5d, -1d);

		doAssertDouble(2, b.x);
		doAssertDouble(-6, b.y);
		doAssertDouble(1, b.z);
		doAssertDouble(-2, b.w);
		assertTrue("b is not identical to a", b == a);

		Vector4 c = a.divide(2d);

		doAssertDouble(1, c.x);
		doAssertDouble(-3, c.y);
		doAssertDouble(0.5, c.z);
		doAssertDouble(-1, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideInt() {
		Vector4 a = new Vector4m(4, -36, 5, 2);
		Vector4 b = a.divide(2, 6, 5, -1);

		doAssertDouble(2, b.x);
		doAssertDouble(-6, b.y);
		doAssertDouble(1, b.z);
		doAssertDouble(-2, b.w);
		assertTrue("c is not identical to a", b == a);

		Vector4 c = a.divide(2);

		doAssertDouble(1, c.x);
		doAssertDouble(-3, c.y);
		doAssertDouble(0.5, c.z);
		doAssertDouble(-1, c.w);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testCeil() {
		Vector4 x = new Vector4m(1.4, -0.2, -2.4, 3.4);
		Vector4 y = x.ceil();
		doAssertDouble(2, y.x);
		doAssertDouble(0, y.y);
		doAssertDouble(-2, y.z);
		doAssertDouble(4, y.w);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testFloor() {
		Vector4 x = new Vector4m(1.4, -0.2, -2.4, 3.4);
		Vector4 y = x.floor();
		doAssertDouble(1, y.x);
		doAssertDouble(-1, y.y);
		doAssertDouble(-3, y.z);
		doAssertDouble(3, y.w);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testRound() {
		Vector4 x = new Vector4m(1.4, -0.2, -2.4, 3.4);
		Vector4 y = x.round();
		doAssertDouble(1, y.x);
		doAssertDouble(0, y.y);
		doAssertDouble(-2, y.z);
		doAssertDouble(3, y.w);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testAbs() {
		Vector4 x = new Vector4m(1.4, -0.2, -2.4, 3.4);
		Vector4 y = x.abs();
		doAssertDouble(1.4, y.x);
		doAssertDouble(0.2, y.y);
		doAssertDouble(2.4, y.z);
		doAssertDouble(3.4, y.w);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testPow() {
		Vector4 x = new Vector4m(1, 2, 3, 4);
		Vector4 y = x.pow(3);
		doAssertDouble(1, y.x);
		doAssertDouble(8, y.y);
		doAssertDouble(27, y.z);
		doAssertDouble(64, y.w);
		assertTrue("y is not identical to x", y == x);

		x = new Vector4m(1, 2, 3, 4);
		y = x.pow(2);
		doAssertDouble(1, y.x);
		doAssertDouble(4, y.y);
		doAssertDouble(9, y.z);
		doAssertDouble(16, y.w);
		assertTrue("y is not identical to x", y == x);

		x = new Vector4m(25, 16, 9, 4);
		y = x.pow(0.5);
		doAssertDouble(5, y.x);
		doAssertDouble(4, y.y);
		doAssertDouble(3, y.z);
		doAssertDouble(2, y.w);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testNormalize() {
		Vector4 x = new Vector4m(3, 4, 5, 6);
		Vector4 y = x.normalize();
		doAssertDouble(0.323, y.x);
		doAssertDouble(0.431, y.y);
		doAssertDouble(1, y.length());
		assertTrue("y is not identical to x", y == x);
	}
}
