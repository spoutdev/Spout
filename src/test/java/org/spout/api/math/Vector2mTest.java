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
import static org.spout.api.math.TestUtils.*;

/**
 *
 * @author yetanotherx
 */
public class Vector2mTest {
	@Test
	public void testSetValues() {
		Vector2m x = new Vector2m(0, 4);
		doAssertDouble(x.x, 0);
		doAssertDouble(x.y, 4);

		x.setX(5);
		x.setY(7);
		doAssertDouble(x.x, 5);
		doAssertDouble(x.y, 7);
	}
	
	@Test
	public void testAddVector2m() {
		Vector2 a = new Vector2m(1, -1);
		Vector2 b = new Vector2m(2, 6);
		Vector2 c = a.add(b);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testAddFloat() {
		Vector2 a = new Vector2m(1, -1);
		Vector2 c = a.add(2.0F, 6.0F);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testAddDouble() {
		Vector2 a = new Vector2m(1, -1);
		Vector2 c = a.add(2.0D, 6.0D);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testAddInt() {
		Vector2 a = new Vector2m(1, -1);
		Vector2 c = a.add(2, 6);

		doAssertDouble("x.X does not equal 3", 3, c.x);
		doAssertDouble("x.Y does not equal 5", 5, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractVector2m() {
		Vector2 a = new Vector2m(1, -1);
		Vector2 b = new Vector2m(2, 6);
		Vector2 c = a.subtract(b);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractFloat() {
		Vector2 a = new Vector2m(1, -1);
		Vector2 c = a.subtract(2.0F, 6.0F);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractDouble() {
		Vector2 a = new Vector2m(1, -1);
		Vector2 c = a.subtract(2.0D, 6.0D);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractInt() {
		Vector2 a = new Vector2m(1, -1);
		Vector2 c = a.subtract(2, 6);

		doAssertDouble("x.X does not equal -1", -1, c.x);
		doAssertDouble("x.Y does not equal -7", -7, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyVector2m() {
		Vector2 a = new Vector2m(1, -1);
		Vector2 b = new Vector2m(2, 6);
		Vector2 c = a.scale(b);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -6", -6, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyFloat() {
		Vector2 a = new Vector2m(1, -1);
		Vector2 b = a.scale(2.0F, 6.0F);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		assertTrue("b is not identical to a", b == a);

		Vector2 c = a.scale(2.0F);

		doAssertDouble("x.X does not equal 4", 4, c.x);
		doAssertDouble("x.Y does not equal -12", -12, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyDouble() {
		Vector2 a = new Vector2m(1, -1);
		Vector2 b = a.scale(2.0D, 6.0D);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		assertTrue("b is not identical to a", b == a);

		Vector2 c = ((Vector2m)a).scale(2.0D);

		doAssertDouble("x.X does not equal 4", 4, c.x);
		doAssertDouble("x.Y does not equal -12", -12, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyInt() {
		Vector2 a = new Vector2m(1, -1);
		Vector2 b = a.scale(2, 6);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		assertTrue("b is not identical to a", b == a);

		Vector2 c = a.scale(2);

		doAssertDouble("x.X does not equal 4", 4, c.x);
		doAssertDouble("x.Y does not equal -12", -12, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideVector2m() {
		Vector2 a = new Vector2m(4, -36);
		Vector2 b = new Vector2m(2, 6);
		Vector2 c = a.divide(b);

		doAssertDouble("x.X does not equal 2", 2, c.x);
		doAssertDouble("x.Y does not equal -6", -6, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideFloat() {
		Vector2 a = new Vector2m(4, -36);
		Vector2 b = a.divide(2.0F, 6.0F);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		assertTrue("b is not identical to a", b == a);

		Vector2 c = a.divide(2.0F);

		doAssertDouble("x.X does not equal 1", 1, c.x);
		doAssertDouble("x.Y does not equal -3", -3, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideDouble() {
		Vector2 a = new Vector2m(4, -36);
		Vector2 b = a.divide(2.0D, 6.0D);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		assertTrue("b is not identical to a", b == a);

		Vector2 c = a.divide(2.0D);

		doAssertDouble("x.X does not equal 1", 1, c.x);
		doAssertDouble("x.Y does not equal -3", -3, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideInt() {
		Vector2 a = new Vector2m(4, -36);
		Vector2 b = a.divide(2, 6);

		doAssertDouble("x.X does not equal 2", 2, b.x);
		doAssertDouble("x.Y does not equal -6", -6, b.y);
		assertTrue("b is not identical to a", b == a);

		Vector2 c = a.divide(2);

		doAssertDouble("x.X does not equal 1", 1, c.x);
		doAssertDouble("x.Y does not equal -3", -3, c.y);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testCross() {
		Vector2 x = new Vector2m(1, 0);
		Vector2 y = x.cross();
		doAssertDouble(0, y.x);
		doAssertDouble(-1, y.y);
		assertTrue("y is not identical to x", y == x);

		x = new Vector2m(5, -3);
		y = x.cross();
		doAssertDouble(-3, y.x);
		doAssertDouble(-5, y.y);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testCeil() {
		Vector2 x = new Vector2m(1.4, 0.2);
		Vector2 y = x.ceil();
		doAssertDouble(2, y.x);
		doAssertDouble(1, y.y);
		assertTrue("y is not identical to x", y == x);

		x = new Vector2m(5.5, -3.3);
		y = x.ceil();
		doAssertDouble(6, y.x);
		doAssertDouble(-3, y.y);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testFloor() {
		Vector2 x = new Vector2m(1.4, 0.2);
		Vector2 y = x.floor();
		doAssertDouble(1, y.x);
		doAssertDouble(0, y.y);
		assertTrue("y is not identical to x", y == x);

		x = new Vector2m(5.5, -3.3);
		y = x.floor();
		doAssertDouble(5, y.x);
		doAssertDouble(-4, y.y);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testRound() {
		Vector2 x = new Vector2m(1.4, 0.2);
		Vector2 y = x.round();
		doAssertDouble(1, y.x);
		doAssertDouble(0, y.y);
		assertTrue("y is not identical to x", y == x);

		x = new Vector2m(5.5, -3.3);
		y = x.round();
		doAssertDouble(6, y.x);
		doAssertDouble(-3, y.y);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testAbs() {
		Vector2 x = new Vector2m(1.4, 0.2);
		Vector2 y = x.abs();
		doAssertDouble(1.4, y.x);
		doAssertDouble(0.2, y.y);
		assertTrue("y is not identical to x", y == x);

		x = new Vector2m(5.5, -3.3);
		y = x.abs();
		doAssertDouble(5.5, y.x);
		doAssertDouble(3.3, y.y);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testPow() {
		Vector2 x = new Vector2m(1.4, 0.2);
		Vector2 y = x.pow(3);
		doAssertDouble(2.744, y.x);
		doAssertDouble(0.008, y.y);
		assertTrue("y is not identical to x", y == x);

		x = new Vector2m(5.5, -3.3);
		y = x.pow(2);
		doAssertDouble(30.25, y.x);
		doAssertDouble(10.89, y.y);
		assertTrue("y is not identical to x", y == x);

		x = new Vector2m(25, 16);
		y = x.pow(0.5);
		doAssertDouble(5, y.x);
		doAssertDouble(4, y.y);
		assertTrue("y is not identical to x", y == x);
	}
}
