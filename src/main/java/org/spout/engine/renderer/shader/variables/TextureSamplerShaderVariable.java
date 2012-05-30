package org.spout.engine.renderer.shader.variables;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.render.RenderMode;
import org.spout.api.render.Texture;

import org.spout.engine.resources.ClientTexture;

public class TextureSamplerShaderVariable extends ShaderVariable {
	int textureID;
	int textureNumber;
	int sampler;

	public TextureSamplerShaderVariable(int program, String name, Texture texture) {
		super(program, name);
		textureID = ((ClientTexture) texture).getTextureID();
	}

	public void set(Texture texture) {
		textureID = ((ClientTexture) texture).getTextureID();
	}

	public void bind(int unit) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		if (((Client) Spout.getEngine()).getRenderMode() != RenderMode.GL30) {
			GL20.glUniform1i(location, textureID);
		} else {
			GL30.glUniform1ui(location, textureID);
		}
	}

	@Override
	public void assign() {

	}
}
