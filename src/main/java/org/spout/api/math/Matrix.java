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

/**
 * Representation of a square matrix
 */
public class Matrix {
	int dimension;
	float[] data;

	/**
	 * Creates a new 4x4 matrix, set to the Identity Matrix
	 */
	public Matrix() {
		this(4);
	}

	/**
	 * Creates a new matrix with the given dimension
	 *
	 * @param dim
	 */
	public Matrix(int dim) {
		dimension = dim;
		data = new float[dim * dim];
		for (int x = 0; x < dim; x++) {
			for (int y = 0; y < dim; y++) {
				if (x == y) {
					data[index(x, y, dim)] = 1;
				} else {
					data[index(x, y, dim)] = 0;
				}
			}
		}
	}

	/**
	 * Creates a new matrix from the given dimension and given data in column
	 * major order
	 *
	 * @param dim
	 * @param dat
	 */
	public Matrix(int dim, float[] dat) {
		dimension = dim;
		data = dat.clone();
	}
	/**
	 * Creates a new copy of provided matrix
	 * @param copy
	 */
	public Matrix(Matrix copy){
		this(copy.dimension, copy.data);
	}

	public int getDimension() {
		return dimension;
	}

	/**
	 * Gets the value at the given row and colum
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	public float get(int row, int column) {
		if (row < 0 || row >= dimension) {
			throw new IllegalArgumentException("Row must be between 0 and " + (dimension - 1));
		}
		if (column < 0 || column >= dimension) {
			throw new IllegalArgumentException("Column must be between 0 and " + (dimension - 1));
		}
		return data[index(row, column, dimension)];
	}

	/**
	 * Sets the value at the given row and column
	 *
	 * @param row
	 * @param column
	 * @param value
	 */
	public void set(int row, int column, float value) {
		if (row < 0 || row >= dimension) {
			throw new IllegalArgumentException("Row must be between 0 and " + (dimension - 1));
		}
		if (column < 0 || column >= dimension) {
			throw new IllegalArgumentException("Column must be between 0 and " + (dimension - 1));
		}
		data[index(row, column, dimension)] = value;
	}

	/**
	 * Multiplies this matrix with the provided matrix
	 *
	 * @param that
	 * @return
	 */
	public Matrix multiply(Matrix that) {
		return Matrix.multiply(this, that);
	}

	/**
	 * Adds this matrix to the given matrix
	 *
	 * @param that
	 * @return
	 */
	public Matrix add(Matrix that) {
		return Matrix.add(this, that);
	}

	/**
	 * Returns this matrix in a single dimension float array
	 *
	 * @return
	 */
	public float[] toArray() {
		return Matrix.toArray(this);
	}

	/**
	 * Adds two matricies together
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Matrix add(Matrix a, Matrix b) {
		if (a.dimension != b.dimension) {
			throw new IllegalArgumentException("Matrix Dimensions must be equal");
		}
		Matrix res = new Matrix(a.dimension);
		for (int x = 0; x < res.dimension; x++) {
			for (int y = 0; y < res.dimension; y++) {
				res.data[index(x, y, res.dimension)] = a.data[index(x, y, res.dimension)] + b.data[index(x, y, res.dimension)];
			}
		}
		return res;
	}

	/**
	 * Multiplies two matricies together
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Matrix multiply(Matrix a, Matrix b) {
		if (a.dimension != b.dimension) {
			throw new IllegalArgumentException("Matrix Dimensions must be equal");
		}
		Matrix res = new Matrix(a.dimension);
		for (int i = 0; i < res.dimension; i++) {
			for (int j = 0; j < res.dimension; j++) {
				res.set(i, j, 0);
				for (int k = 0; k < res.dimension; k++) {
					float r = a.get(i, k) * b.get(k, j);
					res.set(i, j, res.get(i, j) + r);

				}
			}
		}
		return res;
	}

	private static int index(int x, int y, int dim) {
		return x * dim + y;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int y = 0; y < dimension; y++) {
			sb.append("[ ");
			for (int x = 0; x < dimension; x++) {
				sb.append(get(y, x));
				if (x != dimension - 1) {
					sb.append(" , ");
				}
			}
			sb.append(" ]\n");
		}
		return sb.toString();
	}

	/**
	 * Creates and returns a 4x4 identity matrix
	 *
	 * @return
	 */
	public static Matrix createIdentity() {
		return new Matrix(4);
	}

