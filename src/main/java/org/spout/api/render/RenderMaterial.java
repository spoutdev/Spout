package org.spout.api.render;

public interface RenderMaterial {
	
	public Object getValue(String name);
	
	public Shader getShader();
	
	public void assign();
}
