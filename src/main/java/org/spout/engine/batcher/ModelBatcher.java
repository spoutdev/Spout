package org.spout.engine.batcher;

import org.lwjgl.opengl.GL11;
import org.spout.api.render.Renderer;
import org.spout.engine.mesh.BaseMesh;
import org.spout.engine.renderer.BatchVertexRenderer;

public class ModelBatcher {
	
	Renderer renderer;
	
	public ModelBatcher(){
		renderer = BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
		renderer.enableNormals();
		renderer.enableTextures();
	}
	
	public void drawMesh(BaseMesh mesh){
		
	}
	
	
	
	
	
}
