package org.getspout.unchecked.api.collision;

import org.getspout.unchecked.api.math.Vector3;

public class Plane {
	Vector3 point;
	Vector3 normal;
	
	public Plane(Vector3 point, Vector3 normal){
		this.point = point;
		this.normal = normal;
	}
	public Plane(Vector3 point){
		this(point, Vector3.Up);
	}
	public Plane(){
		this(Vector3.ZERO, Vector3.Up);
	}
	public static Plane fromTwoVectors(Vector3 a, Vector3 b){
		return new Plane(a, a.cross(b));
	}
	
}
