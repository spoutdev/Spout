package org.spout.engine.resources;

import java.awt.Color;
import java.util.Map;
import java.util.Set;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.Shader;
import org.spout.api.resource.Resource;

public class ClientRenderMaterial extends Resource implements RenderMaterial {
	Shader shader;
	Map<String, Object> materialParameters;
	
	public ClientRenderMaterial(Shader s, Map<String, Object> params){
		this.shader = s;
		this.materialParameters = params;
	}
	
	public void assign(){
		Set<Map.Entry<String, Object>> s = materialParameters.entrySet();

		for(Map.Entry<String, Object> entry : s){
			if(entry.getValue() instanceof Integer){
				shader.setUniform(entry.getKey(), ((Integer)entry.getValue()).intValue());
			} else if( entry.getValue() instanceof Float){
				shader.setUniform(entry.getKey(), ((Float)entry.getValue()).floatValue());
			} else if( entry.getValue() instanceof Double){
				shader.setUniform(entry.getKey(), ((Double)entry.getValue()).floatValue());
			} else if( entry.getValue() instanceof ClientTexture){
				shader.setUniform(entry.getKey(), (ClientTexture)entry.getValue());
			} else if( entry.getValue() instanceof Vector2){
				shader.setUniform(entry.getKey(), (Vector2)entry.getValue());
			} else if( entry.getValue() instanceof Vector3){
				shader.setUniform(entry.getKey(), (Vector3)entry.getValue());
			} else if( entry.getValue() instanceof Vector4){
				shader.setUniform(entry.getKey(), (Vector4)entry.getValue());
			} else if( entry.getValue() instanceof Color){
				shader.setUniform(entry.getKey(), (Color)entry.getValue());
			}
		}
		//TODO: make view and projection dependent on Material params
		shader.setUniform("View", ((Client)Spout.getEngine()).getActiveCamera().getView());
		shader.setUniform("Projection", ((Client)Spout.getEngine()).getActiveCamera().getProjection());
		
		shader.assign();

	}

	@Override
	public Object getValue(String name) {
		return materialParameters.get(name);
	}
	
	@Override
	public Shader getShader(){
		return shader;
	}
	
	
}
