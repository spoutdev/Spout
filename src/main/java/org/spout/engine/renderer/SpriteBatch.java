package org.spout.engine.renderer;

import java.awt.geom.Rectangle2D;

import org.lwjgl.opengl.GL11;
import org.spout.api.render.Renderer;
import org.spout.engine.texture.Texture;


public class SpriteBatch {
	
	Renderer renderer;
	
	boolean isBatching = false;
	
	public SpriteBatch(){
		renderer = BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
	}
	
	
	public void begin(){
		if(isBatching) throw new IllegalStateException("Already Batching!");
		renderer.begin();
	}
	
	public void end(){
		if(!isBatching) throw new IllegalStateException("Cannot end batching without a begin");
		renderer.end();
		renderer.render();
	}

	public void drawSprite(Rectangle2D.Float rect, Texture texture, int color){
		if(!isBatching) throw new IllegalStateException("Cannot Draw without Begin!");
		
		
	}
	
	
	
	
}
