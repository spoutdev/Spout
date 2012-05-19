/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.math;

import static org.junit.Assert.fail;

import org.junit.Test;

public class MatrixTest {
	private static final double eps = 0.01;

	private void compareMatrixToArray(Matrix m, double[][] array) {
		for (int y = 0; y < m.getDimension(); y++) {
			for (int x = 0; x < m.getDimension(); x++) {
				if (Math.abs(m.get(x, y) - array[x][y]) > eps) {
					fail("Matrix at " + x + "," + y + " is " + m.get(x, y) + " but it should be " + array[x][y]);
				}
			}
		}

	}

	@Test
	public void testMatrix() {
		Matrix m = new Matrix();
		if (m.getDimension() != 4) {
			fail("Default Constructor should make 4x4, got" + m.getDimension());
		}
		double[][] id = { {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
		compareMatrixToArray(m, id);
	}

	@Test
	public void testMatrixInt() {
		for (int i = 2; i <= 4; i++) {

			Matrix m = new Matrix(i);
			if (m.getDimension() != i) {
				fail("deminsion should be " + i + "x" + i + " , got" + m.getDimension());
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
		Matrix m = new Matrix();
		m.set(0, 0, 12);
		m.set(1, 3, 2);
		double[][] id = { {12, 0, 0, 0}, {0, 1, 0, 2}, {0, 0, 1, 0}, {0, 0, 0, 1}};
		compareMatrixToArray(m, id);
	}

	@Test
	public void testMultiplyMatrix() {
		Matrix a = new Matrix();
		Matrix b = new Matrix();
		Matrix m = MathHelper.multiply(a, b);
		if (m.getDimension() != 4) {
			fail("Default Constructor should make 4x4, got" + m.getDimension());
		}
		double[][] id = { {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
		compareMatrixToArray(m, id);

		Matrix c = new Matrix();
		c.set(1, 3, 4);
		c.set(0, 1, 10);
		Matrix d = new Matrix();
		d.set(3, 2, 4);
		d.set(0, 0, -1);
		m = MathHelper.multiply(c, d);
		if (m.getDimension() != 4) {
			fail("Default Constructor should make 4x4, got" + m.getDimension());
		}
		double[][] mul = { {-1, 10, 0, 0}, {0, 1, 16, 4}, {0, 0, 1, 0}, {0, 0, 4, 1}};

		compareMatrixToArray(m, mul);

		//LookAt Test
		Vector3 center = new Vector3(5, 0, 5);
		Vector3 up = Vector3.UP;
		Vector3 at = Vector3.ZERO;

		Vector3 f = center.subtract(at).normalize();
		up = up.normalize();

		Vector3 s = f.cross(up);
		Vector3 u = s.cross(f);

		Matrix mat = new Matrix(4);

		mat.set(0, 0, s.getX());
		mat.set(0, 1, s.getY());
		mat.set(0, 2, s.getZ());

		mat.set(1, 0, u.getX());
		mat.set(1, 1, u.getY());
		mat.set(1, 2, u.getZ());

		mat.set(2, 0, -f.getX());
		mat.set(2, 1, -f.getY());
		mat.set(2, 2, -f.getZ());

		Matrix trans = MathHelper.translate(center.multiply(-1));
		mat = MathHelper.multiply(mat, trans);
		id = new double[][] { {-0.7071068f, 0.0f, 0.7071068f, 0.0f}, {0.0f, 1.0000001f, 0.0f, 0.0f}, {-0.7071068f, 0.0f, -0.7071068f, 0.0f}, {-5.0f, 0.0f, -5.0f, 1.0f}};
		compareMatrixToArray(mat, id);
	}

	@Test
	public void testAddMatrix() {
		Matrix a = new Matrix();
		Matrix b = new Matrix();
		Matrix m = MathHelper.add(a, b);
		if (m.getDimension() != 4) {
			fail("Default Constructor should make 4x4, got" + m.getDimension());
		}
		double[][] id = { {2, 0, 0, 0}, {0, 2, 0, 0}, {0, 0, 2, 0}, {0, 0, 0, 2}};
		compareMatrixToArray(m, id);

		Matrix c = new Matrix();
		c.set(1, 3, 4);
		c.set(0, 1, 10);
		Matrix d = new Matrix();
		d.set(3, 2, 4);
		d.set(0, 0, -1);
		m = MathHelper.add(c, d);
		if (m.getDimension() != 4) {
			fail("Default Constructor should make 4x4, got" + m.getDimension());
		}
		double[][] mul = { {0, 10, 0, 0}, {0, 2, 0, 4}, {0, 0, 2, 0}, {0, 0, 4, 2}};
		compareMatrixToArray(m, mul);
	}

	@Test
	public void testTranslate() {
		Vector3 a = new Vector3(-1, 2, 4);
		double[][] id = { {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {-1, 2, 4, 1}};
		Matrix m = MathHelper.translate(a);
		compareMatrixToArray(m, id);
	}

	@Test
	public void testScaleDouble() {
		double[][] id = { {5, 0, 0, 0}, {0, 5, 0, 0}, {0, 0, 5, 0}, {0, 0, 0, 1}};
		Matrix m = MathHelper.multiply(5.0f);
		compareMatrixToArray(m, id);
	}

	@Test
	public void testScaleVector3() {
		Vector3 s = new Vector3(-1, 5, 3);
		double[][] id = { {-1, 0, 0, 0}, {0, 5, 0, 0}, {0, 0, 3, 0}, {0, 0, 0, 1}};
		Matrix m = MathHelper.multiply(s);
		compareMatrixToArray(m, id);
	}

	@Test
	public void testRotateX() {
		float theta = 30;
		double[][] id = { {1, 0, 0, 0}, {0, Math.cos(Math.toRadians(theta)), -Math.sin(Math.toRadians(theta)), 0}, {0, Math.sin(Math.toRadians(theta)), Math.cos(Math.toRadians(theta)), 0}, {0, 0, 0, 1}};
		Matrix m = MathHelper.rotateX(theta);
		compareMatrixToArray(m, id);
	}

	@Test
	public void testRotateY() {
		float theta = 10;
		double[][] id = { {Math.cos(Math.toRadians(theta)), 0, Math.sin(Math.toRadians(theta)), 0}, {0, 1, 0, 0}, {-Math.sin(Math.toRadians(theta)), 0, Math.cos(Math.toRadians(theta)), 0}, {0, 0, 0, 1}};
		Matrix m = MathHelper.rotateY(theta);
		compareMatrixToArray(m, id);
	}

	@Test
	public void testRotateZ() {
		float theta = 40;
		double[][] id = { {Math.cos(Math.toRadians(theta)), -Math.sin(Math.toRadians(theta)), 0, 0}, {Math.sin(Math.toRadians(theta)), Math.cos(Math.toRadians(theta)), 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
		compareMatrixToArray(MathHelper.rotateZ(theta), id);
	}

	@Test
	public void testRotate() {
		Matrix m;
		Quaternion rot;

		rot = Quaternion.IDENTITY;
		m = MathHelper.rotate(rot);

		double[][] id = { {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};

		compareMatrixToArray(m, id);

		rot = new Quaternion(4, 3, 2, 0, true);
		m = MathHelper.rotate(rot);

		id = new double[][] { {0.103448, 0.827586, 0.551724, 0}, {0.827586, -0.37931, 0.413793, 0}, {0.551724, 0.413793, -0.724138, 0}, {0, 0, 0, 1}};

		compareMatrixToArray(m, id);

		rot = new Quaternion(90, new Vector3(0, 1, 0));
		m = MathHelper.rotate(rot);
		id = new double[][] { {0, 0, 1, 0}, {0, 1, 0, 0}, {-1, 0, 0, 0}, {0, 0, 0, 1}};

		compareMatrixToArray(m, id);
	}
}
