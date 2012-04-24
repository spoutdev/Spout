package org.spout.engine.renderer;

import org.lwjgl.opengl.GL11;
import org.spout.engine.renderer.shader.BasicShader;

public class GL11BatchVertexRenderer extends BatchVertexRenderer {
	
	int displayList;
	

	public GL11BatchVertexRenderer(int mode){
		super(mode);
		displayList = GL11.glGenLists(1);
		
	}
	

	@Override
	protected void doFlush(){
		if(!(activeShader instanceof BasicShader)) throw new IllegalStateException("Need Basic Shader in 1.1 mode");
			
		
		
		GL11.glNewList(displayList, GL11.GL_COMPILE);
		((BasicShader)activeShader).assign(true);
		GL11.glPushMatrix();
		GL11.glBegin(renderMode);
		for(int i = 0; i < numVerticies; i+= 1){
			int index = i *4;
			if(useColors) GL11.glColor3f(colorBuffer.get(index), colorBuffer.get(index+1), colorBuffer.get(index+2));
			if(useNormals) GL11.glNormal3f(normalBuffer.get(index), normalBuffer.get(index + 1), normalBuffer.get(index + 2));
			if(useTextures) GL11.glTexCoord2f(uvBuffer.get((i*2)), uvBuffer.get((i*2) + 1));
			GL11.glVertex4f(vertexBuffer.get(index), vertexBuffer.get(index+1), vertexBuffer.get(index+2), vertexBuffer.get(index+3));
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEndList();
		
	}
	
	
	
	@Override
	public void doRender() {
		
		GL11.glPushMatrix();
		GL11.glCallList(displayList);
		GL11.glPopMatrix();
		
		
	}
	
	

}
