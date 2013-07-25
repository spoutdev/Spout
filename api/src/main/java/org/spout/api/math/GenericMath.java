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
package org.spout.api.math;

import java.awt.Color;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Class containing generic mathematical functions.
 */
public class GenericMath {
	/**
	 * A "close to zero" double epsilon value for use
	 */
	public static final double DBL_EPSILON = Double.longBitsToDouble(0x3cb0000000000000L);
	/**
	 * A "close to zero" float epsilon value for use
	 */
	public static final float FLT_EPSILON = Float.intBitsToFloat(0x34000000);
	private static final ThreadLocal<Random> THREAD_LOCAL_RANDOM = new ThreadLocal<Random>() {
		private final Random random = new SecureRandom();

		@Override
		protected Random initialValue() {
			synchronized (random) {
				return new Random(random.nextLong());
			}
		}
	};

	private GenericMath() {
	}

	/**
	 * Calculates the squared length of all axis offsets given
	 *
	 * @param values of the axis to get the squared length of
	 * @return the squared length
	 */
	public static double lengthSquared(double... values) {
		double rval = 0;
		for (double value : values) {
			rval += value * value;
		}
		return rval;
	}

	/**
	 * Calculates the length of all axis offsets given
	 *
	 * @param values of the axis to get the length of
	 * @return the length
	 */
	public static double length(double... values) {
		return Math.sqrt(lengthSquared(values));
	}

	/**
	 * Calculates the squared length of all axis offsets given
	 *
	 * @param values of the axis to get the squared length of
	 * @return the squared length
	 */
	public static int lengthSquared(int... values) {
		int rval = 0;
		for (int value : values) {
			rval += value * value;
		}
		return rval;
	}

	/**
	 * Calculates the length of all axis offsets given
	 *
	 * @param values of the axis to get the length of
	 * @return the length
	 */
	public static int length(int... values) {
		return (int) Math.sqrt(lengthSquared(values));
	}

	/**
	 * Gets the difference between two angles This value is always positive (0 - 180)
	 *
	 * @param angle1 The first angle
	 * @param angle2 The second angle
	 * @return the positive angle difference
	 */
	public static float getAngleDifference(float angle1, float angle2) {
		return Math.abs(wrapAngle(angle1 - angle2));
	}

	/**
	 * Gets the difference between two radians This value is always positive (0 - PI)
	 *
	 * @param radian1 The first angle
	 * @param radian2 The second angle
	 * @return the positive radian difference
	 */
	public static double getRadianDifference(double radian1, double radian2) {
		return Math.abs(wrapRadian(radian1 - radian2));
	}

	/**
	 * Wraps the angle between -180 and 180 degrees
	 *
	 * @param angle to wrap
	 * @return -180 < angle <= 180
	 */
	public static float wrapAngle(float angle) {
		angle %= 360f;
		if (angle <= -180) {
			return angle + 360;
		} else if (angle > 180) {
			return angle - 360;
		} else {
			return angle;
		}
	}

	/**
	 * Wraps the pitch angle between -90 and 90 degrees
	 *
	 * @param angle to wrap
	 * @return -90 < angle < 90
	 */
	public static float wrapAnglePitch(float angle) {
		angle = wrapAngle(angle);

		if (angle < -90) {
			return -90;
		}
		if (angle > 90) {
			return 90;
		}
		return angle;
	}

	/**
	 * Wraps a byte between 0 and 256
	 *
	 * @param value to wrap
	 * @return 0 < byte < 256
	 */
	public static byte wrapByte(int value) {
		value %= 256;
		if (value < 0) {
			value += 256;
		}
		return (byte) value;
	}

	/**
	 * Wraps the radian between -PI and PI
	 *
	 * @param radian to wrap
	 * @return -PI < radian <= PI
	 */
	public static double wrapRadian(double radian) {
		radian %= TrigMath.TWO_PI;
		if (radian <= -TrigMath.PI) {
			return radian + TrigMath.TWO_PI;
		} else if (radian > TrigMath.PI) {
			return radian - TrigMath.TWO_PI;
		} else {
			return radian;
		}
	}

	/**
	 * Rounds a number to the amount of decimals specified
	 *
	 * @param input to round
	 * @param decimals to round to
	 * @return the rounded number
	 */
	public static double round(double input, int decimals) {
		double p = Math.pow(10, decimals);
		return Math.round(input * p) / p;
	}

