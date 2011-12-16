package org.getspout.api.math;

public class Vector2m extends Vector2 {

	public Vector2m(double x, double y) {
		super(x, y);
		
	}
	
	public Vector2 add(Vector2 that){
		this.x += that.x;
		this.y += that.y;
		return this;
	}
	
	public Vector2 subtract(Vector2 that){
		this.x -= that.x;
		this.y -= that.y;
		return this;
	}
	public Vector2 scale(double scale){
		this.x *= scale;
		this.y *= scale;
		return this;
	}
	
	public double dot(Vector2 that){
		return Vector2.dot(this, that);
	}
	public Vector2 cross(Vector2 that){
		double tmp = this.y;
		this.y = -this.x;
		this.x = tmp;
		return this;
	}
	public double lengthSquared(){
		return Vector2.lengthSquared(this);
	}
	public double length(){
		return Vector2.length(this);
	}
	public Vector2 normalize(){
		double length = this.length();
		this.x *= 1/length;
		this.y *= 1/length;
		return this;
	}
	public double[] toArray(){
		return Vector2.toArray(this);
	}

}
