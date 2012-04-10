package org.spout.engine.renderer.shader.variables;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.spout.api.render.Texture;
import org.spout.engine.resources.ClientTexture;

public class TextureSamplerShaderVariable extends ShaderVariable {
	int textureID;
	int textureNumber;
	
	
	public TextureSamplerShaderVariable(int program, String name, Texture texture, int bindNum) {
		super(program, name);
		textureID = ((ClientTexture)texture).getTextureID();
		this.textureNumber = bindNum;
	}
	
	public void set(Texture texture){
		textureID = ((ClientTexture)texture).getTextureID();
	}

	@Override
	public void assign() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + textureNumber);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL30.glUniform1ui(location, textureID);
	}

}
