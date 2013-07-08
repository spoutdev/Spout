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
package org.spout.math.test.matrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.spout.math.matrix.Matrix2;
import org.spout.math.matrix.Matrix3;
import org.spout.math.matrix.Matrix4;
import org.spout.math.matrix.MatrixN;
import org.spout.math.test.TestUtil;
import org.spout.math.vector.Vector2;

public class Matrix2Test {
	@Test
	public void testDefaultConstructor() {
		Matrix2 matrix = new Matrix2();
		TestUtil.assertEquals(matrix, 1, 0, 0, 1);
	}

	@Test
	public void testCopyMatrix2Constructor() {
		Matrix2 matrix = new Matrix2(new Matrix2(1f, 2f, 3f, 4f));
		TestUtil.assertEquals(matrix, 1, 2, 3, 4);
	}

	@Test
	public void testCopyMatrix3Constructor() {
		Matrix2 matrix = new Matrix2(new Matrix3(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f));
		TestUtil.assertEquals(matrix, 1, 2, 4, 5);
	}

	@Test
	public void testCopyMatrix4Constructor() {
		Matrix2 matrix = new Matrix2(new Matrix4(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f, 13f, 14f, 15f, 16f));
		TestUtil.assertEquals(matrix, 1, 2, 5, 6);
	}

	@Test
	public void testCopyMatrixNConstructor() {
		Matrix2 matrix = new Matrix2(new MatrixN(2));
		TestUtil.assertEquals(matrix, 1, 0, 0, 1);
	}

	@Test
	public void testDoubleComponentsConstructor() {
		Matrix2 matrix = new Matrix2(1.0, 2.0, 3.0, 4.0);
		TestUtil.assertEquals(matrix, 1, 2, 3, 4);
	}

	@Test
	public void testFloatComponentsConstructor() {
		Matrix2 matrix = new Matrix2(1.0f, 2.0f, 3.0f, 4.0f);
		TestUtil.assertEquals(matrix, 1, 2, 3, 4);
	}

	@Test
	public void testGetter() {
		Matrix2 matrix = new Matrix2(1.0f, 2.0f, 3.0f, 4.0f);
		TestUtil.assertEquals(matrix.get(0, 0), 1f);
		TestUtil.assertEquals(matrix.get(0, 1), 2f);
		TestUtil.assertEquals(matrix.get(1, 0), 3f);
		TestUtil.assertEquals(matrix.get(1, 1), 4f);
	}

	@Test
	public void testMatrix2Addition() {
		Matrix2 matrix = new Matrix2(1.0f, 2.0f, 3.0f, 4.0f).add(new Matrix2(1.0f, 2.0f, 3.0f, 4.0f));
		TestUtil.assertEquals(matrix, 2, 4, 6, 8);
	}

	@Test
	public void testMatrix2Subtraction() {
		Matrix2 matrix = new Matrix2(1.0f, 2.0f, 3.0f, 4.0f).sub(new Matrix2(1.0f, 2.0f, 3.0f, 4.0f));
		TestUtil.assertEquals(matrix, 0, 0, 0, 0);
	}

	@Test
	public void testFloatFactorMultiplication() {
		Matrix2 matrix = new Matrix2(1.0f, 2.0f, 3.0f, 4.0f).mul(2f);
		TestUtil.assertEquals(matrix, 2, 4, 6, 8);
	}

	@Test
	public void testDoubleFactorMultiplication() {
		Matrix2 matrix = new Matrix2(1.0f, 2.0f, 3.0f, 4.0f).mul(2.0);
		TestUtil.assertEquals(matrix, 2, 4, 6, 8);
	}

	@Test
	public void testMatrix2Multiplication() {
		Matrix2 matrix = new Matrix2(1.0f, 2.0f, 3.0f, 4.0f).mul(new Matrix2(1.0f, 0.0f, 0.0f, 1.0f));
		TestUtil.assertEquals(matrix, 1, 2, 3, 4);
	}

