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

import static org.junit.Assert.assertEquals;

public class VectorNTest {
	@Test
	public void testDefaultConstructor() {
		VectorN vector = new VectorN(0, 1, 2, 3, 4, 5);
		TestUtil.assertEquals(vector, 0, 1, 2, 3, 4, 5);
	}

	@Test
	public void testCopyVector2Constructor() {
		VectorN vector = new VectorN(new Vector2(0, 1));
		TestUtil.assertEquals(vector, 0, 1);
	}

	@Test
	public void testCopyVector3Constructor() {
		VectorN vector = new VectorN(new Vector3(0, 1, 2));
		TestUtil.assertEquals(vector, 0, 1, 2);
	}

	@Test
	public void testCopyVector4Constructor() {
		VectorN vector = new VectorN(new Vector4(0, 1, 2, 3));
		TestUtil.assertEquals(vector, 0, 1, 2, 3);
	}

	@Test
	public void testCopyVectorNConstructor() {
		VectorN vector = new VectorN(new VectorN(0, 1, 2, 3, 4, 5));
		TestUtil.assertEquals(vector, 0, 1, 2, 3, 4, 5);
	}

	@Test
	public void testFloatComponentsConstructor() {
		VectorN vector = new VectorN(0, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f);
		TestUtil.assertEquals(vector, 0, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f);
	}

	@Test
	public void testSize() {
		VectorN vector = new VectorN(0, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f);
		assertEquals(vector.size(), 6);
	}

	@Test
	public void testGetter() {
		VectorN vector = new VectorN(0, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f);
		TestUtil.assertEquals(vector.get(0), 0);
		TestUtil.assertEquals(vector.get(1), 1.1f);
		TestUtil.assertEquals(vector.get(2), 2.2f);
		TestUtil.assertEquals(vector.get(3), 3.3f);
		TestUtil.assertEquals(vector.get(4), 4.4f);
		TestUtil.assertEquals(vector.get(5), 5.5f);
	}

	@Test
	public void testFlooredGetter() {
		VectorN vector = new VectorN(0, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f, -6.6f);
		TestUtil.assertEquals(vector.getFloored(0), 0);
		TestUtil.assertEquals(vector.getFloored(1), 1);
		TestUtil.assertEquals(vector.getFloored(2), 2);
		TestUtil.assertEquals(vector.getFloored(3), 3);
		TestUtil.assertEquals(vector.getFloored(4), 4);
		TestUtil.assertEquals(vector.getFloored(5), 5);
		TestUtil.assertEquals(vector.getFloored(6), -7);
	}

	@Test
	public void testSetterFloatValue() {
		VectorN vector = new VectorN(0, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f);
		vector.set(0, 6.6f);
		TestUtil.assertEquals(vector.get(0), 6.6f);
	}

	@Test
	public void testSetZero() {
		VectorN vector = new VectorN(0, 1.1f, 2.2f);
		vector.setZero();
		TestUtil.assertEquals(vector.get(0), 0);
		TestUtil.assertEquals(vector.get(1), 0);
		TestUtil.assertEquals(vector.get(2), 0);
	}

	@Test
	public void testResize() {
		VectorN vector1 = new VectorN(0, 1.1f, 2.2f);
		VectorN resize1 = vector1.resize(2);
		TestUtil.assertEquals(resize1.size(), 2);
		VectorN vector2 = new VectorN(0, 1.1f, 2.2f);
		VectorN resize2 = vector2.resize(4);
		TestUtil.assertEquals(resize2.size(), 4);
		TestUtil.assertEquals(resize2.get(3), 0);
	}

	@Test
	public void testVectorNAddition() {
		VectorN vector = new VectorN(0, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f).add(new VectorN(0, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f));
		TestUtil.assertEquals(vector, 0, 2.2f, 4.4f, 6.6f, 8.8f, 11);
	}

	@Test
	public void testFloatComponentsAddition() {
		VectorN vector = new VectorN(0, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f).add(0, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f);
		TestUtil.assertEquals(vector, 0, 2.2f, 4.4f, 6.6f, 8.8f, 11);
	}

	@Test
	public void testVectorNSubtraction() {
		VectorN vector = new VectorN(0, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f).sub(new VectorN(0, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f));
		TestUtil.assertEquals(vector, 0, 0, 0, 0, 0, 0);
	}

