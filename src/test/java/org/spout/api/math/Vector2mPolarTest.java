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
public class Vector2mPolarTest {
	public static final double eps = 0.001;

	private void doAssertDouble(String message, double expect, double got) {
		assertEquals(message, expect, got, eps);
	}

	private void doAssertDouble(double expect, double got) {
		assertEquals(expect, got, eps);
	}

	@Test
	public void testSetValues() {
		Vector2mPolar x = new Vector2mPolar(1, 0);
		doAssertDouble(x.r, 1);
		doAssertDouble(x.theta, 0);

		x.setR(5);
		x.setTheta(7);
		doAssertDouble(x.r, 5);
		doAssertDouble(x.theta, Vector2Polar.getRealAngle(7));
	}

	@Test
	public void testAddVector2mPolar() {
		Vector2Polar a = new Vector2mPolar(1, 0);
		Vector2Polar b = new Vector2mPolar(2, Math.PI * (2.0/3.0) );

		Vector2Polar c = a.add(b);

		doAssertDouble("x.r does not equal Math.sqrt(3)", Math.sqrt(3), c.r);
		doAssertDouble("x.theta does not equal 90 degrees", Math.PI / 2.0, c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testAddFloat() {
		Vector2Polar a = new Vector2mPolar(1, 0);
		Vector2Polar c = a.add(2.0F, (float) Math.PI * (2.0F/3.0F));

		doAssertDouble("x.r does not equal Math.sqrt(3)", Math.sqrt(3), c.r);
		doAssertDouble("x.theta does not equal 90 degrees", Math.PI / 2.0, c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testAddDouble() {
		Vector2Polar a = new Vector2mPolar(1, 0);
		Vector2Polar c = a.add(2.0D, Math.PI * (2.0d/3.0d));

		doAssertDouble("x.r does not equal Math.sqrt(3)", Math.sqrt(3), c.r);
		doAssertDouble("x.theta does not equal 90 degrees", Math.PI / 2.0, c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testAddInt() {
		Vector2Polar a = new Vector2mPolar(1, 0);
		Vector2Polar c = a.add(2, 2);

		doAssertDouble("x.r does not equal ~Math.sqrt(3)", 1.826, c.r);
		doAssertDouble("x.theta does not equal ~90 degrees", Math.toRadians(84.7), c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractVector2mPolar() {
		Vector2Polar a = new Vector2mPolar(1, 0);
		Vector2Polar b = new Vector2mPolar(2, Math.PI * (2.0/3.0) );

		Vector2Polar c = a.subtract(b);

		doAssertDouble("x.r does not equal Math.sqrt(7)", Math.sqrt(7), c.r);
		doAssertDouble("x.theta does not equal the right angle", Vector2Polar.getRealAngle((float)-Math.atan(Math.sqrt(3)*0.5)), c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractFloat() {
		Vector2Polar a = new Vector2mPolar(1, 0);
		Vector2Polar c = a.subtract(2f, (float) Math.PI * (2.0/3.0) );

		doAssertDouble("x.r does not equal Math.sqrt(7)", Math.sqrt(7), c.r);
		doAssertDouble("x.theta does not equal the right angle", Vector2Polar.getRealAngle((float)-Math.atan(Math.sqrt(3)*0.5)), c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractDouble() {
		Vector2Polar a = new Vector2mPolar(1, 0);
		Vector2Polar c = a.subtract(2d, Math.PI * (2.0/3.0) );

		doAssertDouble("x.r does not equal Math.sqrt(7)", Math.sqrt(7), c.r);
		doAssertDouble("x.theta does not equal the right angle", Vector2Polar.getRealAngle((float)-Math.atan(Math.sqrt(3)*0.5)), c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testSubtractInt() {
		Vector2Polar a = new Vector2mPolar(1, 0);
		Vector2Polar c = a.subtract(2, 2);

		doAssertDouble("x.r does not equal ~Math.sqrt(7)", 2.581, c.r);
		doAssertDouble("x.theta does not equal ~the right angle", Math.toRadians(315.215), c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyVector2mPolar() {
		Vector2Polar a = new Vector2mPolar(1, -1);
		Vector2Polar b = new Vector2mPolar(2, 6);
		Vector2Polar c = a.multiply(b);

		doAssertDouble("x.r does not equal 2", 2, c.r);
		doAssertDouble("x.theta does not equal 5", 5, c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyFloat() {
		Vector2Polar a = new Vector2mPolar(1, -1);
		Vector2Polar b = a.multiply(2f);

		doAssertDouble("b.r does not equal 2", 2, b.r);
		doAssertDouble("b.theta does not equal -1", Vector2Polar.getRealAngle(-1), b.theta);
		assertTrue("b is not identical to a", b == a);

		Vector2Polar c = a.multiply(2f, 6f);

		doAssertDouble("c.r does not equal 4", 4, c.r);
		doAssertDouble("c.theta does not equal 5", 5, c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyDouble() {
		Vector2Polar a = new Vector2mPolar(1, -1);
		Vector2Polar b = a.multiply(2d);

		doAssertDouble("b.r does not equal 2", 2, b.r);
		doAssertDouble("b.theta does not equal -1", Vector2Polar.getRealAngle(-1), b.theta);
		assertTrue("b is not identical to a", b == a);

		Vector2Polar c = a.multiply(2d, 6d);

		doAssertDouble("c.r does not equal 4", 4, c.r);
		doAssertDouble("c.theta does not equal 5", 5, c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testMultiplyInt() {
		Vector2Polar a = new Vector2mPolar(1, -1);
		Vector2Polar b = a.multiply(2);

		doAssertDouble("b.r does not equal 2", 2, b.r);
		doAssertDouble("b.theta does not equal -1", Vector2Polar.getRealAngle(-1), b.theta);
		assertTrue("b is not identical to a", b == a);

		Vector2Polar c = a.multiply(2, 6);

		doAssertDouble("c.r does not equal 4", 4, c.r);
		doAssertDouble("c.theta does not equal 5", 5, c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideVector2mPolar() {
		Vector2Polar a = new Vector2mPolar(4, -1);
		Vector2Polar b = new Vector2mPolar(2, 6);
		Vector2Polar c = a.divide(b);

		doAssertDouble("x.r does not equal 2", 2, c.r);
		doAssertDouble("x.theta does not equal -7", Vector2Polar.getRealAngle(-7), c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideFloat() {
		Vector2Polar a = new Vector2mPolar(4, -1);
		Vector2Polar b = a.divide(2f);

		doAssertDouble("b.r does not equal 2", 2, b.r);
		doAssertDouble("b.theta does not equal -1", Vector2Polar.getRealAngle(-1), b.theta);
		assertTrue("b is not identical to a", b == a);

		Vector2Polar c = a.divide(2f, 6f);

		doAssertDouble("c.r does not equal 1", 1, c.r);
		doAssertDouble("c.theta does not equal -7", Vector2Polar.getRealAngle(-7), c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideDouble() {
		Vector2Polar a = new Vector2mPolar(4, -1);
		Vector2Polar b = a.divide(2d);

		doAssertDouble("b.r does not equal 2", 2, b.r);
		doAssertDouble("b.theta does not equal -1", Vector2Polar.getRealAngle(-1), b.theta);
		assertTrue("b is not identical to a", b == a);

		Vector2Polar c = a.divide(2d, 6d);

		doAssertDouble("c.r does not equal 1", 1, c.r);
		doAssertDouble("c.theta does not equal -7", Vector2Polar.getRealAngle(-7), c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testDivideInt() {
		Vector2Polar a = new Vector2mPolar(4, -1);
		Vector2Polar b = a.divide(2);

		doAssertDouble("b.r does not equal 2", 2, b.r);
		doAssertDouble("b.theta does not equal -1", Vector2Polar.getRealAngle(-1), b.theta);
		assertTrue("b is not identical to a", b == a);

		Vector2Polar c = a.divide(2, 6);

		doAssertDouble("c.r does not equal 1", 1, c.r);
		doAssertDouble("c.theta does not equal -7", Vector2Polar.getRealAngle(-7), c.theta);
		assertTrue("c is not identical to a", c == a);
	}

	@Test
	public void testCeil() {
		Vector2Polar x = new Vector2mPolar(1.4, 0.2);
		Vector2Polar y = x.ceil();
		doAssertDouble(2, y.r);
		doAssertDouble(1, y.theta);
		assertTrue("y is not identical to x", y == x);

		x = new Vector2mPolar(5.5, -3.3);
		y = x.ceil();
		doAssertDouble(6, y.r);
		doAssertDouble(3, y.theta);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testFloor() {
		Vector2Polar x = new Vector2mPolar(1.4, 0.2);
		Vector2Polar y = x.floor();
		doAssertDouble(1, y.r);
		doAssertDouble(0, y.theta);
		assertTrue("y is not identical to x", y == x);

		x = new Vector2mPolar(5.5, -3.3);
		y = x.floor();
		doAssertDouble(5, y.r);
		doAssertDouble(2, y.theta);
		assertTrue("y is not identical to x", y == x);
	}

	@Test
	public void testRound() {
		Vector2Polar x = new Vector2mPolar(1.4, 0.2);
		Vector2Polar y = x.round();
		doAssertDouble(1, y.r);
		doAssertDouble(0, y.theta);
		assertTrue("y is not identical to x", y == x);

		x = new Vector2mPolar(5.5, -3.3);
		y = x.round();
		doAssertDouble(6, y.r);
		doAssertDouble(3, y.theta);
		assertTrue("y is not identical to x", y == x);
	}
}
