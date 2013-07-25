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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.Yaml;

import org.spout.api.Spout;
import org.spout.api.model.animation.Animation;
import org.spout.api.model.animation.Skeleton;
import org.spout.api.model.mesh.Mesh;
import org.spout.api.render.RenderMaterial;
import org.spout.api.resource.ResourceLoader;
import org.spout.api.util.typechecker.TypeChecker;
import org.spout.engine.filesystem.resource.ClientModel;

public class ModelLoader extends ResourceLoader {
	private static final TypeChecker<Map<? extends String, ?>> checkerMapStringObject = TypeChecker.tMap(String.class, Object.class);

	public ModelLoader() {
		super("model", "model://Spout/fallbacks/fallback.spm");
	}

	@Override
	public ClientModel load(InputStream in) {
		final Yaml yaml = new Yaml();
		final Map<? extends String, ?> resourceProperties = checkerMapStringObject.check(yaml.load(in));

		Mesh mesh = Spout.getFileSystem().getResource((String) resourceProperties.get("Mesh"));
		RenderMaterial material = Spout.getFileSystem().getResource((String) resourceProperties.get("Material"));

		Skeleton skeleton = null;

		if (resourceProperties.containsKey("Skeleton")) {
			skeleton = Spout.getFileSystem().getResource((String) resourceProperties.get("Skeleton"));
		}

		Map<String, Animation> animations = new HashMap<String, Animation>();

		if (resourceProperties.containsKey("Animation")) {

			Map<? extends String, ?> map = checkerMapStringObject.check(resourceProperties.get("Animation"));

			for (Entry<? extends String, ?> entry : map.entrySet()) {
				Animation animation = Spout.getFileSystem().getResource((String) entry.getValue());

				animations.put(entry.getKey(), animation);
			}
		}

		return new ClientModel(mesh, skeleton, material, animations);
	}
}
