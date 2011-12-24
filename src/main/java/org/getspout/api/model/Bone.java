package org.getspout.api.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.getspout.api.model.renderer.RenderEffect;

public class Bone {
	String name;
	HashMap<String, Bone> children = new HashMap<String, Bone>();
	BoneTransform transform = new BoneTransform();
	ArrayList<RenderEffect> attachedEffects = new ArrayList<RenderEffect>();
	
	Mesh mesh;

	public Bone(String name, BoneTransform parent){
		if(parent != null) transform.setParent(parent);
		this.name = name;
	}
	
	public BoneTransform getTransform() {
		return transform;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public String getName() {
		return name;
	}
	
	public void attachBone(String name, Mesh mesh){
		Bone b = new Bone(name, this.getTransform());
		b.setMesh(mesh);
		children.put(name, b);
	}
	
	public boolean hasBone(String name){
		return(children.containsKey(name));
	}
	
	public boolean isBone(String name){
		return name.equals(this.name);
	}
	
	public void removeBone(String name){
		if(children.containsKey(name)) children.remove(name);
	}
	
	public Bone getBone(String name){
		if(children.containsKey(name)) return children.get(name);
		for(Bone b : children.values()){
			Bone r = b.getBone(name);
			if(r != null) return r;
		}
		return null;
	}
	
	public void attachEffect(RenderEffect effect){
		attachedEffects.add(effect);
	}
	public void detachEffect(RenderEffect effect){
		attachedEffects.remove(effect);
	}
}
