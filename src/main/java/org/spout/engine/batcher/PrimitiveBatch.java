package org.spout.engine.batcher;

import java.awt.Color;

import org.spout.api.math.Vector3;
import org.spout.api.render.Renderer;
import org.spout.engine.renderer.BatchVertexRenderer;
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
		if(sides.length != 6) throw new IllegalStateException("Must have 6 sides!");
		if(sides[0])addQuad(cubeCorners[0].multiply(scale).add(location), cubeCorners[1].multiply(scale).add(location), cubeCorners[2].multiply(scale).add(location), cubeCorners[3].multiply(scale).add(location), c);
		if(sides[1])addQuad(cubeCorners[7].multiply(scale).add(location), cubeCorners[6].multiply(scale).add(location), cubeCorners[5].multiply(scale).add(location), cubeCorners[4].multiply(scale).add(location), c);
		if(sides[2])addQuad(cubeCorners[3].multiply(scale).add(location), cubeCorners[2].multiply(scale).add(location), cubeCorners[6].multiply(scale).add(location), cubeCorners[7].multiply(scale).add(location), c);
		
		if(sides[3])addQuad(cubeCorners[4].multiply(scale).add(location), cubeCorners[5].multiply(scale).add(location), cubeCorners[1].multiply(scale).add(location), cubeCorners[0].multiply(scale).add(location), c);
		if(sides[4])addQuad(cubeCorners[1].multiply(scale).add(location), cubeCorners[5].multiply(scale).add(location), cubeCorners[6].multiply(scale).add(location), cubeCorners[2].multiply(scale).add(location), c);
		if(sides[5])addQuad(cubeCorners[4].multiply(scale).add(location), cubeCorners[0].multiply(scale).add(location), cubeCorners[3].multiply(scale).add(location), cubeCorners[7].multiply(scale).add(location), c);
		
		
		
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
	
	
	public void end(){
		renderer.end();
		
	}
	public void draw(){
		renderer.render();
	}
}
