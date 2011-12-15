package org.getspout.commons.math;

public class Vector2 {
	
	protected double x, y;
	
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
		
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	
	
	public Vector2 add(Vector2 that){
		return Vector2.add(this, that);
	}
	
	public Vector2 subtract(Vector2 that){
		return Vector2.subtract(this, that);
	}
	public Vector2 scale(double scale){
		return Vector2.scale(this, scale);
	}
	
	public double dot(Vector2 that){
		return Vector2.dot(this, that);
	}
	public Vector2 cross(Vector2 that){
		return new Vector2(y, -x);
	}
	public double lengthSquared(){
		return Vector2.lengthSquared(this);
	}
	public double length(){
		return Vector2.length(this);
	}
	public Vector2 normalize(){
		return Vector2.normalize(this);
	}
	public double[] toArray(){
		return Vector2.toArray(this);
	}
	
	
	
	public static Vector2 Zero = new Vector2(0,0);
	public static Vector2 UnitX = new Vector2(1,0);
	public static Vector2 UnitY = new Vector2(0,1);
	public static Vector2 One = new Vector2(1,1);
	
	public static double length(Vector2 a){
		return Math.sqrt(lengthSquared(a));
	}
	
	public static double lengthSquared(Vector2 a){
		return Vector2.dot(a, a);
	}
	
	public static Vector2 normalize(Vector2 a){
		return Vector2.scale(a, (1.f / a.length()));
	}
	
	public static Vector2 subtract(Vector2 a, Vector2 b){
		return new Vector2(a.getX() - b.getX(), a.getY() - b.getY() );
	}
	
	public static Vector2 add(Vector2 a, Vector2 b){
		return new Vector2(a.getX() + b.getX(), a.getY() + b.getY() );
	}
	
	public static Vector2 scale(Vector2 a, double b){
		return new Vector2(a.getX() * b, a.getY());
	}
	
	public static double dot(Vector2 a, Vector2 b){
		return (a.getX() * b.getX() + a.getY() * b.getY() );
	}

	public static double[] toArray(Vector2 a){
		return new double[]{a.getX(), a.getY()};
	}
}
