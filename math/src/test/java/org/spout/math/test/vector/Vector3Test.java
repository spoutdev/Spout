/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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

public class Vector3Test {
	@Test
	public void testCopyVector2DefaultZConstructor() {
		Vector3 vector = new Vector3(new Vector2(0, 1));
		TestUtil.assertEquals(vector, 0, 1, 0);
	}

	@Test
	public void testCopyVector2FloatZConstructor() {
		Vector3 vector = new Vector3(new Vector2(0, 1), 3);
		TestUtil.assertEquals(vector, 0, 1, 3);
	}

	@Test
	public void testCopyVector3Constructor() {
		Vector3 vector = new Vector3(new Vector3(0, 1, 2));
		TestUtil.assertEquals(vector, 0, 1, 2);
	}

	@Test
	public void testCopyVector4Constructor() {
		Vector3 vector = new Vector3(new Vector4(0, 1, 2, 3));
		TestUtil.assertEquals(vector, 0, 1, 2);
	}

	@Test
	public void testCopyVectorNConstructor() {
		Vector3 vector = new Vector3(new VectorN(0, 1, 2, 3, 4));
		TestUtil.assertEquals(vector, 0, 1, 2);
	}

	@Test
	public void testDoubleComponentsConstructor() {
		Vector3 vector = new Vector3(0.5, 1.7, 3.8);
		TestUtil.assertEquals(vector, 0.5f, 1.7f, 3.8f);
	}

	@Test
	public void testFloatComponentsConstructor() {
		Vector3 vector = new Vector3(0.5f, 1.7f, 3.8f);
		TestUtil.assertEquals(vector, 0.5f, 1.7f, 3.8f);
	}

	@Test
	public void testGetters() {
		Vector3 vector = new Vector3(0.5f, 1.7f, 3.8f);
		TestUtil.assertEquals(vector.getX(), 0.5f);
		TestUtil.assertEquals(vector.getY(), 1.7f);
		TestUtil.assertEquals(vector.getZ(), 3.8f);
	}

	@Test
	public void testFloorGetters() {
		Vector3 vector = new Vector3(0.5f, 1.7f, 3.8f);
		TestUtil.assertEquals(vector.getFloorX(), 0);
		TestUtil.assertEquals(vector.getFloorY(), 1);
		TestUtil.assertEquals(vector.getFloorZ(), 3);
	}

	@Test
	public void testVector3Addition() {
		Vector3 vector = new Vector3(0, 1, 1).add(new Vector3(5.5f, -0.5f, 3.8f));
		TestUtil.assertEquals(vector, 5.5f, 0.5f, 4.8f);
	}

	@Test
	public void testDoubleComponentsAddition() {
		Vector3 vector = new Vector3(0, 1, 5).add(5.5, -0.5, 3.8);
		TestUtil.assertEquals(vector, 5.5f, 0.5f, 8.8f);
	}

	@Test
	public void testFloatComponentsAddition() {
		Vector3 vector = new Vector3(0, 1, 5).add(5.5f, -0.5f, 3.8f);
		TestUtil.assertEquals(vector, 5.5f, 0.5f, 8.8f);
	}

	@Test
	public void testVector3Subtraction() {
		Vector3 vector = new Vector3(10, 5, 1).sub(new Vector3(9, 4.5, 2));
		TestUtil.assertEquals(vector, 1, 0.5f, -1);
	}

	@Test
	public void testDoubleComponentsSubtraction() {
		Vector3 vector = new Vector3(10, 5, 1).sub(new Vector3(9, 4.5, 2));
		TestUtil.assertEquals(vector, 1, 0.5f, -1);
	}

	@Test
	public void testFloatComponentsSubtraction() {
		Vector3 vector = new Vector3(10, 5, 1).sub(new Vector3(9, 4.5f, 2));
		TestUtil.assertEquals(vector, 1f, 0.5f, -1f);
	}

