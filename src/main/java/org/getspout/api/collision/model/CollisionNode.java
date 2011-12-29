package org.getspout.api.collision.model;

import java.util.HashMap;

import org.getspout.api.collision.CollisionVolume;

public class CollisionNode {
	CollisionStratagy stratagy = CollisionStratagy.NOCOLLIDE;
	
	HashMap<String, CollisionNode> children = new HashMap<String, CollisionNode>();
	
	CollisionVolume volume;
	
	public CollisionNode(CollisionVolume volume){
		this(volume, CollisionStratagy.NOCOLLIDE);
	}
	
	public CollisionNode(CollisionVolume volume, CollisionStratagy strat){
		this.stratagy = strat;
		this.volume = volume;
	}
	
	@SuppressWarnings("unused")
	public void addChild(String name, CollisionVolume volume){
		if(children.containsKey(name)) throw new IllegalArgumentException("This node already has that child");
		//TODO add a check to see if this volume contains the other volume
		if(false) throw new IllegalArgumentException("Our Volume doesn't fully contain the Child Volume"); 
		children.put(name, new CollisionNode(volume));
	}
	
	public CollisionNode getNode(String name){
		if(children.containsKey(name)) return children.get(name);
		for(CollisionNode node : children.values()){
			CollisionNode ret = node.getNode(name);
			if(ret != null) return ret;
		}
		return null;
	}
	
	
}
