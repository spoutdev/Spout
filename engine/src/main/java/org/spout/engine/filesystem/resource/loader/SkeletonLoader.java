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
package org.spout.engine.filesystem.resource.loader;

import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.Yaml;

import org.spout.api.model.animation.Bone;
import org.spout.api.model.animation.Skeleton;
import org.spout.api.resource.ResourceLoader;
import org.spout.api.util.typechecker.TypeChecker;

@SuppressWarnings ("unchecked")
public class SkeletonLoader extends ResourceLoader {
	private static final TypeChecker<Map<? extends String, ?>> checkerMapStringObject = TypeChecker.tMap(String.class, Object.class);

	public SkeletonLoader() {
		super("skeleton", "skeleton://Spout/entities/Spouty/spouty.ske");
	}

	private static Skeleton loadObj(InputStream stream) {
		System.out.println("Loadind skeleton");
		final Yaml yaml = new Yaml();
		final Map<? extends String, ?> resourceProperties = checkerMapStringObject.check(yaml.load(stream));

		String root = resourceProperties.keySet().iterator().next();

		Map<? extends String, ?> node = (Map<? extends String, ?>) resourceProperties.get(root);

		Skeleton skeleton = new Skeleton();

		loadBone(skeleton, root, null, node);

		//System.out.println("Skeleton loaded : (org.spout.engine.resources.loader.SkeletonLoader line 57)");
		//skeleton.print();

		return skeleton;
	}

	private static void loadBone(Skeleton skeleton, String name, String parentName, Map<? extends String, ?> keymap) {
		Bone bone = new Bone();
		bone.setName(name);

		bone.setVerticies(loadIntList((String) keymap.get("vertices")));
		bone.setWeights(loadFloatList((String) keymap.get("weight")));

		skeleton.addBone(name, parentName, bone);

		if (keymap.containsKey("children")) {
			loadChild(skeleton, name, (Map<? extends String, Map<? extends String, ?>>) keymap.get("children"));
		}
	}

	private static void loadChild(Skeleton skeleton, String parentName, Map<? extends String, Map<? extends String, ?>> keymap) {
		for (Entry<? extends String, Map<? extends String, ?>> entry : keymap.entrySet()) {
			loadBone(skeleton, entry.getKey(), parentName, entry.getValue());
		}
	}

	private static int[] loadIntList(String str) {
		String[] split = str.split(" ");
		int[] result = new int[split.length];

		for (int i = 0; i < split.length; i++) {
			result[i] = Integer.parseInt(split[i]);
		}

		return result;
	}

	private static float[] loadFloatList(String str) {
		String[] split = str.split(" ");
		float[] result = new float[split.length];

		for (int i = 0; i < split.length; i++) {
			result[i] = Float.parseFloat(split[i]);
		}

		return result;
	}

	@Override
	public Skeleton load(InputStream in) {
		return SkeletonLoader.loadObj(in);
	}
}
