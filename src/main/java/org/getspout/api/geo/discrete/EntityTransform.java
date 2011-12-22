package org.getspout.api.geo.discrete;

import org.getspout.api.math.Quaternion;
import org.getspout.api.math.Vector3m;

public class EntityTransform {	
	Point position;
	Quaternion rotation;
	Vector3m scale;
	
	
	public Point getPosition() {
		return position;
	}
	public void setPosition(Point position) {
		this.position = position;
	}
	public Quaternion getRotation() {
		return rotation;
	}
	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}
	public Vector3m getScale() {
		return scale;
	}
	public void setScale(Vector3m scale) {
		this.scale = scale;
	}

}
