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

public class Matrix2 implements Serializable, Cloneable {
	private static final long serialVersionUID = 1;
	public static final Matrix2 IDENTITY = new ImmutableIdentityMatrix2();
	private float m00;
	private float m01;
	private float m10;
	private float m11;

	public Matrix2() {
		setIdentity();
	}

	public Matrix2(Matrix2 m) {
		this(
				m.m00, m.m01,
				m.m10, m.m11);
	}

	private Matrix2(
			float m00, float m01,
			float m10, float m11) {
		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
	}

	public float get(int row, int col) {
		switch (row) {
			case 0:
				switch (col) {
					case 0:
						return m00;
					case 1:
						return m01;
				}
			case 1:
				switch (col) {
					case 0:
						return m10;
					case 1:
						return m11;
				}
		}
		throw new IllegalArgumentException(
				(row < 0 || row > 1 ? "row must be greater than zero and smaller than 2. " : "") +
						(col < 0 || col > 1 ? "col must be greater than zero and smaller than 2." : ""));
	}

	public void set(int row, int col, double val) {
		set(row, col, (float) val);
	}

	public void set(int row, int col, float val) {
		switch (col) {
			case 0:
				switch (row) {
					case 0:
						m00 = val;
						return;
					case 1:
						m01 = val;
						return;
				}
			case 1:
				switch (row) {
					case 0:
						m10 = val;
						return;
					case 1:
						m11 = val;
						return;
				}
		}
		throw new IllegalArgumentException(
				(row < 0 || row > 1 ? "row must be greater than zero and smaller than 2. " : "") +
						(col < 0 || col > 1 ? "col must be greater than zero and smaller than 2." : ""));
	}

	public final void setIdentity() {
		m00 = 1;
		m01 = 0;
		m10 = 0;
		m11 = 1;
	}

	public void setZero() {
		m00 = 0;
		m01 = 0;
		m10 = 0;
		m11 = 0;
	}

	public Matrix2 add(Matrix2 m) {
		return new Matrix2(
				m00 + m.m00, m01 + m.m01,
				m10 + m.m10, m11 + m.m11);
	}

	public Matrix2 sub(Matrix2 m) {
		return new Matrix2(
				m00 - m.m00, m01 - m.m01,
				m10 - m.m10, m11 - m.m11);
	}

	public Matrix2 mul(float a) {
		return new Matrix2(
				m00 * a, m01 * a,
				m10 * a, m11 * a);
	}

	public Matrix2 mul(Matrix2 m) {
		return new Matrix2(
				m00 * m.m00 + m01 * m.m10, m00 * m.m01 + m01 * m.m11,
				m10 * m.m00 + m11 * m.m10, m10 * m.m01 + m11 * m.m11);
	}

	public Matrix2 div(float a) {
		return new Matrix2(
				m00 / a, m01 / a,
				m10 / a, m11 / a);
	}

	public Matrix2 translate(float x) {
		return createTranslation(x).mul(this);
	}

	public Matrix2 scale(double scale) {
		return scale((float) scale);
	}

	public Matrix2 scale(float scale) {
		return scale(scale, scale);
	}

	public Matrix2 scale(Vector2 v) {
		return scale(v.getX(), v.getY());
	}

	public Matrix2 scale(float x, float y) {
		return createScaling(x, y).mul(this);
	}

	public Matrix2 rotate(Complex rot) {
		return createRotation(rot).mul(this);
	}

	public Vector2 transform(Vector2 v) {
		return transform(v.getX(), v.getY());
	}

	public Vector2 transform(float x, float y) {
		return new Vector2(
				m00 * x + m01 * y,
				m10 * x + m11 * y);
	}

	public Matrix2 floor() {
		return new Matrix2(
				GenericMath.floor(m00), GenericMath.floor(m01),
				GenericMath.floor(m10), GenericMath.floor(m11));
	}

	public Matrix2 ceil() {
		return new Matrix2(
				(float) Math.ceil(m00), (float) Math.ceil(m01),
				(float) Math.ceil(m10), (float) Math.ceil(m11));
	}

	public Matrix2 round() {
		return new Matrix2(
				Math.round(m00), Math.round(m01),
				Math.round(m10), Math.round(m11));
	}

	public Matrix2 abs() {
		return new Matrix2(
				Math.abs(m00), Math.abs(m01),
				Math.abs(m10), Math.abs(m11));
	}

	public Matrix2 negate() {
		return new Matrix2(
				-m00, -m01,
				-m10, -m11);
	}

	public Matrix2 transpose() {
		return new Matrix2(
				m00, m10,
				m01, m11);
	}

	public float trace() {
		return m00 + m11;
	}

	public float determinant() {
		return m00 * m11 - m01 * m10;
	}

	public Matrix2 invert() {
		final float det = determinant();
		if (det == 0) {
			return null;
		}
		return new Matrix2(
				m11 / det, -m01 / det,
				-m10 / det, m00 / det);
	}

	public float[] toArray() {
		return new float[]{m00, m01, m10, m11};
	}

	@Override
	public String toString() {
		return m00 + " " + m01 + "\n" + m10 + " " + m11;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Matrix2)) {
			return false;
		}
		final Matrix2 matrix2 = (Matrix2) o;
		if (Float.compare(matrix2.m00, m00) != 0) {
			return false;
		}
		if (Float.compare(matrix2.m01, m01) != 0) {
			return false;
		}
		if (Float.compare(matrix2.m10, m10) != 0) {
			return false;
		}
		if (Float.compare(matrix2.m11, m11) != 0) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = (m00 != +0.0f ? Float.floatToIntBits(m00) : 0);
		result = 31 * result + (m01 != +0.0f ? Float.floatToIntBits(m01) : 0);
		result = 31 * result + (m10 != +0.0f ? Float.floatToIntBits(m10) : 0);
		result = 31 * result + (m11 != +0.0f ? Float.floatToIntBits(m11) : 0);
		return result;
	}

	@Override
	public Matrix2 clone() {
		return new Matrix2(this);
	}

	public static Matrix2 createScaling(double scale) {
		return createScaling((float) scale);
	}

	public static Matrix2 createScaling(float scale) {
		return createScaling(scale, scale);
	}

	public static Matrix2 createScaling(Vector2 v) {
		return createScaling(v.getX(), v.getY());
	}

	public static Matrix2 createScaling(float x, float y) {
		return new Matrix2(
				x, 0,
				0, y);
	}

	public static Matrix2 createTranslation(float x) {
		return new Matrix2(
				1, x,
				0, 1);
	}

	public static Matrix2 createRotation(Complex rot) {
		rot = rot.normalize();
		return new Matrix2(
				rot.getX(), -rot.getY(),
				rot.getY(), rot.getX());
	}

	private static class ImmutableIdentityMatrix2 extends Matrix2 {
		@Override
		public void set(int row, int col, float val) {
			throw new UnsupportedOperationException("You may not alter this matrix");
		}

		@Override
		public void setZero() {
			throw new UnsupportedOperationException("You may not alter this matrix");
		}
	}
}
