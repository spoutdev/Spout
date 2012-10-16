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
package org.spout.engine.resources;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spout.api.render.Texture;

public class ClientTexture extends Texture {
	int textureID = -1;

	public ClientTexture(BufferedImage baseImage) {
		super(baseImage);
	}

	protected ClientTexture(){
		super(null);		
	}

	@Override
	public Texture subTexture(int x, int y, int w, int h) {
		return new ClientTexture(image.getSubimage(x, y, w, h));
	}

	public int getTextureID() {
		if (textureID == -1) {
			throw new IllegalStateException("Cannot use an unloaded texture");
		}
		return textureID;
	}

	@Override
	public void bind() {
		if (textureID == -1) {
			throw new IllegalStateException("Cannot bind an unloaded texture!");
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}

	public void unload() {
		if (textureID == -1) {
			throw new IllegalStateException("Cannot delete an unloaded texture!");
		}

		GL11.glDeleteTextures(textureID);
		textureID = -1;
	}

	@Override
	public void load() {
		if (textureID != -1) {
			throw new IllegalStateException("Cannot load an already loaded texture!");
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);

		textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);

		/*if (((Client) Spout.getEngine()).getRenderMode() != RenderMode.GL30) {

			//Use Mipmaps
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
		}*/

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);

		//Bilinear Filter the closest mipmap
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

		//Wrap the texture
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		int width = image.getWidth();
		int height = image.getHeight();

		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);

		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {

				int pixel = pixels[y * width + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green component
				buffer.put((byte) (pixel & 0xFF));         // Blue component
				buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component. Only for RGBA
			}
		}

		buffer.flip();
		//if (((Client) Spout.getEngine()).getRenderMode() == RenderMode.GL30) {
		//	GL30.glGenerateMipmap(textureID);
		//}


		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		//EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D); //Not sure if this extension is supported on most cards. 
	}

	@Override
	public boolean isLoaded() {
		return textureID != -1;
	}
}