	/**
	 * Calculates the linear interpolation between a and b with the given percent
	 *
	 * @param a The first know value
	 * @param b The second know value
	 * @param percent The percent
	 * @return the interpolated value
	 */
	public static double lerp(double a, double b, double percent) {
		return (1 - percent) * a + percent * b;
	}

	/**
	 * Calculates the linear interpolation between a and b with the given percent
	 *
	 * @param a The first know value
	 * @param b The second know value
	 * @param percent The percent
	 * @return the interpolated value
	 */
	public static float lerp(float a, float b, float percent) {
		return (1 - percent) * a + percent * b;
	}

	/**
	 * Calculates the linear interpolation between a and b with the given percent
	 *
	 * @param a The first know value
	 * @param b The second know value
	 * @param percent The percent
	 * @return the interpolated value
	 */
	public static int lerp(int a, int b, double percent) {
		return (int) ((1 - percent) * a + percent * b);
	}

	/**
	 * Calculates the linear interpolation between a and b with the given percent
	 *
	 * @param a The first know value
	 * @param b The second know value
	 * @param percent The percent
	 * @return the interpolated vector
	 */
	public static Vector3 lerp(Vector3 a, Vector3 b, float percent) {
		return a.multiply(1 - percent).add(b.multiply(percent));
	}

	/**
	 * Calculates the linear interpolation between a and b with the given percent
	 *
	 * @param a The first know value
	 * @param b The second know value
	 * @param percent The percent
	 * @return the interpolated vector
	 */
	public static Vector2 lerp(Vector2 a, Vector2 b, float percent) {
		return a.multiply(1 - percent).add(b.multiply(percent));
	}

	/**
	 * Calculates the value at x using linear interpolation
	 *
	 * @param x the X coord of the value to interpolate
	 * @param x1 the X coord of q0
	 * @param x2 the X coord of q1
	 * @param q0 the first known value (x1)
	 * @param q1 the second known value (x2)
	 * @return the interpolated value
	 */
	public static double lerp(double x, double x1, double x2, double q0, double q1) {
		return ((x2 - x) / (x2 - x1)) * q0 + ((x - x1) / (x2 - x1)) * q1;
	}

	/**
	 * Calculates the linear interpolation between a and b with the given percent
	 *
	 * @param a The first know value
	 * @param b The second know value
	 * @param percent The percent
	 * @return Color
	 */
	public static Color lerp(Color a, Color b, double percent) {
		int red = lerp(a.getRed(), b.getRed(), percent);
		int blue = lerp(a.getBlue(), b.getBlue(), percent);
		int green = lerp(a.getGreen(), b.getGreen(), percent);
		int alpha = lerp(a.getAlpha(), b.getAlpha(), percent);
		return new Color(red, green, blue, alpha);
	}

	/**
	 * Calculates the linear interpolation between a and b with the given percent
	 *
	 * @param a The first know value
	 * @param b The second know value
	 * @param percent The percent
	 * @return Quarternion
	 */
	public static Quaternion lerp(Quaternion a, Quaternion b, float percent) {
		float x = lerp(a.getX(), b.getX(), percent);
		float y = lerp(a.getY(), b.getY(), percent);
		float z = lerp(a.getZ(), b.getZ(), percent);
		float w = lerp(a.getW(), b.getW(), percent);
		return new Quaternion(x, y, z, w, true);
	}

	/**
	 * Calculates the value at x,y using bilinear interpolation
	 *
	 * @param x the X coord of the value to interpolate
	 * @param y the Y coord of the value to interpolate
	 * @param q00 the first known value (x1, y1)
	 * @param q01 the second known value (x1, y2)
	 * @param q10 the third known value (x2, y1)
	 * @param q11 the fourth known value (x2, y2)
	 * @param x1 the X coord of q00 and q01
	 * @param x2 the X coord of q10 and q11
	 * @param y1 the Y coord of q00 and q10
	 * @param y2 the Y coord of q01 and q11
	 * @return the interpolated value
	 */
	public static double biLerp(double x, double y, double q00, double q01,
								double q10, double q11, double x1, double x2, double y1, double y2) {
		double q0 = lerp(x, x1, x2, q00, q10);
		double q1 = lerp(x, x1, x2, q01, q11);
		return lerp(y, y1, y2, q0, q1);
	}

