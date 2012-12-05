/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
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
