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

import org.junit.Assert;
import org.junit.Test;
import org.spout.math.matrix.Matrix2;
import org.spout.math.matrix.Matrix3;
import org.spout.math.matrix.Matrix4;
import org.spout.math.matrix.MatrixN;
import org.spout.math.test.TestUtil;
import org.spout.math.vector.VectorN;

public class MatrixNTest {
	@Test
	public void testSizeConstructor() {
		MatrixN matrix  = new MatrixN(4);
		assertEquals(matrix.size(), 4);
	}

	@Test
	public void testCopyMatrix2Constructor() {
		MatrixN matrix  = new MatrixN(new Matrix2(1, 0, 0, 1));
		TestUtil.assertEquals(matrix, 1, 0, 0, 1);
	}

	@Test
	public void testCopyMatrix3Constructor() {
		MatrixN matrix  = new MatrixN(new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1));
		TestUtil.assertEquals(matrix, 1, 0, 0, 0, 1, 0, 0, 0, 1);
	}

	@Test
	public void testCopyMatrix4Constructor() {
		MatrixN matrix  = new MatrixN(new Matrix4(
				1, 0, 0, 0, 
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1));
		TestUtil.assertEquals(matrix,
				1, 0, 0, 0, 
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testCopyMatrixNConstructor() {
		MatrixN matrix  = new MatrixN(new MatrixN(4));
		TestUtil.assertEquals(matrix,
				1, 0, 0, 0, 
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testDoubleComponentsConstructor() {

	}

	@Test
	public void testFloatComponentsConstructor() {

	}

	@Test
	public void testSize() {
		MatrixN matrix = new MatrixN(4);
		assertEquals(matrix.size(), 4);
	}

	@Test
	public void testGetter() {
		MatrixN matrix = new MatrixN(4);
		TestUtil.assertEquals(matrix.get(0, 0), 1);
		TestUtil.assertEquals(matrix.get(0, 1), 0);
		TestUtil.assertEquals(matrix.get(0, 2), 0);
		TestUtil.assertEquals(matrix.get(0, 3), 0);
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
	public void testSetterFloatValue() {
		MatrixN matrix = new MatrixN(4);
		matrix.set(0, 0, 1f);
		matrix.set(0, 1, 2f);
		matrix.set(0, 2, 3f);
		matrix.set(0, 3, 4f);
		matrix.set(1, 0, 5f);
		matrix.set(1, 1, 6f);
		matrix.set(1, 2, 7f);
		matrix.set(1, 3, 8f);
		matrix.set(2, 0, 9f);
		matrix.set(2, 1, 10f);
		matrix.set(2, 2, 11f);
		matrix.set(2, 3, 12f);
		matrix.set(3, 0, 13f);
		matrix.set(3, 1, 14f);
		matrix.set(3, 2, 15f);
		matrix.set(3, 3, 16f);
		TestUtil.assertEquals(matrix.get(0, 0), 1);
		TestUtil.assertEquals(matrix.get(0, 1), 2);
		TestUtil.assertEquals(matrix.get(0, 2), 3);
		TestUtil.assertEquals(matrix.get(0, 3), 4);
		TestUtil.assertEquals(matrix.get(1, 0), 5);
		TestUtil.assertEquals(matrix.get(1, 1), 6);
		TestUtil.assertEquals(matrix.get(1, 2), 7);
		TestUtil.assertEquals(matrix.get(1, 3), 8);
		TestUtil.assertEquals(matrix.get(2, 0), 9);
		TestUtil.assertEquals(matrix.get(2, 1), 10);
		TestUtil.assertEquals(matrix.get(2, 2), 11);
		TestUtil.assertEquals(matrix.get(2, 3), 12);
		TestUtil.assertEquals(matrix.get(3, 0), 13);
		TestUtil.assertEquals(matrix.get(3, 1), 14);
		TestUtil.assertEquals(matrix.get(3, 2), 15);
		TestUtil.assertEquals(matrix.get(3, 3), 16);
	}

	@Test
	public void testSetterDoubleValue() {
		MatrixN matrix = new MatrixN(4);
		matrix.set(0, 0, 1d);
		matrix.set(0, 1, 2d);
		matrix.set(0, 2, 3d);
		matrix.set(0, 3, 4d);
		matrix.set(1, 0, 5d);
		matrix.set(1, 1, 6d);
		matrix.set(1, 2, 7d);
		matrix.set(1, 3, 8d);
		matrix.set(2, 0, 9d);
		matrix.set(2, 1, 10d);
		matrix.set(2, 2, 11d);
		matrix.set(2, 3, 12d);
		matrix.set(3, 0, 13d);
		matrix.set(3, 1, 14d);
		matrix.set(3, 2, 15d);
		matrix.set(3, 3, 16d);
		TestUtil.assertEquals(matrix.get(0, 0), 1);
		TestUtil.assertEquals(matrix.get(0, 1), 2);
		TestUtil.assertEquals(matrix.get(0, 2), 3);
		TestUtil.assertEquals(matrix.get(0, 3), 4);
		TestUtil.assertEquals(matrix.get(1, 0), 5);
		TestUtil.assertEquals(matrix.get(1, 1), 6);
		TestUtil.assertEquals(matrix.get(1, 2), 7);
		TestUtil.assertEquals(matrix.get(1, 3), 8);
		TestUtil.assertEquals(matrix.get(2, 0), 9);
		TestUtil.assertEquals(matrix.get(2, 1), 10);
		TestUtil.assertEquals(matrix.get(2, 2), 11);
		TestUtil.assertEquals(matrix.get(2, 3), 12);
		TestUtil.assertEquals(matrix.get(3, 0), 13);
		TestUtil.assertEquals(matrix.get(3, 1), 14);
		TestUtil.assertEquals(matrix.get(3, 2), 15);
		TestUtil.assertEquals(matrix.get(3, 3), 16);
	}

	@Test
	public void testSetIdentity() {
		MatrixN matrix = new MatrixN(4);
		matrix.setIdentity();
		TestUtil.assertEquals(matrix,
				1, 0, 0, 0, 
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testSetZero() {
		MatrixN matrix = new MatrixN(4);
		matrix.setZero();
		TestUtil.assertEquals(matrix,
				0, 0, 0, 0, 
				0, 0, 0, 0,
				0, 0, 0, 0,
				0, 0, 0, 0);
	}

	@Test
	public void testResize() {
		MatrixN matrix1 = new MatrixN(4);
		MatrixN resize1 = matrix1.resize(2);
		TestUtil.assertEquals(resize1.size(), 2);
		MatrixN matrix2 = new MatrixN(8);
		MatrixN resize2 = matrix2.resize(4);
		TestUtil.assertEquals(resize2.size(), 4);
		TestUtil.assertEquals(resize2.get(3, 3), 1);
	}

	@Test
	public void testMatrixNAddition() {
		MatrixN matrix = new MatrixN(4).add(new MatrixN(4));
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0, 
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 2);
	}

	@Test
	public void testMatrixNSubtraction() {
		MatrixN matrix = new MatrixN(4).sub(new MatrixN(4));
		TestUtil.assertEquals(matrix,
				0, 0, 0, 0, 
				0, 0, 0, 0,
				0, 0, 0, 0,
				0, 0, 0, 0);
	}

	@Test
	public void testFloatFactorMultiplication() {
		MatrixN matrix = new MatrixN(4).mul(2f);
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0, 
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 2);
	}

	@Test
	public void testDoubleFactorMultiplication() {
		MatrixN matrix = new MatrixN(4).mul(2d);
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0, 
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 2);
	}

	@Test
	public void testMatrixNMultiplication() {

	}

	@Test
	public void testFloatFactorDivision() {
		MatrixN matrix = new MatrixN(4).div(0.5f);
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0, 
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 2);
	}

	@Test
	public void testDoubleFactorDivision() {
		MatrixN matrix = new MatrixN(4).div(0.5d);
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0, 
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 2);
	}

	@Test
	public void testMatrixNDivision() {

	}

	@Test
	public void testRaiseToFloatPower() {
		MatrixN matrix = new MatrixN(new Matrix4(
				1f, 2f, 3f, 4f,
				5f, 6f, 7f, 8f,
				9f, 10f, 11f, 12f,
				13f, 14f, 15f, 16f)).pow(2f);
		TestUtil.assertEquals(matrix, 
				1f, 4f, 9f, 16f,
				25f, 36f, 49f, 64f,
				81f, 100f, 121f, 144f,
				169f, 196f, 225f, 256f);
	}

	@Test
	public void testRaiseToDoublePower() {
		MatrixN matrix = new MatrixN(new Matrix4(
				1f, 2f, 3f, 4f,
				5f, 6f, 7f, 8f,
				9f, 10f, 11f, 12f,
				13f, 14f, 15f, 16f)).pow(2d);
		TestUtil.assertEquals(matrix, 
				1f, 4f, 9f, 16f,
				25f, 36f, 49f, 64f,
				81f, 100f, 121f, 144f,
				169f, 196f, 225f, 256f);
	}

	@Test
	public void testTranslateVectorN() {
		MatrixN matrix = new MatrixN(4).translate(new VectorN(1f, 0f, 0f));
		TestUtil.assertEquals(matrix,
				1, 0, 0, 1,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testTranslateFloatComponents() {
		MatrixN matrix = new MatrixN(4).translate(1f, 0f, 0f);
		TestUtil.assertEquals(matrix,
				1, 0, 0, 1,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testScaleVectorN() {
		MatrixN matrix = new MatrixN(4).scale(new VectorN(2f, 2f, 2f, 2f));
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 2);
	}

	@Test
	public void testScaleFloatComponents() {
		MatrixN matrix = new MatrixN(4).scale(2f, 2f, 2f, 2f);
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
	public void testTransformVectorN() {

	}

	@Test
	public void testTransformFloatComponents() {

	}

	@Test
	public void testFloor() {
		MatrixN matrix = new Matrix4(
				1.1, 2.5, 2.9, 0,
				-1.1, -2.5, -2.9, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).toMatrixN().floor();
		TestUtil.assertEquals(matrix,
				1, 2, 2, 0,
				-2, -3, -3, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testCeiling() {
		MatrixN matrix = new Matrix4(
				1.1, 2.5, 2.9, 0,
				-1.1, -2.5, -2.9, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).toMatrixN().ceil();
		TestUtil.assertEquals(matrix,
				2, 3, 3, 0,
				-1, -2, -2, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testRound() {
		MatrixN matrix = new Matrix4(
				1.1, 2.5, 2.9, 0,
				-1.1, -2.5, -2.9, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).toMatrixN().round();
		TestUtil.assertEquals(matrix,
				1, 3, 3, 0,
				-1, -2, -3, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testAbsolute() {
		MatrixN matrix = new Matrix4(
				1.1, 2.5, 2.9, 0,
				-1.1, -2.5, -2.9, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).toMatrixN().abs();
		TestUtil.assertEquals(matrix,
				1.1f, 2.5f, 2.9f, 0,
				1.1f, 2.5f, 2.9f, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testNegate() {
		MatrixN matrix = new Matrix4(
				1.1, 2.5, 2.9, 0,
				-1.1, -2.5, -2.9, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).toMatrixN().negate();
		TestUtil.assertEquals(matrix,
				-1.1f, -2.5f, -2.9f, 0,
				1.1f, 2.5f, 2.9f, 0,
				0, 0, -1, 0,
				0, 0, 0, -1);
	}

	@Test
	public void testTranspose() {
		MatrixN matrix = new Matrix4(
				1, 2, 3, 4,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1).toMatrixN().transpose();
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
				0, 0, 0, 4).toMatrixN().trace();
		TestUtil.assertEquals(value, 10);
	}

	@Test
	public void testDeterminant() {
		float value = new Matrix4(
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4).toMatrixN().determinant();
		TestUtil.assertEquals(value, 24);
	}

	@Test
	public void testInvert() {
		MatrixN matrix = new Matrix4(
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4).toMatrixN().invert();
		TestUtil.assertEquals(matrix,
				1, 0, 0, 0,
				0, 0.5f, 0, 0,
				0, 0, 0.33333333f, 0,
				0, 0, 0, 0.25f);
	}

	@Test
	public void testConvertToMatrix2() {
		Matrix2 matrix = new MatrixN(4).toMatrix2();
		TestUtil.assertEquals(matrix,
				1, 0,
				0, 1);
	}

	@Test
	public void testConvertToMatrix3() {
		Matrix3 matrix = new MatrixN(4).toMatrix3();
		TestUtil.assertEquals(matrix,
				1, 0, 0,
				0, 1, 0,
				0, 0, 1);
	}

	@Test
	public void testConvertToMatrix4() {
		Matrix4 matrix = new MatrixN(4).toMatrix4();
		TestUtil.assertEquals(matrix,
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testConvertToArrayRowMajorDefault() {
		float[] array = new MatrixN(new Matrix4(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4)).toArray(true);
		TestUtil.assertEquals(array,
				1, 0, 0, 0,
				2, 2, 0, 0,
				3, 0, 3, 0,
				4, 0, 0, 4);
	}

	@Test
	public void testConvertToArray() {
		float[] array = new MatrixN(new Matrix4(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4)).toArray();
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
				0, 0, 0, 4).toMatrixN(), new Matrix4(
						1, 2, 3, 4,
						0, 2, 0, 0,
						0, 0, 3, 0,
						0, 0, 0, 4).toMatrixN());
		Assert.assertNotEquals(new Matrix4(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4).toMatrixN(), new Matrix4(
						1, 2, 3, 4,
						0, 2, 4, 0,
						0, 7, 3, 0,
						0, 0, 0, 4).toMatrixN());
	}

	@Test
	public void testCloning() {
		MatrixN matrix = new Matrix4(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4).toMatrixN();
		Assert.assertEquals(matrix, matrix.clone());
	}

	@Test
	public void testCreateFromScalingDoubleFactor() {

	}

	@Test
	public void testCreateFromScalingFloatFactor() {

	}

	@Test
	public void testCreateFromScalingVectorN() {
		MatrixN matrix = MatrixN.createScaling(new VectorN(2f, 2f, 2f, 2f));
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 2);
	}

	@Test
	public void testCreateFromScalingFloatComponents() {
		MatrixN matrix = MatrixN.createScaling(2f, 2f, 2f, 2f);
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 2, 0,
				0, 0, 0, 2);
	}

	@Test
	public void testCreateTranslationVectorN() {
		MatrixN matrix = MatrixN.createTranslation(new VectorN(2f, 2f, 2f));
		TestUtil.assertEquals(matrix,
				1, 0, 0, 2,
				0, 1, 0, 2,
				0, 0, 1, 2,
				0, 0, 0, 1);
	}

	@Test
	public void testCreateTranslationFloatComponents() {
		MatrixN matrix = MatrixN.createTranslation(2f, 2f, 2f);
		TestUtil.assertEquals(matrix,
				1, 0, 0, 2,
				0, 1, 0, 2,
				0, 0, 1, 2,
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
