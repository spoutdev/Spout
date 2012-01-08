package org.spout.api.math;

/**
 * A 4-dimensional vector represented by float-precision x,y,z,w coordinates
 *
 * Note, this is the Immutable form of Vector4. All operations will construct a
 * new Vector4.
 */
public class Vector4 implements Comparable<Vector4> {

	/**
	 * Represents the Zero vector (0, 0, 0, 0)
	 */
	public final static Vector4 ZERO = new Vector4(0, 0, 0, 0);
	/**
	 * Represents a unit vector (1, 1, 1, 1)
	 */
	public final static Vector4 ONE = new Vector4(1, 1, 1, 1);
	/**
	 * Represents a unit vector in the X direction (1, 0, 0, 0)
	 */
	public final static Vector4 UNIT_X = new Vector4(1, 0, 0, 0);
	/**
	 * Represents a unit vector in the Y direction (0, 1, 0, 0)
	 */
	public final static Vector4 UNIT_Y = new Vector4(0, 1, 0, 0);
	/**
	 * Represents a unit vector in the Z direction (0, 0, 1, 0)
	 */
	public final static Vector4 UNIT_Z = new Vector4(0, 0, 1, 0);
	/**
	 * Represents a unit vector in the W direction (0, 0, 1, 1)
	 */
	public final static Vector4 UNIT_W = new Vector4(0, 0, 0, 1);
	
	protected float x;
	protected float y;
	protected float z;
	protected float w;

