package org.spout.engine.renderer;
import java.nio.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class GL30BatchVertexRenderer extends BatchVertexRenderer {
	final int SIZE_FLOAT = 4;
	
	int vao;
	int vbos = -1;
	
	
	
	
	
	/**
	 * Batch Renderer using OpenGL 3.0 mode.
	 * 
	 * @param renderMode 
	 * Mode to render in
	 */
	public GL30BatchVertexRenderer(int renderMode){
		super(renderMode);
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
				
	}
	

	
	protected void doFlush(){
		if(activeShader == null) throw new IllegalStateException("Batch must have a shader attached");
		if(vbos != -1) GL15.glDeleteBuffers(vbos);
		
		GL30.glBindVertexArray(vao);
		int size = numVerticies * 4 * SIZE_FLOAT;
		if(useColors) size += numVerticies * 4 * SIZE_FLOAT;
		if(useNormals) size += numVerticies * 4 * SIZE_FLOAT;
		if(useTextures) size += numVerticies * 2 * SIZE_FLOAT;
		
		vbos = GL15.glGenBuffers();
		
		int offset = 0;
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbos);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, GL15.GL_STATIC_DRAW);
		
		FloatBuffer vBuffer =  BufferUtils.createFloatBuffer(vertexBuffer.size());
		vBuffer.clear();
		vBuffer.put(vertexBuffer.toArray());
		vBuffer.flip();
		//GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, vBuffer);
		activeShader.enableAttribute("vPosition", 4, GL11.GL_FLOAT,0, offset);
		offset += numVerticies * 4 * SIZE_FLOAT;
		if(useColors){
			
			vBuffer =  BufferUtils.createFloatBuffer(colorBuffer.size());
			vBuffer.clear();
			vBuffer.put(colorBuffer.toArray());
			vBuffer.flip();
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, vBuffer);
			
			activeShader.enableAttribute("vColor", 4, GL11.GL_FLOAT,0,offset);
			offset += numVerticies * 4 * SIZE_FLOAT;
		}
		if(useNormals){
		
			vBuffer =  BufferUtils.createFloatBuffer(normalBuffer.size());
			vBuffer.clear();
			vBuffer.put(normalBuffer.toArray());
			vBuffer.flip();
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, vBuffer);
			
			activeShader.enableAttribute("vNormal", 4, GL11.GL_FLOAT,0, offset);
			offset += numVerticies * 4 * SIZE_FLOAT;
		}
		if(useTextures){
			
			vBuffer =  BufferUtils.createFloatBuffer(uvBuffer.size());
			vBuffer.clear();
			vBuffer.put(uvBuffer.toArray());
			vBuffer.flip();
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, vBuffer);
			
			activeShader.enableAttribute("vTexCoord", 2, GL11.GL_FLOAT,0, offset);
			offset += numVerticies * 2 * SIZE_FLOAT;
		}
			
		
		
		activeShader.assign();
	}
	
	/**
	 * Draws this batch
	 */
	public void doRender(){
		GL30.glBindVertexArray(vao);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbos);
		
		activeShader.assign();
		GL11.glDrawArrays(renderMode, 0, numVerticies);
		
		
	}
	
	
}
