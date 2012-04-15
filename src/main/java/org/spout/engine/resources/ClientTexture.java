package org.spout.engine.resources;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.spout.api.render.Texture;

public class ClientTexture extends Texture {

	int textureID = -1;
	
	public ClientTexture(BufferedImage baseImage) {
		super(baseImage);
		
	}

	@Override
	public Texture subTexture(int x, int y, int w, int h) {
		return new ClientTexture(image.getSubimage(x, y, w, h));
	}

	public int getTextureID(){
		if(textureID == -1) throw new IllegalStateException("Cannot use an unloaded texture");
		return textureID;
	}
	
	@Override
	public void bind() {
		if(textureID == -1) throw new IllegalStateException("Cannot bind an unloaded texture!");
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
	}
	
	
	public void unload(){
		if(textureID == -1) throw new IllegalStateException("Cannot delete an unloaded texture!");
		
		GL11.glDeleteTextures(textureID);
		textureID = -1;
		
	}
	

	@Override
	public void load() {
		if(textureID != -1) throw new IllegalStateException("Cannot load an already loaded texture!");
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
		
		//Use Mipmaps
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
		
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
	        for(int x = 0; x < width; x++){		           
	        	for(int y = 0; y < height; y++){
	                int pixel = pixels[y * width + x];
	                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
	                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
	                buffer.put((byte) (pixel & 0xFF));               // Blue component
	                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
	            }
	        }

	        buffer.flip(); 
	                
	        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	        //EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D); //Not sure if this extension is supported on most cards. 
			
	}

}
