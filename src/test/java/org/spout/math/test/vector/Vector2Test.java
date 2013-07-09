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

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import org.spout.math.TrigMath;
import org.spout.math.test.TestUtil;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;
import org.spout.math.vector.Vector4;
import org.spout.math.vector.VectorN;

public class Vector2Test {
	@Test
	public void testDefaultConstructor() {
		Vector2 vector = new Vector2();
		TestUtil.assertEquals(vector, 0, 0);
	}

	@Test
	public void testCopyVector2Constructor() {
		Vector2 vector = new Vector2(new Vector2(0, 1));
		TestUtil.assertEquals(vector, 0, 1);
	}

	@Test
	public void testCopyVector3Constructor() {
		Vector2 vector = new Vector2(new Vector3(0, 1, 2));
		TestUtil.assertEquals(vector, 0, 1);
	}

	@Test
	public void testCopyVector4Constructor() {
		Vector2 vector = new Vector2(new Vector4(0, 1, 2, 3));
		TestUtil.assertEquals(vector, 0, 1);
	}

	@Test
	public void testCopyVectorNConstructor() {
		Vector2 vector = new Vector2(new VectorN(0, 1, 2, 3, 4));
		TestUtil.assertEquals(vector, 0, 1);
	}

	@Test
	public void testDoubleComponentsConstructor() {
		Vector2 vector = new Vector2(0.5, 1.7);
		TestUtil.assertEquals(vector, 0.5, 1.7);
	}

	@Test
	public void testFloatComponentsConstructor() {
		Vector2 vector = new Vector2(0.5f, 1.7f);
		TestUtil.assertEquals(vector, 0.5f, 1.7f);
	}

	@Test
	public void testGetters() {
		Vector2 vector = new Vector2(0.5f, 1.7f);
		TestUtil.assertEquals(vector.getX(), 0.5f);
		TestUtil.assertEquals(vector.getY(), 1.7f);
	}

	@Test
	public void testFloorGetters() {
		Vector2 vector = new Vector2(0.5f, 1.7f);
		TestUtil.assertEquals(vector.getFloorX(), 0);
		TestUtil.assertEquals(vector.getFloorY(), 1);
	}

	@Test
	public void testVector2Addition() {
		Vector2 vector = new Vector2(0, 1).add(new Vector2(5.5f, -0.5f));
		TestUtil.assertEquals(vector, 5.5f, 0.5f);
	}

	@Test
	public void testDoubleComponentsAddition() {
		Vector2 vector = new Vector2(0, 1).add(5.5, -0.5);
		TestUtil.assertEquals(vector, 5.5, 0.5);
	}

	@Test
	public void testFloatComponentsAddition() {
		Vector2 vector = new Vector2(0, 1).add(5.5f, -0.5f);
		TestUtil.assertEquals(vector, 5.5f, 0.5f);
	}

	@Test
	public void testVector2Subtraction() {
		Vector2 vector = new Vector2(10, 5).sub(new Vector2(9f, 4.5f));
		TestUtil.assertEquals(vector, 1, 0.5);
	}

	@Test
	public void testDoubleComponentsSubtraction() {
		Vector2 vector = new Vector2(10, 5).sub(9, 4.5);
		TestUtil.assertEquals(vector, 1, 0.5);
	}

	@Test
	public void testFloatComponentsSubtraction() {
		Vector2 vector = new Vector2(10, 5).sub(9f, 4.5f);
		TestUtil.assertEquals(vector, 1, 0.5f);
	}

	@Test
	public void testDoubleFactorMultiplication() {
		Vector2 vector = new Vector2(2, 3).mul(1.5);
		TestUtil.assertEquals(vector, 3, 4.5);
	}

	@Test
	public void testFloatFactorMultiplication() {
		Vector2 vector = new Vector2(2, 3).mul(1.5f);
		TestUtil.assertEquals(vector, 3, 4.5f);
	}

	@Test
	public void testVector2Multiplication() {
		Vector2 vector = new Vector2(2, 3).mul(new Vector2(1.5f, 2.5f));
		TestUtil.assertEquals(vector, 3, 7.5f);
	}

	@Test
	public void testDoubleComponentsMultiplication() {
		Vector2 vector = new Vector2(2, 3).mul(1.5, 2.5);
		TestUtil.assertEquals(vector, 3, 7.5);
	}

	@Test
	public void testFloatComponentsMultiplication() {
		Vector2 vector = new Vector2(2, 3).mul(1.5f, 2.5f);
		TestUtil.assertEquals(vector, 3, 7.5f);
	}

