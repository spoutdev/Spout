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
package org.spout.math.imaginary;

import java.io.Serializable;

import org.spout.math.GenericMath;
import org.spout.math.TrigMath;
import org.spout.math.matrix.Matrix3;
import org.spout.math.vector.Vector2;

/**
 * Represent a complex number of the form <code>x + yi</code>. The x and y components are stored as floats. This class is immutable.
 */
public class Complex implements Imaginary, Comparable<Complex>, Serializable, Cloneable {
	private static final long serialVersionUID = 1;
	/**
	 * An immutable identity (1, 0) complex.
	 */
	public static final Complex IDENTITY = new Complex();
	private final float x;
	private final float y;
	private transient volatile boolean hashed = false;
	private transient volatile int hashCode = 0;

	/**
	 * Constructs a new complex. The components are set to the identity (1, 0).
	 */
	public Complex() {
		this(1, 0);
	}

	public Complex(float angle) {
		this.x = (float) Math.cos(Math.toRadians(angle));
		this.y = (float) Math.sin(Math.toRadians(angle));
	}

	/**
	 * Constructs a new complex from the double components.
	 *
	 * @param x The x (real) component
	 * @param y The y (imaginary) component
	 */
	public Complex(double x, double y) {
		this((float) x, (float) y);
	}

