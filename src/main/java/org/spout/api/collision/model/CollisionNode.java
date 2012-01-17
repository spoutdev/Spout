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
package org.spout.api.collision.model;

import java.util.HashMap;

import org.spout.api.collision.CollisionVolume;

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
