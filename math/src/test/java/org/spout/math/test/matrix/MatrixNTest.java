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
import org.spout.math.vector.VectorN;

import static org.junit.Assert.assertEquals;

public class MatrixNTest {
	@Test
	public void testSizeConstructor() {
		MatrixN matrix = new MatrixN(4);
		assertEquals(matrix.size(), 4);
	}

	@Test
	public void testCopyMatrix2Constructor() {
		MatrixN matrix = new MatrixN(new Matrix2(1, 2, 3, 4));
		TestUtil.assertEquals(matrix, 1, 2, 3, 4);
	}

	@Test
	public void testCopyMatrix3Constructor() {
		MatrixN matrix = new MatrixN(new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9));
		TestUtil.assertEquals(matrix, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	public void testCopyMatrix4Constructor() {
		MatrixN matrix = new MatrixN(new Matrix4(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16));
		TestUtil.assertEquals(matrix,
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
	}

	@Test
	public void testCopyMatrixNConstructor() {
		MatrixN matrix = new MatrixN(new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16));
		TestUtil.assertEquals(matrix,
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
	}

	@Test
	public void testFloatComponentsConstructor() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
		TestUtil.assertEquals(matrix,
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
	}

	@Test
	public void testSize() {
		MatrixN matrix = new MatrixN(5);
		assertEquals(matrix.size(), 5);
	}

	@Test
	public void testGetter() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
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
	public void testRowGetter() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
		TestUtil.assertEquals(matrix.getRow(0), 1, 2, 3, 4);
		TestUtil.assertEquals(matrix.getRow(1), 5, 6, 7, 8);
		TestUtil.assertEquals(matrix.getRow(2), 9, 10, 11, 12);
		TestUtil.assertEquals(matrix.getRow(3), 13, 14, 15, 16);
	}

	@Test
	public void testColumnGetter() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
		TestUtil.assertEquals(matrix.getColumn(0), 1, 5, 9, 13);
		TestUtil.assertEquals(matrix.getColumn(1), 2, 6, 10, 14);
		TestUtil.assertEquals(matrix.getColumn(2), 3, 7, 11, 15);
		TestUtil.assertEquals(matrix.getColumn(3), 4, 8, 12, 16);
	}

	@Test
	public void testSetterFloatValue() {
		MatrixN matrix = new MatrixN(4);
		matrix.set(0, 0, 1);
		matrix.set(0, 1, 2);
		matrix.set(0, 2, 3);
		matrix.set(0, 3, 4);
		matrix.set(1, 0, 5);
		matrix.set(1, 1, 6);
		matrix.set(1, 2, 7);
		matrix.set(1, 3, 8);
		matrix.set(2, 0, 9);
		matrix.set(2, 1, 10);
		matrix.set(2, 2, 11);
		matrix.set(2, 3, 12);
		matrix.set(3, 0, 13);
		matrix.set(3, 1, 14);
		matrix.set(3, 2, 15);
		matrix.set(3, 3, 16);
		TestUtil.assertEquals(matrix,
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
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
		TestUtil.assertEquals(matrix,
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
	}

	@Test
	public void testSetIdentity() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
		matrix.setIdentity();
		TestUtil.assertEquals(matrix,
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testSetZero() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
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
		MatrixN matrix2 = new MatrixN(4);
		MatrixN resize2 = matrix2.resize(8);
		TestUtil.assertEquals(resize2.size(), 8);
		TestUtil.assertEquals(resize2.get(7, 7), 0);
	}

	@Test
	public void testMatrixNAddition() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
		MatrixN m = new MatrixN(
				1, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1);
		matrix = matrix.add(m);
		TestUtil.assertEquals(matrix,
				2, 3, 4, 5,
				6, 7, 8, 9,
				10, 11, 12, 13,
				14, 15, 16, 17);
	}

	@Test
	public void testMatrixNSubtraction() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
		MatrixN m = new MatrixN(
				1, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1);
		matrix = matrix.sub(m);
		TestUtil.assertEquals(matrix,
				0, 1, 2, 3,
				4, 5, 6, 7,
				8, 9, 10, 11,
				12, 13, 14, 15);
	}

