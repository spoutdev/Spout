package org.spout.api.model;

/**
 * Renderer to attach to a Mesh to change the way the mesh renders
 * 
 *
 */
public interface Renderer {

	/**
	 * Called before the mesh has been batched.  
	 * 
	 * Used for setting the shader or texture.
	 */
	public void preBatch();
	/**
	 * Called after the mesh has been batched but before the batch has been flushed to the GPU
	 * 
	 * Used to add additional verticies to the model
	 */
	public void postBatch();
	
	/**
	 * Called before the mesh is drawn to the scene.  
	 * Used to set GPU modes and/or effects
	 */
	public void preDraw();
	
	/**
	 * Called after the mesh is drawn to the scene
	 * 
	 * Used to clean up things done in preDraw()
	 */
	public void postDraw();
	
}