	@Test
	public void testDoubleFactorDivision() {
		Vector2 vector = new Vector2(2, 3).div(2d);
		TestUtil.assertEquals(vector, 1, 1.5);
	}

	@Test
	public void testFloatFactorDivision() {
		Vector2 vector = new Vector2(2, 3).div(2);
		TestUtil.assertEquals(vector, 1, 1.5f);
	}

	@Test
	public void testVector2Division() {
		Vector2 vector = new Vector2(2, 6).div(new Vector2(2, 4));
		TestUtil.assertEquals(vector, 1, 1.5f);
	}

	@Test
	public void testDoubleComponentsDivision() {
		Vector2 vector = new Vector2(2, 6).div(2d, 4d);
		TestUtil.assertEquals(vector, 1, 1.5);
	}

	@Test
	public void testFloatComponentsDivision() {
		Vector2 vector = new Vector2(2, 6).div(2, 4);
		TestUtil.assertEquals(vector, 1, 1.5f);
	}

	@Test
	public void testVector2DotProduct() {
		float f = new Vector2(2, 3).dot(new Vector2(4, 5));
		TestUtil.assertEquals(f, 23);
	}

	@Test
	public void testDoubleComponentsDotProduct() {
		float f = new Vector2(2, 3).dot(4d, 5d);
		TestUtil.assertEquals(f, 23d);
	}

	@Test
	public void testFloatComponentsDotProduct() {
		float f = new Vector2(2, 3).dot(4, 5);
		TestUtil.assertEquals(f, 23);
	}

	@Test
	public void testRaiseToFloatPower() {
		Vector2 vector = new Vector2(2, 6).pow(2);
		TestUtil.assertEquals(vector, 4, 36);
	}

	@Test
	public void testRaiseToDoublePower() {
		Vector2 vector = new Vector2(2, 6).pow(2d);
		TestUtil.assertEquals(vector, 4d, 36d);
	}

	@Test
	public void testCeiling() {
		Vector2 vector = new Vector2(2.5f, 6.7f).ceil();
		TestUtil.assertEquals(vector, 3, 7);
	}

	@Test
	public void testFloor() {
		Vector2 vector = new Vector2(2.5f, 6.7f).floor();
		TestUtil.assertEquals(vector, 2, 6);
	}

	@Test
	public void testRound() {
		Vector2 vector = new Vector2(2.2f, 6.7f).round();
		TestUtil.assertEquals(vector, 2, 7);
	}

	@Test
	public void testAbsolute() {
		Vector2 vector1 = new Vector2(-2.5f, -6.7f).abs();
		TestUtil.assertEquals(vector1, 2.5f, 6.7f);
		Vector2 vector2 = new Vector2(2.5f, 6.7f).abs();
		TestUtil.assertEquals(vector2, 2.5f, 6.7f);
	}

	@Test
	public void testNegate() {
		Vector2 vector = new Vector2(2.2f, -6.7f).negate();
		TestUtil.assertEquals(vector, -2.2f, 6.7f);
	}

	@Test
	public void testVector2Minimum() {
		Vector2 vector = new Vector2(2, 6).min(new Vector2(3, 4));
		TestUtil.assertEquals(vector, 2, 4);
	}

	@Test
	public void testDoubleComponentsMinimum() {
		Vector2 vector = new Vector2(2, 6).min(3d, 4d);
		TestUtil.assertEquals(vector, 2d, 4d);
	}

	@Test
	public void testFloatComponentsMinimum() {
		Vector2 vector = new Vector2(2, 6).min(3, 4);
		TestUtil.assertEquals(vector, 2, 4);
	}

	@Test
	public void testVector2Maximum() {
		Vector2 vector = new Vector2(2, 6).max(new Vector2(3, 4));
		TestUtil.assertEquals(vector, 3, 6);
	}

	@Test
	public void testDoubleComponentsMaximum() {
		Vector2 vector = new Vector2(2, 6).max(3d, 4d);
		TestUtil.assertEquals(vector, 3d, 6d);
	}

	@Test
	public void testFloatComponentsMaximum() {
		Vector2 vector = new Vector2(2, 6).max(3, 4);
		TestUtil.assertEquals(vector, 3, 6);
	}

	@Test
	public void testVector2DistanceSquared() {
		float f = new Vector2(2, 3).distanceSquared(new Vector2(4, 5));
		TestUtil.assertEquals(f, 8);
	}

	@Test
	public void testDoubleComponentsDistanceSquared() {
		float f = new Vector2(2, 3).distanceSquared(4d, 5d);
		TestUtil.assertEquals(f, 8d);
	}

