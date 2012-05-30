package org.spout.engine.resources.loader;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import org.spout.api.render.Shader;

import org.spout.engine.filesystem.BasicResourceLoader;
import org.spout.engine.filesystem.FileSystem;
import org.spout.engine.resources.RenderMaterial;

public class RenderMaterialLoader extends BasicResourceLoader<RenderMaterial> {
	@Override
	public String getFallbackResourceName() {
		return "material://Spout/fallbacks/generic.smt";
	}

	@Override
	public RenderMaterial getResource(InputStream stream) {
		Yaml yaml = new Yaml();
		Map<?, ?> resource = (Map<?, ?>) yaml.load(stream);

		if (!(resource.get("Shader") instanceof String)) {
			throw new IllegalStateException("Tried to load a shader but wasn't given a path");
		}

		String in = (String) resource.get("Shader");
		@SuppressWarnings("unused")
		Shader s = (Shader) FileSystem.getResource(in);

		return null;
	}
}
