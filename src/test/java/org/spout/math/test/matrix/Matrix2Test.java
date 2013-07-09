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
		Matrix2 matrix = new Matrix2(new Matrix2(1, 2, 3, 4));
		TestUtil.assertEquals(matrix, 1, 2, 3, 4);
	}

	@Test
	public void testCopyMatrix3Constructor() {
		Matrix2 matrix = new Matrix2(new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9));
		TestUtil.assertEquals(matrix, 1, 2, 4, 5);
	}

	@Test
	public void testCopyMatrix4Constructor() {
		Matrix2 matrix = new Matrix2(new Matrix4(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16));
		TestUtil.assertEquals(matrix, 1, 2, 5, 6);
	}

	@Test
	public void testCopyMatrixNConstructor() {
		Matrix2 matrix = new Matrix2(new MatrixN(2));
		TestUtil.assertEquals(matrix, 1, 0, 0, 1);
	}

	@Test
	public void testDoubleComponentsConstructor() {
		Matrix2 matrix = new Matrix2(1d, 2d, 3d, 4d);
		TestUtil.assertEquals(matrix, 1, 2, 3, 4);
	}

	@Test
	public void testFloatComponentsConstructor() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4);
		TestUtil.assertEquals(matrix, 1, 2, 3, 4);
	}

	@Test
	public void testGetter() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4);
		TestUtil.assertEquals(matrix.get(0, 0), 1);
		TestUtil.assertEquals(matrix.get(0, 1), 2);
		TestUtil.assertEquals(matrix.get(1, 0), 3);
		TestUtil.assertEquals(matrix.get(1, 1), 4);
	}

	@Test
	public void testMatrix2Addition() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4).add(new Matrix2(1, 2, 3, 4));
		TestUtil.assertEquals(matrix, 2, 4, 6, 8);
	}

	@Test
	public void testMatrix2Subtraction() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4).sub(new Matrix2(1, 2, 3, 4));
		TestUtil.assertEquals(matrix, 0, 0, 0, 0);
	}

	@Test
	public void testFloatFactorMultiplication() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4).mul(2f);
		TestUtil.assertEquals(matrix, 2, 4, 6, 8);
	}

	@Test
	public void testDoubleFactorMultiplication() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4).mul(2d);
		TestUtil.assertEquals(matrix, 2, 4, 6, 8);
	}

	@Test
	public void testMatrix2Multiplication() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4).mul(new Matrix2(1, 0, 0, 1));
		TestUtil.assertEquals(matrix, 1, 2, 3, 4);
	}

	@Test
	public void testFloatFactorDivision() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4).div(0.5f);
		TestUtil.assertEquals(matrix, 2, 4, 6, 8);
	}

	@Test
	public void testDoubleFactorDivision() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4).div(0.5);
		TestUtil.assertEquals(matrix, 2, 4, 6, 8);
	}

	@Test
	public void testMatrix2Division() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4).mul(new Matrix2(1, 0, 0, 1));
		TestUtil.assertEquals(matrix, 1, 2, 3, 4);
	}

	@Test
	public void testRaiseToFloatPower() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4).pow(2f);
		TestUtil.assertEquals(matrix, 1, 4, 9, 16);
	}

	@Test
	public void testRaiseToDoublePower() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4).pow(2d);
		TestUtil.assertEquals(matrix, 1, 4, 9, 16);
	}

	@Test
	public void testTranslateFloatDistance() {
		Matrix2 matrix = new Matrix2(1, 0, 0, 1).translate(5f);
		TestUtil.assertEquals(matrix, 1, 5, 0, 1);
	}

	@Test
	public void testScaleDoubleFactor() {
		Matrix2 matrix = new Matrix2(1, 0, 0, 1).scale(2.5);
		TestUtil.assertEquals(matrix, 2.5f, 0, 0, 2.5f);
	}

	@Test
	public void testScaleFloatFactor() {
		Matrix2 matrix = new Matrix2(1, 0, 0, 1).scale(2.5f);
		TestUtil.assertEquals(matrix, 2.5f, 0, 0, 2.5f);
	}

	@Test
	public void testScaleVector2() {
		Matrix2 matrix = new Matrix2(1, 0, 0, 1).scale(new Vector2(2, 3));
		TestUtil.assertEquals(matrix, 2, 0, 0, 3);
	}

	@Test
	public void testScaleFloatComponents() {
		Matrix2 matrix = new Matrix2(1, 0, 0, 1).scale(2, 3);
		TestUtil.assertEquals(matrix, 2, 0, 0, 3);
	}

	@Test
	public void testRotateComplex() {
		Matrix2 matrix = new Matrix2(1, 0, 0, 1).rotate(new Complex(0, 1));
		TestUtil.assertEquals(matrix, 0, -1, 1, 0);
	}

	@Test
	public void testTransformVector2() {
		Vector2 vector = new Matrix2(1, 0, 0, 1).scale(2, 1).translate(4).transform(new Vector2(3, 1));
		TestUtil.assertEquals(vector, 10, 1);
	}

	@Test
	public void testTransformFloatComponents() {
		Vector2 vector = new Matrix2(1, 0, 0, 1).scale(2, 1).translate(4).transform(3, 1);
		TestUtil.assertEquals(vector, 10, 1);
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
		Matrix2 matrix = new Matrix2(1, 2, 3, 4).transpose();
		TestUtil.assertEquals(matrix, 1, 3, 2, 4);
	}

	@Test
	public void testTrace() {
		float f = new Matrix2(1, 2, 3, 4).trace();
		TestUtil.assertEquals(f, 5);
	}

	@Test
	public void testDeterminant() {
		float f = new Matrix2(1, 2, 3, 4).determinant();
		TestUtil.assertEquals(f, -2);
	}

	@Test
	public void testInvert() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4).invert();
		TestUtil.assertEquals(matrix, -2, 1, 1.5f, -0.5f);
	}

	@Test
	public void testConvertToMatrix3() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4);
		TestUtil.assertEquals(matrix.toMatrix3(), 1, 2, 0, 3, 4, 0, 0, 0, 0);
	}

	@Test
	public void testConvertToMatrix4() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4);
		TestUtil.assertEquals(matrix.toMatrix4(), 1, 2, 0, 0, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	@Test
	public void testConvertToMatrixN() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4);
		TestUtil.assertEquals(matrix.toMatrixN(), 1, 2, 3, 4);
	}

	@Test
	public void testConvertToArrayRowMajorDefault() {
		float[] array = new Matrix2(1, 2, 3, 4).toArray(true);
		TestUtil.assertEquals(array, 1, 3, 2, 4);
	}

	@Test
	public void testConvertToArray() {
		float[] array = new Matrix2(1, 2, 3, 4).toArray();
		TestUtil.assertEquals(array, 1, 2, 3, 4);
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(new Matrix2(1, 2, 3, 4).equals(new Matrix2(1, 2, 3, 4)));
		Assert.assertFalse(new Matrix2(1, 2, 3, 4).equals(new Matrix2(1, 2, 3, 5)));
	}

	@Test
	public void testCloning() {
		Matrix2 matrix = new Matrix2(1, 2, 3, 4);
		Assert.assertEquals(matrix, matrix.clone());
	}

	@Test
	public void testCreateFromScalingDoubleFactor() {
		Matrix2 matrix = Matrix2.createScaling(2d);
		TestUtil.assertEquals(matrix, 2, 0, 0, 2);
	}

	@Test
	public void testCreateFromScalingFloatFactor() {
		Matrix2 matrix = Matrix2.createScaling(2);
		TestUtil.assertEquals(matrix, 2, 0, 0, 2);
	}

	@Test
	public void testCreateFromScalingVector2() {
		Matrix2 matrix = Matrix2.createScaling(new Vector2(2, 1.5f));
		TestUtil.assertEquals(matrix, 2, 0, 0, 1.5f);
	}

	@Test
	public void testCreateFromScalingFloatComponents() {
		Matrix2 matrix = Matrix2.createScaling(2, 1.5f);
		TestUtil.assertEquals(matrix, 2, 0, 0, 1.5f);
	}

	@Test
	public void testCreateTranslationFromFloatDistance() {
		Matrix2 matrix = Matrix2.createTranslation(5);
		TestUtil.assertEquals(matrix, 1, 5, 0, 1);
	}

	@Test
	public void testCreateRotationFromComplex() {
		Matrix2 matrix = Matrix2.createRotation(new Complex(2, 3));
		TestUtil.assertEquals(matrix, 0.5547002f, -0.8320503f, 0.8320503f, 0.5547002f);
	}
}
