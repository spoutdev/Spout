package org.spout.server.client.renderer.shader;

public class ShaderCompileException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ShaderCompileException(String text){
		super(text);
	}
}
