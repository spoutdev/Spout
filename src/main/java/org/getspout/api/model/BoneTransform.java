package org.getspout.api.model;

import org.getspout.api.math.Quaternion;
import org.getspout.api.math.Vector3;

public class BoneTransform {
	Vector3 position = Vector3.ZERO;
	Quaternion rotation = Quaternion.identity;
	Vector3 scale = Vector3.ONE;
	
	BoneTransform parent;

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}

	public Vector3 getScale() {
		return scale;
	}

	public void setScale(Vector3 scale) {
		this.scale = scale;
	}

	public BoneTransform getParent() {
		return parent;
	}

	public void setParent(BoneTransform parent) {
		this.parent = parent;
	}
	
	private BoneTransform add(BoneTransform other){
		BoneTransform t = new BoneTransform();
		t.position = this.position.add(other.position);
		t.rotation = this.rotation.multiply(other.rotation);
		t.scale = this.scale.add(other.scale);
		return t;
	}
	
	public BoneTransform getAbsolutePosition(){
		if(parent == null) return this;
		return this.add(parent.getAbsolutePosition());
	}
	
}
