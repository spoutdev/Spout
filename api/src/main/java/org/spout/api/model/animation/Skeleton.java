/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.model.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Skeleton {
	private Bone root;
	private ArrayList<Bone> bones = new ArrayList<Bone>();
	private Map<String, Bone> bonesName = new HashMap<String, Bone>();
	private ArrayList<ArrayList<Integer>> verticies = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Float>> weights = new ArrayList<ArrayList<Float>>();
	private int maxBonePerVertice = 0;
	private Map<String, Animation> animations;

	public Skeleton() {

	}

	/**
	 * Add a bone in this skeleton, if the bone don't have parent, it must be the root bone if the root is
	 *
	 * Define as
	 */
	public void addBone(String name, String parentName, Bone bone) {

		//Define a uniq id
		bone.setId(bonesName.size());

		//Store the bone with name
		bonesName.put(name, bone);

		//Store it in array
		bones.add(bone);

		//Root bone
		if (root == null) {
			if (parentName != null) {
				throw new IllegalStateException("Root bone can't have parent");
			}

			root = bone;
		} else {
			if (parentName == null) {
				throw new IllegalStateException("Root bone already defined");
			}

			Bone parent = bonesName.get(parentName);

			if (parent == null) {
				throw new IllegalStateException("Parent bone unfindable");
			}

			parent.attachBone(bone);
			bone.setParent(parent);
		}

		if (bone.getVerticies().length != bone.getWeight().length) {
			throw new IllegalStateException("Number of vertices don't match number of weight");
		}

		//Fill verticeBone
		for (int i = 0; i < bone.getVerticies().length; i++) {
			int id = bone.getVerticies()[i];
			float weight = bone.getWeight()[i];

			while (id > verticies.size() - 1) {
				verticies.add(new ArrayList<Integer>());
				weights.add(new ArrayList<Float>());
			}

			verticies.get(id).add(bone.getId());
			weights.get(id).add(weight);

			maxBonePerVertice = Math.max(verticies.get(id).size(), maxBonePerVertice);
		}
	}

	/*
	 * Add a animation
	 */
	public void addAnimation(String name, Animation animation) {
		animations.put(name, animation);
	}

	public int getBoneSize() {
		return bones.size();
	}

	public Bone getBone(int i) {
		return bones.get(i);
	}

	public ArrayList<ArrayList<Integer>> getVerticeArray() {
		return verticies;
	}

	public ArrayList<ArrayList<Float>> getWeightArray() {
		return weights;
	}

	public int getBonePerVertice() {
		return maxBonePerVertice;
	}

	public void print() {
		StringBuilder str = new StringBuilder();
		str.append("Skeleton : \n");
		for (int i = 0; i < verticies.size(); i++) {
			str.append(" Vertice " + i + " : ");
			for (int j = 0; j < verticies.get(i).size(); j++) {
				str.append(verticies.get(i).get(j) + ", ");
			}
			str.append("\n");
		}
		System.out.println(str.toString());
	}

	public Bone getBoneByName(String key) {
		return bonesName.get(key);
	}
}