	@Test
	public void testFloatFactorMultiplication() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
		matrix = matrix.mul(2);
		TestUtil.assertEquals(matrix,
				2, 4, 6, 8,
				10, 12, 14, 16,
				18, 20, 22, 24,
				26, 28, 30, 32);
	}

	@Test
	public void testDoubleFactorMultiplication() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
		matrix = matrix.mul(2d);
		TestUtil.assertEquals(matrix,
				2, 4, 6, 8,
				10, 12, 14, 16,
				18, 20, 22, 24,
				26, 28, 30, 32);
	}

	@Test
	public void testMatrixNMultiplication() {
		MatrixN matrix = new MatrixN(
				1, 10, 0, 0,
				0, 1, 0, 4,
				0, 0, 1, 0,
				0, 0, 0, 1);
		MatrixN m = new MatrixN(
				-1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 4, 1);
		matrix = matrix.mul(m);
		TestUtil.assertEquals(matrix,
				-1, 10, 0, 0,
				0, 1, 16, 4,
				0, 0, 1, 0,
				0, 0, 4, 1);
	}

	@Test
	public void testFloatFactorDivision() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
		matrix = matrix.div(0.5f);
		TestUtil.assertEquals(matrix,
				2, 4, 6, 8,
				10, 12, 14, 16,
				18, 20, 22, 24,
				26, 28, 30, 32);
	}

	@Test
	public void testDoubleFactorDivision() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
		matrix = matrix.div(0.5);
		TestUtil.assertEquals(matrix,
				2, 4, 6, 8,
				10, 12, 14, 16,
				18, 20, 22, 24,
				26, 28, 30, 32);
	}

	@Test
	public void testMatrixNDivision() {
		MatrixN matrix = new MatrixN(
				1, 10, 0, 0,
				0, 1, 0, 4,
				0, 0, 1, 0,
				0, 0, 0, 1);
		MatrixN m = new MatrixN(
				-1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 4, 1);
		matrix = matrix.div(m);
		TestUtil.assertEquals(matrix,
				-1, 10, 0, 0,
				0, 1, -16, 4,
				0, 0, 1, 0,
				0, 0, -4, 1);
	}

	@Test
	public void testRaiseToFloatPower() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
		matrix = matrix.pow(2);
		TestUtil.assertEquals(matrix,
				1, 4, 9, 16,
				25, 36, 49, 64,
				81, 100, 121, 144,
				169, 196, 225, 256);
	}

	@Test
	public void testRaiseToDoublePower() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
		matrix = matrix.pow(2d);
		TestUtil.assertEquals(matrix,
				1, 4, 9, 16,
				25, 36, 49, 64,
				81, 100, 121, 144,
				169, 196, 225, 256);
	}

	@Test
	public void testTranslateVectorN() {
		MatrixN matrix = new MatrixN(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
		matrix = matrix.translate(new VectorN(2, 3, 4));
		TestUtil.assertEquals(matrix,
				1, 0, 0, 2,
				0, 1, 0, 3,
				0, 0, 1, 4,
				0, 0, 0, 1);
	}

	@Test
	public void testTranslateFloatComponents() {
		MatrixN matrix = new MatrixN(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
		matrix = matrix.translate(2, 3, 4);
		TestUtil.assertEquals(matrix,
				1, 0, 0, 2,
				0, 1, 0, 3,
				0, 0, 1, 4,
				0, 0, 0, 1);
	}

	@Test
	public void testScaleVectorN() {
		MatrixN matrix = new MatrixN(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
		matrix = matrix.scale(new VectorN(2, 3, 4, 5));
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0,
				0, 3, 0, 0,
				0, 0, 4, 0,
				0, 0, 0, 5);
	}

	@Test
	public void testScaleFloatComponents() {
		MatrixN matrix = new MatrixN(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
		matrix = matrix.scale(2, 3, 4, 5);
		TestUtil.assertEquals(matrix,
				2, 0, 0, 0,
				0, 3, 0, 0,
				0, 0, 4, 0,
				0, 0, 0, 5);
	}

	@Test
	public void testRotateComplex() {
		MatrixN matrix = new MatrixN(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
		matrix = matrix.rotate(new Complex(2, 3));
		TestUtil.assertEquals(matrix,
				0.5547002f, -0.8320503f, 0, 0,
				0.8320503f, 0.5547002f, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testRotateQuaternion() {
		MatrixN matrix = new MatrixN(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
		matrix = matrix.rotate(new Quaternion(4, 3, 2, 0));
		TestUtil.assertEquals(matrix,
				0.103448f, 0.827586f, 0.551724f, 0,
				0.827586f, -0.37931f, 0.413793f, 0,
				0.551724f, 0.413793f, -0.724138f, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testTransformVectorN() {
		MatrixN matrix = new MatrixN(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
		VectorN vector = matrix.scale(2, 3, 4, 1).translate(4, 5, 3).transform(new VectorN(3, 2, 8, 1));
		TestUtil.assertEquals(vector, 10, 11, 35, 1);
	}

	@Test
	public void testTransformFloatComponents() {
		MatrixN matrix = new MatrixN(
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
		VectorN vector = matrix.scale(2, 3, 4, 1).translate(4, 5, 3).transform(3, 2, 8, 1);
		TestUtil.assertEquals(vector, 10, 11, 35, 1);
	}

	@Test
	public void testFloor() {
		MatrixN matrix = new MatrixN(
				-1.1f, 2.5f, 2.9f, 3.2f,
				-6.3f, 2.2f, 2.1f, 5.6f,
				-8.8f, 8.1f, 4.6f, 6.7f,
				-1.3f, 1.7f, 7.3f, 7.6f);
		matrix = matrix.floor();
		TestUtil.assertEquals(matrix,
				-2, 2, 2, 3,
				-7, 2, 2, 5,
				-9, 8, 4, 6,
				-2, 1, 7, 7);
	}

	@Test
	public void testCeiling() {
		MatrixN matrix = new MatrixN(
				-1.1f, 2.5f, 2.9f, 3.2f,
				-6.3f, 2.2f, 2.1f, 5.6f,
				-8.8f, 8.1f, 4.6f, 6.7f,
				-1.3f, 1.7f, 7.3f, 7.6f);
		matrix = matrix.ceil();
		TestUtil.assertEquals(matrix,
				-1, 3, 3, 4,
				-6, 3, 3, 6,
				-8, 9, 5, 7,
				-1, 2, 8, 8);
	}

	@Test
	public void testRound() {
		MatrixN matrix = new MatrixN(
				-1.1f, 2.5f, 2.9f, 3.2f,
				-6.3f, 2.2f, 2.1f, 5.6f,
				-8.8f, 8.1f, 4.6f, 6.7f,
				-1.3f, 1.7f, 7.3f, 7.6f);
		matrix = matrix.round();
		TestUtil.assertEquals(matrix,
				-1, 3, 3, 3,
				-6, 2, 2, 6,
				-9, 8, 5, 7,
				-1, 2, 7, 8);
	}

	@Test
	public void testAbsolute() {
		MatrixN matrix = new MatrixN(
				-1, -1, -1, -1,
				-1, -1, -1, -1,
				-1, -1, -1, -1,
				-1, -1, -1, -1);
		matrix = matrix.abs();
		TestUtil.assertEquals(matrix,
				1, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1);
		MatrixN matrix2 = new MatrixN(
				1, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1);
		matrix2 = matrix2.abs();
		TestUtil.assertEquals(matrix2,
				1, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1);
	}

	@Test
	public void testNegate() {
		MatrixN matrix = new MatrixN(
				1, -1, 1, -1,
				-1, 1, -1, 1,
				1, -1, 1, -1,
				-1, 1, -1, 1);
		matrix = matrix.negate();
		TestUtil.assertEquals(matrix,
				-1, 1, -1, 1,
				1, -1, 1, -1,
				-1, 1, -1, 1,
				1, -1, 1, -1);
	}

	@Test
	public void testTranspose() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				5, 6, 7, 8,
				9, 10, 11, 12,
				13, 14, 15, 16);
		matrix = matrix.transpose();
		TestUtil.assertEquals(matrix,
				1, 5, 9, 13,
				2, 6, 10, 14,
				3, 7, 11, 15,
				4, 8, 12, 16);
	}

	@Test
	public void testTrace() {
		MatrixN matrix = new MatrixN(
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
		float value = matrix.trace();
		TestUtil.assertEquals(value, 10);
	}

	@Test
	public void testDeterminant() {
		MatrixN matrix = new MatrixN(
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
		float value = matrix.determinant();
		TestUtil.assertEquals(value, 24);
	}

	@Test
	public void testInvert() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				4, 1, 2, 3,
				3, 4, 1, 2,
				2, 3, 1, 4);
		matrix = matrix.invert();
		TestUtil.assertEquals(matrix,
				-0.1875f, 0.275f, 0.0625f, -0.05f,
				0.0625f, -0.225f, 0.3125f, -0.05f,
				0.4375f, 0.025f, 0.1875f, -0.55f,
				-0.0625f, 0.025f, -0.3125f, 0.45f);
	}

	@Test
	public void testConvertToMatrix2() {
		MatrixN matrix = new MatrixN(
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
		Matrix2 matrix2 = matrix.toMatrix2();
		TestUtil.assertEquals(matrix2,
				1, 0,
				0, 2);
	}

	@Test
	public void testConvertToMatrix3() {
		MatrixN matrix = new MatrixN(
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
		Matrix3 matrix3 = matrix.toMatrix3();
		TestUtil.assertEquals(matrix3,
				1, 0, 0,
				0, 2, 0,
				0, 0, 3);
	}

	@Test
	public void testConvertToMatrix4() {
		MatrixN matrix = new MatrixN(
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
		Matrix4 matrix4 = matrix.toMatrix4();
		TestUtil.assertEquals(matrix4,
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
	}

	@Test
	public void testConvertToArrayRowMajorDefault() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
		float[] array = matrix.toArray();
		TestUtil.assertEquals(array,
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
	}

	@Test
	public void testConvertToArray() {
		MatrixN matrix1 = new MatrixN(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
		float[] array1 = matrix1.toArray(false);
		TestUtil.assertEquals(array1,
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
		MatrixN matrix2 = new MatrixN(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
		float[] array2 = matrix2.toArray(true);
		TestUtil.assertEquals(array2,
				1, 0, 0, 0,
				2, 2, 0, 0,
				3, 0, 3, 0,
				4, 0, 0, 4);
	}

	@Test
	public void testEquals() {
		MatrixN matrix01 = new MatrixN(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
		MatrixN matrix02 = new MatrixN(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
		Assert.assertTrue(matrix01.equals(matrix02));
		MatrixN matrix11 = new MatrixN(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
		MatrixN matrix12 = new MatrixN(
				1, 2, 3, 4,
				0, 2, 4, 0,
				0, 7, 3, 0,
				0, 0, 0, 4);
		Assert.assertFalse(matrix11.equals(matrix12));
	}

	@Test
	public void testCloning() {
		MatrixN matrix = new MatrixN(
				1, 2, 3, 4,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
		Assert.assertEquals(matrix, matrix.clone());
	}

	@Test
	public void testCreateFromScalingVectorN() {
		MatrixN matrix = MatrixN.createScaling(new VectorN(1, 2, 3, 4));
		TestUtil.assertEquals(matrix,
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
	}

	@Test
	public void testCreateFromScalingFloatComponents() {
		MatrixN matrix = MatrixN.createScaling(1, 2, 3, 4);
		TestUtil.assertEquals(matrix,
				1, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 3, 0,
				0, 0, 0, 4);
	}

	@Test
	public void testCreateTranslationVectorN() {
		MatrixN matrix = MatrixN.createTranslation(new VectorN(1, 2, 3));
		TestUtil.assertEquals(matrix,
				1, 0, 0, 1,
				0, 1, 0, 2,
				0, 0, 1, 3,
				0, 0, 0, 1);
	}

	@Test
	public void testCreateTranslationFloatComponents() {
		MatrixN matrix = MatrixN.createTranslation(1, 2, 3);
		TestUtil.assertEquals(matrix,
				1, 0, 0, 1,
				0, 1, 0, 2,
				0, 0, 1, 3,
				0, 0, 0, 1);
	}

	@Test
	public void testCreateRotationFromComplex() {
		MatrixN matrix = MatrixN.createRotation(4, new Complex(2, 3));
		TestUtil.assertEquals(matrix,
				0.5547002f, -0.8320503f, 0, 0,
				0.8320503f, 0.5547002f, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testCreateRotationFromQuaternion() {
		MatrixN matrix = MatrixN.createRotation(4, new Quaternion(4, 3, 2, 0));
		TestUtil.assertEquals(matrix,
				0.103448f, 0.827586f, 0.551724f, 0,
				0.827586f, -0.37931f, 0.413793f, 0,
				0.551724f, 0.413793f, -0.724138f, 0,
				0, 0, 0, 1);
	}

	@Test
	public void testCreateLookAt() {
		// TODO: figure out a test for this
	}

	@Test
	public void testCreatePerspective() {
		// TODO: figure out a test for this
	}

	@Test
	public void testCreateOrthographic() {
		// TODO: figure out a test for this
	}
}
