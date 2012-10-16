/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
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
	private ClientTexture texture;

	public TextureSamplerShaderVariable(int program, String name, Texture texture) {
		super(program, name);
		set(texture);
	}
	
	public ClientTexture getTexture() {
		return texture;
	}

	public void set(Texture texture) {
		this.texture = (ClientTexture) texture;
		textureID = this.texture.getTextureID();
	}

	public void bind(int unit){
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
