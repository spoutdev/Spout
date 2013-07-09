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
package org.spout.math.test.vector;

import org.junit.Assert;
import org.junit.Test;

import org.spout.math.TrigMath;
import org.spout.math.test.TestUtil;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;
import org.spout.math.vector.Vector4;
import org.spout.math.vector.VectorN;

public class Vector4Test {
	@Test
	public void testDefaultConstructor() {
		Vector4 vector = new Vector4();
		TestUtil.assertEquals(vector, 0, 0, 0, 0);
	}

	@Test
	public void testCopyVector2DefaultZWConstructor() {
		Vector4 vector = new Vector4(new Vector2(0, 1), 1, 2);
		TestUtil.assertEquals(vector, 0, 1, 1, 2);
	}

	@Test
	public void testCopyVector2FloatZWConstructor() {
		Vector4 vector = new Vector4(new Vector2(0, 1), 1, 2);
		TestUtil.assertEquals(vector, 0, 1, 1, 2);
	}

	@Test
	public void testCopyVector3DefaultWConstructor() {
		Vector4 vector = new Vector4(new Vector3(0, 1, 1), 2);
		TestUtil.assertEquals(vector, 0, 1, 1, 2);
	}

	@Test
	public void testCopyVector3FloatWConstructor() {
		Vector4 vector = new Vector4(new Vector3(0, 1, 1), 2);
		TestUtil.assertEquals(vector, 0, 1, 1, 2);
	}

	@Test
	public void testCopyVector4Constructor() {
		Vector4 vector = new Vector4(new Vector4(0, 1, 1, 2));
		TestUtil.assertEquals(vector, 0, 1, 1, 2);
	}

	@Test
	public void testCopyVectorNConstructor() {
		Vector4 vector = new Vector4(new VectorN(0, 1, 1, 2, 5));
		TestUtil.assertEquals(vector, 0, 1, 1, 2);
	}

	@Test
	public void testDoubleComponentsConstructor() {
		Vector4 vector = new Vector4(0.5, 1.7, 3.8, 5.5);
		TestUtil.assertEquals(vector, 0.5, 1.7, 3.8, 5.5);
	}

	@Test
	public void testFloatComponentsConstructor() {
		Vector4 vector = new Vector4(0.5f, 1.7f, 3.8f, 5.5f);
		TestUtil.assertEquals(vector, 0.5f, 1.7f, 3.8f, 5.5f);
	}

	@Test
	public void testGetters() {
		Vector4 vector = new Vector4(0.5f, 1.7f, 3.8f, 5.5f);
		TestUtil.assertEquals(vector.getX(), 0.5f);
		TestUtil.assertEquals(vector.getY(), 1.7f);
		TestUtil.assertEquals(vector.getZ(), 3.8f);
		TestUtil.assertEquals(vector.getW(), 5.5f);
	}

	@Test
	public void testFloorGetters() {
		Vector4 vector = new Vector4(0.5f, 1.7f, 3.8f, 5.5f);
		TestUtil.assertEquals(vector.getFloorX(), 0);
		TestUtil.assertEquals(vector.getFloorY(), 1);
		TestUtil.assertEquals(vector.getFloorZ(), 3);
		TestUtil.assertEquals(vector.getFloorW(), 5);
	}

	@Test
	public void testVector4Addition() {
		Vector4 vector = new Vector4(0, 1, 1, 1).add(new Vector4(5.5f, -0.5f, 3.8f, 5.5f));
		TestUtil.assertEquals(vector, 5.5f, 0.5f, 4.8f, 6.5f);
	}

	@Test
	public void testDoubleComponentsAddition() {
		Vector4 vector = new Vector4(0, 1, 1, 1).add(5.5, -0.5, 3.8, 5.5);
		TestUtil.assertEquals(vector, 5.5, 0.5, 4.8, 6.5);
	}

	@Test
	public void testFloatComponentsAddition() {
		Vector4 vector = new Vector4(0, 1, 1, 1).add(5.5f, -0.5f, 3.8f, 5.5f);
		TestUtil.assertEquals(vector, 5.5f, 0.5f, 4.8f, 6.5f);
	}

	@Test
	public void testVector4Subtraction() {
		Vector4 vector = new Vector4(10, 5, 1, 1).sub(new Vector4(9, 4.5, 2, 1));
		TestUtil.assertEquals(vector, 1, 0.5, -1, 0);
	}

	@Test
	public void testDoubleComponentsSubtraction() {
		Vector4 vector = new Vector4(10, 5, 1, 1).sub(9, 4.5, 2, 1);
		TestUtil.assertEquals(vector, 1, 0.5, -1, 0);
	}

	@Test
	public void testFloatComponentsSubtraction() {
		Vector4 vector = new Vector4(10, 5, 1, 1).sub(9, 4.5f, 2f, 1f);
		TestUtil.assertEquals(vector, 1, 0.5f, -1, 0);
	}

