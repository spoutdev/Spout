package org.spout.engine.batcher;

import java.awt.Color;

import org.spout.api.math.Vector3;
import org.spout.api.render.Renderer;
import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.renderer.vertexformat.PositionColor;
import org.lwjgl.opengl.GL11;

public class PrimitiveBatch {
	Renderer renderer;
	
	private final Vector3[] cubeCorners = new Vector3[] { Vector3.ZERO , Vector3.UNIT_Y, new Vector3(0,1,1), Vector3.UNIT_Z,
			Vector3.UNIT_X, new Vector3(1,1,0), Vector3.ONE, new Vector3(1, 0, 1)};
	
	
	public PrimitiveBatch(){
		renderer = BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
	}
	
	public Renderer getRenderer(){
		return renderer;
	}
	
	public void begin(){
		renderer.enableColors();
		renderer.begin();
	}
	
	public void addCube(Vector3 location, Vector3 scale, Color c, boolean[] sides){
		/*
		batch.addQuad(corners[0], corners[1], corners[2], corners[3]); //draws
		batch.addQuad(corners[7], corners[6], corners[5], corners[4]);
		batch.addQuad(corners[3], corners[2], corners[6], corners[7]);
		batch.addQuad(corners[4], corners[5], corners[1], corners[0]); //draws
		batch.addQuad(corners[1], corners[5], corners[6], corners[2]);
		batch.addQuad(corners[4], corners[0], corners[3], corners[7]);
		*/
		addQuad(cubeCorners[0].multiply(scale).add(location), cubeCorners[1].multiply(scale).add(location), cubeCorners[2].multiply(scale).add(location), cubeCorners[3].multiply(scale).add(location), c);
		addQuad(cubeCorners[7].multiply(scale).add(location), cubeCorners[6].multiply(scale).add(location), cubeCorners[5].multiply(scale).add(location), cubeCorners[4].multiply(scale).add(location), c);
		addQuad(cubeCorners[3].multiply(scale).add(location), cubeCorners[2].multiply(scale).add(location), cubeCorners[6].multiply(scale).add(location), cubeCorners[7].multiply(scale).add(location), c);
		
		addQuad(cubeCorners[4].multiply(scale).add(location), cubeCorners[5].multiply(scale).add(location), cubeCorners[1].multiply(scale).add(location), cubeCorners[0].multiply(scale).add(location), c);
		addQuad(cubeCorners[1].multiply(scale).add(location), cubeCorners[5].multiply(scale).add(location), cubeCorners[6].multiply(scale).add(location), cubeCorners[2].multiply(scale).add(location), c);
		addQuad(cubeCorners[4].multiply(scale).add(location), cubeCorners[0].multiply(scale).add(location), cubeCorners[3].multiply(scale).add(location), cubeCorners[7].multiply(scale).add(location), c);
		
		
		
	}
	
	
	
	
	public void addQuad(Vector3 a, Vector3 b, Vector3 c, Vector3 d, Color col){
		renderer.addColor(col);
		renderer.addVertex(a);
		renderer.addColor(col);		
		renderer.addVertex(b);
		renderer.addColor(col);		
		renderer.addVertex(c);
		
		renderer.addColor(col);		
		renderer.addVertex(c);
		renderer.addColor(col);
		renderer.addVertex(a);
		renderer.addColor(col);
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
