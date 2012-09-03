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

import java.awt.Color;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Yaml;

import org.spout.api.Spout;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;
import org.spout.api.render.Shader;
import org.spout.api.render.Texture;
import org.spout.api.resource.BasicResourceLoader;
import org.spout.api.resource.Resource;
import org.spout.api.util.typechecker.TypeChecker;

import org.spout.engine.resources.ClientRenderMaterial;

public class RenderMaterialLoader extends BasicResourceLoader<ClientRenderMaterial> {
	@Override
	public String getFallbackResourceName() {
		return "material://Spout/resources/fallbacks/generic.smt";
	}

	private static final TypeChecker<Map<? extends String, ?>> checkerMapStringObject = TypeChecker.tMap(String.class, Object.class);
	private static final Pattern vectorPattern = Pattern.compile("(vector[234]|color)\\((.*)\\)");
	@Override
	public ClientRenderMaterial getResource(InputStream stream) {
		final Yaml yaml = new Yaml();
		final Map<? extends String, ?> resourceProperties = checkerMapStringObject.check(yaml.load(stream));

		final Object shaderPathObject = resourceProperties.get("Shader");
		if (!(shaderPathObject instanceof String)) {
			throw new IllegalStateException("Tried to load a shader but wasn't given a path");
		}

		String shaderPath = (String) shaderPathObject;
		Spout.log(shaderPath);

		final Resource shaderObject = Spout.getFilesystem().getResource(shaderPath);
		if (!(shaderObject instanceof Shader)) {
			throw new IllegalStateException("Tried to load a shader but was given the path to a different kind of resource");
		}

		final Shader shader = (Shader) shaderObject;
		boolean depthTesting = true;
		if(resourceProperties.containsKey("RenderState"))
		{
			final Map<? extends String, ?> renderState = checkerMapStringObject.check(resourceProperties.get("RenderState"));
			Object s = renderState.get("Depth");
			if(s instanceof Boolean) depthTesting = (Boolean)s;
		}
		final Map<? extends String, ?> params = checkerMapStringObject.check(resourceProperties.get("MaterialParams"));

		// Better make a new HashMap, who knows whether we can even write to it...
		final Map<String, Object> paramsNew = new HashMap<String, Object>();

		// Loop through the params and replace
		for (Entry<? extends String, ?> entry : params.entrySet()) {
			final String key = entry.getKey();
			final Object value = entry.getValue();

			if (!(value instanceof String)) {
				paramsNew.put(key, value);
				continue;
			}

			final String val = (String) value;

			if (val.contains("://")) {
				// It's a resource!
				Resource resource = Spout.getFilesystem().getResource(val);
				if (resource instanceof Texture && !((Texture)resource).isLoaded()) {
					((Texture)resource).load();
				}

				paramsNew.put(key, resource);
				continue;
			}

			final Matcher matcher = vectorPattern.matcher(val);
			if (!matcher.matches()) {
				// It's not a Vector or a color => next
				paramsNew.put(key, value);
				continue;
			}

			final String type = matcher.group(1);
			final String[] values = matcher.group(2).split(",");

			System.out.println(values);

			if (type.equals("color")) {
				switch (values.length) {
				case 0:
				case 1:
				case 2:
					throw new IllegalArgumentException("Colors need at least 3 components");

				case 3:
					paramsNew.put(key, new Color(
							Float.parseFloat(values[0]),
							Float.parseFloat(values[1]),
							Float.parseFloat(values[2]),
							1.0f
					));
					continue;

				case 4:
					paramsNew.put(key, new Color(
							Float.parseFloat(values[0]),
							Float.parseFloat(values[1]),
							Float.parseFloat(values[2]),
							Float.parseFloat(values[3])
					));
					continue;

				default:
					throw new IllegalArgumentException("Colors takes at most 3 components");
				}
			}

			if (type.equals("vector2")) {
				if (values.length != 2) {
					throw new IllegalArgumentException("Vector2 needs 2 components");
				}

				paramsNew.put(key, new Vector2(
						Float.parseFloat(values[0]),
						Float.parseFloat(values[1])
				));
				continue;
			}

			if (type.equals("vector3")) {
				if (values.length != 3) {
					throw new IllegalArgumentException("Vector3 needs 3 components");
				}

				paramsNew.put(key, new Vector3(
						Float.parseFloat(values[0]),
						Float.parseFloat(values[1]),
						Float.parseFloat(values[2])
				));
				continue;
			}

			if (val.startsWith("vector4")) {
				if (values.length != 4) {
					throw new IllegalArgumentException("Vector4 needs 4 components");
				}

				paramsNew.put(key, new Vector4(
						Float.parseFloat(values[0]),
						Float.parseFloat(values[1]),
						Float.parseFloat(values[2]),
						Float.parseFloat(values[3])
				));
				continue;
			}

			throw new IllegalStateException("This should never happen.");
		}

		//TODO: Parse matricies 
		return new ClientRenderMaterial(shader, paramsNew, null, null, depthTesting);
	}
	@Override
	public String getProtocol() {
		return "material";
	}
	@Override
	public String[] getExtensions() {
		return new String[] { "smt" };
	}
}
