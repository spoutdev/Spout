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

import java.io.Serializable;
import java.util.Arrays;

public class Matrix implements Serializable, Cloneable {
	private static final long serialVersionUID = 1;
	public static final Matrix IDENTITY_2 = new ImmutableIdentityMatrix(2);
	public static final Matrix IDENTITY_3 = new ImmutableIdentityMatrix(3);
	public static final Matrix IDENTITY_4 = new ImmutableIdentityMatrix(4);
	private final float[][] mat;

	public Matrix(int size) {
		if (size < 2) {
			throw new IllegalArgumentException("Minimum matrix size is 2");
		}
		mat = new float[size][size];
		setIdentity();
	}

	public Matrix(Matrix m) {
		mat = deepClone(m.mat);
	}

	public int size() {
		return mat.length;
	}

	public float get(int row, int col) {
		return mat[row][col];
	}

	public void set(int row, int col, double val) {
		set(row, col, (float) val);
	}

	public void set(int row, int col, float val) {
		mat[row][col] = val;
	}

	public final void setIdentity() {
		final int size = size();
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (row == col) {
					mat[row][col] = 1;
				} else {
					mat[row][col] = 0;
				}
			}
		}
	}

	public Matrix resize(int size) {
		final Matrix d = new Matrix(size);
		size = Math.min(size, size());
		for (int row = 0; row < size; row++) {
			System.arraycopy(mat[row], 0, d.mat[row], 0, size);
		}
		return d;
	}

	public Matrix add(Matrix m) {
		final int size = size();
		if (size != m.size()) {
			throw new IllegalArgumentException("Matrix sizes must be the same");
		}
		final Matrix d = new Matrix(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = mat[row][col] + m.mat[row][col];
			}
		}
		return d;
	}

	public Matrix sub(Matrix m) {
		final int size = size();
		if (size != m.size()) {
			throw new IllegalArgumentException("Matrix sizes must be the same");
		}
		final Matrix d = new Matrix(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = mat[row][col] - m.mat[row][col];
			}
		}
		return d;
	}

	public Matrix mul(float a) {
		final int size = size();
		final Matrix d = new Matrix(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = mat[row][col] * a;
			}
		}
		return d;
	}

	public Matrix mul(Matrix m) {
		final int size = size();
		if (size != m.size()) {
			throw new IllegalArgumentException("Matrix sizes must be the same");
		}
		final Matrix d = new Matrix(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				float dot = 0;
				for (int i = 0; i < size; i++) {
					dot += mat[row][i] * m.mat[i][col];
				}
				d.mat[row][col] = dot;
			}
		}
		return d;
	}

	public Matrix div(float a) {
		final int size = size();
		final Matrix d = new Matrix(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = mat[row][col] / a;
			}
		}
		return d;
	}

	public Matrix translate(Vector2 v) {
		return translate(v.getX(), v.getY());
	}

	public Matrix translate(Vector3 v) {
		return translate(v.getX(), v.getY(), v.getZ());
	}

	public Matrix translate(Vector4 v) {
		return translate(v.getX(), v.getY(), v.getZ(), v.getW());
	}

	public Matrix translate(Vector v) {
		return translate(v.toArray());
	}

	public Matrix translate(float... v) {
		return createTranslation(size(), v).mul(this);
	}

	public Matrix scale(Vector2 v) {
		return scale(v.getX(), v.getY());
	}

	public Matrix scale(Vector3 v) {
		return scale(v.getX(), v.getY(), v.getZ());
	}

	public Matrix scale(Vector4 v) {
		return scale(v.getX(), v.getY(), v.getZ(), v.getW());
	}

	public Matrix scale(Vector v) {
		return scale(v.toArray());
	}

	public Matrix scale(float... v) {
		return createScaling(size(), v).mul(this);
	}

	public Matrix rotate(Complex rot) {
		return createRotation(size(), rot).mul(this);
	}

	public Matrix rotate(Quaternion rot) {
		return createRotation(size(), rot).mul(this);
	}

	public Vector2 transform(Vector2 v) {
		return transform(v.getX(), v.getY()).toVector2();
	}

	public Vector3 transform(Vector3 v) {
		return transform(v.getX(), v.getY(), v.getZ()).toVector3();
	}

	public Vector4 transform(Vector4 v) {
		return transform(v.getX(), v.getY(), v.getZ(), v.getW()).toVector4();
	}

	public Vector transform(Vector v) {
		return transform(v.toArray());
	}

	public Vector transform(float... vec) {
		final int originalSize = vec.length;
		final int size = size();
		vec = adjustSize(vec, size, 1);
		final Vector d = new Vector(size);
		for (int row = 0; row < size; row++) {
			float dot = 0;
			for (int col = 0; col < size; col++) {
				dot += mat[row][col] * vec[col];
			}
			d.set(row, dot);
		}
		return d.resize(originalSize);
	}

	public Matrix floor() {
		final int size = size();
		final Matrix d = new Matrix(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = GenericMath.floor(mat[row][col]);
			}
		}
		return d;
	}

	public Matrix ceil() {
		final int size = size();
		final Matrix d = new Matrix(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = (float) Math.ceil(mat[row][col]);
			}
		}
		return d;
	}

	public Matrix round() {
		final int size = size();
		final Matrix d = new Matrix(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = Math.round(mat[row][col]);
			}
		}
		return d;
	}

	public Matrix abs() {
		final int size = size();
		final Matrix d = new Matrix(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = Math.abs(mat[row][col]);
			}
		}
		return d;
	}

	public Matrix negate() {
		final int size = size();
		final Matrix d = new Matrix(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = -mat[row][col];
			}
		}
		return d;
	}

	public Matrix transpose() {
		final int size = size();
		final Matrix d = new Matrix(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = mat[col][row];
			}
		}
		return d;
	}

	public float trace() {
		final int size = size();
		float trace = 0;
		for (int rowCol = 0; rowCol < size; rowCol++) {
			trace += mat[rowCol][rowCol];
		}
		return trace;
	}

	public float determinant() {
		final int size = size();
		final float[][] m = deepClone(mat);
		float det;
		for (int i = 0; i < size - 1; i++) {
			for (int col = i + 1; col < size; col++) {
				det = m[i][col] / m[i][i];
				for (int row = i; row < size; row++) {
					m[row][col] -= det * m[row][i];
				}
			}
		}
		det = 1;
		for (int i = 0; i < size; i++) {
			det *= m[i][i];
		}
		return det;
	}

	public Matrix invert() {
		if (determinant() == 0) {
			return null;
		}
		final int size = size();
		final AugmentedMatrix augMat = new AugmentedMatrix(this);
		final int augmentedSize = augMat.getAugmentedSize();
		for (int i = 0; i < size; i++) {
			for (int row = 0; row < size; row++) {
				if (i != row) {
					final float ratio = augMat.get(row, i) / augMat.get(i, i);
					for (int col = 0; col < augmentedSize; col++) {
						augMat.set(row, col, augMat.get(row, col) - ratio * augMat.get(i, col));
					}
				}
			}
		}
		for (int row = 0; row < size; row++) {
			final float div = augMat.get(row, row);
			for (int col = 0; col < augmentedSize; col++) {
				augMat.set(row, col, augMat.get(row, col) / div);
			}
		}
		return augMat.getAugmentation();
	}

	public float[] toArray() {
		final int size = size();
		final float[] array = new float[size * size];
		for (int row = 0; row < size; row++) {
			System.arraycopy(mat[row], 0, array, row * size, size);
		}
		return array;
	}

	@Override
	public String toString() {
		final int size = size();
		final StringBuilder builder = new StringBuilder();
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				builder.append(mat[row][col]);
				if (col < size - 1) {
					builder.append(' ');
				}
			}
			if (row < size - 1) {
				builder.append('\n');
			}
		}
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Matrix)) {
			return false;
		}
		return Arrays.deepEquals(mat, ((Matrix) obj).mat);
	}

	@Override
	public int hashCode() {
		return 79 * 5 + Arrays.deepHashCode(mat);
	}

	@Override
	public Matrix clone() {
		return new Matrix(this);
	}

	private static float[] adjustSize(float[] array, int toSize, float filler) {
		if (array.length == toSize) {
			return array;
		}
		final float[] d = new float[toSize];
		final int size = Math.min(array.length, toSize);
		System.arraycopy(array, 0, d, 0, size);
		Arrays.fill(d, size, toSize, filler);
		return d;
	}

	private static float[][] deepClone(float[][] array) {
		final int size = array.length;
		float[][] clone = array.clone();
		for (int i = 0; i < size; i++) {
			clone[i] = array[i].clone();
		}
		return clone;
	}

	public static Matrix createScaling(int size, double scale) {
		return createScaling(size, (float) scale);
	}

	public static Matrix createScaling(int size, float scale) {
		return createScaling(size, adjustSize(new float[0], size, scale));
	}

	public static Matrix createScaling(int size, Vector2 v) {
		return createScaling(size, v.getX(), v.getY());
	}

	public static Matrix createScaling(int size, Vector3 v) {
		return createScaling(size, v.getX(), v.getY(), v.getZ());
	}

	public static Matrix createScaling(int size, Vector4 v) {
		return createScaling(size, v.getX(), v.getY(), v.getW());
	}

	public static Matrix createScaling(int size, Vector v) {
		return createScaling(size, v.toArray());
	}

	public static Matrix createScaling(int size, float... vec) {
		final Matrix m = new Matrix(size);
		vec = adjustSize(vec, size, 1);
		for (int rowCol = 0; rowCol < size; rowCol++) {
			m.set(rowCol, rowCol, vec[rowCol]);
		}
		return m;
	}

	public static Matrix createTranslation(int size, Vector2 v) {
		return createTranslation(size, v.getX(), v.getY());
	}

	public static Matrix createTranslation(int size, Vector3 v) {
		return createTranslation(size, v.getX(), v.getY(), v.getZ());
	}

	public static Matrix createTranslation(int size, Vector4 v) {
		return createTranslation(size, v.getX(), v.getY(), v.getW());
	}

	public static Matrix createTranslation(int size, Vector v) {
		return createTranslation(size, v.toArray());
	}

	public static Matrix createTranslation(int size, float... vec) {
		final Matrix m = new Matrix(size);
		vec = adjustSize(vec, size - 1, 0);
		for (int row = 0; row < size - 1; row++) {
			m.set(row, size - 1, vec[row]);
		}
		return m;
	}

	public static Matrix createRotation(int size, Complex rot) {
		if (size < 2) {
			throw new IllegalArgumentException("Minimum matrix size is 2");
		}
		final Matrix m = new Matrix(size);
		rot = rot.normalize();
		m.set(0, 0, rot.getX());
		m.set(0, 1, -rot.getY());
		m.set(1, 0, rot.getY());
		m.set(1, 1, rot.getX());
		return m;
	}

	public static Matrix createRotation(int size, Quaternion rot) {
		if (size < 3) {
			throw new IllegalArgumentException("Minimum matrix size is 3");
		}
		final Matrix m = new Matrix(size);
		rot = rot.normalize();
		m.set(0, 0, 1 - 2 * rot.getY() * rot.getY() - 2 * rot.getZ() * rot.getZ());
		m.set(0, 1, 2 * rot.getX() * rot.getY() - 2 * rot.getW() * rot.getZ());
		m.set(0, 2, 2 * rot.getX() * rot.getZ() + 2 * rot.getW() * rot.getY());
		m.set(1, 0, 2 * rot.getX() * rot.getY() + 2 * rot.getW() * rot.getZ());
		m.set(1, 1, 1 - 2 * rot.getX() * rot.getX() - 2 * rot.getZ() * rot.getZ());
		m.set(1, 2, 2 * rot.getY() * rot.getZ() - 2 * rot.getW() * rot.getX());
		m.set(2, 0, 2 * rot.getX() * rot.getZ() - 2 * rot.getW() * rot.getY());
		m.set(2, 1, 2 * rot.getY() * rot.getZ() + 2.f * rot.getX() * rot.getW());
		m.set(2, 2, 1 - 2 * rot.getX() * rot.getX() - 2 * rot.getY() * rot.getY());
		return m;
	}

	/**
	 * Creates a "look at" matrix for the given eye point.
	 *
	 * @param size The size of the matrix, minimum of 4
	 * @param eye The position of the camera
	 * @param at The point that the camera is looking at
	 * @param up The "up" vector
	 * @return A rotational transform that corresponds to a camera looking at the given point
	 */
	public static Matrix createLookAt(int size, Vector3 eye, Vector3 at, Vector3 up) {
		if (size < 4) {
			throw new IllegalArgumentException("Minimum matrix size is 4");
		}
		final Vector3 f = at.sub(eye).normalize();
		up = up.normalize();
		final Vector3 s = f.cross(up).normalize();
		final Vector3 u = s.cross(f).normalize();
		final Matrix mat = new Matrix(size);
		mat.set(0, 0, s.getX());
		mat.set(1, 0, s.getY());
		mat.set(2, 0, s.getZ());
		mat.set(0, 1, u.getX());
		mat.set(1, 1, u.getY());
		mat.set(2, 1, u.getZ());
		mat.set(0, 2, -f.getX());
		mat.set(1, 2, -f.getY());
		mat.set(2, 2, -f.getZ());
		final Matrix trans = createTranslation(size, eye.mul(-1));
		return trans.mul(mat);
	}

	/**
	 * Creates a perspective projection matrix with the given (x) FOV, aspect, near and far planes
	 *
	 * @param size The size of the matrix, minimum of 4
	 * @param fov The field of view in the x direction
	 * @param aspect The aspect ratio, usually width/height
	 * @param zNear The near plane, cannot be 0
	 * @param zFar the far plane, zFar cannot equal zNear
	 * @return A perspective projection matrix built from the given values
	 */
	public static Matrix createPerspective(int size, float fov, float aspect, float zNear, float zFar) {
		final float yMax = zNear * TrigMath.tan(fov * (float) TrigMath.HALF_DEG_TO_RAD);
		final float xMax = yMax * aspect;
		return createOrthographic(size, xMax, -xMax, yMax, -yMax, zNear, zFar);
	}

	/**
	 * Creates an orthographic viewing frustum built from the provided values
	 *
	 * @param size The size of the matrix, minimum of 4
	 * @param right the right most plane of the viewing frustum
	 * @param left the left most plane of the viewing frustum
	 * @param top the top plane of the viewing frustum
	 * @param bottom the bottom plane of the viewing frustum
	 * @param near the near plane of the viewing frustum
	 * @param far the far plane of the viewing frustum
	 * @return A viewing frustum build from the provided values
	 */
	public static Matrix createOrthographic(int size, float right, float left, float top, float bottom,
											float near, float far) {
		if (size < 4) {
			throw new IllegalArgumentException("Minimum matrix size is 4");
		}
		final Matrix orthographic = new Matrix(size);
		final float temp1 = 2.0f * near;
		final float temp2 = right - left;
		final float temp3 = top - bottom;
		final float temp4 = far - near;
		orthographic.set(0, 0, temp1 / temp2);
		orthographic.set(1, 1, temp1 / temp3);
		orthographic.set(0, 2, (right + left) / temp2);
		orthographic.set(1, 2, (top + bottom) / temp3);
		orthographic.set(2, 2, (-far - near) / temp4);
		orthographic.set(2, 3, -1);
		orthographic.set(3, 2, -temp1 * far / temp4);
		orthographic.set(3, 3, 0);
		return orthographic;
	}

	private static class ImmutableIdentityMatrix extends Matrix {
		public ImmutableIdentityMatrix(int size) {
			super(size);
		}

		@Override
		public void set(int col, int row, float val) {
			throw new UnsupportedOperationException("You may not alter this matrix");
		}
	}

	private static class AugmentedMatrix {
		private final Matrix mat;
		private final Matrix aug;
		private final int size;

		public AugmentedMatrix(Matrix mat) {
			this.mat = mat.clone();
			this.size = mat.size();
			aug = new Matrix(size);
		}

		public Matrix getMatrix() {
			return mat;
		}

		public Matrix getAugmentation() {
			return aug;
		}

		public int getSize() {
			return size;
		}

		public int getAugmentedSize() {
			return getSize() * 2;
		}

		public float get(int row, int col) {
			if (col < size) {
				return mat.get(row, col);
			} else {
				return aug.get(row, col - size);
			}
		}

		public void set(int row, int col, float val) {
			if (col < size) {
				mat.set(row, col, val);
			} else {
				aug.set(row, col - size, val);
			}
		}
	}
}
