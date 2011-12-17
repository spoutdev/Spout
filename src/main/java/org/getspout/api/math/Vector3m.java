package org.getspout.api.math;

public class Vector3m extends Vector3 {

	public Vector3m(double x, double y, double z) {
		super(x, y, z);
	}
	public Vector3m(Vector3 vector) {
		super(vector.getX(), vector.getY(), vector.getZ());
	}
	public void setZ(double z) {
		this.z = z;
	}
	public void setY(double y) {
		this.y = y;
	}
	public void setX(double x) {
		this.x = x;
	}

	
	/**
	 * Adds two vectors
	 * @param that
	 * @return
	 */
	public Vector3 add(Vector3 that){
		this.x += that.x;
		this.y += that.y;
		this.z += that.z;
		return this;
	}
	/** 
	 * Subtracts two vectors
	 * @param that
	 * @return
	 */
	public Vector3 subtract(Vector3 that){
		this.x -= that.x;
		this.y -= that.y;
		this.z -= that.z;
		return this;
	}
	/**
	 * Scales by the scalar value
	 * @param scale
	 * @return
	 */
	public Vector3 scale(double scale){
		this.x *= scale;
		this.y *= scale;
		this.z *= scale;
		return this;
	}
	
	/**
	 * Takes the cross product of two vectors
	 * @param that
	 * @return
	 */
	public Vector3 cross(Vector3 that){
		this.x = this.getY() * that.getZ() - this.getZ() * that.getY();
		this.y = this.getZ() * that.getX() - this.getX() * that.getZ();
		this.z = this.getX() * that.getY() - this.getY() * that.getX();	
		
		return this;
	}
	/**
	 * returns the vector with a length of 1
	 * @return
	 */
	public Vector3 normalize(){
		double length = this.length();
		this.x *= 1/length;
		this.y *= 1/length;
		this.z *= 1/length;
		return this;
	}
		

}
