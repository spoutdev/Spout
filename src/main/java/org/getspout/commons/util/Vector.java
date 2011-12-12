/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.commons.util;

public interface Vector extends FixedVector {

	/**
	 * Set the X component.
	 * 
	 * @param x
	 * @return this vector
	 */
	public Vector setX(double x);

	/**
	 * Set the Y component.
	 * 
	 * @param y
	 * @return this vector
	 */
	public Vector setY(double y);

	/**
	 * Set the Z component.
	 * 
	 * @param z
	 * @return this vector
	 */
	public Vector setZ(double z);

	/**
	 * Adds the vector by another.
	 * 
	 * @param vec
	 * @return the same vector
	 */
	public Vector add(Vector vec);

	/**
	 * Subtracts the vector by another.
	 * 
	 * @param vec
	 * @return the same vector
	 */
	public Vector subtract(Vector vec);

	/**
	 * Multiplies the vector by another.
	 * 
	 * @param vec
	 * @return the same vector
	 */
	public Vector multiply(Vector vec);

	/**
	 * Divides the vector by another.
	 * 
	 * @param vec
	 * @return the same vector
	 */
	public Vector divide(Vector vec);

	/**
	 * Copies another vector
	 * 
	 * @param vec
	 * @return the same vector
	 */
	public Vector copy(Vector vec);

	/**
	 * Sets this vector to the midpoint between this vector and another.
	 * 
	 * @param other
	 * @return this same vector (now a midpoint)
	 */
	public Vector midpoint(Vector other);

	/**
	 * Gets a new midpoint vector between this vector and another.
	 * 
	 * @param other
	 * @return a new midpoint vector
	 */
	public Vector getMidpoint(Vector other);

	/**
	 * Performs scalar multiplication, multiplying all components with a scalar.
	 * 
	 * @param m
	 * @return the same vector
	 */
	public Vector multiply(double m);

	/**
	 * Calculates the cross product of this vector with another. The cross product is defined as:
	 * 
	 * x = y1 * z2 - y2 * z1<br/>
	 * y = z1 * x2 - z2 * x1<br/>
	 * z = x1 * y2 - x2 * y1
	 * 
	 * @param o
	 * @return the same vector
	 */
	public Vector crossProduct(Vector o);

	/**
	 * Converts this vector to a unit vector (a vector with length of 1).
	 * 
	 * @return the same vector
	 */
	public Vector normalize();

	/**
	 * Zero this vector's components.
	 * 
	 * @return the same vector
	 */
	public Vector zero();
}
