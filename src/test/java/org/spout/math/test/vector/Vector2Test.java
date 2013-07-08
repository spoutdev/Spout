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

import org.junit.Test;

import org.spout.math.test.TestUtil;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;
import org.spout.math.vector.Vector4;
import org.spout.math.vector.VectorN;

public class Vector2Test {
	@Test
	public void testDefaultConstructor() {
		Vector2 vector = new Vector2();
		TestUtil.assertEquals(vector, 0f, 0f);
	}

	@Test
	public void testCopyVector2Constructor() {
		Vector2 copy = new Vector2(new Vector2(0f, 1f));
		TestUtil.assertEquals(copy, 0f, 1f);
	}

	@Test
	public void testCopyVector3Constructor() {
		Vector2 copy = new Vector2(new Vector3(0f, 1f, 2f));
		TestUtil.assertEquals(copy, 0f, 1f);
	}

	@Test
	public void testCopyVector4Constructor() {
		Vector2 copy = new Vector2(new Vector4(0f, 1f, 2f, 3f));
		TestUtil.assertEquals(copy, 0f, 1f);
	}

	@Test
	public void testCopyVectorNConstructor() {
		Vector2 copy = new Vector2(new VectorN(0f, 1f, 2f, 3f, 4f));
		TestUtil.assertEquals(copy, 0f, 1f);
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
		TestUtil.assertEquals(vector, 0.5f, 1.7f);
	}

	@Test
	public void testFloorGetters() {
		Vector2 vector = new Vector2(0.5f, 1.7f);
		TestUtil.assertEquals(vector.getFloorX(), 0);
		TestUtil.assertEquals(vector.getFloorY(), 1);
	}

	@Test
	public void testVector2Addition() {
		Vector2 vector = new Vector2(0, 1).add(new Vector2(5.5, -0.5));
		TestUtil.assertEquals(vector, 5.5, 0.5);
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
		Vector2 vector = new Vector2(10, 5).sub(new Vector2(9, 4.5));
		TestUtil.assertEquals(vector, 1, 0.5);
	}

	@Test
	public void testDoubleComponentsSubtraction() {
		Vector2 vector = new Vector2(10, 5).sub(9, 4.5);
		TestUtil.assertEquals(vector, 1, 0.5);
	}

	@Test
	public void testFloatComponentsSubtraction() {
		Vector2 vector = new Vector2(10f, 5f).sub(9f, 4.5f);
		TestUtil.assertEquals(vector, 1f, 0.5f);
	}

	@Test
	public void testDoubleFactorMultiplication() {

	}

	@Test
	public void testFloatFactorMultiplication() {

	}

	@Test
	public void testVector2Multiplication() {

	}

	@Test
	public void testDoubleComponentsMultiplication() {

	}

	@Test
	public void testFloatComponentsMultiplication() {

	}

	@Test
	public void testDoubleFactorDivision() {

	}

	@Test
	public void testFloatFactorDivision() {

	}

	@Test
	public void testVector2Division() {

	}

	@Test
	public void testDoubleComponentsDivision() {

	}

	@Test
	public void testFloatComponentsDivision() {

	}

	@Test
	public void testVector2DotProduct() {

	}

	@Test
	public void testDoubleComponentsDotProduct() {

	}

	@Test
	public void testFloatComponentsDotProduct() {

	}

	@Test
	public void testRaiseToFloatPower() {

	}

	@Test
	public void testRaiseToDoublePower() {

	}

	@Test
	public void testCeiling() {

	}

	@Test
	public void testFloor() {

	}

	@Test
	public void testRound() {

	}

	@Test
	public void testAbsolute() {

	}

	@Test
	public void testNegate() {

	}

	@Test
	public void testVector2Minimum() {

	}

	@Test
	public void testDoubleComponentsMinimum() {

	}

	@Test
	public void testFloatComponentsMinimum() {

	}

	@Test
	public void testVector2Maximum() {

	}

	@Test
	public void testDoubleComponentsMaximum() {

	}

	@Test
	public void testFloatComponentsMaximum() {

	}

	@Test
	public void testVector2DistanceSquared() {

	}

	@Test
	public void testDoubleComponentsDistanceSquared() {

	}

	@Test
	public void testFloatComponentsDistanceSquared() {

	}

	@Test
	public void testVector2Distance() {

	}

	@Test
	public void testDoubleComponentsDistance() {

	}

	@Test
	public void testFloatComponentsDistance() {

	}

	@Test
	public void testLength() {

	}

	@Test
	public void testLengthSquared() {

	}

	@Test
	public void testNormalize() {

	}

	@Test
	public void testConvertToVector3DefaultZ() {

	}

	@Test
	public void testConvertToVector3FloatZ() {

	}

	@Test
	public void testConvertToVector3DoubleZ() {

	}

	@Test
	public void testConvertToVector4DefaultZW() {

	}

	@Test
	public void testConvertToVector4FloatZW() {

	}

	@Test
	public void testConvertToVector4DoubleZW() {

	}

	@Test
	public void testConvertToVectorN() {

	}

	@Test
	public void testConvertToArray() {

	}

	@Test
	public void testComparison() {

	}

	@Test
	public void testEquals() {

	}

	@Test
	public void testCloning() {

	}

	@Test
	public void testCreateDirectionFromRandom() {

	}

	@Test
	public void testCreateDirectionFromFloatAngleRadians() {

	}
}
