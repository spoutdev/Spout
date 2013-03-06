/*
 * This file is part of Math.
 *
 * Copyright (c) 2011-2013, Spout LLC <http://www.spout.org/>
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

/**
 * Class containing complex mathematical functions.
 */
public class ComplexMath {
	/**
	 * Returns the length squared of the given Complex
	 * @param a The complex
	 * @return The square of the length
	 */
	public static float lengthSquared(Complex a) {
		return a.x * a.x + a.y * a.y;
	}

	/**
	 * Returns the length of the given Comlex <br/> <br/> Note: Uses
	 * Math.sqrt.
	 * @param a The complex
	 * @return length of the complex
	 */
	public static float length(Complex a) {
		return (float) Math.sqrt(lengthSquared(a));
	}

	/**
	 * Constructs and returns a new Complex that is the given Complex but
	 * with a length of 1
	 * @param a The Complex
	 * @return normalized Complex
	 */
	public static Complex normalize(Complex a) {
		float length = length(a);
		return new Complex(a.x / length, a.y / length);
	}

	/**
	 * Constructs and returns a new Complex that is A * B
	 * @param a The left Complex
	 * @param b The right Complex
	 * @return The product Complex of left times b
	 */
	public static Complex multiply(Complex a, Complex b) {
		float x = a.x * b.x - a.y * b.y;

		float y = a.x * b.y + a.y * b.x;

		return new Complex(x, y);
	}

	/**
	 * Creates a Complex from an angle
	 * @param angle
	 * @return The Complex representation or the angle
	 */
	public static Complex rotation(float angle) {
		return new Complex(angle);
	}

	/**
	 * Constructs and returns a new Complex that is rotated about the axis
	 * and angle
	 * @param a The Complex
	 * @param angle The angle
	 * @return The rotated Complex
	 */
	public static Complex rotate(Complex a, float angle) {
		return multiply(rotation(angle), a);
	}

	/**
	 * Returns the rotation between two vectors.
	 * @param a The first vector
	 * @param b The second vector
	 * @return the rotation between both vectors
	 */
	public static Complex rotationTo(Vector2 a, Vector2 b) {
		if (a == b || a.equals(b)) {
			return Complex.IDENTITY;
		}
		// Normally the dot product must be divided by the product of the lengths,
		// but if they are both 1, we can skip that.
		a = a.normalize();
		b = b.normalize();
		return new Complex((float) Math.toDegrees(Math.acos(a.dot(b))));
	}
}