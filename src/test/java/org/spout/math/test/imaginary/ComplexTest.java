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
package org.spout.math.test.imaginary;

import org.junit.Assert;
import org.junit.Test;

import org.spout.math.TrigMath;
import org.spout.math.imaginary.Complex;
import org.spout.math.test.TestUtil;
import org.spout.math.vector.Vector2;

public class ComplexTest {
	@Test
	public void testDefaultConstructor() {
		Complex complex = new Complex();
		TestUtil.assertEquals(complex, 1, 0);
	}

	@Test
	public void testDoubleComponentsConstructor() {
		Complex complex = new Complex(2d, 3d);
		TestUtil.assertEquals(complex, 2d, 3d);
	}

	@Test
	public void testFloatComponentsConstructor() {
		Complex complex = new Complex(2, 3);
		TestUtil.assertEquals(complex, 2, 3);
	}

	@Test
	public void testCopyConstructor() {
		Complex complex = new Complex(new Complex(2, 3));
		TestUtil.assertEquals(complex, 2, 3);
	}

	@Test
	public void testGetters() {
		Complex complex = new Complex(2, 3);
		TestUtil.assertEquals(complex.getX(), 2);
		TestUtil.assertEquals(complex.getY(), 3);
	}

	@Test
	public void testComplexAddition() {
		Complex vector = new Complex(0, 1).add(new Complex(5.5f, -0.5f));
		TestUtil.assertEquals(vector, 5.5f, 0.5f);
	}

	@Test
	public void testDoubleComponentsAddition() {
		Complex complex = new Complex(0, 1).add(5.5, -0.5);
		TestUtil.assertEquals(complex, 5.5, 0.5);
	}

	@Test
	public void testFloatComponentsAddition() {
		Complex complex = new Complex(0, 1).add(5.5f, -0.5f);
		TestUtil.assertEquals(complex, 5.5f, 0.5f);
	}

	@Test
	public void testComplexSubtraction() {
		Complex complex = new Complex(10, 5).sub(new Complex(9f, 4.5f));
		TestUtil.assertEquals(complex, 1, 0.5);
	}

	@Test
	public void testDoubleComponentsSubtraction() {
		Complex complex = new Complex(10, 5).sub(9, 4.5);
		TestUtil.assertEquals(complex, 1, 0.5);
	}

	@Test
	public void testFloatComponentsSubtraction() {
		Complex complex = new Complex(10, 5).sub(9f, 4.5f);
		TestUtil.assertEquals(complex, 1, 0.5f);
	}

	@Test
	public void testDoubleFactorMultiplication() {
		Complex complex = new Complex(2, 3).mul(1.5);
		TestUtil.assertEquals(complex, 3, 4.5);
	}

	@Test
	public void testFloatFactorMultiplication() {
		Complex complex = new Complex(2, 3).mul(1.5f);
		TestUtil.assertEquals(complex, 3, 4.5f);
	}

	@Test
	public void testComplexMultiplication() {
		Complex complex = new Complex(2, 3).mul(new Complex(6, 9));
		TestUtil.assertEquals(complex, -15, 36);
	}

	@Test
	public void testDoubleComponentsMultiplication() {
		Complex complex = new Complex(2, 3).mul(6d, 9d);
		TestUtil.assertEquals(complex, -15d, 36d);
	}

	@Test
	public void testFloatComponentsMultiplication() {
		Complex complex = new Complex(2, 3).mul(6, 9);
		TestUtil.assertEquals(complex, -15, 36);
	}

	@Test
	public void testDoubleFactorDivision() {
		Complex complex = new Complex(2, 3).div(2d);
		TestUtil.assertEquals(complex, 1, 1.5);
	}

	@Test
	public void testFloatFactorDivision() {
		Complex complex = new Complex(2, 3).div(2);
		TestUtil.assertEquals(complex, 1, 1.5f);
	}

	@Test
	public void testComplexDotProduct() {
		float f = new Complex(2, 3).dot(new Complex(4, 5));
		TestUtil.assertEquals(f, 23);
	}

	@Test
	public void testDoubleComponentsDotProduct() {
		float f = new Complex(2, 3).dot(4d, 5d);
		TestUtil.assertEquals(f, 23d);
	}

	@Test
	public void testFloatComponentsDotProduct() {
		float f = new Complex(2, 3).dot(4, 5);
		TestUtil.assertEquals(f, 23);
	}

	@Test
	public void testDirection() {
		Vector2 vector = new Complex(4, 3).getDirection();
		TestUtil.assertEquals(vector, 4, 3);
	}

	@Test
	public void testAngleRadians() {
		float f = new Complex((float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO).getAngleRad();
		TestUtil.assertEquals(f, (float) TrigMath.QUARTER_PI);
	}

	@Test
	public void testAngleDegrees() {
		float f = new Complex((float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO).getAngleDeg();
		TestUtil.assertEquals(f, 45);
	}

	@Test
	public void testConjugate() {
		Complex complex = new Complex(2, 3).conjugate();
		TestUtil.assertEquals(complex, 2, -3);
	}

	@Test
	public void testInvert() {
		Complex complex = new Complex(2, 3).invert();
		TestUtil.assertEquals(complex, 0.1538461538f, -0.2307692307f);
	}

	@Test
	public void testLengthSquared() {
		float f = new Complex(3, 4).lengthSquared();
		TestUtil.assertEquals(f, 25);
	}

	@Test
	public void testLength() {
		float f = new Complex(3, 4).length();
		TestUtil.assertEquals(f, 5);
	}

	@Test
	public void testNormalize() {
		Complex complex = new Complex(3, 4).normalize();
		TestUtil.assertEquals(complex, 0.6f, 0.8f);
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(new Complex(122, 43).equals(new Complex(122, 43)));
		Assert.assertFalse(new Complex(122, 43).equals(new Complex(378, 95)));
	}

	@Test
	public void testComparison() {
		int c1 = new Complex(10, 20).compareTo(new Complex(20, 20));
		Assert.assertTrue(c1 < 0);
		int c2 = new Complex(10, 20).compareTo(new Complex(10, 20));
		Assert.assertTrue(c2 == 0);
		int c3 = new Complex(10, 20).compareTo(new Complex(10, 10));
		Assert.assertTrue(c3 > 0);
	}

	@Test
	public void testCloning() {
		Complex complex = new Complex(3, 2);
		Assert.assertEquals(complex, complex.clone());
	}

	@Test
	public void testCreateFromRotationBetweenTwoVector2() {
		Complex complex = Complex.fromRotationTo(new Vector2(0, 1), new Vector2(-1, 0));
		TestUtil.assertEquals(complex, 0, 1);
	}

	@Test
	public void testCreateFromDoubleAngleDegrees() {
		Complex complex = Complex.fromAngleDeg(45d);
		TestUtil.assertEquals(complex, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromFloatAngleDegrees() {
		Complex complex = Complex.fromAngleDeg(45);
		TestUtil.assertEquals(complex, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromDoubleAngleRadians() {
		Complex complex = Complex.fromAngleRad(TrigMath.QUARTER_PI);
		TestUtil.assertEquals(complex, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromFloatAngleRadians() {
		Complex complex = Complex.fromAngleRad((float) TrigMath.QUARTER_PI);
		TestUtil.assertEquals(complex, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}
}