	@Test
	public void testDoubleFactorMultiplication() {
		Vector4 vector = new Vector4(2, 3, 4, 5).mul(1.5);
		TestUtil.assertEquals(vector, 3, 4.5, 6, 7.5);
	}

	@Test
	public void testFloatFactorMultiplication() {
		Vector4 vector = new Vector4(2, 3, 4, 5).mul(1.5f);
		TestUtil.assertEquals(vector, 3, 4.5f, 6, 7.5f);
	}

	@Test
	public void testVector4Multiplication() {
		Vector4 vector = new Vector4(2, 3, 4, 5).mul(new Vector4(1.5f, 2.5f, 3.5f, 4.5f));
		TestUtil.assertEquals(vector, 3, 7.5f, 14, 22.5f);
	}

	@Test
	public void testDoubleComponentsMultiplication() {
		Vector4 vector = new Vector4(2, 3, 4, 5).mul(2d);
		TestUtil.assertEquals(vector, 4d, 6d, 8d, 10d);
	}

	@Test
	public void testFloatComponentsMultiplication() {
		Vector4 vector = new Vector4(2, 3, 4, 5).mul(2);
		TestUtil.assertEquals(vector, 4, 6, 8, 10);
	}

	@Test
	public void testDoubleFactorDivision() {
		Vector4 vector = new Vector4(2, 3, 4, 5).div(2d);
		TestUtil.assertEquals(vector, 1, 1.5, 2, 2.5);
	}

	@Test
	public void testFloatFactorDivision() {
		Vector4 vector = new Vector4(2, 3, 4, 5).div(2);
		TestUtil.assertEquals(vector, 1, 1.5f, 2, 2.5f);
	}

	@Test
	public void testVector4Division() {
		Vector4 vector = new Vector4(2, 6, 12, 16).div(new Vector4(2, 4, 8, 8));
		TestUtil.assertEquals(vector, 1, 1.5f, 1.5f, 2f);
	}

	@Test
	public void testDoubleComponentsDivision() {
		Vector4 vector = new Vector4(2, 6, 16, 18).div(2d, 4d, 8d, 9d);
		TestUtil.assertEquals(vector, 1, 1.5, 2, 2);
	}

	@Test
	public void testFloatComponentsDivision() {
		Vector4 vector = new Vector4(2, 6, 16, 18).div(2, 4, 8, 9);
		TestUtil.assertEquals(vector, 1, 1.5f, 2, 2);
	}

	@Test
	public void testVector4DotProduct() {
		float f = new Vector4(2, 3, 4, 5).dot(new Vector4(6, 7, 8, 9));
		TestUtil.assertEquals(f, 110);
	}

	@Test
	public void testDoubleComponentsDotProduct() {
		float f = new Vector4(2, 3, 4, 5).dot(6d, 7d, 8d, 9d);
		TestUtil.assertEquals(f, 110d);
	}

	@Test
	public void testFloatComponentsDotProduct() {
		float f = new Vector4(2, 3, 4, 5).dot(6, 7, 8, 9);
		TestUtil.assertEquals(f, 110);
	}

	@Test
	public void testRaiseToFloatPower() {
		Vector4 vector = new Vector4(2, 6, 8, 5.5f).pow(2);
		TestUtil.assertEquals(vector, 4, 36, 64, 30.25f);
	}

	@Test
	public void testRaiseToDoublePower() {
		Vector4 vector = new Vector4(2, 6, 8, 5.5f).pow(2d);
		TestUtil.assertEquals(vector, 4, 36, 64, 30.25);
	}

	@Test
	public void testCeiling() {
		Vector4 vector = new Vector4(2.5f, 6.7f, 7.9f, 8.1f).ceil();
		TestUtil.assertEquals(vector, 3, 7, 8, 9);
	}

	@Test
	public void testFloor() {
		Vector4 vector = new Vector4(2.5f, 6.7f, 7.8f, 9.1f).floor();
		TestUtil.assertEquals(vector, 2, 6, 7, 9);
	}

	@Test
	public void testRound() {
		Vector4 vector = new Vector4(2.2f, 6.7f, 7.8f, 9.1f).round();
		TestUtil.assertEquals(vector, 2, 7, 8, 9);
	}

	@Test
	public void testAbsolute() {
		Vector4 vector1 = new Vector4(-2.5f, -6.7f, -55, 0).abs();
		TestUtil.assertEquals(vector1, 2.5f, 6.7f, 55, 0);
		Vector4 vector2 = new Vector4(2.5f, 6.7f, 55, 0).abs();
		TestUtil.assertEquals(vector2, 2.5f, 6.7f, 55, 0);
	}

	@Test
	public void testNegate() {
		Vector4 vector = new Vector4(2.2f, -6.7f, 15.8f, 20).negate();
		TestUtil.assertEquals(vector, -2.2f, 6.7f, -15.8f, -20);
	}

	@Test
	public void testVector4Minimum() {
		Vector4 vector = new Vector4(2, 6, -1, 0).min(new Vector4(3, 4, 10, -1));
		TestUtil.assertEquals(vector, 2, 4, -1, -1);
	}

