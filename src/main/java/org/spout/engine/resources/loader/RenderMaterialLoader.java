package org.spout.engine.resources.loader;

import java.awt.Color;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

import org.spout.api.Spout;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;
import org.spout.api.render.Shader;
import org.spout.api.render.Texture;
import org.spout.api.resource.Resource;

import org.spout.engine.filesystem.BasicResourceLoader;
import org.spout.engine.filesystem.FileSystem;
import org.spout.engine.resources.ClientRenderMaterial;

public class RenderMaterialLoader extends BasicResourceLoader<ClientRenderMaterial> {
	@Override
	public String getFallbackResourceName() {
		return "material://Spout/fallbacks/generic.smt";
	}

	@Override
	public ClientRenderMaterial getResource(InputStream stream) {
		Yaml yaml = new Yaml();
		Map<String, ?> resource = (Map<String, ?>) yaml.load(stream);

		if (!(resource.get("Shader") instanceof String)) {
			throw new IllegalStateException("Tried to load a shader but wasn't given a path");
		}

		String in = (String) resource.get("Shader");
		Spout.log(in);
		Shader s = (Shader) FileSystem.getResource(in);

		Map<String, Object> params = (Map<String, Object>) resource.get("MaterialParams");

		//Loop through the params and replace
		Set<Map.Entry<String, Object>> entrySet = params.entrySet();
		for (Map.Entry<String, Object> entry : entrySet) {
			if (entry.getValue() instanceof String) {
				String val = (String) entry.getValue();
				if (val.contains("://")) { //its a resource!
					Resource r = FileSystem.getResource(val);
					if (r instanceof Texture && !((Texture) r).isLoaded()) {
						((Texture) r).load();
					}
					params.put(entry.getKey(), r);
				}

				//TODO: clean this up
				if (val.contains("(")) { //It's a Vector or a color!
					String valueString = val.substring(val.indexOf('(') + 1, val.lastIndexOf(')'));
					System.out.println(valueString);
					if (val.startsWith("color")) {
						String[] values = valueString.split(",");
						if (values.length < 3) {
							throw new IllegalArgumentException("Colors need atleast 3 components");
						}
						if (values.length == 3) {
							Color col = new Color(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]), 1.0f);
							params.put(entry.getKey(), col);
						}
						if (values.length == 4) {
							Color col = new Color(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3]));
							params.put(entry.getKey(), col);
						}
					}
					if (val.startsWith("vector2")) {
						String[] values = valueString.split(",");
						if (values.length < 2) {
							throw new IllegalArgumentException("Colors need atleast 2 components");
						}
						Vector2 vec = new Vector2(Float.parseFloat(values[0]), Float.parseFloat(values[1]));
						params.put(entry.getKey(), vec);
					}
					if (val.startsWith("vector3")) {
						String[] values = valueString.split(",");
						if (values.length < 3) {
							throw new IllegalArgumentException("Colors need atleast 3 components");
						}
						Vector3 vec = new Vector3(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
						params.put(entry.getKey(), vec);
					}
					if (val.startsWith("vector4")) {
						String[] values = valueString.split(",");
						if (values.length < 4) {
							throw new IllegalArgumentException("Colors need atleast 3 components");
						}
						Vector4 vec = new Vector4(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3]));
						params.put(entry.getKey(), vec);
					}
				}
			}
		}

		ClientRenderMaterial mat = new ClientRenderMaterial(s, params);

		return mat;
	}
}
