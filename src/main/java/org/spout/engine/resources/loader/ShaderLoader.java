package org.spout.engine.resources.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;



import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.render.RenderMode;
import org.spout.engine.filesystem.BasicResourceLoader;
import org.spout.engine.filesystem.SharedFilesystem;
import org.spout.engine.renderer.shader.ClientShader;
import org.yaml.snakeyaml.Yaml;


public class ShaderLoader extends BasicResourceLoader<ClientShader> {

	@Override
	public ClientShader getResource(InputStream stream) {
		
		if (((Client) Spout.getEngine()).getRenderMode() == RenderMode.GL11) {
			return ClientShader.BASIC;
		}
	
		
		
		//Get the paths for the Vertex and Fragment shaders
		Yaml yaml = new Yaml();
		Map<String, Map<String, ?>> resource = (Map<String, Map<String, ?>>) yaml.load(stream);
		
		ClientShader shader = null;
		if (((Client) Spout.getEngine()).getRenderMode() == RenderMode.GL30) {
			Map<String, ?> shaderfiles = resource.get("GL30");
			String fragShader = shaderfiles.get("Fragment").toString();
			String vertShader = shaderfiles.get("Vertex").toString();
			
			String fragSrc = readShaderSource(Spout.getFilesystem().getResourceStream(fragShader));
			String vertSrc = readShaderSource(Spout.getFilesystem().getResourceStream(vertShader));
			
			shader = new ClientShader(vertSrc, fragSrc, true);
			
			
		} else {
			Map<String, ?> shaderfiles = resource.get("GL20");
			String fragShader = shaderfiles.get("Fragment").toString();
			String vertShader = shaderfiles.get("Vertex").toString();
			
			String fragSrc = readShaderSource(Spout.getFilesystem().getResourceStream(fragShader));
			String vertSrc = readShaderSource(Spout.getFilesystem().getResourceStream(vertShader));
			
			shader = new ClientShader(vertSrc, fragSrc, true);
			
		}
		
		//TODO: Read Values in the shader to file
		
		
		
		return shader;
		
	}

	private String readShaderSource(InputStream stream){
		Scanner scan = new Scanner(stream);

		StringBuilder src = new StringBuilder();

		while (scan.hasNextLine()) {
			src.append(scan.nextLine() + "\n");
		}
		try {
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return src.toString();
	}

	@Override
	public String getFallbackResourceName() {
		return "shader://Spout/fallbacks/fallback.ssf";
	}
	
}