	@Test
	public void testDoubleFactorMultiplication() {
		Vector3 vector = new Vector3(2, 3, 4).mul(1.5);
		TestUtil.assertEquals(vector, 3, 4.5f, 6);
	}

	@Test
	public void testFloatFactorMultiplication() {
		Vector3 vector = new Vector3(2, 3, 4).mul(1.5f);
		TestUtil.assertEquals(vector, 3, 4.5f, 6);
	}

	@Test
	public void testVector3Multiplication() {
		Vector3 vector = new Vector3(2, 3, 4).mul(new Vector3(1.5f, 2.5f, 3.5f));
		TestUtil.assertEquals(vector, 3, 7.5f, 14);
	}

	@Test
	public void testDoubleComponentsMultiplication() {
		Vector3 vector = new Vector3(2, 3, 4).mul(2d);
		TestUtil.assertEquals(vector, 4, 6, 8);
	}

	@Test
	public void testFloatComponentsMultiplication() {
		Vector3 vector = new Vector3(2, 3, 4).mul(2);
		TestUtil.assertEquals(vector, 4, 6, 8);
	}

	@Test
	public void testDoubleFactorDivision() {
		Vector3 vector = new Vector3(2, 3, 4).div(2d);
		TestUtil.assertEquals(vector, 1, 1.5f, 2);
	}

	@Test
	public void testFloatFactorDivision() {
		Vector3 vector = new Vector3(2, 3, 4).div(2);
		TestUtil.assertEquals(vector, 1, 1.5f, 2);
	}

	@Test
	public void testVector3Division() {
		Vector3 vector = new Vector3(2, 6, 12).div(new Vector3(2, 4, 8));
		TestUtil.assertEquals(vector, 1, 1.5f, 1.5f);
	}

	@Test
	public void testDoubleComponentsDivision() {
		Vector3 vector = new Vector3(2, 6, 16).div(2d, 4d, 8d);
		TestUtil.assertEquals(vector, 1, 1.5f, 2);
	}

	@Test
	public void testFloatComponentsDivision() {
		Vector3 vector = new Vector3(2, 6, 16).div(2, 4, 8);
		TestUtil.assertEquals(vector, 1, 1.5f, 2);
	}

	@Test
	public void testVector3DotProduct() {
		float f = new Vector3(2, 3, 4).dot(new Vector3(4, 5, 6));
		TestUtil.assertEquals(f, 47);
	}

	@Test
	public void testDoubleComponentsDotProduct() {
		float f = new Vector3(2, 3, 4).dot(4d, 5d, 6d);
		TestUtil.assertEquals(f, 47);
	}

	@Test
	public void testFloatComponentsDotProduct() {
		float f = new Vector3(2, 3, 4).dot(4, 5, 6);
		TestUtil.assertEquals(f, 47);
	}

	@Test
	public void testVector3Cross() {
		Vector3 f = new Vector3(2, 3, 4).cross(new Vector3(4, 5, 6.5));
		TestUtil.assertEquals(f, -0.5f, 3, -2);
	}

	@Test
	public void testDoubleComponentsCross() {
		Vector3 f = new Vector3(2, 3, 4).cross(4, 5, 6.5);
		TestUtil.assertEquals(f, -0.5f, 3, -2);
	}

	@Test
	public void testFloatComponentsCross() {
		Vector3 f = new Vector3(2, 3, 4).cross(4, 5, 6.5f);
		TestUtil.assertEquals(f, -0.5f, 3, -2);
	}

	@Test
	public void testRaiseToFloatPower() {
		Vector3 vector = new Vector3(2, 6, 8).pow(2);
		TestUtil.assertEquals(vector, 4f, 36f, 64f);
	}

	@Test
	public void testRaiseToDoublePower() {
		Vector3 vector = new Vector3(2, 6, 8).pow(2d);
		TestUtil.assertEquals(vector, 4, 36, 64);
	}

