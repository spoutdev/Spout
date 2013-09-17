/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spout.engine.filesystem.resource.loader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.spout.api.Spout;
import org.spout.api.plugin.PluginClassLoader;
import org.spout.api.render.effect.RenderEffect;
import org.spout.api.resource.ResourceLoader;
import org.spout.api.util.typechecker.TypeChecker;
import org.spout.engine.SpoutClient;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;
import org.spout.math.vector.Vector4;
import org.spout.renderer.Material;
import org.spout.renderer.data.Color;
import org.spout.renderer.gl.Program;
import org.spout.renderer.gl.Texture;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Johnson
 */
public class RenderMaterialLoader extends ResourceLoader {
	private static final TypeChecker<Map<? extends String, ?>> checkerMapStringObject = TypeChecker.tMap(String.class, Object.class);
	private static final Pattern vectorPattern = Pattern.compile("(vector[234]|color)\\((.*)\\)");

	public RenderMaterialLoader() {
		super("material", "material://Spout/fallbacks/generic.smt");
	}

	@Override
	public Material load(InputStream in) {
		final Yaml yaml = new Yaml();
		final Map<? extends String, ?> resourceProperties = checkerMapStringObject.check(yaml.load(in));

		final Object shaderPathObject = resourceProperties.get("Shader");
		
		if (!(shaderPathObject instanceof String)) {
			throw new IllegalStateException("Tried to load a shader but wasn't given a path");
		}

		String shaderPath = (String) shaderPathObject;
		Spout.log(shaderPath);
		ArrayList<Texture> textures = new ArrayList();
		final Program shader = Spout.getFileSystem().getResource(shaderPath);
		//int layer = 0;
		//boolean depthTesting = true;
		
		//if (resourceProperties.containsKey("RenderState")) {
		//	final Map<? extends String, ?> renderState = checkerMapStringObject.check(resourceProperties.get("RenderState"));
		//	Object s = renderState.get("Depth");
		//	if (s instanceof Boolean) {
		//		depthTesting = (Boolean) s;
		//	}
		//	
		//	Object s2 = renderState.get("Layer");
		//	if (s2 != null && s2 instanceof Integer) {
		//		layer = (Integer) s2;
		//	}
		//}

		// Better make a new HashMap, who knows whether we can even write to it...
		final Map<String, Object> paramsNew = new HashMap<>();

		if (resourceProperties.containsKey("MaterialParams")) {
			final Map<? extends String, ?> params = checkerMapStringObject.check(resourceProperties.get("MaterialParams"));

			// Loop through the params and replace
			for (Entry<? extends String, ?> entry : params.entrySet()) {
			final String key = entry.getKey();
			final Object value = entry.getValue();

			if (!(value instanceof String)) {
				//aramsNew.put(key, value);
				continue;
			}

			final String val = (String) value;

			if (val.contains("://")) {
				// It's a resource!
				Texture resource = Spout.getFileSystem().getResource(val);
				textures.add(resource);
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
						shader.setUniform(key, new Color(
						Float.parseFloat(values[0]),
						Float.parseFloat(values[1]),
						Float.parseFloat(values[2]),
						1.0f
						));
						continue;

					case 4:
						shader.setUniform(key, new Color(
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

				shader.setUniform(key, new Vector2(
				Float.parseFloat(values[0]),
				Float.parseFloat(values[1])
				));
				continue;
				}

			if (type.equals("vector3")) {
				if (values.length != 3) {
					throw new IllegalArgumentException("Vector3 needs 3 components");
				}

				shader.setUniform(key, new Vector3(
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

				shader.setUniform(key, new Vector4(
				Float.parseFloat(values[0]),
				Float.parseFloat(values[1]),
				Float.parseFloat(values[2]),
				Float.parseFloat(values[3])
				));
				continue;
			}

			throw new IllegalStateException("This should never happen.");
		}
	}

	//TODO: Parse matricies

	Material material = new Material(shader);
	int assign = 0;
	for(Texture t : textures) {
	    material.addTexture(assign, t);
	    assign++;
	}
	return material;
}
}