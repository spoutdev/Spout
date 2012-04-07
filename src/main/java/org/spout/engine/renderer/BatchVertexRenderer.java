package org.spout.engine.renderer;
import java.awt.Color;
import java.io.FileNotFoundException;

import gnu.trove.list.array.*;


import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;
import org.spout.api.render.Shader;
import org.spout.api.render.Renderer;
import org.spout.engine.renderer.shader.EmptyShader;


public abstract class BatchVertexRenderer implements Renderer {

	public static BatchModes GLMode = BatchModes.GL11;
	
	public static Renderer constructNewBatch(int renderMode){
		if(GLMode == BatchModes.GL11) return new GL11BatchVertexRenderer(renderMode);
		if(GLMode == BatchModes.GL30) return new GL30BatchVertexRenderer(renderMode);
		if(GLMode == BatchModes.GLES20) return new GLES20BatchVertexRenderer(renderMode);
		throw new IllegalArgumentException("GL Mode:" + GLMode + " Not reconized");
	}
	
	
	
	boolean batching = false;
	boolean flushed = false;
	
	int renderMode;
	
	//Using FloatArrayList because I need O(1) access time
	//and fast ToArray()
	TFloatArrayList vertexBuffer = new TFloatArrayList();
	TFloatArrayList colorBuffer = new TFloatArrayList();
	TFloatArrayList normalBuffer = new TFloatArrayList();
	TFloatArrayList uvBuffer = new TFloatArrayList();
	
	
	int numVerticies = 0;
	
	boolean useColors = false;
	boolean useNormals = false;
	boolean useTextures = false;
	
	Shader activeShader = null;
	
	public BatchVertexRenderer(int mode){
		renderMode = mode;
	}
	
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#begin()
	 */
	public void begin(){
		if(batching) throw new IllegalStateException("Already Batching!");
		batching = true;
		flushed = false;		
		vertexBuffer.clear();
		colorBuffer.clear();
		normalBuffer.clear();
		uvBuffer.clear();
		
		
		numVerticies = 0;
	}
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#end()
	 */
	public void end(){
		if(!batching) throw new IllegalStateException("Not Batching!");
		batching = false;
		flush();
	}
	
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#flush()
	 */
	public final void flush(){
		if( vertexBuffer.size() % 4 != 0) throw new IllegalStateException("Vertex Size Mismatch (How did this happen?)");
		if( useColors){
			if(colorBuffer.size() % 4 != 0) throw new IllegalStateException("Color Size Mismatch (How did this happen?)");
			if(colorBuffer.size() / 4 != numVerticies) throw new IllegalStateException("Color Buffer size does not match numVerticies");
	
		}
		if(useNormals){
			if(normalBuffer.size() % 4 != 0) throw new IllegalStateException("Normal Size Mismatch (How did this happen?)");
			if(normalBuffer.size() / 4 != numVerticies) throw new IllegalStateException("Normal Buffer size does not match numVerticies");
			
		}
		if(useTextures){
			if(uvBuffer.size() % 2 != 0) throw new IllegalStateException("UV size Mismatch (How did this happen?)");
			if(uvBuffer.size() / 2 != numVerticies) throw new IllegalStateException("UV Buffer size does not match numVerticies");
			
		}
		
		
		//Call the overriden flush
		doFlush();
		
		//clean up after flush
		postFlush();
		
	}
	
	protected abstract void doFlush();
	
	protected void postFlush(){		
		flushed = true;		
	}
	
	/**
	 * The act of drawing.  The Batch will check if it's possible to render
	 * as well as setup for rendering.  If it's possible to render, it will call doRender()
	 * 
	 *  
	 */
	protected abstract void doRender();
	
	
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#render()
	 */
	public final void render(){
		checkRender();
		
		doRender();
		
		
	}
	
