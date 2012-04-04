package org.spout.engine.client.renderer.shader;

@SuppressWarnings("serial")
public class ShaderVariableNotFoundException extends RuntimeException {
	public ShaderVariableNotFoundException(String variableName){
		super("Variable: " + variableName + " Not Found (was it optimized out?");
	}
}