	@Test
	public void testFloatComponentsSubtraction() {
		VectorN vector = new VectorN(0, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f).sub(0, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f);
		TestUtil.assertEquals(vector, 0, 0, 0, 0, 0, 0);
	}

	@Test
	public void testDoubleFactorMultiplication() {
		VectorN vector = new VectorN(0.1f, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f).mul(2d);
		TestUtil.assertEquals(vector, 0.2f, 2.2f, 4.4f, 6.6f, 8.8f, 11f);
	}

	@Test
	public void testFloatFactorMultiplication() {
		VectorN vector = new VectorN(0.1f, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f).mul(2);
		TestUtil.assertEquals(vector, 0.2f, 2.2f, 4.4f, 6.6f, 8.8f, 11f);
	}

	@Test
	public void testVectorNMultiplication() {
		VectorN vector = new VectorN(0.1f, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f).mul(new VectorN(1, 2, 3, 4, 5, 6));
		TestUtil.assertEquals(vector, 0.1f, 2.2f, 6.6f, 13.2f, 22, 33);
	}

	@Test
	public void testFloatComponentsMultiplication() {
		VectorN vector = new VectorN(0.1f, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f).mul(2, 2, 3, 3, 4, 4);
		TestUtil.assertEquals(vector, 0.2f, 2.2f, 6.6f, 9.9f, 17.6f, 22);
	}

	@Test
	public void testDoubleFactorDivision() {
		VectorN vector = new VectorN(1, 2, 3, 4, 5, 6).div(2d);
		TestUtil.assertEquals(vector, 0.5f, 1, 1.5f, 2, 2.5f, 3);
	}

	@Test
	public void testFloatFactorDivision() {
		VectorN vector = new VectorN(1, 2, 3, 4, 5, 6).div(2);
		TestUtil.assertEquals(vector, 0.5f, 1, 1.5f, 2, 2.5f, 3);
	}

	@Test
	public void testVectorNDivision() {
		VectorN vector = new VectorN(1, 2, 3, 4, 5, 6).div(new VectorN(2, 1, 0.5f, 8, 4, 6));
		TestUtil.assertEquals(vector, 0.5f, 2, 6, 0.5f, 1.25f, 1);
	}

	@Test
	public void testFloatComponentsDivision() {
		VectorN vector = new VectorN(1, 2, 3, 4, 5, 6).div(2, 1, 0.5f, 8, 4, 6);
		TestUtil.assertEquals(vector, 0.5f, 2, 6, 0.5f, 1.25f, 1);
	}

	@Test
	public void testVectorNDotProduct() {
		float f = new VectorN(2, 3, 4, 5).dot(new VectorN(6, 7, 8, 9));
		TestUtil.assertEquals(f, 110);
	}

	@Test
	public void testFloatComponentsDotProduct() {
		float f = new VectorN(2, 3, 4, 5).dot(6, 7, 8, 9);
		TestUtil.assertEquals(f, 110f);
	}

	@Test
	public void testRaiseToDoublePower() {
		VectorN vector = new VectorN(2, 6, 8, 5.5f).pow(2d);
		TestUtil.assertEquals(vector, 4, 36, 64, 30.25f);
	}

	@Test
	public void testRaiseToFloatPower() {
		VectorN vector = new VectorN(2, 6, 8, 5.5f).pow(2);
		TestUtil.assertEquals(vector, 4, 36, 64, 30.25f);
	}

	@Test
	public void testCeiling() {
		VectorN vector = new VectorN(2.5f, 6.7f, 7.9f, 8.1f).ceil();
		TestUtil.assertEquals(vector, 3, 7, 8, 9);
	}

	@Test
	public void testFloor() {
		VectorN vector = new VectorN(2.5f, 6.7f, 7.8f, 9.1f).floor();
		TestUtil.assertEquals(vector, 2, 6, 7, 9);
	}

	@Test
	public void testRound() {
		VectorN vector = new VectorN(2.2f, 6.7f, 7.8f, 9.1f).round();
		TestUtil.assertEquals(vector, 2, 7, 8, 9);
	}

	@Test
	public void testAbsolute() {
		VectorN vector1 = new VectorN(-2.5f, -6.7f, -55, 0).abs();
		TestUtil.assertEquals(vector1, 2.5f, 6.7f, 55, 0);
		VectorN vector2 = new VectorN(2.5f, 6.7f, 55, 0).abs();
		TestUtil.assertEquals(vector2, 2.5f, 6.7f, 55, 0);
	}

