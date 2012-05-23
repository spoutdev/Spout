package org.spout.engine.resources.loader;

import java.io.InputStream;
import java.util.Map;

import org.spout.api.render.Shader;
import org.spout.engine.filesystem.BasicResourceLoader;
import org.spout.engine.filesystem.FileSystem;
import org.spout.engine.resources.RenderMaterial;
import org.yaml.snakeyaml.Yaml;

public class RenderMaterialLoader extends BasicResourceLoader<RenderMaterial> {

	@Override
	public String getFallbackResourceName() {
		return "material://Spout/fallbacks/generic.smt";
	}

	@Override
	public RenderMaterial getResource(InputStream stream) {
		Yaml yaml = new Yaml();
		Map<String, ?> resource = (Map<String, ?>) yaml.load(stream);
		
		if(!(resource.get("Shader") instanceof String)) {
			throw new IllegalStateException("Tried to load a shader but wasn't given a path");
		}
		
		String in = (String)resource.get("Shader");
		Shader s = (Shader)FileSystem.getResource(in);
		
		

		
	
		return null;
	}

}
