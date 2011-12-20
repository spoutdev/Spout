package org.getspout.api.math;

import org.getspout.api.util.Color;

public class MathHelper {
	public static final double PI = Math.PI;
	public static final double SQUARED_PI = PI * PI;
	public static final double HALF_PI = 0.5 * PI;
	public static final double QUATER_PI = 0.5 * HALF_PI;
	public static final double TWO_PI = 2.0 * PI;
	public static final double THREE_PI_HALVES = TWO_PI - HALF_PI;

	/**
	 * Calculates the linear interpolation between a and b with the given percent
	 * @param a
	 * @param b
	 * @param percent
	 * @return
	 */
	public static double lerp(double a, double b, double percent) {
		return (1-percent) * a + percent * b;
	}
	/**
	 * Calculates the linear interpolation between a and b with the given percent
	 * @param a
	 * @param b
	 * @param percent
	 * @return
	 */
	public static int lerp(int a, int b, double percent) {
		return (int) ((1-percent) * a + percent * b);
	}
	/**
	 * Calculates the linear interpolation between a and b with the given percent
	 * @param a
	 * @param b
	 * @param percent
	 * @return
	 */
	public static Vector3 lerp(Vector3 a, Vector3 b, double percent) {
		return a.scale(1-percent).add(b.scale(percent));
	}
	/**
	 * Calculates the linear interpolation between a and b with the given percent
	 * @param a
	 * @param b
	 * @param percent
	 * @return
	 */
	public static Vector2 lerp(Vector2 a, Vector2 b, double percent) {
		return a.scale(1-percent).add(b.scale(percent));
	}
	/**
	 * Calculates the linear interpolation between a and b with the given percent
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
		if (value < low) return low;
		if (value > high) return high;
		return value;
	}

	public static int clamp(int value, int low, int high) {
		if (value < low) return low;
		if (value > high) return high;
		return value;
	}

	/**
	 * Returns the forward vector transformed by the provided pitch and yaw
	 * @param pitch
	 * @param yaw
	 * @return
	 */
	public static Vector3 getDirectionVector(double pitch, double yaw){
		return Vector3.transform(Vector3.UNIT_X, Matrix.rotateY(pitch).multiply(Matrix.rotateZ(yaw)));
	}
	
	
	
	

	
	//Fast Math Implementation

	public final static double cos(final double x) {
		return sin(x + ((x > HALF_PI) ? -THREE_PI_HALVES : HALF_PI));
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
		return (Math.abs(x) < 1) ? x / (1 + atan_a * x * x) : Math.signum(x) * HALF_PI - x / (x * x + atan_a);
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
}
