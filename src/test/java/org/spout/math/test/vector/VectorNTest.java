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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spout.math.test.TestUtil;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;
import org.spout.math.vector.Vector4;
import org.spout.math.vector.VectorN;

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
		VectorN vector = new VectorN(0.0f, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f);
		TestUtil.assertEquals(vector, 0.0f, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f);
	}

	@Test
	public void testSize() {
		VectorN vector = new VectorN(0.0f, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f);
		assertEquals(vector.size(), 6);
	}

	@Test
	public void testGetter() {
		VectorN vector = new VectorN(0.0f, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f);
		TestUtil.assertEquals(vector.get(0), 0.0f);
		TestUtil.assertEquals(vector.get(1), 1.1f);
		TestUtil.assertEquals(vector.get(2), 2.2f);
		TestUtil.assertEquals(vector.get(3), 3.3f);
		TestUtil.assertEquals(vector.get(4), 4.4f);
		TestUtil.assertEquals(vector.get(5), 5.5f);
	}

	@Test
	public void testFlooredGetter() {
		VectorN vector = new VectorN(0.0f, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f, -6.6f);
		TestUtil.assertEquals(vector.getFloored(0), 0f);
		TestUtil.assertEquals(vector.getFloored(1), 1f);
		TestUtil.assertEquals(vector.getFloored(2), 2f);
		TestUtil.assertEquals(vector.getFloored(3), 3f);
		TestUtil.assertEquals(vector.getFloored(4), 4f);
		TestUtil.assertEquals(vector.getFloored(5), 5f);
		TestUtil.assertEquals(vector.getFloored(6), -7f);
	}

	@Test
	public void testSetterFloatValue() {
		VectorN vector = new VectorN(0.0f, 1.1f, 2.2f, 3.3f, 4.4f, 5.5f);
		vector.set(0, 6.6f);
		TestUtil.assertEquals(vector.get(0), 6.6f);
	}

	@Test
	public void testSetZero() {
		VectorN vector = new VectorN(0.0f, 1.1f, 2.2f);
		vector.setZero();
		TestUtil.assertEquals(vector.get(0), 0f);
		TestUtil.assertEquals(vector.get(1), 0f);
		TestUtil.assertEquals(vector.get(2), 0f);
	}

	@Test
	public void testResize() {
		VectorN vector1 = new VectorN(0.0f, 1.1f, 2.2f);
		VectorN resize1 = vector1.resize(2);
		TestUtil.assertEquals(resize1.size(), 2);
		VectorN vector2 = new VectorN(0.0f, 1.1f, 2.2f);
		VectorN resize2 = vector2.resize(4);
		TestUtil.assertEquals(resize2.size(), 4);
		TestUtil.assertEquals(resize2.get(3), 0f);
	}

	@Test
	public void testVectorNAddition() {

	}

	@Test
	public void testDoubleComponentsAddition() {

	}

	@Test
	public void testFloatComponentsAddition() {

	}

	@Test
	public void testVectorNSubtraction() {

	}

	@Test
	public void testDoubleComponentsSubtraction() {

	}

	@Test
	public void testFloatComponentsSubtraction() {

	}

	@Test
	public void testDoubleFactorMultiplication() {

	}

	@Test
	public void testFloatFactorMultiplication() {

	}

	@Test
	public void testVectorNMultiplication() {

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
	public void testVectorNDivision() {

	}

	@Test
	public void testDoubleComponentsDivision() {

	}

	@Test
	public void testFloatComponentsDivision() {

	}

	@Test
	public void testVectorNDotProduct() {

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
	public void testVectorNMinimum() {

	}

	@Test
	public void testDoubleComponentsMinimum() {

	}

	@Test
	public void testFloatComponentsMinimum() {

	}

	@Test
	public void testVectorNMaximum() {

	}

	@Test
	public void testDoubleComponentsMaximum() {

	}

	@Test
	public void testFloatComponentsMaximum() {

	}

	@Test
	public void testVectorNDistanceSquared() {

	}

	@Test
	public void testDoubleComponentsDistanceSquared() {

	}

	@Test
	public void testFloatComponentsDistanceSquared() {

	}

	@Test
	public void testVectorNDistance() {

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
	public void testConvertToVector2() {

	}

	@Test
	public void testConvertToVector3() {

	}

	@Test
	public void testConvertToVector4() {

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
}