	@Test
	public void testFloatFactorDivision() {
		Matrix2 matrix = new Matrix2(1.0f, 2.0f, 3.0f, 4.0f).div(0.5f);
		TestUtil.assertEquals(matrix, 2, 4, 6, 8);
	}

	@Test
	public void testDoubleFactorDivision() {
		Matrix2 matrix = new Matrix2(1.0f, 2.0f, 3.0f, 4.0f).div(0.5);
		TestUtil.assertEquals(matrix, 2, 4, 6, 8);
	}

	@Test
	public void testMatrix2Division() {
		Matrix2 matrix = new Matrix2(1.0f, 2.0f, 3.0f, 4.0f).mul(new Matrix2(1.0f, 0.0f, 0.0f, 1.0f));
		TestUtil.assertEquals(matrix, 1, 2, 3, 4);
	}

	@Test
	public void testRaiseToFloatPower() {
		Matrix2 matrix = new Matrix2(1.0f, 2.0f, 3.0f, 4.0f).pow(2f);
		TestUtil.assertEquals(matrix, 1, 4, 9, 16);
	}

	@Test
	public void testRaiseToDoublePower() {
		Matrix2 matrix = new Matrix2(1.0f, 2.0f, 3.0f, 4.0f).pow(2.0);
		TestUtil.assertEquals(matrix, 1, 4, 9, 16);
	}

	@Test
	public void testTranslateFloatDistance() {
		Matrix2 matrix1 = new Matrix2(1.0f, 0.0f, 0.0f, 1.0f).translate(5f);
		TestUtil.assertEquals(matrix1, 1, 5, 0, 1);
	}

	@Test
	public void testScaleDoubleFactor() {
		Matrix2 matrix1 = new Matrix2(1.0f, 0.0f, 0.0f, 1.0f).scale(2.5);
		TestUtil.assertEquals(matrix1, 2.5f, 0f, 0f, 2.5f);
	}

	@Test
	public void testScaleFloatFactor() {
		Matrix2 matrix1 = new Matrix2(1.0f, 0.0f, 0.0f, 1.0f).scale(2.5f);
		TestUtil.assertEquals(matrix1, 2.5f, 0f, 0f, 2.5f);
	}

	@Test
	public void testScaleVector2() {
		Matrix2 matrix1 = new Matrix2(1.0f, 0.0f, 0.0f, 1.0f).scale(new Vector2(2f, 3f));
		TestUtil.assertEquals(matrix1, 2f, 0f, 0f, 3f);
	}

	@Test
	public void testScaleFloatComponents() {
		Matrix2 matrix1 = new Matrix2(1.0f, 0.0f, 0.0f, 1.0f).scale(2f, 3f);
		TestUtil.assertEquals(matrix1, 2f, 0f, 0f, 3f);
	}

	@Test
	public void testRotateComplex() {

	}

	@Test
	public void testTransformVector2() {

	}

	@Test
	public void testTransformFloatComponents() {

	}

	@Test
	public void testFloor() {
		Matrix2 matrix = new Matrix2(1.1f, 2.5f, 3.9f, -4.8f).floor();
		TestUtil.assertEquals(matrix, 1, 2, 3, -5);
	}

	@Test
	public void testCeiling() {
		Matrix2 matrix = new Matrix2(1.1f, 2.5f, 3.9f, -4.8f).ceil();
		TestUtil.assertEquals(matrix, 2, 3, 4, -4);
	}

	@Test
	public void testRound() {
		Matrix2 matrix = new Matrix2(1.1f, 2.5f, 3.9f, -4.8f).round();
		TestUtil.assertEquals(matrix, 1, 3, 4, -5);
	}

	@Test
	public void testAbsolute() {
		Matrix2 matrix = new Matrix2(1.1f, 2.5f, 3.9f, -4.8f).abs();
		TestUtil.assertEquals(matrix, 1.1f, 2.5f, 3.9f, 4.8f);
	}

	@Test
	public void testNegate() {
		Matrix2 matrix = new Matrix2(1.1f, 2.5f, 3.9f, -4.8f).negate();
		TestUtil.assertEquals(matrix, -1.1f, -2.5f, -3.9f, 4.8f);
	}

