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

import org.spout.api.util.Color;

public class MathHelper {
	/**
	 * A "close to zero" double epsilon value for use
	 */
	public static final double DBL_EPSILON = Double.longBitsToDouble(0x3cb0000000000000L);

	/**
	 * A "close to zero" float epsilon value for use
	 */
	public static final float FLT_EPSILON = Float.intBitsToFloat(0x34000000);

	public static final double PI = Math.PI;

	public static final double SQUARED_PI = PI * PI;

	public static final double HALF_PI = 0.5 * PI;

	public static final double QUATER_PI = 0.5 * HALF_PI;

	public static final double TWO_PI = 2.0 * PI;

	public static final double THREE_PI_HALVES = TWO_PI - HALF_PI;

	/**
	 * Calculates the linear interpolation between a and b with the given
	 * percent
	 *
	 * @param a
	 * @param b
	 * @param percent
	 * @return
	 */
	public static double lerp(double a, double b, double percent) {
		return (1 - percent) * a + percent * b;
	}

	/**
	 * Calculates the linear interpolation between a and b with the given
	 * percent
	 *
	 * @param a
	 * @param b
	 * @param percent
	 * @return
	 */
	public static float lerp(float a, float b, float percent) {
		return (1 - percent) * a + percent * b;
	}

	/**
	 * Calculates the linear interpolation between a and b with the given
	 * percent
	 *
	 * @param a
	 * @param b
	 * @param percent
	 * @return
	 */
	public static int lerp(int a, int b, double percent) {
		return (int) ((1 - percent) * a + percent * b);
	}

	/**
	 * Calculates the linear interpolation between a and b with the given
	 * percent
	 *
	 * @param a
	 * @param b
	 * @param percent
	 * @return
	 */
	public static Vector3 lerp(Vector3 a, Vector3 b, float percent) {
		return a.multiply(1 - percent).add(b.multiply(percent));
	}

	/**
	 * Calculates the linear interpolation between a and b with the given
	 * percent
	 *
	 * @param a
	 * @param b
	 * @param percent
	 * @return
	 */
	public static Vector2 lerp(Vector2 a, Vector2 b, float percent) {
		return a.multiply(1 - percent).add(b.multiply(percent));
	}

	/**
	 * Calculates the linear interpolation between a and b with the given
	 * percent
	 *
	 * @param a
	 * @param b
	 * @param percent
	 * @return
	 */
	public static Color lerp(Color a, Color b, double percent) {
		int red = lerp(a.getRedI(), b.getRedI(), percent);
		int blue = lerp(a.getBlueI(), b.getBlueI(), percent);
		int green = lerp(a.getGreenI(), b.getGreenI(), percent);
		int alpha = lerp(a.getAlphaI(), b.getAlphaI(), percent);
		return new Color(red, blue, green, alpha);
	}

	public static double clamp(double value, double low, double high) {
		if (value < low) {
			return low;
		}
		if (value > high) {
			return high;
		}
		return value;
	}

	public static int clamp(int value, int low, int high) {
		if (value < low) {
			return low;
		}
		if (value > high) {
			return high;
		}
		return value;
	}

	/**
	 * Returns the forward vector transformed by the provided pitch and yaw
	 *
	 * @param pitch
	 * @param yaw
	 * @return
	 */
	public static Vector3 getDirectionVector(float pitch, float yaw) {
		return Vector3.transform(Vector3.UNIT_X, Matrix.rotate((new Quaternion(pitch, Vector3.UNIT_Z)).multiply(new Quaternion(yaw, Vector3.UNIT_Y))));
	}

	/**
	 * Returns the forward vector transformed by the provided quaternion
	 *
	 * @param pitch
	 * @param yaw
	 * @return
	 */
	public static Vector3 getDirectionVector(Quaternion rot) {
		return Vector3.transform(Vector3.UNIT_X, Matrix.rotate(rot));
	}

	//Fast Math Implementation
	public final static double cos(final double x) {
		return sin(x + (x > HALF_PI ? -THREE_PI_HALVES : HALF_PI));
	}

	public final static double sin(double x) {
		x = sin_a * x * Math.abs(x) + sin_b * x;
		return sin_p * (x * Math.abs(x) - x) + x;
	}

	public final static double tan(final double x) {
		return sin(x) / cos(x);
	}

	public final static double asin(final double x) {
		return x * (Math.abs(x) * (Math.abs(x) * asin_a + asin_b) + asin_c) + Math.signum(x) * (asin_d - Math.sqrt(1 - x * x));
	}

	public final static double acos(final double x) {
		return HALF_PI - asin(x);
	}

	public final static double atan(final double x) {
		return Math.abs(x) < 1 ? x / (1 + atan_a * x * x) : Math.signum(x) * HALF_PI - x / (x * x + atan_a);
	}

	public final static double inverseSqrt(double x) {
		final double xhalves = 0.5d * x;
		x = Double.longBitsToDouble(0x5FE6EB50C7B537AAl - (Double.doubleToRawLongBits(x) >> 1));
		return x * (1.5d - xhalves * x * x);
	}

	public final static double sqrt(final double x) {
		return x * inverseSqrt(x);
	}

	private static final double sin_a = -4 / SQUARED_PI;

	private static final double sin_b = 4 / PI;

	private static final double sin_p = 9d / 40;

	private final static double asin_a = -0.0481295276831013447d;

	private final static double asin_b = -0.343835993947915197d;

	private final static double asin_c = 0.962761848425913169d;

	private final static double asin_d = 1.00138940860107040d;

	private final static double atan_a = 0.280872d;

	// Integer Maths
	
	public static int floor(double x) {
		int y = (int)x;
		if (x < y) return y - 1;
		return y;
	}
	
	public static int floor(float x) {
		int y = (int)x;
		if (x < y) return y - 1;
		return y;
	}

	/**
	 * Rounds an integer up to the next power of 2.
	 *
	 * @param x
	 * @return the lowest power of 2 greater or equal to x
	 */
	public static int roundUpPow2(int x) {
		if (x <= 0) {
			return 1;
		} else if (x > 0x40000000) {
			throw new IllegalArgumentException("Rounding " + x + " to the next highest power of two would exceed the int range");
		} else {
			x--;
			x |= x >> 1;
			x |= x >> 2;
			x |= x >> 4;
			x |= x >> 8;
			x |= x >> 16;
			x++;
			return x;
		}
	}
}
