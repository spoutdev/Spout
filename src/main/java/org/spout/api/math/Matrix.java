/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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

import java.io.Serializable;

/**
 * Representation of a square matrix
 */
public class Matrix implements Serializable {
	private static final long serialVersionUID = 1L;

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
					data[MathHelper.index(x, y, dim)] = 1;
				} else {
					data[MathHelper.index(x, y, dim)] = 0;
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
	public Matrix(Matrix copy) {
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
		return data[MathHelper.index(row, column, dimension)];
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
		data[MathHelper.index(row, column, dimension)] = value;
	}

	/**
	 * Multiplies this matrix with the provided matrix
	 *
	 * @param that
	 * @return
	 */
	public Matrix multiply(Matrix that) {
		return MathHelper.multiply(this, that);
	}

	/**
	 * Adds this matrix to the given matrix
	 *
	 * @param that
	 * @return
	 */
	public Matrix add(Matrix that) {
		return MathHelper.add(this, that);
	}

	/**
	 * Returns this matrix in a single dimension float array
	 *
	 * @return
	 */
	public float[] toArray() {
		return MathHelper.toArray(this);
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
}
