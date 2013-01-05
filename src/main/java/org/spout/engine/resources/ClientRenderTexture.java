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
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GLContext;
import org.spout.api.Spout;
import org.spout.api.exception.ComputerIsPotatoException;
import org.spout.api.render.RenderMode;
import org.spout.api.render.Texture;
import org.spout.engine.SpoutClient;

public class ClientRenderTexture extends ClientTexture {
	public static final int INVALID_BUFFER = -1;
	public static final int SCREEN_BUFFER = 0;
	public static ByteBuffer EMPTY_BUFFER = BufferUtils.createByteBuffer(0);
	
	private int framebuffer = INVALID_BUFFER;
	private boolean useDepthBuffer = false;
	private boolean useStencilBuffer = false;
	
	int depthTarget = INVALID_BUFFER;
	int stencilTarget = INVALID_BUFFER; //TODO: Implement stencil component
	
	
	boolean useEXT = false;

	public ClientRenderTexture() {
		super(null, (int)((SpoutClient)Spout.getEngine()).getResolution().getX(), (int)((SpoutClient)Spout.getEngine()).getResolution().getY());
		
		//Detect which path we should use to create framebuffers.  
		//if both of these are false, we cannot use framebuffers so throw an exception
		boolean arb = GLContext.getCapabilities().GL_ARB_framebuffer_object;
		boolean ext = GLContext.getCapabilities().GL_EXT_framebuffer_object;		
		if(!arb && !ext) throw new ComputerIsPotatoException("Does not support Framebuffers");	

		//if arb is false, use ext
		if(!arb) useEXT = true;
		
		Spout.log("Using EXT: " + useEXT);
	
	}

	public ClientRenderTexture(boolean depth){
		this();
		useDepthBuffer = true;
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
			GL11.glViewport(0, 0, width, height);			
		
	}
	
	public void release() {
		if(framebuffer != INVALID_BUFFER) {
			if(useEXT)  EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, SCREEN_BUFFER);
			else GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, SCREEN_BUFFER);
			GL11.glViewport(0, 0, width, height);
				
			
		}
	}

	@Override
	public void writeGPU() {				
		if(framebuffer != INVALID_BUFFER) throw new IllegalStateException("Framebuffer already created!");
		
		//Create the color buffer for this renderTexture
		textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_INT, (java.nio.ByteBuffer) null);  // Create the texture data

		
		if(useEXT) {
			framebuffer = EXTFramebufferObject.glGenFramebuffersEXT();
			
			EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, framebuffer);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
			
			
			
			EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D, textureID, 0);
			
			
			
			if(useDepthBuffer) {
				depthTarget = GL30.glGenRenderbuffers();
				EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthTarget);
				EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, GL11.GL_DEPTH_COMPONENT, this.getWidth(), this.getHeight());
				EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, GL30.GL_DEPTH_ATTACHMENT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthTarget);
			}
			
			
			if(EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT) != EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT)
			{
				System.out.println("ERROR: Framebuffer not complete");
				throw new ComputerIsPotatoException("Framebuffer not complete");
			}

		
		} else {
				framebuffer = GL30.glGenFramebuffers();
				GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
				
								
				GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, textureID, 0);			
				
				
				if(useDepthBuffer) {
					depthTarget = GL30.glGenRenderbuffers();
					GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthTarget);
					GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, this.getWidth(), this.getHeight());
					GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthTarget);
				}
				GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, SCREEN_BUFFER);
				
				if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE)
				{
					System.out.println("ERROR: Framebuffer not complete");
					throw new ComputerIsPotatoException("Framebuffer not complete");
				}
	
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		
	}


	
	protected boolean isGL30() {
		return ((SpoutClient)Spout.getEngine()).getRenderMode() == RenderMode.GL30;
	}
	
	@Override
	public boolean isLoaded() {
		return framebuffer != INVALID_BUFFER;
	}
}