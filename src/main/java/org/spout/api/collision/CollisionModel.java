/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.collision;

import java.util.ArrayList;

import org.spout.api.math.Vector3;

/**
 * Defines a Heirachial Collision Volume
 *
 */
public class CollisionModel extends CollisionVolume {
	CollisionVolume area;
	
	ArrayList<CollisionModel> children = new ArrayList<CollisionModel>();
	
	Vector3 origin;
	
	public CollisionModel(){
		area = new BoundingBox();
		
	}
	
	public CollisionModel(CollisionVolume base){
		if(base instanceof CollisionModel) throw new IllegalArgumentException("Cannot create a collision model with a collision model as an area");
		area = base;
	}
		
	
	public void addChild(CollisionVolume child){
		if(child instanceof CollisionModel){
			area.offset(origin);
			children.add((CollisionModel)child);
		} else {
			CollisionModel c = new CollisionModel(child);
			addChild(c); 
			//recursive calls ftw
		}
	}
	
	public CollisionVolume getVolume(){
		return area;
	}
	
	
	@Override
	public CollisionVolume offset(Vector3 ammount) {
		origin = origin.add(ammount);
		for(CollisionModel m : children){
			m.offset(ammount);
		}
		return this;
	}

	@Override
	public boolean intersects(CollisionVolume other) {
		if(other instanceof CollisionModel){
			if(!area.intersects(((CollisionModel)other).getVolume())) return false;
		}
		if(!area.intersects(other)) return false; //Check us
		if(children.size() > 0) return true; //We intersect and have no children, it intersects.
		for(CollisionModel m : children){
			if(m.intersects(other)) return true;
		}
		return false;
	}

	@Override
	public boolean contains(CollisionVolume other) {
		if(other instanceof CollisionModel){
			if(!area.contains(((CollisionModel)other).getVolume())) return false;
		}
		if(!area.contains(other)) return false; //Check us
		if(children.size() > 0) return true; //We intersect and have no children, it intersects.
		//TODO: Make this a breadth first search.  Right now it's depth first and it will be slow.
		for(CollisionModel m : children){
			if(m.contains(other)) return true;
		}
		return false;
	}

	@Override
	public boolean containsPoint(Vector3 b) {
		if(!area.containsPoint(b)) return false; //Check us
		if(children.size() > 0) return true; //We intersect and have no children, it intersects.
		//TODO: Make this a breadth first search.  Right now it's depth first and it will be slow.
		for(CollisionModel m : children){
			if(m.containsPoint(b)) return true;
		}
		return false;
	}

	@Override
	public Vector3 resolve(CollisionVolume other) {
		
		//TODO make this resolve with children
		if(other instanceof CollisionModel) return area.resolve(((CollisionModel)other).getVolume());
		return area.resolve(other);
	}

	@Override
	public Vector3 getPosition() {
		return origin;
	}

	public void setPosition(Vector3 position) {
		this.origin = position;
	}
	
}
