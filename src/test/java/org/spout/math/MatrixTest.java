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
package org.spout.math;

import org.junit.Test;

import org.spout.math.imaginary.Quaternion;
import org.spout.math.matrix.MatrixN;
import org.spout.math.vector.Vector3;

import static org.junit.Assert.fail;

public class MatrixTest {
	private static final double eps = 0.01;

	private void compareMatrixToArray(MatrixN m, double[][] array) {
		for (int y = 0; y < m.size(); y++) {
			for (int x = 0; x < m.size(); x++) {
				if (Math.abs(m.get(x, y) - array[x][y]) > eps) {
					fail("Matrix at " + x + "," + y + " is " + m.get(x, y) + " but it should be " + array[x][y]);
				}
			}
		}
	}

	@Test
	public void testMatrix() {
		MatrixN m = new MatrixN(4);
		if (m.size() != 4) {
			fail("Constructor should make 4x4, got" + m.size());
		}
		double[][] id = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
		compareMatrixToArray(m, id);
	}

	@Test
	public void testMatrixInt() {
		for (int i = 2; i <= 4; i++) {

			MatrixN m = new MatrixN(i);
			if (m.size() != i) {
				fail("deminsion should be " + i + "x" + i + " , got" + m.size());
			}
			for (int x = 0; x < i; x++) {
				for (int y = 0; y < i; y++) {
					if (x == y && m.get(x, y) != 1) {
						fail(x + "," + y + "Should be 1, got " + m.get(x, y));
					}
					if (x != y && m.get(x, y) != 0) {
						fail(x + "," + y + "Should be 0, got " + m.get(x, y));
					}
				}
			}
		}
	}

	@Test
	public void testGetAndSet() {
		MatrixN m = new MatrixN(4);
		m.set(0, 0, 12);
		m.set(1, 3, 2);
		double[][] id = {{12, 0, 0, 0}, {0, 1, 0, 2}, {0, 0, 1, 0}, {0, 0, 0, 1}};
		compareMatrixToArray(m, id);
	}

	@Test
	public void testMultiplyMatrix() {
		MatrixN a = new MatrixN(4);
		MatrixN b = new MatrixN(4);
		MatrixN m = a.mul(b);
		if (m.size() != 4) {
			fail("Constructor should make 4x4, got" + m.size());
		}
		double[][] id = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
		compareMatrixToArray(m, id);

		MatrixN c = new MatrixN(4);
		c.set(1, 3, 4);
		c.set(0, 1, 10);
		MatrixN d = new MatrixN(4);
		d.set(3, 2, 4);
		d.set(0, 0, -1);
		m = c.mul(d);
		if (m.size() != 4) {
			fail("Constructor should make 4x4, got" + m.size());
		}
		double[][] mul = {{-1, 10, 0, 0}, {0, 1, 16, 4}, {0, 0, 1, 0}, {0, 0, 4, 1}};

		compareMatrixToArray(m, mul);
	}

	@Test
	public void testAddMatrix() {
		MatrixN a = new MatrixN(4);
		MatrixN b = new MatrixN(4);
		MatrixN m = a.add(b);
		if (m.size() != 4) {
			fail("Constructor should make 4x4, got" + m.size());
		}
		double[][] id = {{2, 0, 0, 0}, {0, 2, 0, 0}, {0, 0, 2, 0}, {0, 0, 0, 2}};
		compareMatrixToArray(m, id);

		MatrixN c = new MatrixN(4);
		c.set(1, 3, 4);
		c.set(0, 1, 10);
		MatrixN d = new MatrixN(4);
		d.set(3, 2, 4);
		d.set(0, 0, -1);
		m = c.add(d);
		if (m.size() != 4) {
			fail("Constructor should make 4x4, got" + m.size());
		}
		double[][] mul = {{0, 10, 0, 0}, {0, 2, 0, 4}, {0, 0, 2, 0}, {0, 0, 4, 2}};
		compareMatrixToArray(m, mul);
	}

	@Test
	public void testTranslate() {
		Vector3 a = new Vector3(-1, 2, 4);
		double[][] id = {{1, 0, 0, -1}, {0, 1, 0, 2}, {0, 0, 1, 4}, {0, 0, 0, 1}};
		MatrixN m = MatrixN.createTranslation(4, a);
		compareMatrixToArray(m, id);
	}

	@Test
	public void testScaleVector3() {
		Vector3 s = new Vector3(-1, 5, 3);
		double[][] id = {{-1, 0, 0, 0}, {0, 5, 0, 0}, {0, 0, 3, 0}, {0, 0, 0, 1}};
		MatrixN m = MatrixN.createScaling(4, s);
		compareMatrixToArray(m, id);
	}

	@Test
	public void testRotate() {
		MatrixN m;
		Quaternion rot;

		rot = Quaternion.IDENTITY;
		m = MatrixN.createRotation(4, rot);

		double[][] id = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};

		compareMatrixToArray(m, id);

		rot = new Quaternion(4, 3, 2, 0);
		m = MatrixN.createRotation(4, rot);

		id = new double[][]{
				{0.103448, 0.827586, 0.551724, 0},
				{0.827586, -0.37931, 0.413793, 0},
				{0.551724, 0.413793, -0.724138, 0},
				{0, 0, 0, 1}
		};

		compareMatrixToArray(m, id);

		rot = Quaternion.fromAngleDegAxis(90, new Vector3(0, 1, 0));
		m = MatrixN.createRotation(4, rot);
		id = new double[][]{{0, 0, 1, 0}, {0, 1, 0, 0}, {-1, 0, 0, 0}, {0, 0, 0, 1}};

		compareMatrixToArray(m, id);
	}
}