	@Test
	public void testFloatComponentsDistanceSquared() {
		float f = new Vector2(2, 3).distanceSquared(4f, 5f);
		TestUtil.assertEquals(f, 8);
	}

	@Test
	public void testVector2Distance() {
		float f = new Vector2(4, 6).distance(new Vector2(7, 2));
		TestUtil.assertEquals(f, 5);
	}

	@Test
	public void testDoubleComponentsDistance() {
		float f = new Vector2(4, 6).distance(7d, 2d);
		TestUtil.assertEquals(f, 5d);
	}

	@Test
	public void testFloatComponentsDistance() {
		float f = new Vector2(4, 6).distance(7, 2);
		TestUtil.assertEquals(f, 5);
	}

	@Test
	public void testLength() {
		float f = new Vector2(3, 4).length();
		TestUtil.assertEquals(f, 5);
	}

	@Test
	public void testLengthSquared() {
		float f = new Vector2(3, 4).lengthSquared();
		TestUtil.assertEquals(f, 25);
	}

	@Test
	public void testNormalize() {
		Vector2 vector = new Vector2(2, 2).normalize();
		TestUtil.assertEquals(vector, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testConvertToVector3DefaultZ() {
		Vector3 vector = new Vector2(1, 2).toVector3();
		TestUtil.assertEquals(vector, 1, 2, 0);
	}

	@Test
	public void testConvertToVector3FloatZ() {
		Vector3 vector = new Vector2(1, 2).toVector3(3);
		TestUtil.assertEquals(vector, 1, 2, 3);
	}

	@Test
	public void testConvertToVector3DoubleZ() {
		Vector3 vector = new Vector2(1, 2).toVector3(3d);
		TestUtil.assertEquals(vector, 1d, 2d, 3d);
	}

	@Test
	public void testConvertToVector4DefaultZW() {
		Vector4 vector = new Vector2(1, 2).toVector4();
		TestUtil.assertEquals(vector, 1, 2, 0, 0);
	}

	@Test
	public void testConvertToVector4FloatZW() {
		Vector4 vector = new Vector2(1, 2).toVector4(3, 4);
		TestUtil.assertEquals(vector, 1, 2, 3, 4);
	}

	@Test
	public void testConvertToVector4DoubleZW() {
		Vector4 vector = new Vector2(1, 2).toVector4(3d, 4d);
		TestUtil.assertEquals(vector, 1d, 2d, 3d, 4d);
	}

	@Test
	public void testConvertToVectorN() {
		VectorN vector = new Vector2(1, 2).toVectorN();
		TestUtil.assertEquals(vector, 1, 2);
	}

	@Test
	public void testConvertToArray() {
		float[] array = new Vector2(1, 2).toArray();
		TestUtil.assertEquals(array, 1, 2);
	}

	@Test
	public void testComparison() {
		int c1 = new Vector2(10, 20).compareTo(new Vector2(20, 20));
		Assert.assertTrue(c1 < 0);
		int c2 = new Vector2(10, 20).compareTo(new Vector2(10, 20));
		Assert.assertTrue(c2 == 0);
		int c3 = new Vector2(10, 20).compareTo(new Vector2(10, 10));
		Assert.assertTrue(c3 > 0);
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(new Vector2(122, 43).equals(new Vector2(122, 43)));
		Assert.assertFalse(new Vector2(122, 43).equals(new Vector2(378, 95)));
	}

	@Test
	public void testCloning() {
		Vector2 vector = new Vector2(3, 2);
		Assert.assertEquals(vector, vector.clone());
	}

	@Test
	public void testCreateDirectionFromRandom() {
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			Vector2 vector = Vector2.createRandomDirection(random);
			TestUtil.assertEquals(vector.length(), 1);
		}
	}

	@Test
	public void testCreateDirectionFromFloatAngleRadians() {
		Vector2 vector1 = Vector2.createDirection(0);
		TestUtil.assertEquals(vector1, 1, 0);
		Vector2 vector2 = Vector2.createDirection((float) TrigMath.QUARTER_PI);
		TestUtil.assertEquals(vector2, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
		Vector2 vector3 = Vector2.createDirection((float) TrigMath.HALF_PI);
		TestUtil.assertEquals(vector3, 0, 1);
		Vector2 vector4 = Vector2.createDirection((float) TrigMath.PI);
		TestUtil.assertEquals(vector4, -1, 0);
		Vector2 vector5 = Vector2.createDirection((float) TrigMath.THREE_PI_HALVES);
		TestUtil.assertEquals(vector5, 0, -1);
		Vector2 vector6 = Vector2.createDirection((float) TrigMath.TWO_PI);
		TestUtil.assertEquals(vector6, 1, 0);
	}
}
