/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.math;

import javax.vecmath.Matrix4f;

/**
 * Class containing matrix mathematical functions.
 */
public class MatrixMath {
	private MatrixMath() {
	}

	/**
	 * Rounds all fields of the vector to the nearest integer value.
	 * @param o Matrix to use
	 * @return The rounded matrix
	 */
	public static Matrix round(Matrix o) {
		Matrix ret = new Matrix(o);
		final int dimension = o.getDimension();
		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				ret.set(x, y, Math.round(o.get(x, y)));
			}
		}
		return ret;
	}

	/**
	 * Adds two matrices together
	 * @param a The left matrix
	 * @param b The right matrix
	 * @return The sum matrix of left plus right
	 */
	public static Matrix add(Matrix a, Matrix b) {
		if (a.getDimension() != b.getDimension()) {
			throw new IllegalArgumentException("Matrix Dimensions must be equal");
		}
		final int dimension = a.getDimension();
		Matrix res = new Matrix(dimension);
		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				res.set(x, y, a.get(x, y) + b.get(x, y));
			}
		}
		return res;
	}

	/**
	 * Multiplies two matrices together
	 * @param a The left matrix
	 * @param b The right matrix
	 * @return The product matrix of left times right
	 */
	public static Matrix multiply(Matrix a, Matrix b) {
		if (a.getDimension() != b.getDimension()) {
			throw new IllegalArgumentException("Matrix Dimensions must be equal");
		}
		final int dimension = a.getDimension();
		Matrix res = new Matrix(dimension);
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				res.set(i, j, 0);
				for (int k = 0; k < dimension; k++) {
					float r = a.get(i, k) * b.get(k, j);
					res.set(i, j, res.get(i, j) + r);
				}
			}
		}
		return res;
	}

	/**
	 * Creates and returns a 4x4 identity matrix
	 * @return a 4x4 identity matrix
	 */
	public static Matrix createIdentity() {
		return new Matrix(4);
	}

	/**
	 * Creates and returns a 4x4 uniform scalar matrix
	 * @param scale The scale to apply to the identity matrix
	 * @return The scaled matrix
	 */
	public static Matrix createScaled(float scale) {
		Matrix res = createIdentity();
		res.set(0, 0, scale);
		res.set(1, 1, scale);
		res.set(2, 2, scale);
		return res;
	}

	/**
	 * Creates and returns a 4x4 uniform scalar matrix
	 * @param scale The scale to apply to the identity matrix
	 * @return The scaled matrix
	 */
	public static Matrix createScaled(Vector3 scale) {
		Matrix res = createIdentity();
		res.set(0, 0, scale.getX());
		res.set(1, 1, scale.getY());
		res.set(2, 2, scale.getZ());
		return res;
	}

	/**
	 * Creates and returns a 4x4 matrix that represents the translation provided
	 * by the given Vector3
	 * @param vector The translation vector
	 * @return The matrix form of the translation vector
	 */
	public static Matrix createTranslated(Vector3 vector) {
		Matrix res = createIdentity();
		res.set(3, 0, vector.getX());
		res.set(3, 1, vector.getY());
		res.set(3, 2, vector.getZ());
		return res;
	}

	/**
	 * Creates and returns a 4x4 rotation matrix around the X axis
	 * @param rot The rotation around x in degrees
	 * @return The matrix corresponding to the rotation
	 */
	public static Matrix createRotatedX(float rot) {
		Matrix res = createIdentity();

		double rotRad = Math.toRadians(rot);

		float rotCos = (float) Math.cos(rotRad);
		float rotSin = (float) Math.sin(rotRad);

		res.set(1, 1, rotCos);
		res.set(1, 2, -rotSin);
		res.set(2, 1, rotSin);
		res.set(2, 2, rotCos);

		return res;
	}

	/**
	 * Creates and returns a 4x4 rotation matrix around the Y axis
	 * @param rot The rotation around y in degrees
	 * @return The matrix corresponding to the rotation
	 */
	public static Matrix createRotatedY(float rot) {
		Matrix res = createIdentity();

		double rotRad = Math.toRadians(rot);

		float rotCos = (float) Math.cos(rotRad);
		float rotSin = (float) Math.sin(rotRad);

		res.set(0, 0, rotCos);
		res.set(0, 2, rotSin);
		res.set(2, 0, -rotSin);
		res.set(2, 2, rotCos);

		return res;
	}

	/**
	 * Creates and returns a 4x4 rotation matrix around the Z axis
	 * @param rot The rotation around z in degrees
	 * @return The matrix corresponding to the rotation
	 */
	public static Matrix createRotatedZ(float rot) {
		Matrix res = createIdentity();

		double rotRad = Math.toRadians(rot);

		float rotCos = (float) Math.cos(rotRad);
		float rotSin = (float) Math.sin(rotRad);

		res.set(0, 0, rotCos);
		res.set(0, 1, -rotSin);
		res.set(1, 0, rotSin);
		res.set(1, 1, rotCos);

		return res;
	}

	/**
	 * Creates and returns a 4x4 rotation matrix given by the provided
	 * Quaternion
	 * @param rot The rotation
	 * @return The rotation matrix for the quaternion
	 */
	public static Matrix createRotated(Quaternion rot) {
		Matrix res = createIdentity();
		//Confirm that we are dealing with a unit quaternion
		Quaternion r = rot.normalize();

		float xx2 = 2f * r.getX() * r.getX();
		float yy2 = 2f * r.getY() * r.getY();
		float zz2 = 2f * r.getZ() * r.getZ();

		float xy2 = 2f * r.getX() * r.getY();
		float xz2 = 2f * r.getX() * r.getZ();
		float yz2 = 2f * r.getY() * r.getZ();

		float wx2 = 2f * r.getW() * r.getX();
		float wy2 = 2f * r.getW() * r.getY();
		float wz2 = 2f * r.getW() * r.getZ();

		res.set(0, 0, 1 - yy2 - zz2);
		res.set(0, 1, xy2 - wz2);
		res.set(0, 2, xz2 + wy2);
		res.set(0, 3, 0);

		res.set(1, 0, xy2 + wz2);
		res.set(1, 1, 1 - xx2 - zz2);
		res.set(1, 2, yz2 - wx2);
		res.set(1, 3, 0);

		res.set(2, 0, xz2 - wy2);
		res.set(2, 1, yz2 + wx2);
		res.set(2, 2, 1 - xx2 - yy2);
		res.set(2, 3, 0);
		//3, [0-3] will be 0,0,0,1 due to identity matrix

		return res;
	}

	/**
	 * Creates a lookat matrix with the given eye point.
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

		Matrix trans = createTranslated(eye.multiply(-1));
		mat = multiply(trans, mat);
		return mat;
	}

	/**
	 * Creates a perspective projection matrix with the given (x) FOV, aspect,
	 * near and far planes
	 * @param fov The Field of View in the x direction
	 * @param aspect The aspect ratio, usually width/height
	 * @param znear The near plane. Cannot be 0
	 * @param zfar the far plane. zfar cannot equal znear
	 * @return A perspective projection matrix built from the given values
	 */
	public static Matrix createPerspective(float fov, float aspect, float znear, float zfar) {
		float ymax, xmax;
		ymax = znear * (float) Math.tan(fov * TrigMath.HALF_DEGTORAD);
		xmax = ymax * aspect;
		return createOrthographic(xmax, -xmax, ymax, -ymax, znear, zfar);
	}

	/**
	 * Creates an orthographic viewing fustrum built from the provided values
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

	/**
	 * Transpose the matrix.
	 * @param in The matrix to transpose
	 * @return The transposed matrix
	 */
	public static Matrix transpose(Matrix in) {
		final int dimension = in.getDimension();
		Matrix r = new Matrix(dimension);
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				r.set(j, i, in.get(i, j));
			}
		}
		return r;
	}

	/**
	 * Transforms a vecmath matrix to a Spout matrix.
	 * @param vector The vecmath matrix
	 * @return The vector as a Spout matrix
	 */
	public static Matrix toMatrix(Matrix4f matrix) {
		Matrix out = new Matrix(4);
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				out.set(x, y, matrix.getElement(x, y));
			}
		}
		return out;
	}
}