	@Test
	public void testDoubleComponentsMinimum() {
		Vector4 vector = new Vector4(2, 6, -1, 0).min(3, 4, 10, -1.1);
		TestUtil.assertEquals(vector, 2, 4, -1, -1.1);
	}

	@Test
	public void testFloatComponentsMinimum() {
		Vector4 vector = new Vector4(2, 6, -1, 0).min(3, 4, 10, -1.1f);
		TestUtil.assertEquals(vector, 2, 4, -1, -1.1f);
	}

	@Test
	public void testVector4Maximum() {
		Vector4 vector = new Vector4(2, 6, -1, 0).max(new Vector4(3, 4, 10, -1));
		TestUtil.assertEquals(vector, 3, 6, 10, 0);
	}

	@Test
	public void testDoubleComponentsMaximum() {
		Vector4 vector = new Vector4(2, 6, -1, 0).max(3, 4, 10, -1.1);
		TestUtil.assertEquals(vector, 3, 6, 10, 0);
	}

	@Test
	public void testFloatComponentsMaximum() {
		Vector4 vector = new Vector4(2, 6, -1, 0).max(3, 4, 10, -1.1f);
		TestUtil.assertEquals(vector, 3, 6, 10, 0);
	}

	@Test
	public void testVector4DistanceSquared() {
		float f = new Vector3(2, 3, 4).distanceSquared(new Vector3(5, 6, 7));
		TestUtil.assertEquals(f, 27);
	}

	@Test
	public void testDoubleComponentsDistanceSquared() {
		float f = new Vector4(2, 3, 4, 1).distanceSquared(5d, 6d, 7d, 1d);
		TestUtil.assertEquals(f, 27d);
	}

	@Test
	public void testFloatComponentsDistanceSquared() {
		float f = new Vector4(2, 3, 4, 5).distanceSquared(5, 6, 7, 5);
		TestUtil.assertEquals(f, 27);
	}

	@Test
	public void testVector4Distance() {
		float f = new Vector4(0, 2, 4, 8).distance(new Vector4(0, 8, 16, 8));
		TestUtil.assertEquals(f, 13.416407585144043);
	}

	@Test
	public void testDoubleComponentsDistance() {
		float f = new Vector4(0, 2, 4, 8).distance(new Vector4(0d, 8d, 16d, 8d));
		TestUtil.assertEquals(f, 13.416407585144043);
	}

	@Test
	public void testFloatComponentsDistance() {
		float f = new Vector4(0, 2, 4, 8).distance(new Vector4(0, 8, 16, 8));
		TestUtil.assertEquals(f, 13.416407585144043f);
	}

	@Test
	public void testLength() {
		float f = new Vector4(3, 4, 5, 6).length();
		TestUtil.assertEquals(f, 9.273618698120117f);
	}

	@Test
	public void testLengthSquared() {
		float f = new Vector4(3, 4, 5, 6).lengthSquared();
		TestUtil.assertEquals(f, 86f);
	}

	@Test
	public void testNormalize() {
		Vector4 v1 = new Vector4(1, 1, 0, 0).normalize();
		TestUtil.assertEquals(v1, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0);
		Vector4 v2 = new Vector4(0, 1, 0, 1).normalize();
		TestUtil.assertEquals(v2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testConvertToVector2() {
		Vector2 vector = new Vector4(1, 2, 3, 4).toVector2();
		TestUtil.assertEquals(vector, 1, 2);
	}

	@Test
	public void testConvertToVector3() {
		Vector3 vector = new Vector4(1, 2, 3, 4).toVector3();
		TestUtil.assertEquals(vector, 1, 2, 3);
	}

	@Test
	public void testConvertToVectorN() {
		VectorN vector = new Vector4(1, 2, 3, 4).toVectorN();
		TestUtil.assertEquals(vector, 1, 2, 3, 4);
	}

	@Test
	public void testConvertToArray() {
		float[] array = new Vector4(1, 2, 3, 4).toArray();
		TestUtil.assertEquals(array, 1, 2, 3, 4);
	}

	@Test
	public void testComparison() {
		int c1 = new Vector4(10, 20, 30, 40).compareTo(new Vector4(20, 20, 30, 40));
		Assert.assertTrue(c1 < 0);
		int c2 = new Vector4(10, 20, 30, 40).compareTo(new Vector4(10, 20, 30, 40));
		Assert.assertTrue(c2 == 0);
		int c3 = new Vector4(10, 20, 30, 40).compareTo(new Vector4(10, 10, 30, 40));
		Assert.assertTrue(c3 > 0);
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(new Vector4(122, 43, 96, 50).equals(new Vector4(122, 43, 96, 50)));
		Assert.assertFalse(new Vector4(122, 43, 96, 50).equals(new Vector4(378, 95, 96, 0)));
	}

	@Test
	public void testCloning() {
		Vector4 vector = new Vector4(3, 2, 5, 6);
		Assert.assertEquals(vector, vector.clone());
	}
}
