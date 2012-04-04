package org.spout.server.client.renderer.shader;


import java.nio.FloatBuffer;

import org.spout.api.math.Matrix;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spout.server.client.renderer.shader.variables.Mat4ShaderVariable;

public class BasicShader extends ClientShader {
	FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4*4);
	
	public BasicShader() {
		super(null, null);
		
	}
	
	public void assign(boolean compatabilityMode){
		if(!variables.containsKey("Projection"))throw new IllegalStateException("Basic Shader must have a projection matrix assigned");
		if(!variables.containsKey("View")) throw new IllegalStateException("Basic Shader must have a view matrix assigned");
		
		if(compatabilityMode){
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			matrixBuffer.clear();
			matrixBuffer.put(getProjectionMatrix().toArray());
			matrixBuffer.flip();
			
			GL11.glLoadMatrix(matrixBuffer);
			
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			matrixBuffer.clear();
			matrixBuffer.put(getViewMatrix().toArray());
			matrixBuffer.flip();
			
			GL11.glLoadMatrix(matrixBuffer);
			
		}else{
			super.assign();
		}
		
	}
	
	public void setViewMatrix(Matrix mat){
		setUniform("View", mat);
	}
	
	public Matrix getViewMatrix()	{
		return ((Mat4ShaderVariable)variables.get("View")).get();
		
	}
	public Matrix getProjectionMatrix()	{
		return ((Mat4ShaderVariable)variables.get("Projection")).get();		
	}
	
	
	public void setProjectionMatrix(Matrix mat){
		setUniform("Projection", mat);
	}
}
