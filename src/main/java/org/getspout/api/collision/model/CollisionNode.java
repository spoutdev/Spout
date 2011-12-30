package org.getspout.api.collision.model;

import java.util.HashMap;

import org.getspout.api.collision.CollisionVolume;

public class CollisionNode {
	CollisionStrategy stratagy = CollisionStrategy.NOCOLLIDE;
	
	HashMap<String, CollisionNode> children = new HashMap<String, CollisionNode>();
	
	CollisionVolume volume;
	
	public CollisionNode(CollisionVolume volume){
		this(volume, CollisionStrategy.NOCOLLIDE);
	}
	
	public CollisionNode(CollisionVolume volume, CollisionStrategy strat){
		this.stratagy = strat;
		this.volume = volume;
	}
	
	public void addChild(String name, CollisionVolume volume){
		if(children.containsKey(name)) throw new IllegalArgumentException("This node already has that child");
		//TODO add a check to see if this volume contains the other volume
		if(!this.volume.contains(volume)) throw new IllegalArgumentException("Our Volume doesn't fully contain the Child Volume"); 
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
