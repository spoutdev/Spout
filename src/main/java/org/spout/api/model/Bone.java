/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.spout.api.model.renderer.RenderEffect;

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
