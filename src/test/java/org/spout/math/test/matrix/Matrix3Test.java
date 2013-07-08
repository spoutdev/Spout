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
import org.spout.math.vector.Vector3;

public class Matrix3Test {
	@Test
	public void testDefaultConstructor() {
		Matrix3 matrix = new Matrix3();
		TestUtil.assertEquals(matrix, 1, 0, 0, 0, 1, 0, 0, 0, 1);
	}

	@Test
	public void testCopyMatrix2Constructor() {
		Matrix3 matrix = new Matrix3(new Matrix2(1, 2, 3, 4));
		TestUtil.assertEquals(matrix, 1, 2, 0, 3, 4, 0, 0, 0, 0);
	}

	@Test
	public void testCopyMatrix3Constructor() {
		Matrix3 matrix = new Matrix3(new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9));
		TestUtil.assertEquals(matrix, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	public void testCopyMatrix4Constructor() {
		Matrix3 matrix = new Matrix3(new Matrix4(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16));
		TestUtil.assertEquals(matrix, 1, 2, 3, 5, 6, 7, 9, 10, 11);
	}

	@Test
	public void testCopyMatrixNConstructor() {
		Matrix3 matrix = new Matrix3(new Matrix4(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16));
		TestUtil.assertEquals(matrix, 1, 2, 3, 5, 6, 7, 9, 10, 11);
	}

	@Test
	public void testDoubleComponentsConstructor() {
		Matrix3 matrix = new Matrix3(1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d);
		TestUtil.assertEquals(matrix, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	public void testFloatComponentsConstructor() {
		Matrix3 matrix = new Matrix3(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f);
		TestUtil.assertEquals(matrix, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	public void testGetter() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9);
		TestUtil.assertEquals(matrix.get(0, 0), 1);
		TestUtil.assertEquals(matrix.get(0, 1), 2);
		TestUtil.assertEquals(matrix.get(0, 2), 3);
		TestUtil.assertEquals(matrix.get(1, 0), 4);
		TestUtil.assertEquals(matrix.get(1, 1), 5);
		TestUtil.assertEquals(matrix.get(1, 2), 6);
		TestUtil.assertEquals(matrix.get(2, 0), 7);
		TestUtil.assertEquals(matrix.get(2, 1), 8);
		TestUtil.assertEquals(matrix.get(2, 2), 9);
	}

	@Test
	public void testMatrix3Addition() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).add(new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9));
		TestUtil.assertEquals(matrix, 2, 4, 6, 8, 10, 12, 14, 16, 18);
	}

