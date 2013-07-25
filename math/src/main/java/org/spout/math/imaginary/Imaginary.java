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
package org.spout.math.imaginary;

/**
 * Represents an imaginary number.
 */
public interface Imaginary {
	/**
	 * Multiplies the imaginary number by the given scalar.
	 *
	 * @param a The scalar to multiply by
	 * @return The multiplied imaginary number
	 */
	public Imaginary mul(float a);

	/**
	 * Divides the imaginary number by the given scalar.
	 *
	 * @param a The scalar to divide by
	 * @return The multiplied imaginary number
	 */
	public Imaginary div(float a);

	/**
	 * Returns the conjugated imaginary number.
	 *
	 * @return The conjugate
	 */
	public Imaginary conjugate();

	/**
	 * Returns the inverts imaginary number.
	 *
	 * @return The inverse
	 */
	public Imaginary invert();

	/**
	 * Returns the length of the imaginary number.
	 *
	 * @return The length
	 */
	public float length();

	/**
	 * Returns the square of the length of the imaginary number.
	 *
	 * @return The square of the length
	 */
	public float lengthSquared();

	/**
	 * Normalizes the imaginary number.
	 *
	 * @return The imaginary number, but of unit length
	 */
	public Imaginary normalize();
}
