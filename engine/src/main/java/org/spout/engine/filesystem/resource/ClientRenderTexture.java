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
package org.spout.engine.filesystem.resource;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

import org.spout.api.Spout;
import org.spout.api.exception.ComputerIsPotatoException;
import org.spout.api.render.RenderMode;
import org.spout.api.render.Texture;

import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutRenderer;

public class ClientRenderTexture extends ClientTexture {
	public static final int INVALID_BUFFER = -1;
	public static final int SCREEN_BUFFER = 0;
	public static ByteBuffer EMPTY_BUFFER = BufferUtils.createByteBuffer(0);

	private int framebuffer = INVALID_BUFFER;
	private boolean useDepthBuffer = false;
	private boolean useStencilBuffer = false;
	private boolean useNormalBuffer = false;

	int depthTarget = INVALID_BUFFER;
	int stencilTarget = INVALID_BUFFER; //TODO: Implement stencil component
	int normalTarget = INVALID_BUFFER;

	ClientTextureHandle depthTexture = null;
	ClientTextureHandle stencilTexture = null;
	ClientTextureHandle normalTexture = null;

	boolean useEXT = false;

	public ClientRenderTexture() {
		super(null, (int)((SpoutClient)Spout.getEngine()).getResolution().getX(), (int)((SpoutClient)Spout.getEngine()).getResolution().getY());

		//Detect which path we should use to create framebuffers.
		//if all 3 are false, we cannot use framebuffers so throw an exception
		boolean gl30 = GLContext.getCapabilities().OpenGL30;
		boolean arb = GLContext.getCapabilities().GL_ARB_framebuffer_object;
		boolean ext = GLContext.getCapabilities().GL_EXT_framebuffer_object;

		if(gl30) {
			useEXT = false;
		}
		else if(arb) {
			useEXT = false;
		}
		else if( ext) {
			useEXT = true;
		}
		else {
			throw new ComputerIsPotatoException("Does not support Framebuffers");
		}


		Spout.log("Using EXT: " + useEXT);

	}

	public ClientRenderTexture(boolean depth){
		this(true, false, false);

	}

	public ClientRenderTexture(boolean depth, boolean stencil, boolean normals){
		this();
		useDepthBuffer = depth;
		useStencilBuffer = stencil;
		useNormalBuffer = normals;
	}

	@Override
	public Texture subTexture(int x, int y, int w, int h) {
		// TODO Auto-generated method stub
		return null;
	}

	public void activate() {

		if(framebuffer == INVALID_BUFFER) return; //Can't set this to active if it's not created yet

		if(useEXT)  EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, framebuffer);
		else GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
		SpoutRenderer.checkGLError();

