/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.resources.loader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spout.api.math.Vector3;
import org.spout.api.model.animation.Animation;
import org.spout.api.model.animation.Bone;
import org.spout.api.model.animation.BoneTransform;
import org.spout.api.resource.BasicResourceLoader;
import org.spout.api.util.typechecker.TypeChecker;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings("unchecked")
public class AnimationLoader extends BasicResourceLoader<Bone> {

	private static final TypeChecker<Map<? extends String, ?>> checkerMapStringObject = TypeChecker.tMap(String.class, Object.class);

	private static Bone loadObj(InputStream stream) {
		/*final Yaml yaml = new Yaml();
		final Map<? extends String, ?> resourceProperties = checkerMapStringObject.check(yaml.load(stream));

		String root = resourceProperties.keySet().iterator().next();

		Map<? extends String, ?> node = (Map<? extends String, ?>) resourceProperties.get(root);

		return loadBone(root, null, node);*/
		
		return null;
	}

	/*private static Bone loadBone(String name, Bone parent, Map<? extends String, ?> keymap){
		Map<String, Animation> animations = loadAnimations((Map<? extends Integer, Map<? extends String, ?>>) keymap.get("anim"));

		Bone bone = new Bone(name, parent, animations);

		if(keymap.containsKey("children"))
			loadChild(bone, (Map<? extends String, Map<? extends String, ?>>) keymap.get("children"));

		bone.setVertex(loadIntList((String) keymap.get("vertices")));
		bone.setWeights(loadFloatList((String) keymap.get("weight")));

		return bone;
	}

	private static void loadChild(Bone parent, Map<? extends String, Map<? extends String, ?>> keymap){
		for(Entry<? extends String, Map<? extends String, ?>> entry : keymap.entrySet()){
			loadBone(entry.getKey(), parent, (Map<? extends String, Map<? extends String, ?>>)entry.getValue());
		}
	}

	private static Map<String,Animation> loadAnimations(Map<? extends Integer, Map<? extends String, ?>> keymap){
		Map<String,Animation> map = new HashMap<String,Animation>();
		Animation animation = new Animation();
		animation.setTransforms(new BoneTransform[keymap.size()]);

		//int index = 0;
		for(Entry<? extends Integer, Map<? extends String, ?>> entry : keymap.entrySet()){
			Integer index = entry.getKey();

			animation.getTransforms()[index] = loadBoneTransform((Map<? extends String, Object>)entry.getValue());

			//index++;
		}

		return map;
	}

	private static BoneTransform loadBoneTransform(Map<? extends String, Object> keymap){
		Vector3 head = loadVector3((String)keymap.get("head"));
		Vector3 tail = loadVector3((String)keymap.get("tail"));

		return new BoneTransform(head, tail);
	}

	private static int[] loadIntList(String str){
		String []split = str.split(" ");
		int []result = new int[split.length];

		for(int i = 0; i < split.length; i++){		
			result[i] = Integer.parseInt(split[i]);
		}

		return result;
	}

	private static float[] loadFloatList(String str){
		String []split = str.split(" ");
		float []result = new float[split.length];

		for(int i = 0; i < split.length; i++){		
			result[i] = Float.parseFloat(split[i]);
		}

		return result;
	}

	private static Vector3 loadVector3(String str){
		String []split = str.split(" ");
		return new Vector3(
				Double.parseDouble(split[0]),
				Double.parseDouble(split[1]),
				Double.parseDouble(split[2]));
	}*/

	@Override
	public String getFallbackResourceName() {
		return "animation://Spout/resources/resources/entities/Spouty/spouty.sam";
	}

	@Override
	public Bone getResource(InputStream stream) {
		return AnimationLoader.loadObj(stream);
	}

	@Override
	public String getProtocol() {
		return "animation";
	}

	@Override
	public String[] getExtensions() {
		return new String[] { "sam" };
	}

}