	@Test
	public void testMatrix3Subtraction() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).sub(new Matrix3(0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5));
		TestUtil.assertEquals(matrix, 0.5f, 1f, 1.5f, 2f, 2.5f, 3f, 3.5f, 4f, 4.5f);
	}

	@Test
	public void testFloatFactorMultiplication() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).mul(2f);
		TestUtil.assertEquals(matrix, 2, 4, 6, 8, 10, 12, 14, 16, 18);
	}

	@Test
	public void testDoubleFactorMultiplication() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).mul(2d);
		TestUtil.assertEquals(matrix, 2, 4, 6, 8, 10, 12, 14, 16, 18);
	}

	@Test
	public void testMatrix3Multiplication() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).mul(new Matrix3());
		TestUtil.assertEquals(matrix, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	public void testFloatFactorDivision() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).div(2f);
		TestUtil.assertEquals(matrix, 0.5f, 1f, 1.5f, 2f, 2.5f, 3f, 3.5f, 4f, 4.5f);
	}

	@Test
	public void testDoubleFactorDivision() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).div(2d);
		TestUtil.assertEquals(matrix, 0.5f, 1f, 1.5f, 2f, 2.5f, 3f, 3.5f, 4f, 4.5f);
	}

	@Test
	public void testMatrix3Division() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).div(new Matrix3());
		TestUtil.assertEquals(matrix, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	public void testRaiseToFloatPower() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).pow(2f);
		TestUtil.assertEquals(matrix, 1, 4, 9, 16, 25, 36, 49, 64, 81);
	}

	@Test
	public void testRaiseToDoublePower() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).pow(2d);
		TestUtil.assertEquals(matrix, 1, 4, 9, 16, 25, 36, 49, 64, 81);
	}

	@Test
	public void testTranslateVector2() {
		Matrix3 matrix = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).translate(new Vector2(1, 0));
		TestUtil.assertEquals(matrix, 1, 0, 1, 0, 1, 0, 0, 0, 1);
	}

	@Test
	public void testTranslateFloatComponents() {
		Matrix3 matrix = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).translate(1f, 0f);
		TestUtil.assertEquals(matrix, 1, 0, 1, 0, 1, 0, 0, 0, 1);
	}

	@Test
	public void testScaleFloatFactor() {
		Matrix3 matrix = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).scale(2f);
		TestUtil.assertEquals(matrix, 2, 0, 0, 0, 2, 0, 0, 0, 2);
	}

	@Test
	public void testScaleVector3() {
		Matrix3 matrix = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).scale(new Vector3(2, 2, 2));
		TestUtil.assertEquals(matrix, 2, 0, 0, 0, 2, 0, 0, 0, 2);
	}

	@Test
	public void testScaleFloatComponents() {
		Matrix3 matrix = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).scale(2d);
		TestUtil.assertEquals(matrix, 2, 0, 0, 0, 2, 0, 0, 0, 2);
	}

	@Test
	public void testRotateComplex() {

	}

	@Test
	public void testRotateQuaternion() {

	}

	@Test
	public void testTransformVector3() {
		Vector3 vector = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).transform(new Vector3(1, 0, 0));
		TestUtil.assertEquals(vector, 1, 0, 0);
	}

	@Test
	public void testTransformFloatComponents() {
		Vector3 vector = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).transform(1, 0, 0);
		TestUtil.assertEquals(vector, 1, 0, 0);
	}

	@Test
	public void testFloor() {
		Matrix3 matrix = new Matrix3(1.1, 2.9, 3.5, -1.1, -2.5, -3.9, 0, 0, 0).floor();
		TestUtil.assertEquals(matrix, 1, 2, 3, -2, -3, -4, 0, 0, 0);
	}

	@Test
	public void testCeiling() {
		Matrix3 matrix = new Matrix3(1.1, 2.9, 3.5, -1.1, -2.5, -3.9, 0, 0, 0).ceil();
		TestUtil.assertEquals(matrix, 2, 3, 4, -1, -2, -3, 0, 0, 0);
	}

	@Test
	public void testRound() {
		Matrix3 matrix = new Matrix3(1.1, 2.9, 3.5, -1.1, -2.5, -3.9, 0, 0, 0).round();
		TestUtil.assertEquals(matrix, 1, 3, 4, -1, -2, -4, 0, 0, 0);
	}

	@Test
	public void testAbsolute() {
		Matrix3 matrix = new Matrix3(-1, 1, -1, 1, -1, 1, -1, 1, -1).abs();
		TestUtil.assertEquals(matrix, 1, 1, 1, 1, 1, 1, 1, 1, 1);
	}

	@Test
	public void testNegate() {
		Matrix3 matrix = new Matrix3(-1, 1, -1, 1, -1, 1, -1, 1, -1).negate();
		TestUtil.assertEquals(matrix, 1, -1, 1, -1, 1, -1, 1, -1, 1);
	}

	@Test
	public void testTranspose() {
		Matrix3 matrix = new Matrix3(0, 1, 2, 3, 4, 5, 6, 7, 8).transpose();
		TestUtil.assertEquals(matrix, 0, 3, 6, 1, 4, 7, 2, 5, 8);
	}

	@Test
	public void testTrace() {
		float value = new Matrix3(0, 1, 2, 3, 4, 5, 6, 7, 8).trace();
		TestUtil.assertEquals(value, 12);
	}

	@Test
	public void testDeterminant() {
		float value = new Matrix3(0, 1, 2, 3, 4, 5, 6, 7, 8).determinant();
		TestUtil.assertEquals(value, 0);
	}

	@Test
	public void testInvert() {
		Matrix3 matrix1 = new Matrix3(0, 1, 2, 3, 4, 5, 6, 7, 8).invert();
		assertEquals(matrix1, null);
		Matrix3 matrix2 = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).invert();
		TestUtil.assertEquals(matrix2, 1, 0, 0, 0, 1, 0, 0, 0, 1);
		Matrix3 matrix3 = new Matrix3(1, 1, 0, 0, 1, 0, 0, 0, 1).invert();
		TestUtil.assertEquals(matrix3, 1, -1, 0, 0, 1, 0, 0, 0, 1);
	}

	@Test
	public void testConvertToMatrix2() {
		Matrix2 matrix = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).toMatrix2();
		TestUtil.assertEquals(matrix, 1, 0, 0, 1);
	}

	@Test
	public void testConvertToMatrix4() {
		Matrix4 matrix = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).toMatrix4();
		TestUtil.assertEquals(matrix, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0);
	}

	@Test
	public void testConvertToMatrixN() {
		MatrixN matrix = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).toMatrixN();
		TestUtil.assertEquals(matrix, 1, 0, 0, 0, 1, 0, 0, 0, 1);
	}

	@Test
	public void testConvertToArrayRowMajorDefault() {
		float[] array = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).toArray(true);
		TestUtil.assertEquals(array, 1, 4, 7, 2, 5, 8, 3, 6, 9);
	}

	@Test
	public void testConvertToArray() {
		float[] array = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).toArray();
		TestUtil.assertEquals(array, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	public void testEquals() {
		assertEquals(new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9), new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9));
		assertNotEquals(new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9), new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 0));
	}

	@Test
	public void testCloning() {
		assertEquals(new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9), new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9));
	}

	@Test
	public void testCreateFromScalingDoubleFactor() {
		Matrix3 matrix = Matrix3.createScaling(2d);
		TestUtil.assertEquals(matrix, 2, 0, 0, 0, 2, 0, 0, 0, 2);
	}

	@Test
	public void testCreateFromScalingFloatFactor() {
		Matrix3 matrix = Matrix3.createScaling(2f);
		TestUtil.assertEquals(matrix, 2, 0, 0, 0, 2, 0, 0, 0, 2);
	}

	@Test
	public void testCreateFromScalingVector3() {
		Matrix3 matrix = Matrix3.createScaling(new Vector3(1, 2, 3));
		TestUtil.assertEquals(matrix, 1, 0, 0, 0, 2, 0, 0, 0, 3);
	}

	@Test
	public void testCreateFromScalingFloatComponents() {
		Matrix3 matrix = Matrix3.createScaling(1f, 2f, 3f);
		TestUtil.assertEquals(matrix, 1, 0, 0, 0, 2, 0, 0, 0, 3);
	}

	@Test
	public void testCreateTranslationVector2() {
		Matrix3 matrix1 = Matrix3.createTranslation(new Vector2(1, 0));
		TestUtil.assertEquals(matrix1, 1, 0, 1, 0, 1, 0, 0, 0, 1);
		Matrix3 matrix2 = Matrix3.createTranslation(new Vector2(0, 1));
		TestUtil.assertEquals(matrix2, 1, 0, 0, 0, 1, 1, 0, 0, 1);
	}

	@Test
	public void testCreateTranslationFloatComponents() {
		Matrix3 matrix1 = Matrix3.createTranslation(1f, 0f);
		TestUtil.assertEquals(matrix1, 1, 0, 1, 0, 1, 0, 0, 0, 1);
		Matrix3 matrix2 = Matrix3.createTranslation(0f, 1f);
		TestUtil.assertEquals(matrix2, 1, 0, 0, 0, 1, 1, 0, 0, 1);
	}

	@Test
	public void testCreateRotationFromComplex() {

	}

	@Test
	public void testCreateRotationFromQuaternion() {

	}
}
