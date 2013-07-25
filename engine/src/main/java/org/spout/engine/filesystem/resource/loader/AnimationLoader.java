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

import org.spout.api.Spout;
import org.spout.api.model.animation.Animation;
import org.spout.api.model.animation.Bone;
import org.spout.api.model.animation.BoneTransform;
import org.spout.api.model.animation.Skeleton;
import org.spout.api.resource.ResourceLoader;
import org.spout.api.util.typechecker.TypeChecker;

@SuppressWarnings ("unchecked")
public class AnimationLoader extends ResourceLoader {
	private static final TypeChecker<Map<? extends String, ?>> checkerMapStringObject = TypeChecker.tMap(String.class, Object.class);

	public AnimationLoader() {
		super("animation", "animation://Spout/entities/Spouty/spouty.sam");
	}

	private static Animation loadObj(InputStream stream) {
		final Yaml yaml = new Yaml();
		final Map<? extends String, ?> resourceProperties = checkerMapStringObject.check(yaml.load(stream));

		Skeleton skeleton = Spout.getFileSystem().getResource((String) resourceProperties.get("Skeleton"));

		int frames = (Integer) resourceProperties.get("frames");
		float delay = ((Double) resourceProperties.get("delay")).floatValue();

		Animation animation = new Animation(skeleton, frames, delay);

		Map<? extends String, ?> bones_data = (Map<? extends String, ?>) resourceProperties.get("bones_data");

		for (Entry<? extends String, ?> entry : bones_data.entrySet()) {

			Bone bone = skeleton.getBoneByName(entry.getKey());
			if (bone == null) {
				throw new IllegalStateException("Animation file mapped with the bad Skeleton file");
			}

			Map<? extends Integer, String> value = (Map<? extends Integer, String>) entry.getValue();

			int i = 0;
			for (Entry<? extends Integer, String> entryBone : value.entrySet()) {
				int frame = entryBone.getKey() - 1; //Start at 1

				BoneTransform boneTransform = new BoneTransform(loadFloatList(entryBone.getValue()));

				animation.setBoneTransform(bone.getId(), i/*frame*/, boneTransform);

				i++;
			}
		}

		//System.out.println("Animation loaded : (org.spout.engine.resources.loader.AnimationLoader line 77)");
		//animation.dumbAnimation(" ");

		return animation;
	}

	private static float[] loadFloatList(String str) {
		String[] split = str.split(",");
		float[] result = new float[split.length];

		for (int i = 0; i < split.length; i++) {
			result[i] = (float) Double.parseDouble(split[i]);
		}

		return result;
	}

	@Override
	public Animation load(InputStream in) {
		return AnimationLoader.loadObj(in);
	}
}