	@Test
	public void testCeiling() {
		Vector3 vector = new Vector3(2.5f, 6.7f, 7.9f).ceil();
		TestUtil.assertEquals(vector, 3, 7, 8);
	}

	@Test
	public void testFloor() {
		Vector3 vector = new Vector3(2.5f, 6.7f, 7.8f).floor();
		TestUtil.assertEquals(vector, 2, 6, 7);
	}

	@Test
	public void testRound() {
		Vector3 vector = new Vector3(2.2f, 6.7f, 7.8f).round();
		TestUtil.assertEquals(vector, 2, 7, 8);
	}

	@Test
	public void testAbsolute() {
		Vector3 vector1 = new Vector3(-2.5f, -6.7f, -55).abs();
		TestUtil.assertEquals(vector1, 2.5f, 6.7f, 55);
		Vector3 vector2 = new Vector3(2.5f, 6.7f, 55).abs();
		TestUtil.assertEquals(vector2, 2.5f, 6.7f, 55);
	}

	@Test
	public void testNegate() {
		Vector3 vector = new Vector3(2.2f, -6.7f, 15.8f).negate();
		TestUtil.assertEquals(vector, -2.2f, 6.7f, -15.8f);
	}

	@Test
	public void testVector3Minimum() {
		Vector3 vector = new Vector3(2, 6, -1).min(new Vector3(3, 4, 10));
		TestUtil.assertEquals(vector, 2, 4, -1);
	}

	@Test
	public void testDoubleComponentsMinimum() {
		Vector3 vector = new Vector3(2, 6, 10).min(3d, 4d, -5d);
		TestUtil.assertEquals(vector, 2, 4, -5);
	}

	@Test
	public void testFloatComponentsMinimum() {
		Vector3 vector = new Vector3(2, 6, 10).min(3, 4, -5);
		TestUtil.assertEquals(vector, 2, 4, -5);
	}

	@Test
	public void testVector3Maximum() {
		Vector3 vector = new Vector3(2, 6, 10).max(new Vector3(3, 4, -5));
		TestUtil.assertEquals(vector, 3, 6, 10);
	}

	@Test
	public void testDoubleComponentsMaximum() {
		Vector3 vector = new Vector3(2, 6, 10).max(3d, 4d, -5d);
		TestUtil.assertEquals(vector, 3, 6, 10);
	}

	@Test
	public void testFloatComponentsMaximum() {
		Vector3 vector = new Vector3(2, 6, 10).max(3, 4, -5);
		TestUtil.assertEquals(vector, 3, 6, 10);
	}

	@Test
	public void testVector3DistanceSquared() {
		float f = new Vector3(2, 3, 4).distanceSquared(new Vector3(5, 6, 7));
		TestUtil.assertEquals(f, 27);
	}

	@Test
	public void testDoubleComponentsDistanceSquared() {
		float f = new Vector3(2, 3, 4).distanceSquared(5d, 6d, 7d);
		TestUtil.assertEquals(f, 27);
	}

	@Test
	public void testFloatComponentsDistanceSquared() {
		float f = new Vector3(2, 3, 4).distanceSquared(5, 6, 7);
		TestUtil.assertEquals(f, 27);
	}

	@Test
	public void testVector3Distance() {
		float f = new Vector3(0, 2, 4).distance(new Vector3(0, 8, 16));
		TestUtil.assertEquals(f, 13.4164075851f);
	}

	@Test
	public void testDoubleComponentsDistance() {
		float f = new Vector3(0, 2, 4).distance(0d, 8d, 16d);
		TestUtil.assertEquals(f, 13.4164075851f);
	}

	@Test
	public void testFloatComponentsDistance() {
		float f = new Vector3(0, 2, 4).distance(0, 8, 16);
		TestUtil.assertEquals(f, 13.4164075851f);
	}