	/**
	 * Calculates the value at a target using bilinear interpolation
	 *
	 * @param target the vector of the value to interpolate
	 * @param q00 the first known value (known1.x, known1.y)
	 * @param q01 the second known value (known1.x, known2.y)
	 * @param q10 the third known value (known2.x, known1.y)
	 * @param q11 the fourth known value (known2.x, known2.y)
	 * @param known1 the X coord of q00 and q01 and the Y coord of q00 and q10
	 * @param known2 the X coord of q10 and q11 and the Y coord of q01 and q11
	 * @return the interpolated value
	 */
	public static double biLerp(Vector2 target, double q00, double q01,
								double q10, double q11, Vector2 known1, Vector2 known2) {
		return biLerp(target.getX(), target.getY(), q00, q01, q10, q11,
				known1.getX(), known2.getX(), known1.getY(), known2.getY());
	}

	/**
	 * Calculates the value at x,y,z using trilinear interpolation
	 *
	 * @param x the X coord of the value to interpolate
	 * @param y the Y coord of the value to interpolate
	 * @param z the Z coord of the value to interpolate
	 * @param q000 the first known value (x1, y1, z1)
	 * @param q001 the second known value (x1, y2, z1)
	 * @param q010 the third known value (x1, y1, z2)
	 * @param q011 the fourth known value (x1, y2, z2)
	 * @param q100 the fifth known value (x2, y1, z1)
	 * @param q101 the sixth known value (x2, y2, z1)
	 * @param q110 the seventh known value (x2, y1, z2)
	 * @param q111 the eighth known value (x2, y2, z2)
	 * @param x1 the X coord of q000, q001, q010 and q011
	 * @param x2 the X coord of q100, q101, q110 and q111
	 * @param y1 the Y coord of q000, q010, q100 and q110
	 * @param y2 the Y coord of q001, q011, q101 and q111
	 * @param z1 the Z coord of q000, q001, q100 and q101
	 * @param z2 the Z coord of q010, q011, q110 and q111
	 * @return the interpolated value
	 */
	public static double triLerp(double x, double y, double z, double q000, double q001,
								 double q010, double q011, double q100, double q101, double q110, double q111,
								 double x1, double x2, double y1, double y2, double z1, double z2) {
		double q00 = lerp(x, x1, x2, q000, q100);
		double q01 = lerp(x, x1, x2, q010, q110);
		double q10 = lerp(x, x1, x2, q001, q101);
		double q11 = lerp(x, x1, x2, q011, q111);
		double q0 = lerp(y, y1, y2, q00, q10);
		double q1 = lerp(y, y1, y2, q01, q11);
		return lerp(z, z1, z2, q0, q1);
	}

	/**
	 * Calculates the value at target using trilinear interpolation
	 *
	 * @param target the vector of the value to interpolate
	 * @param q000 the first known value (known1.x, known1.y, known1.z)
	 * @param q001 the second known value (known1.x, known2.y, known1.z)
	 * @param q010 the third known value (known1.x, known1.y, known2.z)
	 * @param q011 the fourth known value (known1.x, known2.y, known2.z)
	 * @param q100 the fifth known value (known2.x, known1.y, known1.z)
	 * @param q101 the sixth known value (known2.x, known2.y, known1.z)
	 * @param q110 the seventh known value (known2.x, known1.y, known2.z)
	 * @param q111 the eighth known value (known2.x, known2.y, known2.z)
	 * @param known1 the X coord of q000, q001, q010 and q011, the Y coord of q000, q010, q100 and q110 and the Z coord of q000, q001, q100 and q101
	 * @param known2 the X coord of q100, q101, q110 and q111, the Y coord of q001, q011, q101 and q111 and the Z coord of q010, q011, q110 and q111
	 * @return the interpolated value
	 */
	public static double triLerp(Vector3 target, double q000, double q001, double q010,
								 double q011, double q100, double q101, double q110, double q111, Vector3 known1, Vector3 known2) {
		return triLerp(target.getX(), target.getY(), target.getZ(), q000, q001, q010, q011, q100, q101, q110, q111,
				known1.getX(), known2.getX(), known1.getY(), known2.getY(), known1.getZ(), known2.getZ());
	}

