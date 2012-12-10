package org.spout.engine.resources;

import java.awt.Color;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spout.api.Spout;
import org.spout.api.render.RenderMode;
import org.spout.api.render.Texture;
import org.spout.engine.SpoutClient;

public class ClientRenderTexture extends Texture {
	public static final int INVALID_BUFFER = -1;
	public static final int OUTPUT_BUFFER = 0;
	
	private int framebuffer = INVALID_BUFFER;
	private boolean useDepthBuffer = false;
	private boolean useStencilBuffer = false;
	
	int colorTarget = INVALID_BUFFER;
	int depthTarget = INVALID_BUFFER;
	int stencilTarget = INVALID_BUFFER; //TODO: Implement stencil component

	public ClientRenderTexture() {
		super(null, (int)((SpoutClient)Spout.getEngine()).getResolution().getX(), (int)((SpoutClient)Spout.getEngine()).getResolution().getY());
		
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
		if(isGL30()){
			if(framebuffer == INVALID_BUFFER) return; //Can't set this to active if it's not created yet
			GL30.glBindRenderbuffer(GL30.GL_FRAMEBUFFER, framebuffer);
		}
	}

	@Override
	public void writeGPU() {
		if(isGL30()) {
			if(framebuffer == INVALID_BUFFER) {
				framebuffer = GL30.glGenFramebuffers();
				
				//Create the color buffer for this renderTexture
				colorTarget = GL11.glGenTextures();
				GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTarget);
				
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.getWidth(), this.getHeight(), 0, GL11.GL_RGBA8, GL11.GL_UNSIGNED_BYTE, 0);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
				
				if(useDepthBuffer) {
					depthTarget = GL30.glGenRenderbuffers();
					GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthTarget);
					GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, this.getWidth(), this.getHeight());
					GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthTarget);
				}
				GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, 0, colorTarget, 0);
				
			}
		}
		
	}

	@Override
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTarget);
		
	}

	public void release() {
		if(isGL30()) {
			if(framebuffer != INVALID_BUFFER) {
				GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, OUTPUT_BUFFER);
			}
		}
	}
	
	protected boolean isGL30() {
		return ((SpoutClient)Spout.getEngine()).getRenderMode() == RenderMode.GL30;
	}
	
	@Override
	public boolean isLoaded() {
		return framebuffer != INVALID_BUFFER;
	}

}