	protected void checkRender(){
		if(batching) throw new IllegalStateException("Cannot Render While Batching");
		if(!flushed) throw new IllegalStateException("Cannon Render Without Flushing the Batch");		
	}
	
	

	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#addVertex(float, float, float, float)
	 */
	public void addVertex(float x, float y, float z, float w){
		vertexBuffer.add(x);
		vertexBuffer.add(y);
		vertexBuffer.add(z);
		vertexBuffer.add(w);
		
		numVerticies++;
	}
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#addVertex(float, float, float)
	 */
	public void addVertex(float x, float y, float z){
		addVertex(x,y,z,1.0f);
	}
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#addVertex(float, float)
	 */
	public void addVertex(float x, float y){
		addVertex(x,y,0.0f,1.0f);
	}
	
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#addVertex(org.spout.api.math.Vector3)
	 */
	public void addVertex(Vector3 vertex){
		addVertex(vertex.getX(), vertex.getY(), vertex.getZ());
	}
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#AddVertex(org.spout.api.math.Vector2)
	 */
	public void addVertex(Vector2 vertex){
		addVertex(vertex.getX(), vertex.getY());
	}
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#AddVertex(org.spout.api.math.Vector4)
	 */
	public void addVertex(Vector4 vertex){
		addVertex(vertex.getX(), vertex.getY(), vertex.getZ(), vertex.getZ());
	}
	
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#addColor(float, float, float)
	 */
	public void addColor(float r, float g, float b){
		addColor(r,g,b,1.0f);
	}
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#addColor(float, float, float, float)
	 */
	public void addColor(float r, float g, float b, float a){
		if(!useColors) this.enableColors();
		colorBuffer.add(r);
		colorBuffer.add(g);
		colorBuffer.add(b);
		colorBuffer.add(a);
	}
	
	public void addColor(int r, int g, int b, int a){
		addColor(r/255.0f, g/255.0f, b/255.0f, a/255.0f);
	}
	
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#addColor(org.spout.api.util.Color)
	 */
	public void addColor(Color color){
		addColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#addNormal(float, float, float, float)
	 */
	public void addNormal(float x, float y, float z, float w){
		if(!useNormals) this.enableNormals();
		normalBuffer.add(x);
		normalBuffer.add(y);
		normalBuffer.add(z);
		normalBuffer.add(w);
	}
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#addNormal(float, float, float)
	 */
	public void addNormal(float x, float y, float z){
		addNormal(x,y,z,1.0f);
	}

	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#addNormal(org.spout.api.math.Vector3)
	 */
	public void addNormal(Vector3 vertex){
		addNormal(vertex.getX(), vertex.getY(), vertex.getZ());
	}
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#addNormal(org.spout.api.math.Vector4)
	 */
	public void addNormal(Vector4 vertex){
		addNormal(vertex.getX(), vertex.getY(), vertex.getZ(), vertex.getZ());
	}
	
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#addTexCoord(float, float)
	 */
	public void addTexCoord(float u, float v){
		if(this.useTextures) this.enableTextures();
		uvBuffer.add(u);
		uvBuffer.add(v);
	}
	
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#addTexCoord(org.spout.api.math.Vector2)
	 */
	public void addTexCoord(Vector2 uv){
		addTexCoord(uv.getX(), uv.getY());
	}
	
	
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#setShader(org.spout.client.renderer.shader.Shader)
	 */
	public void setShader(Shader shader){
		if(shader == null){		
			try {
				activeShader = new EmptyShader();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else activeShader = shader;
	}
	
	@Override
	public Shader getShader(){
		return activeShader;		
	}
	
	
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#enableColors()
	 */
	public void enableColors(){
		useColors = true;
	}
	
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#enableNormals()
	 */
	public void enableNormals(){
		useNormals = true;
	}
	
	/* (non-Javadoc)
	 * @see org.spout.client.renderer.Renderer#enableTextures()
	 */
	public void enableTextures(){
		useTextures = true;
	}
	
}
