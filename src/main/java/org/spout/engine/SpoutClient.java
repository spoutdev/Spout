package org.spout.engine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.entity.Entity;
import org.spout.api.geo.World;
import org.spout.api.math.Matrix;
import org.spout.api.math.Vector2;
import org.spout.api.plugin.PluginStore;
import org.spout.api.render.BasicCamera;
import org.spout.api.render.Camera;
import org.spout.api.render.Renderer;
import org.spout.api.render.Shader;
import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.renderer.shader.BasicShader;
import org.spout.engine.filesystem.FileSystem;


import java.awt.Color;
import java.io.File;


public class SpoutClient extends SpoutEngine implements Client {

	private Camera activeCamera;
	
	private final Vector2 resolution = new Vector2(854, 480);
	private final float aspectRatio = resolution.getX() / resolution.getY();

	public static void main(String[] args) {
		System.setProperty("org.lwjgl.librarypath",System.getProperty("user.dir") + "/natives/");
		SpoutClient c = new SpoutClient();
		Spout.setEngine(c);
		FileSystem.init();
		
		c.init(args);
		c.start();
		
	
	
	}
	
	

	public SpoutClient() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init(String[] args){
		super.init(args);
		
		try {
			Display.setDisplayMode(new DisplayMode((int)resolution.getX(), (int)resolution.getY()));
			Display.create();
			
			System.out.println("OpenGL Information");
			System.out.println(GL11.glGetString(GL11.GL_VENDOR));
			System.out.println(GL11.glGetString(GL11.GL_RENDER));
			System.out.println(GL11.glGetString(GL11.GL_VERSION));
			System.out.println(GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
			System.out.println(GL11.glGetString(GL11.GL_EXTENSIONS));
			
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		activeCamera = new BasicCamera(Matrix.createIdentity(), Matrix.createIdentity());
		Shader shader = new BasicShader();
		renderer = BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
		renderer.setShader(shader);
		
		scheduler.startRenderThread();
		
		
	}
	Renderer renderer;
	
	public void render(){
		
		
		
		
		renderer.getShader().setUniform("View", activeCamera.getView());
		renderer.getShader().setUniform("Projection", activeCamera.getProjection());
		
		
		renderer.begin();
		renderer.addColor(Color.RED);
		renderer.addVertex(0,0);
		renderer.addColor(Color.BLUE);
		renderer.addVertex(-1, 1);
		renderer.addColor(Color.GREEN);
		renderer.addVertex(1, 1);	
		renderer.end();
		
		
		renderer.render();	
		
		
		
	}

	@Override
	public File getTemporaryCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getStatsFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getActivePlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public World getWorld() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Camera getActiveCamera() {
		return activeCamera;
	}

	@Override
	public void setActiveCamera(Camera activeCamera) {
		this.activeCamera = activeCamera;
	}

	@Override
	public PluginStore getPluginStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getResourcePackFolder() {
		// TODO Auto-generated method stub
		return null;
	}
}
