package org.spout.engine.client.renderer.shader.variables;

import org.lwjgl.opengl.GL20;
import org.spout.engine.client.renderer.BatchModes;
import org.spout.engine.client.renderer.BatchVertexRenderer;
import org.spout.engine.client.renderer.shader.ShaderVariableNotFoundException;

public abstract class ShaderVariable {
	public static final boolean variableError = false;
	
	int program;
	int location;
	@SuppressWarnings("unused")
	public ShaderVariable(int program, String name){
		if(BatchVertexRenderer.GLMode == BatchModes.GL11) return;  //Shaders don't exist in OpenGL 1.1
		this.program = program;
		GL20.glUseProgram(program);
		//If we are an attribute, we aren't a uniform.  Don't continue
		if(this instanceof AttributeShaderVariable) return;
		
		this.location = GL20.glGetUniformLocation(program, name);
		
		//Error Checking.  In production, leave this as a warning, because OpenGL doesn't care if you try to put something
		//into a variable that doesn't exist (it ignores it).
		//
		//If we want to have a debug mode, switch the final bool to true to throw an exception if the variable doesn't exist.
		//This is the same as treating warnings as errors, and could be useful for debugging shaders.
		if(this.location == -1 && !variableError){
			System.out.println("[Warning] Shader Variable: "+ name + " not found! (Was it optimized out?)");
		}else if(this.location == -1 && variableError){
			throw new ShaderVariableNotFoundException(name);
		}
	}
	public abstract void assign();
	
}