	/**
	 * Constructs and initializes a Vector4 from the given x, y, z, w
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param w the w coordinate
	 */
	public Vector4(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	/**
	 * Constructs and initializes a Vector4 from the given x, y, z, w
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param w the w coordinate
	 */
	public Vector4(double x, double y, double z, double w) {
		this((float) x, (float) y, (float) z, (float) w);
	}

	/**
	 * Constructs and initializes a Vector4 from the given x, y, z, w
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param w the w coordinate
	 */
	public Vector4(int x, int y, int z, int w) {
		this((float) x, (float) y, (float) z, (float) w);
	}

	/**
	 * Constructs and initializes a Vector4 from an old Vector4
	 * 
	 * @param o 
	 */
	public Vector4(Vector4 o) {
		this(o.x, o.y, o.z, o.w);
	}

	/**
	 * Constructs and initializes a Vector4 to (0,0)
	 */
	public Vector4() {
		this(0, 0, 0, 0);
	}

	/**
	 * Gets the X coordinate
	 *
	 * @return The X coordinate
	 */
	public float getX() {
		return x;
	}

	/**
	 * Gets the Y coordinate
	 *
	 * @return The Y coordinate
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * Gets the Z coordinate
	 *
	 * @return The Z coordinate
	 */
	public float getZ() {
		return x;
	}

	/**
	 * Gets the W coordinate
	 *
	 * @return The W coordinate
	 */
	public float getW() {
		return w;
	}

	/**
	 * Adds this Vector4 to the value of the Vector4 argument
	 *
	 * @param that The Vector4 to add
	 * @return the new Vector4
	 */
	public Vector4 add(Vector4 that) {
		return Vector4.add(this, that);
	}

	/**
	 * Adds a Vector4 comprised of the given x, y, z, w values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return 
	 */
	public Vector4 add(float x, float y, float z, float w) {
		return Vector4.add(this, new Vector4(x, y, z, w));
	}

	/**
	 * Adds a Vector4 comprised of the given x, y, z, w values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return 
	 */
	public Vector4 add(double x, double y, double z, double w) {
		return Vector4.add(this, new Vector4(x, y, z, w));
	}

	/**
	 * Adds a Vector4 comprised of the given x, y, z, w values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return 
	 */
	public Vector4 add(int x, int y, int z, int w) {
		return Vector4.add(this, new Vector4(x, y, z, w));
	}

	/**
	 * Subtracts this Vector4 to the value of the Vector4 argument
	 *
	 * @param that The Vector4 to subtract
	 * @return the new Vector4
	 */
	public Vector4 subtract(Vector4 that) {
		return Vector4.subtract(this, that);
	}

	/**
	 * Subtracts a Vector4 comprised of the given x, y, z, w values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return 
	 */
	public Vector4 subtract(float x, float y, float z, float w) {
		return Vector4.subtract(this, new Vector4(x, y, z, w));
	}

	/**
	 * Subtracts a Vector4 comprised of the given x, y, z, w values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return 
	 */
	public Vector4 subtract(double x, double y, double z, double w) {
		return Vector4.subtract(this, new Vector4(x, y, z, w));
	}

	/**
	 * Subtracts a Vector4 comprised of the given x, y, z, w values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return 
	 */
	public Vector4 subtract(int x, int y, int z, int w) {
		return Vector4.subtract(this, new Vector4(x, y, z, w));
	}

	/**
	 * Multiplies this Vector4 to the value of the Vector4 argument
	 *
	 * @param that The Vector4 to multiply
	 * @return the new Vector4
	 */
	public Vector4 multiply(Vector4 that) {
		return Vector4.multiply(this, that);
	}

	/**
	 * Multiplies a Vector4 comprised of the given x, y, z, w values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return 
	 */
	public Vector4 multiply(float x, float y, float z, float w) {
		return Vector4.multiply(this, new Vector4(x, y, z, w));
	}

	/**
	 * Multiplies a Vector4 comprised of the given x, y, z, w values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return 
	 */
	public Vector4 multiply(double x, double y, double z, double w) {
		return Vector4.multiply(this, new Vector4(x, y, z, w));
	}

	/**
	 * Multiplies a Vector4 comprised of the given x, y, z, w values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return 
	 */
	public Vector4 multiply(int x, int y, int z, int w) {
		return Vector4.multiply(this, new Vector4(x, y, z, w));
	}

	/**
	 * Multiplies a Vector4 by the given value
	 * 
	 * @param val
	 * @return 
	 */
	public Vector4 multiply(float val) {
		return Vector4.multiply(this, new Vector4(val, val, val, val));
	}

	/**
	 * Multiplies a Vector4 by the given value
	 * 
	 * @param val
	 * @return 
	 */
	public Vector4 multiply(double val) {
		return Vector4.multiply(this, new Vector4(val, val, val, val));
	}

	/**
	 * Multiplies a Vector4 by the given value
	 * 
	 * @param val
	 * @return 
	 */
	public Vector4 multiply(int val) {
		return Vector4.multiply(this, new Vector4(val, val, val, val));
	}

	/**
	 * Divides the given Vector4 from this Vector4
	 *
	 * @param that The Vector4 to divide
	 * @return the new Vector4
	 */
	public Vector4 divide(Vector4 that) {
		return Vector4.divide(this, that);
	}

	/**
	 * Divides a Vector4 comprised of the given x, y, z, w values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return 
	 */
	public Vector4 divide(float x, float y, float z, float w) {
		return Vector4.divide(this, new Vector4(x, y, z, w));
	}

	/**
	 * Divides a Vector4 comprised of the given x, y, z, w values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return 
	 */
	public Vector4 divide(double x, double y, double z, double w) {
		return Vector4.divide(this, new Vector4(x, y, z, w));
	}

	/**
	 * Divides a Vector4 comprised of the given x, y, z, w values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return 
	 */
	public Vector4 divide(int x, int y, int z, int w) {
		return Vector4.divide(this, new Vector4(x, y, z, w));
	}

	/**
	 * Divides a Vector4 by the given value
	 * 
	 * @param val
	 * @return 
	 */
	public Vector4 divide(float val) {
		return Vector4.divide(this, new Vector4(val, val, val, val));
	}

	/**
	 * Divides a Vector4 by the given value
	 * 
	 * @param val
	 * @return 
	 */
	public Vector4 divide(double val) {
		return Vector4.divide(this, new Vector4(val, val, val, val));
	}

	/**
	 * Divides a Vector4 by the given value
	 * 
	 * @param val
	 * @return 
	 */
	public Vector4 divide(int val) {
		return Vector4.divide(this, new Vector4(val, val, val, val));
	}

	/**
	 * Returns this Vector4 dot the Vector4 argument. Dot Product is defined as
	 * a.x*b.x + a.y*b.y
	 *
	 * @param that The Vector4 to dot with this.
	 * @return The dot product
	 */
	public float dot(Vector4 that) {
		return Vector4.dot(this, that);
	}

	/**
	 * Returns a Vector3 object with the x, y, z values
	 * from this Vector4 object.
	 * 
	 * @return 
	 */
	public Vector3 toVector3() {
		return Vector4.toVector3(this);
	}

	/**
	 * Returns a Vector3m object with the x, y, z values
	 * from this Vector4 object.
	 * 
	 * @return 
	 */
	public Vector3m toVector3m() {
		return Vector4.toVector3m(this);
	}
	
	/**
	 * Returns a Vector2 object with the x, y values
	 * from this Vector4 object.
	 * 
	 * @return 
	 */
	public Vector2 toVector2() {
		return Vector4.toVector2(this);
	}

	/**
	 * Returns a Vector2m object with the x, y values
	 * from this Vector4 object.
	 * 
	 * @return 
	 */
	public Vector2m toVector2m() {
		return Vector4.toVector2m(this);
	}

	/**
	 * Rounds the values of this Vector4 up to 
	 * the nearest integer value. 
	 * 
	 * @return 
	 */
	public Vector4 ceil() {
		return Vector4.ceil(this);
	}

	/**
	 * Rounds the values of this Vector4 down to 
	 * the nearest integer value. 
	 * 
	 * @return 
	 */
	public Vector4 floor() {
		return Vector4.floor(this);
	}

	/**
	 * Rounds the values of this Vector4 to 
	 * the nearest integer value. 
	 * 
	 * @return 
	 */
	public Vector4 round() {
		return Vector4.round(this);
	}

	/**
	 * Sets the values of this Vector4 to their
	 * absolute value.
	 * 
	 * @return 
	 */
	public Vector4 abs() {
		return Vector4.abs(this);
	}

	/**
	 * Gets the distance between this Vector4 and a given Vector4.
	 * 
	 * @param a
	 * @return 
	 */
	public double distance(Vector4 a) {
		return Vector4.distance(a, this);
	}

	/**
	 * Raises the values of this Vector4 to the given power.
	 * 
	 * @param power
	 * @return 
	 */
	public Vector4 pow(double power) {
		return Vector4.pow(this, power);
	}

	/**
	 * Calculates the length of this Vector4 squared.
	 *
	 * @return the squared length
	 */
	public float lengthSquared() {
		return Vector4.lengthSquared(this);
	}

	/**
	 * Calculates the length of this Vector4 Note: This makes use of the sqrt
	 * function, and is not cached. That could affect performance
	 *
	 * @return the length of this Vector4
	 */
	public float length() {
		return Vector4.length(this);
	}

	/**
	 * Returns this Vector4 where the length is equal to 1
	 *
	 * @return This Vector4 with length 1
	 */
	public Vector4 normalize() {
		return Vector4.normalize(this);
	}

	/**
	 * Returns this Vector4 in an array. Element 0 contains x Element 1 contains
	 * y
	 *
	 * @return The array containing this Vector4
	 */
	public float[] toArray() {
		return Vector4.toArray(this);
	}

	/**
	 * Compares two Vector3s
	 */
	public int compareTo(Vector4 o) {
		return Vector4.compareTo(this, o);
	}

	/**
	 * Checks if two Vector4s are equal
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Vector4)) {
			return false;
		}
		if (this == o) {
			return true;
		}
		return compareTo(this, (Vector4) o) == 0;
	}

	/**
	 * Generates a hashcode for this Vector
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 53 * hash + Float.floatToIntBits(this.x);
		hash = 53 * hash + Float.floatToIntBits(this.y);
		hash = 53 * hash + Float.floatToIntBits(this.z);
		hash = 53 * hash + Float.floatToIntBits(this.w);
		return hash;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + w + ")";
	}

	/**
	 * Returns the length of the provided Vector4 Note: This makes use of the
	 * sqrt function, and is not cached. This could affect performance.
	 *
	 * @param a The Vector4 to calculate the length of
	 * @return The length of the Vector4
	 */
	public static float length(Vector4 a) {
		return (float) Math.sqrt(lengthSquared(a));
	}

	/**
	 * Returns the length squared of the provided Vector4
	 *
	 * @param a the Vector4 to calculate the length squared
	 * @return the length squared of the Vector4
	 */
	public static float lengthSquared(Vector4 a) {
		return Vector4.dot(a, a);
	}

	/**
	 * Returns a Vector4 that is the unit form of the provided Vector4
	 *
	 * @param a
	 * @return
	 */
	public static Vector4 normalize(Vector4 a) {
		return a.multiply(1.f / a.length());
	}

	/**
	 * Adds one Vector4 to the other Vector4
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector4 add(Vector4 a, Vector4 b) {
		return new Vector4(a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
	}
	
	/**
	 * Subtracts one Vector4 from the other Vector4
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector4 subtract(Vector4 a, Vector4 b) {
		return new Vector4(a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
	}

	/**
	 * Multiplies one Vector4 by the other Vector4
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector4 multiply(Vector4 a, Vector4 b) {
		return new Vector4(a.x * b.x, a.y * b.y, a.z * b.z, a.w * b.w);
	}

	/**
	 * Divides one Vector4 by the other Vector4
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector4 divide(Vector4 a, Vector4 b) {
		return new Vector4(a.x / b.x, a.y / b.y, a.z / b.z, a.w / b.w);
	}

	/**
	 * Calculates the Dot Product of two Vector4s Dot Product is defined as
	 * a.x*b.x + a.y*b.y
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static float dot(Vector4 a, Vector4 b) {
		return a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;
	}

	/**
	 * Returns a Vector3 object with the x, y, z values
	 * from this Vector4 object.
	 * 
	 * @return 
	 */
	public static Vector3 toVector3(Vector4 o) {
		return new Vector3(o.x, o.y, o.z);
	}

	/**
	 * Returns a Vector3m object with the x, y, z values
	 * from this Vector4 object.
	 * 
	 * @return 
	 */
	public static Vector3m toVector3m(Vector4 o) {
		return new Vector3m(o.x, o.y, o.z);
	}
	
	/**
	 * Returns a Vector2 object with the x, y values
	 * from this Vector4 object.
	 * 
	 * @return 
	 */
	public static Vector2 toVector2(Vector4 o) {
		return new Vector2(o.x, o.y);
	}

	/**
	 * Returns a Vector2m object with the x, y values
	 * from this Vector4 object.
	 * 
	 * @return 
	 */
	public static Vector2m toVector2m(Vector4 o) {
		return new Vector2m(o.x, o.y);
	}

	/**
	 * Rounds the values of the given Vector4 up to 
	 * the nearest integer value. 
	 * 
	 * @param o Vector4 to use
	 * @return 
	 */
	public static Vector4 ceil(Vector4 o) {
		return new Vector4(Math.ceil(o.x), Math.ceil(o.y), Math.ceil(o.z), Math.ceil(o.w));
	}

	/**
	 * Rounds the values of the given Vector4 down to 
	 * the nearest integer value. 
	 * 
	 * @param o Vector4 to use
	 * @return 
	 */
	public static Vector4 floor(Vector4 o) {
		return new Vector4(Math.floor(o.x), Math.floor(o.y), Math.floor(o.z), Math.floor(o.w));
	}

	/**
	 * Rounds the values of the given Vector4 to 
	 * the nearest integer value. 
	 * 
	 * @param o Vector4 to use
	 * @return 
	 */
	public static Vector4 round(Vector4 o) {
		return new Vector4(Math.round(o.x), Math.round(o.y), Math.round(o.z), Math.round(o.w));
	}

	/**
	 * Sets the values of the given Vector4 to their
	 * absolute value.
	 * 
	 * @param o Vector4 to use
	 * @return 
	 */
	public static Vector4 abs(Vector4 o) {
		return new Vector4(Math.abs(o.x), Math.abs(o.y), Math.abs(o.z), Math.abs(o.w));
	}

	/**
	 * Returns a Vector4 containing the smallest values.
	 * 
	 * @param o1
	 * @param o2
	 * @return 
	 */
	public static Vector4 min(Vector4 o1, Vector4 o2) {
		return new Vector4(Math.min(o1.x, o2.x), Math.min(o1.y, o2.y), Math.min(o1.z, o2.z), Math.min(o1.w, o2.w));
	}

	/**
	 * Returns a Vector4 containing the largest values.
	 * 
	 * @param o1
	 * @param o2
	 * @return 
	 */
	public static Vector4 max(Vector4 o1, Vector4 o2) {
		return new Vector4(Math.max(o1.x, o2.x), Math.max(o1.y, o2.y), Math.max(o1.z, o2.z), Math.max(o1.w, o2.w));
	}

	/**
	 * Returns a Vector4 with random values (between 0 and 1)
	 * 
	 * @param o
	 * @return 
	 */
	public static Vector4 rand() {
		return new Vector4(Math.random(), Math.random(), Math.random(), Math.random());
	}

	/**
	 * Returns the provided Vector4 in an array.
	 *
	 * @return The array containing the Vector4
	 */
	public static float[] toArray(Vector4 a) {
		return new float[]{a.x, a.y, a.z, a.w};
	}

	/**
	 * Compares two Vector3s
	 */
	public static int compareTo(Vector4 a, Vector4 b) {
		return (int) a.lengthSquared() - (int) b.lengthSquared();
	}

	/**
	 * Gets the distance between two Vector4. 
	 * 
	 * @param a
	 * @param b
	 * @return 
	 */
	public static double distance(Vector4 a, Vector4 b) {
		double xyzDist = Vector3.distance(a.toVector3(), b.toVector3());
		return Math.sqrt(Math.pow(xyzDist, 2) + Math.pow(Vector4.subtract(a, b).getW(), 2));
	}

	/**
	 * Raises the values of a Vector4 to the given power.
	 * 
	 * @param o
	 * @param power
	 * @return 
	 */
	public static Vector4 pow(Vector4 o, double power) {
		return new Vector4(Math.pow(o.x, power), Math.pow(o.y, power), Math.pow(o.z, power), Math.pow(o.w, power));
	}

	/**
	 * Checks if two Vector4s are equal
	 */
	public static boolean equals(Object a, Object b) {
		return a.equals(b);
	}
}
