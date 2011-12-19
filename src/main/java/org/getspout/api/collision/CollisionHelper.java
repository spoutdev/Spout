package org.getspout.api.collision;

public class CollisionHelper {
	public static boolean checkCollision(BoundingBox a, BoundingBox b){
		return (a.min.compareTo(b.max)<=0 && a.max.compareTo(b.min)>= 0);
	}
	public static boolean checkCollision(BoundingSphere a, BoundingSphere b){
		return(a.radius + b.radius >= a.center.length() - b.center.length());
	}
}
