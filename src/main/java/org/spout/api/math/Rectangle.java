package org.spout.api.math;

public class Rectangle {
	final Vector2 position;
	final Vector2 extents;
	
	public Rectangle(Vector2 position, Vector2 extents){
		this.position = position;
		this.extents = extents;
	}
	
	public Rectangle(float x, float y, float w, float h){
		this(new Vector2(x,y), new Vector2(w,h));
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public Vector2 getExtents() {
		return extents;
	}
	
	public float getX() {
		return position.getX();
	}
	
	public float getY() {
		return position.getY();
	}
	
	public float getWidth() {
		return extents.getX();
	}
	
	public float getHeight() {
		return extents.getY();
	}
	
}
