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
package org.spout.math.matrix;

import java.io.Serializable;
import java.util.Arrays;

import org.spout.math.GenericMath;
import org.spout.math.TrigMath;
import org.spout.math.imaginary.Complex;
import org.spout.math.imaginary.Quaternion;
import org.spout.math.vector.Vector3;
import org.spout.math.vector.VectorN;

public class MatrixN implements Matrix, Serializable, Cloneable {
	private static final long serialVersionUID = 1;
	public static final MatrixN IDENTITY_2 = new ImmutableIdentityMatrixN(2);
	public static final MatrixN IDENTITY_3 = new ImmutableIdentityMatrixN(3);
	public static final MatrixN IDENTITY_4 = new ImmutableIdentityMatrixN(4);
	private final float[][] mat;

	public MatrixN(int size) {
		if (size < 2) {
			throw new IllegalArgumentException("Minimum matrix size is 2");
		}
		mat = new float[size][size];
		setIdentity();
	}

	public MatrixN(Matrix2 m) {
		mat = new float[][] {
				{m.get(0, 0), m.get(0, 1)},
				{m.get(1, 0), m.get(1, 1)}
		};
	}

	public MatrixN(Matrix3 m) {
		mat = new float[][] {
				{m.get(0, 0), m.get(0, 1), m.get(0, 2)},
				{m.get(1, 0), m.get(1, 1), m.get(1, 2)},
				{m.get(2, 0), m.get(2, 1), m.get(2, 2)}
		};
	}

	public MatrixN(Matrix4 m) {
		mat = new float[][] {
				{m.get(0, 0), m.get(0, 1), m.get(0, 2), m.get(0, 3)},
				{m.get(1, 0), m.get(1, 1), m.get(1, 2), m.get(1, 3)},
				{m.get(2, 0), m.get(2, 1), m.get(2, 2), m.get(2, 3)},
				{m.get(3, 0), m.get(3, 1), m.get(3, 2), m.get(3, 3)}
		};
	}

