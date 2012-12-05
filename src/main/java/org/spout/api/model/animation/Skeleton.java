package org.spout.api.model.animation;

import java.util.HashMap;
import java.util.Map;

import org.spout.api.resource.Resource;

public class Skeleton extends Resource{

	private Bone root;
	private Map<String, Bone> bonesName = new HashMap<String, Bone>();

	private Map<String,Animation> animations;

	public Skeleton(){

	}

	/**
	 * Add a bone in this skeleton,
	 * if the bone don't have parent, it must be the root bone
	 * if the root is 
	 * 
	 * Define as 
	 * @param name
	 * @param parentName
	 * @param bone
	 */
	public void addBone(String name, String parentName, Bone bone){

		//Define a uniq id
		bone.setId(bonesName.size());

		//Store the bone with name
		bonesName.put(name, bone);

		//Root bone
		if(root == null){
			if(parentName != null)
				throw new IllegalStateException("Root bone can't have parent");

			root = bone;
		}else{
			if(parentName == null)
				throw new IllegalStateException("Root bone already defined");

			Bone parent = bonesName.get(parentName);

			if(parent == null)
				throw new IllegalStateException("Parent bone unfindable");

			parent.attachBone(bone);
			bone.setParent(parent);
		}
	}
	
	/*
	 * Add a animation
	 */
	public void addAnimation(String name, Animation animation){
		animations.put(name, animation);
	}


}
