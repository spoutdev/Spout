package org.getspout.api.math;

/**
 * A 2-dimensional vector represented by float-precision x,y coordinates
 *
 * Note, this is the Immutable form of Vector2. All operations will construct a
 * new Vector2.
 */
public class Vector2 implements Comparable<Vector2> {
	/**
	 * Represents the Zero vector (0,0)
	 */
	public final static Vector2 ZERO = new Vector2(0, 0);

	/**
	 * Represents a unit vector in the X direction (1,0)
	 */
	public final static Vector2 UNIT_X = new Vector2(1, 0);

	/**
	 * Represents a unit vector in the Y direction (0,1)
	 */
	public final static Vector2 UNTI_Y = new Vector2(0, 1);

	/**
	 * Represents a unit vector (1,1)
	 */
	public static Vector2 ONE = new Vector2(1, 1);

	protected float x, y;

	/**
	 * Construct and Initialized a Vector2 from the given x, y
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Construct and Initialized a Vector2 from the given x, y
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Vector2(Double x, Double y) {
		this(x.floatValue(), y.floatValue());
	}

	/**
	 * Construct and Initialized a Vector2 to (0,0)
	 */
	public Vector2() {
		this(0, 0);
	}
	
	/**
	 * Construct and Initialized a Vector2 from an old Vector2
	 * 
	 * @param original 
	 */
	public Vector2(Vector2 original) {
		this(original.x, original.y);
	}

	/**
	 * Gets the X coordiante
	 *
	 * @return The X coordinate
	 */
	public float getX() {
		return x;
	}

