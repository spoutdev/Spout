package org.spout.engine.renderer.vertexbuffer;

import java.nio.FloatBuffer;

import org.spout.api.render.RenderMaterial;



/**
 * Abstract, API Neutral VertexBuffer interface
 *
 */
public abstract class VertexBuffer {
	
	/**
	 * Clears the buffer and uploads new data to the GPU
	 * @param data
	 * @param verticies
	 */
	public abstract void setData(FloatBuffer data, int verticies);
	/**
	 * Enables attribute locations
	 * @param location
	 * @param offset
	 */
	public abstract void enableAttribute(int location, int offset);
	/**
	 * Binds the buffer to be drawn
	 * 
	 * Does not need to be called before drawBuffer
	 */
	public abstract void bindBuffer();
	/**
	 * Draws the current buffer.  
	 * @param material
	 */
	abstract void drawBuffer(RenderMaterial material);
	
}
