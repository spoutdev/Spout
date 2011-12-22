package org.getspout.api.math;

/**
 * Represents a 3d vector.
 */
public class Vector3 implements Comparable<Vector3> {
	/**
	 * Vector with all elements set to 0. (0, 0, 0)
	 */
	public static Vector3 ZERO = new Vector3(0, 0, 0);
	/**
	 * Unit Vector in the X direction. (1, 0, 0)
	 */
	public static Vector3 UNIT_X = new Vector3(1, 0, 0);
	/**
	 * Unit Vector facing Forward. (1, 0, 0)
	 */
	public static Vector3 Forward = UNIT_X;
	/**
	 * Unit Vector in the Y direction. (0, 1, 0)
	 */
	public static Vector3 UNIT_Y = new Vector3(0, 1, 0);
	/**
	 * Unit Vector pointing Up. (0, 1, 0)
	 */
	public static Vector3 Up = UNIT_Y;
	/**
	 * Unit Vector in the Z direction. (0, 0, 1)
	 */
	public static Vector3 UNIT_Z = new Vector3(0, 0, 1);
	/**
	 * Unit Vector pointing Right. (0, 0, 1)
	 */
	public static Vector3 Right = UNIT_Z;
	/**
	 * Unit Vector with all elements set to 1. (1, 1, 1)
	 */
	public static Vector3 ONE = new Vector3(1, 1, 1);

	protected float x, y, z;

	/**
	 * Constructs a new Vector3 with the given x, y, z
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Constructs a new Vector3 with all elements set to 0
	 */
	public Vector3() {
		this(0, 0, 0);
	}

	/**
	 * Constructs a new Vector3 that is a clone of the given vector3
	 *
	 * @param clone
	 */
	public Vector3(Vector3 clone) {
		this(clone.getX(), clone.getY(), clone.getZ());
	}

	/**
	 * Constructs a new Vector3 from the given Vector2 and z
	 *
	 * @param vector
	 * @param z
	 */
	public Vector3(Vector2 vector, float z) {
		this(vector.getX(), vector.getY(), z);
	}

