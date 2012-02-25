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
public class Vector2PolarTest {
	public static final double eps = 0.001;

	private void doAssertDouble(String message, double expect, double got) {
		assertEquals(message, expect, got, eps);
	}

	private void doAssertDouble(double expect, double got) {
		assertEquals(expect, got, eps);
	}

	@Test
	public void testUnits() {
		doAssertDouble(1, Vector2Polar.UNIT.r);
		doAssertDouble(0, Vector2Polar.UNIT.theta);

		doAssertDouble(0, Vector2Polar.ZERO.r);
		doAssertDouble(0, Vector2Polar.ZERO.theta);
	}

	@Test
	public void testRealAngle() {
		doAssertDouble(0, Vector2Polar.getRealAngle(0));
		doAssertDouble(Math.PI / 4.0, Vector2Polar.getRealAngle((float)Math.PI / 4.0f));
		doAssertDouble(Math.PI / 4.0, Vector2Polar.getRealAngle((float)Math.toRadians(45)));
		doAssertDouble(Math.PI / 4.0, Vector2Polar.getRealAngle((float)Math.toRadians(405)));
		doAssertDouble(Math.PI * (7.0/4.0), Vector2Polar.getRealAngle((float)Math.toRadians(675)));
		doAssertDouble(Math.PI * (7.0/4.0), Vector2Polar.getRealAngle((float)Math.toRadians(-45)));
	}

	@Test
	public void testConstructors() {
		Vector2Polar x = new Vector2Polar(2f, 3f);
		doAssertDouble("x.r does not equal 2f", 2f, x.r);
		doAssertDouble("x.theta does not equal 3f", 3f, x.theta);

		x = new Vector2Polar(4f, 5f);
		doAssertDouble("x.r does not equal 4f", 4f, x.r);
		doAssertDouble("x.theta does not equal 5f", 5f, x.theta);

		x = new Vector2Polar(3d, 4d);
		doAssertDouble("x.r does not equal 3f", 3f, x.r);
		doAssertDouble("x.theta does not equal 4f", 4f, x.theta);

		x = new Vector2Polar(2, 3);
		doAssertDouble("x.r does not equal 2f", 2f, x.r);
		doAssertDouble("x.theta does not equal 3f", 3f, x.theta);

		Vector2Polar y = new Vector2Polar(x);
		doAssertDouble("y.r does not equal 2f", 2f, y.r);
		doAssertDouble("y.theta does not equal 3f", 3f, y.theta);

		x = new Vector2Polar();
		doAssertDouble("x.r does not equal 0", 0, x.r);
		doAssertDouble("x.theta does not equal 0", 0, x.theta);
	}

	@Test
	public void testAddVector2Polar() {
		Vector2Polar a = new Vector2Polar(1, 0);
		Vector2Polar b = new Vector2Polar(2, Math.PI * (2.0/3.0) );

		Vector2Polar c = a.add(b);

		doAssertDouble("x.r does not equal Math.sqrt(3)", Math.sqrt(3), c.r);
		doAssertDouble("x.theta does not equal 90 degrees", Math.PI / 2.0, c.theta);
	}

	@Test
	public void testAddFloat() {
		Vector2Polar a = new Vector2Polar(1, 0);
		Vector2Polar c = a.add(2.0F, (float) Math.PI * (2.0F/3.0F));

		doAssertDouble("x.r does not equal Math.sqrt(3)", Math.sqrt(3), c.r);
		doAssertDouble("x.theta does not equal 90 degrees", Math.PI / 2.0, c.theta);
	}

	@Test
	public void testAddDouble() {
		Vector2Polar a = new Vector2Polar(1, 0);
		Vector2Polar c = a.add(2.0D, Math.PI * (2.0d/3.0d));

		doAssertDouble("x.r does not equal Math.sqrt(3)", Math.sqrt(3), c.r);
		doAssertDouble("x.theta does not equal 90 degrees", Math.PI / 2.0, c.theta);
	}

	@Test
	public void testAddInt() {
		Vector2Polar a = new Vector2Polar(1, 0);
		Vector2Polar c = a.add(2, 2);

		doAssertDouble("x.r does not equal ~Math.sqrt(3)", 1.826, c.r);
		doAssertDouble("x.theta does not equal ~90 degrees", Math.toRadians(84.7), c.theta);
	}

	@Test
	public void testSubtractVector2Polar() {
		Vector2Polar a = new Vector2Polar(1, 0);
		Vector2Polar b = new Vector2Polar(2, Math.PI * (2.0/3.0) );

		Vector2Polar c = a.subtract(b);

		doAssertDouble("x.r does not equal Math.sqrt(7)", Math.sqrt(7), c.r);
		doAssertDouble("x.theta does not equal the right angle", Vector2Polar.getRealAngle((float)-Math.atan(Math.sqrt(3)*0.5)), c.theta);
	}

