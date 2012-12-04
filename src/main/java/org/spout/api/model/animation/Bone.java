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

public class Bone extends Resource{
	private String name;

	private Bone parent = null;
	private HashMap<String, Bone> children = new HashMap<String, Bone>();

	int[] vertex;
	float[] weights;
	
	private Map<String, Animation> animations;

	public Bone(String name, Bone parent, Map<String, Animation> animations2) {
		this.parent = parent;

		if(parent != null)
			parent.attachBone(this);

		this.name = name;
		this.animations = animations2;
	}

	public String getName() {
		return name;
	}

	private void attachBone(Bone child) {
		children.put(child.getName(), child);
	}

	public boolean hasBone(String name) {
		return children.containsKey(name);
	}

	public void removeBone(String name) {
		if (children.containsKey(name)) {
			children.remove(name);
		}
	}

	public Bone getBone(String name) {
		if (children.containsKey(name)) {
			return children.get(name);
		}
		for (Bone b : children.values()) {
			Bone r = b.getBone(name);
			if (r != null) {
				return r;
			}
		}
		return null;
	}

	public Bone getParent() {
		return parent;
	}

	public void setVertex(int[] vertex) {
		this.vertex = vertex;
	}

	public void setWeights(float[] weights) {
		this.weights = weights;
	}

	public void dumbBone(String str){
		System.out.println(str + "Bones : " + name + (parent != null ? "(parent : " + parent.getName() + ")" : ""));
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(str + "Vertices " + vertex.length + ": ");
		for(int v : vertex)
			sb.append(v).append(", ");
		System.out.println(sb.toString());
		
		sb = new StringBuilder();
		
		sb.append(str + "Weights " + weights.length + ": ");
		for(float w : weights)
			sb.append(w).append(", ");
		System.out.println(sb.toString());
		
		System.out.println(str + "Child : ");
		for(Bone bone : children.values()){
			bone.dumbBone(str + "  ");
		}
		
		for(Animation a : animations.values()){
			a.dumbAnimation(str + "  ");
		}
	}
	
}