	/**
	 * Constructs a new complex from the float components.
	 *
	 * @param x The x (real) component
	 * @param y The y (imaginary) component
	 */
	public Complex(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Copy constructor.
	 *
	 * @param c The complex to copy
	 */
	public Complex(Complex c) {
		this.x = c.x;
		this.y = c.y;
	}

	/**
	 * Gets the x (real) component of this complex.
	 *
	 * @return The x (real) component
	 */
	public float getX() {
		return x;
	}

	/**
	 * Gets the y (imaginary) component of this complex.
	 *
	 * @return The y (imaginary) component
	 */
	public float getY() {
		return y;
	}

	/**
	 * Adds another complex to this one.
	 *
	 * @param c The complex to add
	 * @return A new complex, which is the sum of both
	 */
	public Complex add(Complex c) {
		return add(c.x, c.y);
	}

	/**
	 * Adds the double components of another complex to this one.
	 *
	 * @param x The x (real) component of the complex to add
	 * @param y The y (imaginary) component of the complex to add
	 * @return A new complex, which is the sum of both
	 */
	public Complex add(double x, double y) {
		return add((float) x, (float) y);
	}

	/**
	 * Adds the float components of another complex to this one.
	 *
	 * @param x The x (real) component of the complex to add
	 * @param y The y (imaginary) component of the complex to add
	 * @return A new complex, which is the sum of both
	 */
	public Complex add(float x, float y) {
		return new Complex(this.x + x, this.y + y);
	}

	/**
	 * Subtracts another complex from this one.
	 *
	 * @param c The complex to subtract
	 * @return A new complex, which is the difference of both
	 */
	public Complex sub(Complex c) {
		return sub(c.x, c.y);
	}

	/**
	 * Subtracts the double components of another complex from this one.
	 *
	 * @param x The x (real) component of the complex to subtract
	 * @param y The y (imaginary) component of the complex to subtract
	 * @return A new complex, which is the difference of both
	 */
	public Complex sub(double x, double y) {
		return sub((float) x, (float) y);
	}

	/**
	 * Subtracts the float components of another complex from this one.
	 *
	 * @param x The x (real) component of the complex to subtract
	 * @param y The y (imaginary) component of the complex to subtract
	 * @return A new complex, which is the difference of both
	 */
	public Complex sub(float x, float y) {
		return new Complex(this.x - x, this.y - y);
	}

	/**
	 * Multiplies the components of this complex by a double scalar.
	 *
	 * @param a The multiplication scalar
	 * @return A new complex, which has each component multiplied by the scalar
	 */
	public Complex mul(double a) {
		return mul((float) a);
	}

	/**
	 * Multiplies the components of this complex by a float scalar.
	 *
	 * @param a The multiplication scalar
	 * @return A new complex, which has each component multiplied by the scalar
	 */
	@Override
	public Complex mul(float a) {
		return new Complex(x * a, y * a);
	}

	/**
	 * Multiplies another complex with this one.
	 *
	 * @param c The complex to multiply with
	 * @return A new complex, which is the product of both
	 */
	public Complex mul(Complex c) {
		return mul(c.x, c.y);
	}

	/**
	 * Multiplies the double components of another complex with this one.
	 *
	 * @param x The x (real) component of the complex to multiply with
	 * @param y The y (imaginary) component of the complex to multiply with
	 * @return A new complex, which is the product of both
	 */
	public Complex mul(double x, double y) {
		return mul((float) x, (float) y);
	}

	/**
	 * Multiplies the float components of another complex with this one.
	 *
	 * @param x The x (real) component of the complex to multiply with
	 * @param y The y (imaginary) component of the complex to multiply with
	 * @return A new complex, which is the product of both
	 */
	public Complex mul(float x, float y) {
		return new Complex(
				this.x * x - this.y * y,
				this.x * y + this.y * x);
	}

	/**
	 * Divides the components of this complex by a double scalar.
	 *
	 * @param a The division scalar
	 * @return A new complex, which has each component divided by the scalar
	 */
	public Complex div(double a) {
		return div((float) a);
	}

	/**
	 * Divides the components of this complex by a float scalar.
	 *
	 * @param a The division scalar
	 * @return A new complex, which has each component divided by the scalar
	 */
	@Override
	public Complex div(float a) {
		return new Complex(x / a, y / a);
	}

	// TODO: complex division?

	/**
	 * Returns the dot product of this complex with another one.
	 *
	 * @param c The complex to calculate the dot product with
	 * @return The dot product of the two complexes
	 */
	public float dot(Complex c) {
		return dot(c.x, c.y);
	}

	/**
	 * Returns the dot product of this complex with the double components of another one.
	 *
	 * @param x The x (real) component of the complex to calculate the dot product with
	 * @param y The y (imaginary) component of the complex to calculate the dot product with
	 * @return The dot product of the two complexes
	 */
	public float dot(double x, double y) {
		return dot((float) x, (float) y);
	}

	/**
	 * Returns the dot product of this complex with the float components of another one.
	 *
	 * @param x The x (real) component of the complex to calculate the dot product with
	 * @param y The y (imaginary) component of the complex to calculate the dot product with
	 * @return The dot product of the two complexes
	 */
	public float dot(float x, float y) {
		return this.x * x + this.y * y;
	}

	/**
	 * Returns a unit vector pointing in the same direction as this complex on the complex plane.
	 *
	 * @return The vector representing the direction this complex is pointing to
	 */
	public Vector2 getDirection() {
		return new Vector2(x, y).normalize();
	}

	/**
	 * Returns the angle in radians formed by the direction vector of this complex on the complex plane.
	 *
	 * @return The angle in radians of the direction vector of this complex
	 */
	public float getAngleRad() {
		return (float) TrigMath.atan2(x, y);
	}

	/**
	 * Returns the angle in degrees formed by the direction vector of this complex on the complex plane.
	 *
	 * @return The angle in degrees of the direction vector of this complex
	 */
	public float getAngleDeg() {
		return (float) Math.toDegrees(getAngleRad());
	}

	/**
	 * Returns the conjugate of this complex. <br> Conjugation of a complex <code>a</code> is an operation returning complex <code>a'</code> such that <code>a' * a = a * a' = |a|<sup>2</sup></code> where
	 * <code>|a|<sup>2<sup/></code> is squared length of <code>a</code>.
	 *
	 * @return A new complex, which is the conjugate of this one
	 */
	@Override
	public Complex conjugate() {
		return new Complex(x, -y);
	}

	/**
	 * Returns the inverse of this complex. <br> Inversion of a complex <code>a</code> returns complex <code>a<sup>-1</sup> = a' / |a|<sup>2</sup></code> where <code>a'</code> is {@link #conjugate()
	 * conjugation} of <code>a</code>, and <code>|a|<sup>2</sup></code> is squared length of <code>a</code>. <br> For any complexes <code>a, b, c</code>, such that <code>a * b = c</code> equations
	 * <code>a<sup>-1</sup> * c = b</code> and <code>c * b<sup>-1</sup> = a</code> are true.
	 *
	 * @return A new complex, which is the inverse of this one
	 */
	@Override
	public Complex invert() {
		return conjugate().div(lengthSquared());
	}

	/**
	 * Returns the square of the length of this complex.
	 *
	 * @return The square of the length
	 */
	@Override
	public float lengthSquared() {
		return (float) GenericMath.lengthSquared(x, y);
	}

	/**
	 * Returns the length of this complex.
	 *
	 * @return The length
	 */
	@Override
	public float length() {
		return (float) GenericMath.length(x, y);
	}

	/**
	 * Normalizes this complex.
	 *
	 * @return A new complex of unit length
	 */
	@Override
	public Complex normalize() {
		final float length = length();
		return new Complex(x / length, y / length);
	}

	/**
	 * Converts this Complex into a {@link Matrix3}
	 *
	 * @return The {@link Matrix3}
	 */
	public Matrix3 toMatrix() {
		return new Matrix3(x, -y, 0,
						   y, x, 0,
						   0, 0, 1);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Complex)) {
			return false;
		}
		final Complex complex = (Complex) o;
		if (Float.compare(complex.x, x) != 0) {
			return false;
		}
		if (Float.compare(complex.y, y) != 0) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		if (!hashed) {
			final int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
			hashCode = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
			hashed = true;
		}
		return hashCode;
	}

	@Override
	public int compareTo(Complex c) {
		return (int) (lengthSquared() - c.lengthSquared());
	}

	@Override
	public Complex clone() {
		return new Complex(this);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	/**
	 * Creates a new complex from the angle defined from the first to the second vector.
	 *
	 * @param from The first vector
	 * @param to The second vector
	 * @return The complex defined by the angle between the vectors
	 */
	public static Complex fromRotationTo(Vector2 from, Vector2 to) {
		return fromAngleRad(TrigMath.acos(from.dot(to) / (from.length() * to.length())));
	}

	/**
	 * Creates a new complex from the double angle in degrees.
	 *
	 * @param angle The angle in degrees
	 * @return The complex defined by the angle
	 */
	public static Complex fromAngleDeg(double angle) {
		return fromAngleRad(Math.toRadians(angle));
	}

	/**
	 * Creates a new complex from the double angle in radians.
	 *
	 * @param angle The angle in radians
	 * @return The complex defined by the angle
	 */
	public static Complex fromAngleRad(double angle) {
		return fromAngleRad((float) angle);
	}

	/**
	 * Creates a new complex from the float angle in radians.
	 *
	 * @param angle The angle in radians
	 * @return The complex defined by the angle
	 */
	public static Complex fromAngleDeg(float angle) {
		return fromAngleRad((float) Math.toRadians(angle));
	}

	/**
	 * Creates a new complex from the float angle in radians.
	 *
	 * @param angle The angle in radians
	 * @return The complex defined by the angle
	 */
	public static Complex fromAngleRad(float angle) {
		return new Complex(TrigMath.cos(angle), TrigMath.sin(angle));
	}
}