		GL11.glViewport(0, 0, width, height);
		SpoutRenderer.checkGLError();
	}

	public void release() {

		if(framebuffer != INVALID_BUFFER) {
			if(useEXT)  EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, SCREEN_BUFFER);
			else GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, SCREEN_BUFFER);
			SpoutRenderer.checkGLError();

			GL11.glViewport(0, 0, width, height);
			SpoutRenderer.checkGLError();

			GL11.glDrawBuffer(GL11.GL_BACK);
		}
	}

	@Override
	public void writeGPU() {
		if(framebuffer != INVALID_BUFFER) throw new IllegalStateException("Framebuffer already created!");

		int buffers = 1;

		//Create the color buffer for this renderTexture
		textureID = GL11.glGenTextures();
		SpoutRenderer.checkGLError();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		SpoutRenderer.checkGLError();

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		SpoutRenderer.checkGLError();
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		SpoutRenderer.checkGLError();
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_INT, (ByteBuffer) null);  // Create the texture data
		SpoutRenderer.checkGLError();

		if(useNormalBuffer) {
			//Create the color buffer for this renderTexture
			normalTarget = GL11.glGenTextures();
			SpoutRenderer.checkGLError();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalTarget);
			SpoutRenderer.checkGLError();

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			SpoutRenderer.checkGLError();
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			SpoutRenderer.checkGLError();
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);  // Create the texture data
			SpoutRenderer.checkGLError();
			buffers++;

		}

		if(useDepthBuffer){
			//Create the color buffer for this renderTexture
			depthTarget = GL11.glGenTextures();
			SpoutRenderer.checkGLError();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTarget);
			SpoutRenderer.checkGLError();

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			SpoutRenderer.checkGLError();
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			SpoutRenderer.checkGLError();
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);  // Create the texture data
			SpoutRenderer.checkGLError();
			buffers++;
		}

	

		if(useEXT) {
			framebuffer = EXTFramebufferObject.glGenFramebuffersEXT();
			SpoutRenderer.checkGLError();

			EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, framebuffer);
			SpoutRenderer.checkGLError();
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
			SpoutRenderer.checkGLError();

			EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D, textureID, 0);
			SpoutRenderer.checkGLError();

			if(useDepthBuffer) {
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTarget);
				SpoutRenderer.checkGLError();

				EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, GL11.GL_TEXTURE_2D, depthTarget, 0);
				SpoutRenderer.checkGLError();
			}

			if(useNormalBuffer){
				
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalTarget);
				SpoutRenderer.checkGLError();

				EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT1_EXT, GL11.GL_TEXTURE_2D, normalTarget, 0);
				SpoutRenderer.checkGLError();
			}
			
			
			
			
			if(EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT) != EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT){
				System.out.println("ERROR: Framebuffer not complete");
				throw new ComputerIsPotatoException("Framebuffer not complete");
			}
			SpoutRenderer.checkGLError();

		} else {
			framebuffer = GL30.glGenFramebuffers();
			SpoutRenderer.checkGLError();

			
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
			SpoutRenderer.checkGLError();
			
			IntBuffer drawBuffers = BufferUtils.createIntBuffer(buffers);
			drawBuffers.put(GL30.GL_COLOR_ATTACHMENT0);
			if(useNormalBuffer) drawBuffers.put(GL30.GL_COLOR_ATTACHMENT1);
			drawBuffers.flip();
			GL20.glDrawBuffers(drawBuffers);
			SpoutRenderer.checkGLError();
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
			SpoutRenderer.checkGLError();

			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D,  textureID, 0);	
			SpoutRenderer.checkGLError();		

			if(useDepthBuffer) {
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTarget);
				SpoutRenderer.checkGLError();
		
				
				GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTarget, 0);	
				SpoutRenderer.checkGLError();	
			}
			if(useNormalBuffer) {
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalTarget);
				SpoutRenderer.checkGLError();

				GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, normalTarget, 0);	
				SpoutRenderer.checkGLError();		

			}
			
			int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
			if(status != GL30.GL_FRAMEBUFFER_COMPLETE){
				System.out.println("ERROR: Framebuffer not complete.  Status: " + status);
				throw new ComputerIsPotatoException("Framebuffer not complete");
			}
			SpoutRenderer.checkGLError();
			
			
			
			
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, SCREEN_BUFFER);
			SpoutRenderer.checkGLError();

			

		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		SpoutRenderer.checkGLError();
	}

	
	public Texture getDepthTexture(){
		if(depthTexture == null){
			depthTexture = new ClientTextureHandle(depthTarget, width, height);
		}
		return depthTexture;
	}
	
	public Texture getStencilTexture(){
		if(stencilTexture == null){
			stencilTexture = new ClientTextureHandle(stencilTarget, width, height);
		}
		return stencilTexture;
	}
	
	
	public Texture getNormalTexture() {
		if(normalTexture == null){
			normalTexture = new ClientTextureHandle(normalTarget, width, height);
		}
		return normalTexture;
		
	}
	
	protected boolean isGL30() {
		return ((SpoutClient)Spout.getEngine()).getRenderMode() == RenderMode.GL30 ||
				((SpoutClient)Spout.getEngine()).getRenderMode() == RenderMode.GL40;
	}

	@Override
	public boolean isLoaded() {
		return framebuffer != INVALID_BUFFER;
	}
}