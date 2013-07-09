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
import org.spout.math.matrix.Matrix2;
import org.spout.math.matrix.Matrix3;
import org.spout.math.matrix.Matrix4;
import org.spout.math.matrix.MatrixN;
import org.spout.math.test.TestUtil;
import org.spout.math.vector.Vector3;
import org.spout.math.vector.Vector4;

public class Matrix4Test {
	@Test
	public void testDefaultConstructor() {
		Matrix4 matrix = new Matrix4();
		TestUtil.assertEquals(matrix, 
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testCopyMatrix2Constructor() {
		Matrix4 matrix = new Matrix4(new Matrix2());
		TestUtil.assertEquals(matrix, 
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 0, 0,
				0, 0, 0, 0);
	}

	@Test
	public void testCopyMatrix3Constructor() {
		Matrix4 matrix = new Matrix4(new Matrix3());
		TestUtil.assertEquals(matrix, 
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 0);
	}

	@Test
	public void testCopyMatrix4Constructor() {
		Matrix4 matrix = new Matrix4(new Matrix4(
				1, 2, 3, 4,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1));
		TestUtil.assertEquals(matrix, 
				1, 2, 3, 4,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testCopyMatrixNConstructor() {
		Matrix4 matrix = new Matrix4(new MatrixN(4));
		TestUtil.assertEquals(matrix, 
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testDoubleComponentsConstructor() {
		Matrix4 matrix = new Matrix4(
				1d, 2d, 3d, 4d,
				0d, 1d, 0d, 0d,
				0d, 0d, 1d, 0d,
				0d, 0d, 0d, 1d);
		TestUtil.assertEquals(matrix, 
				1, 2, 3, 4,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testFloatComponentsConstructor() {
		Matrix4 matrix = new Matrix4(
				1f, 2f, 3f, 4f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, 0f,
				0f, 0f, 0f, 1f);
		TestUtil.assertEquals(matrix, 
				1, 2, 3, 4,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testGetter() {
		Matrix4 matrix = new Matrix4(
				1f, 2f, 3f, 4f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, 0f,
				0f, 0f, 0f, 1f);
		TestUtil.assertEquals(matrix.get(0, 0), 1);
		TestUtil.assertEquals(matrix.get(0, 1), 2);
		TestUtil.assertEquals(matrix.get(0, 2), 3);
		TestUtil.assertEquals(matrix.get(0, 3), 4);
		TestUtil.assertEquals(matrix.get(1, 0), 0);
		TestUtil.assertEquals(matrix.get(1, 1), 1);
		TestUtil.assertEquals(matrix.get(1, 2), 0);
		TestUtil.assertEquals(matrix.get(1, 3), 0);
		TestUtil.assertEquals(matrix.get(2, 0), 0);
		TestUtil.assertEquals(matrix.get(2, 1), 0);
		TestUtil.assertEquals(matrix.get(2, 2), 1);
		TestUtil.assertEquals(matrix.get(2, 3), 0);
		TestUtil.assertEquals(matrix.get(3, 0), 0);
		TestUtil.assertEquals(matrix.get(3, 1), 0);
		TestUtil.assertEquals(matrix.get(3, 2), 0);
		TestUtil.assertEquals(matrix.get(3, 3), 1);
	}

	@Test
	public void testMatrix4Addition() {
		Matrix4 matrix = new Matrix4(
				1f, 2f, 3f, 4f,
				5f, 6f, 7f, 8f,
				9f, 10f, 11f, 12f,
				13f, 14f, 15f, 16f).add(new Matrix4(
						1f, 1f, 1f, 1f,
						1f, 1f, 1f, 1f,
						1f, 1f, 1f, 1f,
						1f, 1f, 1f, 1f));
		TestUtil.assertEquals(matrix, 
				2f, 3f, 4f, 5f,
				6f, 7f, 8f, 9f, 
				10f, 11f, 12f, 13f, 
				14f, 15f, 16f, 17f);
	}

	@Test
	public void testMatrix4Subtraction() {
		Matrix4 matrix = new Matrix4(
				1f, 2f, 3f, 4f,
				5f, 6f, 7f, 8f,
				9f, 10f, 11f, 12f,
				13f, 14f, 15f, 16f).sub(new Matrix4(
						1f, 1f, 1f, 1f,
						1f, 1f, 1f, 1f,
						1f, 1f, 1f, 1f,
						1f, 1f, 1f, 1f));
		TestUtil.assertEquals(matrix, 
				0f, 1f, 2f, 3f, 
				4f, 5f, 6f, 7f,
				8f, 9f, 10f, 11f,
				12f, 13f, 14f, 15f);
	}

	@Test
	public void testFloatFactorMultiplication() {
		Matrix4 matrix = new Matrix4(
				1f, 2f, 3f, 4f,
				5f, 6f, 7f, 8f,
				9f, 10f, 11f, 12f,
				13f, 14f, 15f, 16f).mul(2f);
		TestUtil.assertEquals(matrix, 
				2f, 4f, 6f, 8f,
				10f, 12f, 14f, 16f,
				18f, 20f, 22f, 24f,
				26f, 28f, 30f, 32f);
	}

	@Test
	public void testDoubleFactorMultiplication() {
		Matrix4 matrix = new Matrix4(
				1f, 2f, 3f, 4f,
				5f, 6f, 7f, 8f,
				9f, 10f, 11f, 12f,
				13f, 14f, 15f, 16f).mul(2d);
		TestUtil.assertEquals(matrix, 
				2f, 4f, 6f, 8f,
				10f, 12f, 14f, 16f,
				18f, 20f, 22f, 24f,
				26f, 28f, 30f, 32f);
	}

	@Test
	public void testMatrix4Multiplication() {
		Matrix4 matrix = new Matrix4(
				1f, 2f, 3f, 4f,
				5f, 6f, 7f, 8f,
				9f, 10f, 11f, 12f,
				13f, 14f, 15f, 16f).mul(new Matrix4(
						1f, 0f, 0f, 0f,
						0f, 1f, 0f, 0f,
						0f, 0f, 1f, 0f,
						0f, 0f, 0f, 1f));
		TestUtil.assertEquals(matrix, 
				1f, 2f, 3f, 4f,
				5f, 6f, 7f, 8f,
				9f, 10f, 11f, 12f,
				13f, 14f, 15f, 16f);
	}

	@Test
	public void testFloatFactorDivision() {
		Matrix4 matrix = new Matrix4(
				1f, 2f, 3f, 4f,
				5f, 6f, 7f, 8f,
				9f, 10f, 11f, 12f,
				13f, 14f, 15f, 16f).div(0.5f);
		TestUtil.assertEquals(matrix, 
				2f, 4f, 6f, 8f,
				10f, 12f, 14f, 16f,
				18f, 20f, 22f, 24f,
				26f, 28f, 30f, 32f);
	}

	@Test
	public void testDoubleFactorDivision() {
		Matrix4 matrix = new Matrix4(
				1f, 2f, 3f, 4f,
				5f, 6f, 7f, 8f,
				9f, 10f, 11f, 12f,
				13f, 14f, 15f, 16f).div(0.5);
		TestUtil.assertEquals(matrix, 
				2f, 4f, 6f, 8f,
				10f, 12f, 14f, 16f,
				18f, 20f, 22f, 24f,
				26f, 28f, 30f, 32f);
	}

	@Test
	public void testMatrix4Division() {
		// TODO Make Matrix4.div(Matrix4) ?
	}

	@Test
	public void testRaiseToFloatPower() {
		Matrix4 matrix = new Matrix4(
				1f, 2f, 3f, 4f,
				5f, 6f, 7f, 8f,
				9f, 10f, 11f, 12f,
				13f, 14f, 15f, 16f).pow(2f);
		TestUtil.assertEquals(matrix, 
				1f, 4f, 9f, 16f,
				25f, 36f, 49f, 64f,
				81f, 100f, 121f, 144f,
				169f, 196f, 225f, 256f);
	}

	@Test
	public void testRaiseToDoublePower() {
		Matrix4 matrix = new Matrix4(
				1f, 2f, 3f, 4f,
				5f, 6f, 7f, 8f,
				9f, 10f, 11f, 12f,
				13f, 14f, 15f, 16f).pow(2d);
		TestUtil.assertEquals(matrix, 
				1f, 4f, 9f, 16f,
				25f, 36f, 49f, 64f,
				81f, 100f, 121f, 144f,
				169f, 196f, 225f, 256f);
	}

	@Test
	public void testTranslateVector3() {
		Matrix4 matrix = new Matrix4(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).translate(new Vector3(1, 0, 0));
		TestUtil.assertEquals(matrix,
				1, 0, 0, 1,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testTranslateFloatComponents() {
		Matrix4 matrix = new Matrix4(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).translate(1f, 0f, 0f);
		TestUtil.assertEquals(matrix,
				1, 0, 0, 1,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testScaleDoubleFactor() {
		Matrix4 matrix = new Matrix4(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).scale(2d);
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 2);
	}

	@Test
	public void testScaleFloatFactor() {
		Matrix4 matrix = new Matrix4(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).scale(2f);
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 2);
	}

	@Test
	public void testScaleVector4() {
		Matrix4 matrix = new Matrix4(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).scale(new Vector4(2, 2, 2, 2));
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 2);
	}

	@Test
	public void testScaleFloatComponents() {
		Matrix4 matrix = new Matrix4(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).scale(2f, 2f, 2f, 2f);
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 2);
	}

	@Test
	public void testRotateComplex() {

	}

	@Test
	public void testRotateQuaternion() {

	}

	@Test
	public void testTransformVector4() {

	}

	@Test
	public void testTransformFloatComponents() {

	}

	@Test
	public void testFloor() {
		Matrix4 matrix = new Matrix4(
				1.1, 2.5, 2.9, 0,
				-1.1, -2.5, -2.9, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).floor();
		TestUtil.assertEquals(matrix,
				1, 2, 2, 0,
				-2, -3, -3, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testCeiling() {
		Matrix4 matrix = new Matrix4(
				1.1, 2.5, 2.9, 0,
				-1.1, -2.5, -2.9, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).ceil();
		TestUtil.assertEquals(matrix,
				2, 3, 3, 0,
				-1, -2, -2, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testRound() {
		Matrix4 matrix = new Matrix4(
				1.1, 2.5, 2.9, 0,
				-1.1, -2.5, -2.9, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).round();
		TestUtil.assertEquals(matrix,
				1, 3, 3, 0,
				-1, -2, -3, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testAbsolute() {
		Matrix4 matrix = new Matrix4(
				1.1, 2.5, 2.9, 0,
				-1.1, -2.5, -2.9, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).abs();
		TestUtil.assertEquals(matrix,
				1.1f, 2.5f, 2.9f, 0,
				1.1f, 2.5f, 2.9f, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testNegate() {
		Matrix4 matrix = new Matrix4(
				1.1, 2.5, 2.9, 0,
				-1.1, -2.5, -2.9, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).negate();
		TestUtil.assertEquals(matrix,
				-1.1f, -2.5f, -2.9f, 0,
				1.1f, 2.5f, 2.9f, 0,
				0, 0, -1, 0,
				0, 0, 0, -1);
	}

	@Test
	public void testTranspose() {
		Matrix4 matrix = new Matrix4(
				1, 2, 3, 4,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).transpose();
		TestUtil.assertEquals(matrix,
				1, 0, 0, 0,
				2, 1, 0, 0,
				3, 0, 1, 0,
				4, 0, 0, 1);
	}

	@Test
	public void testTrace() {
		float value = new Matrix4(
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4).trace();
		TestUtil.assertEquals(value, 10);
	}

	@Test
	public void testDeterminant() {
		float value = new Matrix4(
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4).determinant();
		TestUtil.assertEquals(value, 24);
	}

	@Test
	public void testInvert() {
		Matrix4 matrix = new Matrix4(
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4).invert();
		TestUtil.assertEquals(matrix,
				1, 0, 0, 0,
				0, 0.5f, 0, 0,
				0, 0, 0.33333333f, 0,
				0, 0, 0, 0.25f);
	}

	@Test
	public void testConvertToMatrix2() {
		Matrix2 matrix = new Matrix4(
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4).toMatrix2();
		TestUtil.assertEquals(matrix,
				1, 0,
				0, 2);
	}

	@Test
	public void testConvertToMatrix3() {
		Matrix3 matrix = new Matrix4(
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4).toMatrix3();
		TestUtil.assertEquals(matrix,
				1, 0, 0,
				0, 2, 0,
				0, 0, 3);
	}

	@Test
	public void testConvertToMatrixN() {
		MatrixN matrix = new Matrix4(
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4).toMatrixN();
		TestUtil.assertEquals(matrix,
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
	}

	@Test
	public void testConvertToArrayRowMajorDefault() {
		float[] array = new Matrix4(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4).toArray(true);
		TestUtil.assertEquals(array,
				1, 0, 0, 0,
				2, 2, 0, 0,
				3, 0, 3, 0,
				4, 0, 0, 4);
	}

	@Test
	public void testConvertToArray() {
		float[] array = new Matrix4(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4).toArray();
		TestUtil.assertEquals(array,
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
	}

	@Test
	public void testEquals() {
		Assert.assertEquals(new Matrix4(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4), new Matrix4(
						1, 2, 3, 4,
						0, 2, 0, 0,
						0, 0, 3, 0,
						0, 0, 0, 4));
		Assert.assertNotEquals(new Matrix4(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4), new Matrix4(
						1, 2, 3, 4,
						0, 2, 4, 0,
						0, 7, 3, 0,
						0, 0, 0, 4));
	}

	@Test
	public void testCloning() {
		Matrix4 matrix = new Matrix4(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
		Assert.assertEquals(matrix, matrix.clone());
	}

	@Test
	public void testCreateFromScalingDoubleFactor() {
		Matrix4 matrix = Matrix4.createScaling(2d);
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 2);
	}

	@Test
	public void testCreateFromScalingFloatFactor() {
		Matrix4 matrix = Matrix4.createScaling(2f);
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 2);
	}

	@Test
	public void testCreateFromScalingVector4() {
		Matrix4 matrix = Matrix4.createScaling(new Vector4(1, 2, 3, 4));
		TestUtil.assertEquals(matrix,
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
	}

	@Test
	public void testCreateFromScalingFloatComponents() {
		Matrix4 matrix = Matrix4.createScaling(1f, 2f, 3f, 4f);
		TestUtil.assertEquals(matrix,
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
	}

	@Test
	public void testCreateTranslationVector3() {
		Matrix4 matrix = Matrix4.createTranslation(new Vector3(1, 2, 3));
		TestUtil.assertEquals(matrix,
				1, 0, 0, 1,
				0, 1, 0, 2,
				0, 0, 1, 3,
				0, 0, 0, 1);
	}

	@Test
	public void testCreateTranslationFloatComponents() {
		Matrix4 matrix = Matrix4.createTranslation(1f, 2f, 3f);
		TestUtil.assertEquals(matrix,
				1, 0, 0, 1,
				0, 1, 0, 2,
				0, 0, 1, 3,
				0, 0, 0, 1);
	}

	@Test
	public void testCreateRotationFromComplex() {

	}

	@Test
	public void testCreateRotationFromQuaternion() {

	}

	@Test
	public void testCreateLookAt() {

	}

	@Test
	public void testCreatePerspective() {

	}

	@Test
	public void testCreateOrthographic() {

	}
}