	public MatrixN(float... m) {
		if (m.length < 4) {
			throw new IllegalArgumentException("Minimum matrix size is 2");
		}
		final int size = (int) Math.ceil(Math.sqrt(m.length));
		mat = new float[size][size];
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				final int index = col + row * size;
				if (index < m.length) {
					mat[row][col] = m[index];
				} else {
					mat[row][col] = 0;
				}
			}
		}
	}

	public MatrixN(MatrixN m) {
		mat = deepClone(m.mat);
	}

	public int size() {
		return mat.length;
	}

	@Override
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

	public void setZero() {
		final int size = size();
		for (int row = 0; row < size; row++) {
			Arrays.fill(mat[row], 0);
		}
	}

	public MatrixN resize(int size) {
		final MatrixN d = new MatrixN(size);
		for (int rowCol = size(); rowCol < size; rowCol++) {
			d.set(rowCol, rowCol, 0);
		}
		size = Math.min(size, size());
		for (int row = 0; row < size; row++) {
			System.arraycopy(mat[row], 0, d.mat[row], 0, size);
		}
		return d;
	}

	public MatrixN add(MatrixN m) {
		final int size = size();
		if (size != m.size()) {
			throw new IllegalArgumentException("Matrix sizes must be the same");
		}
		final MatrixN d = new MatrixN(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = mat[row][col] + m.mat[row][col];
			}
		}
		return d;
	}

	public MatrixN sub(MatrixN m) {
		final int size = size();
		if (size != m.size()) {
			throw new IllegalArgumentException("Matrix sizes must be the same");
		}
		final MatrixN d = new MatrixN(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = mat[row][col] - m.mat[row][col];
			}
		}
		return d;
	}

	public MatrixN mul(double a) {
		return mul((float) a);
	}

	@Override
	public MatrixN mul(float a) {
		final int size = size();
		final MatrixN d = new MatrixN(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = mat[row][col] * a;
			}
		}
		return d;
	}

	public MatrixN mul(MatrixN m) {
		final int size = size();
		if (size != m.size()) {
			throw new IllegalArgumentException("Matrix sizes must be the same");
		}
		final MatrixN d = new MatrixN(size);
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

	public MatrixN div(double a) {
		return div((float) a);
	}

	@Override
	public MatrixN div(float a) {
		final int size = size();
		final MatrixN d = new MatrixN(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = mat[row][col] / a;
			}
		}
		return d;
	}

	public MatrixN div(MatrixN m) {
		return mul(m.invert());
	}

	public MatrixN pow(double pow) {
		return pow((float) pow);
	}

	@Override
	public MatrixN pow(float pow) {
		final int size = size();
		final MatrixN d = new MatrixN(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = (float) Math.pow(mat[row][col], pow);
			}
		}
		return d;
	}

	public MatrixN translate(VectorN v) {
		return translate(v.toArray());
	}

	public MatrixN translate(float... v) {
		return createTranslation(v).mul(this);
	}

	public MatrixN scale(VectorN v) {
		return scale(v.toArray());
	}

	public MatrixN scale(float... v) {
		return createScaling(v).mul(this);
	}

	public MatrixN rotate(Complex rot) {
		return createRotation(size(), rot).mul(this);
	}

	public MatrixN rotate(Quaternion rot) {
		return createRotation(size(), rot).mul(this);
	}

	public VectorN transform(VectorN v) {
		return transform(v.toArray());
	}

	public VectorN transform(float... vec) {
		final int size = size();
		if (size != vec.length) {
			throw new IllegalArgumentException("Matrix and vector sizes must be the same");
		}
		final VectorN d = new VectorN(size);
		for (int row = 0; row < size; row++) {
			float dot = 0;
			for (int col = 0; col < size; col++) {
				dot += mat[row][col] * vec[col];
			}
			d.set(row, dot);
		}
		return d;
	}

	@Override
	public MatrixN floor() {
		final int size = size();
		final MatrixN d = new MatrixN(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = GenericMath.floor(mat[row][col]);
			}
		}
		return d;
	}

	@Override
	public MatrixN ceil() {
		final int size = size();
		final MatrixN d = new MatrixN(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = (float) Math.ceil(mat[row][col]);
			}
		}
		return d;
	}

	@Override
	public MatrixN round() {
		final int size = size();
		final MatrixN d = new MatrixN(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = Math.round(mat[row][col]);
			}
		}
		return d;
	}

	@Override
	public MatrixN abs() {
		final int size = size();
		final MatrixN d = new MatrixN(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = Math.abs(mat[row][col]);
			}
		}
		return d;
	}

	@Override
	public MatrixN negate() {
		final int size = size();
		final MatrixN d = new MatrixN(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = -mat[row][col];
			}
		}
		return d;
	}

	@Override
	public MatrixN transpose() {
		final int size = size();
		final MatrixN d = new MatrixN(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				d.mat[row][col] = mat[col][row];
			}
		}
		return d;
	}

	@Override
	public float trace() {
		final int size = size();
		float trace = 0;
		for (int rowCol = 0; rowCol < size; rowCol++) {
			trace += mat[rowCol][rowCol];
		}
		return trace;
	}

	@Override
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

	@Override
	public MatrixN invert() {
		if (determinant() == 0) {
			return null;
		}
		final int size = size();
		final AugmentedMatrixN augMat = new AugmentedMatrixN(this);
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

	public Matrix2 toMatrix2() {
		return new Matrix2(this);
	}

	public Matrix3 toMatrix3() {
		return new Matrix3(this);
	}

	public Matrix4 toMatrix4() {
		return new Matrix4(this);
	}

	public float[] toArray() {
		return toArray(false);
	}

	@Override
	public float[] toArray(boolean columnMajor) {
		final int size = size();
		final float[] array = new float[size * size];
		if (columnMajor) {
			for (int col = 0; col < size; col++) {
				for (int row = 0; row < size; row++) {
					array[row + col * size] = mat[row][col];
				}
			}
		} else {
			for (int row = 0; row < size; row++) {
				System.arraycopy(mat[row], 0, array, row * size, size);
			}
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
		if (!(obj instanceof MatrixN)) {
			return false;
		}
		return Arrays.deepEquals(mat, ((MatrixN) obj).mat);
	}

	@Override
	public int hashCode() {
		return 79 * 5 + Arrays.deepHashCode(mat);
	}

	@Override
	public MatrixN clone() {
		return new MatrixN(this);
	}

	public static MatrixN createScaling(VectorN v) {
		return createScaling(v.toArray());
	}

	public static MatrixN createScaling(float... vec) {
		final int size = vec.length;
		final MatrixN m = new MatrixN(size);
		for (int rowCol = 0; rowCol < size; rowCol++) {
			m.set(rowCol, rowCol, vec[rowCol]);
		}
		return m;
	}

	public static MatrixN createTranslation(VectorN v) {
		return createTranslation(v.toArray());
	}

	public static MatrixN createTranslation(float... vec) {
		final int size = vec.length;
		final MatrixN m = new MatrixN(size + 1);
		for (int row = 0; row < size; row++) {
			m.set(row, size, vec[row]);
		}
		return m;
	}

	public static MatrixN createRotation(int size, Complex rot) {
		if (size < 2) {
			throw new IllegalArgumentException("Minimum matrix size is 2");
		}
		final MatrixN m = new MatrixN(size);
		rot = rot.normalize();
		m.set(0, 0, rot.getX());
		m.set(0, 1, -rot.getY());
		m.set(1, 0, rot.getY());
		m.set(1, 1, rot.getX());
		return m;
	}

	public static MatrixN createRotation(int size, Quaternion rot) {
		if (size < 3) {
			throw new IllegalArgumentException("Minimum matrix size is 3");
		}
		final MatrixN m = new MatrixN(size);
		rot = rot.normalize();
		m.set(0, 0, 1 - 2 * rot.getY() * rot.getY() - 2 * rot.getZ() * rot.getZ());
		m.set(0, 1, 2 * rot.getX() * rot.getY() - 2 * rot.getW() * rot.getZ());
		m.set(0, 2, 2 * rot.getX() * rot.getZ() + 2 * rot.getW() * rot.getY());
		m.set(1, 0, 2 * rot.getX() * rot.getY() + 2 * rot.getW() * rot.getZ());
		m.set(1, 1, 1 - 2 * rot.getX() * rot.getX() - 2 * rot.getZ() * rot.getZ());
		m.set(1, 2, 2 * rot.getY() * rot.getZ() - 2 * rot.getW() * rot.getX());
		m.set(2, 0, 2 * rot.getX() * rot.getZ() - 2 * rot.getW() * rot.getY());
		m.set(2, 1, 2 * rot.getY() * rot.getZ() + 2 * rot.getX() * rot.getW());
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
	public static MatrixN createLookAt(int size, Vector3 eye, Vector3 at, Vector3 up) {
		if (size < 4) {
			throw new IllegalArgumentException("Minimum matrix size is 4");
		}
		final Vector3 f = at.sub(eye).normalize();
		up = up.normalize();
		final Vector3 s = f.cross(up).normalize();
		final Vector3 u = s.cross(f).normalize();
		final MatrixN mat = new MatrixN(size);
		mat.set(0, 0, s.getX());
		mat.set(0, 1, s.getY());
		mat.set(0, 2, s.getZ());
		mat.set(1, 0, u.getX());
		mat.set(1, 1, u.getY());
		mat.set(1, 2, u.getZ());
		mat.set(2, 0, -f.getX());
		mat.set(2, 1, -f.getY());
		mat.set(2, 2, -f.getZ());
		return mat.translate(eye.mul(-1).toVectorN());
	}

	// TODO: add double overload

	/**
	 * Creates a perspective projection matrix with the given (x) FOV, aspect, near and far planes
	 *
	 * @param size The size of the matrix, minimum of 4
	 * @param fov The field of view in the x direction
	 * @param aspect The aspect ratio, usually width/height
	 * @param near The near plane, cannot be 0
	 * @param far the far plane, zFar cannot equal zNear
	 * @return A perspective projection matrix built from the given values
	 */
	public static MatrixN createPerspective(int size, float fov, float aspect, float near, float far) {
		if (size < 4) {
			throw new IllegalArgumentException("Minimum matrix size is 4");
		}
		final MatrixN perspective = new MatrixN(size);
		final float scale = 1 / TrigMath.tan(fov * (float) TrigMath.HALF_DEG_TO_RAD);
		perspective.set(0, 0, scale / aspect);
		perspective.set(1, 1, scale);
		perspective.set(2, 2, (far + near) / (near - far));
		perspective.set(2, 3, 2 * far * near / (near - far));
		perspective.set(3, 2, -1);
		return perspective;
	}

	// TODO: add double overload

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
	 * @return A viewing frustum built from the provided values
	 */
	public static MatrixN createOrthographic(int size, float right, float left, float top, float bottom,
											 float near, float far) {
		if (size < 4) {
			throw new IllegalArgumentException("Minimum matrix size is 4");
		}
		final MatrixN orthographic = new MatrixN(size);
		orthographic.set(0, 0, 2 / (right - left));
		orthographic.set(1, 1, 2 / (top - bottom));
		orthographic.set(2, 2, -2 / (far - near));
		orthographic.set(0, 3, -(right + left) / (right - left));
		orthographic.set(1, 3, -(top + bottom) / (top - bottom));
		orthographic.set(2, 3, -(far + near) / (far - near));
		return orthographic;
	}

	private static float[][] deepClone(float[][] array) {
		final int size = array.length;
		final float[][] clone = array.clone();
		for (int i = 0; i < size; i++) {
			clone[i] = array[i].clone();
		}
		return clone;
	}

	private static class ImmutableIdentityMatrixN extends MatrixN {
		public ImmutableIdentityMatrixN(int size) {
			super(size);
		}

		@Override
		public void set(int row, int col, float val) {
			throw new UnsupportedOperationException("You may not alter this matrix");
		}

		@Override
		public void setZero() {
			throw new UnsupportedOperationException("You may not alter this matrix");
		}
	}

	private static class AugmentedMatrixN {
		private final MatrixN mat;
		private final MatrixN aug;
		private final int size;

		private AugmentedMatrixN(MatrixN mat) {
			this.mat = mat.clone();
			this.size = mat.size();
			aug = new MatrixN(size);
		}

		private MatrixN getAugmentation() {
			return aug;
		}

		private int getAugmentedSize() {
			return size * 2;
		}

		private float get(int row, int col) {
			if (col < size) {
				return mat.get(row, col);
			} else {
				return aug.get(row, col - size);
			}
		}

		private void set(int row, int col, float val) {
			if (col < size) {
				mat.set(row, col, val);
			} else {
				aug.set(row, col - size, val);
			}
		}
	}
}
