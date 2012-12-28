/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine.resources.loader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spout.api.Spout;
import org.spout.api.model.animation.Animation;
import org.spout.api.model.animation.Skeleton;
import org.spout.api.model.mesh.Mesh;
import org.spout.api.render.RenderMaterial;
import org.spout.api.resource.BasicResourceLoader;
import org.spout.api.util.typechecker.TypeChecker;
import org.spout.engine.resources.ClientModel;
import org.yaml.snakeyaml.Yaml;

public class ModelLoader extends BasicResourceLoader<ClientModel> {

	private static final String[] extensions = new String[]{"spm"};
	private static final TypeChecker<Map<? extends String, ?>> checkerMapStringObject = TypeChecker.tMap(String.class, Object.class);

	@Override
	public String getProtocol() {
		return "model";
	}

	@Override
	public String[] getExtensions() {
		return extensions;
	}

	@Override
	public String getFallbackResourceName() {
		return "model://Spout/fallbacks/fallback.spm";
	}

	@Override
	public ClientModel getResource(InputStream stream) {
		final Yaml yaml = new Yaml();
		final Map<? extends String, ?> resourceProperties = checkerMapStringObject.check(yaml.load(stream));

		Mesh mesh = (Mesh) Spout.getFilesystem().getResource((String) resourceProperties.get("Mesh"));
		RenderMaterial material = (RenderMaterial) Spout.getFilesystem().getResource((String) resourceProperties.get("Material"));

		Skeleton skeleton = null;

		if (resourceProperties.containsKey("Skeleton")) {
			skeleton = (Skeleton) Spout.getFilesystem().getResource((String) resourceProperties.get("Skeleton"));
		}

		Map<String, Animation> animations = new HashMap<String, Animation>();

		if (resourceProperties.containsKey("Animation")) {

			Map<? extends String, ?> map = checkerMapStringObject.check(resourceProperties.get("Animation"));

			for (Entry<? extends String, ?> entry : map.entrySet()) {
				Animation animation = (Animation) Spout.getFilesystem().getResource((String) entry.getValue());

				animations.put(entry.getKey(), animation);
			}
		}

		return new ClientModel(mesh, skeleton, material, animations);
	}
}
