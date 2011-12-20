package org.getspout.api.collision;

import org.getspout.api.math.Vector3;

public class BoundingBox {
	Vector3 min;
	Vector3 max;
	
	
	public BoundingBox(Vector3 min, Vector3 max){
		this.min = min;
		this.max = max;
		
	}
	public BoundingBox(Vector3 pos){
		this.min = pos;
		this.max = pos.add(Vector3.ONE);
	}
	public BoundingBox(){
		this(Vector3.ZERO, Vector3.ONE);		
	}
	
	public boolean intersects(BoundingBox b){
		return CollisionHelper.checkCollision(this, b);
	}
	public boolean intersects(BoundingSphere b){
		return CollisionHelper.checkCollision(this, b);
	}
	
}
 