	@Test
	public void testNegate() {
		VectorN vector = new VectorN(2.2f, -6.7f, 15.8f, 20).negate();
		TestUtil.assertEquals(vector, -2.2f, 6.7f, -15.8f, -20);
	}

	@Test
	public void testVectorNMinimum() {
		VectorN vector = new VectorN(2, 6, -1, 0).min(new VectorN(3, 4, 10, -1));
		TestUtil.assertEquals(vector, 2, 4, -1, -1);
	}

	@Test
	public void testFloatComponentsMinimum() {
		VectorN vector = new VectorN(2, 6, -1, 0).min(3, 4, 10, -1.1f);
		TestUtil.assertEquals(vector, 2, 4, -1, -1.1f);
	}

	@Test
	public void testVectorNMaximum() {
		VectorN vector = new VectorN(2, 6, -1, 0).max(new VectorN(3, 4, 10, -1));
		TestUtil.assertEquals(vector, 3, 6, 10, 0);
	}

	@Test
	public void testFloatComponentsMaximum() {
		VectorN vector = new VectorN(2, 6, -1, 0).max(3, 4, 10, -1.1f);
		TestUtil.assertEquals(vector, 3, 6, 10, 0);
	}

	@Test
	public void testVectorNDistanceSquared() {
		float f = new VectorN(2, 3, 4).distanceSquared(new VectorN(5, 6, 7));
		TestUtil.assertEquals(f, 27);
	}

	@Test
	public void testFloatComponentsDistanceSquared() {
		float f = new VectorN(2, 3, 4, 5).distanceSquared(5, 6, 7, 5);
		TestUtil.assertEquals(f, 27);
	}

	@Test
	public void testVectorNDistance() {
		float f = new VectorN(0, 2, 4, 8).distance(new VectorN(0, 8, 16, 8));
		TestUtil.assertEquals(f, 13.4164078649f);
	}

	@Test
	public void testFloatComponentsDistance() {
		float f = new VectorN(0, 2, 4, 8).distance(0, 8, 16, 8);
		TestUtil.assertEquals(f, 13.4164078649f);
	}

	@Test
	public void testLength() {
		float f = new VectorN(3, 4, 5, 6).length();
		TestUtil.assertEquals(f, 9.2736186981f);
	}

	@Test
	public void testLengthSquared() {
		float f = new VectorN(3, 4, 5, 6).lengthSquared();
		TestUtil.assertEquals(f, 86);
	}

	@Test
	public void testNormalize() {
		VectorN v1 = new VectorN(1, 1, 0, 0).normalize();
		TestUtil.assertEquals(v1, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0);
		VectorN v2 = new VectorN(0, 1, 0, 1).normalize();
		TestUtil.assertEquals(v2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testConvertToVector2() {
		VectorN vector = new VectorN(1, 2, 3, 4);
		TestUtil.assertEquals(vector.toVector2(), 1, 2);
	}

	@Test
	public void testConvertToVector3() {
		VectorN vector = new VectorN(1, 2, 3, 4);
		TestUtil.assertEquals(vector.toVector3(), 1, 2, 3);
	}

	@Test
	public void testConvertToVector4() {
		VectorN vector = new VectorN(1, 2, 3, 4);
		TestUtil.assertEquals(vector.toVector4(), 1, 2, 3, 4);
	}

	@Test
	public void testConvertToArray() {
		float[] array = new VectorN(1, 2, 3, 4).toArray();
		TestUtil.assertEquals(array, 1, 2, 3, 4);
	}

	@Test
	public void testComparison() {
		int c1 = new VectorN(10, 20, 30, 40).compareTo(new VectorN(20, 20, 30, 40));
		Assert.assertTrue(c1 < 0);
		int c2 = new VectorN(10, 20, 30, 40).compareTo(new VectorN(10, 20, 30, 40));
		Assert.assertTrue(c2 == 0);
		int c3 = new VectorN(10, 20, 30, 40).compareTo(new VectorN(10, 10, 30, 40));
		Assert.assertTrue(c3 > 0);
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(new VectorN(1, 2, 3).equals(new VectorN(1, 2, 3)));
		Assert.assertFalse(new VectorN(1, 2, 3).equals(new VectorN(2, 2, 3)));
	}

	@Test
	public void testCloning() {
		VectorN vector = new VectorN(1, 2, 3).clone();
		TestUtil.assertEquals(vector, 1, 2, 3);
	}
}