	@Test
	public void testSubtractFloat() {
		Vector2Polar a = new Vector2Polar(1, 0);
		Vector2Polar c = a.subtract(2f, (float) Math.PI * (2.0/3.0) );

		doAssertDouble("x.r does not equal Math.sqrt(7)", Math.sqrt(7), c.r);
		doAssertDouble("x.theta does not equal the right angle", Vector2Polar.getRealAngle((float)-Math.atan(Math.sqrt(3)*0.5)), c.theta);
	}

	@Test
	public void testSubtractDouble() {
		Vector2Polar a = new Vector2Polar(1, 0);
		Vector2Polar c = a.subtract(2d, Math.PI * (2.0/3.0) );

		doAssertDouble("x.r does not equal Math.sqrt(7)", Math.sqrt(7), c.r);
		doAssertDouble("x.theta does not equal the right angle", Vector2Polar.getRealAngle((float)-Math.atan(Math.sqrt(3)*0.5)), c.theta);
	}

	@Test
	public void testSubtractInt() {
		Vector2Polar a = new Vector2Polar(1, 0);
		Vector2Polar c = a.subtract(2, 2);

		doAssertDouble("x.r does not equal ~Math.sqrt(7)", 2.581, c.r);
		doAssertDouble("x.theta does not equal ~the right angle", Math.toRadians(315.215), c.theta);
	}

	@Test
	public void testMultiplyVector2Polar() {
		Vector2Polar a = new Vector2Polar(1, -1);
		Vector2Polar b = new Vector2Polar(2, 6);
		Vector2Polar c = a.multiply(b);

		doAssertDouble("x.r does not equal 2", 2, c.r);
		doAssertDouble("x.theta does not equal 5", 5, c.theta);
	}

	@Test
	public void testMultiplyFloat() {
		Vector2Polar a = new Vector2Polar(1, -1);
		Vector2Polar b = a.multiply(2f, 6f);

		doAssertDouble("b.r does not equal 2", 2, b.r);
		doAssertDouble("b.theta does not equal 5", 5, b.theta);

		Vector2Polar c = a.multiply(2f);

		doAssertDouble("c.r does not equal 2", 2, c.r);
		doAssertDouble("c.theta does not equal -1", Vector2Polar.getRealAngle(-1), c.theta);
	}

	@Test
	public void testMultiplyDouble() {
		Vector2Polar a = new Vector2Polar(1, -1);
		Vector2Polar b = a.multiply(2d, 6d);

		doAssertDouble("b.r does not equal 2", 2, b.r);
		doAssertDouble("b.theta does not equal 5", 5, b.theta);

		Vector2Polar c = a.multiply(2d);

		doAssertDouble("c.r does not equal 2", 2, c.r);
		doAssertDouble("c.theta does not equal -1", Vector2Polar.getRealAngle(-1), c.theta);
	}

	@Test
	public void testMultiplyInt() {
		Vector2Polar a = new Vector2Polar(1, -1);
		Vector2Polar b = a.multiply(2, 6);

		doAssertDouble("b.r does not equal 2", 2, b.r);
		doAssertDouble("b.theta does not equal 5", 5, b.theta);

		Vector2Polar c = a.multiply(2);

		doAssertDouble("c.r does not equal 2", 2, c.r);
		doAssertDouble("c.theta does not equal -1", Vector2Polar.getRealAngle(-1), c.theta);
	}

	@Test
	public void testDivideVector2Polar() {
		Vector2Polar a = new Vector2Polar(4, -1);
		Vector2Polar b = new Vector2Polar(2, 6);
		Vector2Polar c = a.divide(b);

		doAssertDouble("x.r does not equal 2", 2, c.r);
		doAssertDouble("x.theta does not equal -7", Vector2Polar.getRealAngle(-7), c.theta);
	}

	@Test
	public void testDivideFloat() {
		Vector2Polar a = new Vector2Polar(4, -1);
		Vector2Polar b = a.divide(2f, 6f);

		doAssertDouble("b.r does not equal 2", 2, b.r);
		doAssertDouble("b.theta does not equal -7", Vector2Polar.getRealAngle(-7), b.theta);

		Vector2Polar c = a.divide(2f);

		doAssertDouble("c.r does not equal 2", 2, c.r);
		doAssertDouble("c.theta does not equal -1", Vector2Polar.getRealAngle(-1), c.theta);
	}

	@Test
	public void testDivideDouble() {
		Vector2Polar a = new Vector2Polar(4, -1);
		Vector2Polar b = a.divide(2d, 6d);

		doAssertDouble("b.r does not equal 2", 2, b.r);
		doAssertDouble("b.theta does not equal -7", Vector2Polar.getRealAngle(-7), b.theta);

		Vector2Polar c = a.divide(2d);

		doAssertDouble("c.r does not equal 2", 2, c.r);
		doAssertDouble("c.theta does not equal -1", Vector2Polar.getRealAngle(-1), c.theta);
	}

	@Test
	public void testDivideInt() {
		Vector2Polar a = new Vector2Polar(4, -1);
		Vector2Polar b = a.divide(2, 6);

		doAssertDouble("b.r does not equal 2", 2, b.r);
		doAssertDouble("b.theta does not equal -7", Vector2Polar.getRealAngle(-7), b.theta);

		Vector2Polar c = a.divide(2);

		doAssertDouble("c.r does not equal 2", 2, c.r);
		doAssertDouble("c.theta does not equal -1", Vector2Polar.getRealAngle(-1), c.theta);
	}