	/**
	 * Gets the Y coordiante
	 *
	 * @return The Y coordinate
	 */
	public float getY() {
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
	public Vector2 scale(float scale) {
		return Vector2.scale(this, scale);
	}

	/**
	 * Returns this Vector2 dot the Vector2 argument. Dot Product is defined as
	 * a.x*b.x + a.y*b.y
	 *
	 * @param that The Vector2 to dot with this.
	 * @return The dot product
	 */
	public float dot(Vector2 that) {
		return Vector2.dot(this, that);
	}
	
	/**
	 * Returns a Vector3 object with a y-value of 0.
	 * The x of this Vector2 becomes the x of the Vector3,
	 * the y of this Vector2 becomes the z of the Vector3.
	 * 
	 * @return 
	 */
	public Vector3 toVector3() {
		return Vector2.toVector3(this);
	}
	
	/**
	 * Returns a Vector3m object with a y-value of 0.
	 * The x of this Vector2 becomes the x of the Vector3m,
	 * the y of this Vector2 becomes the z of the Vector3m.
	 * 
	 * @return 
	 */
	public Vector3m toVector3m() {
		return Vector2.toVector3m(this);
	}
	
	/**
	 * Returns a Vector3 object with the given y value.
	 * The x of this Vector2 becomes the x of the Vector3,
	 * the y of this Vector2 becomes the z of the Vector3.
	 * 
	 * @param y Y value to use in the new Vector3.
	 * @return 
	 */
	public Vector3 toVector3(float y) {
		return Vector2.toVector3(this, y);
	}
	
	/**
	 * Returns a Vector3m object with the given y value.
	 * The x of this Vector2 becomes the x of the Vector3m,
	 * the y of this Vector2 becomes the z of the Vector3m.
	 * 
	 * @param y Y value to use in the new Vector3m.
	 * @return 
	 */
	public Vector3m toVector3m(float y) {
		return Vector2.toVector3m(this, y);
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
	 * Rounds the X and Y values of this Vector2 up to 
	 * the nearest integer value. 
	 * 
	 * @return 
	 */
	public Vector2 ceil() {
		return new Vector2(Math.ceil(x), Math.ceil(y));
	}
	
	/**
	 * Rounds the X and Y values of this Vector2 down to 
	 * the nearest integer value. 
	 * 
	 * @return 
	 */
	public Vector2 floor() {
		return new Vector2(Math.floor(x), Math.floor(y));
	}
	
	/**
	 * Rounds the X and Y values of this Vector2 to 
	 * the nearest integer value. 
	 * 
	 * @return 
	 */
	public Vector2 round() {
		return new Vector2(Math.round(x), Math.round(y));
	}
	
	/**
	 * Sets the X and Y values of this Vector2 to their
	 * absolute value.
	 * 
	 * @return 
	 */
	public Vector2 abs() {
		return new Vector2(Math.abs(x), Math.abs(y));
	}

	/**
	 * Calculates the length of this Vector2 squared.
	 *
	 * @return the squared length
	 */
	public float lengthSquared() {
		return Vector2.lengthSquared(this);
	}

	/**
	 * Calculates the length of this Vector2 Note: This makes use of the sqrt
	 * function, and is not cached. That could affect performance
	 *
	 * @return the length of this vector2
	 */
	public float length() {
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
	public float[] toArray() {
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
	public static float length(Vector2 a) {
		return (float)MathHelper.sqrt(lengthSquared(a));
	}

	/**
	 * Returns the length squared of the provided Vector2
	 *
	 * @param a the Vector2 to calculate the length squared
	 * @return the length squared of the Vector2
	 */
	public static float lengthSquared(Vector2 a) {
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
	public static Vector2 scale(Vector2 a, float b) {
		return new Vector2(a.getX() * b, a.getY() * b);
	}

	/**
	 * Calculates the Dot Product of two Vector2s Dot Product is defined as
	 * a.x*b.x + a.y*b.y
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static float dot(Vector2 a, Vector2 b) {
		return a.getX() * b.getX() + a.getY() * b.getY();
	}
	
	/**
	 * Returns a Vector3 object with a y-value of 0.
	 * The x of the Vector2 becomes the x of the Vector3,
	 * the y of the Vector2 becomes the z of the Vector3.
	 * 
	 * @param o Vector2 to use as the x/z values
	 * @return 
	 */
	public static Vector3 toVector3(Vector2 o) {
		return new Vector3(o.x, 0, o.y);
	}
	
	/**
	 * Returns a Vector3m object with a y-value of 0.
	 * The x of the Vector2 becomes the x of the Vector3m,
	 * the y of the Vector2 becomes the z of the Vector3m.
	 * 
	 * @param o Vector2 to use as the x/z values
	 * @return 
	 */
	public static Vector3m toVector3m(Vector2 o) {
		return new Vector3m(o.x, 0, o.y);
	}
	
	/**
	 * Returns a Vector3 object with the given y-value.
	 * The x of the Vector2 becomes the x of the Vector3,
	 * the y of the Vector2 becomes the z of the Vector3.
	 * 
	 * @param o Vector2 to use as the x/z values
	 * @param y Y value of the new Vector3
	 * @return 
	 */
	public static Vector3 toVector3(Vector2 o, float y) {
		return new Vector3(o.x, y, o.y);
	}
	
	/**
	 * Returns a Vector3m object with the given y-value.
	 * The x of the Vector2 becomes the x of the Vector3m,
	 * the y of the Vector2 becomes the z of the Vector3m.
	 * 
	 * @param o Vector2 to use as the x/z values
	 * @param y Y value of the new Vector3
	 * @return 
	 */
	public static Vector3m toVector3m(Vector2 o, float y) {
		return new Vector3m(o.x, y, o.y);
	}
	
	/**
	 * Rounds the X and Y values of the given Vector2 up to 
	 * the nearest integer value. 
	 * 
	 * @param o Vector2 to use
	 * @return 
	 */
	public static Vector2 ceil(Vector2 o) {
		return new Vector2(Math.ceil(o.x), Math.ceil(o.y));
	}
	
	/**
	 * Rounds the X and Y values of the given Vector2 down to 
	 * the nearest integer value. 
	 * 
	 * @param o Vector2 to use
	 * @return 
	 */
	public static Vector2 floor(Vector2 o) {
		return new Vector2(Math.floor(o.x), Math.floor(o.y));
	}
	
	/**
	 * Rounds the X and Y values of the given Vector2 to 
	 * the nearest integer value. 
	 * 
	 * @param o Vector2 to use
	 * @return 
	 */
	public static Vector2 round(Vector2 o) {
		return new Vector2(Math.round(o.x), Math.round(o.y));
	}
	
	/**
	 * Sets the X and Y values of the given Vector2 to their
	 * absolute value.
	 * 
	 * @param o Vector2 to use
	 * @return 
	 */
	public static Vector2 abs(Vector2 o) {
		return new Vector2(Math.abs(o.x), Math.abs(o.y));
	}
	
	/**
	 * Returns a Vector2 containing the smallest X and Y values.
	 * 
	 * @param o1
	 * @param o2
	 * @return 
	 */
	public static Vector2 min(Vector2 o1, Vector2 o2) {
		return new Vector2(Math.min(o1.x, o2.x), Math.min(o1.y, o2.y));
	}
	
	/**
	 * Returns a Vector2 containing the largest X and Y values.
	 * 
	 * @param o1
	 * @param o2
	 * @return 
	 */
	public static Vector2 max(Vector2 o1, Vector2 o2) {
		return new Vector2(Math.max(o1.x, o2.x), Math.max(o1.y, o2.y));
	}
	
	/**
	 * Returns a Vector2 with random X and Y values (between 0 and 1)
	 * 
	 * @param o
	 * @return 
	 */
	public static Vector2 rand() {
		return new Vector2(Math.random(), Math.random());
	}

	/**
	 * Returns the provided Vector2 in an array. Element 0 contains x Element 1
	 * contains y
	 *
	 * @return The array containing the Vector2
	 */
	public static float[] toArray(Vector2 a) {
		return new float[] {a.getX(), a.getY()};
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

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

}
