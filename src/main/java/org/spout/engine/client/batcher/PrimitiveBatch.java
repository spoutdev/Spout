package org.spout.engine.client.batcher;

import org.spout.api.math.Vector3;
import org.spout.api.render.Renderer;
import org.spout.engine.client.renderer.BatchVertexRenderer;
import org.spout.engine.client.renderer.vertexformat.PositionColor;
import org.lwjgl.opengl.GL11;

public class PrimitiveBatch {
	Renderer renderer;
	
	public PrimitiveBatch(){
		renderer = BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
	}
	
	public Renderer getRenderer(){
		return renderer;
	}
	
	public void begin(){
		renderer.enableNormals();
		renderer.enableColors();
		renderer.begin();
	}
	
	public void addQuad(Vector3 a, Vector3 b, Vector3 c, Vector3 d){
		renderer.addVertex(a);
		renderer.addVertex(b);
		renderer.addVertex(c);
		
		renderer.addVertex(c);
		renderer.addVertex(a);
		renderer.addVertex(d);
		
	}
	public void addQuad(PositionColor a, PositionColor b, PositionColor c, PositionColor d){
		Vector3 normal = (a.getPosition().subtract(b.getPosition())).cross(b.getPosition().subtract(c.getPosition()));
		renderer.addColor(a.getColor());
		renderer.addNormal(normal);
		renderer.addVertex(a.getPosition());
		
		renderer.addColor(b.getColor());
		renderer.addNormal(normal);
		renderer.addVertex(b.getPosition());
		
		renderer.addColor(c.getColor());
		renderer.addNormal(normal);		
		renderer.addVertex(c.getPosition());
		
		renderer.addColor(c.getColor());
		renderer.addNormal(normal);		
		renderer.addVertex(c.getPosition());
		
		renderer.addColor(d.getColor());
		renderer.addNormal(normal);		
		renderer.addVertex(d.getPosition());
		
		renderer.addColor(a.getColor());
		renderer.addNormal(normal);		
		renderer.addVertex(a.getPosition());
		
	}
	
	public void end(){
		renderer.end();
		
	}
	public void draw(){
		renderer.render();
	}
}