	@Test
	public void testDot() {
		Vector2Polar x = new Vector2Polar(2, 3);
		doAssertDouble("x dot x should be 4", 4, x.dot(x));

		x = new Vector2Polar(3, 2);
		Vector2Polar y = new Vector2Polar(4, -1);
		doAssertDouble("x dot y should be 12cos(3)", 12*Math.cos(3), x.dot(y));
	}

	@Test
	public void testToVector2() {
		Vector2Polar x = new Vector2Polar(5, Math.toRadians(53.13));
		Vector2 y = new Vector2(3, 4);

		assertTrue(x.toVector2().equals(y));
	}

	@Test
	public void testToVector2m() {
		Vector2Polar x = new Vector2Polar(5, Math.toRadians(53.13));
		Vector2m y = new Vector2m(3, 4);

		assertTrue(x.toVector2m().equals(y));
	}

	@Test
	public void testCeil() {
		Vector2Polar x = new Vector2Polar(1.4, 0.2);
		Vector2Polar y = x.ceil();
		doAssertDouble(2, y.r);
		doAssertDouble(1, y.theta);

		x = new Vector2Polar(5.5, -3.3);
		y = x.ceil();
		doAssertDouble(6, y.r);
		doAssertDouble(3, y.theta);
	}

	@Test
	public void testFloor() {
		Vector2Polar x = new Vector2Polar(1.4, 0.2);
		Vector2Polar y = x.floor();
		doAssertDouble(1, y.r);
		doAssertDouble(0, y.theta);

		x = new Vector2Polar(5.5, -3.3);
		y = x.floor();
		doAssertDouble(5, y.r);
		doAssertDouble(2, y.theta);
	}

	@Test
	public void testRound() {
		Vector2Polar x = new Vector2Polar(1.4, 0.2);
		Vector2Polar y = x.round();
		doAssertDouble(1, y.r);
		doAssertDouble(0, y.theta);

		x = new Vector2Polar(5.5, -3.3);
		y = x.round();
		doAssertDouble(6, y.r);
		doAssertDouble(3, y.theta);
	}

	@Test
	public void testDistance() {
		Vector2Polar x = new Vector2Polar(1.4, 0.2);
		doAssertDouble(1.4, x.distance(new Vector2Polar()));

		Vector2Polar y = new Vector2Polar(5.5, -3.3);
		doAssertDouble(Math.sqrt(46.631), x.distance(y));
	}

	@Test
	public void testToArray() {
		Vector2Polar x = new Vector2Polar(5, 3);
		float[] r = x.toArray();
		assertArrayEquals(new float[]{5, 3}, r, (float) eps);
		doAssertDouble(5, r[0]);
		doAssertDouble(3, r[1]);
	}

	@Test
	public void testCompareTo() {
		Vector2Polar x = new Vector2Polar(0, 0);
		Vector2Polar y = new Vector2Polar(1, 1);
		assertTrue(x.compareTo(y) < 0);
		x = new Vector2Polar(5, 3);
		y = new Vector2Polar(-2, 5);
		assertTrue(x.compareTo(y) >= 0);
	}

	@Test
	public void testEquals() {
		Vector2Polar x = new Vector2Polar(1, 1);
		Vector2Polar y = new Vector2Polar(1, 1);
		Vector2Polar z = new Vector2Polar(1, 2);
		assertTrue(x.equals(y));
		assertFalse(x.equals(z));
	}

	@Test
	public void testHashCode() {
		Vector2Polar x = new Vector2Polar(5, 27);
		Vector2Polar y = new Vector2Polar(5, -3);
		System.out.println(x.hashCode());
		System.out.println(y.hashCode());
		doAssertDouble(617563713, x.hashCode());
		doAssertDouble(624059315, y.hashCode());
	}

	@Test
	public void testToString() {
		Vector2Polar x = new Vector2Polar(3, 5);
		assertEquals("(3.0, 5.0 radians)", x.toString());
	}

	@Test
	public void testMin() {
		Vector2Polar x = new Vector2Polar(5, -15); //3.85 radians
		Vector2Polar y = new Vector2Polar(3, 2); //2 radians
		assertEquals(new Vector2Polar(3, 2), Vector2Polar.min(x, y));
	}

	@Test
	public void testMax() {
		Vector2Polar x = new Vector2Polar(5, -15);
		Vector2Polar y = new Vector2Polar(3, 2);
		assertEquals(new Vector2Polar(5, -15), Vector2Polar.max(x, y));
	}

	@Test
	public void testRand() {
		for (int i = 0; i < 100; ++i) {
			Vector2Polar x = Vector2Polar.rand();
			assertTrue(x.r >= -1);
			assertTrue(x.r <= 1);
			assertTrue(x.theta >= 0);
			assertTrue(x.theta <= Math.PI * 2);
		}
	}
}