	/**
	 * Blends two colors into one.
	 *
	 * @param a The first color
	 * @param b The second color
	 * @return The blended color
	 */
	public static Color blend(Color a, Color b) {
		int red = lerp(a.getRed(), b.getRed(), (a.getAlpha() / 255.0));
		int blue = lerp(a.getBlue(), b.getBlue(), (a.getAlpha() / 255.0));
		int green = lerp(a.getGreen(), b.getGreen(), (a.getAlpha() / 255.0));
		int alpha = lerp(a.getAlpha(), b.getAlpha(), (a.getAlpha() / 255.0));
		return new Color(red, green, blue, alpha);
	}

	/**
	 * Generates a random color
	 *
	 * @return Random color
	 */
	public static Color randomColor() {
		Random rng = new Random();
		return new Color(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255));
	}

	/**
	 * Clamps the value between the low and high boundaries
	 *
	 * @param value The value to clamp
	 * @param low The low bound of the clamp
	 * @param high The high bound of the clamp
	 * @return the clamped value
	 */
	public static double clamp(double value, double low, double high) {
		if (value < low) {
			return low;
		}
		if (value > high) {
			return high;
		}
		return value;
	}

	/**
	 * Clamps the value between the low and high boundaries
	 *
	 * @param value The value to clamp
	 * @param low The low bound of the clamp
	 * @param high The high bound of the clamp
	 * @return the clamped value
	 */
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
	 * Returns a fast estimate of the inverse square root of the value
	 *
	 * @param x The value
	 * @return The estimate of the inverse square root
	 */
	public static double inverseSqrt(double x) {
		final double xhalves = 0.5d * x;
		x = Double.longBitsToDouble(0x5FE6EB50C7B537AAl - (Double.doubleToRawLongBits(x) >> 1));
		return x * (1.5d - xhalves * x * x);
	}

	/**
	 * <<<<<<< HEAD ======= Returns a fast estimate of the square root of the value
	 *
	 * @param x The value
	 * @return The estimate of the square root
	 */
	public static double sqrt(double x) {
		return x * inverseSqrt(x);
	}

	/**
	 * >>>>>>> scene Rounds x down to the closest integer
	 *
	 * @param x The value to floor
	 * @return The closest integer
	 */
	public static int floor(double x) {
		int y = (int) x;
		if (x < y) {
			return y - 1;
		}
		return y;
	}

	/**
	 * Rounds x down to the closest integer
	 *
	 * @param x The value to floor
	 * @return The closest integer
	 */
	public static int floor(float x) {
		int y = (int) x;
		if (x < y) {
			return y - 1;
		}
		return y;
	}

	/**
	 * Gets the maximum byte value from two values
	 *
	 * @param value1 The first value
	 * @param value2 The second value
	 * @return the maximum of value1 and value2
	 */
	public static byte max(byte value1, byte value2) {
		return value1 > value2 ? value1 : value2;
	}

	/**
	 * Rounds an integer up to the next power of 2.
	 *
	 * @param x The integer to round
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

	/**
	 * Rounds an integer up to the next power of 2.
	 *
	 * @param x The long to round
	 * @return the lowest power of 2 greater or equal to x
	 */
	public static long roundUpPow2(long x) {
		if (x <= 0) {
			return 1;
		} else if (x > 0x4000000000000000L) {
			throw new IllegalArgumentException("Rounding " + x + " to the next highest power of two would exceed the int range");
		} else {
			x--;
			x |= x >> 1;
			x |= x >> 2;
			x |= x >> 4;
			x |= x >> 8;
			x |= x >> 16;
			x |= x >> 32;
			x++;
			return x;
		}
	}

	/**
	 * Converts a multiplication into a shift.
	 *
	 * @param x the multiplicand
	 * @return the left shift required to multiply by the multiplicand
	 */
	public static int multiplyToShift(int x) {
		if (x < 1) {
			throw new IllegalArgumentException("Multiplicand must be at least 1");
		}
		int shift = 31 - Integer.numberOfLeadingZeros(x);
		if ((1 << shift) != x) {
			throw new IllegalArgumentException("Multiplicand must be a power of 2");
		}
		return shift;
	}

	/**
	 * Casts a value to a float. May return null.
	 *
	 * @param o The object to attempt to cast
	 * @return The object as a float
	 */
	public static Float castFloat(Object o) {
		if (o == null) {
			return null;
		}

		if (o instanceof Number) {
			return ((Number) o).floatValue();
		}

		try {
			return Float.valueOf(o.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Casts a value to a byte. May return null.
	 *
	 * @param o The object to attempt to cast
	 * @return The object as a byte
	 */
	public static Byte castByte(Object o) {
		if (o == null) {
			return null;
		}

		if (o instanceof Number) {
			return ((Number) o).byteValue();
		}

		try {
			return Byte.valueOf(o.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Casts a value to a short. May return null.
	 *
	 * @param o The object to attempt to cast
	 * @return The object as a short
	 */
	public static Short castShort(Object o) {
		if (o == null) {
			return null;
		}

		if (o instanceof Number) {
			return ((Number) o).shortValue();
		}

		try {
			return Short.valueOf(o.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Casts a value to an integer. May return null.
	 *
	 * @param o The object to attempt to cast
	 * @return The object as an int
	 */
	public static Integer castInt(Object o) {
		if (o == null) {
			return null;
		}

		if (o instanceof Number) {
			return ((Number) o).intValue();
		}

		try {
			return Integer.valueOf(o.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Casts a value to a double. May return null.
	 *
	 * @param o The object to attempt to cast
	 * @return The object as a double
	 */
	public static Double castDouble(Object o) {
		if (o == null) {
			return null;
		}

		if (o instanceof Number) {
			return ((Number) o).doubleValue();
		}

		try {
			return Double.valueOf(o.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Casts a value to a long. May return null.
	 *
	 * @param o The object to attempt to cast
	 * @return The object as a long
	 */
	public static Long castLong(Object o) {
		if (o == null) {
			return null;
		}

		if (o instanceof Number) {
			return ((Number) o).longValue();
		}

		try {
			return Long.valueOf(o.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Casts a value to a boolean. May return null.
	 *
	 * @param o The object to attempt to cast
	 * @return The object as a boolean
	 */
	public static Boolean castBoolean(Object o) {
		if (o == null) {
			return null;
		}

		if (o instanceof Boolean) {
			return (Boolean) o;
		} else if (o instanceof String) {
			try {
				return Boolean.parseBoolean((String) o);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}

		return null;
	}

	/**
	 * Calculates the mean of a set of values
	 *
	 * @param values to calculate the mean of
	 * @return the mean of the values
	 */
	public static int mean(int... values) {
		int sum = 0;
		for (int v : values) {
			sum += v;
		}
		return sum / values.length;
	}

	/**
	 * Calculates the mean of a set of values.
	 *
	 * @param values to calculate the mean of
	 * @return the mean of the values
	 */
	public static double mean(double... values) {
		double sum = 0;
		for (double v : values) {
			sum += v;
		}
		return sum / values.length;
	}

	/**
	 * Converts an integer to hexadecimal form with at least the minimum of digits specified (by adding leading zeros).
	 *
	 * @param dec The integer to convert
	 * @param minDigits The minimum of digits in the hexadecimal form
	 * @return The integer in hexadecimal form
	 */
	public static String decToHex(int dec, int minDigits) {
		String ret = Integer.toHexString(dec);
		while (ret.length() < minDigits) {
			ret = '0' + ret;
		}
		return ret;
	}

	/**
	 * Returns the modulo of x by div with corrections for negative numbers.
	 *
	 * @param x The number as an int
	 * @param div The div as an int
	 * @return The corrected modulo
	 */
	public static int mod(int x, int div) {
		return x < 0 ? ((x + 1) % div) + (div - 1) : x % div;
	}

	/**
	 * Returns the modulo of x by div with corrections for negative numbers.
	 *
	 * @param x The number as an float
	 * @param div The div as an float
	 * @return The corrected modulo
	 */
	public static float mod(float x, float div) {
		float m = x % div;
		while (m < 0) {
			m += div;
		}
		return m;
	}

	/**
	 * Returns the modulo of x by div with corrections for negative numbers.
	 *
	 * @param x The number as an double
	 * @param div The div as an double
	 * @return The corrected modulo
	 */
	public static double mod(double x, double div) {
		double m = x % div;
		while (m < 0) {
			m += div;
		}
		return m;
	}

	/**
	 * Gets a thread local Random object that is seeded using SecureRandom. Only one Random is created per thread.
	 *
	 * @return The random for the thread
	 */
	public static Random getRandom() {
		return THREAD_LOCAL_RANDOM.get();
	}
}