	@Test
	public void testLength() {
		float f = new Vector3(3, 4, 5).length();
		TestUtil.assertEquals(f, 7.071068f);
	}

	@Test
	public void testLengthSquared() {
		float f = new Vector3(3, 4, 5).lengthSquared();
		TestUtil.assertEquals(f, 50);
	}

	@Test
	public void testNormalize() {
		Vector3 vector = new Vector3(2, 2, 0).normalize();
		TestUtil.assertEquals(vector, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO, 0);
	}

	@Test
	public void testConvertToVector2() {
		Vector2 vector = new Vector3(1, 2, 3).toVector2();
		TestUtil.assertEquals(vector, 1, 2);
	}

	@Test
	public void testConvertToVector4DefaultW() {
		Vector4 vector = new Vector3(1, 2, 3).toVector4();
		TestUtil.assertEquals(vector, 1, 2, 3, 0);
	}

	@Test
	public void testConvertToVector4FloatW() {
		Vector4 vector = new Vector3(1, 2, 3).toVector4(4);
		TestUtil.assertEquals(vector, 1, 2, 3, 4);
	}

	@Test
	public void testConvertToVector4DoubleW() {
		Vector4 vector = new Vector3(1, 2, 3).toVector4(4d);
		TestUtil.assertEquals(vector, 1, 2, 3, 4);
	}

	@Test
	public void testConvertToVectorN() {
		VectorN vector = new Vector3(1, 2, 3).toVectorN();
		TestUtil.assertEquals(vector, 1, 2, 3);
	}

	@Test
	public void testConvertToArray() {
		float[] array = new Vector3(1, 2, 3).toArray();
		TestUtil.assertEquals(array, 1, 2, 3);
	}

	@Test
	public void testComparison() {
		int c1 = new Vector3(10, 20, 30).compareTo(new Vector3(20, 20, 30));
		Assert.assertTrue(c1 < 0);
		int c2 = new Vector3(10, 20, 30).compareTo(new Vector3(10, 20, 30));
		Assert.assertTrue(c2 == 0);
		int c3 = new Vector3(10, 20, 30).compareTo(new Vector3(10, 10, 30));
		Assert.assertTrue(c3 > 0);
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(new Vector3(122, 43, 96).equals(new Vector3(122, 43, 96)));
		Assert.assertFalse(new Vector3(122, 43, 96).equals(new Vector3(378, 95, 96)));
	}

	@Test
	public void testCloning() {
		Vector3 vector = new Vector3(3, 2, 5);
		Assert.assertEquals(vector, vector.clone());
	}

	@Test
	public void testCreateDirectionFromRandom() {
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			Vector3 vector = Vector3.createRandomDirection(random);
			TestUtil.assertEquals(vector.length(), 1);
		}
	}

	@Test
	public void testCreateDirectionFromFloatAnglesRadians() {
		Vector3 vector1 = Vector3.createDirection(0, 0);
		TestUtil.assertEquals(vector1, 1, 0, 0);
		Vector3 vector2 = Vector3.createDirection((float) TrigMath.QUARTER_PI, 0);
		TestUtil.assertEquals(vector2, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Vector3 vector3 = Vector3.createDirection((float) TrigMath.HALF_PI, (float) TrigMath.HALF_PI);
		TestUtil.assertEquals(vector3, 0, 1, 0);
		Vector3 vector4 = Vector3.createDirection((float) TrigMath.PI, (float) TrigMath.PI);
		TestUtil.assertEquals(vector4, 1, 0, 0);
		Vector3 vector5 = Vector3.createDirection((float) TrigMath.THREE_PI_HALVES, (float) TrigMath.THREE_PI_HALVES);
		TestUtil.assertEquals(vector5, 0, -1, 0);
		Vector3 vector6 = Vector3.createDirection((float) TrigMath.TWO_PI, (float) TrigMath.TWO_PI);
		TestUtil.assertEquals(vector6, 1, 0, 0);
	}
}