	/**
	 * Creates and returns a 4x4 matrix that represents the translation provided
	 * by the given Vector3
	 *
	 * @param vector
	 * @return
	 */
	public static Matrix translate(Vector3 vector) {
		Matrix res = createIdentity();
		res.set(3, 0, vector.getX());
		res.set(3, 1, vector.getY());
		res.set(3, 2, vector.getZ());
		return res;
	}

	/**
	 * Creates and returns a 4x4 uniform scalar matrix
	 *
	 * @param ammount
	 * @return
	 */
	public static Matrix multiply(float ammount) {
		Matrix res = createIdentity();
		res.set(0, 0, ammount);
		res.set(1, 1, ammount);
		res.set(2, 2, ammount);
		return res;
	}

	/**
	 * Creates and returns a 4x4 scalar matrix that multiplys each axis given by
	 * the provided Vector3
	 *
	 * @param ammount
	 * @return
	 */
	public static Matrix multiply(Vector3 ammount) {
		Matrix res = createIdentity();
		res.set(0, 0, ammount.getX());
		res.set(1, 1, ammount.getY());
		res.set(2, 2, ammount.getZ());
		return res;
	}

	/**
	 * Creates and returns a 4x4 rotation matrix around the X axis
	 *
	 * @param rot
	 * @return
	 */
	public static Matrix rotateX(float rot) {
		Matrix res = createIdentity();
		res.set(1, 1, (float) Math.cos(Math.toRadians(rot)));
		res.set(1, 2, (float) -Math.sin(Math.toRadians(rot)));
		res.set(2, 1, (float) Math.sin(Math.toRadians(rot)));
		res.set(2, 2, (float) Math.cos(Math.toRadians(rot)));

		return res;
	}

	/**
	 * Creates and returns a 4x4 rotation matrix around the Y axis
	 *
	 * @param rot
	 * @return
	 */
	public static Matrix rotateY(float rot) {
		Matrix res = createIdentity();
		res.set(0, 0, (float) Math.cos(Math.toRadians(rot)));
		res.set(0, 2, (float) Math.sin(Math.toRadians(rot)));
		res.set(2, 0, (float) -Math.sin(Math.toRadians(rot)));
		res.set(2, 2, (float) Math.cos(Math.toRadians(rot)));
		return res;
	}

	/**
	 * Creates and returns a 4x4 rotation matrix around the Z axis
	 *
	 * @param rot
	 * @return
	 */
	public static Matrix rotateZ(float rot) {
		Matrix res = createIdentity();
		res.set(0, 0, (float) Math.cos(Math.toRadians(rot)));
		res.set(0, 1, (float) -Math.sin(Math.toRadians(rot)));
		res.set(1, 0, (float) Math.sin(Math.toRadians(rot)));
		res.set(1, 1, (float) Math.cos(Math.toRadians(rot)));
		return res;
	}

	/**
	 * Creates and returns a 4x4 rotation matrix given by the provided
	 * Quaternion
	 *
	 * @param rot
	 * @return
	 */
	public static Matrix rotate(Quaternion rot) {
		Matrix res = createIdentity();
		Quaternion r = rot.normalize(); //Confirm that we are dealing with a unit quaternion

		res.set(0, 0, 1 - 2 * r.getY() * r.getY() - 2 * r.getZ() * r.getZ());
		res.set(0, 1, 2 * r.getX() * r.getY() - 2 * r.getW() * r.getZ());
		res.set(0, 2, 2 * r.getX() * r.getZ() + 2 * r.getW() * r.getY());
		res.set(0, 3, 0);

		res.set(1, 0, 2 * r.getX() * r.getY() + 2 * r.getW() * r.getZ());
		res.set(1, 1, 1 - 2 * r.getX() * r.getX() - 2 * r.getZ() * r.getZ());
		res.set(1, 2, 2 * r.getY() * r.getZ() - 2 * r.getW() * r.getX());
		res.set(1, 3, 0);

		res.set(2, 0, 2 * r.getX() * r.getZ() - 2 * r.getW() * r.getY());
		res.set(2, 1, 2.f * r.getY() * r.getZ() + 2.f * r.getX() * r.getW());
		res.set(2, 2, 1 - 2 * r.getX() * r.getX() - 2 * r.getY() * r.getY());
		res.set(2, 3, 0);

		//3, [0-3] will be 0,0,0,1 due to identity matrix

		return res;
	}