	@Test
	public void testTranspose() {
		Matrix2 matrix = new Matrix2(1f, 2f, 3f, 4f).transpose();
		TestUtil.assertEquals(matrix, 1f, 3f, 2f, 4f);
	}

	@Test
	public void testTrace() {
		float value = new Matrix2(1f, 2f, 3f, 4f).trace();
		TestUtil.assertEquals(value, 5);
	}

	@Test
	public void testDeterminant() {
		float value = new Matrix2(1f, 2f, 3f, 4f).determinant();
		TestUtil.assertEquals(value, -2);
	}

	@Test
	public void testInvert() {
		Matrix2 matrix = new Matrix2(1f, 2f, 3f, 4f).invert();
		TestUtil.assertEquals(matrix, -2f, 1f, 1.5f, -0.5f);
	}

	@Test
	public void testConvertToMatrix3() {
		Matrix2 matrix = new Matrix2(1f, 2f, 3f, 4f);
		TestUtil.assertEquals(matrix.toMatrix3(), 1f, 2f, 0f, 3f, 4f, 0f, 0f, 0f, 0f);
	}

	@Test
	public void testConvertToMatrix4() {
		Matrix2 matrix = new Matrix2(1f, 2f, 3f, 4f);
		TestUtil.assertEquals(matrix.toMatrix4(), 1f, 2f, 0f, 0f, 3f, 4f, 0f, 0f, 0f, 0f, 0f , 0f, 0f, 0f, 0f , 0f);
	}

	@Test
	public void testConvertToMatrixN() {
		Matrix2 matrix = new Matrix2(1f, 2f, 3f, 4f);
		TestUtil.assertEquals(matrix.toMatrixN(), 1f, 2f, 3f, 4f);
	}

	@Test
	public void testConvertToArrayRowMajorDefault() {
		float[] array = new Matrix2(1f, 2f, 3f, 4f).toArray(true);
		TestUtil.assertEquals(array, 1f, 3f, 2f, 4f);
	}

	@Test
	public void testConvertToArray() {
		float[] array = new Matrix2(1f, 2f, 3f, 4f).toArray();
		TestUtil.assertEquals(array, 1f, 2f, 3f, 4f);
	}

	@Test
	public void testEquals() {
		assertEquals(new Matrix2(1f, 2f, 3f, 4f), new Matrix2(1f, 2f, 3f, 4f));
		assertNotEquals(new Matrix2(1f, 2f, 3f, 4f), new Matrix2(1f, 2f, 3f, 5f));
	}

	@Test
	public void testCloning() {
		Matrix2 matrix = new Matrix2(1f, 2f, 3f, 4f);
		assertEquals(matrix, matrix.clone());
	}

	@Test
	public void testCreateFromScalingDoubleFactor() {
		Matrix2 matrix = Matrix2.createScaling(2.);
		TestUtil.assertEquals(matrix, 2f, 0f, 0f, 2f);
	}

	@Test
	public void testCreateFromScalingFloatFactor() {
		Matrix2 matrix = Matrix2.createScaling(2f);
		TestUtil.assertEquals(matrix, 2f, 0f, 0f, 2f);
	}

	@Test
	public void testCreateFromScalingVector2() {
		Matrix2 matrix = Matrix2.createScaling(new Vector2(2.0, 1.5));
		TestUtil.assertEquals(matrix, 2f, 0f, 0f, 1.5f);
	}

	@Test
	public void testCreateFromScalingFloatComponents() {
		Matrix2 matrix = Matrix2.createScaling(2.0f, 1.5f);
		TestUtil.assertEquals(matrix, 2f, 0f, 0f, 1.5f);
	}

	@Test
	public void testCreateTranslationFromFloatDistance() {
		Matrix2 matrix = Matrix2.createTranslation(5f);
		TestUtil.assertEquals(matrix, 1f, 5f, 0f, 1f);
	}

	@Test
	public void testCreateRotationFromComplex() {

	}
}
