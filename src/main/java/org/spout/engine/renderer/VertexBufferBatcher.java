package org.spout.engine.renderer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.spout.engine.renderer.vertexbuffer.VertexBuffer;

public class VertexBufferBatcher extends BatchVertexRenderer {
	final int SIZE_FLOAT = 4;
	VertexBuffer buffer;
	
	public VertexBufferBatcher(int mode, VertexBuffer buffer) {
		super(mode);
		this.buffer = buffer;
	}

	@Override
	protected void doFlush() {
		//Calculate the size
		int size = numVerticies * 4 * SIZE_FLOAT;
		
		if (useColors) {
			size += numVerticies * 4 * SIZE_FLOAT;			
		}
		if (useNormals) {
			size += numVerticies * 4 * SIZE_FLOAT;			
		}
		if (useTextures) {
			size += numVerticies * 2 * SIZE_FLOAT;			
		}
		FloatBuffer verts = BufferUtils.createFloatBuffer(size);
		verts.put(vertexBuffer.toArray());
		
		if(useColors){
			verts.put(colorBuffer.toArray());
		}
		if(useNormals){
			verts.put(normalBuffer.toArray());
		}
		if(useTextures){
			verts.put(uvBuffer.toArray());
		}
		
		verts.flip();
		
		this.buffer.setData(verts, numVerticies);
		
		
		
		
	}

	@Override
	protected void doRender() {
		// TODO Auto-generated method stub
		
	}

}