	public static Vector3 transform(Vector3 v, Matrix m) {
		float[] vector = {v.getX(), v.getY(), v.getZ(), 1};
		float[] vres = new float[4];
		for (int i = 0; i < m.dimension; i++) {
			vres[i] = 0;
			for (int k = 0; k < m.dimension; k++) {
				float n = m.get(i, k) * vector[k];
				vres[i] += n;

			}
		}

		return new Vector3(vres[0], vres[1], vres[2]);
	}

	/**
	 * Returns the given matrix in a single dimension float array
	 *
	 * @return
	 */
	public static float[] toArray(Matrix m) {
		return m.data.clone();
	}

	/**
	 * Creates a lookat matrix with the given eye point.
	 *
	 * @param eye The location of the camera
	 * @param at The location that the camera is looking at
	 * @param up The direction that corrisponds to Up
	 * @return A rotational transform that corrisponds to a camera looking at
	 *         the given values
	 */
	public static Matrix createLookAt(Vector3 eye, Vector3 at, Vector3 up) {
		Vector3 f = at.subtract(eye).normalize();
		up = up.normalize();

		Vector3 s = f.cross(up).normalize();
		Vector3 u = s.cross(f).normalize();

		Matrix mat = new Matrix(4);

		mat.set(0, 0, s.getX());
		mat.set(1, 0, s.getY());
		mat.set(2, 0, s.getZ());

		mat.set(0, 1, u.getX());
		mat.set(1, 1, u.getY());
		mat.set(2, 1, u.getZ());

		mat.set(0, 2, -f.getX());
		mat.set(1, 2, -f.getY());
		mat.set(2, 2, -f.getZ());

		Matrix trans = Matrix.translate(eye.multiply(-1));
		mat = Matrix.multiply(trans, mat);
		return mat;
	}

	/**
	 * Creates a perspective projection matrix with the given (x) FOV, aspect,
	 * near and far planes
	 *
	 * @param fov The Field of View in the x direction
	 * @param aspect The aspect ratio, usually width/height
	 * @param znear The near plane. Cannot be 0
	 * @param zfar the far plane. zfar cannot equal znear
	 * @return A perspective projection matrix built from the given values
	 */
	public static Matrix createPerspective(float fov, float aspect, float znear, float zfar) {
		float ymax, xmax;
		ymax = znear * (float) Math.tan(fov * Math.PI / 360.0);
		xmax = ymax * aspect;
		return createOrthographic(xmax, -xmax, ymax, -ymax, znear, zfar);
	}

	/**
	 * Creates an orthographic viewing fustrum built from the provided values
	 *
	 * @param right the right most plane of the viewing fustrum
	 * @param left the left most plane of the viewing fustrum
	 * @param top the top plane of the viewing fustrum
	 * @param bottom the bottom plane of the viewing fustrum
	 * @param near the near plane of the viewing fustrum
	 * @param far the far plane of the viewing fustrum
	 * @return A viewing fustrum build from the provided values
	 */
	public static Matrix createOrthographic(float right, float left, float top, float bottom, float near, float far) {
		Matrix ortho = new Matrix();
		float temp, temp2, temp3, temp4;
		temp = 2.0f * near;
		temp2 = right - left;
		temp3 = top - bottom;
		temp4 = far - near;

		ortho.set(0, 0, temp / temp2);
		ortho.set(1, 1, temp / temp3);

		ortho.set(0, 2, (right + left) / temp2);
		ortho.set(1, 2, (top + bottom) / temp3);
		ortho.set(2, 2, (-far - near) / temp4);
		ortho.set(2, 3, -1);

		ortho.set(3, 2, -temp * far / temp4);
		ortho.set(3, 3, 0);

		return ortho;
	}
}
