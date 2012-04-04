package org.spout.engine.renderer.shader;

@SuppressWarnings("serial")
public class ShaderVariableNotFoundException extends RuntimeException {
	public ShaderVariableNotFoundException(String variableName){
		super("Variable: " + variableName + " Not Found (was it optimized out?");
	}
}