	/**
	 * Constructs a new Vector3 from the given Vector2 and z set to 0
	 *
	 * @param vector
	 */
	public Vector3(Vector2 vector) {
		this(vector, 0);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	/**
	 * Adds two vectors
	 *
	 * @param that
	 * @return
	 */
	public Vector3 add(Vector3 that) {
		return Vector3.add(this, that);
	}

	/**
	 * Subtracts two vectors
	 *
	 * @param that
	 * @return
	 */
	public Vector3 subtract(Vector3 that) {
		return Vector3.subtract(this, that);
	}

	/**
	 * Scales by the scalar value
	 *
	 * @param scale
	 * @return
	 */
	public Vector3 scale(float scale) {
		return Vector3.scale(this, scale);
	}

	/**
	 * Takes the dot product of two vectors
	 *
	 * @param that
	 * @return
	 */
	public float dot(Vector3 that) {
		return Vector3.dot(this, that);
	}

	/**
	 * Takes the cross product of two vectors
	 *
	 * @param that
	 * @return
	 */
	public Vector3 cross(Vector3 that) {
		return Vector3.cross(this, that);
	}

	/**
	 * returns the squared length of the vector
	 *
	 * @return
	 */
	public float lengthSquared() {
		return Vector3.lengthSquared(this);
	}

	/**
	 * returns the length of this vector. Note: makes use of Math.sqrt and is
	 * not cached.
	 *
	 * @return
	 */
	public float length() {
		return Vector3.length(this);
	}

	/**
	 * returns the vector with a length of 1
	 *
	 * @return
	 */
	public Vector3 normalize() {
		return Vector3.normalize(this);
	}

	/**
	 * returns the vector as [x,y,z]
	 *
	 * @return
	 */
	public float[] toArray() {
		return Vector3.toArray(this);
	}

	/**
	 * Returns a new vector that is a transformation of this vector around the
	 * given transformation
	 *
	 * @param transformation
	 * @return
	 */
	public Vector3 transform(Matrix transformation) {
		return Vector3.transform(this, transformation);
	}
	public Vector3 transform(Quaternion transformation){
		return Vector3.transform(this, transformation);
	}
	/**
	 * Compares two Vector3s
	 */

	public int compareTo(Vector3 o) {
		return Vector3.compareTo(this, o);
	}

	/**
	 * Checks if two Vector3s are equal
	 */

	
	public boolean equals(Object o) {
		return Vector3.equals(this, o);
	}
	
	/**
	 * toString Override
	 */
	public String toString(){
		return String.format("{ %f, %f, %f",x,y,z);
	}

	/**
	 * Returns the length of the given vector Note: Makes use of Math.sqrt and
	 * is not cached, so can be slow
	 *
	 * @param a
	 * @return
	 */
	public static float length(Vector3 a) {
		return (float)Math.sqrt(lengthSquared(a));
	}

	/**
	 * returns the length squared to the given vector
	 *
	 * @param a
	 * @return
	 */
	public static float lengthSquared(Vector3 a) {
		return Vector3.dot(a, a);
	}

	/**
	 * Returns a new vector that is the given vector but length 1
	 *
	 * @param a
	 * @return
	 */
	public static Vector3 normalize(Vector3 a) {
		return Vector3.scale(a, (1.f / a.length()));
	}

	/**
	 * Creates a new vector that is A - B
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 subtract(Vector3 a, Vector3 b) {
		return new Vector3(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
	}

	/**
	 * Creates a new Vector that is A + B
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 add(Vector3 a, Vector3 b) {
		return new Vector3(a.getX() + b.getX(), a.getY() + b.getY(), a.getZ() + b.getZ());
	}

	/**
	 * Creates a new vector that is A multiplied by the uniform scalar B
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 scale(Vector3 a, float b) {
		return new Vector3(a.getX() * b, a.getY() * b, a.getZ() * b);
	}

	/**
	 * Returns the dot product of A and B
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static float dot(Vector3 a, Vector3 b) {
		return a.getX() * b.getX() + a.getY() * b.getY() + a.getZ() * b.getZ();
	}

	/**
	 * Creates a new Vector that is the A x B The Cross Product is the vector
	 * orthogonal to both A and B
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector3 cross(Vector3 a, Vector3 b) {
		return new Vector3(a.getY() * b.getZ() - a.getZ() * b.getY(), a.getZ() * b.getX() - a.getX() * b.getZ(), a.getX() * b.getY() - a.getY() * b.getX());
	}

	/**
	 * Returns a new float array that is {x, y, z}
	 *
	 * @param a
	 * @return
	 */
	public static float[] toArray(Vector3 a) {
		return new float[] {a.getX(), a.getY(), a.getZ()};
	}

	/**
	 * Calculates and returns a new Vector3 transformed by the transformation
	 * matrix
	 *
	 * @param vector the vector to transform
	 * @param transformation the transformation matrix
	 * @return
	 */
	public static Vector3 transform(Vector3 vector, Matrix transformation) {
		//TODO rewrite this
		
		return new Vector3(0,0, 0);
	}
	/**
	 * Calculates and returns a new Vector3 transformed by the given quaternion
	 * @param vector
	 * @param rot
	 * @return
	 */
	public static Vector3 transform(Vector3 vector, Quaternion rot){
		return Vector3.transform(vector, Matrix.rotate(rot));
	}
	

	/**
	 * Compares two Vector3s
	 */
	public static int compareTo(Vector3 a, Vector3 b) {
		return (int) a.lengthSquared() - (int) b.lengthSquared();
	}

	/**
	 * Checks if two Vector2s are equal
	 */
	public static boolean equals(Object a, Object b) {
		if (!(a instanceof Vector3) || !(b instanceof Vector3)) {
			return false;
		}
		if (a == b) {
			return true;
		}
		return compareTo((Vector3) a, (Vector3) b) == 0;
	}

}
