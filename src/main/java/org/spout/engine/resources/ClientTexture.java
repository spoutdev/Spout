/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.resources;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.render.RenderMode;
import org.spout.api.render.Texture;
import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutRenderer;
import org.spout.engine.renderer.shader.ClientShader.ShaderCompilationTask;

public class ClientTexture extends Texture {
	int textureID = -1;

	public ClientTexture(int[] colors, int width, int height){
		super(colors, width, height);
	}

	public ClientTexture(BufferedImage baseImage) {
		super(baseImage.getRGB(0, 0, baseImage.getWidth(), baseImage.getHeight(), null, 0, baseImage.getWidth()), baseImage.getWidth(), baseImage.getHeight());
	}

	@Override
	public Texture subTexture(int x, int y, int w, int h) {
		throw new UnsupportedOperationException("TODO: Reimplement this");
	}

	public int getTextureID() {
		if (!isLoaded()) {
			throw new IllegalStateException("Cannot use an unloaded texture");
		}
		return textureID;
	}

	@Override
	public void bind() {
		if (!isLoaded()) {
			throw new IllegalStateException("Cannot bind an unloaded texture!");
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		SpoutRenderer.checkGLError();
	}

	public void unload() {
		if (!isLoaded()) {
			throw new IllegalStateException("Cannot delete an unloaded texture!");
		}

		GL11.glDeleteTextures(textureID);
		SpoutRenderer.checkGLError();
		textureID = -1;
	}

	class WriteGPUTask implements Runnable{
		int width, height;
		int[] image;

		public WriteGPUTask(int width, int height, int[] image){
			this.width = width;
			this.height = height;
			this.image = image;
		}

		@Override
		public void run() {
			if( ((Client)Spout.getEngine()).getRenderMode() == RenderMode.GL11){
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				SpoutRenderer.checkGLError();
			}

			textureID = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
			SpoutRenderer.checkGLError();

			if( ((Client)Spout.getEngine()).getRenderMode() == RenderMode.GL11){
				GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
				SpoutRenderer.checkGLError();
			}

			/*if (((Client) Spout.getEngine()).getRenderMode() != RenderMode.GL30) {

				//Use Mipmaps
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
			}*/

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
			SpoutRenderer.checkGLError();
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
			SpoutRenderer.checkGLError();

			//Bilinear Filter the closest mipmap
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			SpoutRenderer.checkGLError();
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			SpoutRenderer.checkGLError();

			//Wrap the texture
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			SpoutRenderer.checkGLError();
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			SpoutRenderer.checkGLError();

			ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
			for (int y = 0; y < height; y++) {

				for (int x = 0; x < width; x++) {
					Color pixel = new Color(image[y * width + x], true);
					buffer.put((byte) pixel.getRed()); // Red component
					buffer.put((byte) pixel.getGreen());  // Green component
					buffer.put((byte) pixel.getBlue());         // Blue component
					buffer.put((byte) pixel.getAlpha()); // Alpha component. Only for RGBA
				}
			}

			buffer.flip();
			//if (((Client) Spout.getEngine()).getRenderMode() == RenderMode.GL30) {
			//	GL30.glGenerateMipmap(textureID);
			//}

			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
			SpoutRenderer.checkGLError();

			//EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D); //Not sure if this extension is supported on most cards. 
		}

	}

	@Override
	public void writeGPU() {
		if (isLoaded()) {
			throw new IllegalStateException("Cannot load an already loaded texture!");
		}
		((SpoutClient) Spout.getEngine()).getScheduler().enqueueRenderTask(new WriteGPUTask(getWidth(),getHeight(),this.image));
	}

	@Override
	public boolean isLoaded() {
		return textureID != -1;
	}
}
