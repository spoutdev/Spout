package org.getspout.api.math;

/**
 * A 2-dimensional vector represented by double-precision x,y coordinates
 *
 * Note, this is the Immutable form of Vector2. All operations will construct a
 * new Vector2.
 */
public class Vector2 implements Comparable<Vector2> {
	/**
	 * Represents the Zero vector (0,0)
	 */
	public static Vector2 ZERO = new Vector2(0, 0);

	/**
	 * Represents a unit vector in the X direction (1,0)
	 */
	public static Vector2 UNIT_X = new Vector2(1, 0);

	/**
	 * Represents a unit vector in the Y direction (0,1)
	 */
	public static Vector2 UNTI_Y = new Vector2(0, 1);

	/**
	 * Represents a unit vector (1,1)
	 */
	public static Vector2 ONE = new Vector2(1, 1);

	protected double x, y;

	/**
	 * Construct and Initialized a Vector2 from the given x, y
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Construct and Initialized a Vector2 to (0,0)
	 */
	public Vector2() {
		this(0, 0);
	}

	/**
	 * Gets the X coordiante
	 *
	 * @return The X coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Gets the Y coordiante
	 *
	 * @return The Y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Adds this Vector2 to the value of the Vector2 argument
	 *
	 * @param that The Vector2 to add
	 * @return the new Vector2
	 */
	public Vector2 add(Vector2 that) {
		return Vector2.add(this, that);
	}

	/**
	 * Subtracts this Vector2 to the value of the Vector2 argument
	 *
	 * @param that The Vector2 to subtract
	 * @return the new Vector2
	 */
	public Vector2 subtract(Vector2 that) {
		return Vector2.subtract(this, that);
	}

	/**
	 * Scales this Vector2 by the value of the argument
	 *
	 * @param scale The amount to scale by
	 * @return A new Vector2 scaled by the amount.
	 */
	public Vector2 scale(double scale) {
		return Vector2.scale(this, scale);
	}

	/**
	 * Returns this Vector2 dot the Vector2 argument. Dot Product is defined as
	 * a.x*b.x + a.y*b.y
	 *
	 * @param that The Vector2 to dot with this.
	 * @return The dot product
	 */
	public double dot(Vector2 that) {
		return Vector2.dot(this, that);
	}

	/**
	 * Returns the Cross Product of this Vector2 Note: Cross Product is
	 * undefined in 2d space. This returns the orthogonal vector to this vector
	 *
	 * @return The orthogonal vector to this vector.
	 */
	public Vector2 cross() {
		return new Vector2(y, -x);
	}

	/**
	 * Calculates the length of this Vector2 squared.
	 *
	 * @return the squared length
	 */
	public double lengthSquared() {
		return Vector2.lengthSquared(this);
	}

	/**
	 * Calculates the length of this Vector2 Note: This makes use of the sqrt
	 * function, and is not cached. That could affect performance
	 *
	 * @return the length of this vector2
	 */
	public double length() {
		return Vector2.length(this);
	}

	/**
	 * Returns this Vector2 where the length is equal to 1
	 *
	 * @return This Vector2 with length 1
	 */
	public Vector2 normalize() {
		return Vector2.normalize(this);
	}

	/**
	 * Returns this Vector2 in an array. Element 0 contains x Element 1 contains
	 * y
	 *
	 * @return The array containing this Vector2
	 */
	public double[] toArray() {
		return Vector2.toArray(this);
	}

	/**
	 * Compares two Vector3s
	 */

	public int compareTo(Vector2 o) {
		return Vector2.compareTo(this, o);
	}

	/**
	 * Checks if two Vector2s are equal
	 */

	@Override
	public boolean equals(Object o) {
		return Vector2.equals(this, o);
	}

	/**
	 * Returns the length of the provided Vector2 Note: This makes use of the
	 * sqrt function, and is not cached. This could affect performance.
	 *
	 * @param a The Vector2 to calculate the length of
	 * @return The length of the Vector2
	 */
	public static double length(Vector2 a) {
		return MathHelper.sqrt(lengthSquared(a));
	}

	/**
	 * Returns the length squared of the provided Vector2
	 *
	 * @param a the Vector2 to calculate the length squared
	 * @return the length squared of the Vector2
	 */
	public static double lengthSquared(Vector2 a) {
		return Vector2.dot(a, a);
	}

	/**
	 * Returns a Vector2 that is the unit form of the provided Vector2
	 *
	 * @param a
	 * @return
	 */
	public static Vector2 normalize(Vector2 a) {
		return Vector2.scale(a, (1.f / a.length()));
	}

	/**
	 * Subtracts one Vector2 from the other Vector2
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2 subtract(Vector2 a, Vector2 b) {
		return new Vector2(a.getX() - b.getX(), a.getY() - b.getY());
	}

	/**
	 * Adds one Vector2 to the other Vector2
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2 add(Vector2 a, Vector2 b) {
		return new Vector2(a.getX() + b.getX(), a.getY() + b.getY());
	}

	/**
	 * Scales the Vector2 by the ammount
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2 scale(Vector2 a, double b) {
		return new Vector2(a.getX() * b, a.getY());
	}

	/**
	 * Calculates the Dot Product of two Vector2s Dot Product is defined as
	 * a.x*b.x + a.y*b.y
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static double dot(Vector2 a, Vector2 b) {
		return a.getX() * b.getX() + a.getY() * b.getY();
	}

	/**
	 * Returns the provided Vector2 in an array. Element 0 contains x Element 1
	 * contains y
	 *
	 * @return The array containing the Vector2
	 */
	public static double[] toArray(Vector2 a) {
		return new double[] {a.getX(), a.getY()};
	}

	/**
	 * Compares two Vector3s
	 */
	public static int compareTo(Vector2 a, Vector2 b) {
		return (int) a.lengthSquared() - (int) b.lengthSquared();
	}

	/**
	 * Checks if two Vector2s are equal
	 */
	public static boolean equals(Object a, Object b) {
		if (!(a instanceof Vector2) || !(b instanceof Vector2)) {
			return false;
		}
		if (a == b) {
			return true;
		}
		return compareTo((Vector2) a, (Vector2) b) == 0;
	}

}
