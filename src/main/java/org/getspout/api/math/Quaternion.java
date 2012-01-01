package org.getspout.api.math;

/**
 * Represents a rotation around a unit 4d circle. 
 * 
 * 
 */
public class Quaternion {
	double x,y,z,w;
	
	/**
	 * Represents no rotation
	 */
	public static Quaternion identitiy = new Quaternion(1,0,0,0);
	
	/**
	 * Constructs a new Quaternion with the given xyzw 
	 * NOTE: This represents a Unit Vector in 4d space.  Do not use unless you know what you are doing.
	 * If you want to create a normal rotation, use the angle/axis override.
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public Quaternion(double x, double y, double z, double w){
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	/**
	 * Constructs a new Quaternion that represents a given rotation around an arbatrary axis
	 * @param angle Angle, in Degrees, to rotate the axis about by
	 * @param axis
	 */
	public Quaternion(double angle, Vector3 axis){
		this(axis.getX() * MathHelper.sin(Math.toRadians(angle/2)), axis.getY() * MathHelper.sin(Math.toRadians(angle/2)), axis.getZ() * MathHelper.sin(Math.toRadians(angle/2)), MathHelper.cos(Math.toRadians(angle/2)));
	}
	/**
	 *  Copy Constructor
	 */
	public Quaternion(Quaternion rotation) {
		this(rotation.x, rotation.y, rotation.z, rotation.w);
	}
	/**
	 * Returns the X component of the quaternion
	 * @return
	 */
	public double getX() {
		return x;
	}
	/**
	 * Returns the Y component of the quaternion
	 * @return
	 */
	public double getY() {
		return y;
	}
	/**
	 * Returns the Z component of the quaternion
	 * @return
	 */
	public double getZ() {
		return z;
	}
	/**
	 * Returns the W component of the quaternion
	 * @return
	 */
	public double getW() {
		return w;
	}
	/**
	 * Returns the length squared of the quaternion
	 * @return
	 */
	public double lengthSquared(){
		return Quaternion.lengthSquared(this);
	}
	/**
	 * Returns the length of the quaternion. 
	 * Note: This uses square root, so is slowish
	 * @return
	 */
	public double length(){
		return Quaternion.length(this);
	}
	/**
	 * Returns this quaternion but length() == 1
	 * @return
	 */
	public Quaternion normalize(){
		return Quaternion.normalize(this);
	}
	/**
	 * Multiplies this Quaternion by the other Quaternion
	 * @param o
	 * @return
	 */
	public Quaternion multiply(Quaternion o){
		return Quaternion.multiply(this, o);
	}
	/**
	 * Creates and returns a new Quaternion that represnets this quaternion rotated by the given Axis and Angle
	 * @param angle
	 * @param axis
	 * @return
	 */
	public Quaternion rotate(double angle, Vector3 axis){
		return Quaternion.rotate(this, angle, axis);
	}
	/**
	 * Returns the angles about each axis of this quaternion stored in a Vector3
	 * 
	 * vect.X = Rotation about the X axis (Roll)
	 * vect.Y = Rotation about the Y axis (Yaw)
	 * vect.Z = Rotation about the Z axis (Pitch)
	 * 
	 * @param a
	 * @return
	 */
	public Vector3 getAxisAngles(){
		return Quaternion.getAxisAngles(this);
	}
	
	/**
	 * Returns the length squared of the given Quaternion
	 * @param a
	 * @return
	 */
	public static double lengthSquared(Quaternion a){
		return (a.x*a.x + a.y*a.y + a.z*a.z + a.w*a.w);
	}
	/**
	 * Returns the length of the given Quaternion
	 * Note: Uses sqrt, so is slowish.
	 * @param a
	 * @return
	 */
	public static double length(Quaternion a){
		return MathHelper.sqrt(lengthSquared(a));
	}
	/**
	 * Constructs and returns a new Quaternion that is the given Quaternion but length() == 1
	 * @param a
	 * @return
	 */
	public static Quaternion normalize(Quaternion a){
		double length = length(a);
		return new Quaternion(a.x / length, a.y / length, a.z / length, a.w / length);
	}
	/**
	 * Constructs and returns a new Quaternion that is A * B
	 * @param a
	 * @param b
	 * @return
	 */
	public static Quaternion multiply(Quaternion a, Quaternion b){
		double x = a.w * b.x + a.x * b.w + a.y * b.z + a.z * b.y;
		double y = a.w * b.y - a.x * b.z + a.y * b.w + a.z * b.x;
		double z = a.w * b.z + a.x * b.y - a.y * b.x + a.z * b.w;
		double w = a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z;
		
		return new Quaternion(x,y,z,w);
	}
	/**
	 * Constructs and returns a new Quaternion that is A rotated about the axis and angle
	 * @param a
	 * @param angle
	 * @param axis
	 * @return
	 */
	public static Quaternion rotate(Quaternion a, double angle, Vector3 axis){
		return multiply(new Quaternion(angle, axis), a);
	}
	/**
	 * Returns the angles about each axis of this quaternion stored in a Vector3
	 * 
	 * vect.X = Rotation about the X axis (Roll)
	 * vect.Y = Rotation about the Y axis (Yaw)
	 * vect.Z = Rotation about the Z axis (Pitch)
	 * 
	 * @param a
	 * @return
	 */
	public static Vector3 getAxisAngles(Quaternion a){
		float roll = (float)Math.atan2(2 * (a.getX() * a.getY() + a.getZ() * a.getW()), 1 - 2 * (a.getY() * a.getY() + a.getZ() * a.getZ()));
		float pitch = (float)Math.asin(2 * (a.getX() * a.getZ() - a.getW() * a.getY()));
		float yaw = (float)Math.atan2(2 * ( a.getX() * a.getW() + a.getY() * a.getZ()), 1 - 2 * (a.getZ() * a.getZ() + a.getW() * a.getW()));
		
		return new Vector3(roll, pitch, yaw);
	}
	
	
	
}
