/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.collision;

import java.util.ArrayList;

import org.spout.api.math.Vector3;

/**
 * Represents a tree of {@link CollisionVolume}.
 * Has a CollisionVolume as a root of the node, 
 * and any children added are automatically converted to CollisionModel to represent the node.
 *
 */
public class CollisionModel extends CollisionVolume {
	private final CollisionVolume root;
	
	private final ArrayList<CollisionModel> nodes;
	
	public CollisionModel() {
		this(new BoundingBox());
	}
	
	public CollisionModel(CollisionVolume base) {
		this(base, new ArrayList<CollisionModel>());
	}

	private CollisionModel(CollisionVolume base, ArrayList<CollisionModel> nodes) {
		if (base instanceof CollisionModel) {
			throw new IllegalArgumentException("Cannot create a collision model with a collision model as an area");
		}
		root = base;
		this.nodes = nodes;
	}

	/**
	 * Adds a child to this tree of {@link CollisionVolume}.
	 * Returns a new instance of the tree with the child in it,
	 * or null if the child was null.
	 * 
	 * TODO: We may want to offset the children added by the same amount this tree is offset from origin.
	 * 
	 * @param child {@link CollisionVolume} to add
	 * @return new {@link CollisionModel} representing the tree including the new child, or null if the child was null
	 */
	public CollisionModel addChild(CollisionVolume child) {
		if (child == null) {
			return null;
		}

		ArrayList<CollisionModel> children = new ArrayList<CollisionModel>(nodes);

		if (child instanceof CollisionModel) {
			children.add((CollisionModel) child);
		} else {
			CollisionModel node = new CollisionModel(child);
			children.add(node);
		}
		return new CollisionModel(root, nodes);
	}

	public CollisionVolume getVolume() {
		return root;
	}

	@Override
	public CollisionModel offset(Vector3 amount) {
		CollisionVolume base = root.offset(amount);
		ArrayList<CollisionModel> children = new ArrayList<CollisionModel>(nodes.size());

		for (CollisionModel child : nodes) {
			children.add(child.offset(amount));
		}

		return new CollisionModel(base, children);
	}

	@Override
	public boolean intersects(CollisionVolume other) {
		if (other instanceof CollisionModel) {
			if (!root.intersects(((CollisionModel) other).getVolume()))
				return false;
		}
		if (!root.intersects(other)) {
			return false; // Return false if this volume doesn't intersect at all
		}
		if (nodes.size() == 0) {
			return true; // Return true if we have no children, and we intersected above
		}
		for (CollisionModel m : nodes) {
			if (m.intersects(other)) return true; // Return true if any children intersect
		}
		return false;
	}

	@Override
	public boolean contains(CollisionVolume other) {
		if (other instanceof CollisionModel) {
			if (!root.contains(((CollisionModel)other).getVolume())) return false;
		}
		if (!root.contains(other)) {
			return false; // Return false if this volume doesn't contain the other at all
		}
		if (nodes.size() == 0) {
			return true; // Return true if we have no children, and we contained the other above
		}
		//TODO: Make this a breadth first search.  Right now it's depth first and it will be slow.
		for (CollisionModel m : nodes) {
			if (m.contains(other)) {
				return true; // Return true if any children contain the other
			}
		}
		return false;
	}

	@Override
	public boolean containsPoint(Vector3 b) {
		if (!root.containsPoint(b)) {
			return false; // Return false if this volume doesn't contain the point at all
		}
		if (nodes.size() == 0) {
			return true; // Return true if we have no children, and we contained the point above
		}
		//TODO: Make this a breadth first search.  Right now it's depth first and it will be slow.
		for (CollisionModel m : nodes) {
			if (m.containsPoint(b)) {
				return true; // Return true if any children contain the point
			}
		}
		return false;
	}

	@Override
	public Vector3 resolve(CollisionVolume other) {
		
		//TODO make this resolve with children
		if (other instanceof CollisionModel) {
			return root.resolve(((CollisionModel)other).getVolume());
		}

		return root.resolve(other);
	}

	@Override
	public Vector3 getPosition() {
		return root.getPosition();
	}
}