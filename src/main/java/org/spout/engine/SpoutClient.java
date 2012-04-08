package org.spout.engine;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.PixelFormat;
import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.entity.Entity;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;
import org.spout.api.math.Matrix;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.plugin.PluginStore;
import org.spout.api.render.BasicCamera;
import org.spout.api.render.Camera;
import org.spout.api.render.Renderer;
import org.spout.api.render.Shader;
import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.renderer.shader.BasicShader;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutWorld;
import org.spout.engine.batcher.PrimitiveBatch;
import org.spout.engine.filesystem.FileSystem;


import java.awt.Color;
import java.io.File;
import java.util.Collection;


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
	}
	
	@Override
	public void start(){
		super.start();
		scheduler.startRenderThread();
		
	}
	
	public void initRenderer()
	{
		
		try {
			Display.setDisplayMode(new DisplayMode((int)resolution.getX(), (int)resolution.getY()));
			
			if(System.getProperty("os.name").toLowerCase().contains("mac")){
				String[] ver = System.getProperty("os.version").split("\\.");
				if(Integer.parseInt(ver[1]) >= 7){
					ContextAttribs ca  = new ContextAttribs(3, 2).withProfileCore(true);
					Display.create(new PixelFormat(8, 24, 0), ca);
				}
				else{
					Display.create();
				}
			
				
			}
			else{
				Display.create();
				
			}
			Display.setTitle("Spout Client");
			
			
			System.out.println("OpenGL Information");
			System.out.println("Vendor: " + GL11.glGetString(GL11.GL_VENDOR));
			System.out.println("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION));
			System.out.println("GLSL Version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
			System.out.println("Extensions Supported: " + GL11.glGetString(GL11.GL_EXTENSIONS));
			
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		activeCamera = new BasicCamera(Matrix.createPerspective(75, aspectRatio, 0.001f, 1000), Matrix.createLookAt(new Vector3(-20, 20, 20), Vector3.ZERO, Vector3.UP));
		Shader shader = new BasicShader();
		renderer = new PrimitiveBatch();
		renderer.getRenderer().setShader(shader);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	
	PrimitiveBatch renderer;
	final boolean[] sides  = { true, true, true, true, true, true };
	
	long ticks = 0;
	
	public void render(float dt){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);
		
		ticks++;
		double cx = 20 * Math.sin(Math.toRadians(ticks));
		double cz = 20 * Math.cos(Math.toRadians(ticks));
		double cy = 20 * Math.sin(Math.toRadians(ticks));

		Matrix view = Matrix.createLookAt(new Vector3(cx,cy,cz), Vector3.ZERO, Vector3.UP);
		renderer.getRenderer().getShader().setUniform("View", view);
		renderer.getRenderer().getShader().setUniform("Projection", activeCamera.getProjection());
		
		
		
		renderer.begin();
		if(this.getLiveWorlds().size() > 0){
			Object[] worlds = this.getWorlds().toArray();
			SpoutWorld world = (SpoutWorld)worlds[0];
			SpoutChunk c = world.getChunk(0, 4, 0);
			ChunkSnapshot snap = c.getSnapshot();
			for(int x = 0; x < 16; x++){
				for(int y = 0; y < 16; y++){
					for(int z = 0; z < 16; z++){
						BlockMaterial m = snap.getBlockMaterial(x, y, z);
						
						Color col = Color.getHSBColor((float)Math.random() * 360f, 1, 1);
						if(m.isSolid()) renderer.addCube(new Vector3(x,y,z), Vector3.ONE, col, sides);
					}
				}
			}
		}
		else{
			renderer.addCube(Vector3.ZERO, Vector3.ONE, Color.red, sides);
		}
		renderer.end();
		
		
		renderer.draw();
		
		
		
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
