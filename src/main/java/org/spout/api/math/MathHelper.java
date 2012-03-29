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

import java.awt.Color;

/**
 * Class containing various mathematical functions
 */
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

	public static final double QUARTER_PI = 0.5 * HALF_PI;

	public static final double TWO_PI = 2.0 * PI;

	public static final double THREE_PI_HALVES = TWO_PI - HALF_PI;

	public static final double DEGTORAD = PI / 180.0;

	public static final double RADTODEG = 180.0 / PI;

	public static final double SQRTOFTWO = Math.sqrt(2.0);
	
	public static final double HALF_SQRTOFTWO = 0.5 * SQRTOFTWO;

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
	 * Gets the difference between two angles
	 * This value is always positive (0 - 180)
	 * 
	 * @param angle1
	 * @param angle2
	 * @return the positive angle difference
	 */
	public static float getAngleDifference(float angle1, float angle2) {
		return Math.abs(wrapAngle(angle1 - angle2));
	}

	/**
	 * Gets the difference between two radians
	 * This value is always positive (0 - PI)
	 * 
	 * @param radian1
	 * @param radian2
	 * @return the positive radian difference
	 */
	public static double getRadianDifference(double radian1, double radian2) {
		return Math.abs(wrapRadian(radian1 - radian2));
	}

	/**
	 * Wraps the angle between -180 and 180 degrees
	 * 
	 * @param angle to wrap
	 * @return -180 > angle <= 180
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
	 * Wraps the radian between -PI and PI
	 * 
	 * @param radian to wrap
	 * @return -PI > radian <= PI
	 */
	public static double wrapRadian(double radian) {
		radian %= TWO_PI;
		if (radian <= -PI) {
			return radian + TWO_PI;
		} else if (radian > PI) {
			return radian - TWO_PI;
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
		int red = lerp(a.getRed(), b.getRed(), percent);
		int blue = lerp(a.getBlue(), b.getBlue(), percent);
		int green = lerp(a.getGreen(), b.getGreen(), percent);
		int alpha = lerp(a.getAlpha(), b.getAlpha(), percent);
		return new Color(red, green, blue, alpha);
	}
	
	public static Quaternion lerp(Quaternion a, Quaternion b, float percent){
		float x = lerp(a.getX(), b.getX(), percent);
		float y = lerp(a.getY(), b.getY(), percent);
		float z = lerp(a.getZ(), b.getZ(), percent);
		float w = lerp(a.getW(), b.getW(), percent);
		return new Quaternion(x,y,z,w, true);
		
	
	}
	
	
	public static Color blend(Color a, Color b){
		int red = lerp(a.getRed(), b.getRed(), (a.getAlpha()/255.0));
		int blue = lerp(a.getBlue(), b.getBlue(), (a.getAlpha()/255.0));
		int green = lerp(a.getGreen(), b.getGreen(), (a.getAlpha()/255.0));
		int alpha = lerp(a.getAlpha(), b.getAlpha(), (a.getAlpha()/255.0));
		return new Color(red, green, blue, alpha);
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
		return Vector3.transform(Vector3.UNIT_X, Matrix.rotate(new Quaternion(pitch, Vector3.UNIT_Z).multiply(new Quaternion(yaw, Vector3.UNIT_Y))));
	}

	/**
	 * Returns the forward vector transformed by the provided quaternion
	 *
	 * @param pitch
	 * @param yaw
	 * @return
	 */
	public static Vector3 getDirectionVector(Quaternion rot) {
		return Vector3.transform(Vector3.FORWARD, Matrix.rotate(rot));
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
		int y = (int) x;
		if (x < y) {
			return y - 1;
		}
		return y;
	}

	public static int floor(float x) {
		int y = (int) x;
		if (x < y) {
			return y - 1;
		}
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
	
	/**
	 * Casts a value to an integer. May return null.
	 *
	 * @param o
	 * @return
	 */
	public static Integer castInt(Object o) {
		if (o instanceof Number) {
			return ((Number)o).intValue();
		}
		
		return null;
	}

	/**
	 * Casts a value to a double. May return null.
	 *
	 * @param o
	 * @return
	 */
	public static Double castDouble(Object o) {
		if (o instanceof Number) {
			return ((Number)o).doubleValue();
		}
		
		return null;
	}

	/**
	 * Casts a value to a boolean. May return null.
	 *
	 * @param o
	 * @return
	 */
	public static Boolean castBoolean(Object o) {
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
}