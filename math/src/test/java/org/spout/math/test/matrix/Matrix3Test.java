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

import org.junit.Assert;
import org.junit.Test;

import org.spout.math.imaginary.Complex;
import org.spout.math.imaginary.Quaternion;
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
		Matrix3 matrix = new Matrix3(new MatrixN(1, 2, 3, 4, 5, 6, 7, 8, 9));
		TestUtil.assertEquals(matrix, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	public void testDoubleComponentsConstructor() {
		Matrix3 matrix = new Matrix3(1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d);
		TestUtil.assertEquals(matrix, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	public void testFloatComponentsConstructor() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9);
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
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).sub(new Matrix3(0.5f, 1, 1.5f, 2, 2.5f, 3, 3.5f, 4, 4.5f));
		TestUtil.assertEquals(matrix, 0.5f, 1, 1.5f, 2, 2.5f, 3, 3.5f, 4, 4.5f);
	}

	@Test
	public void testFloatFactorMultiplication() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).mul(2);
		TestUtil.assertEquals(matrix, 2, 4, 6, 8, 10, 12, 14, 16, 18);
	}

	@Test
	public void testDoubleFactorMultiplication() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).mul(2d);
		TestUtil.assertEquals(matrix, 2, 4, 6, 8, 10, 12, 14, 16, 18);
	}

	@Test
	public void testMatrix3Multiplication() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).mul(new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1));
		TestUtil.assertEquals(matrix, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	public void testFloatFactorDivision() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).div(2);
		TestUtil.assertEquals(matrix, 0.5f, 1, 1.5f, 2, 2.5f, 3, 3.5f, 4, 4.5f);
	}

	@Test
	public void testDoubleFactorDivision() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).div(2d);
		TestUtil.assertEquals(matrix, 0.5f, 1, 1.5f, 2, 2.5f, 3, 3.5f, 4, 4.5f);
	}

	@Test
	public void testMatrix3Division() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).div(new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1));
		TestUtil.assertEquals(matrix, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	public void testRaiseToFloatPower() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).pow(2);
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
		Matrix3 matrix = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).translate(1, 0);
		TestUtil.assertEquals(matrix, 1, 0, 1, 0, 1, 0, 0, 0, 1);
	}

	@Test
	public void testScaleFloatFactor() {
		Matrix3 matrix = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).scale(2);
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
		Matrix3 matrix = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).rotate(new Complex(2, 3));
		TestUtil.assertEquals(matrix, 0.5547002f, -0.8320503f, 0, 0.8320503f, 0.5547002f, 0, 0, 0, 1);
	}

	@Test
	public void testRotateQuaternion() {
		Matrix3 matrix = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).rotate(new Quaternion(4, 3, 2, 0));
		TestUtil.assertEquals(matrix, 0.103448f, 0.827586f, 0.551724f, 0.827586f, -0.37931f, 0.413793f, 0.551724f, 0.413793f, -0.724138f);
	}

	@Test
	public void testTransformVector3() {
		Vector3 vector = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).scale(2, 3, 1).translate(4, 5).transform(new Vector3(3, 2, 1));
		TestUtil.assertEquals(vector, 10, 11, 1);
	}

	@Test
	public void testTransformFloatComponents() {
		Vector3 vector = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).scale(2, 3, 1).translate(4, 5).transform(3, 2, 1);
		TestUtil.assertEquals(vector, 10, 11, 1);
	}

	@Test
	public void testFloor() {
		Matrix3 matrix = new Matrix3(1.1f, 2.9f, 3.5f, -1.1f, -2.5f, -3.9f, -4.2f, 1.4f, 8.6f).floor();
		TestUtil.assertEquals(matrix, 1, 2, 3, -2, -3, -4, -5, 1, 8);
	}

	@Test
	public void testCeiling() {
		Matrix3 matrix = new Matrix3(1.1f, 2.9f, 3.5f, -1.1f, -2.5f, -3.9f, -4.2f, 1.4f, 8.6f).ceil();
		TestUtil.assertEquals(matrix, 2, 3, 4, -1, -2, -3, -4, 2, 9);
	}

	@Test
	public void testRound() {
		Matrix3 matrix = new Matrix3(1.1f, 2.9f, 3.5f, -1.1f, -2.5f, -3.9f, -4.2f, 1.4f, 8.6f).round();
		TestUtil.assertEquals(matrix, 1, 3, 4, -1, -2, -4, -4, 1, 9);
	}

	@Test
	public void testAbsolute() {
		Matrix3 matrix1 = new Matrix3(-1, -1, -1, -1, -1, -1, -1, -1, -1).abs();
		TestUtil.assertEquals(matrix1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
		Matrix3 matrix2 = new Matrix3(1, 1, 1, 1, 1, 1, 1, 1, 1).abs();
		TestUtil.assertEquals(matrix2, 1, 1, 1, 1, 1, 1, 1, 1, 1);
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
		float f = new Matrix3(0, 1, 2, 3, 4, 5, 6, 7, 8).trace();
		TestUtil.assertEquals(f, 12);
	}

	@Test
	public void testDeterminant() {
		float f = new Matrix3(0, 1, 2, 3, 4, 5, 6, 7, 8).determinant();
		TestUtil.assertEquals(f, 0);
	}

	@Test
	public void testInvert() {
		Matrix3 matrix1 = new Matrix3(0, 1, 2, 3, 4, 5, 6, 7, 8).invert();
		Assert.assertEquals(matrix1, null);
		Matrix3 matrix2 = new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1).invert();
		TestUtil.assertEquals(matrix2, 1, 0, 0, 0, 1, 0, 0, 0, 1);
		Matrix3 matrix3 = new Matrix3(1, 2, 3, 3, 1, 2, 2, 3, 1).invert();
		TestUtil.assertEquals(matrix3,
				-0.2777777777f, 0.3888888888f, 0.0555555555f,
				0.0555555555f, -0.2777777777f, 0.3888888888f,
				0.3888888888f, 0.0555555555f, -0.2777777777f);
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
		Assert.assertTrue(new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).equals(new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9)));
		Assert.assertFalse(new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9).equals(new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 0)));
	}

	@Test
	public void testCloning() {
		Matrix3 matrix = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9);
		Assert.assertEquals(matrix, matrix.clone());
	}

	@Test
	public void testCreateFromScalingDoubleFactor() {
		Matrix3 matrix = Matrix3.createScaling(2d);
		TestUtil.assertEquals(matrix, 2, 0, 0, 0, 2, 0, 0, 0, 2);
	}

	@Test
	public void testCreateFromScalingFloatFactor() {
		Matrix3 matrix = Matrix3.createScaling(2);
		TestUtil.assertEquals(matrix, 2, 0, 0, 0, 2, 0, 0, 0, 2);
	}

	@Test
	public void testCreateFromScalingVector3() {
		Matrix3 matrix = Matrix3.createScaling(new Vector3(1, 2, 3));
		TestUtil.assertEquals(matrix, 1, 0, 0, 0, 2, 0, 0, 0, 3);
	}

	@Test
	public void testCreateFromScalingFloatComponents() {
		Matrix3 matrix = Matrix3.createScaling(1, 2, 3);
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
		Matrix3 matrix1 = Matrix3.createTranslation(1, 0);
		TestUtil.assertEquals(matrix1, 1, 0, 1, 0, 1, 0, 0, 0, 1);
		Matrix3 matrix2 = Matrix3.createTranslation(0, 1);
		TestUtil.assertEquals(matrix2, 1, 0, 0, 0, 1, 1, 0, 0, 1);
	}

	@Test
	public void testCreateRotationFromComplex() {
		Matrix3 matrix = Matrix3.createRotation(new Complex(2, 3));
		TestUtil.assertEquals(matrix, 0.5547002f, -0.8320503f, 0, 0.8320503f, 0.5547002f, 0, 0, 0, 1);
	}

	@Test
	public void testCreateRotationFromQuaternion() {
		Matrix3 matrix = Matrix3.createRotation(new Quaternion(4, 3, 2, 0));
		TestUtil.assertEquals(matrix, 0.103448f, 0.827586f, 0.551724f, 0.827586f, -0.37931f, 0.413793f, 0.551724f, 0.413793f, -0.724138f);
	}
}
