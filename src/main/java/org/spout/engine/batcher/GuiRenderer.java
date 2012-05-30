package org.spout.engine.batcher;

import java.awt.geom.Rectangle2D;

import org.lwjgl.opengl.GL11;

import org.spout.api.render.Renderer;
import org.spout.api.render.Texture;
import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.resources.RenderMaterial;

public class GuiRenderer {
	static Renderer renderer;
	static RenderMaterial guiMaterial;
	static RenderMaterial textMaterial;
	
	
	public static void init(){
		renderer = BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
		
	}
	
	

}
